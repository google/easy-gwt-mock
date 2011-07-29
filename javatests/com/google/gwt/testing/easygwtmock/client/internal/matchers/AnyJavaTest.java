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

package com.google.gwt.testing.easygwtmock.client.internal.matchers;

import junit.framework.TestCase;

import java.util.HashSet;

/**
 * Tests the Any class
 * 
 * @author Michael Goderbauer
 */
public class AnyJavaTest extends TestCase {

  private Any any;

  @Override
  public void setUp() {
    any = Any.ANY;
  }
  
  public void testMatches() {
    assertTrue(any.matches(null));
    assertTrue(any.matches(23));
    assertTrue(any.matches("Hallo"));
    assertTrue(any.matches(new Object()));
    assertTrue(any.matches(new HashSet<Integer>()));
  }
  
  public void testAppend() {
    StringBuffer buffer = new StringBuffer();
    any.appendTo(buffer);
    assertEquals("<any>", buffer.toString());
  }
}
