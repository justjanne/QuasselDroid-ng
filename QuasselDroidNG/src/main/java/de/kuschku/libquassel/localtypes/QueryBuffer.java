package de.kuschku.libquassel.localtypes;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.IrcUser;

public class QueryBuffer implements Buffer {
    private final BufferInfo info;
    private IrcUser user;

    public QueryBuffer(BufferInfo info, IrcUser user) {
        this.info = info;
        this.user = user;
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
