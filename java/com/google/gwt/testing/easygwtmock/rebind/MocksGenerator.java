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

package com.google.gwt.testing.easygwtmock.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JConstructor;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.testing.easygwtmock.client.UndeclaredThrowableException;
import com.google.gwt.testing.easygwtmock.client.internal.AssertionErrorWrapper;
import com.google.gwt.testing.easygwtmock.client.internal.Call;
import com.google.gwt.testing.easygwtmock.client.internal.Method;
import com.google.gwt.testing.easygwtmock.client.internal.MocksControlBase;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generator for mock classes.
 * 
 * @author Michael Goderbauer
 */
public class MocksGenerator {
  
  private GeneratorContext context;
  private TreeLogger logger;

  MocksGenerator(GeneratorContext context, TreeLogger logger) {
    this.context = context;
    this.logger = logger;
  }
  
  /**
   * Generates a mock class for {@code interfaceToMock}.
   */
  String generateMock(JClassType typeToMock) throws UnableToCompleteException {
  
    JPackage interfacePackage = typeToMock.getPackage();
    String packageName = interfacePackage == null ? "" : interfacePackage.getName();
    String newClassName = typeToMock.getName().replace(".", "_") + "Mock";
    
    // GenericType<Integer> has to generate a different mock implementation than
    // GenericType<String>, that's what we check and do here
    if (typeToMock.isParameterized() != null) {
      StringBuilder typeList = new StringBuilder();
      for (JClassType genericArg : typeToMock.isParameterized().getTypeArgs()) {
        typeList.append(genericArg.getParameterizedQualifiedSourceName());
      }
      newClassName += Integer.toHexString(typeList.toString().hashCode());
    }
    
    String fullNewClassName = packageName + "." + newClassName;
    
    PrintWriter printWriter = this.context.tryCreate(this.logger, packageName, newClassName);
    if (printWriter == null) {
      // We generated this before.
      return fullNewClassName;
    }
    
    ClassSourceFileComposerFactory composer = 
        new ClassSourceFileComposerFactory(packageName, newClassName);
    composer.addImport(MocksControlBase.class.getCanonicalName());
    composer.addImport(Method.class.getCanonicalName());
    composer.addImport(Call.class.getCanonicalName());
    composer.addImport(UndeclaredThrowableException.class.getCanonicalName());
    if (typeToMock.isInterface() != null) {
      composer.addImplementedInterface(typeToMock.getParameterizedQualifiedSourceName());
    } else {
      composer.setSuperclass(typeToMock.getParameterizedQualifiedSourceName());
    }
    
    SourceWriter sourceWriter = composer.createSourceWriter(this.context, printWriter);
    sourceWriter.println();
    
    JMethod[] overridableMethods = typeToMock.getOverridableMethods();
    
    List<JMethod> methodsToMock = new ArrayList<JMethod>();
    Set<String> needsDefaultImplementation = new HashSet<String>();
    for (JMethod method : overridableMethods) {
      if (isSpecialMethodOfObject(method)) {
        needsDefaultImplementation.add(method.getName());
      } else if (method.getParameterTypes().length == 0 && method.getName().equals("getClass")) {
        // ignore, Bug 5026788 in GWT
      } else {
        methodsToMock.add(method);
      }
    }
    
    printFields(sourceWriter, methodsToMock);
    printConstructors(sourceWriter, newClassName, typeToMock.getConstructors());
    printMockMethods(sourceWriter, methodsToMock, newClassName);
    printDefaultMethods(sourceWriter, typeToMock, needsDefaultImplementation);
    
    sourceWriter.commit(this.logger);
    
    return fullNewClassName;
  }

  private boolean isSpecialMethodOfObject(JMethod method) {
    String name = method.getName();
    JType[] types = method.getParameterTypes();
    return types.length == 0 && (name.equals("finalize") ||
                                 name.equals("hashCode") ||
                                 name.equals("toString")) ||
           types.length == 1 && name.equals("equals") &&
               types[0].getQualifiedSourceName().equals("java.lang.Object");
  }

