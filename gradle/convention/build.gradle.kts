plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
  mavenCentral()
  google()
}

dependencies {
  compileOnly(libs.android.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
  plugins {
    register("androidApplication") {
      id = "justjanne.android.app"
      implementationClass = "AndroidApplicationConvention"
    }
    register("androidLibrary") {
      id = "justjanne.android.library"
      implementationClass = "AndroidLibraryConvention"
    }
    register("kotlinAndroid") {
      id = "justjanne.kotlin.android"
      implementationClass = "KotlinAndroidConvention"
    }
    register("kotlin") {
      id = "justjanne.kotlin"
      implementationClass = "KotlinConvention"
    }
  }
}

configure<JavaPluginExtension> {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}
