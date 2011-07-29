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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * An object that keeps track of the expected method calls of mock objects (= behavior
 * of mock objects).
 * 
 * @author Michael Goderbauer
 */
public class MocksBehavior {
  private final Queue<ExpectedCall> expectedCalls;
  private Set<Object> niceMocks;
 
  MocksBehavior() {
    this.expectedCalls = new LinkedList<ExpectedCall>();
    this.niceMocks = new HashSet<Object>();
  }
  
  /**
   * Checks if all expected calls were met.
   */
  void verify() throws AssertionErrorWrapper {
    for (ExpectedCall call : this.expectedCalls) {
      if (!call.expectationMet()) {
        StringBuilder error = new StringBuilder("\n  Expectation failure on verify. ");
        appendExpectationList(error, true, null);
        throw new AssertionErrorWrapper(new AssertionError(error.toString()));
      }
    }
  }

  /**
   * Adds an expected call to the behavior of the mocks.
   */
  void addExpected(ExpectedCall expected) {
    this.expectedCalls.add(expected);
  }

  /**
   * Checks, if an actual call was expected.
   * 
   * @return expected return value for invocation
   */
  Answer<? extends Object> addActual(Call actual) throws AssertionErrorWrapper {
    for (ExpectedCall expected : this.expectedCalls) {
      if (!expected.canBeInvoked()) {
        continue;
      }
      if (!expected.matches(actual)) {
        continue;
      }
      return expected.invoke();
    }
    
    if (isNiceMock(actual.getMock())) {
      return AnswerFactory.forValue(actual.getDefaultReturnValue());
    }
    
    StringBuilder error = new StringBuilder("\n  Unexpected method call ");
    error.append(actual.toString()).append(". ");
    appendExpectationList(error, true, actual);
    throw new AssertionErrorWrapper(new AssertionError(error.toString()));
  }

  private boolean isNiceMock(Object mock) {
    return this.niceMocks.contains(mock);
  }
  
  void addNiceMock(Object mock) {
    this.niceMocks.add(mock);
  }
  
  void removeNiceMock(Object mock) {
    this.niceMocks.remove(mock);
  }

  private void appendExpectationList(StringBuilder builder, boolean markUnfullfiled,
                                     Call matchingCall) {
    builder.append("List of all expectations:");
    
    if (this.expectedCalls.size() == 0) {
      builder.append("\n    <empty>\n");
      return;
    }
    
    if (matchingCall != null) {
      builder.append("\n  Potential matches are marked with (+1).");
    }
    
    for (ExpectedCall expected : this.expectedCalls) {
      builder.append("\n    ");
      if (markUnfullfiled && !expected.expectationMet()) {
        builder.append("--> ");
      } else {
        builder.append("    ");
      }
      builder.append(expected.toString())
          .append(": expected ").append(expected.getExpectedCallRange())
          .append(", actual ").append(expected.getCallCount());
      if (matchingCall != null && expected.matches(matchingCall)) {
        builder.append(" (+1)");
      }
    }

    builder.append("\n");
  }
}
