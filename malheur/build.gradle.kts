plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
}

android {
  compileSdkVersion(26)
  buildToolsVersion("27.0.2")

  defaultConfig {
    minSdkVersion(9)
    targetSdkVersion(26)

    consumerProguardFiles("proguard-rules.pro")
  }
}

dependencies {
  implementation(kotlin("stdlib", "1.2.0"))

  implementation("com.google.code.gson", "gson", "2.2.4")
}
