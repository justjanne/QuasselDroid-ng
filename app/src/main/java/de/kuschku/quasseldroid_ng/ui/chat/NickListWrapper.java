/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
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

package de.kuschku.quasseldroid_ng.ui.chat;

import android.support.annotation.NonNull;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;
import de.kuschku.util.backports.Objects;
import de.kuschku.util.irc.IrcCaseMapper;
import de.kuschku.util.observables.callbacks.ElementCallback;
import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.lists.ObservableSortedList;

public class NickListWrapper {
    @NonNull
    private final QIrcChannel channel;
    private final ObservableSortedList<QIrcUser> list = new ObservableSortedList<>(QIrcUser.class, new ObservableSortedList.ItemComparator<QIrcUser>() {
        @Override
        public int compare(@NonNull QIrcUser o1, @NonNull QIrcUser o2) {
            int indexa = channel.network().modeToIndex(channel.userModes(o1));
            int indexb = channel.network().modeToIndex(channel.userModes(o2));
            if (indexa == indexb) {
                return o1.nick().compareToIgnoreCase(o2.nick());
            } else {
                return indexa - indexb;
            }
        }

        @Override
        public boolean areContentsTheSame(QIrcUser oldItem, QIrcUser newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areItemsTheSame(@NonNull QIrcUser item1, @NonNull QIrcUser item2) {
            return Objects.equals(item1.hostmask(), item2.hostmask());
        }
    });

    public NickListWrapper(@NonNull Drawer drawerRight, @NonNull QIrcChannel channel) {
        drawerRight.removeAllItems();
        this.channel = channel;
        for (String nick : channel.users()) {
            list.add(channel.network().ircUser(nick));
        }
        channel.users().addCallback(new ElementCallback<String>() {
            @Override
            public void notifyItemInserted(String element) {
                list.add(channel.network().ircUser(element));
            }

            @Override
            public void notifyItemRemoved(String element) {
                for (QIrcUser user : list) {
                    if (IrcCaseMapper.equalsIgnoreCase(user.nick(), element)) {
                        list.remove(user);
                        return;
                    }
                }
            }

            @Override
            public void notifyItemChanged(String element) {
                for (int i = 0; i < list.size(); i++) {
                    if (IrcCaseMapper.equalsIgnoreCase(list.get(i).nick(), element)) {
                        list.notifyItemChanged(i);
                        return;
                    }
                }
            }
        });
        this.list.addCallback(new UICallback() {
            @Override
            public void notifyItemInserted(int position) {
                drawerRight.getItemAdapter().add(position, fromUser(list.get(position)));
            }

            @Override
            public void notifyItemChanged(int position) {
                drawerRight.getItemAdapter().notifyItemChanged(position);
            }

            @Override
            public void notifyItemRemoved(int position) {
                drawerRight.removeItemByPosition(position);
            }

            @Override
            public void notifyItemMoved(int from, int to) {
                notifyItemRemoved(from);
                notifyItemInserted(from);
            }

            @Override
            public void notifyItemRangeInserted(int position, int count) {
                for (int i = position; i < position + count; i++) {
                    notifyItemInserted(i);
                }
            }

            @Override
            public void notifyItemRangeChanged(int position, int count) {
                for (int i = position; i < position + count; i++) {
                    notifyItemChanged(i);
                }
            }

            @Override
            public void notifyItemRangeRemoved(int position, int count) {
                for (int i = position; i < position + count; i++) {
                    notifyItemRemoved(i);
                }
            }
        });
        for (QIrcUser user : list) {
            if (user != null)
                drawerRight.getItemAdapter().add(fromUser(user));
        }
    }

    private IDrawerItem fromUser(@NonNull QIrcUser user) {
        return new PrimaryDrawerItem()
                .withName(user.nick())
                .withBadge(channel.network().modeToPrefix(channel.userModes(user)))
                .withBadgeStyle(new BadgeStyle().withColor(0xFFFF0000).withTextColor(0xFFFFFFFF));
    }
}
