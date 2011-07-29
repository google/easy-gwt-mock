/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.gwt.testing.easygwtmock.client.internal;

import com.google.gwt.testing.easygwtmock.client.Answer;
import com.google.gwt.testing.easygwtmock.client.ArgumentMatcher;
import com.google.gwt.testing.easygwtmock.client.ExpectationSetters;
import com.google.gwt.testing.easygwtmock.client.internal.matchers.ArgumentCapture;
import com.google.gwt.testing.easygwtmock.client.internal.matchers.Equals;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Allows setting expectations for an associated expected call.
 * 
 * @author Michael Goderbauer
 */
public class ExpectationSettersImpl implements ExpectationSetters<Object> {
  
  private final Call call;
  private final List<ArgumentMatcher> matchers;
  private Set<ArgumentCapture> captures;
  
  private final MocksBehavior behavior;
  
  private boolean retired;
  private boolean callUsedAtLeastOnce;
  private Answer<? extends Object> answer;
  private boolean unusedAnswer;
  
  ExpectationSettersImpl(Call call, List<ArgumentMatcher> matchers,
                    Set<ArgumentCapture> captures, MocksBehavior behavior) {
    this.call = call;
    this.matchers = createMissingMatchers(call, matchers);
    this.captures = captures;
    this.behavior = behavior;
    this.retired = false;
  }

  @Override
  public ExpectationSetters<Object> andAnswer(Answer<? extends Object> answer) {
    checkIsNotRetired();
    
    if (this.unusedAnswer) {
      saveExpectation(Range.DEFAULT);
    }
    
    this.answer = answer;
    this.unusedAnswer = true;
    
    return this;
  }
  
  @Override
  public ExpectationSetters<Object> andReturn(final Object value) {
    Method method = this.call.getMethod();
    if (method.isReturnValueVoid()) {
      throw new IllegalStateException("Cannot add return value to void method");
    }
    if (method.isReturnValuePrimitive() && value == null) {
      throw new IllegalStateException("Cannot add 'null' as return value to premitive method");
    }
    
    return this.andAnswer(AnswerFactory.forValue(value));
  }

  @Override
  public ExpectationSetters<Object> andThrow(final Throwable throwable) {
    Method method = this.call.getMethod();
    if (!method.canThrow(throwable)) {
      throw new IllegalStateException(Utils.cutPackage(throwable.getClass().getName()) + 
          " is not declared by " + call.getMethod());
    }
    
    return this.andAnswer(AnswerFactory.forThrowable(throwable));
  }
  
  @Override
  public ExpectationSetters<Object> andCallOnSuccess(final Object result) {
    if (!this.call.getMethod().isReturnValueVoid()) {
      throw new IllegalStateException("andCallOnSuccess() is only supported for void methods");
    }
    
    Class<?>[] argumentTypes = call.getMethod().getArgumentTypes();
    if (argumentTypes.length < 1 || 
        !Utils.isSubclass(argumentTypes[argumentTypes.length - 1], AsyncCallback.class)) {
      throw new IllegalStateException(
        "andCallOnSuccess() can only be used with methods " +
        "that take an AsyncCallback as last argument");
    }
    return this.andAnswer(AnswerFactory.forOnSuccess(result));
  }
  
  @Override
  public ExpectationSetters<Object> andCallOnFailure(final Throwable caught) {
    if (!this.call.getMethod().isReturnValueVoid()) {
      throw new IllegalStateException("andCallOnSuccess() is only supported for void methods");
    }
    
    Class<?>[] argumentTypes = call.getMethod().getArgumentTypes();
    if (argumentTypes.length < 1 ||
        !argumentTypes[argumentTypes.length - 1].equals(AsyncCallback.class)) {
      throw new IllegalStateException(
        "andCallOnFailure() can only be used with methods " +
        "that take an AsyncCallback as last argument");
    }
    return this.andAnswer(AnswerFactory.forOnFailure(caught));
  }

  @Override
  public ExpectationSetters<Object> times(int min, int max) {
    if (min < 0) {
      throw new IllegalArgumentException("min has to be non-negative");
    }
    if (max < 1) {
      throw new IllegalArgumentException("max has to be positive");
    }
    if (min > max) {
      throw new IllegalArgumentException("max has to be greater than min");
    }
    
    saveExpectation(new Range(min, max));
    return this;
  }
  
  @Override
  public ExpectationSetters<Object> times(int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Argument to times has to be greater than 1");
    }
    if (count == Range.UNLIMITED_MAX) {
      throw new IllegalArgumentException("Argument to times cannot be unlimited");
    }
    saveExpectation(new Range(count, count));
    return this;
  }

  @Override
  public ExpectationSetters<Object> once() {
    saveExpectation(Range.DEFAULT);
    return this;
  }

  @Override
  public ExpectationSetters<Object> atLeastOnce() {
    saveExpectation(new Range(1, Range.UNLIMITED_MAX));
    return this;
  }

  @Override
  public ExpectationSetters<Object> anyTimes() {
    saveExpectation(new Range(0, Range.UNLIMITED_MAX));
    return this;
  }
  
  private void saveExpectation(Range range) {
    checkIsNotRetired();
    
    if (!this.call.getMethod().isReturnValueVoid() && !this.unusedAnswer) {
      throw new IllegalStateException(
          "Missing behavior definition for preceding method call " + this.call.toString());
    }
    
    if (this.answer == null) {
      // void methods do not need an answer, create a dummy one.
      this.answer = AnswerFactory.forValue(null);
    }
    
    ExpectedCall expected = new ExpectedCall(this.call, this.matchers, 
                                             this.captures, this.answer, range);
    behavior.addExpected(expected);
    
    this.unusedAnswer = false;
    this.callUsedAtLeastOnce = true;
  }
  
  /**
   * Disables the ExpectationSetter. After calling this method it
   * cannot be used to record expectations anymore.
   */
  void retire() {
    if (this.unusedAnswer || !this.callUsedAtLeastOnce) {
      saveExpectation(Range.DEFAULT);
    }
    this.retired = true;
  }

  /**
   * In case no matchers were used for the call, this method creates
   * equal matchers for the provided arguments.
   */
  private List<ArgumentMatcher> createMissingMatchers(Call call, 
                                                       List<ArgumentMatcher> matchers) {
    if (matchers == null) {
      // no matchers used
      List<ArgumentMatcher> result = new ArrayList<ArgumentMatcher>();
      for (final Object argument : call.getArguments()) {
        result.add(new Equals(argument));
      }
      return result;
    }
  
    if (matchers.size() != call.getArguments().size()) {
      throw new IllegalStateException(""
          + call.getArguments().size()
          + " matchers expected, "
          + matchers.size()
          + " recorded.\n"
          + "This exception usually occurs when matchers "
          + "are mixed with raw values when recording a method:\n"
          + "\tmock.foo(5, ctrl.eq(6));\t// wrong\n"
          + "You need to use no matcher at all or a matcher for every single param:\n"
          + "\tmock.foo(ctrl.eq(5), ctrl.eq(6));\t// right\n"
          + "\tmock.foo(5, 6);\t// also right");
    }
    return matchers;    
  }
  
  /**
   * Check, if this setter is still valid to set expectations. 
   */
  private void checkIsNotRetired() {
    if (this.retired) {
      throw new IllegalStateException(
          "Cannot use this expectation setter anymore." +
          "\nThis exception usually occurs when you hold on to an ExpectationSetter." +
          "\nYou should not do that.");
    }
  }
}