  /**
   * Print the default implementation for {@link java.lang.Object} methods.
   * If typeToMock is a class it will only print default implementations for
   * methods of Object listed in the methods parameter. If typeToMock is an interface
   * it will always print default implementations for all methods of Object.
   */
  private void printDefaultMethods(SourceWriter writer, JClassType typeToMock, Set<String> methods) {
    
    // always print default implementations for interfaces
    boolean isInterface = typeToMock.isInterface() != null;
    
    if (isInterface || methods.contains("equals")) {
      writer.println("public boolean equals(Object obj) {");
      writer.indent();
      writer.println("this.mocksControl.unmockableCallTo(\"equals()\");");
      writer.println("return obj == this;"); // object identity since Mocks don't hold any data
      writer.outdent();
      writer.println("}");
      writer.println();
    }
    
    if (isInterface || methods.contains("toString")) {
      writer.println("public String toString() {");
      writer.indent();
      writer.println("this.mocksControl.unmockableCallTo(\"toString()\");");
      writer.println("return \"Mock for %s\";", typeToMock.getName());
      writer.outdent();
      writer.println("}");
      writer.println();
    }
    
    if (isInterface || methods.contains("hashCode")) {
      writer.println("public int hashCode() {");
      writer.indent();
      writer.println("this.mocksControl.unmockableCallTo(\"hashCode()\");");
      writer.println("return System.identityHashCode(this);"); // hashCode of java.lang.Object
      writer.outdent();
      writer.println("}");
      writer.println();
    }
    
    if (isInterface || methods.contains("finalize")) {
      writer.println("protected void finalize() throws Throwable {");
      writer.indent();
      writer.println("this.mocksControl.unmockableCallTo(\"finalize()\");");
      writer.println("super.finalize();");
      writer.outdent();
      writer.println("}");
      writer.println();
    }
  }
  
  /**
   * Prints required fields for mock class.
   */
  private void printFields(SourceWriter sourceWriter, List<JMethod> methodsToMock) {

    StringBuilder throwableArray = 
        new StringBuilder("private static final Class<?>[][] throwables = {");
    StringBuilder argumentTypesArray = 
        new StringBuilder("private static final Class<?>[][] argumentTypes = {");
    StringBuilder methodArray = 
        new StringBuilder("private static final Method[] methods = {");
    
    for (int i = 0; i < methodsToMock.size(); i++) {
      JMethod method = methodsToMock.get(i);
      
      throwableArray.append("{");
      for (JClassType throwable : method.getThrows()) {
        throwableArray
            .append(throwable.getQualifiedSourceName()).append(".class, ");
      }
      if (method.getThrows().length != 0) {
        throwableArray.setLength(throwableArray.length() - 2);
      }
      throwableArray.append("}, ");
      
      argumentTypesArray.append("{");
      for (JType argumentType : method.getParameterTypes()) {
        argumentTypesArray
            .append(argumentType.getErasedType().getQualifiedSourceName()).append(".class, ");
      }
      if (method.getParameterTypes().length != 0) {
        argumentTypesArray.setLength(argumentTypesArray.length() - 2);
      }
      argumentTypesArray.append("}, ");
      
      methodArray
          .append("new Method(\"").append(method.getName()).append("\", ")
          .append(method.getReturnType().getErasedType().getQualifiedSourceName())
          .append(".class, argumentTypes[").append(i).append("], ")
          .append("throwables[").append(i).append("]), ");
    }
    
    if (methodsToMock.size() != 0) {
      throwableArray.setLength(throwableArray.length() - 2);
      argumentTypesArray.setLength(argumentTypesArray.length() - 2);
      methodArray.setLength(methodArray.length() - 2);
    }
    throwableArray.append("};");
    argumentTypesArray.append("};");
    methodArray.append("};");
    
    sourceWriter.println(throwableArray.toString());
    sourceWriter.println(argumentTypesArray.toString());
    sourceWriter.println(methodArray.toString());
    sourceWriter.println("private MocksControlBase mocksControl;");
    sourceWriter.println();
  }

  /**
   * Prints each constructor for the mock class, and a hidden init method.
   */
  private void printConstructors(SourceWriter out, String newClassName,
      JConstructor[] constructors) {

    if (constructors.length == 0) {
      // probably an interface
      out.print("public  %s() {}", newClassName);
    }

    for (JConstructor constructor : constructors) {
      out.print("public  %s(", newClassName);
      printMatchingParameters(out, constructor);
      out.println(") {");

      out.indent();
      printMatchingSuperCall(out, constructor);
      out.outdent();

      out.println("}");
      out.println();
    }

    out.println("public %s __mockInit(MocksControlBase newValue) {", newClassName);
    out.indent();
    out.println("this.mocksControl = newValue;");
    out.println("return this;");
    out.outdent();
    out.println("}");
    out.println();
  }

  private void printMatchingParameters(SourceWriter out, JConstructor constructorToCall) {
    JParameter[] params = constructorToCall.getParameters();
    for (int i = 0; i < params.length; i++) {
      if (i > 0) {
        out.print(", ");
      }
      JParameter param = params[i];
      out.print(param.getType().getParameterizedQualifiedSourceName());
      out.print(" ");
      out.print(param.getName());
    }
  }

