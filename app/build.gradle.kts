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

    minSdk = 21
    targetSdk = 30

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    testInstrumentationRunnerArguments["disableAnalytics"] = "true"
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
  implementation("androidx.compose.foundation", "foundation", androidxComposeVersion)
  implementation("androidx.compose.foundation", "foundation-layout", androidxComposeVersion)
  implementation("androidx.compose.material", "material", androidxComposeVersion)
  implementation("androidx.compose.material", "material-icons-extended", androidxComposeVersion)
  implementation("androidx.compose.runtime", "runtime", androidxComposeVersion)
  implementation("androidx.compose.runtime", "runtime-livedata", androidxComposeVersion)
  implementation("androidx.compose.ui", "ui-tooling", androidxComposeVersion)
  implementation("androidx.compose.ui", "ui-util", androidxComposeVersion)
  implementation("androidx.compose.ui", "ui-viewbinding", androidxComposeVersion)
  testImplementation("androidx.compose.ui", "ui-test", androidxComposeVersion)

  val androidxActivityComposeVersion: String by project
  implementation("androidx.activity", "activity-compose", androidxActivityComposeVersion)

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

  implementation("org.threeten", "threetenbp", "1.5.1")

  implementation("io.coil-kt", "coil", "1.2.2")
  implementation("com.google.accompanist", "accompanist-coil", "0.11.1")

  val libquasselVersion: String by project
  implementation("de.justjanne.libquassel", "libquassel-client", libquasselVersion)

  val junit4Version: String by project
  testImplementation("junit", "junit", junit4Version)
  androidTestImplementation("androidx.test.ext", "junit", "1.1.2")
  androidTestImplementation("androidx.test.espresso", "espresso-core", "3.3.0")
}
