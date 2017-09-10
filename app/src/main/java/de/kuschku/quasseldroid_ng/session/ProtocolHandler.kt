package de.kuschku.quasseldroid_ng.session

import de.kuschku.quasseldroid_ng.ObjectNotFoundException
import de.kuschku.quasseldroid_ng.protocol.HandshakeMessage
import de.kuschku.quasseldroid_ng.protocol.QVariant_
import de.kuschku.quasseldroid_ng.protocol.SignalProxyMessage
import de.kuschku.quasseldroid_ng.quassel.syncables.RpcHandler
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.ISyncableObject
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.invokers.Invokers
import de.kuschku.quasseldroid_ng.util.helpers.Logger
import de.kuschku.quasseldroid_ng.util.helpers.debug
import de.kuschku.quasseldroid_ng.util.helpers.warn
import org.threeten.bp.Instant

abstract class ProtocolHandler : SignalProxy, AuthHandler {
  private val objectStorage: ObjectStorage = ObjectStorage(this)
  protected val rpcHandler: RpcHandler = RpcHandler(this)

  private val toInit = mutableMapOf<ISyncableObject, MutableList<SignalProxyMessage.SyncMessage>>()
  private val syncQueue = mutableListOf<SignalProxyMessage.SyncMessage>()

  protected var isInitializing = false

  private var currentCallClass = ""
  private var currentCallSlot = ""
  private var currentCallInstance = ""

  abstract fun onInitDone()

  override fun handle(f: SignalProxyMessage) {
    try {
      super<SignalProxy>.handle(f)
    } catch (e: Throwable) {
      Logger.warn("ProtocolHandler", "", e)
    }
  }

  override fun handle(function: HandshakeMessage) {
    try {
      super<AuthHandler>.handle(function)
    } catch (e: Throwable) {
      Logger.warn("ProtocolHandler", "", e)
    }
  }

  override fun handle(f: SignalProxyMessage.InitData) {
    Logger.debug("<", f.toString())
    val obj: ISyncableObject = objectStorage.get(f.className, f.objectName)
      ?: throw ObjectNotFoundException(f.className, f.objectName)

    obj.fromVariantMap(f.initData)
    obj.initialized = true
    synchronize(obj)
    checkForInitDone()
    toInit.remove(obj)?.forEach(this::handle)
  }

  private fun checkForInitDone() {
    if (isInitializing && toInit.isEmpty()) {
      isInitializing = false
      syncQueue.forEach(this::handle)
      onInitDone()
    }
  }

  override fun handle(f: SignalProxyMessage.SyncMessage) {
    val obj = objectStorage.get(f.className, f.objectName)
    if (obj == null) {
      if (isInitializing) {
        syncQueue.add(f)
        return
      } else {
        Logger.debug("<", f.toString())
        throw ObjectNotFoundException(f.className, f.objectName)
      }
    }

    val initQueue = toInit[obj]
    if (initQueue != null) {
      initQueue.add(f)
      return
    }

    Logger.debug("<", f.toString())

    val invoker = Invokers.get(f.className) ?: throw IllegalArgumentException(
      "Invalid classname: ${f.className}")
    currentCallClass = f.className
    currentCallInstance = f.objectName
    currentCallSlot = f.slotName
    invoker.invoke(obj, f.slotName, f.params)
    currentCallClass = ""
    currentCallInstance = ""
    currentCallSlot = ""
  }

  override fun handle(f: SignalProxyMessage.RpcCall) {
    Logger.debug("<", f.toString())

    currentCallSlot = f.slotName
    Invokers.RPC?.invoke(rpcHandler, f.slotName, f.params)
    currentCallSlot = ""
  }

  override fun handle(f: SignalProxyMessage.HeartBeat) {
    dispatch(SignalProxyMessage.HeartBeatReply(f.timestamp))
    dispatch(SignalProxyMessage.HeartBeat(Instant.now()))
  }

  override fun callSync(type: String, instance: String, slot: String, params: List<QVariant_>) {
    // Don’t transmit calls back that we just got from the network
    if (type != currentCallClass || slot != currentCallSlot || instance != currentCallInstance) {
      dispatch(SignalProxyMessage.SyncMessage(type, instance, slot, params))
    }
  }

  override fun callRpc(slot: String, params: List<QVariant_>) {
    // Don’t transmit calls back that we just got from the network
    if (slot != currentCallSlot) {
      dispatch(SignalProxyMessage.RpcCall(slot, params))
    }
  }

  override fun renameObject(syncableObject: ISyncableObject, newName: String, oldName: String) {
    objectStorage.rename(syncableObject, newName, oldName)
  }

  override fun renameObject(className: String, newName: String, oldName: String) {
    objectStorage.rename(className, newName, oldName)
  }

  override fun synchronize(syncableObject: ISyncableObject, baseInit: Boolean) {
    if (!syncableObject.initialized)
      syncableObject.init()

    objectStorage.add(syncableObject)

    if (!syncableObject.initialized) {
      if (baseInit) {
        toInit.put(syncableObject, mutableListOf())
      }
      dispatch(SignalProxyMessage.InitRequest(syncableObject.className, syncableObject.objectName))
    }
  }

  override fun stopSynchronize(syncableObject: ISyncableObject) {
    objectStorage.remove(syncableObject)
    toInit.remove(syncableObject)
  }
}
