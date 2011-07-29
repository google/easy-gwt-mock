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

import com.google.gwt.testing.easygwtmock.client.UndeclaredThrowableException;

import junit.framework.TestCase;

/**
 * Tests AssertionErrorWrapper, IllegalStateExceptionWrapper, UndeclaredThrowableException
 * 
 * @author Michael Goderbauer
 */
public class ExceptionJavaTest extends TestCase {
  
  public void testAssertionErrorWraper() {
    AssertionError wrapped = new AssertionError();
    AssertionErrorWrapper exeption = new AssertionErrorWrapper(wrapped);
    
    assertSame(exeption.getAssertionError(), wrapped);
  }
  
  public void testIllegalStateExceptionWrapper() {
    IllegalStateException wrapped = new IllegalStateException();
    IllegalStateExceptionWrapper exeption = new IllegalStateExceptionWrapper(wrapped);
    
    assertSame(exeption.getIllegalStateException(), wrapped);
  }

  public void testUndeclaredThrowableException() {
    Throwable wrapped = new IllegalStateException();
    UndeclaredThrowableException exeption = new UndeclaredThrowableException(wrapped);
    
    assertSame(exeption.getUndeclaredThrowable(), wrapped);
  }
}
