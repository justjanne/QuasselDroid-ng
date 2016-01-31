package de.kuschku.libquassel.functions.types;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

public class Heartbeat {
    public final DateTime dateTime;

    public Heartbeat(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Heartbeat() {
        this(DateTime.now());
    }

    @NonNull
    @Override
    public String toString() {
        return "Heartbeat{" +
                "dateTime=" + dateTime +
                '}';
    }
}
