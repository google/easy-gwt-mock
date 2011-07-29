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

import junit.framework.TestCase;

/**
 * Tests the Range class
 * 
 * @author Michael Goderbauer
 */
public class RangeJavaTest extends TestCase {
  
  public void testToString() {
    Range range = new Range(1, 1);
    assertEquals("1", range.toString());
    
    range = new Range(1, 4);
    assertEquals("between 1 and 4", range.toString());
    
    range = new Range(1, Range.UNLIMITED_MAX);
    assertEquals("at least 1", range.toString());
  }
  
  public void testIncludes() {
    Range range = new Range(4, 10);
    
    assertTrue("should include 6", range.includes(6));
    assertTrue("should include 4", range.includes(4));
    assertTrue("should include 10", range.includes(10));
    
    assertFalse("should not include 11", range.includes(11));
    assertFalse("should not include 3", range.includes(3));
  }
}
