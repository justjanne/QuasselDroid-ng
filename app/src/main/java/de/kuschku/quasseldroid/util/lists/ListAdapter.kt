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
import androidx.recyclerview.widget.RecyclerView

/**
 * [RecyclerView.Adapter] base class for presenting List data in a
 * [RecyclerView], including computing diffs between Lists on a background thread.
 *
 *
 * This class is a convenience wrapper around [AsyncListDiffer] that implements Adapter common
 * default behavior for item access and counting.
 *
 *
 * While using a LiveData&lt;List> is an easy way to provide data to the adapter, it isn't required
 * - you can use [.submitList] when new lists are available.
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
 * UserAdapter&lt;User> adapter = new UserAdapter();
 * viewModel.usersList.observe(this, list -> adapter.submitList(list));
 * recyclerView.setAdapter(adapter);
 * }
 * }
 *
 * class UserAdapter extends ListAdapter&lt;User, UserViewHolder> {
 * public UserAdapter() {
 * super(User.DIFF_CALLBACK);
 * }
 * @Override
 * public void onBindViewHolder(UserViewHolder holder, int position) {
 * holder.bindTo(getItem(position));
 * }
 * public static final DiffUtil.ItemCallback&lt;User> DIFF_CALLBACK =
 * new DiffUtil.ItemCallback&lt;User>() {
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
 * Advanced users that wish for more control over adapter behavior, or to provide a specific base
 * class should refer to [AsyncListDiffer], which provides custom mapping from diff events
 * to adapter positions.
 *
 * @param <T> Type of the Lists this Adapter will receive.
 * @param <VH> A class that extends ViewHolder that will be used by the adapter.
</VH></T> */
abstract class ListAdapter<T : Any, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH> {
  private val mHelper: AsyncListDiffer<T>

  protected constructor(diffCallback: DiffUtil.ItemCallback<T>) {
    mHelper = AsyncListDiffer(AdapterListUpdateCallback(this),
                              ::onUpdateFinished,
                              AsyncDifferConfig.Builder(diffCallback).build())
  }

  protected constructor(config: AsyncDifferConfig<T>) {
    mHelper = AsyncListDiffer(AdapterListUpdateCallback(this),
                              ::onUpdateFinished,
                              config)
  }

  /**
   * Submits a new list to be diffed, and displayed.
   *
   *
   * If a list is already being displayed, a diff will be computed on a background thread, which
   * will dispatch Adapter.notifyItem events on the main thread.
   *
   * @param list The new list to be displayed.
   */
  fun submitList(list: List<T>) {
    mHelper.submitList(list)
  }

  protected fun getItem(position: Int): T {
    return mHelper.currentList[position]
  }

  override fun getItemCount(): Int {
    return mHelper.currentList.size
  }

  open fun onUpdateFinished(list: List<T>) = Unit
}
