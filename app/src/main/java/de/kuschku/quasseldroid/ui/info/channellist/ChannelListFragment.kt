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

package de.kuschku.quasseldroid.ui.info.channellist

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.protocol.QStringList
import de.kuschku.libquassel.quassel.syncables.IrcListHelper
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.helpers.mapSwitchMap
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.retint
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.ui.settings.fragment.ServiceBoundSettingsFragment
import de.kuschku.quasseldroid.util.ui.view.MaterialContentLoadingProgressBar
import de.kuschku.quasseldroid.util.ui.view.WarningBarView
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class ChannelListFragment : ServiceBoundSettingsFragment() {
  @BindView(R.id.search_input)
  lateinit var searchInput: EditText

  @BindView(R.id.search_button)
  lateinit var searchButton: AppCompatImageButton

  @BindView(R.id.progress)
  lateinit var progress: MaterialContentLoadingProgressBar

  @BindView(R.id.search_results)
  lateinit var searchResults: RecyclerView

  @BindView(R.id.error)
  lateinit var errorDisplay: WarningBarView

  @Inject
  lateinit var adapter: ChannelListAdapter

  private var query: Query? = null
  private var state: State = State()

  data class Query(
    val networkId: NetworkId,
    val filters: QStringList
  )

  data class State(
    val loading: Boolean = false,
    val error: String? = null
  )

  fun updateState(loading: Boolean = state.loading, error: String? = state.error) {
    fun internalUpdateState(state: State) {
      if (state.error != null) {
        errorDisplay.setText(state.error)
        errorDisplay.setMode(WarningBarView.MODE_ICON)
      } else {
        errorDisplay.setMode(WarningBarView.MODE_NONE)
      }

      if (state.loading) {
        progress.show()
      } else {
        progress.hide()
      }
    }

    val newState = State(loading, error)
    if (newState != state) {
      state = newState
      internalUpdateState(newState)
    }
  }

  val results = BehaviorSubject.createDefault(emptyList<IrcListHelper.ChannelDescription>())
  val sort = BehaviorSubject.createDefault(Sort(Sort.Field.CHANNEL_NAME, Sort.Direction.ASC))

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.info_channellist, container, false)
    ButterKnife.bind(this, view)

    val networkId = NetworkId(arguments?.getInt("network_id", -1) ?: -1)

    searchResults.adapter = adapter
    searchResults.layoutManager = LinearLayoutManager(view.context)
    searchResults.itemAnimator = DefaultItemAnimator()

    combineLatest(results, sort).toLiveData().observe(this, Observer { (results, sort) ->
      adapter.submitList(results.let {
        when (sort.field) {
          Sort.Field.CHANNEL_NAME -> {
            when (sort.direction) {
              Sort.Direction.ASC  ->
                it.sortedBy(IrcListHelper.ChannelDescription::channelName)
              Sort.Direction.DESC ->
                it.sortedByDescending(IrcListHelper.ChannelDescription::channelName)
            }
          }
          Sort.Field.USER_COUNT   -> {
            when (sort.direction) {
              Sort.Direction.ASC  ->
                it.sortedBy(IrcListHelper.ChannelDescription::userCount)
              Sort.Direction.DESC ->
                it.sortedByDescending(IrcListHelper.ChannelDescription::userCount)
            }
          }
          Sort.Field.TOPIC        -> {
            when (sort.direction) {
              Sort.Direction.ASC  ->
                it.sortedBy(IrcListHelper.ChannelDescription::topic)
              Sort.Direction.DESC ->
                it.sortedByDescending(IrcListHelper.ChannelDescription::topic)
            }
          }
        }
      })
    })

    viewModel.ircListHelper
      .mapSwitchMap(IrcListHelper::observable)
      .filter(Optional<IrcListHelper.Event>::isPresent)
      .map(Optional<IrcListHelper.Event>::get)
      .toLiveData().observe(this, Observer {
        when (it) {
          is IrcListHelper.Event.ChannelList -> {
            if (it.netId == query?.networkId) {
              results.onNext(it.data)
            }
          }
          is IrcListHelper.Event.Finished    -> {
            if (it.netId == query?.networkId) {
              updateState(false, null)
            }
          }
          is IrcListHelper.Event.Error       -> {
            updateState(false, it.error)
          }
        }
      })

    searchButton.setOnClickListener {
      viewModel.ircListHelper.value?.orNull()?.let { ircListHelper ->
        val query = Query(
          networkId,
          listOf(searchInput.text.toString())
        )

        ircListHelper.requestChannelList(networkId, query.filters)
        updateState(true, null)
        results.onNext(emptyList())
        this.query = query
      }
    }
    return view
  }

  override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
    inflater?.inflate(R.menu.activity_channellist, menu)
    (activity as? AppCompatActivity)?.supportActionBar?.themedContext?.let {
      menu?.retint(it)
    }
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    R.id.channel_name_asc  -> {
      sort.onNext(Sort(Sort.Field.CHANNEL_NAME, Sort.Direction.ASC))
      true
    }
    R.id.channel_name_desc -> {
      sort.onNext(Sort(Sort.Field.CHANNEL_NAME, Sort.Direction.DESC))
      true
    }
    R.id.user_count_asc    -> {
      sort.onNext(Sort(Sort.Field.USER_COUNT, Sort.Direction.ASC))
      true
    }
    R.id.user_count_desc   -> {
      sort.onNext(Sort(Sort.Field.USER_COUNT, Sort.Direction.DESC))
      true
    }
    R.id.topic_asc         -> {
      sort.onNext(Sort(Sort.Field.TOPIC, Sort.Direction.ASC))
      true
    }
    R.id.topic_desc        -> {
      sort.onNext(Sort(Sort.Field.TOPIC, Sort.Direction.DESC))
      true
    }
    else                   -> super.onOptionsItemSelected(item)
  }
}
