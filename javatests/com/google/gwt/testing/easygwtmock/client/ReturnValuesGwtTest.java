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
 * Tests if return values can be specified.
 * 
 * @author Michael Goderbauer
 */
public class ReturnValuesGwtTest extends GWTTestCase {

  interface MyControl extends MocksControl {
    ToMock getMock();
  }
  
  interface ToMock {
    String returnString();
    int returnInt();
    void noReturnValue();
  }

  private MyControl ctrl;
  private ToMock mock; 
  
  @Override
  public void gwtSetUp() {
    this.ctrl = GWT.create(MyControl.class);
    this.mock = ctrl.getMock();
  }
  
  public void testAllExpectationMet() {
    ctrl.expect(mock.returnString()).andReturn("Hallo");
    ctrl.expect(mock.returnString()).andReturn("Hi");
    ctrl.expect(mock.returnInt()).andReturn(42);
    mock.returnInt();
    ctrl.expectLastCall().andReturn(44);
    mock.noReturnValue();
    
    ctrl.replay();
    
    assertEquals(mock.returnString(), "Hallo");
    assertEquals(mock.returnString(), "Hi");
    assertEquals(mock.returnInt(), 42);
    assertEquals(mock.returnInt(), 44);
    mock.noReturnValue();
    
    ctrl.verify();
  }
  
  public void testReturnValueNotSpecified() {
    mock.returnInt();
    
    try {
      ctrl.replay();
      fail();
    } catch (IllegalStateException expected) {
      assertEquals(expected.getMessage(), 
        "Missing behavior definition for preceding method call returnInt()");
    }
  }
  
  public void testNoMethodCall() {
    try {
      ctrl.expectLastCall().andReturn(44);
      fail();
    } catch (IllegalStateException expected) {
      assertEquals(expected.getMessage(), "Method call on mock needed before setting expectations");
    }
  }
  
  public void testSpecifyReturnForVoidMethods() {
    mock.noReturnValue();
    try {
      ctrl.expectLastCall().andReturn(44);
      fail();
    } catch (IllegalStateException expected) {
      assertEquals(expected.getMessage(), "Cannot add return value to void method");
    }
  }
  
  public void testSetNullAsReturnValueForPrimitiveMethod() {
    try {
      ctrl.expect(mock.returnInt()).andReturn(null);
      fail();
    } catch (IllegalStateException expected) {
      assertEquals(expected.getMessage(), "Cannot add 'null' as return value to premitive method");
    }
  }
  
  public void testChaining() {
    ctrl.expect(mock.returnInt()).andReturn(11).andReturn(12).andReturn(13);
    
    ctrl.replay();
    
    assertEquals(11, mock.returnInt());
    assertEquals(12, mock.returnInt());
    assertEquals(13, mock.returnInt());
    
    try {
      mock.returnInt();
      fail();
    } catch (AssertionError expected) {
    }
  }
  
  @Override
  public String getModuleName() {
    return "com.google.gwt.testing.easygwtmock.easygwtmock";
  }
}
