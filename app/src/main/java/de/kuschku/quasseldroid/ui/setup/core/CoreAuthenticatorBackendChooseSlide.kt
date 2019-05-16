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

package de.kuschku.quasseldroid.ui.setup.core

import de.kuschku.libquassel.protocol.coresetup.CoreSetupData
import de.kuschku.quasseldroid.R

class CoreAuthenticatorBackendChooseSlide : CoreBackendChooseSlide() {
  override val title = R.string.slide_core_authenticator_select_title
  override val description = R.string.slide_core_authenticator_select_description

  override val inputKey = CoreSetupData::authenticatorInfo
  override val outputKey = "authenticator"
}
