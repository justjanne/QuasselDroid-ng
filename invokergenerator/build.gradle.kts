
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.java
import org.gradle.kotlin.dsl.project

plugins {
  java
}

dependencies {
  implementation(project(":invokerannotations"))
  implementation("com.google.auto.service:auto-service:1.0-rc3")
  implementation("com.squareup:javapoet:1.9.0")
}

fun DependencyHandlerScope.implementation(dependencyNotation: Any)
  = "implementation"(dependencyNotation)
