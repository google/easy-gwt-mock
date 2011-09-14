// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.gwt.testing.easygwtmock.client.dummyclasses;

/**
 * Dummy class for testing.
 *
 * @author skybrian@google.com (Brian Slesinsky)
 */
public class OneArgClassToMock {
  public final String arg;

  public OneArgClassToMock(String arg) {
    this.arg = arg;
  }

  // cannot be mocked
  public final String makeMessage() {
    return getGreeting() + " " + arg + "!";
  }

  // can be mocked
  public String getGreeting() {
    return "Hello,";
  }
}
