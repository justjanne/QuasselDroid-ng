package de.kuschku.coverageconverter

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.register
import org.gradle.testing.jacoco.tasks.JacocoReport

class CoverageConverterPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.afterEvaluate {
      val testTask = tasks.getByName<Test>("test")

      val jacocoReportTask = tasks.getByName<JacocoReport>("jacocoTestReport"){
        dependsOn(testTask)
      }

      tasks.register("coberturaTestReport") {
        dependsOn(jacocoReportTask)
        mustRunAfter(jacocoReportTask)
        group = "verification"

        doLast(ConvertCoverageReportsAction(jacocoReportTask))
      }
    }
  }
}

