plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
}

android {
  compileSdkVersion(27)
  buildToolsVersion("27.0.3")

  defaultConfig {
    minSdkVersion(16)
    targetSdkVersion(27)

    consumerProguardFiles("proguard-rules.pro")

    javaCompileOptions {
      annotationProcessorOptions {
        arguments = mapOf("room.schemaLocation" to "$projectDir/schemas")
      }
    }
  }
}

dependencies {
  implementation(kotlin("stdlib", "1.2.30"))

  // App Compat
  withVersion("27.1.0") {
    implementation("com.android.support", "appcompat-v7", version)
  }

  // App Arch Persistence
  withVersion("1.1.0-beta1") {
    implementation("android.arch.persistence.room", "runtime", version)
    implementation("android.arch.persistence.room", "rxjava2", version)
    kapt("android.arch.persistence.room", "compiler", version)
    testImplementation("android.arch.persistence.room", "testing", version)
  }

  // App Arch Paging
  implementation("android.arch.paging", "runtime", "1.0.0-alpha7") {
    exclude(group = "junit", module = "junit")
  }

  // Utility
  implementation("org.threeten", "threetenbp", "1.3.6", classifier = "no-tzdb")
  implementation("org.jetbrains", "annotations", "15.0")

  // Quassel
  implementation(project(":lib")) {
    exclude(group = "org.threeten", module = "threetenbp")
  }
}