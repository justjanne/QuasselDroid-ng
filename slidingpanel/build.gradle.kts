plugins {
  id("com.android.library")
}

android {
  compileSdkVersion(27)
  buildToolsVersion("27.0.3")

  defaultConfig {
    minSdkVersion(14)
    targetSdkVersion(27)
  }
}

dependencies {
  // App Compat
  withVersion("27.0.2") {
    implementation("com.android.support", "support-v4", version)
    implementation("com.android.support", "support-annotations", version)
    implementation("com.android.support", "recyclerview-v7", version)
  }
}
