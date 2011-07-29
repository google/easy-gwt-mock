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

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;


/**
 * Turns a mock into a nice mock which will return an appropriate default value
 * (0, null, false) in response to unexpected method calls instead of throwing
 * an exception.
 * 
 * Can be used to annotate a method within the extended MocksControl interface or 
 * to annotate the entire interface.
 * 
 * @author Michael Goderbauer
 */
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Nice { 
  boolean value() default true;
}
