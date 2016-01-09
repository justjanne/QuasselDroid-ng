package de.kuschku.libquassel.objects.types;

public class SessionInit {
    public final SessionState SessionState;

    public SessionInit(de.kuschku.libquassel.objects.types.SessionState sessionState) {
        SessionState = sessionState;
    }

    @Override
    public String toString() {
        return "SessionInit{" +
                "SessionState=" + SessionState +
                '}';
    }
}
