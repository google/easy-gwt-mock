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
 * We test if we can mock an interface that extends another interface.
 * 
 * @author Michael Goderbauer
 */
public class MockExtendedInterfacesGwtTest extends GWTTestCase {

  interface BaseInterface {
    void baseDoSomething();
  }
  
  interface ChildInterface extends BaseInterface {
    void childDoSomething();
  }
  
  interface MyIMocksControl extends MocksControl {
    ChildInterface getMock();
  }
 
  public void testExtendedInterface() {
    MyIMocksControl ctrl = GWT.create(MyIMocksControl.class);
    ChildInterface mock = ctrl.getMock();
    
    mock.baseDoSomething();
    mock.childDoSomething();
    
    ctrl.replay();
    
    mock.baseDoSomething();
    mock.childDoSomething();
    
    ctrl.verify();
  }
  
  @Override
  public String getModuleName() {
    return "com.google.gwt.testing.easygwtmock.easygwtmock";
  }
}
