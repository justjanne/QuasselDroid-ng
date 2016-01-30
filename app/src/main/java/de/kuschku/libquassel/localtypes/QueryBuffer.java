package de.kuschku.libquassel.localtypes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.IrcUser;

public class QueryBuffer implements Buffer {
    @NonNull
    private final BufferInfo info;
    @Nullable
    private final IrcUser user;

    public QueryBuffer(@NonNull BufferInfo info, @Nullable IrcUser user) {
        this.info = info;
        this.user = user;
    }

    @NonNull
    @Override
    public BufferInfo getInfo() {
        return info;
    }

    @Nullable
    @Override
    public String getName() {
        return getInfo().name;
    }

    @Override
    public BufferInfo.BufferStatus getStatus() {
        return  (user == null) ?    BufferInfo.BufferStatus.OFFLINE :
                (user.isAway()) ?   BufferInfo.BufferStatus.AWAY :
                                    BufferInfo.BufferStatus.ONLINE;
    }

    @Nullable
    public IrcUser getUser() {
        return user;
    }

    @NonNull
    @Override
    public String toString() {
        return "QueryBuffer{" +
                "info=" + info +
                ", user=" + user +
                '}';
    }
}
