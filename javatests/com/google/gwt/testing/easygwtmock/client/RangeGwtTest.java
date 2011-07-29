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
 * We test setting ranges on expected calls.
 * 
 * @author Michael Goderbauer
 */
public class RangeGwtTest extends GWTTestCase {

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
  
  public void testTimes_allMet() {
    ctrl.expect(mock.returnString()).andReturn("Hallo").times(2).andReturn("Hi").times(2, 3);
    
    ctrl.replay();
    
    assertEquals("Hallo", mock.returnString());
    assertEquals("Hallo", mock.returnString());
    assertEquals("Hi", mock.returnString());
    assertEquals("Hi", mock.returnString());
    assertEquals("Hi", mock.returnString());
    
    ctrl.verify();
  }
  
  public void testTimes_unexpectedCall() {
    ctrl.expect(mock.returnString()).andReturn("Hallo").times(2).andReturn("Hi").times(2, 3);
    
    ctrl.replay();
    
    assertEquals("Hallo", mock.returnString());
    assertEquals("Hallo", mock.returnString());
    assertEquals("Hi", mock.returnString());
    assertEquals("Hi", mock.returnString());
    assertEquals("Hi", mock.returnString());

    boolean exceptionThrown = true;
    try {
      assertEquals("Hi", mock.returnString()); //unexpected
      exceptionThrown = false;
    } catch (AssertionError expected) {
      assertEquals("\n  Unexpected method call returnString(). List of all expectations:" +
          "\n  Potential matches are marked with (+1)." +
          "\n        returnString(): expected 2, actual 2 (+1)" +
          "\n        returnString(): expected between 2 and 3, actual 3 (+1)\n",
          expected.getMessage());
    } 
    assertTrue("should have thrown exception", exceptionThrown);

    ctrl.verify();
  }
  
  public void testTimes_missingCall() {
    ctrl.expect(mock.returnString()).andReturn("Hallo").times(2).andReturn("Hi").times(2, 3);
    
    ctrl.replay();
    
    assertEquals("Hallo", mock.returnString());
    assertEquals("Hallo", mock.returnString());
    assertEquals("Hi", mock.returnString());

    boolean exceptionThrown = true;
    try {
      ctrl.verify();
      exceptionThrown = false;
    } catch (AssertionError expected) {
      assertEquals("\n  Expectation failure on verify. List of all expectations:" +
          "\n        returnString(): expected 2, actual 2" +
          "\n    --> returnString(): expected between 2 and 3, actual 1\n", expected.getMessage());
    } 
    assertTrue("should have thrown exception", exceptionThrown);
  }
  
  public void testAnyTimes() {
    ctrl.expect(mock.returnString()).andReturn("Hallo").andReturn("Hi").anyTimes();
    
    ctrl.replay();
    
    assertEquals("Hallo", mock.returnString());
    assertEquals("Hi", mock.returnString());
    assertEquals("Hi", mock.returnString());
    assertEquals("Hi", mock.returnString());

    ctrl.verify();
  }
  
  public void testTimes_voidMethods_allMet1() {
    mock.noReturnValue();
    ctrl.expectLastCall().times(2, 8);
    
    ctrl.replay();
    
    mock.noReturnValue();
    mock.noReturnValue();
    mock.noReturnValue();
    mock.noReturnValue();

    ctrl.verify();
  }
  
  public void testTimes_voidMethods_allMet2() {
    mock.noReturnValue();
    ctrl.expectLastCall().times(3).times(2);
    
    ctrl.replay();
    
    mock.noReturnValue();
    mock.noReturnValue();
    mock.noReturnValue();
    mock.noReturnValue();
    mock.noReturnValue();

    ctrl.verify();
  }
  
  public void testTimes_voidMethods_unexpectedCall() {
    mock.noReturnValue();
    ctrl.expectLastCall().times(2, 4);
    
    ctrl.replay();
    
    mock.noReturnValue();
    mock.noReturnValue();
    mock.noReturnValue();
    mock.noReturnValue();
    
    
    boolean exceptionThrown = true;
    try {
      mock.noReturnValue();
      exceptionThrown = false;
    } catch (AssertionError expected) {
      assertEquals("\n  Unexpected method call noReturnValue(). List of all expectations:" +
          "\n  Potential matches are marked with (+1)." +
          "\n        noReturnValue(): expected between 2 and 4, actual 4 (+1)\n",
          expected.getMessage());
    } 
    assertTrue("should have thrown exception", exceptionThrown);

    ctrl.verify();
  }
  
  public void testTimes_voidMethods_callMissing() {
    mock.noReturnValue();
    ctrl.expectLastCall().times(2);
    ctrl.expectLastCall().times(3);
    
    ctrl.replay();
    
    mock.noReturnValue();
    mock.noReturnValue();
    mock.noReturnValue();
    mock.noReturnValue();
    
    
    boolean exceptionThrown = true;
    try {
      ctrl.verify();
      exceptionThrown = false;
    } catch (AssertionError expected) {
      assertEquals("\n  Expectation failure on verify. List of all expectations:" +
          "\n        noReturnValue(): expected 2, actual 2" +
          "\n    --> noReturnValue(): expected 3, actual 2\n", expected.getMessage());
    } 
    assertTrue("should have thrown exception", exceptionThrown);
  }
  
  public void testTimes_negativeMin() {
    try {
      ctrl.expect(mock.returnInt()).andReturn(43).times(-2);
      fail("should have thrown exception");
    } catch (IllegalArgumentException expected) {
    }
  }
  
  public void testTimes_negativeMax() {
    try {
      ctrl.expect(mock.returnInt()).andReturn(43).times(3, 1);
      fail("should have thrown exception");
    } catch (IllegalArgumentException expected) {
    }
  }
  
  public void testTimes_illegalRange() {
    try {
      ctrl.expect(mock.returnInt()).andReturn(43).times(-3, -1);
      fail("should have thrown exception");
    } catch (IllegalArgumentException expected) {
    }
  }
  
  @Override
  public String getModuleName() {
    return "com.google.gwt.testing.easygwtmock.easygwtmock";
  }
}
