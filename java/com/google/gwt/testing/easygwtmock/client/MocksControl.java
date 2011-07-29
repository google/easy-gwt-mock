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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * To create mock objects, extend this interface and populate it with methods
 * that return the types you want to mock.
 * 
 * <p>Example: If you want to create a mock of {@code ComplexType} and {@code AnotherType} create
 * the following interface:
 * 
 * <p><pre>
 * public interface MyMocksControl extends MocksControl {
 *   ComplexType getComplexTypeMock();
 *   AnotherType getAnotherTypeMock();
 * } 
 * </pre>
 * 
 * <p>Pass this interface to {@code GWT.create()} to generate mocks and access them in the
 * following way:
 * 
 * <p><pre>
 * MyMocksControl ctrl = GWT.create(MyMocksControl.class);
 * ComplexType mock = ctrl.getComplexTypeMock();
 * </pre>
 * 
 * <p>The interface also provides the methods listed below to manipulate the state of the mocks
 * and to access various argument matcher.
 * 
 * @author Michael Goderbauer
 * JavaDoc partly by OFFIS and Tammo Freese of EasyMock
 */
public interface MocksControl {
  
  /**
   * Switches mocks to replay mode.
   */
  public void replay();
  
  /**
   * Verifies if all expectations on mocks were met.
   */
  public void verify();
  
  /**
   * Resets all mocks, deletes all expectations.
   */
  public void reset();
  
  /**
   * Returns the expectation setter for the last expected invocation.
   * 
   * @param <T>
   *            type returned by the expected method
   * @param value
   *            the parameter is used to transport the type to the
   *            ExpectationSetter. It allows writing the expected call as
   *            argument, i.e.
   *            <code>ctrl.expect(mock.getName()).andReturn("John Doe")<code>.
   */
  public <T> ExpectationSetters<T> expect(T value);
  
  /**
   * Returns the expectation setter for the last expected invocation. 
   * This method is used for expected invocations on void methods.
   * 
   * @param <T>
   *            type returned by the expected method
   */
  public <T> ExpectationSetters<T> expectLastCall();
  
  /**
   * Turns a mock into a nice mock which will return an appropriate default value
   * (0, null, false) in response to unexpected method calls instead of throwing
   * an exception.
   * 
   * @return the same mock you passed into the method
   */
  public <T> T setToNice(T mock);
  
  /**
   * Turns a nice mock into a regular mock which will throw an exception as response to
   * an unexpected call. This is default behavior.
   * 
   * @see #setToNice(Object)
   * 
   * @return the same mock you passed into the method
   */
  public <T> T setToNotNice(T mock);
  
  /**
   * Expects any boolean argument.
   */
  public boolean anyBoolean();

  /**
   * Expects any byte argument.
   */
  public byte anyByte();

  /**
   * Expects any char argument.
   */
  public char anyChar();

  /**
   * Expects any int argument.
   */
  public int anyInt();

  /**
   * Expects any long argument.
   */
  public long anyLong();

  /**
   * Expects any float argument.
   */
  public float anyFloat();

  /**
   * Expects any double argument.
   */
  public double anyDouble();

  /**
   * Expects any short argument.
   */
  public short anyShort();

  /**
   * Expects any Object argument.
   * This matcher (and {@link #anyObject(Class)}) can be used in these three ways:
   * <ul>
   * <li><code>(T)ctrl.anyObject() // explicit cast</code></li>
   * <li>
   * <code>ctrl.&lt;T&gt; anyObject() // fixing the returned generic</code>
   * </li>
   * <li>
   * <code>ctrl.anyObject(T.class) // pass the returned type in parameter</code>
   * </li>
   * </ul>
   * 
   * @return null
   */
  public <T> T anyObject();

  /**
   * Expects any Object argument.
   * To work well with generics, this matcher can be used in three different ways.
   * See {@link #anyObject()}.
   * 
   * @param <T>
   *            type of the method argument to match
   * @param clazz
   *            the class of the argument to match
   * @return null
   */
  public <T> T anyObject(final Class<T> clazz);
  
  /**
   * Expects a boolean that is equal to the given value.
   */
  public boolean eq(boolean value);

  /**
   * Expects a byte that is equal to the given value.
   */
  public byte eq(byte value);

  /**
   * Expects a char that is equal to the given value.
   */
  public char eq(char value);

  /**
   * Expects a double that is equal to the given value.
   */
  public double eq(double value);

  /**
   * Expects a float that is equal to the given value.
   */
  public float eq(float value);

