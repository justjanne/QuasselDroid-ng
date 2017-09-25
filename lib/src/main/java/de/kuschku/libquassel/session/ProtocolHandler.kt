package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.quassel.exceptions.ObjectNotFoundException
import de.kuschku.libquassel.quassel.syncables.RpcHandler
import de.kuschku.libquassel.quassel.syncables.interfaces.ISyncableObject
import de.kuschku.libquassel.quassel.syncables.interfaces.invokers.Invokers
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.DEBUG
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.WARN
import de.kuschku.libquassel.util.compatibility.log
import org.threeten.bp.Instant

abstract class ProtocolHandler : SignalProxy, AuthHandler {
  private val objectStorage: ObjectStorage = ObjectStorage(this)
  private val rpcHandler: RpcHandler = RpcHandler(this)

  private val toInit = mutableMapOf<ISyncableObject, MutableList<SignalProxyMessage.SyncMessage>>()
  private val syncQueue = mutableListOf<SignalProxyMessage.SyncMessage>()

  protected var isInitializing = false

  private var currentCallClass = ""
  private var currentCallSlot = ""
  private var currentCallInstance = ""

  abstract fun onInitDone()

  override fun handle(f: SignalProxyMessage): Boolean {
    try {
      if (!super<SignalProxy>.handle(f)) {
        log(DEBUG, "No receiver registered for $f")
      }
    } catch (e: Throwable) {
      log(WARN, "ProtocolHandler",
          "Error Handling SignalProxyMessage", e)
    }
    return true
  }

  override fun handle(f: HandshakeMessage): Boolean {
    try {
      if (!super<AuthHandler>.handle(f)) {
        log(DEBUG, "No receiver registered for $f")
      }
    } catch (e: Throwable) {
      log(WARN, "ProtocolHandler",
          "Error Handling HandshakeMessage", e)
    }
    return true
  }

  override fun handle(f: SignalProxyMessage.InitData): Boolean {
    log(DEBUG, "ProtocolHandler", "< $f")
    val obj: ISyncableObject = objectStorage.get(f.className, f.objectName)
      ?: throw ObjectNotFoundException(f.className, f.objectName)

    obj.fromVariantMap(f.initData)
    obj.initialized = true
    synchronize(obj)
    val list = toInit.remove(obj)
    checkForInitDone()
    list?.map(this::handle)
    return true
  }

  private fun checkForInitDone() {
    if (isInitializing && toInit.isEmpty()) {
      isInitializing = false
      onInitDone()
      syncQueue.map {
        try {
          this.handle(it)
        } catch (e: Throwable) {
          log(WARN, "ProtocolHandler", e)
        }
      }
    }
  }

  override fun handle(f: SignalProxyMessage.SyncMessage): Boolean {
    val obj = objectStorage.get(f.className, f.objectName) ?: if (isInitializing) {
      syncQueue.add(f)
      return true
    } else {
      log(DEBUG, "ProtocolHandler", "< $f")
      throw ObjectNotFoundException(f.className, f.objectName)
    }

    val initQueue = toInit[obj]
    if (initQueue != null) {
      initQueue.add(f)
      return true
    }

    log(DEBUG, "ProtocolHandler", f.toString())

    val invoker = Invokers.get(f.className)
      ?: throw IllegalArgumentException("Invalid classname: ${f.className}")
    currentCallClass = f.className
    currentCallInstance = f.objectName
    currentCallSlot = f.slotName
    invoker.invoke(obj, f.slotName, f.params)
    currentCallClass = ""
    currentCallInstance = ""
    currentCallSlot = ""
    return true
  }

  override fun handle(f: SignalProxyMessage.RpcCall): Boolean {
    log(DEBUG, "ProtocolHandler", "< $f")

    currentCallSlot = f.slotName
    Invokers.RPC?.invoke(rpcHandler, f.slotName, f.params)
    currentCallSlot = ""
    return true
  }

  override fun handle(f: SignalProxyMessage.HeartBeat): Boolean {
    dispatch(SignalProxyMessage.HeartBeatReply(f.timestamp))
    dispatch(SignalProxyMessage.HeartBeat(Instant.now()))
    return true
  }

  override fun shouldSync(type: String, instance: String, slot: String): Boolean
    = type != currentCallClass || slot != currentCallSlot || instance != currentCallInstance

  override fun callSync(type: String, instance: String, slot: String, params: List<QVariant_>) {
    dispatch(SignalProxyMessage.SyncMessage(type, instance, slot, params))
  }

  override fun shouldRpc(slot: String): Boolean
    = slot != currentCallSlot

  override fun callRpc(slot: String, params: List<QVariant_>) {
    dispatch(SignalProxyMessage.RpcCall(slot, params))
  }

  override fun renameObject(syncableObject: ISyncableObject, newName: String, oldName: String) {
    objectStorage.rename(syncableObject, newName, oldName)
  }

  override fun renameObject(className: String, newName: String, oldName: String) {
    objectStorage.rename(className, newName, oldName)
  }

  override fun synchronize(syncableObject: ISyncableObject?, baseInit: Boolean) {
    if (syncableObject == null)
      return

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

  override fun stopSynchronize(syncableObject: ISyncableObject?) {
    if (syncableObject == null)
      return

    objectStorage.remove(syncableObject)
    toInit.remove(syncableObject)
  }

  open fun cleanUp() {
    objectStorage.clear()
    toInit.clear()
  }
}
