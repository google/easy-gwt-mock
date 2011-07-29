/*
 * Copyright 2001-2010 the original author or authors.
 * Portions Copyright 2011 Google Inc.
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

package com.google.gwt.testing.easygwtmock.client.internal.matchers;

import com.google.gwt.testing.easygwtmock.client.ArgumentMatcher;
import com.google.gwt.testing.easygwtmock.client.Capture;

/**
 * Matches any argument and saves it in the provided capture for later usage.
 * 
 * @author Michael Goderbauer
 * Originally written for EasyMock {@link "www.easymock.org"} by Henri Tremblay
 */
public class ArgumentCapture implements ArgumentMatcher {

  private Capture<? extends Object> capture;
  private Object potentialValue;
  
  public ArgumentCapture(Capture<? extends Object> capture) {
    this.capture = capture;
  }
  
  @Override
  public boolean matches(Object argument) {
    this.potentialValue = argument;
    return true;
  }

  @Override
  public void appendTo(StringBuffer buffer) {
    buffer.append("captured(").append(this.capture).append(")");
  }
  
  public void captureArgument() {
    this.capture.captureValue(this.potentialValue);
  }
}
