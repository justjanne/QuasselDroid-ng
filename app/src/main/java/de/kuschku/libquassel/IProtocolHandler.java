package de.kuschku.libquassel;

import android.support.annotation.NonNull;

import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.InitRequestFunction;
import de.kuschku.libquassel.functions.types.RpcCallFunction;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.objects.types.ClientInitAck;
import de.kuschku.libquassel.objects.types.ClientInitReject;
import de.kuschku.libquassel.objects.types.ClientLoginAck;
import de.kuschku.libquassel.objects.types.ClientLoginReject;
import de.kuschku.libquassel.objects.types.SessionInit;

public interface IProtocolHandler {
    void onEventMainThread(InitDataFunction packedFunc);

    void onEventMainThread(InitRequestFunction packedFunc);

    void onEventMainThread(RpcCallFunction packedFunc);

    void onEventMainThread(SyncFunction packedFunc);

    void onEvent(ClientInitReject message);

    void onEvent(ClientInitAck message);

    void onEvent(ClientLoginAck message);

    void onEvent(ClientLoginReject message);

    void onEvent(SessionInit message);

    @NonNull
    Client getClient();
}
