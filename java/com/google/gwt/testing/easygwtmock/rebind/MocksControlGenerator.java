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

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.testing.easygwtmock.client.MocksControl;
import com.google.gwt.testing.easygwtmock.client.Nice;
import com.google.gwt.testing.easygwtmock.client.internal.MocksControlBase;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * MocksControlGenerator generates the concrete implementation of an interface that extends 
 * {@link com.google.gwt.testing.easygwtmock.client.MocksControl} giving access to the
 * mocks specified in that interface.
 * 
 * @author Michael Goderbauer
 */
public class MocksControlGenerator extends Generator {
  
  /**
   * Generates the concrete MocksControl implementation of the {@code typeName} interface and 
   * delegates generation of mock classes to
   * {@link com.google.gwt.testing.easygwtmock.rebind.MocksGenerator}
   */
  @Override
  public String generate(TreeLogger logger, GeneratorContext context, String typeName)
          throws UnableToCompleteException {
    
    TypeOracle typeOracle = context.getTypeOracle();
    JClassType mockControlInterface = typeOracle.findType(typeName);
    
    if (mockControlInterface == null) {
      logger.log(TreeLogger.ERROR, "Unable to find metadata for type '" + typeName + "'", null);
      throw new UnableToCompleteException();
    }
    
    if (mockControlInterface.isInterface() == null) {
      logger.log(TreeLogger.ERROR,
          mockControlInterface.getQualifiedSourceName() + " is not an interface", null);
      throw new UnableToCompleteException();
    }
    
    JPackage interfacePackage = mockControlInterface.getPackage();
    String packageName = interfacePackage == null ? "" : interfacePackage.getName();
    String newClassName = mockControlInterface.getName().replace(".", "_") + "Impl";
    String fullNewClassName = packageName + "." + newClassName;
         
    PrintWriter printWriter = context.tryCreate(logger, packageName, newClassName);
    if (printWriter == null) {
      // We generated this before.
      return fullNewClassName;
    }
    
    ClassSourceFileComposerFactory composer =
        new ClassSourceFileComposerFactory(packageName, newClassName);
    composer.setSuperclass(MocksControlBase.class.getCanonicalName());
    composer.addImplementedInterface(mockControlInterface.getQualifiedSourceName());

    SourceWriter writer = composer.createSourceWriter(context, printWriter);
    writer.println();
    
    MocksGenerator mocksGenerator = new MocksGenerator(context, logger);
    JClassType markerInterface = typeOracle.findType(MocksControl.class.getCanonicalName());
    
    Set<String> reservedNames = getMethodNames(composer.getSuperclassName(), logger, typeOracle);
    
    for (JMethod method : mockControlInterface.getOverridableMethods()) {
      if (method.getEnclosingType().equals(markerInterface)) {
        // Method is implemented in MocksControlBase
        continue;
      }
      if (reservedNames.contains(method.getName())) {
        // Method name is already used in base class and method should not be overwritten!
        logger.log(TreeLogger.ERROR, method.getName() + 
            " is a reserved name. Do not use it in the extended MocksControl interface", null);
        throw new UnableToCompleteException();
      }
      
      JClassType interfaceToMock = method.getReturnType().isInterface();
      
      if (interfaceToMock == null) {
        logger.log(TreeLogger.ERROR, 
            method.getReturnType().getQualifiedSourceName() + " is not an interface", null);
        throw new UnableToCompleteException();
      }
      
      String mockClassName = mocksGenerator.generateMock(interfaceToMock);
      
      writer.println("%s {", method.getReadableDeclaration(false, false, false, false, true));
      writer.indent();
      if (isNiceMock(method, mockControlInterface)) {
        writer.println("return this.setToNice(new %s(this));", mockClassName);
      } else {
        writer.println("return new %s(this);", mockClassName);
      }
      writer.outdent();
      writer.println("}");
    }
    
    writer.commit(logger);
    return fullNewClassName;
  }

  private boolean isNiceMock(JMethod method, JClassType mockControlInterface) {
    boolean isNice = false; //default
    
    Nice interfaceAnnotation = mockControlInterface.getAnnotation(Nice.class);
    if (interfaceAnnotation != null) {
      isNice = interfaceAnnotation.value();
    }
    
    Nice methodAnotation = method.getAnnotation(Nice.class);
    if (methodAnotation != null) {
      isNice = methodAnotation.value();
    }
    
    return isNice;
  }

  private Set<String> getMethodNames(String className, TreeLogger logger,
                               TypeOracle typeOracle) throws UnableToCompleteException {
    
    JClassType baseClass = typeOracle.findType(className);
    if (baseClass == null) {
      logger.log(TreeLogger.ERROR, "Unable to find metadata for type '" + baseClass + "'", null);
      throw new UnableToCompleteException();
    }
    
    Set<String> result = new HashSet<String>();
    for (JMethod method : baseClass.getMethods()) {
      if (!method.isPrivate()) {
        result.add(method.getName());
      }
    }
    
    return result;
  }
}
