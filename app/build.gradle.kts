plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("de.justjanne.git-version")
  id("de.justjanne.android-signing")
}

android {
  setCompileSdkVersion(30)
  buildToolsVersion = "30.0.3"

  defaultConfig {
    applicationId = "com.iskrembilen.quasseldroid"

    setMinSdkVersion(21)
    setTargetSdkVersion(30)

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    // Disable test runner analytics
    testInstrumentationRunnerArguments(mapOf(
      "disableAnalytics" to "true"
    ))
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      multiDexEnabled = true

      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "proguard-rules.pro"
      )
    }

    getByName("debug") {
      applicationIdSuffix = ".debug"
      multiDexEnabled = true
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  buildFeatures {
    compose = true
  }

  composeOptions {
    val androidxComposeVersion: String by project
    kotlinCompilerExtensionVersion = androidxComposeVersion
  }

  kotlinOptions {
    useIR = true
  }
}

kapt {
  correctErrorTypes = true
}

dependencies {
  val androidxCoreVersion: String by project
  implementation("androidx.core", "core-ktx", androidxCoreVersion)

  val androidxAppcompatVersion: String by project
  implementation("androidx.appcompat", "appcompat", androidxAppcompatVersion)

  val mdcVersion: String by project
  implementation("com.google.android.material", "material", mdcVersion)

  val androidxComposeVersion: String by project
  implementation("androidx.compose.ui", "ui", androidxComposeVersion)
  implementation("androidx.compose.material", "material", androidxComposeVersion)
  implementation("androidx.compose.ui", "ui-tooling", androidxComposeVersion)

  val androidxLifecycleVersion: String by project
  implementation("androidx.lifecycle", "lifecycle-runtime-ktx", androidxLifecycleVersion)

  val androidxMultidexVersion: String by project
  implementation("androidx.multidex", "multidex", androidxMultidexVersion)

  val daggerHiltVersion: String by project
  implementation("com.google.dagger", "hilt-android", daggerHiltVersion)
  annotationProcessor("com.google.dagger", "hilt-android-compiler", daggerHiltVersion)
  testImplementation("com.google.dagger", "hilt-android-testing", daggerHiltVersion)
  testAnnotationProcessor("com.google.dagger", "hilt-android-compiler", daggerHiltVersion)
  androidTestImplementation("com.google.dagger", "hilt-android-testing", daggerHiltVersion)
  androidTestAnnotationProcessor("com.google.dagger", "hilt-android-compiler", daggerHiltVersion)

  implementation("org.threeten", "threetenbp", "1.4.0")

  implementation(project(":protocol"))

  implementation("io.coil-kt", "coil", "1.1.1")
  implementation("dev.chrisbanes.accompanist", "accompanist-coil", "0.5.0")

  val junit4Version: String by project
  testImplementation("junit", "junit", junit4Version)
  androidTestImplementation("androidx.test.ext", "junit", "1.1.2")
  androidTestImplementation("androidx.test.espresso", "espresso-core", "3.3.0")
}
