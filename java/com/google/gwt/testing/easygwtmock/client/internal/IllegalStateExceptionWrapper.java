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

/**
 * Wraps an IllegalStateException that is thrown somewhere in the internals of EasyGwtMock.
 * Before the exception leaves the framework, it is unwrapped and the stacktrace is cut
 * to hide the internals of EasyGwtMock from the user. 
 * 
 * @author Michael Goderbauer
 */
public class IllegalStateExceptionWrapper extends Exception {

  private final IllegalStateException exception;
  
  IllegalStateExceptionWrapper(IllegalStateException exception) {
    this.exception = exception;
  }
  
  public IllegalStateException getIllegalStateException() {
    return this.exception;
  }
}
