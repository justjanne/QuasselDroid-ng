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

package de.kuschku.quasseldroid.ui.coresettings.passwordchange

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.textfield.TextInputLayout
import de.kuschku.libquassel.quassel.syncables.RpcHandler
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.helpers.mapMapNullable
import de.kuschku.libquassel.util.helpers.mapSwitchMap
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.AccountDatabase
import de.kuschku.quasseldroid.util.TextValidator
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import javax.inject.Inject

class PasswordChangeFragment : ServiceBoundFragment() {
  @BindView(R.id.user)
  lateinit var user: EditText

  @BindView(R.id.password_old_wrapper)
  lateinit var oldPasswordWrapper: TextInputLayout

  @BindView(R.id.password_old)
  lateinit var oldPassword: EditText

  @BindView(R.id.password_new)
  lateinit var newPassword: EditText

  @BindView(R.id.password_repeat_wrapper)
  lateinit var repeatPasswordWrapper: TextInputLayout

  @BindView(R.id.password_repeat)
  lateinit var repeatPassword: EditText

  @BindView(R.id.error)
  lateinit var error: TextView

  @BindView(R.id.save)
  lateinit var save: Button

  @BindView(R.id.progress)
  lateinit var progress: MaterialProgressBar

  @Inject
  lateinit var accountDatabase: AccountDatabase

  private var waiting: AccountDatabase.Account? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_passwordchange, container, false)
    ButterKnife.bind(this, view)

    val account = accountDatabase.accounts().findById(accountId)

    user.setText(account?.user)

    viewModel.session
      .mapMapNullable(ISession::rpcHandler)
      .mapSwitchMap(RpcHandler::passwordChanged)
      .filter(Optional<Boolean>::isPresent)
      .map(Optional<Boolean>::get)
      .toLiveData().observe(this, Observer {
        val waiting = this.waiting
        if (waiting != null) {
          if (it) {
            save.setText(R.string.label_save)
            save.isEnabled = true
            save.isClickable = true
            progress.visibility = View.GONE
            error.visibility = View.GONE
            runInBackground {
              accountDatabase.accounts().save(waiting)
            }
            this.waiting = null
            activity?.finish()
          } else {
            error.visibility = View.VISIBLE
          }
        }
      })

    oldPassword.addTextChangedListener(object : TextValidator(
      activity,
      oldPasswordWrapper::setError,
      getString(R.string.label_password_error_wrong)
    ) {
      override fun validate(text: Editable) = text.toString() == account?.pass
    })

    repeatPassword.addTextChangedListener(object : TextValidator(
      activity,
      repeatPasswordWrapper::setError,
      getString(R.string.label_password_error_nomatch)
    ) {
      override fun validate(text: Editable) = text.toString() == newPassword.text.toString()
    })

    save.setOnClickListener {
      save.setText(R.string.label_saving)
      save.isEnabled = false
      save.isClickable = false
      progress.visibility = View.VISIBLE
      error.visibility = View.GONE

      val pass = newPassword.text.toString()

      waiting = account?.copy(pass = pass)

      viewModel.session.value?.orNull()?.rpcHandler?.changePassword(0L,
                                                                    user.text.toString(),
                                                                    oldPassword.text.toString(),
                                                                    pass)
    }

    return view
  }
}
