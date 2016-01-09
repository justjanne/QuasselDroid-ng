package de.kuschku.libquassel.localtypes;

import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.IrcUser;
import de.kuschku.quasseldroid_ng.BufferDrawerItem;

public class QueryBuffer implements Buffer {
    private final BufferInfo info;
    private IrcUser user;
    private IDrawerItem drawerElement = new BufferDrawerItem(this);

    public QueryBuffer(BufferInfo info, IrcUser user) {
        this.info = info;
        this.user = user;
    }

    public IDrawerItem getDrawerElement() {
        return drawerElement;
    }

    @Override
    public BufferInfo getInfo() {
        return info;
    }

    @Override
    public String getName() {
        return getInfo().name;
    }

    public IrcUser getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "QueryBuffer{" +
                "info=" + info +
                ", user=" + user +
                '}';
    }
}
