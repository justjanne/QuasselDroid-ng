/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.justjanne.coverageconverter

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.io.File

class CoverageConverterPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val extension = project.extensions.create("coverage", CoverageConverterExtension::class.java)

    if (extension.autoConfigureCoverage) {
      val jacocoPluginExtension = project.extensions.findByType(JacocoPluginExtension::class.java)
      if (jacocoPluginExtension != null) {
        jacocoPluginExtension.toolVersion = "0.8.3"
      }
    }

    project.afterEvaluate {
      val testTask = tasks.getByName("test")

      val jacocoReportTask = tasks.getByName("jacocoTestReport") as? JacocoReport
      if (jacocoReportTask != null) {
        jacocoReportTask.dependsOn(testTask)
        if (extension.autoConfigureCoverage) {
          jacocoReportTask.sourceDirectories.from(fileTree("src/main/kotlin"))
          jacocoReportTask.classDirectories.from(fileTree("build/classes"))
          jacocoReportTask.reports {
            xml.destination = File("${buildDir}/reports/jacoco/report.xml")
            html.isEnabled = true
            xml.isEnabled = true
            csv.isEnabled = false
          }
        }

        tasks.register("coberturaTestReport") {
          dependsOn(jacocoReportTask)
          mustRunAfter(jacocoReportTask)
          group = "verification"

          doLast(CoverageConverterAction(jacocoReportTask))
        }
      }
    }
  }
}
