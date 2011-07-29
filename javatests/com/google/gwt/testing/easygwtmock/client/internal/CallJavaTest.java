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

package com.google.gwt.testing.easygwtmock.client.internal;

import junit.framework.TestCase;

import java.util.List;

/**
 * Tests the Call class
 * 
 * @author Michael Goderbauer
 */
public class CallJavaTest extends TestCase {

  private Object mock;
  private Method method;

  @Override
  public void setUp(){
    this.mock = new Object();
    Class<?>[] empty = {};
    this.method = new Method("foo", int.class, empty, empty);
  }
  
  public void testAddVarArgument() {
    Call call = new Call(mock, method, 1, 2, "hallo");
    
    Object[] varArgs = { "arg1", "arg2", "arg3" };
    call.addVarArgument(varArgs);
    
    List<Object> args = call.getArguments();
    assertEquals(args.get(0), 1);
    assertEquals(args.get(1), 2);
    assertEquals(args.get(2), "hallo");
    assertEquals(args.get(3), "arg1");
    assertEquals(args.get(4), "arg2");
    assertEquals(args.get(5), "arg3");
  }
  
  public void testToString() {
    Call call = new Call(mock, method, 1, 2, "hallo");
    assertEquals("foo(1, 2, hallo)", call.toString());
    
    call = new Call(mock, method);
    assertEquals("foo()", call.toString());
    
    call = new Call(mock, method, 1, "hallo");
    
    int[] varArgs = { 42, 43, 44 };
    call.addVarArgument(varArgs);
    
    assertEquals("foo(1, hallo, 42, 43, 44)", call.toString());
  }
  
  public void testGetDefaultReturnValue() {
    Call call = new Call(mock, method, 1, 2, "hallo");
    assertEquals(method.getDefaultReturnValue(), call.getDefaultReturnValue());
  }
  
}
