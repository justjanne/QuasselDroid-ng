package de.kuschku.libquassel.functions.types;

import org.joda.time.DateTime;

public class HeartbeatReply {
    public final DateTime dateTime;

    public HeartbeatReply(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "HeartbeatReply{" +
                "dateTime=" + dateTime +
                '}';
    }
}
