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

import java.util.HashMap;
import java.util.Map;

/**
 * An object that represents a method that can be called on a mock object.
 * 
 * @author Michael Goderbauer
 */
public class Method {
  
  private final String name;
  private final Class<?> returnType;
  private final Class<?>[] declaredThrowables;
  private final Class<?>[] argumentTypes;
  
  private static Map<Class<?>, Object> defaultReturnValues = new HashMap<Class<?>, Object>();
  static {
    defaultReturnValues.put(byte.class, (byte) 0);
    defaultReturnValues.put(short.class, (short) 0);
    defaultReturnValues.put(int.class, 0);
    defaultReturnValues.put(long.class, (long) 0);
    defaultReturnValues.put(float.class, (float) 0);
    defaultReturnValues.put(double.class, (double) 0);
    defaultReturnValues.put(boolean.class, false);
    defaultReturnValues.put(char.class, (char) 0);
  }
  
  
  public Method(String name, Class<?> returnType, Class<?>[] argumentTypes,
                Class<?>[] declaredThrowables) {
    this.name = name;
    this.returnType = returnType;
    this.declaredThrowables = declaredThrowables;
    this.argumentTypes = argumentTypes;
  }

  public String getName() {
    return this.name;
  }
  
  public Object getDefaultReturnValue() {
    return defaultReturnValues.get(this.returnType);
  }

  public boolean isReturnValueVoid() {
    return this.returnType.equals(void.class);
  }

  public boolean isReturnValuePrimitive() {
    return this.returnType.isPrimitive();
  }
  
  public boolean canThrow(Throwable throwable) {
    if (throwable instanceof RuntimeException) {
      return true;
    }
    if (throwable instanceof Error) {
      return true;
    }
    Class<?> throwableClass = throwable.getClass();
    for (Class<?> declaredThrowable : this.declaredThrowables) {
      if (Utils.isSubclass(throwableClass, declaredThrowable)) {
        return true;
      }
    }
    return false;
  }

  public Class<?>[] getArgumentTypes() {
    return this.argumentTypes;
  }
  
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder(this.name);
    result.append("(");
    for (Class<?> argument : this.argumentTypes) {
      result.append(Utils.cutPackage(argument.getName())).append(", ");
    }
    if (this.argumentTypes.length > 0) {
      result.setLength(result.length() - 2);
    }
    
    result.append(")");
    return result.toString();
  }
}
