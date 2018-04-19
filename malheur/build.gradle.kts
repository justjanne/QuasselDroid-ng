plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
}

android {
  compileSdkVersion(27)
  buildToolsVersion("27.0.3")

  defaultConfig {
    minSdkVersion(14)
    targetSdkVersion(27)

    consumerProguardFiles("proguard-rules.pro")
  }
}

dependencies {
  implementation(kotlin("stdlib", "1.2.40"))

  implementation("com.google.code.gson", "gson", "2.8.2")
}
