plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
  mavenCentral()
  google()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
  implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.6.10-1.0.4")
  implementation("com.android.tools.build:gradle:7.1.1")
}

configure<JavaPluginExtension> {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}
