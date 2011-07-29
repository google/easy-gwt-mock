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
import com.google.gwt.testing.easygwtmock.client.internal.matchers.Any;

import junit.framework.TestCase;

import org.easymock.EasyMock;

/**
 * Tests the ReplayState class
 * 
 * @author Michael Goderbauer
 */
public class ReplayStateJavaTest extends TestCase {
  
  private MocksBehavior behavior;
  private ReplayState replayState;

  @Override
  public void setUp(){
    behavior = EasyMock.createMock(MocksBehavior.class);
    replayState = new ReplayState(behavior);
  }
  
  public void testGetExpectationSetter() {
    EasyMock.replay(behavior);
    try {
      replayState.getExpectationSetter();
    } catch (IllegalStateExceptionWrapper expected) {
      assertEquals("Cannot set expectations while in replay state",
          expected.getIllegalStateException().getMessage());
    }
    EasyMock.verify(behavior);
  }
  
  public void testCheckCanSwitchToReplay() {
    EasyMock.replay(behavior);
    try {
      replayState.checkCanSwitchToReplay();
    } catch (IllegalStateExceptionWrapper expected) {
      assertEquals("Cannot switch to replay mode while in replay state",
          expected.getIllegalStateException().getMessage());
    }
    EasyMock.verify(behavior);
  }
  
  public void testVerify() throws AssertionErrorWrapper {
    behavior.verify();
    EasyMock.replay(behavior);
    
    replayState.verify();
    
    EasyMock.verify(behavior);
  }
  
  public void testInvoke() throws AssertionErrorWrapper {
    Call call = EasyMock.createMock(Call.class);
    EasyMock.replay(call);
    
    @SuppressWarnings("unchecked")
    Answer<Object> answer = EasyMock.createMock(Answer.class);
    EasyMock.replay(answer);
    
    EasyMock.<Answer<? extends Object>>expect(behavior.addActual(call)).andReturn(answer);
    EasyMock.replay(behavior);
    
    assertSame(answer, replayState.invoke(call));
    
    EasyMock.verify(behavior);
    EasyMock.verify(call);
    EasyMock.verify(answer);
  }
  
  public void testReportMatcher() {
    EasyMock.replay(behavior);
    try {
      replayState.reportMatcher(Any.ANY);
    } catch (IllegalStateExceptionWrapper expected) {
      assertEquals("Argument matchers must not be used in replay state",
          expected.getIllegalStateException().getMessage());
    }
    EasyMock.verify(behavior);
  }
  
  public void testReportCapture() {
    EasyMock.replay(behavior);
    try {
      replayState.reportCapture(null);
    } catch (IllegalStateExceptionWrapper expected) {
      assertEquals("Captures must not be used in replay state",
          expected.getIllegalStateException().getMessage());
    }
    EasyMock.verify(behavior);
  }
}
