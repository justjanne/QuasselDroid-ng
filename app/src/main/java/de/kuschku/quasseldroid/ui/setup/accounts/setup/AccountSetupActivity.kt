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

package de.kuschku.quasseldroid.ui.setup.accounts.setup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import de.kuschku.quasseldroid.persistence.AccountDatabase
import de.kuschku.quasseldroid.ui.setup.SetupActivity
import de.kuschku.quasseldroid.util.AndroidHandlerThread
import org.threeten.bp.Instant
import javax.inject.Inject

class AccountSetupActivity : SetupActivity() {
  private val handler = AndroidHandlerThread("Setup")

  @Inject
  lateinit var database: AccountDatabase

  override fun onDone(data: Bundle) {
    val account = AccountDatabase.Account(
      id = 0,
      host = data.getString("host"),
      port = data.getInt("port"),
      user = data.getString("user"),
      pass = data.getString("pass"),
      name = data.getString("name"),
      lastUsed = Instant.now().epochSecond
    )
    handler.post {
      database.accounts().create(account)
      runOnUiThread {
        setResult(Activity.RESULT_OK)
        finish()
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    handler.onCreate()
    super.onCreate(savedInstanceState)
  }

  override fun onDestroy() {
    handler.onDestroy()
    super.onDestroy()
  }

  override val fragments = listOf(
    AccountSetupConnectionSlide(),
    AccountSetupUserSlide(),
    AccountSetupNameSlide()
  )

  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, AccountSetupActivity::class.java)
  }
}
