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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An object that represents a method call.
 * 
 * @author Michael Goderbauer
 */
public class Call {

  private final Object mock;
  private final Method method;
  private final List<Object> arguments;

  public Call(Object mock, Method method, Object... arguments) {
    this.mock = mock;
    this.method = method;
    this.arguments = new ArrayList<Object>(Arrays.asList(arguments));
  }
  
  public List<Object> getArguments() {
    return this.arguments;
  }
  
  /**
   * Add the varargs argument to the call.
   */
  public void addVarArgument(Object[] arg) {
    for (Object o : arg) {
      this.arguments.add(o);
    }
  }
  
  /**
   * Add the varargs argument to the call.
   */
  public void addVarArgument(byte[] arg) {
    for (byte o : arg) {
      this.arguments.add(o);
    }
  }
  
  /**
   * Add the varargs argument to the call.
   */
  public void addVarArgument(short[] arg) {
    for (short o : arg) {
      this.arguments.add(o);
    }
  }
  
  /**
   * Add the varargs argument to the call.
   */
  public void addVarArgument(int[] arg) {
    for (int o : arg) {
      this.arguments.add(o);
    }
  }
  
  /**
   * Add the varargs argument to the call.
   */
  public void addVarArgument(long[] arg) {
    for (long o : arg) {
      this.arguments.add(o);
    }
  }
  
  /**
   * Add the varargs argument to the call.
   */
  public void addVarArgument(float[] arg) {
    for (float o : arg) {
      this.arguments.add(o);
    }
  }
  
  /**
   * Add the varargs argument to the call.
   */
  public void addVarArgument(double[] arg) {
    for (double o : arg) {
      this.arguments.add(o);
    }
  }
  
  /**
   * Add the varargs argument to the call.
   */
  public void addVarArgument(boolean[] arg) {
    for (boolean o : arg) {
      this.arguments.add(o);
    }
  }
  
  /**
   * Add the varargs argument to the call.
   */
  public void addVarArgument(char[] arg) {
    for (char o : arg) {
      this.arguments.add(o);
    }
  }

  @Override
  public String toString() {
    return this.method.getName() + "(" + argumentsToString() + ")";
  }
  
  private String argumentsToString() {
    StringBuffer b = new StringBuffer();
    for (int i = 0; i < this.arguments.size(); i++) {
      if (i != 0) {
        b.append(", ");
      }
      Utils.appendArgumentTo(this.arguments.get(i), b);
    }
    return b.toString();
  }

  Method getMethod() {
    return this.method;
  }

  Object getMock() {
    return this.mock;
  }

  Object getDefaultReturnValue() {
    return this.method.getDefaultReturnValue();
  }
}
