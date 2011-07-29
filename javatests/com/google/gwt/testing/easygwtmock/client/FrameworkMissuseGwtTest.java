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

/**
 * Tests various framework misuse cases.
 * 
 * @author Michael Goderbauer
 */
public class FrameworkMissuseGwtTest extends GWTTestCase {

  interface InterfaceToMock {
    void doSomething(int e);
  }
  
  interface MyIMocksControl extends MocksControl {
    InterfaceToMock getMock();
  }

  private MyIMocksControl ctrl;
  private InterfaceToMock mock;
  
  @Override
  public void gwtSetUp() {
    this.ctrl = GWT.create(MyIMocksControl.class);
    this.mock = ctrl.getMock();
  }
  
  public void testVerifyBeforeReplay() {
    try {
      ctrl.verify();
      fail();
    } catch (IllegalStateException expected) {   
    }
  }
  
  public void testReplayAfterReplay() { 
    ctrl.replay();
    try {
      ctrl.replay();
      fail();
    } catch (IllegalStateException expected) {   
    }
  }
  
  public void testReplayVerifyAfterReset() { 
    
    mock.doSomething(1);
    ctrl.replay();
    mock.doSomething(1);
    ctrl.verify();
    
    ctrl.reset();
    
    mock.doSomething(2);
    ctrl.replay();
    mock.doSomething(2);
    ctrl.verify();
  }
  
  @Override
  public String getModuleName() {
    return "com.google.gwt.testing.easygwtmock.easygwtmock";
  }
}
