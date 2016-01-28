package de.kuschku.libquassel.functions.types;

import org.joda.time.DateTime;

public class Heartbeat {
    public final DateTime dateTime;

    public Heartbeat(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Heartbeat{" +
                "dateTime=" + dateTime +
                '}';
    }
}
