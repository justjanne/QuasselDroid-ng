/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.kuschku.quasseldroid.util.lists

import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Helper for computing the difference between two lists via [DiffUtil] on a background
 * thread.
 *
 *
 * It can be connected to a
 * [RecyclerView.Adapter][androidx.recyclerview.widget.RecyclerView.Adapter], and will signal the
 * adapter of changes between sumbitted lists.
 *
 *
 * For simplicity, the [ListAdapter] wrapper class can often be used instead of the
 * AsyncListDiffer directly. This AsyncListDiffer can be used for complex cases, where overriding an
 * adapter base class to support asynchronous List diffing isn't convenient.
 *
 *
 * The AsyncListDiffer can consume the values from a LiveData of `List` and present the
 * data simply for an adapter. It computes differences in list contents via [DiffUtil] on a
 * background thread as new `List`s are received.
 *
 *
 * Use [.getCurrentList] to access the current List, and present its data objects. Diff
 * results will be dispatched to the ListUpdateCallback immediately before the current list is
 * updated. If you're dispatching list updates directly to an Adapter, this means the Adapter can
 * safely access list items and total size via [.getCurrentList].
 *
 *
 * A complete usage pattern with Room would look like this:
 * <pre>
 * @Dao
 * interface UserDao {
 * @Query("SELECT * FROM user ORDER BY lastName ASC")
 * public abstract LiveData&lt;List&lt;User>> usersByLastName();
 * }
 *
 * class MyViewModel extends ViewModel {
 * public final LiveData&lt;List&lt;User>> usersList;
 * public MyViewModel(UserDao userDao) {
 * usersList = userDao.usersByLastName();
 * }
 * }
 *
 * class MyActivity extends AppCompatActivity {
 * @Override
 * public void onCreate(Bundle savedState) {
 * super.onCreate(savedState);
 * MyViewModel viewModel = ViewModelProviders.of(this).get(MyViewModel.class);
 * RecyclerView recyclerView = findViewById(R.id.user_list);
 * UserAdapter adapter = new UserAdapter();
 * viewModel.usersList.observe(this, list -> adapter.submitList(list));
 * recyclerView.setAdapter(adapter);
 * }
 * }
 *
 * class UserAdapter extends RecyclerView.Adapter&lt;UserViewHolder> {
 * private final AsyncListDiffer&lt;User> mDiffer = new AsyncListDiffer(this, DIFF_CALLBACK);
 * @Override
 * public int getItemCount() {
 * return mDiffer.getCurrentList().size();
 * }
 * public void submitList(List&lt;User> list) {
 * mDiffer.submitList(list);
 * }
 * @Override
 * public void onBindViewHolder(UserViewHolder holder, int position) {
 * User user = mDiffer.getCurrentList().get(position);
 * holder.bindTo(user);
 * }
 * public static final DiffUtil.ItemCallback&lt;User> DIFF_CALLBACK
 * = new DiffUtil.ItemCallback&lt;User>() {
 * @Override
 * public boolean areItemsTheSame(
 * @NonNull User oldUser, @NonNull User newUser) {
 * // User properties may have changed if reloaded from the DB, but ID is fixed
 * return oldUser.getId() == newUser.getId();
 * }
 * @Override
 * public boolean areContentsTheSame(
 * @NonNull User oldUser, @NonNull User newUser) {
 * // NOTE: if you use equals, your object must properly override Object#equals()
 * // Incorrectly returning false here will result in too many animations.
 * return oldUser.equals(newUser);
 * }
 * }
 * }</pre>
 *
 * @param <T> Type of the lists this AsyncListDiffer will receive.
 *
 * @see DiffUtil
 *
 * @see AdapterListUpdateCallback
</T> */
class AsyncListDiffer<T : Any> {
  private val mUpdateCallback: ListUpdateCallback
  private val mUpdateFinishedCallback: ((List<T>) -> Unit)?
  private val mConfig: AsyncDifferConfig<T>

  private var mList: List<T>? = null

