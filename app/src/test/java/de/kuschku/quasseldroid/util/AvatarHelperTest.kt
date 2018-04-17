package de.kuschku.quasseldroid.util

import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.MessageSettings
import org.junit.Test
import org.threeten.bp.Instant

class AvatarHelperTest {
  @Test
  fun testGravatar() {
    val message = QuasselDatabase.DatabaseMessage(
      messageId = 1,
      time = Instant.now(),
      type = Message_Type.of(Message_Type.Plain).toInt(),
      flag = Message_Flag.of().toInt(),
      bufferId = 0,
      sender = "justJanne",
      senderPrefixes = "",
      realName = "Janne Koschinski <janne@kuschku.de>",
      avatarUrl = "",
      content = "Lorem Ipsum I Dolor Sit Amet",
      ignored = false
    )

    assert(
      AvatarHelper.avatar(
        MessageSettings(
          showGravatarAvatars = true,
          showIRCCloudAvatars = true
        ),
        message
      ).contains("https://www.gravatar.com/avatar/81128f11cae692bc486e3f88b854ddf1?d=404")
    )

    assert(
      AvatarHelper.avatar(
        MessageSettings(
          showGravatarAvatars = false,
          showIRCCloudAvatars = false
        ),
        message
      ).isEmpty()
    )
  }

  @Test
  fun testIrcCloud() {
    val message = QuasselDatabase.DatabaseMessage(
      messageId = 1,
      time = Instant.now(),
      type = Message_Type.of(Message_Type.Plain).toInt(),
      flag = Message_Flag.of().toInt(),
      bufferId = 0,
      sender = "jwheare!sid2@irccloud.com",
      senderPrefixes = "",
      realName = "James Wheare",
      avatarUrl = "",
      content = "Lorem Ipsum I Dolor Sit Amet",
      ignored = false
    )

    assert(
      AvatarHelper.avatar(
        MessageSettings(
          showGravatarAvatars = true,
          showIRCCloudAvatars = true
        ),
        message
      ).contains("https://static.irccloud-cdn.com/avatar-redirect/2")
    )

    assert(
      AvatarHelper.avatar(
        MessageSettings(
          showGravatarAvatars = false,
          showIRCCloudAvatars = false
        ),
        message
      ).isEmpty()
    )
  }

  @Test
  fun testActualAvatars() {
    val message = QuasselDatabase.DatabaseMessage(
      messageId = 1,
      time = Instant.now(),
      type = Message_Type.of(Message_Type.Plain).toInt(),
      flag = Message_Flag.of().toInt(),
      bufferId = 0,
      sender = "jwheare!sid2@irccloud.com",
      senderPrefixes = "",
      realName = "James Wheare",
      avatarUrl = "https://quasseldroid.info/favicon.png",
      content = "Lorem Ipsum I Dolor Sit Amet",
      ignored = false
    )

    assert(
      AvatarHelper.avatar(
        MessageSettings(
          showGravatarAvatars = true,
          showIRCCloudAvatars = true
        ),
        message
      ).contains("https://quasseldroid.info/favicon.png")
    )

    assert(
      AvatarHelper.avatar(
        MessageSettings(
          showGravatarAvatars = false,
          showIRCCloudAvatars = false
        ),
        message
      ) == listOf("https://quasseldroid.info/favicon.png")
    )
  }
}
