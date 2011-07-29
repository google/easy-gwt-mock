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
 * Used to answer expected calls.
 * 
 * @param <T>
 *            the type to return.
 * 
 * @author Michael Goderbauer
 * Originally written for EasyMock {@link "www.easymock.org"} by OFFIS, Tammo Freese
 */
public interface Answer<T> {

  /**
   * Is called by EasyGwtMock to answer an expected call. The answer may be to
   * return a value, or to throw an exception. Be careful when using the methods
   * arguments - using them is not refactoring-safe.
   * 
   * @return the value to be returned
   * @throws Throwable
   *             the throwable to be thrown
   */
  T answer(Object[] args) throws Throwable;
}
