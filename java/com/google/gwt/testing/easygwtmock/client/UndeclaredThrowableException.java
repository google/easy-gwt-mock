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

/**
 * Exception is thrown when a mock method is supposed to throw a checked exception,
 * that is not declared in the method's signature.
 * 
 * @author Michael Goderbauer
 */
public class UndeclaredThrowableException extends RuntimeException {
  
  private Throwable undeclaredThrowable;

  public UndeclaredThrowableException(Throwable exception) {
    super("Cannot throw undeclared exception: " + exception.toString(), exception);
    this.undeclaredThrowable = exception;
  }
  
  public Throwable getUndeclaredThrowable() {
      return undeclaredThrowable;
  }
}
