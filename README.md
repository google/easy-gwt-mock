# Easy GWT Mock  #

_Easy GWT Mock_ is an EasyMock-like mocking framework for [Google Web Toolkit](http://www.gwtproject.org/) which allows the creation of mock objects within `GWTTestCase`. Feature and syntax-wise it is a lot like [EasyMock](http://www.easymock.org/). However, there are three main differences:
  * _Easy GWT Mock_ focuses on mocking interfaces, class mocking is somewhat limited.
  * You have to create an interface extending MocksControl to specify which types you want to mock.
  * Unlike EasyMock, _Easy GWT Mock_ does not use static methods to record expectations, etc.

Read the full list of differences between EasyMock and _Easy GWT Mock_ at the end of this page or continue reading to learn how to use _Easy GWT Mock_ in your project.

## Integrating _Easy GWT Mock_ in Your Project ##

Checkout the sources and execute
```
ant -Dgwt.home=/path/to/gwt jar
```
to build the _Easy GWT Mock_ jar. Include the jar from the out directory in your project and add the following line to your application's gwt.xml file:
```
<inherits name='com.google.gwt.testing.easygwtmock.EasyGwtMock'/>
```
That's it! You are ready to use _Easy GWT Mock_.

## Create Your First Mock ##

Let’s get started! Imagine, you want to mock the following type (in this case, an interface):
```
public interface ToMock {
  int add(int a, int b);
  void doSomething(String s);
  int call(Callback callback);
}
```
First, you need to tell _Easy GWT Mock_ which type(s) you want to mock by creating a special interface that extends `MocksControl`. This interface should include a method for each type you want to mock with that type as return type:
```
interface MyMocksControl extends MocksControl {
  ToMock getDummyMock();
  // add one method for every other type to mock
}
```
Next, you pass this newly defined interface to `GWT.create()`, which will generate a control object implementing that interface. Hold on to that object as it gives you access to your mocks and other mocking related features.
```
// Create the control object
MyMocksControl ctrl = GWT.create(MyMocksControl.class);
// Fetch the mock you want from the control object
ToMock mock = ctrl.getDummyMock();
```
After you get your hands on a mock object (as shown above), the workflow is similar to EasyMock. You use EasyMock-like syntax to record expectations. However, instead of calling static methods on EasyMock, you must call instance methods on your generated control object. Once you are done recording expectations, you can switch to replay mode and use the mock object like a normal object. At the end, call `verify()` to check if all expectations have been fulfilled:
```
ctrl.expect(mock.add(2, 3)).andReturn(5).once();
// Like in EasyMock, once() is the default so you can leave it out:
ctrl.expect(mock.add(1, 1)).andReturn(2);

ctrl.expect(mock.add(4, 2)).andReturn(6).anyTimes();
ctrl.expect(mock.add(6, 2)).andReturn(8).times(2);
ctrl.expect(mock.add(2, 1)).andReturn(3).times(4, 6);

// Use expectLastCall() for void methods:
mock.doSomething("Hello");
ctrl.expectLastCall().atLeastOnce();

// chaining expectations is also possible:
ctrl.expect(mock.add(4, 2)).andReturn(6).andReturn(3);
// ... is equivalent to:
ctrl.expect(mock.add(4, 2)).andReturn(6);
ctrl.expect(mock.add(4, 2)).andReturn(3);

ctrl.replay();

// ... (interact with the mock)

ctrl.verify();
```
That’s it! If you want to learn more about the semantic of `anyTimes()`, `expectLastCall()`, `once()`, etc. see the [EasyMock documentation](http://easymock.org/user-guide.html).


## Using Argument Matchers ##

Sometimes you do not want to specify the exact value of an argument. That is where argument matchers are useful. Use argument matchers to specify that you expect an argument to be within a certain range:
```
ctrl.expect(mock.add(ctrl.anyInt(), ctrl.anyInt()).andReturn(4);
```
If you decide to use a matcher for one argument, you have to use them for all arguments:
```
ctrl.expect(mock.add(ctrl.anyInt(), 6).andReturn(4); // illegal
ctrl.expect(mock.add(ctrl.anyInt(), ctrl.eq(6)).andReturn(4); // fix
```
Have a lock at the `MocksControl` interface to learn which argument matchers are currently available. If there is no matcher that fits your use case, then fear not because you can create your own matcher!

## Create Your Own Argument Matcher ##

Create a class that implements the `ArgumentMatcher` interface. This interface contains two methods: `matches(Object actual)` checks if the actual argument of a method call is accepted by the argument matcher and `appendTo(StringBuffer buffer)` appends a string representation of the argument matcher to the given string buffer. Let’s say we only expect odd integers as arguments. We could specify the following argument matcher for the job:
```
public class IsOdd implements ArgumentMatcher {

  public boolean matches(Object actual) {
    if (!(actual instanceof Integer)) {
      return false;
    }
    int argument = ((Integer) actual);
    return argument % 2 == 1;
  }

  public void appendTo(StringBuffer buffer) {
    buffer.append("isOdd()");
  }
}
```
To use this matcher, just pass an instance of it to one of the `ctrl.matchesXXX()` methods. Replace `XXX` with the type you want to match (either Object or the name of a primitive type):
```
IsOdd isOdd = new IsOdd();
ctrl.expect(mock.add(ctrl.matchesInt(isOdd), ctrl.anyInt()).andReturn(4);
```

## Capture Arguments for Later Use ##

Sometimes you want to hold on to an argument passed to a mock method. You can do that with captures. Just create a `Capture` for the type you are interested in and pass it to one of the `captureXXX()` argument matcher. Use `captureByte()`, `captureShort()`, `captureInt()`, etc. to capture a primitive type or `captureObject()` to capture an Object.
A capture will accept any argument!
```
Capture<Integer> addend = new Capture<Integer>;
mock.add(ctrl.captureInt(addend), ctrl.eq(4));

// do something with the mock

addend.getFirstValue(); // returns the first value captured
addend.getLastValue();  // returns the last value captured
addend.getValues();     // returns a list of all captured values
```

## Throwing Exceptions ##

You can throw an exception as a reaction to a mock method call by using `andThrow()` instead of `andReturn()`. Be aware: You can only throw checked exceptions which are declared in the method's signature or unchecked exceptions (subtypes of `RuntimeException` and `Error`). Throwing undeclared checked exceptions will lead to an `UndeclaredThrowableException`.
```
ctrl.expect(mock.add(11, 2)).andThrow(new IllegalArgumentException());
```

## Calling onSuccess() or onFailure() ##

Asynchronous callbacks are quite common in GWT applications - and there is an easy way to invoke their `onSuccess()` or `onFailure()` method with _Easy GWT Mock_. If your method accepts a GWT `AsyncCallback` object as argument you can use `andCallOnSuccess()` as well as `andCallOnFailure()` during expectation recording to invoke the callback as response to a mock method call. Both methods pass their only argument on to `onSuccess()` or `onFailure()`. Let’s say method `foo()` accepts an `AsyncCallback<Integer>` object and as a reaction to calling foo() we want to call its `onSuccess()` method with argument 42. Here is the code for setting up that expectation:
```
mock.foo(ctrl.asyncCallback(Integer.class));
ctrl.expectLastCall().andCallOnSuccess(42);
```
If your callback object does not implement the `AsyncCallback` interface, use the more generic `Answer` described in the next section to invoke your callback.

## More Sophisticated Responses ##

Sometimes you actually want to do something more sophisticated than just returning a value or throwing an exception. For example, you might want to call a callback in reaction to a method call. You can do that and much more with the help of `Answer`:
```
ctrl.expect(mock.call(callback)).andAnswer(new Answer<Integer>() {
  @Override
  public Integer answer(Object[] args) throws Throwable {
    ((Callback) args[0]).doIt();
    return 42;
  }});
```
Caution: Using the method’s arguments within an `Answer` is not refactoring-safe!

Word of advice: If your callback implements GWT’s `AsyncCallback` interface you should not use `Answer` and instead use the easier way described in the previous section.

## Nice Mocks ##

Mocks throw an exception in response to an unexpected method call as default behavior. However, there are situations where you want a “nice” mock that returns appropriate default values ({{0}}}, `null`, `false`) to an unexpected call instead of throwing an exception. Those mocks are called nice mocks and there are three ways to create one. You can turn any mock into a nice mock during replay mode by calling `setToNice()`. Optionally, use `setToNotNice()` to reverse the result.
```
ctrl.setToNice(mock);
// do something with mock, unexpected calls will not throw exceptions
ctrl.setToNotNice(mock); //optional
```
In addition to that, you can annotate a mock with `@Nice` in the extended `MocksControl` interface to create nice mocks. In the example below, `getNiceMock()` will return a nice mock of `TypeToMock`:
```
interface MyMocksControl extends MocksControl {
  @Nice TypeToMock getNiceMock(); //returns a nice mock
  TypeToMock getNotNiceMock(); // returns a regular (“not nice”) mock
}
```
Last, but not least, you can annotate the entire interface with `@Nice`:
```
@Nice
interface MyMocksControl extends MocksControl {
  TypeToMock getMock(); //returns a nice mock
  AnotherTypeToMock getAnotherMock(); // returns a nice mock
}
```
The following is also valid:
```
@Nice
interface MyMocksControl extends MocksControl {
  TypeToMock getMock(); //returns a nice mock
  @Nice(false) TypeToMock getAnotherMock(); // returns a regular mock
}
```
**WARNING:** `ctrl.reset()` will reset all mocks to the default (not nice) behavior!

## Object’s equal(), hasCode() and toString() methods ##

You cannot mock the following basic methods of `java.lang.Object`: `equals()`, `toString()`, `hashCode()`. For your convenience, we have provided a default implementation for those methods. This makes it easier to add mocks to collections which might make an arbitrary amount of `equals()` and `hashCode()` calls to the mock. The default `toString()` implementation makes it easy for exceptions to display a string representation of the mock.

## Limited Class Mocking (coming soon) ##

The main focus of _Easy GWT Mock_ is on mocking interfaces. Class mocking is somewhat limited because due to the way GWT and Java work the mock is actually a subclass of the class to mock. Please keep the following limitations in mind when mocking classes:
  * _Easy GWT Mock_ will call the constructor of the class to mock. Therefore, make sure the class you want to mock doesn’t do anything expensive or crazy in the constructor.
  * You can only mock classes with a zero-argument constructor.
  * When the Java garbage collector decides to destroy the mock, the `finalize()` method of the class to mock might be called.
  * Methods marked as `final` or `private` cannot be mocked. If you call them, their normal code will be executed. It is highly discouraged to call those methods during record mode!
  * Classes marked as `final` cannot be mocked as well.

Class mocking with _Easy GWT Mock_ is super-easy as long as you keep those limitations in mind. To mock a class with a zero-arguments constructor just use the same simple syntax you have learnt for mocking interfaces with _Easy GWT Mock_.

## Frequently Asked Questions (FAQ) ##

**_Where should I put the interface extending `MocksControl` that defines which classes I want to mock?_**

As you can see in the tests for the framework I usually implement the interface as an inner type right inside the test classes where I need the mocks. That way, I end up with one interface per test class that only contains the mocks I need for that class. Other people have suggested to have just one interface for all your tests which includes all classes you want to mock.

## Differences between EasyMock and _Easy GWT Mock_ ##

Here is an (incomplete) list of the differences between _Easy GWT Mock_ and EasyMock:
  * _Easy GWT Mock_ focuses on mocking interfaces, class mocking is somewhat limited.
  * You have to create an interface extending `MocksControl` to specify which classes you want to mock.
  * Unlike EasyMock, _Easy GWT Mock_ does not use static methods to record expectations, set-up argument matchers, etc.
  * `Answer` provides access to the arguments of the mock’s method via an argument to `answer()`. EasyMock uses the static function `getCurrentArguments()` for that.
  * Error messages differ slightly.
  * Registering custom argument matchers works slightly different (see section “Create your own argument matcher”)
  * Registering argument captures works slightly different, you have to specify the type you want to capture (use `captureBoolen()`, `captureObject()`, etc. instead of `capture()`)

### Additional Features ###

_Easy GWT Mock_ has some special GWT related features which are not part of EasyMock:
  * `andCallOnSuccess()`: invoke the onSucess() method of an AsyncCallback provided as argument to a mock method.
  * `andCallOnFailure()`: invoke the onFailure() method of an AsyncCallback provided as argument to a mock method.

### Missing Features ###

The following features are available in EasyMock, but they are not (yet) implemented in _Easy GWT Mock_:
  * strict mocks where the order of method calls is checked
  * some argument matchers are not ported over to _Easy GWT Mock_
  * naming a mock for better error messages
  * partial mocks: mock only some methods of a class (requires class mocking)
  * `andDelegateTo()`: delegates a mock method call to a provided real object
  * `andStubAnswer()`, `andStubReturn()`, etc.


### Ideas For the Future ###

  * log all calls to a mock object with `GWT.log()`

## Special Thanks ##

Special thanks to the guys who created [EasyMock](http://www.easymock.org/).
