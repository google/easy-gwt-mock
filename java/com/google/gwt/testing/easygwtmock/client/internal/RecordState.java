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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An object representing the record state.
 * 
 * @author Michael Goderbauer
 */
public class RecordState implements MocksControlState {

  private final MocksBehavior behavior;
  List<ArgumentMatcher> argumentMatchers; // Visible for testing
  Set<ArgumentCapture> argumentCaptures; // Visible for testing
  ExpectationSettersImpl currentSetter; // Visible for testing
  private String unmockableLastCall;
  
  RecordState(MocksBehavior behavior) {
    this.behavior = behavior;
  }

  @Override
  public void checkCanSwitchToReplay() {
    retireCurrentSetter();
  }
  
  @Override
  public ExpectationSetters<Object> getExpectationSetter() throws IllegalStateExceptionWrapper {
    if (this.unmockableLastCall != null) {
      throw new IllegalStateExceptionWrapper(
          new IllegalStateException("Method " + this.unmockableLastCall + " cannot be mocked"));
    }
    if (this.currentSetter == null) {
      throw new IllegalStateExceptionWrapper(
          new IllegalStateException("Method call on mock needed before setting expectations"));
    }
    return this.currentSetter;
  }

  @Override
  public void verify() throws IllegalStateExceptionWrapper {
    throw new IllegalStateExceptionWrapper(
        new IllegalStateException("Calling verify is not allowed in record state"));
  }

  @Override
  public Answer<? extends Object> invoke(final Call call) {
    retireCurrentSetter();
    this.currentSetter = new ExpectationSettersImpl(call, this.argumentMatchers,
                                               this.argumentCaptures, this.behavior);
    this.unmockableLastCall = null;
    this.argumentMatchers = null;
    this.argumentCaptures = null;
    return AnswerFactory.forValue(call.getDefaultReturnValue());
  }
  
  @Override
  public void unmockableCallTo(String methodName) {
    retireCurrentSetter();
    this.argumentMatchers = null;
    this.argumentCaptures = null;
    this.unmockableLastCall = methodName;
  }

  @Override
  public void reportMatcher(ArgumentMatcher matcher) {
    if (this.argumentMatchers == null) {
      this.argumentMatchers = new ArrayList<ArgumentMatcher>();
    }
    this.argumentMatchers.add(matcher);
  }
  
  private void retireCurrentSetter() {
    if (this.currentSetter != null) {
      this.currentSetter.retire();
      this.currentSetter = null;
    }
  }

  @Override
  public void reportCapture(ArgumentCapture argumentCapture) throws IllegalStateExceptionWrapper {
    this.reportMatcher(argumentCapture);
    if (this.argumentCaptures == null) {
      this.argumentCaptures = new HashSet<ArgumentCapture>();
    }
    if (!this.argumentCaptures.add(argumentCapture)) {
      throw new IllegalStateExceptionWrapper(
          new IllegalStateException("Cannot use same capture twice for same method call"));
    }
  }
}
