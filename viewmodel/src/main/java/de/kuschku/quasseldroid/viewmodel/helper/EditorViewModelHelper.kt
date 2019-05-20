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

package de.kuschku.quasseldroid.viewmodel.helper

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.syncables.AliasManager
import de.kuschku.libquassel.quassel.syncables.IrcChannel
import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helper.combineLatest
import de.kuschku.libquassel.util.helper.mapNullable
import de.kuschku.libquassel.util.helper.nullIf
import de.kuschku.libquassel.util.helper.safeSwitchMap
import de.kuschku.quasseldroid.util.emoji.EmojiData
import de.kuschku.quasseldroid.viewmodel.ChatViewModel
import de.kuschku.quasseldroid.viewmodel.EditorViewModel
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import de.kuschku.quasseldroid.viewmodel.data.AutoCompleteItem
import de.kuschku.quasseldroid.viewmodel.data.BufferStatus
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

open class EditorViewModelHelper @Inject constructor(
  val editor: EditorViewModel,
  chat: ChatViewModel,
  quassel: QuasselViewModel
) : ChatViewModelHelper(chat, quassel) {
  val rawAutoCompleteData: Observable<Triple<Optional<ISession>, BufferId, Pair<String, IntRange>>> =
    combineLatest(
      connectedSession,
      chat.bufferId,
      editor.lastWord.safeSwitchMap {
        it
      }.distinctUntilChanged()
    )

  val autoCompleteData: Observable<Pair<String, List<AutoCompleteItem>>> = rawAutoCompleteData
    .distinctUntilChanged()
    .debounce(300, TimeUnit.MILLISECONDS)
    .safeSwitchMap { (sessionOptional, id, lastWord) ->
      val session = sessionOptional.orNull()
      val bufferSyncer = session?.bufferSyncer
      val bufferInfo = bufferSyncer?.bufferInfo(id)
      if (bufferSyncer != null) {
        session.liveNetworks().safeSwitchMap { networks ->
          bufferSyncer.liveBufferInfos().safeSwitchMap { infos ->
            session.aliasManager.updates().map(AliasManager::aliasList).safeSwitchMap { aliases ->
              val network = networks[bufferInfo?.networkId] ?: Network.NULL
              val ircChannel = if (bufferInfo?.type?.hasFlag(Buffer_Type.ChannelBuffer) == true) {
                network.ircChannel(bufferInfo.bufferName) ?: IrcChannel.NULL
              } else IrcChannel.NULL
              ircChannel.liveIrcUsers().safeSwitchMap { users ->
                fun filterStart(name: String): Boolean {
                  return name.trimStart(*IGNORED_CHARS)
                    .startsWith(
                      lastWord.first.trimStart(*IGNORED_CHARS),
                      ignoreCase = true
                    )
                }

                fun filter(name: String): Boolean {
                  return name.trim(*IGNORED_CHARS)
                    .contains(
                      lastWord.first.trim(*IGNORED_CHARS),
                      ignoreCase = true
                    )
                }

                fun processResults(results: List<Observable<out AutoCompleteItem>>) =
                  combineLatest<AutoCompleteItem>(results)
                    .map { list ->
                      Pair(
                        lastWord.first,
                        list.sorted()
                      )
                    }

                fun getAliases() = aliases.filter {
                  filterStart(it.name ?: "")
                }.map {
                  Observable.just(AutoCompleteItem.AliasItem(
                    "/${it.name}",
                    it.expansion
                  ))
                }

                fun getBuffers() = infos.values.filter {
                  filterStart(it.bufferName ?: "")
                }.filter {
                  it.type.toInt() == Buffer_Type.ChannelBuffer.toInt()
                }.mapNotNull { info ->
                  networks[info.networkId]?.let { info to it }
                }.map { (info, network) ->
                  network.liveIrcChannel(
                    info.bufferName
                  ).safeSwitchMap { channel ->
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

                fun getUsers(): Set<IrcUser> = when {
                  bufferInfo?.type?.hasFlag(Buffer_Type.ChannelBuffer) == true ->
                    users
                  bufferInfo?.type?.hasFlag(Buffer_Type.QueryBuffer) == true   ->
                    network.ircUser(bufferInfo.bufferName).nullIf { it == IrcUser.NULL }?.let {
                      setOf(it)
                    } ?: emptySet()
                  else                                                         ->
                    emptySet()
                }

                fun getNicks() = getUsers().filter {
                  filterStart(it.nick())
                }.map<IrcUser, Observable<AutoCompleteItem.UserItem>> {
                  it.updates().map { user ->
                    val userModes = ircChannel.userModes(user)
                    val prefixModes = network.prefixModes()

                    val lowestMode = userModes.mapNotNull(prefixModes::indexOf).min()
                                     ?: prefixModes.size

                    AutoCompleteItem.UserItem(
                      user.nick(),
                      user.hostMask(),
                      network.modesToPrefixes(userModes),
                      lowestMode,
                      user.realName(),
                      user.isAway(),
                      user.network().isMyNick(user.nick()),
                      network.support("CASEMAPPING")
                    )
                  }
                }

                fun getEmojis() = EmojiData.processedEmojiMap.filter {
                  it.shortCodes.any {
                    it.contains(lastWord.first.trim(':'))
                  }
                }.map {
                  Observable.just(it)
                }

                processResults(when (lastWord.first.firstOrNull()) {
                                 '/'  -> getAliases()
                                 '@'  -> getNicks()
                                 '#'  -> getBuffers()
                                 ':'  -> getEmojis()
                                 else -> getAliases() + getNicks() + getBuffers() + getEmojis()
                               })
              }
            }
          }
        }
      } else {
        Observable.just(Pair(lastWord.first, emptyList()))
      }
    }

  companion object {
    val IGNORED_CHARS = charArrayOf(
      '!', '"', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=',
      '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'
    )
  }
}
