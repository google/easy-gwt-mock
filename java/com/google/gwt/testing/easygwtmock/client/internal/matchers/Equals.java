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

package com.google.gwt.testing.easygwtmock.client.internal.matchers;

import com.google.gwt.testing.easygwtmock.client.ArgumentMatcher;
import com.google.gwt.testing.easygwtmock.client.internal.Utils;

/**
 * Matches any argument that is equal to a given value.
 * Equality is determined by .equals().
 * 
 * @author Michael Goderbauer
 * Originally written for EasyMock {@link "www.easymock.org"} by OFFIS, Tammo Freese
 */
public class Equals implements ArgumentMatcher {

  private Object expected;

  public Equals(Object expected) {
      this.expected = expected;
  }
  
  @Override
  public boolean matches(Object actual) {
    if (this.expected == null) {
      return actual == null;
    }
    return expected.equals(actual);
  }

  @Override
  public void appendTo(StringBuffer buffer) {
    Utils.appendArgumentTo(this.expected, buffer);
  }
}
