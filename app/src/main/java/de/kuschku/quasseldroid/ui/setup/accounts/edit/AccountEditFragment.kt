/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.quasseldroid.ui.setup.accounts.edit

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.textfield.TextInputLayout
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.dao.findById
import de.kuschku.quasseldroid.persistence.db.AccountDatabase
import de.kuschku.quasseldroid.persistence.models.Account
import de.kuschku.quasseldroid.persistence.util.AccountId
import de.kuschku.quasseldroid.util.Patterns
import de.kuschku.quasseldroid.util.TextValidator
import de.kuschku.quasseldroid.util.helper.editCommit
import de.kuschku.quasseldroid.util.ui.settings.fragment.Changeable
import de.kuschku.quasseldroid.util.ui.settings.fragment.Deletable
import de.kuschku.quasseldroid.util.ui.settings.fragment.Savable
import de.kuschku.quasseldroid.util.ui.settings.fragment.SettingsFragment
import javax.inject.Inject

class AccountEditFragment : SettingsFragment(), Changeable, Savable, Deletable {
  @BindView(R.id.nameWrapper)
  lateinit var nameWrapper: TextInputLayout
  @BindView(R.id.name)
  lateinit var name: EditText

  @BindView(R.id.hostWrapper)
  lateinit var hostWrapper: TextInputLayout
  @BindView(R.id.host)
  lateinit var host: EditText

  @BindView(R.id.portWrapper)
  lateinit var portWrapper: TextInputLayout
  @BindView(R.id.port)
  lateinit var port: EditText

  @BindView(R.id.require_ssl)
  lateinit var requireSsl: SwitchCompat

  @BindView(R.id.userWrapper)
  lateinit var userWrapper: TextInputLayout
  @BindView(R.id.user)
  lateinit var user: EditText

  @BindView(R.id.passWrapper)
  lateinit var passWrapper: TextInputLayout
  @BindView(R.id.pass)
  lateinit var pass: EditText

  @Inject
  lateinit var database: AccountDatabase

  private var account: Account? = null
  private var accountId: AccountId = AccountId(-1L)

  private lateinit var handlerThread: HandlerThread
  private lateinit var handler: Handler

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handlerThread = HandlerThread("accountEdit")
    handlerThread.start()
    handler = Handler(handlerThread.looper)
  }

  override fun onDestroy() {
    super.onDestroy()
    handlerThread.quit()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.setup_account_edit, container, false)
    ButterKnife.bind(this, view)

    setHasOptionsMenu(true)

    handler.post {
      accountId = AccountId(arguments?.getLong("account", -1L) ?: -1L)
      if (!accountId.isValidId()) {
        activity?.setResult(Activity.RESULT_CANCELED)
        activity?.finish()
      }
      account = database.accounts().findById(accountId)
      if (account == null) {
        activity?.setResult(Activity.RESULT_CANCELED)
        activity?.finish()
      }

      name.setText(account?.name)
      host.setText(account?.host)
      port.setText(account?.port?.toString())
      requireSsl.isChecked = account?.requireSsl == true
      user.setText(account?.user)
      pass.setText(account?.pass)
    }

    nameValidator = object : TextValidator(
      activity, nameWrapper::setError, resources.getString(R.string.hint_invalid_name)
    ) {
      override fun validate(text: Editable) = text.isNotBlank()
    }

    hostValidator = object : TextValidator(
      activity, hostWrapper::setError, resources.getString(R.string.hint_invalid_host)
    ) {
      override fun validate(text: Editable) = text.toString().matches(Patterns.DOMAIN_NAME)
    }

    portValidator = object : TextValidator(
      activity, portWrapper::setError, resources.getString(R.string.hint_invalid_port)
    ) {
      override fun validate(text: Editable) = text.toString().toIntOrNull() in (0 until 65536)
    }

    userValidator = object : TextValidator(
      activity, userWrapper::setError, resources.getString(R.string.hint_invalid_user)
    ) {
      override fun validate(text: Editable) = text.isNotBlank()
    }

    name.addTextChangedListener(nameValidator)
    host.addTextChangedListener(hostValidator)
    port.addTextChangedListener(portValidator)
    user.addTextChangedListener(userValidator)
    nameValidator.afterTextChanged(name.text)
    hostValidator.afterTextChanged(host.text)
    portValidator.afterTextChanged(port.text)
    userValidator.afterTextChanged(user.text)
    return view
  }

  private lateinit var nameValidator: TextValidator
  private lateinit var hostValidator: TextValidator
  private lateinit var portValidator: TextValidator
  private lateinit var userValidator: TextValidator

  private val isValid
    get() = nameValidator.isValid && hostValidator.isValid && portValidator.isValid
            && userValidator.isValid

  fun applyChanges() = account?.copy(
    name = name.text.toString(),
    host = host.text.toString(),
    port = port.text.toString().toIntOrNull() ?: 4242,
    requireSsl = requireSsl.isChecked,
    user = user.text.toString(),
    pass = pass.text.toString()
  )

  override fun onSave() = applyChanges()?.let {
    handler.post {
      database.accounts().save(it)
    }
    true
  } ?: false

  override fun onDelete() {
    handler.post {
      account?.let {
        val preferences = activity?.getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE)
        if (AccountId(preferences?.getLong(Keys.Status.selectedAccount, -1) ?: -1) == it.id) {
          preferences?.editCommit {
            remove(Keys.Status.selectedAccount)
          }
        }
        database.accounts().delete(it)
      }
    }
  }

  override fun hasChanged() = account != applyChanges()
}
