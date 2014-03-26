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
import com.google.gwt.testing.easygwtmock.client.BaseGwtTestCase;

/**
 * Test behavior of unmockable toString(), equals(), hashCode()
 * 
 * @author Michael Goderbauer
 */
public class UnmockableMethodsGwtTest extends BaseGwtTestCase {
  
  interface MyControl extends MocksControl {
    ToMock getMock();
  }
  
  interface ToMock {
    int aMethod();
  }
  
  private MyControl ctrl;
  private ToMock mock; 
  
  @Override
  public void gwtSetUp() {
    this.ctrl = GWT.create(MyControl.class);
    this.mock = ctrl.getMock();
  }

  public void testMockingUnmockableMethod_toString() {
    try {
      ctrl.expect(mock.toString()).andReturn("Hallo");
      fail("should have thrown exception");
    } catch (IllegalStateException expected) {
      assertEquals("Method toString() cannot be mocked", expected.getMessage());
    }
  }
  
  public void testMockingUnmockableMethod_equals() {
    try {
      ctrl.expect(mock.equals(ctrl.anyInt())).andReturn(true);
      fail("should have thrown exception");
    } catch (IllegalStateException expected) {
      assertEquals("Method equals() cannot be mocked", expected.getMessage());
    }
  }
  
  public void testMockingUnmockableMethod_hashCode() {
    try {
      ctrl.expect(mock.hashCode()).andReturn(42);
      fail("should have thrown exception");
    } catch (IllegalStateException expected) {
      assertEquals("Method hashCode() cannot be mocked", expected.getMessage());
    }
  }
  
  public void testImplementationOfUnmockableMethods_equals() {
    ToMock secondMock = ctrl.getMock();
    
    assertTrue("should be equal", mock.equals(mock));
    assertFalse("should not be equal", mock.equals(42));
    assertFalse("should be equal", mock.equals(secondMock));
    
    ctrl.reset();
    ctrl.replay();
    
    assertTrue("should be equal", mock.equals(mock));
    assertFalse("should not be equal", mock.equals(42));
    assertFalse("should be equal", mock.equals(secondMock));
    
    ctrl.verify();
  }
  
  public void testImplementationOfUnmockableMethods_hashCode() {
    int hashCode = mock.hashCode();
    
    ctrl.reset();
    ctrl.replay();
    
    assertEquals(hashCode, mock.hashCode());
    
    ctrl.verify();
  }
  
  public void testImplementationOfUnmockableMethods_toString() {
    assertEquals("Mock for UnmockableMethodsGwtTest.ToMock", mock.toString());
    
    ctrl.reset();
    ctrl.replay();
    
    assertEquals("Mock for UnmockableMethodsGwtTest.ToMock", mock.toString());
    
    ctrl.verify();
  }
}
