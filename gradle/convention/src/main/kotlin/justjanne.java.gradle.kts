import gradle.kotlin.dsl.accessors._9f9f63157b527b37420ecbe9e569524a.testImplementation
import gradle.kotlin.dsl.accessors._9f9f63157b527b37420ecbe9e569524a.testRuntimeOnly

plugins {
  java
  id("justjanne.repositories")
}

dependencies {
  testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.8.2")
  testImplementation("org.junit.jupiter", "junit-jupiter-params", "5.8.2")
  testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine")
}

configure<JavaPluginExtension> {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