  private void printMatchingSuperCall(SourceWriter out, JConstructor constructorToCall) {
    if (constructorToCall.getParameters().length == 0) {
      return; // will be added automatically
    }

    out.print("super(");

    JParameter[] params = constructorToCall.getParameters();
    for (int i = 0; i < params.length; i++) {
      if (i > 0) {
        out.print(", ");
      }
      out.print(params[i].getName());
    }
    out.println(");");
  }

  /**
   * Generates and prints the actual mock versions of the methods.
   */
  private void printMockMethods(SourceWriter sourceWriter, List<JMethod> methodsToMock,
                                String newClassName) {
    int methodNo = 0;
    for (JMethod method : methodsToMock) {
      sourceWriter.println("%s {", method.getReadableDeclaration(false, true, false, false, true));
      sourceWriter.indent();
      printMockMethodBody(sourceWriter, method, methodNo++, newClassName);
      sourceWriter.outdent();
      sourceWriter.println("}");
      sourceWriter.println();
    }
  }

  private void printMockMethodBody(SourceWriter sourceWriter, JMethod method, 
                                     int methodNo, String newClassName) {
    
    JParameter[] args = method.getParameters();
    
    String callVar = freeVariableName("call", args);
    sourceWriter.print("Call %s = new Call(this, %s.methods[%d]", callVar, newClassName, methodNo);
    
    int argsCount = method.isVarArgs() ? args.length - 1 : args.length;

    for (int i = 0; i < argsCount; i++) {
      sourceWriter.print(", %s", args[i].getName());
    }
    sourceWriter.println(");");
    
    if (method.isVarArgs()) {
      sourceWriter.println("%s.addVarArgument(%s);", callVar, args[args.length - 1].getName());
    }
    
    sourceWriter.println("try {");
    sourceWriter.indent();
    
    if (!isVoid(method)) {
      sourceWriter.print("return (");
      
      JType returnType = method.getReturnType();
      JPrimitiveType primitiveType = returnType.isPrimitive();
      if (primitiveType != null) {
        sourceWriter.print(primitiveType.getQualifiedBoxedSourceName());
      } else if (returnType.isTypeParameter() != null) {
        sourceWriter.print(returnType.isTypeParameter().getName());
      } else {
        sourceWriter.print(returnType.getQualifiedSourceName());
      }
      
      sourceWriter.print(") ");
    }
    
    sourceWriter.println("this.mocksControl.invoke(%s).answer(%s.getArguments().toArray());", 
        callVar, callVar);
    sourceWriter.outdent();
    
    String exceptionVar = freeVariableName("exception", args);
    sourceWriter.println("} catch (Throwable %s) {", exceptionVar);
    sourceWriter.indent();
    
    String assertionError = AssertionErrorWrapper.class.getCanonicalName();
    sourceWriter.println("if (%s instanceof %s) throw (AssertionError) " +
        "((%s) %s).getAssertionError().fillInStackTrace();", 
        exceptionVar, assertionError, assertionError, exceptionVar);
    
    for (JClassType exception : method.getThrows()) {
      printRethrowException(sourceWriter, exceptionVar, exception.getQualifiedSourceName());
    }
    printRethrowException(sourceWriter, exceptionVar, "RuntimeException");
    printRethrowException(sourceWriter, exceptionVar, "Error");
    sourceWriter.println("throw new UndeclaredThrowableException(%s);", exceptionVar);
    sourceWriter.outdent();
    sourceWriter.println("}");
  }

  private void printRethrowException(SourceWriter writer, String var, String exception) {
    writer.println("if (%s instanceof %s) throw (%s) %s;", var, exception, exception, var);
  }

  /**
   * Returns a variable name that doesn't collide with any of the argument names.
   * The returned variable name starts with the provided prefix.
   */
  private String freeVariableName(String prefix, JParameter[] args) {
    String nextVar = prefix;
    
    Set<String> usedNames = new HashSet<String>();
    for (JParameter arg : args) {
      usedNames.add(arg.getName());
    }
    
    int varCount = 0;
    while (usedNames.contains(nextVar)) {
      nextVar = prefix + varCount++;
    }
    return nextVar;
  }
  
  private boolean isVoid(JMethod method) {
    JPrimitiveType primitiveType = method.getReturnType().isPrimitive();
    return (primitiveType != null && primitiveType.getSimpleSourceName().equals("void"));
  }
}
