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
 * We test the generation of mock classes for interfaces with vararg methods.
 * 
 * @author Michael Goderbauer
 */
public class MockMethodsWithVarArgsGwtTest extends BaseGwtTestCase {

  interface InterfaceToMock {
    void foo1(int i, Object...a);
    void foo2(float i, byte...a);
    void foo3(String i, short...a);
    void foo4(Object i, int...a);
    void foo5(boolean i, long...a);
    void foo6(char i, float...a);
    void foo7(long i, double...a);
    void foo8(boolean...a);
    void foo9(char...a);
  }
  
  interface MyIMockControl extends MocksControl {
    InterfaceToMock getMock();
  }
  
  public void testVarArgs() {
    MyIMockControl ctrl = GWT.create(MyIMockControl.class);
    InterfaceToMock mock = ctrl.getMock();
    
    mock.foo1(2, 3, 4);
    mock.foo2(2f, (byte) 4, (byte) 3);
    mock.foo3("Hi", (short) 3, (short) 4);
    mock.foo4(null, 3, 4);
    mock.foo5(true, 3L, 4L);
    mock.foo6('A', 3f, 4f);
    mock.foo7(12L, 3d, 4d);
    mock.foo8(true, false, true, true);
    mock.foo9('A', 'B', 'C');
    
    ctrl.replay();
    
    mock.foo1(2, 3, 4);
    mock.foo2(2f, (byte) 4, (byte) 3);
    mock.foo3("Hi", (short) 3, (short) 4);
    mock.foo4(null, 3, 4);
    mock.foo5(true, 3L, 4L);
    mock.foo6('A', 3f, 4f);
    mock.foo7(12L, 3d, 4d);
    mock.foo8(true, false, true, true);
    mock.foo9('A', 'B', 'C');
    
    ctrl.verify();
  }
}
