/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.annotations;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;

@SuppressWarnings("WeakerAccess")
@AutoService(Processor.class)
@SupportedAnnotationTypes("de.kuschku.libquassel.annotations.Syncable")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class InvokerProcessor extends AbstractProcessor {

  private Filer filer;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    filer = processingEnv.getFiler();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    List<SyncableElement> syncableElements = new ArrayList<>();
    for (Element element : roundEnv.getElementsAnnotatedWith(Syncable.class)) {
      if (element.getKind() == ElementKind.INTERFACE) {
        List<SlotElement> slotElements = new ArrayList<>();
        for (Element element1 : element.getEnclosedElements()) {
          if (element1.getKind() == ElementKind.METHOD) {
            ExecutableElement it = (ExecutableElement) element1;
            ExecutableType methodType = (ExecutableType) it.asType();

            Slot slotAnnotation = element1.getAnnotation(Slot.class);
            if (slotAnnotation != null) {
              String slotName = slotAnnotation.value().isEmpty() ? it.getSimpleName().toString() : slotAnnotation.value();
              slotElements.add(new SlotElement(it, methodType, slotName, slotAnnotation));
            }
          }
        }

        PackageElement packageElement = (PackageElement) element.getEnclosingElement();
        TypeElement typeElement = (TypeElement) element;
        Syncable annotation = typeElement.getAnnotation(Syncable.class);

        syncableElements.add(new SyncableElement(packageElement, typeElement, annotation, slotElements));
      }
    }

    try {
      for (SyncableElement syncableElement : syncableElements) {
        generateInvoker(syncableElement);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }

  private void generateInvoker(SyncableElement element) throws IOException {
    String packageName = element.packageElement.getQualifiedName().toString() + ".invokers";
    String invokerName = element.annotation.name() + "Invoker";

    ClassName type = ClassName.get(packageName, invokerName);
    ClassName wrongObjectTypeException = ClassName.get("de.kuschku.libquassel.quassel.exceptions", "WrongObjectTypeException");
    ClassName unknownMethodException = ClassName.get("de.kuschku.libquassel.quassel.exceptions", "UnknownMethodException");
    ClassName nonNullAnnotation = ClassName.get("androidx.annotation", "NonNull");

    MethodSpec methodSpecConstructor = MethodSpec
      .constructorBuilder()
      .addModifiers(Modifier.PRIVATE)
      .build();

    FieldSpec fieldSpecInstance = FieldSpec
      .builder(type, "INSTANCE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
      .initializer("new $T()", type)
      .build();

    MethodSpec methodSpecClassName = MethodSpec
      .methodBuilder("getClassName")
      .addModifiers(Modifier.PUBLIC)
      .addAnnotation(nonNullAnnotation)
      .addAnnotation(Override.class)
      .returns(String.class)
      .addStatement("return $S", element.annotation.name())
      .build();

    ParameterSpec parameterSpecOn = ParameterSpec
      .builder(
        Object.class,
        "on"
      )
      .addAnnotation(nonNullAnnotation)
      .build();

    ParameterSpec parameterSpecMethod = ParameterSpec
      .builder(
        String.class,
        "method"
      )
      .addAnnotation(nonNullAnnotation).build();

    ParameterSpec parameterSpecParams = ParameterSpec
      .builder(
        ParameterizedTypeName.get(
          ClassName.get(List.class),
          WildcardTypeName.subtypeOf(
            ParameterizedTypeName.get(
              ClassName.get("de.kuschku.libquassel.protocol", "QVariant"),
              WildcardTypeName.subtypeOf(Object.class)
            )
          )
        ),
        "params"
      )
      .addAnnotation(nonNullAnnotation)
      .build();

    MethodSpec.Builder invokeSpec = MethodSpec
      .methodBuilder("invoke")
      .addModifiers(Modifier.PUBLIC)
      .addAnnotation(Override.class)
      .addException(wrongObjectTypeException)
      .addException(unknownMethodException)
      .addParameter(parameterSpecOn)
      .addParameter(parameterSpecMethod)
      .addParameter(parameterSpecParams)
      .beginControlFlow("if (on instanceof $T)", element.typeElement)
      .addStatement("$T it = ($T) $N", element.typeElement, element.typeElement, parameterSpecOn)
      .beginControlFlow("switch ($N)", parameterSpecMethod);

    for (SlotElement slot : element.slots) {
      invokeSpec = invokeSpec.beginControlFlow("case $S:", slot.slotName);
      invokeSpec = invokeSpec.addCode("it.$N(\n$>", slot.element.getSimpleName());
      for (int i = 0; i < slot.type.getParameterTypes().size(); i++) {
        TypeMirror parameterType = slot.type.getParameterTypes().get(i);
        boolean isLast = i + 1 == slot.type.getParameterTypes().size();

        invokeSpec = invokeSpec.addCode("($T) ($T) $N.get($L).getData()", parameterType, Object.class, parameterSpecParams, i);
        if (!isLast)
          invokeSpec = invokeSpec.addCode(",");
        invokeSpec = invokeSpec.addCode("\n");
      }
      invokeSpec = invokeSpec.addCode("$<);\n");
      invokeSpec = invokeSpec.endControlFlow("return");
    }

    invokeSpec = invokeSpec
      .beginControlFlow("default:")
      .addStatement("throw new $T($N(), $N)",
        unknownMethodException,
        methodSpecClassName,
        parameterSpecMethod
      )
      .endControlFlow()
      .endControlFlow()
      .addCode("$<} else{\n$>")
      .addStatement("throw new $T($N, $N())",
        wrongObjectTypeException,
        parameterSpecOn,
        methodSpecClassName
      )
      .endControlFlow();

    TypeSpec typeSpec = TypeSpec
      .classBuilder(type)
      .addSuperinterface(ParameterizedTypeName.get(
        ClassName.get(packageName, "Invoker"),
        TypeName.get(element.typeElement.asType())
      ))
      .addModifiers(Modifier.PUBLIC)
      .addField(fieldSpecInstance)
      .addMethod(methodSpecConstructor)
      .addMethod(methodSpecClassName)
      .addMethod(invokeSpec.build())
      .build();

    JavaFile javaFile = JavaFile
      .builder(packageName, typeSpec)
      .build();

    javaFile.writeTo(filer);
  }

  private class SlotElement {
    final ExecutableElement element;
    final ExecutableType type;

    final String slotName;

    final Slot slot;

    public SlotElement(ExecutableElement element, ExecutableType type, String slotName, Slot slot) {
      this.element = element;
      this.type = type;
      this.slotName = slotName;
      this.slot = slot;
    }
  }

  private class SyncableElement {
    final PackageElement packageElement;
    final TypeElement typeElement;

    final Syncable annotation;

    final List<SlotElement> slots;

    public SyncableElement(PackageElement packageElement, TypeElement typeElement, Syncable annotation, List<SlotElement> slots) {
      this.packageElement = packageElement;
      this.typeElement = typeElement;
      this.annotation = annotation;
      this.slots = slots;
    }
  }
}
