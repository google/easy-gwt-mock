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

/**
 * An object representing the replay state.
 * 
 * @author Michael Goderbauer
 */
public class ReplayState implements MocksControlState {

  private MocksBehavior behavior;
  
  ReplayState(MocksBehavior behavior) {
    this.behavior = behavior;
  }
  
  @Override
  public ExpectationSetters<Object> getExpectationSetter() throws IllegalStateExceptionWrapper {
    throw new IllegalStateExceptionWrapper(
      new IllegalStateException("Cannot set expectations while in replay state"));
  }

  @Override
  public void checkCanSwitchToReplay() throws IllegalStateExceptionWrapper {
    throw new IllegalStateExceptionWrapper(
        new IllegalStateException("Cannot switch to replay mode while in replay state"));
  }

  @Override
  public void verify() throws AssertionErrorWrapper {
    this.behavior.verify();
  }

  @Override
  public Answer<? extends Object> invoke(Call call) throws AssertionErrorWrapper {
    return this.behavior.addActual(call);
  }

  @Override
  public void reportMatcher(ArgumentMatcher matcher) throws IllegalStateExceptionWrapper {
    throw new IllegalStateExceptionWrapper(
        new IllegalStateException("Argument matchers must not be used in replay state"));
  }

  @Override
  public void reportCapture(ArgumentCapture argumentCapture) throws IllegalStateExceptionWrapper {
    throw new IllegalStateExceptionWrapper(
        new IllegalStateException("Captures must not be used in replay state"));
  }

  @Override
  public void unmockableCallTo(String methodName) {
    // we don't care about this during replay mode
  }
}
