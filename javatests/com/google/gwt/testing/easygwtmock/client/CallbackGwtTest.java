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
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Tests callOnSuccess() and callOnFailure() of ExpectationSetter.
 * 
 * @author Michael Goderbauer
 */
public class CallbackGwtTest extends BaseGwtTestCase {
  interface MyControl extends MocksControl {
    ToMock getMock();
  }
  
  interface ToMock {
    void oneArgument(AsyncCallback<String> callback);
    void twoArguments(int i, AsyncCallback<Integer> callback);
    int nonVoid(String s);
    void noCallback(String s);
  }

  private MyControl ctrl;
  private ToMock mock; 
  
  @Override
  public void gwtSetUp() {
    this.ctrl = GWT.create(MyControl.class);
    this.mock = ctrl.getMock();
  }
  
  public void testOnSuccess_oneArgument() {
    MyCallback<String> callback = new MyCallback<String>();
    
    mock.oneArgument(ctrl.asyncCallback(String.class));
    ctrl.expectLastCall().andCallOnSuccess("Hallo");
    
    ctrl.replay();
    
    assertEquals(callback.onSuccessCalledCount, 0);
    mock.oneArgument(callback);
    assertEquals(callback.onSuccessCalledCount, 1);
    assertEquals("Hallo", callback.result);
    assertEquals(callback.onFailureCalledCount, 0);
  }
  
  public void testOnSuccess_twoArguments() {
    MyCallback<Integer> callback = new MyCallback<Integer>();
    
    mock.twoArguments(ctrl.eq(4), ctrl.asyncCallback(Integer.class));
    ctrl.expectLastCall().andCallOnSuccess(42);
    
    ctrl.replay();
    
    assertEquals(callback.onSuccessCalledCount, 0);
    mock.twoArguments(4, callback);
    assertEquals(callback.onSuccessCalledCount, 1);
    assertEquals(42, callback.result);
    assertEquals(callback.onFailureCalledCount, 0);
  }
  
  public void testOnFailure_oneArgument() {
    MyCallback<String> callback = new MyCallback<String>();
    Throwable throwable = new RuntimeException();
    
    mock.oneArgument(ctrl.asyncCallback(String.class));
    ctrl.expectLastCall().andCallOnFailure(throwable);
    
    ctrl.replay();
    
    assertEquals(callback.onFailureCalledCount, 0);
    mock.oneArgument(callback);
    assertEquals(callback.onFailureCalledCount, 1);
    assertEquals(throwable, callback.caught);
    assertEquals(callback.onSuccessCalledCount, 0);
  }
  
  public void testOnFailure_twoArgument() {
    MyCallback<Integer> callback = new MyCallback<Integer>();
    Throwable throwable = new RuntimeException();
    
    mock.twoArguments(ctrl.eq(4), ctrl.asyncCallback(Integer.class));
    ctrl.expectLastCall().andCallOnFailure(throwable);
    
    ctrl.replay();
    
    assertEquals(callback.onFailureCalledCount, 0);
    mock.twoArguments(4, callback);
    assertEquals(callback.onFailureCalledCount, 1);
    assertEquals(throwable, callback.caught);
    assertEquals(callback.onSuccessCalledCount, 0);
  }
  
  public void testOnSuccess_NonVoidMethod() {
    MyCallback<Integer> callback = new MyCallback<Integer>();
    
    try {
      ctrl.expect(mock.nonVoid("hi")).andCallOnSuccess(11);
      fail("should have thrown exception");
    } catch (IllegalStateException expected) {
    }
  }
  
  public void testOnFailure_NonVoidMethod() {
    MyCallback<Integer> callback = new MyCallback<Integer>();
    
    try {
      ctrl.expect(mock.nonVoid("hi")).andCallOnFailure(new Error());
      fail("should have thrown exception");
    } catch (IllegalStateException expected) {
    }
  }
  
  public void testOnSuccess_MethodWithoutCallbackArg() {
    MyCallback<Integer> callback = new MyCallback<Integer>();
    mock.noCallback("hi");
    
    try {
      ctrl.expectLastCall().andCallOnSuccess(11);
      fail("should have thrown exception");
    } catch (IllegalStateException expected) {
      assertEquals("andCallOnSuccess() can only be used with methods " +
          "that take an AsyncCallback as last argument", expected.getMessage());
    }
  }
  
  public void testOnFailure_MethodWithoutCallbackArg() {
    MyCallback<Integer> callback = new MyCallback<Integer>();
    
    mock.noCallback("hi");
    
    try {
      ctrl.expectLastCall().andCallOnFailure(null);
      fail("should have thrown exception");
    } catch (IllegalStateException expected) {
      assertEquals(
        "andCallOnFailure() can only be used with methods " +
        "that take an AsyncCallback as last argument", expected.getMessage());
    }
  }
  
  public void testOnSuccess_NoCallback() {
    mock.oneArgument(ctrl.<AsyncCallback<String>>anyObject());
    ctrl.expectLastCall().andCallOnSuccess("Hallo");
    
    ctrl.replay();
    
    try {
      mock.oneArgument(null);
      fail("should have thrown exception");
    } catch (IllegalArgumentException expected) {
    }
  }
  
  public void testOnFailure_NoCallback() {
    mock.oneArgument(ctrl.<AsyncCallback<String>>anyObject());
    ctrl.expectLastCall().andCallOnFailure(null);
    
    ctrl.replay();
    
    try {
      mock.oneArgument(null);
      fail("should have thrown exception");
    } catch (IllegalArgumentException expected) {
    }
  }
  
  class MyCallback <T> implements AsyncCallback<T> {

    int onFailureCalledCount = 0;
    int onSuccessCalledCount = 0;
    
    Object result;
    Throwable caught;
    
    @Override
    public void onFailure(Throwable caught) {
      this.onFailureCalledCount++;
      this.caught = caught;
    }

    @Override
    public void onSuccess(Object result) {
      this.onSuccessCalledCount++;
      this.result = result;
    }
  }
}
