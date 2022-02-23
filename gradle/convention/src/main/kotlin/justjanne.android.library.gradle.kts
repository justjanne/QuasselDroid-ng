plugins {
  id("com.android.library")
  id("justjanne.kotlin.android")
}

android {
  compileSdk = 30

  defaultConfig {
    minSdk = 21
    targetSdk = 30

    consumerProguardFiles("proguard-rules.pro")

    // Disable test runner analytics
    testInstrumentationRunnerArguments["disableAnalytics"] = "true"
  }

  lint {
    warningsAsErrors = true
    lintConfig = file("../lint.xml")
  }
}
