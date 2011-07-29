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

import java.util.ArrayList;
import java.util.List;

/**
 * Tests Answer in combination with andAnswer, andThrow, andReturn.
 * 
 * @author Michael Goderbauer
 */
public class AnswerGwtTest extends GWTTestCase {
  
  interface MyControl extends MocksControl {
    ToMock getMock();
  }
  
  interface ToMock {
    int throwsSomething(int i, String s) throws MyException;
    int callTheCallback(Callback callback);
    <T> List<T> genericMethod(T obj);
  }

  private MyControl ctrl;
  private ToMock mock; 
  
  @Override
  public void gwtSetUp() {
    this.ctrl = GWT.create(MyControl.class);
    this.mock = ctrl.getMock();
  }
  
  public void testThrowCheckedException() throws MyException {
    MyException exception = new MyException();
    ctrl.expect(mock.throwsSomething(1, "Hello")).andThrow(exception);
    
    ctrl.replay();
    
    try {
      mock.throwsSomething(1, "Hello");
      fail("should have thrown exception");
    } catch (MyException expected) {
      assertSame(exception, expected);
    }
    
    ctrl.verify();
  }
  
  public void testThrowUncheckedException() throws MyException {
    RuntimeException exception = new RuntimeException();
    ctrl.expect(mock.throwsSomething(1, "Hello")).andThrow(exception);
    
    ctrl.replay();
    
    try {
      mock.throwsSomething(1, "Hello");
      fail("should have thrown exception");
    } catch (RuntimeException expected) {
      assertSame(exception, expected);
    }
    
    ctrl.verify();
  }
  
  public void testThrowUndeclaredCheckedException() throws MyException {
    MyUndeclaredException exception = new MyUndeclaredException();
    
    try {
      ctrl.expect(mock.throwsSomething(1, "Hello")).andThrow(exception);
      fail("should have thrown exception");
    } catch (IllegalStateException expected) { 
      assertEquals(
          "AnswerGwtTest.MyUndeclaredException is not declared by throwsSomething(int, String)",
          expected.getMessage());
    }
  }
  
  public void testAccessMethodArgs() throws MyException {
    
    MyIAnswer answer = new MyIAnswer();
    
    ctrl.expect(mock.throwsSomething(10, "Hello")).andAnswer(answer);
    
    ctrl.replay();
    
    assertEquals(0, answer.counter);
    assertEquals(110, mock.throwsSomething(10, "Hello"));
    assertEquals(1, answer.counter);
    
    ctrl.verify();
  }
  
  public void testCallback() {
    final Callback callback = new Callback();
    ctrl.expect(mock.callTheCallback(callback)).andAnswer(new Answer<Integer>() {
      @Override
      public Integer answer(Object[] args) throws Throwable {
        assertEquals(1, args.length);
        assertEquals(callback, args[0]);
        ((Callback) args[0]).doIt();
        return 42;
      }
    });
    
    ctrl.replay();
    
    assertEquals(0, callback.callCounter);
    assertEquals(42, mock.callTheCallback(callback));
    assertEquals(1, callback.callCounter);
    
    ctrl.verify();
  } 
  
  public void testGenericsWithAnswer() {
    final ArrayList<Integer> result = new ArrayList<Integer>();
    result.add(4);
    result.add(6);
    
    ctrl.expect(mock.genericMethod(1)).andAnswer(new Answer<List<Integer>>() {
      @Override
      public List<Integer> answer(Object[] args) throws Throwable {
        return result;
      }
    });
    
    ctrl.replay();
    
    assertSame(result, mock.genericMethod(1));
    
    ctrl.verify();
  }
  
  public void testThrowUndeclaredExceptionWithAnswer() throws MyException { 
    final Throwable exception = new MyUndeclaredException();
    
    ctrl.expect(mock.throwsSomething(1, "Hallo")).andAnswer(new Answer<Integer>() {
      @Override
      public Integer answer(Object[] args) throws Throwable {
        throw exception;
      }
    });
    
    ctrl.replay();
    try {
      mock.throwsSomething(1, "Hallo");
      fail("should have thrown exception");
    } catch (UndeclaredThrowableException expected) {
      assertSame(exception, expected.getUndeclaredThrowable());
    }
    
    ctrl.verify();
  }
  
  @Override
  public String getModuleName() {
    return "com.google.gwt.testing.easygwtmock.easygwtmock";
  }

  class MyIAnswer implements Answer<Integer> {
    
    int counter = 0;
    
    @Override
    public Integer answer(Object[] args) throws Throwable {
      counter++;
      assertEquals(2, args.length);
      assertEquals(10, args[0]);
      assertEquals("Hello", args[1]);
      return (Integer) args[0] + 100;
    }
  }
  
  class MyException extends Exception {

  }

  class MyUndeclaredException extends Exception {

  }
  
  class Callback {
    int callCounter = 0;
    
    void doIt() {
      callCounter++;
    }
  }
}
