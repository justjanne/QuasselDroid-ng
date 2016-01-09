package de.kuschku.libquassel.functions;

public enum FunctionType {
    INVALID(0),
    SYNC(1),
    RPCCALL(2),
    INITREQUEST(3),
    INITDATA(4),
    HEARTBEAT(5),
    HEARTBEATREPLY(6);

    public final int id;

    FunctionType(int id) {
        this.id = id;
    }

    public static FunctionType fromId(int id) {
        switch (id) {
            case 1:
                return SYNC;
            case 2:
                return RPCCALL;
            case 3:
                return INITREQUEST;
            case 4:
                return INITDATA;
            case 5:
                return HEARTBEAT;
            case 6:
                return HEARTBEATREPLY;
            default:
                return INVALID;
        }
    }
}
