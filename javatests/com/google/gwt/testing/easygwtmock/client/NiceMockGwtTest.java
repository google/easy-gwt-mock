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
 * Tests the behavior of nice mocks
 * 
 * @author Michael Goderbauer
 */
public class NiceMockGwtTest extends GWTTestCase {

  interface ToMock {
    String returnString();
    int returnInt();
    boolean returnBoolean();
    float returnFloat();
  }
  
  interface MyControl extends MocksControl {
    ToMock getMock();
  }

  public void testNiceMocks() {
    MyControl ctrl = GWT.create(MyControl.class);
    ToMock mock = ctrl.getMock();
    ctrl.replay();
    
    ctrl.setToNice(mock);
    
    assertNull(mock.returnString());
    assertEquals(0, mock.returnInt());
    assertEquals(false, mock.returnBoolean());
    assertEquals(0f, mock.returnFloat());
    
    ctrl.verify();
  }
  
  public void testNiceMocks_withExpectationSet() {
    MyControl ctrl = GWT.create(MyControl.class);
    ToMock mock = ctrl.getMock();
    
    ctrl.expect(mock.returnString()).andReturn("Hallo");
    ctrl.replay();
    
    ctrl.setToNice(mock);

    assertEquals("Hallo", mock.returnString());
    assertNull(mock.returnString());
    
    ctrl.verify();
  }
  
  public void testNiceMocks_switching() {
    MyControl ctrl = GWT.create(MyControl.class);
    ToMock mock = ctrl.getMock();
    
    ctrl.expect(mock.returnString()).andReturn("Hallo");
    ctrl.replay();
    
    ctrl.setToNice(mock);

    assertEquals("Hallo", mock.returnString());
    assertNull(mock.returnString());
    assertNull(mock.returnString());
    
    ctrl.setToNotNice(mock);
    
    boolean exceptionThrown = true;
    try {
      mock.returnString();
      exceptionThrown = false;
    } catch (AssertionError expected) {
    }
    assertTrue("should have thrown exception", exceptionThrown);
    
    ctrl.verify();
  }
  
  interface MyControlWithNiceMock extends MocksControl {
    @Nice ToMock getNiceMock();
    ToMock getNotNiceMock();
  }
  
  public void testAnotationOnMethod() {
    MyControlWithNiceMock ctrl = GWT.create(MyControlWithNiceMock.class);
    ToMock niceMock = ctrl.getNiceMock();
    ToMock notNiceMock = ctrl.getNotNiceMock();
    ctrl.replay();
    
    assertNull(niceMock.returnString());
    assertEquals(0, niceMock.returnInt());
    assertEquals(false, niceMock.returnBoolean());
    assertEquals(0f, niceMock.returnFloat());
    
    boolean exceptionThrown = true;
    try {
      notNiceMock.returnInt();
      exceptionThrown = false;
    } catch (AssertionError expected) {
    }
    assertTrue("should have thrown exception", exceptionThrown);
    
    ctrl.verify();
  }
  
  @Nice interface MyNiceControl extends MocksControl {
    ToMock getOneMock();
    ToMock getAnotherMock();
  }
  
  public void testAnotationOnInterface() {
    MyNiceControl ctrl = GWT.create(MyNiceControl.class);
    ToMock aMock = ctrl.getOneMock();
    ToMock anotherMock = ctrl.getOneMock();
    ctrl.replay();
    
    assertNull(aMock.returnString());
    assertEquals(0, aMock.returnInt());
    assertEquals(false, aMock.returnBoolean());
    assertEquals(0f, aMock.returnFloat());
    
    assertNull(anotherMock.returnString());
    assertEquals(0, anotherMock.returnInt());
    assertEquals(false, anotherMock.returnBoolean());
    assertEquals(0f, anotherMock.returnFloat());
    
    ctrl.verify();
  }
  
  @Nice interface MyMixedControl extends MocksControl {
    ToMock getNiceMockOne();
    ToMock getNiceMockTwo();
    @Nice(false) ToMock getNotNiceMock();
  }
  
  public void testMixedAnotationOnInterfaceAndMethod() {
    MyMixedControl ctrl = GWT.create(MyMixedControl.class);
    ToMock niceMockOne = ctrl.getNiceMockOne();
    ToMock niceMockTwo = ctrl.getNiceMockTwo();
    ToMock notNiceMock = ctrl.getNotNiceMock();
    
    ctrl.replay();
    
    assertNull(niceMockOne.returnString());
    assertEquals(0, niceMockOne.returnInt());
    assertEquals(false, niceMockOne.returnBoolean());
    assertEquals(0f, niceMockOne.returnFloat());
    
    assertNull(niceMockTwo.returnString());
    assertEquals(0, niceMockTwo.returnInt());
    assertEquals(false, niceMockTwo.returnBoolean());
    assertEquals(0f, niceMockTwo.returnFloat());
    
    boolean exceptionThrown = true;
    try {
      notNiceMock.returnInt();
      exceptionThrown = false;
    } catch (AssertionError expected) {
    }
    assertTrue("should have thrown exception", exceptionThrown);
    
    ctrl.verify();
  }
  
  @Override
  public String getModuleName() {
    return "com.google.gwt.testing.easygwtmock.easygwtmock";
  }
}
