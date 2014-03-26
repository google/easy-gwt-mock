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
import com.google.gwt.testing.easygwtmock.client.dummyclasses.ClassToMock;
import com.google.gwt.testing.easygwtmock.client.dummyclasses.OneArgClassToMock;

/**
 * Can we mock classes?
 * 
 * @author Michael Goderbauer
 */
public class ClassMockingGwtTest extends BaseGwtTestCase {

  interface MyControl extends MocksControl {
    ClassToMock getMock();
    OneArgClassToMock getOneArgMock(String arg);
    ZeroArgSubClass getSubClassMock();
  }

  public static class ZeroArgSubClass extends OneArgClassToMock {
    public ZeroArgSubClass() {
      super("fixed");
    }
  }

  private MyControl ctrl;
  private ClassToMock mock; 
  
  @Override
  public void gwtSetUp() {
    this.ctrl = GWT.create(MyControl.class);
    this.mock = ctrl.getMock();
  }
  
  public void testClassMocking() {
    ctrl.expect(mock.returnString()).andReturn("Hallo");
    ctrl.expect(mock.returnInt()).andReturn(42);
    mock.noReturnValue();
    
    ctrl.replay();
    
    assertEquals("Hallo", mock.returnString());
    assertEquals(42, mock.returnInt());
    mock.noReturnValue();
    
    ctrl.verify();
  }

  public void testConstructMockWithArgument() throws Exception {
    OneArgClassToMock mock = ctrl.getOneArgMock("hello");
    assertEquals("hello", mock.arg);
  }

  public void testConstructMockWithFixedArgument() throws Exception {
    ZeroArgSubClass mock = ctrl.getSubClassMock();
    assertEquals("fixed", mock.arg);
  }

  public void testPartialMock() throws Exception {
    OneArgClassToMock mock = ctrl.getOneArgMock("earthling");

    ctrl.expect(mock.getGreeting()).andReturn("Greetings,");
    ctrl.replay();

    assertEquals("Greetings, earthling!", mock.makeMessage());

    ctrl.verify();
  }

  public void testFinalMethod() {
    assertEquals("I am final", mock.finalMethod());
  }
  
  public void testToString() {
    assertEquals("Mock for ClassToMock", mock.toString());
  }
  
  public void testFinalEquals() {
    assertTrue("should be true", mock.equals(null));
  }
}