  /**
   * Expects an int that is equal to the given value.
   */
  public int eq(int value);

  /**
   * Expects a long that is equal to the given value.
   */
  public long eq(long value);

  /**
   * Expects a short that is equal to the given value.
   */
  public short eq(short value);

  /**
   * Expects an Object that is equal to the given value.
   */
  public <T> T eq(T value);
  
  /**
   * Expects an AsyncCallback&lt;T&gt; object.
   * 
   * <code>
   * myRemoteApi.getInteger(ctrl.<Integer>asyncCallback());
   * ctrl.expectLastCall().andCallOnSuccess(...);
   * </code>
   * 
   * Alternatively, you can also use {@link #asyncCallback(Class)}
   * 
   * @return null
   */
  public <T> AsyncCallback<T> asyncCallback();
  
  /**
   * Expects an AsyncCallback&lt;T&gt; object.
   * 
   * Example for expecting an AsyncCallback&lt;Integer&gt;:
   * <code>
   * myRemoteApi.getInteger(ctrl.asyncCallback(Integer.class));
   * ctrl.expectLastCall().andCallOnSuccess(...);
   * </code>
   * 
   * Alternatively, you can also use {@link #asyncCallback()}
   * 
   * @return null
   */
  public <T> AsyncCallback<T> asyncCallback(final Class<T> clazz);

  /**
   * Expects any object and captures it for later use.
   */
  public <T> T captureObject(Capture<T> captured);
  
  /**
   * Expects a byte and captures it for later use.
   */
  public byte captureByte(Capture<Byte> captured);
  
  /**
   * Expects a short and captures it for later use.
   */
  public short captureShort(Capture<Short> captured);
  
  /**
   * Expects an int and captures it for later use.
   */
  public int captureInt(Capture<Integer> captured);
  
  /**
   * Expects an long and captures it for later use.
   */
  public long captureLong(Capture<Long> captured);
  
  /**
   * Expects a float and captures it for later use.
   */
  public float captureFloat(Capture<Float> captured);
  
  /**
   * Expects a double and captures it for later use.
   */
  public double captureDouble(Capture<Double> captured);
  
  /**
   * Expects a boolean and captures it for later use.
   */
  public boolean captureBoolean(Capture<Boolean> captured);
  
  /**
   * Expects a char and captures it for later use.
   */
  public char captureChar(Capture<Character> captured);
  
  /**
   * Report an argument matcher for a double value.
   */
  public byte matchesByte(ArgumentMatcher matcher);
  
  /**
   * Report an argument matcher for a double value.
   */
  public short matchesShort(ArgumentMatcher matcher);
  
  /**
   * Report an argument matcher for a double value.
   */
  public int matchesInt(ArgumentMatcher matcher);
  
  /**
   * Report an argument matcher for a double value.
   */
  public long matchesLong(ArgumentMatcher matcher);
  
  /**
   * Report an argument matcher for a double value.
   */
  public float matchesFloat(ArgumentMatcher matcher);
  
  /**
   * Report an argument matcher for a double value.
   */
  public double matchesDouble(ArgumentMatcher matcher);
  
  /**
   * Report an argument matcher for a boolean value.
   */
  public boolean matchesBoolean(ArgumentMatcher matcher);
  
  /**
   * Report an argument matcher for a char value.
   */
  public char matchesChar(ArgumentMatcher matcher);

  /**
   * Report an argument matcher for an object.
   * This method (and {@link #matchesObject(ArgumentMatcher, Class)})
   * can be used in these three ways:
   * <ul>
   * <li><code>(T)ctrl.matchesObject(matcher) // explicit cast</code></li>
   * <li>
   * <code>ctrl.&lt;T&gt; matchesObject(matcher) // fixing the returned generic</code>
   * </li>
   * <li>
   * <code>ctrl.matchesObject(matcher, T.class) // pass the returned type in parameter</code>
   * </li>
   * </ul>
   * 
   * @return null
   */
  public <T> T matchesObject(ArgumentMatcher matcher);
  
  /**
   * Report an argument matcher for an object.
   * To work well with generics, this matcher can be used in three different ways.
   * See {@link #matchesObject(ArgumentMatcher)}.
   * 
   * @param <T>
   *            type of the method argument to match
   * @param matcher
   *            the matcher to report
   * @param clazz
   *            the class of the argument to match
   * @return null
   */
  public <T> T matchesObject(ArgumentMatcher matcher, Class<T> clazz);
}
