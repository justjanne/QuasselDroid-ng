/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.client;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.events.CriticalErrorEvent;
import de.kuschku.libquassel.events.InitEvent;
import de.kuschku.libquassel.events.LagChangedEvent;
import de.kuschku.libquassel.events.PasswordChangeEvent;
import de.kuschku.libquassel.events.StatusMessageEvent;
import de.kuschku.libquassel.functions.types.InitRequestFunction;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.localtypes.backlogstorage.BacklogStorage;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.objects.types.CoreStatus;
import de.kuschku.libquassel.objects.types.SessionState;
import de.kuschku.libquassel.primitives.QMetaTypeRegistry;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.libquassel.syncables.types.impl.BacklogManager;
import de.kuschku.libquassel.syncables.types.impl.CoreInfo;
import de.kuschku.libquassel.syncables.types.impl.Identity;
import de.kuschku.libquassel.syncables.types.interfaces.QAliasManager;
import de.kuschku.libquassel.syncables.types.interfaces.QBacklogManager;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferSyncer;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewManager;
import de.kuschku.libquassel.syncables.types.interfaces.QIgnoreListManager;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.libquassel.syncables.types.interfaces.QNetworkConfig;
import de.kuschku.util.buffermetadata.BufferMetaDataManager;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class Client extends AClient {
    @NonNull
    private final NetworkManager networkManager;
    @NonNull
    private final BufferManager bufferManager;
    @NonNull
    private final IdentityManager identityManager;
    @NonNull
    private final BacklogStorage backlogStorage;
    private final List<String> initRequests = new LinkedList<>();
    @NonNull
    private final QBacklogManager backlogManager;
    private final Map<String, List<SyncFunction>> bufferedSyncs = new HashMap<>();
    private final Map<Integer, Pair<QBufferViewConfig, Integer>> bufferedBuffers = new HashMap<>();
    private final BufferMetaDataManager metaDataManager;
    private final String coreId;
    private int initRequestMax = 0;
    private QBufferViewManager bufferViewManager;
    // local
    private QBufferSyncer bufferSyncer;
    private QAliasManager aliasManager;
    private QIgnoreListManager ignoreListManager;
    private QNetworkConfig globalNetworkConfig;
    private CoreStatus core;
    private CoreInfo coreInfo;
    private long latency;
    private ConnectionChangeEvent.Status connectionStatus;
    private int r = 1;

    public Client(@NonNull BusProvider provider, @NonNull BacklogStorage backlogStorage, @NonNull BufferMetaDataManager metaDataManager, String coreId) {
        this.coreId = coreId;
        this.provider = provider;
        this.networkManager = new NetworkManager(this);
        this.bufferManager = new BufferManager(this);
        this.identityManager = new IdentityManager();
        this.backlogStorage = backlogStorage;
        backlogStorage.setClient(this);
        this.backlogManager = new BacklogManager(this, backlogStorage);
        this.backlogManager.init("", provider, this);
        this.initialized = true;
        this.metaDataManager = metaDataManager;
    }

    public QBufferViewManager bufferViewManager() {
        return bufferViewManager;
    }

    public QBufferSyncer bufferSyncer() {
        return bufferSyncer;
    }

    public QAliasManager aliasManager() {
        return aliasManager;
    }

    @NonNull
    public QBacklogManager backlogManager() {
        return backlogManager;
    }

    public QIgnoreListManager ignoreListManager() {
        return ignoreListManager;
    }

    public QNetworkConfig globalNetworkConfig() {
        return globalNetworkConfig;
    }

    @Override
    public void _displayMsg(Message msg) {
        backlogManager.receiveBacklog(msg);
    }

    @Override
    public void _displayStatusMsg(String network, String message) {
        assertNotNull(provider);

        provider.sendEvent(new StatusMessageEvent(network, message));
    }

    @Override
    public void _bufferInfoUpdated(@NonNull BufferInfo bufferInfo) {
        bufferManager.updateBufferInfo(bufferInfo);
    }

    @Override
    public void _identityCreated(@NonNull Identity identity) {
        identityManager.createIdentity(identity);
        identity.init(String.valueOf(identity.id()), provider, this);
    }

    @Override
    public void _identityRemoved(int id) {
        identityManager.removeIdentity(id);
    }

    @Override
    public void _networkCreated(int network) {
        networkManager.createNetwork(network);
    }

    @Override
    public void _networkRemoved(int network) {
        networkManager.removeNetwork(network);
    }

    @Override
    public void _passwordChanged(long peerPtr, boolean success) {
        assertNotNull(provider);

        if (peerPtr != 0x0000000000000000L)
            provider.sendEvent(new CriticalErrorEvent("Your core has a critical vulnerability. Please update it."));
        provider.sendEvent(new PasswordChangeEvent(success));
    }

    @Override
    public void ___objectRenamed__(String type, String oldName, String newName) {

    }

    public synchronized ConnectionChangeEvent.Status connectionStatus() {
        return connectionStatus;
    }

    public synchronized void setConnectionStatus(@NonNull ConnectionChangeEvent.Status connectionStatus) {
        assertNotNull(provider);

        this.connectionStatus = connectionStatus;
        provider.event.postSticky(new ConnectionChangeEvent(connectionStatus));

        if (connectionStatus == ConnectionChangeEvent.Status.LOADING_BACKLOG) {
            bufferManager().doBacklogInit(20);
        } else if (connectionStatus == ConnectionChangeEvent.Status.CONNECTED) {
            for (int bufferId : bufferManager().bufferIds()) {
                metaDataManager().hiddendata(coreId(), bufferId);
            }
            // FIXME: Init buffer activity state and highlights
        }
    }

    @Nullable
    public Object unsafe_getObjectByIdentifier(@NonNull String className, @NonNull String objectName) {
        switch (className) {
            case "AliasManager": {
                return aliasManager;
            }
            case "BacklogManager": {
                return backlogManager;
            }
            case "BufferSyncer": {
                return bufferSyncer;
            }
            case "BufferViewConfig": {
                if (bufferViewManager == null)
                    return null;

                return bufferViewManager.bufferViewConfig(Integer.parseInt(objectName));
            }
            case "BufferViewManager": {
                return bufferViewManager;
            }
            case "CoreInfo": {
                return coreInfo;
            }
            case "Identity": {
                if (identityManager == null)
                    return null;
                return identityManager.identity(Integer.parseInt(objectName));
            }
            case "IgnoreListManager": {
                return ignoreListManager;
            }
            case "IrcChannel": {
                String[] split = objectName.split("/");
                if (split.length != 2) {
                    Log.w("libquassel", "malformatted object name: " + objectName);
                    return null;
                }
                QNetwork network = networkManager.network(Integer.parseInt(split[0]));
                if (network == null) {
                    Log.w("libquassel", "Network doesn’t exist yet: " + objectName);
                    return null;
                }
                return network.ircChannel(split[1]);
            }
            case "IrcUser": {
                String[] split = objectName.split("/");
                if (split.length != 2) {
                    Log.w("libquassel", "malformatted object name: " + objectName);
                    return null;
                }
                QNetwork network = networkManager.network(Integer.parseInt(split[0]));
                if (network == null) {
                    Log.w("libquassel", "Network doesn’t exist yet: " + objectName);
                    return null;
                }
                return network.ircUser(split[1]);
            }
            case "Network": {
                return networkManager.network(Integer.parseInt(objectName));
            }
            case "NetworkConfig": {
                return globalNetworkConfig;
            }
            case "NetworkInfo": {
                QNetwork network = networkManager().network(Integer.parseInt(objectName));
                if (network == null)
                    return null;
                else
                    return network.networkInfo();
            }
            default: {
                Log.w("libquassel", "Unknown type: " + className + " : " + objectName);
                return null;
            }
        }
    }

    @Nullable
    public <T> T getObjectByIdentifier(@NonNull String className, @NonNull String objectName) {
        Class<T> cl = QMetaTypeRegistry.<T>getType(className).cl;
        return getObjectByIdentifier(cl, className, objectName);
    }

    @Nullable
    public <T> T getObjectByIdentifier(@NonNull Class<T> cl, @NonNull String objectName) {
        return getObjectByIdentifier(cl, cl.getSimpleName(), objectName);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getObjectByIdentifier(@NonNull Class<T> cl, @NonNull String className, @NonNull String objectName) {
        Object obj = unsafe_getObjectByIdentifier(className, objectName);
        // The fancy version of "instanceof" that works with erased types, too
        if (obj == null || !cl.isAssignableFrom(obj.getClass()))
            return null;
        else
            return (T) obj;
    }

    public void init(@NonNull SessionState sessionState) {
        networkManager.init(sessionState.NetworkIds);
        identityManager.init(sessionState.Identities);
        bufferManager.init(sessionState.BufferInfos);

        requestInitObject("BufferSyncer", "");
        requestInitObject("BufferViewManager", "");
        requestInitObject("AliasManager", "");
        requestInitObject("NetworkConfig", "GlobalNetworkConfig");
        requestInitObject("IgnoreListManager", "");
        //sendInitRequest("TransferManager", "");
        // This thing never gets sent...

        assertNotNull(provider);
        provider.event.postSticky(new InitEvent(initRequestMax - initRequests.size(), initRequestMax));
    }

    @NonNull
    public NetworkManager networkManager() {
        return networkManager;
    }

    @NonNull
    public BufferManager bufferManager() {
        return bufferManager;
    }

    @NonNull
    public IdentityManager identityManager() {
        return identityManager;
    }

    public void requestInitObject(@NonNull String className, String objectName) {
        assertNotNull(provider);

        if (connectionStatus() == ConnectionChangeEvent.Status.INITIALIZING_DATA) {
            initRequests.add(hashName(className, objectName));
            initRequestMax++;
        }

        provider.dispatch(new InitRequestFunction(className, objectName));
    }

    public void initObject(String className, @NonNull String objectName, @NonNull SyncableObject object) {
        assertNotNull(provider);

        object.init(objectName, provider, this);

        if (connectionStatus() == ConnectionChangeEvent.Status.INITIALIZING_DATA) {
            initRequests.remove(hashName(className, objectName));
            provider.event.postSticky(new InitEvent(initRequestMax - initRequests.size(), initRequestMax));
            if (initRequests.isEmpty()) {
                setConnectionStatus(ConnectionChangeEvent.Status.LOADING_BACKLOG);
            }
        }

        synchronized (bufferedSyncs) {
            if (r > 0) r--;
            else throw new RuntimeException();
            // Execute cached sync requests
            if (bufferedSyncs.size() > 0) {
                String key = hashName(className, objectName);
                if (bufferedSyncs.containsKey(key)) {
                    Log.d("libquassel", "Unqueueing syncs: " + className + ":" + objectName);
                    List<SyncFunction> functions = bufferedSyncs.get(key);
                    for (SyncFunction function : functions)
                        provider.handle(function);
                    bufferedSyncs.remove(key);
                }
            }
            r++;
        }
    }

    @NonNull
    private String hashName(String className, String objectName) {
        return className + ":" + objectName;
    }

    public void setLatency(long latency) {
        assertNotNull(provider);

        this.latency = latency;
        provider.sendEvent(new LagChangedEvent(latency));
    }

    public CoreInfo coreInfo() {
        return coreInfo;
    }

    public void setCoreInfo(CoreInfo coreInfo) {
        this.coreInfo = coreInfo;
    }

    public CoreStatus core() {
        return core;
    }

    public void setCore(CoreStatus core) {
        this.core = core;
    }

    public long latency() {
        return latency;
    }

    public void setBufferSyncer(QBufferSyncer bufferSyncer) {
        this.bufferSyncer = bufferSyncer;
    }

    public void setBufferViewManager(QBufferViewManager bufferViewManager) {
        this.bufferViewManager = bufferViewManager;
    }

    public void setAliasManager(QAliasManager aliasManager) {
        this.aliasManager = aliasManager;
    }

    public void setIgnoreListManager(QIgnoreListManager ignoreListManager) {
        this.ignoreListManager = ignoreListManager;
    }

    public void setGlobalNetworkConfig(QNetworkConfig globalNetworkConfig) {
        this.globalNetworkConfig = globalNetworkConfig;
    }

    @NonNull
    public BacklogStorage backlogStorage() {
        return backlogStorage;
    }

    public void bufferSync(@NonNull SyncFunction packedFunc) {
        String key = hashName(packedFunc.className, packedFunc.objectName);
        if (connectionStatus() == ConnectionChangeEvent.Status.CONNECTED) {
            Log.d("libquassel", "Queueing sync: " + packedFunc);
        }

        synchronized (bufferedSyncs) {
            if (r > 0) r--;
            else throw new RuntimeException();
            if (!bufferedSyncs.containsKey(key))
                bufferedSyncs.put(key, new LinkedList<>());
            bufferedSyncs.get(key).add(packedFunc);
            Log.d("libquassel", "Queued syncs: " + bufferedSyncs.keySet());
            r++;
        }
    }

    public void bufferBuffer(QBufferViewConfig bufferViewConfig, int bufferId, int pos) {
        bufferedBuffers.put(bufferId, Pair.create(bufferViewConfig, pos));
        Log.d("libquassel", "Queueing buffer: " + bufferId);
        Log.d("libquassel", "Queued buffers: " + bufferedBuffers.keySet());
    }

    public void unbufferBuffer(@NonNull BufferInfo info) {
        if (!bufferManager().exists(info)) {
            bufferManager().createBuffer(info);
            Log.d("libquassel", "Creating buffer from message info: " + info.id);
        }
        if (bufferedBuffers.containsKey(info.id)) {
            Pair<QBufferViewConfig, Integer> pair = bufferedBuffers.get(info.id);
            pair.first._addBuffer(info.id, pair.second);
            Log.d("libquassel", "Un-Queueing buffer: " + info.id);
        }
    }

    @Nullable
    public BusProvider provider() {
        return provider;
    }

    public BufferMetaDataManager metaDataManager() {
        return metaDataManager;
    }

    public String coreId() {
        return coreId;
    }
}
