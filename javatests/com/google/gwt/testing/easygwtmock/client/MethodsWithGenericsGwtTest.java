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
 * Tests mocking generic methods.
 * 
 * @author Michael Goderbauer
 */
public class MethodsWithGenericsGwtTest extends GWTTestCase {

  interface MyControl extends MocksControl {
    ToMock getMock();
  }
  
  interface ToMock {
    <T extends Number> List<T> doSomething(T a, List<T> b);
    <T extends String> T genericMethod(T a);
    List<?> wildcard(List<?> a);
  }

  private MyControl ctrl;
  private ToMock mock;
  private ArrayList<Integer> integerList;
  private ArrayList<String> stringList; 
  
  @Override
  public void gwtSetUp() {
    this.ctrl = GWT.create(MyControl.class);
    this.mock = ctrl.getMock();
    this.integerList = new ArrayList<Integer>();
    this.stringList = new ArrayList<String>();
  }
  
  public void testAllExpectationMet() {
    ctrl.expect(mock.doSomething(1, this.integerList)).andReturn(this.integerList);
    ctrl.expect(mock.genericMethod("hallo")).andReturn("hi");
    ctrl.<List<?>>expect(mock.wildcard(this.integerList)).andReturn(this.stringList);

    ctrl.replay();
    
    assertSame(mock.doSomething(1, this.integerList), this.integerList);
    assertEquals(mock.genericMethod("hallo"), "hi");
    assertSame(mock.wildcard(this.integerList), this.stringList);
    
    ctrl.verify();
  }
  
  @Override
  public String getModuleName() {
    return "com.google.gwt.testing.easygwtmock.easygwtmock";
  }

}
