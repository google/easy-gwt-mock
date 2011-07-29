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
 * Represents how often a call is expected.
 * 
 * @author Michael Goderbauer
 */
public class Range {
  
  /**
   * Represents an open ended max value
   */
  static final int UNLIMITED_MAX = Integer.MAX_VALUE;
  
  /**
   * Default range is Range(1, 1)
   */
  public static final Range DEFAULT = new Range(1, 1);
  
  private final int max;
  private final int min;
  
  Range(int min, int max) {
    this.min = min;
    this.max = max;
  }
  
  @Override
  public String toString() {
    if (this.min == this.max) {
      return "" + this.min;
    } 
    if (this.max == UNLIMITED_MAX) {
      return "at least " + this.min;
    }
    return "between " + this.min + " and " + this.max;
  }

  int getMax() {
    return this.max;
  }
  
  int getMin() {
    return this.min;
  }
  
  /**
   * Checks, if the given value is within the range.
   */
  boolean includes(int value) {
    return value >= this.min && value <= this.max;
  }
}
