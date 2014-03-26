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

package com.google.gwt.testing.easygwtmock.client;

import junit.framework.TestCase;

import java.util.List;

/**
 * Tests the Capture class
 * 
 * @author Michael Goderbauer
 */
public class CaptureJavaTest extends TestCase {
  
  private Capture<Integer> capture;
  
  @Override
  public void setUp() {
    capture = new Capture<Integer>();
  }
  
  public void testCreate() {
    String test = "test";
    Capture<String> captureString = Capture.create();
    captureString.captureValue(test);
    assertEquals(test, captureString.getFirstValue());
  }

  public void testGetValues() {
    assertTrue("should be empty", capture.getValues().isEmpty());
    
    capture.captureValue(10);
    capture.captureValue(20);
    
    List<Integer> values = capture.getValues();
    assertEquals(2, values.size());
    assertEquals(10, (int) values.get(0));
    assertEquals(20, (int) values.get(1));
  }
  
  public void testHasCaptured() {
    assertFalse("should not have captured", capture.hasCaptured());
    
    capture.captureValue(10);
    capture.captureValue(20);
    
    assertTrue("should have captured", capture.hasCaptured());
  }

  public void testGetFirstAndLastValue_oneValue() {
    capture.captureValue(10);
    
    assertEquals(10, (int) capture.getFirstValue());
    assertEquals(10, (int) capture.getLastValue());
  }
  
  public void testGetFirstAndLastValue_threeValue() {
    capture.captureValue(10);
    capture.captureValue(20);
    capture.captureValue(30);
    
    assertEquals(10, (int) capture.getFirstValue());
    assertEquals(30, (int) capture.getLastValue());
  }
  
  public void testToString() {
    assertEquals("<nothing>", capture.toString());
    
    capture.captureValue(10);
    
    assertEquals("10", capture.toString());
    
    capture.captureValue(20);
    
    assertEquals("10, 20", capture.toString());
  }
  
  public void testReset() {
    assertFalse("should not have captured", capture.hasCaptured());
    capture.captureValue(10);
    assertTrue("should have captured", capture.hasCaptured());
    assertFalse("should not be empty", capture.getValues().isEmpty());
    
    capture.reset();
    
    assertFalse("should not have captured", capture.hasCaptured());
    assertTrue("should be empty", capture.getValues().isEmpty());
  }
}
