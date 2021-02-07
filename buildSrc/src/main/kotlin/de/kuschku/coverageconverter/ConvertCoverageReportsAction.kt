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

package de.kuschku.coverageconverter

import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.io.File

internal class ConvertCoverageReportsAction(
  private val jacocoReportTask: JacocoReport
) : Action<Task> {
  private fun findOutputFile(jacocoFile: File): File? {
    val actualFile = jacocoFile.absoluteFile
    if (actualFile.exists() && actualFile.parentFile.name == "jacoco") {
      val folder = File(actualFile.parentFile.parentFile, "cobertura")
      folder.mkdirs()
      return File(folder, actualFile.name)
    }

    return null
  }

  private fun createPythonScript(name: String, temporaryDir: File): File {
    val file = File(temporaryDir, name)
    if (file.exists()) {
      file.delete()
    }
    val source = CoverageConverterPlugin::class.java.getResourceAsStream("/coverageconverter/$name")
    file.writeBytes(source.readAllBytes())
    return file
  }


  override fun execute(t: Task) {
    val cover2coverScript = createPythonScript("cover2cover.py", t.temporaryDir)
    val source2filenameScript = createPythonScript("source2filename.py", t.temporaryDir)

    fun cover2cover(reportFile: File, outputFile: File, sourceDirectories: Iterable<File>) {
      t.project.exec {
        commandLine("python3")
        args(cover2coverScript.absolutePath)
        args(reportFile.absolutePath)
        args(sourceDirectories.map(File::getAbsolutePath))
        standardOutput = outputFile.outputStream()
      }
    }

    fun source2filename(reportFile: File) {
      t.project.exec {
        commandLine("python3")
        args(source2filenameScript.absolutePath)
        args(reportFile.absolutePath)
      }
    }

    jacocoReportTask.reports.forEach {
      if (it.isEnabled && it.destination.extension == "xml") {
        val outputFile = findOutputFile(it.destination)
        if (outputFile != null) {
          cover2cover(it.destination, outputFile, jacocoReportTask.sourceDirectories)
          source2filename(outputFile)
        }
      }
    }
  }
}
