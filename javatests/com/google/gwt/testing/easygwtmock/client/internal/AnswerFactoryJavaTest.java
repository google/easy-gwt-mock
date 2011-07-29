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
import com.google.gwt.user.client.rpc.AsyncCallback;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * Tests {@link AnswerFactory}
 * 
 * @author Michael Goderbauer
 */
public class AnswerFactoryJavaTest extends TestCase {
  
  public void testForValue_null() throws Throwable {
    Answer<Object> answer = AnswerFactory.forValue(null);
    assertNull(answer.answer(null));
  }
  
  public void testForValue_primitive() throws Throwable {
    Answer<Object> answer = AnswerFactory.forValue(42);
    assertEquals(42, answer.answer(null));
  }
  
  public void testForValue_object() throws Throwable {
    Object obj = new Object();
    Answer<Object> answer = AnswerFactory.forValue(obj);
    assertSame(obj, answer.answer(null));
  }
  
  public void testForThrowable() throws Throwable {
    NullPointerException exception = new NullPointerException();
    Answer<Object> answer = AnswerFactory.forThrowable(exception);
    
    try {
      answer.answer(null);
      fail("should have thrown exception");
    } catch (NullPointerException expected) {
      assertSame(exception, expected);
    }
  }
  
  public void testForOnSuccess() throws Throwable {
    @SuppressWarnings("unchecked")
    AsyncCallback<Object> callback = EasyMock.createMock(AsyncCallback.class);
    callback.onSuccess(42);
    EasyMock.replay(callback);
 
    Answer<Object> answer = AnswerFactory.forOnSuccess(42);
    Object[] args = { 14, "Hallo", callback };
    
    answer.answer(args);
    
    EasyMock.verify(callback);
  }
  
  public void testForOnSuccess_noCallbackProvided() throws Throwable {
    Answer<Object> answer = AnswerFactory.forOnSuccess(42);
    Object[] args = { 14, "Hallo" };
    try {
      answer.answer(args);
      fail("should have thrown exception");
    } catch (IllegalArgumentException expected) {
      assertEquals(
          "No com.google.gwt.user.client.rpc.AsyncCallback object as last argument provided",
          expected.getMessage());
    }
  }
  
  public void testForOnFailure() throws Throwable {
    Throwable throwable = new Exception();
    @SuppressWarnings("unchecked")
    AsyncCallback<Object> callback = EasyMock.createMock(AsyncCallback.class);
    callback.onFailure(throwable);
    EasyMock.replay(callback);
 
    Answer<Object> answer = AnswerFactory.forOnFailure(throwable);
    Object[] args = { 14, "Hallo", callback };
    
    answer.answer(args);
    
    EasyMock.verify(callback);
  }
  
  public void testForOnFailure_noCallbackProvided() throws Throwable {
    Answer<Object> answer = AnswerFactory.forOnFailure(new Exception());
    Object[] args = { 14, "Hallo" };
    try {
      answer.answer(args);
      fail("should have thrown exception");
    } catch (IllegalArgumentException expected) {
      assertEquals(
          "No com.google.gwt.user.client.rpc.AsyncCallback object as last argument provided",
          expected.getMessage());
    }
  }
}
