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

import com.google.gwt.testing.easygwtmock.client.Answer;
import com.google.gwt.testing.easygwtmock.client.ArgumentMatcher;
import com.google.gwt.testing.easygwtmock.client.Capture;
import com.google.gwt.testing.easygwtmock.client.internal.matchers.ArgumentCapture;
import com.google.gwt.testing.easygwtmock.client.internal.matchers.Equals;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test the ExpectedCall class
 * 
 * @author Michael Goderbauer
 */
public class ExpectedCallJavaTest extends TestCase { 

  private Object mock;
  private Method method;

  @Override
  public void setUp(){
    this.mock = new Object();
    Class<?>[] argumentTypes = { String.class, int.class };
    Class<?>[] declaredThrowables = {};
    this.method = new Method("foo", int.class, argumentTypes, declaredThrowables);
  }
  
  public void testMatches_equal() {
    Call call1 = new Call(this.mock, this.method, 1, "Hallo", 3);
    ExpectedCall exp = new ExpectedCall(call1, createMatchersFor(call1), null, null, Range.DEFAULT);
    Call call2 = new Call(this.mock, this.method, 1, "Hallo", new Integer(3));
    
    assertTrue("should match", exp.matches(call2));
    assertTrue("should match", exp.matches(call1));
  }
  
  public void testMatches_equalWithNull() {
    Call call1 = new Call(this.mock, this.method, 1, null, 3);
    ExpectedCall exp = new ExpectedCall(call1, createMatchersFor(call1), null, null, Range.DEFAULT);
    Call call2 = new Call(this.mock, this.method, 1, null, new Integer(3));
    
    assertTrue("should match", exp.matches(call2));
    assertTrue("should match", exp.matches(call1));
  }
  
  public void testMatches_NotEqualSameArgumentCount() {
    Call call1 = new Call(this.mock, this.method, 1, "Hallo", 3);
    ExpectedCall exp = new ExpectedCall(call1, createMatchersFor(call1), null, null, Range.DEFAULT);
    Call call2 = new Call(this.mock, this.method, 1, "Hi", new Integer(3));
    
    assertFalse("should not match", exp.matches(call2));
  }
  
  public void testMatches_NotEqualNotSameArgumentCount() {
    Call call1 = new Call(this.mock, this.method, 1, "Hallo", 3);
    ExpectedCall exp = new ExpectedCall(call1, createMatchersFor(call1), null, null, Range.DEFAULT);
    Call call2 = new Call(this.mock, this.method, 1, "Hi");
    
    assertFalse("should not match", exp.matches(call2));
  }
  
  public void testMatches_NotEqualWithNull1() {
    Call call1 = new Call(this.mock, this.method, 1, null, 3);
    ExpectedCall exp = new ExpectedCall(call1, createMatchersFor(call1), null, null, Range.DEFAULT);
    Call call2 = new Call(this.mock, this.method, 1, "Hi", new Integer(3));
    
    assertFalse("should not match", exp.matches(call2));
  }
  
  public void testMatches_NotEqualWithNull2() {
    Call call1 = new Call(this.mock, this.method, 1, "Hallo", 3);
    ExpectedCall exp = new ExpectedCall(call1, createMatchersFor(call1), null, null, Range.DEFAULT);
    Call call2 = new Call(this.mock, this.method, 1, null, new Integer(3));
    
    assertFalse("should not match", exp.matches(call2));
  }
  
  public void testToString_threeArgs() {
    Call call1 = new Call(this.mock, this.method, 1, "Hallo", 3);
    ExpectedCall exp = new ExpectedCall(call1, createMatchersFor(call1), null, null, Range.DEFAULT);
    
    assertEquals("foo(1, Hallo, 3)", exp.toString());
  }
  
  public void testToString_noArgs() {
    Call call1 = new Call(this.mock, this.method);
    ExpectedCall exp = new ExpectedCall(call1, createMatchersFor(call1), null, null, Range.DEFAULT);
    
    assertEquals("foo()", exp.toString());
  }
  
  public void testInvoke() {
    Call call1 = new Call(this.mock, this.method);
    Answer<Object> answer = AnswerFactory.forValue(42);
    
    ExpectedCall exp = new ExpectedCall(call1, createMatchersFor(call1),
                                        null, answer, Range.DEFAULT);
    
    assertFalse("expectations should not be met", exp.expectationMet());
    assertTrue("should be invokable", exp.canBeInvoked());
    assertEquals(0, exp.getCallCount());
    
    assertSame(answer, exp.invoke());
    
    assertTrue("expectations should be met", exp.expectationMet());
    assertFalse("should not be invokable", exp.canBeInvoked());
    assertEquals(1, exp.getCallCount());
  }
  
  public void testInvoke_withCapture() {
    Call call1 = new Call(this.mock, this.method);
    
    Capture<Integer> capture = new Capture<Integer>();
    Set<ArgumentCapture> argumentCaptures = new HashSet<ArgumentCapture>();
    argumentCaptures.add(new ArgumentCapture(capture));
    
    ExpectedCall exp = new ExpectedCall(call1, createMatchersFor(call1),
                                        argumentCaptures, null, Range.DEFAULT);
    
    assertFalse("should not have captured", capture.hasCaptured());
    
    exp.invoke();
    
    assertTrue("should have captured", capture.hasCaptured());
  }
  
  static List<ArgumentMatcher> createMatchersFor(Call call) {
    List<ArgumentMatcher> argMatcher = new ArrayList<ArgumentMatcher>();
    for (Object arg : call.getArguments()) {
      argMatcher.add(new Equals(arg));
    }
    return argMatcher;
  }
}
