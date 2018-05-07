/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.viewmodel

import android.arch.lifecycle.ViewModel
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helpers.mapNullable
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.viewmodel.data.AutoCompleteItem
import de.kuschku.quasseldroid.viewmodel.data.BufferStatus
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class EditorViewModel : ViewModel() {
  val quasselViewModel = BehaviorSubject.create<QuasselViewModel>()

  val session = quasselViewModel.switchMap(QuasselViewModel::session)
  val buffer = quasselViewModel.switchMap(QuasselViewModel::buffer)

  val lastWord = BehaviorSubject.create<Observable<Pair<String, IntRange>>>()

  val rawAutoCompleteData: Observable<Triple<Optional<ISession>, Int, Pair<String, IntRange>>> =
    combineLatest(session, buffer, lastWord).switchMap { (sessionOptional, id, lastWordWrapper) ->
      lastWordWrapper
        .distinctUntilChanged()
        .map { lastWord ->
          Triple(sessionOptional, id, lastWord)
        }
    }

  val autoCompleteData = rawAutoCompleteData
    .distinctUntilChanged()
    .debounce(300, TimeUnit.MILLISECONDS)
    .switchMap { (sessionOptional, id, lastWord) ->
      val session = sessionOptional.orNull()
      val bufferSyncer = session?.bufferSyncer
      val bufferInfo = bufferSyncer?.bufferInfo(id)
      if (bufferSyncer != null) {
        session.liveNetworks().switchMap { networks ->
          bufferSyncer.liveBufferInfos().switchMap { infos ->
            if (bufferInfo?.type?.hasFlag(Buffer_Type.ChannelBuffer) == true) {
              val network = networks[bufferInfo.networkId]
              val ircChannel = network?.ircChannel(
                bufferInfo.bufferName
              )
              if (ircChannel != null) {
                ircChannel.liveIrcUsers().switchMap { users ->
                  val buffers: List<Observable<AutoCompleteItem.ChannelItem>?> = infos.values
                    .filter {
                      it.type.toInt() == Buffer_Type.ChannelBuffer.toInt()
                    }.mapNotNull { info ->
                      networks[info.networkId]?.let { info to it }
                    }.map { (info, network) ->
                      network.liveIrcChannel(
                        info.bufferName
                      ).switchMap { channel ->
                        channel.updates().mapNullable(IrcChannel.NULL) {
                          AutoCompleteItem.ChannelItem(
                            info = info,
                            network = network.networkInfo(),
                            bufferStatus = when (it) {
                              null -> BufferStatus.OFFLINE
                              else -> BufferStatus.ONLINE
                            },
                            description = it?.topic() ?: ""
                          )
                        }
                      }
                    }
                  val nicks = users.map<IrcUser, Observable<AutoCompleteItem.UserItem>?> {
                    it.updates().map { user ->
                      val userModes = ircChannel.userModes(user)
                      val prefixModes = network.prefixModes()

                      val lowestMode = userModes.mapNotNull(prefixModes::indexOf).min()
                                       ?: prefixModes.size

                      AutoCompleteItem.UserItem(
                        user.nick(),
                        network.modesToPrefixes(userModes),
                        lowestMode,
                        user.realName(),
                        user.isAway(),
                        user.network().isMyNick(user.nick()),
                        network.support("CASEMAPPING")
                      )
                    }
                  }

                  combineLatest<AutoCompleteItem>(nicks + buffers)
                    .map { list ->
                      val ignoredStartingCharacters = charArrayOf(
                        '-', '_', '[', ']', '{', '}', '|', '`', '^', '.', '\\', '@'
                      )

                      Pair(
                        lastWord.first,
                        list.filter {
                          it.name.trimStart(*ignoredStartingCharacters)
                            .startsWith(
                              lastWord.first.trimStart(*ignoredStartingCharacters),
                              ignoreCase = true
                            )
                        }.sorted()
                      )
                    }
                }
              } else {
                Observable.just(Pair(lastWord.first, emptyList()))
              }
            } else {
              Observable.just(Pair(lastWord.first, emptyList()))
            }
          }
        }
      } else {
        Observable.just(Pair(lastWord.first, emptyList()))
      }
    }
}
