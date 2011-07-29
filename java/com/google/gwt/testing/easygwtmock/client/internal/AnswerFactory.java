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

import com.google.gwt.testing.easygwtmock.client.Answer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Creates simple Answer objects
 * 
 * @author Michael Goderbauer
 */
public class AnswerFactory {

  static Answer<Object> forValue(final Object value) {  
    return new Answer<Object>() {
      @Override
      public Object answer(Object[] args) throws Throwable {
        return value;
      }};
  }
  
  static Answer<Object> forThrowable(final Throwable throwable) {
    return new Answer<Object>() {
      @Override
      public Object answer(Object[] args) throws Throwable {
        throw throwable;
      }};
  }
  
  static Answer<Object> forOnSuccess(final Object result) {
    return new Answer<Object>() {
      @SuppressWarnings("unchecked")
      @Override
      public Object answer(Object[] args) throws Throwable {
        if (args.length > 0 && args[args.length - 1] instanceof AsyncCallback) {
          ((AsyncCallback<Object>) args[args.length - 1]).onSuccess(result);
          return null;
        }
        throw new IllegalArgumentException(
            "No com.google.gwt.user.client.rpc.AsyncCallback object as last argument provided");
      }};
  }
  
  static Answer<Object> forOnFailure(final Throwable caught) {
    return new Answer<Object>() {
      @SuppressWarnings("unchecked")
      @Override
      public Object answer(Object[] args) throws Throwable {
        if (args.length > 0 && args[args.length - 1] instanceof AsyncCallback) {
          ((AsyncCallback<Object>) args[args.length - 1]).onFailure(caught);
          return null;
        }
        throw new IllegalArgumentException(
            "No com.google.gwt.user.client.rpc.AsyncCallback object as last argument provided");
      }};
  }
}
