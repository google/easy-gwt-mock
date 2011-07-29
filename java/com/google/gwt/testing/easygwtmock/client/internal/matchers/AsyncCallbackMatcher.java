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

package com.google.gwt.testing.easygwtmock.client.internal.matchers;

import com.google.gwt.testing.easygwtmock.client.ArgumentMatcher;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Matches any AsyncCallback object
 * 
 * @author Michael Goderbauer
 */
public class AsyncCallbackMatcher implements ArgumentMatcher {

  public static final ArgumentMatcher MATCHER = new AsyncCallbackMatcher();
  
  private AsyncCallbackMatcher() {
  }

  @Override
  public boolean matches(Object argument) {
    return argument instanceof AsyncCallback;
  }

  @Override
  public void appendTo(StringBuffer buffer) {
    buffer.append("<AsyncCallback>");
  }

}
