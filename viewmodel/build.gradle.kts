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
  }
}

dependencies {
  implementation(kotlin("stdlib", "1.2.31"))

  // App Compat
  withVersion("27.1.0") {
    implementation("com.android.support", "appcompat-v7", version)
  }

  // App Arch Lifecycle
  withVersion("1.1.0") {
    implementation("android.arch.lifecycle", "extensions", version)
    implementation("android.arch.lifecycle", "reactivestreams", version)
    kapt("android.arch.lifecycle", "compiler", version)
  }

  // Utility
  implementation("io.reactivex.rxjava2", "rxjava", "2.1.9")
  implementation("org.threeten", "threetenbp", "1.3.6", classifier = "no-tzdb")
  implementation("org.jetbrains", "annotations", "16.0.1")

  // Quassel
  implementation(project(":lib")) {
    exclude(group = "org.threeten", module = "threetenbp")
  }
}