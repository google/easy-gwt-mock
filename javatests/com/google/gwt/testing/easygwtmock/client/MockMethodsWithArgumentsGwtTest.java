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
 * We test if the expected arguments to a method can be specified.
 * 
 * @author Michael Goderbauer
 */
public class MockMethodsWithArgumentsGwtTest extends GWTTestCase {

  private InterfaceToMock mock;
  private MyIMockControl ctrl;
  
  interface InterfaceToMock {
    void subtract(int a, int b);
    void add(int...sumands);
    void doSomething(String a, int b);
  }
  
  interface MyIMockControl extends MocksControl {
    InterfaceToMock getMock();
  }
  
  @Override
  public void gwtSetUp() {
    this.ctrl = GWT.create(MyIMockControl.class);
    this.mock = ctrl.getMock();
  }
  
  public void testAllExpectationMet() {
    mock.subtract(3, 4);
    mock.add(1, 2, 3, 4);
    mock.doSomething("Hallo", 10);

    ctrl.replay();
    
    mock.subtract(3, 4);
    mock.add(1, 2, 3, 4);
    mock.doSomething("Hallo", 10);
    
    ctrl.verify();
  }
  
  public void testOneExpectedMethodCallMissing() {
    mock.subtract(3, 4);
    mock.add(1, 2, 3, 4);
    mock.doSomething("Hallo", 10);

    ctrl.replay();
    
    mock.subtract(3, 4);
    mock.add(1, 2, 3, 4);
    // we do not call mock.doSomething("Hallo", 10);
    
    boolean exceptionThrown = true;
    try {
      ctrl.verify();
      exceptionThrown = false;
    } catch (AssertionError expected) {
      assertEquals("\n  Expectation failure on verify. List of all expectations:" +
          "\n        subtract(3, 4): expected 1, actual 1" +
          "\n        add(1, 2, 3, 4): expected 1, actual 1" +
          "\n    --> doSomething(Hallo, 10): expected 1, actual 0\n", expected.getMessage());
    }
    assertTrue("should have thrown exception", exceptionThrown);
  }
  
  public void testUnexpectedMethodCall() {
    mock.subtract(3, 4);
    mock.add(1, 2, 3, 4);
    mock.doSomething("Hallo", 10);

    ctrl.replay();
    
    mock.subtract(3, 4);
    mock.add(1, 2, 3, 4);
    mock.doSomething("Hallo", 10);
    
    boolean exceptionThrown = true;
    try {
      mock.add(1, 2, 3, 4); // unexpected!
      exceptionThrown = false;
    } catch (AssertionError expected) {
      assertEquals("\n  Unexpected method call add(1, 2, 3, 4). List of all expectations:" +
          "\n  Potential matches are marked with (+1)." +
          "\n        subtract(3, 4): expected 1, actual 1" +
          "\n        add(1, 2, 3, 4): expected 1, actual 1 (+1)" + 
          "\n        doSomething(Hallo, 10): expected 1, actual 1\n", expected.getMessage());
    } 
    assertTrue("should have thrown exception", exceptionThrown);
    
    ctrl.verify();
  }
  
  @Override
  public String getModuleName() {
    return "com.google.gwt.testing.easygwtmock.easygwtmock";
  }
}
