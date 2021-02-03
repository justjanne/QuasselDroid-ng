plugins {
  id("com.android.application")
  id("kotlin-android")
  id("de.kuschku.justcode")
}

android {
  defaultConfig {
    setMinSdkVersion(21)
    setTargetSdkVersion(30)

    applicationId = "com.iskrembilen.quasseldroid"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      setMinifyEnabled(false)
      setProguardFiles(
        listOf(
          getDefaultProguardFile("proguard-android-optimize.txt"),
          "proguard-rules.pro"
        )
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
    useIR = true
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    val androidxComposeVersion: String by project.extra
    kotlinCompilerExtensionVersion = androidxComposeVersion
  }
}

dependencies {
  val androidxCoreVersion: String by project.extra
  implementation("androidx.core", "core-ktx", androidxCoreVersion)

  val androidxAppcompatVersion: String by project.extra
  implementation("androidx.appcompat", "appcompat", androidxAppcompatVersion)

  val mdcVersion: String by project.extra
  implementation("com.google.android.material", "material", mdcVersion)

  val androidxComposeVersion: String by project.extra
  implementation("androidx.compose.ui", "ui", androidxComposeVersion)
  implementation("androidx.compose.material", "material", androidxComposeVersion)
  implementation("androidx.compose.ui", "ui-tooling", androidxComposeVersion)

  val androidxLifecycleVersion: String by project.extra
  implementation("androidx.lifecycle", "lifecycle-runtime-ktx", androidxLifecycleVersion)

  implementation("org.threeten", "threetenbp", "1.4.0")

  implementation("io.coil-kt", "coil", "1.1.1")
  implementation("dev.chrisbanes.accompanist", "accompanist-coil", "0.5.0")

  testImplementation("junit", "junit", "4.13.1")
  androidTestImplementation("androidx.test.ext", "junit", "1.1.2")
  androidTestImplementation("androidx.test.espresso", "espresso-core", "3.3.0")
}
