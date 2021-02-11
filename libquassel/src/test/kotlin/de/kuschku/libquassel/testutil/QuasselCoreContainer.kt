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

package de.kuschku.libquassel.testutil

import org.slf4j.LoggerFactory
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.utility.DockerImageName

class QuasselCoreContainer : GenericContainer<QuasselCoreContainer>(
  DockerImageName.parse("k8r.eu/justjanne/quassel-docker:v0.13.1")
) {
  init {
    withExposedPorts(QUASSEL_PORT)
    withClasspathResourceMapping(
      "/quasseltest.crt",
      "/quasseltest.crt",
      BindMode.READ_WRITE)
    withEnv("SSL_CERT_FILE", "/quasseltest.crt")
    withClasspathResourceMapping(
      "/quasseltest.key",
      "/quasseltest.key",
      BindMode.READ_WRITE)
    withEnv("SSL_KEY_FILE", "/quasseltest.key")
    withEnv("CONFIG_FROM_ENVIRONMENT", "true")
    withEnv("DB_BACKEND", "SQLite")
    withEnv("AUTH_AUTHENTICATOR", "Database")
    withEnv("SSL_REQUIRED", "true")
  }

  override fun start() {
    super.start()
    followOutput(Slf4jLogConsumer(logger))
  }

  companion object {
    @JvmStatic
    private val logger = LoggerFactory.getLogger(QuasselCoreContainer::class.java)

    const val QUASSEL_PORT = 4242
  }
}
