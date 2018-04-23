buildscript {
  repositories {
    google()
    jcenter()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:3.1.2")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.2.40")
  }
}

allprojects {
  repositories {
    google()
    jcenter()
    maven(url = "https://jitpack.io")
  }
}

subprojects {
  configurations.all {
    resolutionStrategy {
      eachDependency {
        if (requested.name == "kotlin-compiler-embeddable") {
          useVersion("1.2.31")
        }
      }
    }
  }
}
