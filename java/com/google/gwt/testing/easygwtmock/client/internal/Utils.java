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

import java.util.Arrays;

/**
 * Provides different static helper methods.
 * 
 * @author Michael Goderbauer
 */
public class Utils {

  /**
   * Appends a string representation of the given argument to the buffer.
   */
  public static void appendArgumentTo(Object argument, StringBuffer b) {
    if (argument == null) {
      b.append("null");
    } else if (argument.getClass().isArray()) {
      if (argument instanceof Object[]) {
        b.append(Arrays.deepToString((Object[]) argument));
      } else if (argument instanceof boolean[]) {
        b.append(Arrays.toString((boolean[]) argument));
      } else if (argument instanceof byte[]) {
        b.append(Arrays.toString((byte[]) argument));
      } else if (argument instanceof char[]) {
        b.append(Arrays.toString((char[]) argument));
      } else if (argument instanceof short[]) {
        b.append(Arrays.toString((short[]) argument));
      } else if (argument instanceof int[]) {
        b.append(Arrays.toString((int[]) argument));
      } else if (argument instanceof long[]) {
        b.append(Arrays.toString((long[]) argument));
      } else if (argument instanceof float[]) {
        b.append(Arrays.toString((float[]) argument));
      } else if (argument instanceof double[]) {
        b.append(Arrays.toString((double[]) argument));
      }
    } else {
      b.append(argument.toString());
    }
  }
  
  public static boolean isSubclass(Class<?> subclass, Class<?> baseclass) {
    while (subclass != null) {
      if (subclass.equals(baseclass)) {
        return true;
      }
      subclass = subclass.getSuperclass();
    }
    return false;
  }
  
  public static String cutPackage(String str) {
    String[] components = str.split("\\.");
    return components[components.length - 1].replace("$", ".");
  }
}
