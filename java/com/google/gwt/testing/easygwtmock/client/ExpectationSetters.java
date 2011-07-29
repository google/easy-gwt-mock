/*
 * Copyright 2001-2010 the original author or authors.
 * Portions Copyright 2011 Google Inc.
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

package com.google.gwt.testing.easygwtmock.client;

/**
 * Allows setting expectations for an associated expected invocation.
 * Implementations of this interface are returned by
 * {@link MocksControl#expect(Object)}, and by {@link MocksControl#expectLastCall()}.
 * 
 * @param <T> type of what should be returned by this expected call
 * 
 * @author Michael Goderbauer
 * JavaDoc partly by OFFIS and Tammo Freese of EasyMock
 */
public interface ExpectationSetters<T> {

  /**
   * Sets a return value that will be returned for the expected invocation.
   */
  ExpectationSetters<T> andReturn(T value);
  
  /**
   * Sets a throwable that will be thrown for the expected invocation.
   */
  ExpectationSetters<T> andThrow(Throwable throwable);
  
  /**
   * Calls the onSuccess() method of a {@link com.google.gwt.user.client.rpc.AsyncCallback}
   * object, which is provided as argument to the expected invocation.
   * 
   * @param result
   *            is passed to the onSuccess() method
   */
  ExpectationSetters<Object> andCallOnSuccess(Object result);
  
  /**
   * Calls the onFailure() method of a {@link com.google.gwt.user.client.rpc.AsyncCallback}
   * object, which is provided as argument to the expected invocation.
   * 
   * @param caught
   *            is passed to the onFailure() method
   */
  ExpectationSetters<Object> andCallOnFailure(Throwable caught);

  /**
   * Sets an object that will be used to calculate the answer for the expected
   * invocation (either return a value, or throw an exception).
   */
  ExpectationSetters<T> andAnswer(Answer<? extends T> answer);

  /**
   * Expect the last invocation <code>count</code> times.
   */
  ExpectationSetters<T> times(int count);

  /**
   * Expect the last invocation between <code>min</code> and <code>max</code>
   * times.
   */
  ExpectationSetters<T> times(int min, int max);

  /**
   * Expect the last invocation once. This is the default.
   */
  ExpectationSetters<T> once();

  /**
   * Expect the last invocation at least once.
   */
  ExpectationSetters<T> atLeastOnce();

  /**
   * Expect the last invocation any times.
   */
  ExpectationSetters<T> anyTimes();
}
