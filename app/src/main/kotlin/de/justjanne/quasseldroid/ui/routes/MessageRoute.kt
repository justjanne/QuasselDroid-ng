package de.justjanne.quasseldroid.ui.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.navigation.NavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import androidx.room.Room
import de.justjanne.bitflags.of
import de.justjanne.libquassel.irc.HostmaskHelper
import de.justjanne.libquassel.irc.IrcFormat
import de.justjanne.libquassel.irc.IrcFormatDeserializer
import de.justjanne.libquassel.protocol.models.flags.MessageType
import de.justjanne.libquassel.protocol.models.ids.BufferId
import de.justjanne.libquassel.protocol.models.ids.MsgId
import de.justjanne.libquassel.protocol.util.flatMap
import de.justjanne.quasseldroid.R
import de.justjanne.quasseldroid.persistence.AppDatabase
import de.justjanne.quasseldroid.persistence.QuasselRemoteMediator
import de.justjanne.quasseldroid.service.QuasselBackend
import de.justjanne.quasseldroid.ui.components.MessageBase
import de.justjanne.quasseldroid.ui.components.MessageBaseSmall
import de.justjanne.quasseldroid.ui.components.MessageDayChangeView
import de.justjanne.quasseldroid.ui.components.MessagePlaceholder
import de.justjanne.quasseldroid.ui.components.NewMessageView
import de.justjanne.quasseldroid.ui.components.buildNick
import de.justjanne.quasseldroid.ui.theme.QuasselTheme
import de.justjanne.quasseldroid.ui.theme.Typography
import de.justjanne.quasseldroid.util.extensions.format
import de.justjanne.quasseldroid.util.extensions.getPrevious
import de.justjanne.quasseldroid.util.format.IrcFormatRenderer
import de.justjanne.quasseldroid.util.format.TextFormatter
import de.justjanne.quasseldroid.util.mapNullable
import de.justjanne.quasseldroid.util.rememberFlow
import kotlinx.coroutines.flow.flatMapLatest
import org.threeten.bp.ZoneId

@Composable
fun MessageRoute(
  backend: QuasselBackend,
  navController: NavController,
  buffer: BufferId
) {
  val listState = rememberLazyListState()

  val context = LocalContext.current.applicationContext

  val database = remember {
    Room.databaseBuilder(
      context,
      AppDatabase::class.java,
      "app"
    ).build()
  }

  val pageSize = 50
  val limit = 200

  val messages = remember {
    backend.flow()
      .flatMap()
      .mapNullable { it.backlogManager }
      .flatMapLatest { backlogManager ->
        Pager(
          PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = true,
            maxSize = limit
          ),
          remoteMediator = backlogManager?.let {
            QuasselRemoteMediator(
              bufferId = buffer,
              database = database,
              backlogManager = it,
              pageSize = pageSize
            )
          }
        ) {
          database.messageDao().pagingSource(buffer.id)
        }.flow
      }
  }.collectAsLazyPagingItems()

  val markerLine: MsgId? = rememberFlow(null) {
    backend.flow()
      .flatMap()
      .mapNullable { it.bufferSyncer }
      .flatMap()
      .mapNullable { it.markerLines[buffer] }
  }

  Column {
    Row {
      Button(onClick = { navController.navigate("home") }) {
        Text("Back")
      }
    }
    LazyColumn(state = listState) {
      itemsIndexed(messages, key = { _, item -> item.messageId }) { index, model ->
        if (model == null) {
          MessagePlaceholder()
        } else {
          val message = model.toMessage()
          val prev = messages.itemSnapshotList.getPrevious(index)?.toMessage()
          val prevDate = prev?.time?.atZone(ZoneId.systemDefault())?.toLocalDate()
          val messageDate = message.time.atZone(ZoneId.systemDefault()).toLocalDate()

          val followUp = prev != null &&
            message.sender == prev.sender &&
            message.senderPrefixes == prev.senderPrefixes &&
            message.realName == prev.realName &&
            message.avatarUrl == prev.avatarUrl

          val isNew = (markerLine != null && (prev == null || prev.messageId <= markerLine)) &&
            message.messageId > markerLine

          val parsed = IrcFormatDeserializer.parse(message.content)

          if (prevDate == null || !messageDate.isEqual(prevDate)) {
            MessageDayChangeView(messageDate, isNew)
          } else if (isNew) {
            NewMessageView()
          }

          when (message.type) {
            MessageType.of(MessageType.Plain) -> {
              MessageBase(message, followUp) {
                Text(IrcFormatRenderer.render(parsed), style = Typography.body2)
              }
            }
            MessageType.of(MessageType.Action) -> {
              MessageBaseSmall(message) {
                val nick = HostmaskHelper.nick(message.sender)

                Text(
                  TextFormatter.format(
                    AnnotatedString(stringResource(R.string.message_format_action)),
                    buildNick(nick, message.senderPrefixes),
                    IrcFormatRenderer.render(
                      data = parsed.map {
                        it.copy(style = it.style.flipFlag(IrcFormat.Flag.ITALIC))
                      }
                    )
                  ),
                  style = Typography.body2,
                  color = QuasselTheme.chat.onAction
                )
              }
            }
          }
        }
      }
    }
  }
}


