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

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * We only test if we can record simple expectations (e. g. method x gets invoked")
 * and if those mocked methods return their specified default value.
 * 
 * @author Michael Goderbauer
 */
public class SimpleExpectationsGwtTest extends GWTTestCase {
  
  private InterfaceToMock mock;
  private MyIMockControl ctrl;
  
  interface MyIMockControl extends MocksControl {
    InterfaceToMock getMock();
  }
  
  interface InterfaceToMock {
    String returnString();
    int returnInt();
    Integer returnInteger();
    boolean returnBoolean();
    void noReturnValue();
  } 
  
  @Override
  public void gwtSetUp() {
    this.ctrl = GWT.create(MyIMockControl.class);
    this.mock = ctrl.getMock();
  }
  
  public void testAllExpectationMet() {
    ctrl.expect(mock.returnString()).andReturn("Hi");
    ctrl.expect(mock.returnInt()).andReturn(42);
    ctrl.expect(mock.returnInteger()).andReturn(3);
    ctrl.expect(mock.returnBoolean()).andReturn(true);
    mock.noReturnValue();

    ctrl.replay();
    
    assertEquals(mock.returnString(), "Hi");
    assertEquals(mock.returnInt(), 42);
    assertEquals(mock.returnInteger(), new Integer(3));
    assertTrue(mock.returnBoolean());
    mock.noReturnValue();
    
    ctrl.verify();
  }
  
  public void testOneExpectedMethodCallMissing() {
    mock.noReturnValue();
    ctrl.expect(mock.returnInt()).andReturn(10);

    ctrl.replay();
    
    mock.noReturnValue();
    // We do not call mock.returnInt();
    
    boolean exceptionThrown = true;
    try {
      ctrl.verify();
      exceptionThrown = false;
    } catch (AssertionError expected) {
      assertEquals("\n  Expectation failure on verify. List of all expectations:" +
          "\n        noReturnValue(): expected 1, actual 1" +
          "\n    --> returnInt(): expected 1, actual 0\n", expected.getMessage());
    }
    assertTrue("should have thrown exception", exceptionThrown);
  }
  
  public void testUnexpectedMethodCall() {
    mock.noReturnValue();

    ctrl.replay();
    
    mock.noReturnValue();
    
    boolean exceptionThrown = true;
    try {
      mock.noReturnValue(); // We only expected it once!
      exceptionThrown = false;
    } catch (AssertionError expected) {
      assertEquals("\n  Unexpected method call noReturnValue(). List of all expectations:" + 
          "\n  Potential matches are marked with (+1)." +
          "\n        noReturnValue(): expected 1, actual 1 (+1)\n", expected.getMessage());
    } 
    assertTrue("should have thrown exception", exceptionThrown);
    
    ctrl.verify();
  }
  
  @Override
  public String getModuleName() {
    return "com.google.gwt.testing.easygwtmock.easygwtmock";
  }
}

