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

package de.justjanne.gitversion

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class GitVersionPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.run {
      this.extensions.getByType<BaseAppModuleExtension>().run {
        defaultConfig {
          versionCode = gitVersionCode() ?: 1
          versionName = gitVersionName() ?: "1.0.0"

          setBuildConfigField(
            ::buildConfigField,
            "GIT_HEAD",
            gitHead() ?: ""
          )
          setBuildConfigField(
            ::buildConfigField,
            "FANCY_VERSION_NAME",
            fancyVersionName() ?: "1.0.0"
          )
          setBuildConfigField(
            ::buildConfigField,
            "GIT_COMMIT_DATE",
            gitCommitDate()
          )

          setProperty("archivesBaseName", "${rootProject.name}-$versionName")
        }
      }
    }
  }
}
