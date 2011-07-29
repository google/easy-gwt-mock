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
 * Interface for the states a MocksManager can be in
 * (state pattern).
 * 
 * {@link "http://en.wikipedia.org/wiki/State_pattern"} 
 * 
 * @author Michael Goderbauer
 */
interface MocksControlState {

  void checkCanSwitchToReplay() throws IllegalStateExceptionWrapper;

  void verify() throws AssertionErrorWrapper, IllegalStateExceptionWrapper;

  Answer<? extends Object> invoke(Call invocation) throws AssertionErrorWrapper;

  void reportMatcher(ArgumentMatcher matcher) throws IllegalStateExceptionWrapper;
  
  void reportCapture(ArgumentCapture argumentCapture) throws IllegalStateExceptionWrapper;

  ExpectationSetters<Object> getExpectationSetter() throws IllegalStateExceptionWrapper;

  void unmockableCallTo(String methodName);

}
