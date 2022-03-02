plugins {
  id("com.android.library")
  id("justjanne.kotlin.android")
}

@Suppress("UnstableApiUsage")
android {
  compileSdk = 31

  defaultConfig {
    minSdk = 21
    targetSdk = 31

    consumerProguardFiles("proguard-rules.pro")

    // Disable test runner analytics
    testInstrumentationRunnerArguments["disableAnalytics"] = "true"
  }

  lint {
    warningsAsErrors = true
    lintConfig = file("../lint.xml")
  }
}
