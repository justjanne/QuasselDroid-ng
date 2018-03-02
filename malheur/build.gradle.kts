plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
}

android {
  compileSdkVersion(27)
  buildToolsVersion("27.0.3")

  defaultConfig {
    minSdkVersion(9)
    targetSdkVersion(27)

    consumerProguardFiles("proguard-rules.pro")
  }
}

dependencies {
  implementation(kotlin("stdlib", "1.2.30"))

  implementation("com.google.code.gson", "gson", "2.8.2")
}
