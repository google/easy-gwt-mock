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

import java.util.List;

/**
 * Tests to capture arguments of expected method calls
 * 
 * @author Michael Goderbauer
 */
public class CaptureGwtTest extends BaseGwtTestCase {

  interface MyControl extends MocksControl {
    ToMock getMock();
  }
  
  interface ToMock {
    void doSomething(String s, int i, boolean b);
    int returnInt(String s, int i, boolean b);
    boolean returnBoolean(String s, int i, boolean b);
    String returnString(String s, int i, boolean b);
  }

  private MyControl ctrl;
  private ToMock mock;
  private Capture<Boolean> captureBoolean;
  private Capture<Integer> captureInteger;
  private Capture<String> captureString; 
  
  @Override
  public void gwtSetUp() {
    this.ctrl = GWT.create(MyControl.class);
    this.mock = ctrl.getMock();
    this.captureBoolean = new Capture<Boolean>();
    this.captureInteger = new Capture<Integer>();
    this.captureString = new Capture<String>();
  }
  
  public void testCaptureValuesOfBooleanMethod() {
    ctrl.expect(mock.returnBoolean(ctrl.captureObject(captureString),
                                   ctrl.captureInt(captureInteger),
                                   ctrl.captureBoolean(captureBoolean))).andReturn(true);
    
    ctrl.replay();
    
    mock.returnBoolean("Hallo", 356, true);
    
    ctrl.verify();
    
    assertTrue(captureBoolean.hasCaptured());
    assertTrue(captureInteger.hasCaptured());
    assertTrue(captureString.hasCaptured());
    
    assertEquals("Hallo", captureString.getFirstValue());
    assertEquals(356, (int) captureInteger.getFirstValue());
    assertTrue(captureBoolean.getFirstValue());
  }
  
  public void testCaptureValuesOfIntMethod() {
    ctrl.expect(mock.returnInt(ctrl.captureObject(captureString),
                               ctrl.captureInt(captureInteger),
                               ctrl.captureBoolean(captureBoolean))).andReturn(22);
    
    ctrl.replay();
    
    mock.returnInt("Hallo", 356, true);
    
    ctrl.verify();
    
    assertTrue(captureBoolean.hasCaptured());
    assertTrue(captureInteger.hasCaptured());
    assertTrue(captureString.hasCaptured());
    
    assertEquals("Hallo", captureString.getFirstValue());
    assertEquals(356, (int) captureInteger.getFirstValue());
    assertTrue(captureBoolean.getFirstValue());
  }
  
  public void testCaptureValuesOfStringMethod() {
    ctrl.expect(mock.returnString(ctrl.captureObject(captureString),
                                  ctrl.captureInt(captureInteger),
                                  ctrl.captureBoolean(captureBoolean))).andReturn("Ho");
    
    ctrl.replay();
    
    mock.returnString("Hallo", 356, true);
    
    ctrl.verify();
    
    assertTrue(captureBoolean.hasCaptured());
    assertTrue(captureInteger.hasCaptured());
    assertTrue(captureString.hasCaptured());
    
    assertEquals("Hallo", captureString.getFirstValue());
    assertEquals(356, (int) captureInteger.getFirstValue());
    assertTrue(captureBoolean.getFirstValue());
  }
  
  public void testCaptureValuesOfVoidMethod() {
    mock.doSomething(ctrl.captureObject(captureString),
                     ctrl.captureInt(captureInteger),
                     ctrl.captureBoolean(captureBoolean));
    
    ctrl.replay();
    
    mock.doSomething("Hallo", 356, true);
    
    ctrl.verify();
    
    assertTrue(captureBoolean.hasCaptured());
    assertTrue(captureInteger.hasCaptured());
    assertTrue(captureString.hasCaptured());
    
    assertEquals("Hallo", captureString.getFirstValue());
    assertEquals(356, (int) captureInteger.getFirstValue());
    assertTrue(captureBoolean.getFirstValue());
  }
  
  public void testCaptureMoreValuesInOneCapture() {
    ctrl.expect(mock.returnInt(ctrl.captureObject(captureString), ctrl.eq(322), ctrl.eq(true)))
        .andReturn(21).times(3);
    
    ctrl.replay();
    
    mock.returnInt("Hallo", 322, true);
    mock.returnInt("Hi", 322, true);
    mock.returnInt("Bye", 322, true);
    
    ctrl.verify();
    
    assertTrue(captureString.hasCaptured());
    
    assertEquals("Hallo", captureString.getFirstValue());
    assertEquals("Bye", captureString.getLastValue());
    
    List<String> values = captureString.getValues();
    assertEquals(3, values.size());
    assertEquals("Hallo", values.get(0));
    assertEquals("Hi", values.get(1));
    assertEquals("Bye", values.get(2));
  }
}
