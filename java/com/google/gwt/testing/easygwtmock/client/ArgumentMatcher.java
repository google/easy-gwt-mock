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
 * Decides whether an actual argument is accepted.
 * Implement this interface to write your own matcher.
 * 
 * @author Michael Goderbauer
 * Originally written for EasyMock {@link "www.easymock.org"} by OFFIS, Tammo Freese
 */
public interface ArgumentMatcher {
  /**
   * Returns true if this matcher accepts the given argument.
   * <p>
   * Like Object.equals(), it should be aware that the argument passed might
   * be null and of any type. So you will usually start the method with an
   * instanceof and/or null check.
   * <p>
   * The method should <b>never</b> assert if the argument doesn't match. It
   * should only return false. EasyGwtMock will take care of asserting if the
   * call is really unexpected.
   */
  boolean matches(Object argument);

  /**
   * Appends a string representation of this matcher to the given buffer. In
   * case of failure, the printed message will show this string to allow to
   * know which matcher was used for the failing call.
   */
  void appendTo(StringBuffer buffer);
}
