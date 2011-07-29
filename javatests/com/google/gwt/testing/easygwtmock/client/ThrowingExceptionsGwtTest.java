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
 * Tests .andThrows() function of ExpectationSetters
 * 
 * @author Michael Goderbauer
 */
public class ThrowingExceptionsGwtTest extends GWTTestCase {

  interface MyControl extends MocksControl {
    ToMock getMock();
  }
  
  interface ToMock {
    int throw1() throws MyException, MyExtendedException;
    int throw2() throws MyExtendedException, MyException;
    int throw3() throws Throwable, RuntimeException, MyExtendedException;
    int throw4() throws MyException, MyException;
    int throw5() throws MyException;
  }

  private MyControl ctrl;
  private ToMock mock; 
  
  @Override
  public void gwtSetUp() {
    this.ctrl = GWT.create(MyControl.class);
    this.mock = ctrl.getMock();
  }
  
  public void testHiddenExceptions() throws MyExtendedException, MyException {
    ctrl.expect(mock.throw1()).andThrow(new MyException());
    ctrl.expect(mock.throw1()).andThrow(new MyExtendedException());
    ctrl.expect(mock.throw2()).andThrow(new MyException());
    ctrl.expect(mock.throw2()).andThrow(new MyExtendedException());
    
    ctrl.replay();
    
    try {
      mock.throw1();
      fail("should have thrown exception");
    } catch (MyException expected) {
    }
    try {
      mock.throw1();
      fail("should have thrown exception");
    } catch (MyExtendedException expected) {
    }
    try {
      mock.throw2();
      fail("should have thrown exception");
    } catch (MyException expected) {
    }
    try {
      mock.throw2();
      fail("should have thrown exception");
    } catch (MyExtendedException expected) {
    }
    
    ctrl.verify();
  }
  
  public void testDeclaredUncheckedExceptions() throws RuntimeException, Throwable {
    ctrl.expect(mock.throw3()).andThrow(new MyException());
    ctrl.expect(mock.throw3()).andThrow(new MyExtendedException());
    ctrl.expect(mock.throw3()).andThrow(new ArithmeticException());
    ctrl.expect(mock.throw3()).andThrow(new RuntimeException());
    
    ctrl.replay();
    
    try {
      mock.throw3();
      fail("should have thrown exception");
    } catch (MyException expected) {
    }
    try {
      mock.throw3();
      fail("should have thrown exception");
    } catch (MyExtendedException expected) {
    }
    try {
      mock.throw3();
      fail("should have thrown exception");
    } catch (ArithmeticException expected) {
    }
    try {
      mock.throw3();
      fail("should have thrown exception");
    } catch (RuntimeException expected) {
    }
    
    ctrl.verify();
  }
  
  public void testSameExceptionDeclaredTwice() throws MyException  {
    ctrl.expect(mock.throw4()).andThrow(new MyException());
    ctrl.expect(mock.throw4()).andThrow(new MyExtendedException());
    
    ctrl.replay();
    
    try {
      mock.throw4();
      fail("should have thrown exception");
    } catch (MyException expected) {
    }
    try {
      mock.throw4();
      fail("should have thrown exception");
    } catch (MyExtendedException expected) {
    }
    
    ctrl.verify();
  }
  
  public void testExceptionHierarchy() throws MyException  {
    ctrl.expect(mock.throw5()).andThrow(new MyExtendedException());
    
    ctrl.replay();
    
    try {
      mock.throw5();
      fail("should have thrown exception");
    } catch (MyException expected) {
    }
    
    ctrl.verify();
  }
  
  @Override
  public String getModuleName() {
    return "com.google.gwt.testing.easygwtmock.easygwtmock";
  }
  
  class MyException extends Exception {
  }
  
  class MyExtendedException extends MyException {
  }
}
