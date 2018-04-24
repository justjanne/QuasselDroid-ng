/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Message

interface BacklogStorage {
  fun updateIgnoreRules(session: Session)

  fun storeMessages(session: Session, vararg messages: Message, initialLoad: Boolean = false)
  fun storeMessages(session: Session, messages: Iterable<Message>, initialLoad: Boolean = false)

  fun clearMessages(bufferId: BufferId, idRange: IntRange)

  fun clearMessages(bufferId: BufferId)

  fun clearMessages()
}
