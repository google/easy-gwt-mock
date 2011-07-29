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

import com.google.gwt.testing.easygwtmock.client.Capture;

import junit.framework.TestCase;

/**
 * @author Michael Goderbauer
 */
public class ArgumentCaptureJavaTest extends TestCase {

  private ArgumentCapture argumentCapture;
  private Capture<Integer> capture;

  @Override
  public void setUp() {
    capture = new Capture<Integer>();
    argumentCapture = new ArgumentCapture(capture);
  }
  
  public void testMatches() {
    assertTrue("should match", argumentCapture.matches(10));
    assertTrue("should match", argumentCapture.matches(null));
    assertTrue("should match", argumentCapture.matches("Hallo"));
    assertTrue("should match", argumentCapture.matches(new Object()));
  }
  
  public void testCaptureArgument() {
    argumentCapture.matches(10);
    argumentCapture.matches(20);
    
    assertFalse("should not have captured", capture.hasCaptured());
    
    argumentCapture.captureArgument();
    
    assertTrue("should have captured", capture.hasCaptured());
    assertEquals(1, capture.getValues().size());
    assertEquals(20, (int) capture.getFirstValue());
  }
  
  public void testAppendTo() {
    StringBuffer buffer = new StringBuffer();
    argumentCapture.appendTo(buffer);
    assertEquals("captured(<nothing>)", buffer.toString());
    
    argumentCapture.matches(20);
    argumentCapture.captureArgument();
    
    buffer = new StringBuffer();
    argumentCapture.appendTo(buffer);
    assertEquals("captured(20)", buffer.toString());
    
    argumentCapture.matches(30);
    argumentCapture.captureArgument();
    
    buffer = new StringBuffer();
    argumentCapture.appendTo(buffer);
    assertEquals("captured(20, 30)", buffer.toString());
  }
}
