package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;

public class SessionInit {
    public final SessionState SessionState;

    public SessionInit(de.kuschku.libquassel.objects.types.SessionState sessionState) {
        SessionState = sessionState;
    }

    @NonNull
    @Override
    public String toString() {
        return "SessionInit{" +
                "SessionState=" + SessionState +
                '}';
    }
}
