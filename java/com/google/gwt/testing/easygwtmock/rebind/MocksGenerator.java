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
import com.google.gwt.core.ext.typeinfo.JClassType;
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
  String generateMock(JClassType interfaceToMock) {
  
    JPackage interfacePackage = interfaceToMock.getPackage();
    String packageName = interfacePackage == null ? "" : interfacePackage.getName();
    String newClassName = interfaceToMock.getName().replace(".", "_") + "Mock";
    
    // GenericType<Integer> has to generate a different mock implementation than
    // GenericType<String>, that's what we check and do here
    if (interfaceToMock.isParameterized() != null) {
      StringBuilder typeList = new StringBuilder();
      for (JClassType genericArg : interfaceToMock.isParameterized().getTypeArgs()) {
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
    composer.addImplementedInterface(interfaceToMock.getParameterizedQualifiedSourceName());
    
    SourceWriter sourceWriter = composer.createSourceWriter(this.context, printWriter);
    sourceWriter.println();
    
    List<JMethod> methodsToMock = getMethodsToMock(interfaceToMock);
    
    printFields(sourceWriter, methodsToMock);
    printConstructor(sourceWriter, newClassName);
    printMockMethods(sourceWriter, methodsToMock, newClassName);
    printDefaultMethods(sourceWriter, interfaceToMock);
    
    sourceWriter.commit(this.logger);
    
    return fullNewClassName;
  }
  
  /**
   * Print the default implementation for {@link java.lang.Object} methods.
   */
  private void printDefaultMethods(SourceWriter writer, JClassType typeToMock) {
    writer.println("public boolean equals(Object obj) {");
    writer.indent();
    writer.println("this.mocksControl.unmockableCallTo(\"equals()\");");
    writer.println("return obj == this;"); // object identity since Mocks don't hold any data
    writer.outdent();
    writer.println("}");
    writer.println();
    
    writer.println("public String toString() {");
    writer.indent();
    writer.println("this.mocksControl.unmockableCallTo(\"toString()\");");
    writer.println("return \"Mock for %s\";", typeToMock.getName());
    writer.outdent();
    writer.println("}");
    writer.println();
    
    writer.println("public int hashCode() {");
    writer.indent();
    writer.println("this.mocksControl.unmockableCallTo(\"hashCode()\");");
    writer.println("return System.identityHashCode(this);"); // default hashCode of java.lang.Object
    writer.outdent();
    writer.println("}");
    writer.println();
    
    writer.println("protected void finalize() throws Throwable {");
    writer.indent();
    writer.println("this.mocksControl.unmockableCallTo(\"finalize()\");");
    writer.println("super.finalize();");
    writer.outdent();
    writer.println("}");
    writer.println();
  }
  
  /**
   * Returns all overridable methods of typeToMock except for those
   * implemented by {@link java.lang.Object}. We don't want to mock those
   * and provide special implementations for them.
   */
  private List<JMethod> getMethodsToMock(JClassType typeToMock) {
    List<JMethod> result = new ArrayList<JMethod>();
    JMethod[] overridableMethods = typeToMock.getOverridableMethods();
    
    for (JMethod method : overridableMethods) {
      String name = method.getName();
      JType[] types = method.getParameterTypes();
      if (types.length == 0 && (name.equals("finalize") ||
                                name.equals("getClass") || // TODO(goderbauer): Bug 5026788 in GWT
                                name.equals("hashCode") ||
                                name.equals("toString")) ||
          types.length == 1 && types[0].getQualifiedSourceName().equals("java.lang.Object")) {
        continue; // we don't want to mock those methods
      }
      result.add(method);
    }
    return result;
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
    sourceWriter.println("private final MocksControlBase mocksControl;");
    sourceWriter.println();
  }

  /**
   * Prints constructor for mock class.
   */
  private void printConstructor(SourceWriter sourceWriter, String newClassName) {
    sourceWriter.println("public  %s(MocksControlBase mocksControl) {", newClassName);
    sourceWriter.indent();
    sourceWriter.println("this.mocksControl = mocksControl;");
    sourceWriter.outdent();
    sourceWriter.println("}");
    sourceWriter.println();
  }
  
  /**
   * Generates and prints the actual mock versions of the methods.
   */
  private void printMockMethods(SourceWriter sourceWriter, List<JMethod> methodsToMock,
                                String newClassName) {
    int methodNo = 0;
    for (JMethod method : methodsToMock) {
      sourceWriter.println("%s {", method.getReadableDeclaration(false, true, false, false, true)
          .replace("transient", "")); //TODO(goderbauer): known GWT bug, fixed in trunk
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
