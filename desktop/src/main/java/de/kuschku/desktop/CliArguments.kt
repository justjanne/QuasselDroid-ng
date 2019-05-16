/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
 * Copyright (c) 2019 The Quassel Project
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

package de.kuschku.desktop

import picocli.CommandLine

class CliArguments {
  @CommandLine.Option(names = ["--user"])
  var user: String = ""
  @CommandLine.Option(names = ["--pass"])
  var pass: String = ""
  @CommandLine.Option(names = ["--host"])
  var host: String = ""
  @CommandLine.Option(names = ["--port"])
  var port: Int = 4242

  override fun toString(): String {
    return "CliArguments(user='$user', pass='$pass', host='$host', port='$port')"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as CliArguments

    if (user != other.user) return false
    if (pass != other.pass) return false
    if (host != other.host) return false
    if (port != other.port) return false

    return true
  }

  override fun hashCode(): Int {
    var result = user.hashCode()
    result = 31 * result + pass.hashCode()
    result = 31 * result + host.hashCode()
    result = 31 * result + port.hashCode()
    return result
  }
}
