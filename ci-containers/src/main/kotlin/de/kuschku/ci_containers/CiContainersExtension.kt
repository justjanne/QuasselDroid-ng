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

package de.kuschku.ci_containers

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class CiContainersExtension : BeforeEachCallback, AfterEachCallback {
  private fun getContainers(context: ExtensionContext?): List<ProvidedContainer> {
    val containers = mutableListOf<ProvidedContainer>()
    context?.requiredTestInstances?.allInstances?.forEach { instance ->
      instance.javaClass.declaredFields.map { field ->
        if (field.type == ProvidedContainer::class.java) {
          field.trySetAccessible()
          containers.add(field.get(instance) as ProvidedContainer)
        }
      }
    }
    return containers
  }

  override fun beforeEach(context: ExtensionContext?) {
    getContainers(context).forEach(ProvidedContainer::start)
  }

  override fun afterEach(context: ExtensionContext?) {
    getContainers(context).forEach(ProvidedContainer::stop)
  }
}
