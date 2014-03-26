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
 * Tests the interaction between argument matchers and framework.
 * 
 * @author Michael Goderbauer
 */
public class MatcherGwtTest extends BaseGwtTestCase {

  private InterfaceToMock mock;
  private MyIMockControl ctrl;
  
  interface MyIMockControl extends MocksControl {
    InterfaceToMock getMock();
  }
  
  interface InterfaceToMock {
    void subtract(int a, int b);
    void add(int...sumands);
    void doSomething(String a, float b);
  }
  
  @Override
  public void gwtSetUp() {
    this.ctrl = GWT.create(MyIMockControl.class);
    this.mock = ctrl.getMock();
  }
  
  public void testAllExpectationMet() {
    mock.subtract(ctrl.anyInt(), ctrl.anyInt());
    mock.add(ctrl.anyInt(), ctrl.anyInt());
    mock.doSomething(ctrl.<String>anyObject(), ctrl.eq(0f));
    
    ctrl.replay();
    
    mock.add(3, 5);
    mock.subtract(4, 5);
    mock.doSomething("Hi", 0);
    
    ctrl.verify();
  }
  
  public void testMatcherNotSatisfied() {
    mock.add(ctrl.eq(4), ctrl.eq(5));
    
    ctrl.replay();
    
    boolean exceptionThrown = true;
    try {
      mock.add(3, 5);
      exceptionThrown = false;
    } catch (AssertionError expected) {
      assertEquals("\n  Unexpected method call add(3, 5). List of all expectations:" +
          "\n  Potential matches are marked with (+1)." +
          "\n    --> add(4, 5): expected 1, actual 0\n", expected.getMessage());
    } 
    assertTrue("should have thrown exception", exceptionThrown);
  }
  
  public void testMatcherMismatch() {
    try {
      mock.subtract(ctrl.anyInt(), 0); // should be ..., ctrl.eq(0));
      fail();
    } catch (IllegalStateException expected) {
      expected.getMessage().startsWith("2 matchers expected, 1 recorded.");
    }
  }
  
  public void testCustomMatcherMatches() {
    mock.subtract(ctrl.matchesInt(new IsOdd()), ctrl.eq(3));
    
    ctrl.replay();
    
    mock.subtract(5, 3);
  }
  
  public void testCustomMatcherDoesNotMatch() {
    mock.subtract(ctrl.matchesInt(new IsOdd()), ctrl.eq(3));
    
    ctrl.replay();
    
    boolean exceptionThrown = true;
    try {
      mock.subtract(4, 3);
      exceptionThrown = false;
    } catch (AssertionError expected) {
      assertTrue(expected.getMessage().contains("subtract(isOdd(), 3)"));
    }
    assertTrue("should have thrown exception", exceptionThrown);
  }

  class IsOdd implements ArgumentMatcher {

    @Override
    public boolean matches(Object argument) {
      if (!(argument instanceof Integer)) {
        return false;
      }
      return ((Integer) argument) % 2 == 1;
    }

    @Override
    public void appendTo(StringBuffer buffer) {
      buffer.append("isOdd()");
    }
    
  }
}
