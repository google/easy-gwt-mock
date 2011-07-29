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
import com.google.gwt.testing.easygwtmock.client.internal.matchers.ArgumentCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * An object that represents an expected method call.
 * 
 * @author Michael Goderbauer
 */
public class ExpectedCall {
 
  private final Call call; // call made during recording to set up expectation
  private final List<ArgumentMatcher> matchers;
  private final Set<ArgumentCapture> captures;
  private final Answer<? extends Object> answer;
  private final Range range;
  
  private int answerUsageCount;

  ExpectedCall(Call call, List<ArgumentMatcher> matchers, Set<ArgumentCapture> captures,
                Answer<? extends Object> answer, Range range) {
    this.call = call;
    this.matchers = matchers;
    this.captures = captures;
    this.answer =  answer;
    this.range = range;
    this.answerUsageCount = 0;
  }
  
  /**
   * Determines if the provided call matches this expected call.
   */
  boolean matches(Call actual) {
    return this.call.getMock() == actual.getMock()
        && this.call.getMethod() == actual.getMethod()
        && matchesArguments(actual.getArguments());
  }

  /**
   * Determines if the provided argument list fulfills the argument matchers of this call.
   */
  private boolean matchesArguments(List<Object> arguments) {
    if (arguments.size() != matchers.size()) {
      return false;
    }
    for (int i = 0; i < arguments.size(); i++) {
      if (!matchers.get(i).matches(arguments.get(i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append(call.getMethod().getName());
    result.append("(");
    for (Iterator<ArgumentMatcher> it = matchers.iterator(); it.hasNext();) {
      it.next().appendTo(result);
      if (it.hasNext()) {
        result.append(", ");
      }
    }
    result.append(")");
    return result.toString();
  }

  boolean expectationMet() {
    return this.range.includes(this.answerUsageCount);
  }

  boolean canBeInvoked() {
    return this.answerUsageCount < this.range.getMax();
  }
  
  Answer<? extends Object> invoke() {
    this.answerUsageCount++;
    captureArguemnts();
    return this.answer;
  }
  
  private void captureArguemnts() {
    if (this.captures == null) {
      return;
    }
    for (ArgumentCapture capture : this.captures) {
      capture.captureArgument();
    }
  }

  int getCallCount() {
    return this.answerUsageCount;
  }

  String getExpectedCallRange() {
    return this.range.toString();
  }
}
