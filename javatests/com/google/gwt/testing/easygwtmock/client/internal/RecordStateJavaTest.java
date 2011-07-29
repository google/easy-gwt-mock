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

import com.google.gwt.testing.easygwtmock.client.ArgumentMatcher;
import com.google.gwt.testing.easygwtmock.client.internal.matchers.Any;
import com.google.gwt.testing.easygwtmock.client.internal.matchers.ArgumentCapture;
import com.google.gwt.testing.easygwtmock.client.internal.matchers.Equals;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import java.util.ArrayList;

/**
 * Tests the RecordState class
 * 
 * @author Michael Goderbauer
 */
public class RecordStateJavaTest extends TestCase {
  
  private MocksBehavior behavior;
  private RecordState recordState;

  @Override
  public void setUp(){
    behavior = EasyMock.createMock(MocksBehavior.class);
    recordState = new RecordState(behavior);
  }
  
  public void testVerify() {
    EasyMock.replay(behavior);
    try {
      recordState.verify();
    } catch (IllegalStateExceptionWrapper expected) {
      assertEquals("Calling verify is not allowed in record state",
          expected.getIllegalStateException().getMessage());
    }
    EasyMock.verify(behavior);
  }
  
  public void testReportMatcher() {
    EasyMock.replay(behavior);
    
    recordState.reportMatcher(Any.ANY);
    assertEquals(1, recordState.argumentMatchers.size());
    assertTrue(recordState.argumentMatchers.contains(Any.ANY));
    
    
    ArgumentMatcher matcher = new Equals(3);
    recordState.reportMatcher(matcher);
    assertEquals(2, recordState.argumentMatchers.size());
    assertTrue(recordState.argumentMatchers.contains(Any.ANY));
    assertTrue(recordState.argumentMatchers.contains(matcher));
    
    EasyMock.verify(behavior);
  }
  
  public void testReportCapture() throws IllegalStateExceptionWrapper {
    EasyMock.replay(behavior);
    
    ArgumentCapture capture1 = EasyMock.createMock(ArgumentCapture.class);
    ArgumentCapture capture2 = EasyMock.createMock(ArgumentCapture.class);
    EasyMock.replay(capture1);
    EasyMock.replay(capture2);
    
    recordState.reportCapture(capture1);
    assertEquals(1, recordState.argumentCaptures.size());
    assertTrue(recordState.argumentCaptures.contains(capture1));
    
    recordState.reportCapture(capture2);
    assertEquals(2, recordState.argumentCaptures.size());
    assertTrue(recordState.argumentCaptures.contains(capture1));
    assertTrue(recordState.argumentCaptures.contains(capture2));
    
    EasyMock.verify(behavior);
    EasyMock.verify(capture1);
    EasyMock.verify(capture2);
  }
  
  public void testReportCapture_SameTwice() throws IllegalStateExceptionWrapper {
    EasyMock.replay(behavior);
    
    ArgumentCapture capture = EasyMock.createMock(ArgumentCapture.class);
    EasyMock.replay(capture);
    
    recordState.reportCapture(capture);
    
    try {
      recordState.reportCapture(capture);
    } catch (IllegalStateExceptionWrapper expected) {
    }
    
    EasyMock.verify(behavior);
    EasyMock.verify(capture);
  }
  
  public void testCheckCanSwitchToReplay_noCurrentSetter() {
    EasyMock.replay(behavior);
    
    assertNull(recordState.currentSetter);
    recordState.checkCanSwitchToReplay();
    assertNull(recordState.currentSetter);
    
    EasyMock.verify(behavior);
  }
  
  public void testCheckCanSwitchToReplay_withCurrentSetter() {
    EasyMock.replay(behavior);
    
    ExpectationSettersImpl setter = EasyMock.createMock(ExpectationSettersImpl.class);
    setter.retire();
    EasyMock.replay(setter);
    
    recordState.currentSetter = setter;
    recordState.checkCanSwitchToReplay();
    assertNull(recordState.currentSetter);
    
    EasyMock.verify(setter);
    EasyMock.verify(behavior);
  }
  
  public void testGetExpectationSetter_noSetter() {
    EasyMock.replay(behavior);
    assertNull(recordState.currentSetter);
    
    try {
      recordState.getExpectationSetter();
    } catch (IllegalStateExceptionWrapper expected) {
    }

    EasyMock.verify(behavior);
  }
  
  public void testGetExpectationSetter_withSetter() throws IllegalStateExceptionWrapper {
    EasyMock.replay(behavior);
    
    ExpectationSettersImpl setter = EasyMock.createMock(ExpectationSettersImpl.class);
    EasyMock.replay(setter);
    
    recordState.currentSetter = setter;
    assertSame(setter, recordState.getExpectationSetter());
    
    EasyMock.verify(setter);
    EasyMock.verify(behavior);
  }
  
  public void testInvoke() throws Throwable {
    EasyMock.replay(behavior);
    Call call = EasyMock.createMock(Call.class);
    EasyMock.expect(call.getDefaultReturnValue()).andReturn(0);
    EasyMock.expect(call.getArguments()).andReturn(new ArrayList<Object>());
    EasyMock.replay(call);
    
    ExpectationSettersImpl setter = recordState.currentSetter;
    
    assertEquals(0, recordState.invoke(call).answer(null));
    assertNull(recordState.argumentCaptures);
    assertNull(recordState.argumentMatchers);
    assertNotNull(recordState.currentSetter);
    assertNotSame(setter, recordState.currentSetter);
    
    EasyMock.verify(behavior);
    EasyMock.verify(call);
  }
  
  public void testInvoke_retirePrevious() {
    EasyMock.replay(behavior);
    Call call = EasyMock.createMock(Call.class);
    EasyMock.expect(call.getDefaultReturnValue()).andReturn(0);
    EasyMock.expect(call.getArguments()).andReturn(new ArrayList<Object>());
    EasyMock.replay(call);
    
    ExpectationSettersImpl setter = EasyMock.createMock(ExpectationSettersImpl.class);
    setter.retire();
    EasyMock.replay(setter);
    
    recordState.currentSetter = setter;
    
    recordState.invoke(call);
    assertNotSame(setter, recordState.currentSetter);
    
    EasyMock.verify(behavior);
    EasyMock.verify(call);
    EasyMock.verify(setter);
  }
}
