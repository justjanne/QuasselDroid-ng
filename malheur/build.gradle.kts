import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

apply {
  plugin("com.android.library")
  plugin("kotlin-android")
  plugin("kotlin-kapt")
}

android {
  compileSdkVersion(26)
  buildToolsVersion("26.0.1")

  defaultConfig {
    minSdkVersion(9)
    targetSdkVersion(26)

    consumerProguardFiles("proguard-rules.pro")
  }
}

dependencies {
  implementation(kotlin("stdlib"))

  implementation("com.google.code.gson:gson:2.2.4")
}

fun Project.android(f: LibraryExtension.() -> Unit)
  = configure(f)

fun DependencyHandlerScope.androidJacocoAgent(dependencyNotation: Any)
  = "androidJacocoAgent"(dependencyNotation)

fun DependencyHandlerScope.androidJacocoAgent(dependencyNotation: String,
                                              dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidJacocoAgent"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.androidJacocoAnt(dependencyNotation: Any)
  = "androidJacocoAnt"(dependencyNotation)

fun DependencyHandlerScope.androidJacocoAnt(dependencyNotation: String,
                                            dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidJacocoAnt"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.androidTestAnnotationProcessor(dependencyNotation: Any)
  = "androidTestAnnotationProcessor"(dependencyNotation)

fun DependencyHandlerScope.androidTestAnnotationProcessor(dependencyNotation: String,
                                                          dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidTestAnnotationProcessor"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.androidTestApk(dependencyNotation: Any)
  = "androidTestApk"(dependencyNotation)

fun DependencyHandlerScope.androidTestApk(dependencyNotation: String,
                                          dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidTestApk"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.androidTestImplementation(dependencyNotation: Any)
  = "androidTestImplementation"(dependencyNotation)

fun DependencyHandlerScope.androidTestImplementation(dependencyNotation: String,
                                                     dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidTestImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.androidTestJackPlugin(dependencyNotation: Any)
  = "androidTestJackPlugin"(dependencyNotation)

fun DependencyHandlerScope.androidTestJackPlugin(dependencyNotation: String,
                                                 dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidTestJackPlugin"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.androidTestProvided(dependencyNotation: Any)
  = "androidTestProvided"(dependencyNotation)

fun DependencyHandlerScope.androidTestProvided(dependencyNotation: String,
                                               dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidTestProvided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.androidTestWearApp(dependencyNotation: Any)
  = "androidTestWearApp"(dependencyNotation)

fun DependencyHandlerScope.androidTestWearApp(dependencyNotation: String,
                                              dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidTestWearApp"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.annotationProcessor(dependencyNotation: Any)
  = "annotationProcessor"(dependencyNotation)

fun DependencyHandlerScope.annotationProcessor(dependencyNotation: String,
                                               dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "annotationProcessor"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.apk(dependencyNotation: Any)
  = "apk"(dependencyNotation)

fun DependencyHandlerScope.apk(dependencyNotation: String,
                               dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "apk"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.archives(dependencyNotation: Any)
  = "archives"(dependencyNotation)

fun DependencyHandlerScope.archives(dependencyNotation: String,
                                    dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "archives"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.implementation(dependencyNotation: Any)
  = "implementation"(dependencyNotation)

fun DependencyHandlerScope.implementation(dependencyNotation: String,
                                          dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "implementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.debugAnnotationProcessor(dependencyNotation: Any)
  = "debugAnnotationProcessor"(dependencyNotation)

fun DependencyHandlerScope.debugAnnotationProcessor(dependencyNotation: String,
                                                    dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "debugAnnotationProcessor"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.debugApk(dependencyNotation: Any)
  = "debugApk"(dependencyNotation)

fun DependencyHandlerScope.debugApk(dependencyNotation: String,
                                    dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "debugApk"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.debugImplementation(dependencyNotation: Any)
  = "debugImplementation"(dependencyNotation)

fun DependencyHandlerScope.debugImplementation(dependencyNotation: String,
                                               dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "debugImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.debugJackPlugin(dependencyNotation: Any)
  = "debugJackPlugin"(dependencyNotation)

fun DependencyHandlerScope.debugJackPlugin(dependencyNotation: String,
                                           dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "debugJackPlugin"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.debugProvided(dependencyNotation: Any)
  = "debugProvided"(dependencyNotation)

fun DependencyHandlerScope.debugProvided(dependencyNotation: String,
                                         dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "debugProvided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.debugWearApp(dependencyNotation: Any)
  = "debugWearApp"(dependencyNotation)

fun DependencyHandlerScope.debugWearApp(dependencyNotation: String,
                                        dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "debugWearApp"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.default(dependencyNotation: Any)
  = "default"(dependencyNotation)

fun DependencyHandlerScope.default(dependencyNotation: String,
                                   dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "default"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.jackPlugin(dependencyNotation: Any)
  = "jackPlugin"(dependencyNotation)

fun DependencyHandlerScope.jackPlugin(dependencyNotation: String,
                                      dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "jackPlugin"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.kapt(dependencyNotation: Any)
  = "kapt"(dependencyNotation)

fun DependencyHandlerScope.kapt(dependencyNotation: String,
                                dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "kapt"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.kaptAndroidTest(dependencyNotation: Any)
  = "kaptAndroidTest"(dependencyNotation)

fun DependencyHandlerScope.kaptAndroidTest(dependencyNotation: String,
                                           dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "kaptAndroidTest"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.kaptDebug(dependencyNotation: Any)
  = "kaptDebug"(dependencyNotation)

fun DependencyHandlerScope.kaptDebug(dependencyNotation: String,
                                     dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "kaptDebug"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.kaptRelease(dependencyNotation: Any)
  = "kaptRelease"(dependencyNotation)

fun DependencyHandlerScope.kaptRelease(dependencyNotation: String,
                                       dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "kaptRelease"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.kaptTest(dependencyNotation: Any)
  = "kaptTest"(dependencyNotation)

fun DependencyHandlerScope.kaptTest(dependencyNotation: String,
                                    dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "kaptTest"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.kaptTestDebug(dependencyNotation: Any)
  = "kaptTestDebug"(dependencyNotation)

fun DependencyHandlerScope.kaptTestDebug(dependencyNotation: String,
                                         dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "kaptTestDebug"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.kaptTestRelease(dependencyNotation: Any)
  = "kaptTestRelease"(dependencyNotation)

fun DependencyHandlerScope.kaptTestRelease(dependencyNotation: String,
                                           dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "kaptTestRelease"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.provided(dependencyNotation: Any)
  = "provided"(dependencyNotation)

fun DependencyHandlerScope.provided(dependencyNotation: String,
                                    dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "provided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.releaseAnnotationProcessor(dependencyNotation: Any)
  = "releaseAnnotationProcessor"(dependencyNotation)

fun DependencyHandlerScope.releaseAnnotationProcessor(dependencyNotation: String,
                                                      dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "releaseAnnotationProcessor"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.releaseApk(dependencyNotation: Any)
  = "releaseApk"(dependencyNotation)

fun DependencyHandlerScope.releaseApk(dependencyNotation: String,
                                      dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "releaseApk"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.releaseImplementation(dependencyNotation: Any)
  = "releaseImplementation"(dependencyNotation)

fun DependencyHandlerScope.releaseImplementation(dependencyNotation: String,
                                                 dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "releaseImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.releaseJackPlugin(dependencyNotation: Any)
  = "releaseJackPlugin"(dependencyNotation)

fun DependencyHandlerScope.releaseJackPlugin(dependencyNotation: String,
                                             dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "releaseJackPlugin"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.releaseProvided(dependencyNotation: Any)
  = "releaseProvided"(dependencyNotation)

fun DependencyHandlerScope.releaseProvided(dependencyNotation: String,
                                           dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "releaseProvided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.releaseWearApp(dependencyNotation: Any)
  = "releaseWearApp"(dependencyNotation)

fun DependencyHandlerScope.releaseWearApp(dependencyNotation: String,
                                          dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "releaseWearApp"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testAnnotationProcessor(dependencyNotation: Any)
  = "testAnnotationProcessor"(dependencyNotation)

fun DependencyHandlerScope.testAnnotationProcessor(dependencyNotation: String,
                                                   dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testAnnotationProcessor"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testApk(dependencyNotation: Any)
  = "testApk"(dependencyNotation)

fun DependencyHandlerScope.testApk(dependencyNotation: String,
                                   dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testApk"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testImplementation(dependencyNotation: Any)
  = "testImplementation"(dependencyNotation)

fun DependencyHandlerScope.testImplementation(dependencyNotation: String,
                                              dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testDebugAnnotationProcessor(dependencyNotation: Any)
  = "testDebugAnnotationProcessor"(dependencyNotation)

fun DependencyHandlerScope.testDebugAnnotationProcessor(dependencyNotation: String,
                                                        dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testDebugAnnotationProcessor"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testDebugApk(dependencyNotation: Any)
  = "testDebugApk"(dependencyNotation)

fun DependencyHandlerScope.testDebugApk(dependencyNotation: String,
                                        dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testDebugApk"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testDebugImplementation(dependencyNotation: Any)
  = "testDebugImplementation"(dependencyNotation)

fun DependencyHandlerScope.testDebugImplementation(dependencyNotation: String,
                                                   dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testDebugImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testDebugJackPlugin(dependencyNotation: Any)
  = "testDebugJackPlugin"(dependencyNotation)

fun DependencyHandlerScope.testDebugJackPlugin(dependencyNotation: String,
                                               dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testDebugJackPlugin"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testDebugProvided(dependencyNotation: Any)
  = "testDebugProvided"(dependencyNotation)

fun DependencyHandlerScope.testDebugProvided(dependencyNotation: String,
                                             dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testDebugProvided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testDebugWearApp(dependencyNotation: Any)
  = "testDebugWearApp"(dependencyNotation)

fun DependencyHandlerScope.testDebugWearApp(dependencyNotation: String,
                                            dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testDebugWearApp"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testJackPlugin(dependencyNotation: Any)
  = "testJackPlugin"(dependencyNotation)

fun DependencyHandlerScope.testJackPlugin(dependencyNotation: String,
                                          dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testJackPlugin"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testProvided(dependencyNotation: Any)
  = "testProvided"(dependencyNotation)

fun DependencyHandlerScope.testProvided(dependencyNotation: String,
                                        dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testProvided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testReleaseAnnotationProcessor(dependencyNotation: Any)
  = "testReleaseAnnotationProcessor"(dependencyNotation)

fun DependencyHandlerScope.testReleaseAnnotationProcessor(dependencyNotation: String,
                                                          dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testReleaseAnnotationProcessor"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testReleaseApk(dependencyNotation: Any)
  = "testReleaseApk"(dependencyNotation)

fun DependencyHandlerScope.testReleaseApk(dependencyNotation: String,
                                          dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testReleaseApk"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testReleaseImplementation(dependencyNotation: Any)
  = "testReleaseImplementation"(dependencyNotation)

fun DependencyHandlerScope.testReleaseImplementation(dependencyNotation: String,
                                                     dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testReleaseImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testReleaseJackPlugin(dependencyNotation: Any)
  = "testReleaseJackPlugin"(dependencyNotation)

fun DependencyHandlerScope.testReleaseJackPlugin(dependencyNotation: String,
                                                 dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testReleaseJackPlugin"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testReleaseProvided(dependencyNotation: Any)
  = "testReleaseProvided"(dependencyNotation)

fun DependencyHandlerScope.testReleaseProvided(dependencyNotation: String,
                                               dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testReleaseProvided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testReleaseWearApp(dependencyNotation: Any)
  = "testReleaseWearApp"(dependencyNotation)

fun DependencyHandlerScope.testReleaseWearApp(dependencyNotation: String,
                                              dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testReleaseWearApp"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testWearApp(dependencyNotation: Any)
  = "testWearApp"(dependencyNotation)

fun DependencyHandlerScope.testWearApp(dependencyNotation: String,
                                       dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testWearApp"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.wearApp(dependencyNotation: Any)
  = "wearApp"(dependencyNotation)

fun DependencyHandlerScope.wearApp(dependencyNotation: String,
                                   dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "wearApp"(dependencyNotation, dependencyConfiguration)
