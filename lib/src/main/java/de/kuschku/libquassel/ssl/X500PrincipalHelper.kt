/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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

package de.kuschku.libquassel.ssl

import javax.security.auth.x500.X500Principal

val X500Principal.commonName
  get() = commonName(name)

fun commonName(distinguishedName: String) =
  X509Helper.COMMON_NAME.find(distinguishedName)?.groups?.get(1)?.value

val X500Principal.organization
  get() = organization(name)

fun organization(distinguishedName: String) =
  X509Helper.ORGANIZATION.find(distinguishedName)?.groups?.get(1)?.value

val X500Principal.organizationalUnit
  get() = organizationalUnit(name)

fun organizationalUnit(distinguishedName: String) =
  X509Helper.ORGANIZATIONAL_UNIT.find(distinguishedName)?.groups?.get(1)?.value
