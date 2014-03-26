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
 * Tests generating mocks of generic interfaces
 * 
 * @author Michael Goderbauer
 */
public class GenericInterfacesGwtTest extends BaseGwtTestCase {

  interface MyControl extends MocksControl {
    ToMock<String> getStringMock();
    ToMock<Integer> getIntegerMock();
    ToMock<String> getAnotherStringMock();
  }
  
  interface ToMock <T> {
    T doSomething(T t);
  }

  private MyControl ctrl;
  
  @Override
  public void gwtSetUp() {
    this.ctrl = GWT.create(MyControl.class);
  }
  
  public void testStringMock() {
    ToMock<String> stringMock = ctrl.getStringMock();
    ctrl.expect(stringMock.doSomething("Hi")).andReturn("Hello");
    ctrl.replay();
    assertEquals("Hello", stringMock.doSomething("Hi"));
    ctrl.verify();
  }
  
  public void testIntegerMock() {
    ToMock<Integer> integerMock = ctrl.getIntegerMock();
    ctrl.expect(integerMock.doSomething(1)).andReturn(42);
    ctrl.replay();
    assertEquals(42, (int) integerMock.doSomething(1));
    ctrl.verify();
  }
  
  public void testBothMocks() {
    ToMock<String> stringMock = ctrl.getStringMock();
    ToMock<Integer> integerMock = ctrl.getIntegerMock();
    
    ctrl.expect(stringMock.doSomething("Hi")).andReturn("Hello");
    ctrl.expect(integerMock.doSomething(1)).andReturn(42);
    
    ctrl.replay();
    
    assertEquals("Hello", stringMock.doSomething("Hi"));
    assertEquals(42, (int) integerMock.doSomething(1));
    ctrl.verify();
  }
  
  public void testMocksAreSameClass() {
    ToMock<String> stringMock = ctrl.getStringMock();
    ToMock<String> anotherStringMock = ctrl.getAnotherStringMock();
    assertSame(stringMock.getClass(), anotherStringMock.getClass());
  }
}
