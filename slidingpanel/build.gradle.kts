plugins {
  id("com.android.library")
}

android {
  compileSdkVersion(27)
  buildToolsVersion("27.0.3")

  defaultConfig {
    minSdkVersion(14)
    targetSdkVersion(27)

    // Disable test runner analytics
    testInstrumentationRunnerArguments = mapOf(
      "disableAnalytics" to "true"
    )
  }

  lintOptions {
    isWarningsAsErrors = true
    setLintConfig(file("../lint.xml"))
  }
}

dependencies {
  // App Compat
  withVersion("27.1.1") {
    implementation("com.android.support", "support-v4", version)
    implementation("com.android.support", "support-annotations", version)
    implementation("com.android.support", "recyclerview-v7", version)
  }
}