  /**
   * Non-null, unmodifiable version of mList.
   *
   *
   * Collections.emptyList when mList is null, wrapped by Collections.unmodifiableList otherwise
   */
  /**
   * Get the current List - any diffing to present this list has already been computed and
   * dispatched via the ListUpdateCallback.
   *
   *
   * If a `null` List, or no List has been submitted, an empty list will be returned.
   *
   *
   * The returned list may not be mutated - mutations to content must be done through
   * [.submitList].
   *
   * @return current List.
   */
  var currentList = emptyList<T>()
    private set

  // Max generation of currently scheduled runnable
  private var mMaxScheduledGeneration: Int = 0

  /**
   * Convenience for
   * `AsyncListDiffer(new AdapterListUpdateCallback(adapter),
   * new AsyncDifferConfig.Builder().setDiffCallback(diffCallback).build());`
   *
   * @param adapter Adapter to dispatch position updates to.
   * @param diffCallback ItemCallback that compares items to dispatch appropriate animations when
   *
   * @see DiffUtil.DiffResult.dispatchUpdatesTo
   */
  constructor(adapter: RecyclerView.Adapter<*>,
              updateFinishedCallback: ((List<T>) -> Unit)? = null,
              diffCallback: DiffUtil.ItemCallback<T>) {
    mUpdateCallback = AdapterListUpdateCallback(adapter)
    mUpdateFinishedCallback = updateFinishedCallback
    mConfig = AsyncDifferConfig.Builder(diffCallback).build()
  }

  /**
   * Create a AsyncListDiffer with the provided config, and ListUpdateCallback to dispatch
   * updates to.
   *
   * @param listUpdateCallback Callback to dispatch updates to.
   * @param config Config to define background work Executor, and DiffUtil.ItemCallback for
   * computing List diffs.
   *
   * @see DiffUtil.DiffResult.dispatchUpdatesTo
   */
  constructor(listUpdateCallback: ListUpdateCallback,
              updateFinishedCallback: ((List<T>) -> Unit)? = null,
              config: AsyncDifferConfig<T>) {
    mUpdateCallback = listUpdateCallback
    mUpdateFinishedCallback = updateFinishedCallback
    mConfig = config
  }

  /**
   * Pass a new List to the AdapterHelper. Adapter updates will be computed on a background
   * thread.
   *
   *
   * If a List is already present, a diff will be computed asynchronously on a background thread.
   * When the diff is computed, it will be applied (dispatched to the [ListUpdateCallback]),
   * and the new List will be swapped in.
   *
   * @param newList The new List.
   */
  fun submitList(newList: List<T>?) {
    val oldList = mList

    if (newList === oldList) {
      // nothing to do
      return
    }

    // incrementing generation means any currently-running diffs are discarded when they finish
    val runGeneration = ++mMaxScheduledGeneration

    // fast simple remove all
    if (newList == null) {
      val countRemoved = oldList?.size ?: 0
      mList = null
      currentList = emptyList()
      // notify last, after list is updated
      mUpdateCallback.onRemoved(0, countRemoved)
      return
    }

    // fast simple first insert
    if (oldList == null) {
      mList = newList
      currentList = Collections.unmodifiableList(newList)
      // notify last, after list is updated
      mUpdateCallback.onInserted(0, newList.size)
      return
    }

    mConfig.backgroundThreadExecutor.execute {
      val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
          return oldList.size
        }

        override fun getNewListSize(): Int {
          return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
          return mConfig.diffCallback.areItemsTheSame(
            oldList[oldItemPosition], newList[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
          return mConfig.diffCallback.areContentsTheSame(
            oldList[oldItemPosition], newList[newItemPosition])
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
          return mConfig.diffCallback.getChangePayload(
            oldList[oldItemPosition], newList[newItemPosition])
        }
      })

      mConfig.mainThreadExecutor.execute {
        if (mMaxScheduledGeneration == runGeneration) {
          latchList(newList, result)
        }
      }
    }
  }

  private fun latchList(newList: List<T>, diffResult: DiffUtil.DiffResult) {
    mList = newList
    // notify last, after list is updated
    currentList = Collections.unmodifiableList(newList)
    diffResult.dispatchUpdatesTo(mUpdateCallback)
    mUpdateFinishedCallback?.invoke(newList)
  }
}
