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

import java.util.HashMap;
import java.util.Map;

/**
 * Tests the method class
 * 
 * @author Michael Goderbauer
 */
public class MethodJavaTest extends TestCase {
  
  private Map<Class<?>, Method> methods;

  @Override
  public void setUp() {
    methods = new HashMap<Class<?>, Method>();
    methods.put(byte.class, new Method("foo", byte.class, null, null));
    methods.put(short.class, new Method("foo", short.class, null, null));
    methods.put(int.class, new Method("foo", int.class, null, null));
    methods.put(long.class, new Method("foo", long.class, null, null));
    methods.put(float.class, new Method("foo", float.class, null, null));
    methods.put(double.class, new Method("foo", double.class, null, null));
    methods.put(boolean.class, new Method("foo", boolean.class, null, null));
    methods.put(char.class, new Method("foo", char.class, null, null));
    methods.put(Object.class, new Method("foo", Object.class, null, null));
    methods.put(String.class, new Method("foo", String.class, null, null));
    methods.put(void.class, new Method("foo", void.class, null, null));
  }
  
  public void testDefaultReturnValue() {
    assertEquals(methods.get(byte.class).getDefaultReturnValue(), (byte) 0);
    assertEquals(methods.get(short.class).getDefaultReturnValue(), (short) 0);
    assertEquals(methods.get(int.class).getDefaultReturnValue(), 0);
    assertEquals(methods.get(long.class).getDefaultReturnValue(), 0L);
    assertEquals(methods.get(float.class).getDefaultReturnValue(), 0f);
    assertEquals(methods.get(double.class).getDefaultReturnValue(), 0d);
    assertEquals(methods.get(boolean.class).getDefaultReturnValue(), false);
    assertEquals(methods.get(char.class).getDefaultReturnValue(), (char) 0);
    assertNull("should be null", methods.get(Object.class).getDefaultReturnValue());
    assertNull("should be null", methods.get(String.class).getDefaultReturnValue());
  }
  
  public void testIsPrimitive() {
    assertTrue("should be primitive", methods.get(byte.class).isReturnValuePrimitive());
    assertTrue("should be primitive", methods.get(short.class).isReturnValuePrimitive());
    assertTrue("should be primitive", methods.get(int.class).isReturnValuePrimitive());
    assertTrue("should be primitive", methods.get(long.class).isReturnValuePrimitive());
    assertTrue("should be primitive", methods.get(float.class).isReturnValuePrimitive());
    assertTrue("should be primitive", methods.get(double.class).isReturnValuePrimitive());
    assertTrue("should be primitive", methods.get(boolean.class).isReturnValuePrimitive());
    assertTrue("should be primitive", methods.get(char.class).isReturnValuePrimitive());
    assertFalse("should not be primitive", methods.get(Object.class).isReturnValuePrimitive());
    assertFalse("should not be primitive", methods.get(String.class).isReturnValuePrimitive());
  }
  
  public void testIsVoid() {
    assertFalse("should not be void", methods.get(byte.class).isReturnValueVoid());
    assertFalse("should not be void", methods.get(short.class).isReturnValueVoid());
    assertFalse("should not be void", methods.get(int.class).isReturnValueVoid());
    assertFalse("should not be void", methods.get(long.class).isReturnValueVoid());
    assertFalse("should not be void", methods.get(float.class).isReturnValueVoid());
    assertFalse("should not be void", methods.get(double.class).isReturnValueVoid());
    assertFalse("should not be void", methods.get(boolean.class).isReturnValueVoid());
    assertFalse("should not be void", methods.get(char.class).isReturnValueVoid());
    assertFalse("should not be void", methods.get(Object.class).isReturnValueVoid());
    assertFalse("should not be void", methods.get(String.class).isReturnValueVoid());
    assertTrue("should be void", methods.get(void.class).isReturnValueVoid());
  }
  
  public void testCanThrow_true() {
    Class<?>[] throwable = { MyException.class };
    Method method = new Method("foo", int.class, null, throwable);
    
    assertTrue("should be throwable", method.canThrow(new MyException()));
    assertTrue("should be throwable", method.canThrow(new MyExtendedException()));
    assertTrue("should be throwable", method.canThrow(new RuntimeException()));
    assertTrue("should be throwable", method.canThrow(new Error()));
  }
  
  public void testCanThrow_false() {
    Class<?>[] throwable = { MyExtendedException.class };
    Method method = new Method("foo", int.class, null, throwable);
    
    assertFalse("should not be throwable", method.canThrow(new MyException()));
    assertFalse("should not be throwable", method.canThrow(new MyUndeclaredException()));
    
    assertTrue("should be throwable", method.canThrow(new MyExtendedException()));
    assertTrue("should be throwable", method.canThrow(new RuntimeException()));
    assertTrue("should be throwable", method.canThrow(new Error()));
  }
  
  public void testToString() {
    Class<?>[] args = { MyException.class, int.class, String.class };
    Method method = new Method("foo", int.class, args, null);
    
    assertEquals("foo(MethodJavaTest.MyException, int, String)", method.toString());
  }
  
  class MyException extends Exception {
  }
  
  class MyExtendedException extends MyException {
  }
  
  class MyUndeclaredException extends Exception {
  }
}
