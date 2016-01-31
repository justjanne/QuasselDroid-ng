package de.kuschku.libquassel.functions.types;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

public class HeartbeatReply {
    public final DateTime dateTime;

    public HeartbeatReply(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "HeartbeatReply{" +
                "dateTime=" + dateTime +
                '}';
    }
}
