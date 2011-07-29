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
 * Tests the Util class
 * 
 * @author Michael Goderbauer
 */
public class UtilsJavaTest extends TestCase {

  private StringBuffer buffer;

  @Override
  public void setUp() {
    this.buffer = new StringBuffer();
  }
  
  public void testAppendArgumentTo_null() {
    Utils.appendArgumentTo(null, buffer);
    assertEquals("null", buffer.toString());
  }
  
  public void testAppendArgumentTo_primitive() {
    Utils.appendArgumentTo(4, buffer);
    assertEquals("4", buffer.toString());
  }
  
  public void testAppendArgumentTo_primitiveArray() {
    double[] doubleArry = {1.2, 1.3, 1.4};
    Utils.appendArgumentTo(doubleArry, buffer);
    assertEquals("[1.2, 1.3, 1.4]", buffer.toString());
  }
  
  public void testAppendArgumentTo_objectArray() {
    String[] stringArray = {"Hallo", "Hi"};
    Utils.appendArgumentTo(stringArray, buffer);
    assertEquals("[Hallo, Hi]", buffer.toString());
  }
}
