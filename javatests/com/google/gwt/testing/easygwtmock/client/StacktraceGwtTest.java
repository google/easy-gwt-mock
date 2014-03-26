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
 * Tests, if the stacktrace is cut properly to hide EasyGwtMock internals.
 * In other words: The first displayed stackframe should include a method
 * of the EasyGwtMock API.
 * 
 * @author Michael Goderbauer
 *
 */
public class StacktraceGwtTest extends BaseGwtTestCase {

  interface MyControl extends MocksControl {
    ComplexType getMock();
  }
  
  interface ComplexType {
    int foo();
  }

  private MyControl ctrl;
  private ComplexType mock; 
  
  @Override
  public void gwtSetUp() {
    this.ctrl = GWT.create(MyControl.class);
    this.mock = ctrl.getMock();
  }
  
  public void testUnexpectedMethod() {
    if (GWT.isScript()) {
      return; // no real stacktrace in javaScript!
    }
    ctrl.replay();
    
    try {
      mock.foo();
    } catch (AssertionError expected) {
      StackTraceElement stackFrame = expected.getStackTrace()[0];
      assertEquals(
          "com.google.gwt.testing.easygwtmock.client.StacktraceGwtTest_ComplexTypeMock.foo",
          stackFrame.getClassName() + "." + stackFrame.getMethodName());
    }
  }
  
  public void testVerify() {
    if (GWT.isScript()) {
       return; // no real stacktrace in javaScript!
    }
    ctrl.expect(mock.foo()).andReturn(32);
    
    ctrl.replay();
    
    try {
      ctrl.verify();
    } catch (AssertionError expected) {
      StackTraceElement stackFrame = expected.getStackTrace()[0];
      assertEquals(
          "com.google.gwt.testing.easygwtmock.client.internal.MocksControlBase.verify",
          stackFrame.getClassName() + "." + stackFrame.getMethodName());
    }
  }
  
  public void testReplay() {
    if (GWT.isScript()) {
      return; // no real stacktrace in javaScript!
    }
    ctrl.expect(mock.foo()).andReturn(32);
    
    ctrl.replay();
    
    try {
      ctrl.replay();
    } catch (IllegalStateException expected) {
      StackTraceElement stackFrame = expected.getStackTrace()[0];
      assertEquals(
          "com.google.gwt.testing.easygwtmock.client.internal.MocksControlBase.replay",
          stackFrame.getClassName() + "." + stackFrame.getMethodName());
    }
  }
}
