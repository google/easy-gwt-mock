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

package com.google.gwt.testing.easygwtmock.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Will contain what was captured by the {@code capture()} matcher.
 * 
 * @param <T> Type of the captured element
 * 
 * @author Michael Goderbauer
 * Originally written for EasyMock {@link "www.easymock.org"} by Henri Tremblay
 */
public class Capture<T> {
  
  private List<T> values = new ArrayList<T>();
  
  /**
   * Will reset capture to a "nothing captured yet" state
   */
  public void reset() {
    this.values.clear();
  }
  
  /**
   * @return true if something was captured
   */
  public boolean hasCaptured() {
    return !this.values.isEmpty();
  }
  
  /**
   * @return all captured values.
   */
  public List<T> getValues() {
    return this.values;
  }
  
  /**
   * @return the first captured value.
   */
  public T getFirstValue() {
    return this.values.get(0);
  }
  
  /**
   * @return the last captured value.
   */
  public T getLastValue() {
      return this.values.get(this.values.size() - 1);
  }
  
  // The following is for internal EasyGwtMock usage only
  
  /**
   * Used internally by EasyGwtMock to capture a value.
   */
  @SuppressWarnings("unchecked")
  public void captureValue(Object value) {
    this.values.add((T) value);
  }
  
  @Override
  public String toString() {
    if (this.values.isEmpty()) {
      return "<nothing>";
    }
    String string = this.values.toString();
    return string.substring(1, string.length() - 1);
  }
}
