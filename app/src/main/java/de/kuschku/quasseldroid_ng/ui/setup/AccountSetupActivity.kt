package de.kuschku.quasseldroid_ng.ui.setup

import android.os.Bundle
import de.kuschku.quasseldroid_ng.ui.setup.slides.AccountSetupConnectionSlide
import de.kuschku.quasseldroid_ng.ui.setup.slides.AccountSetupNameSlide
import de.kuschku.quasseldroid_ng.ui.setup.slides.AccountSetupUserSlide

class AccountSetupActivity : SetupActivity() {
  override fun onDone(data: Bundle) {
  }

  override val fragments = listOf(
    AccountSetupConnectionSlide(),
    AccountSetupUserSlide(),
    AccountSetupNameSlide()
  )
}
