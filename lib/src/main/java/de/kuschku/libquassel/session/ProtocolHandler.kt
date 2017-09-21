package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.HandshakeMessage
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.SignalProxyMessage
import de.kuschku.libquassel.quassel.exceptions.ObjectNotFoundException
import de.kuschku.libquassel.quassel.syncables.RpcHandler
import de.kuschku.libquassel.quassel.syncables.interfaces.ISyncableObject
import de.kuschku.libquassel.quassel.syncables.interfaces.invokers.Invokers
import org.threeten.bp.Instant
import java.util.logging.Level
import java.util.logging.Logger

abstract class ProtocolHandler : SignalProxy, AuthHandler {
  companion object {
    private val logger = Logger.getLogger("ProtocolHandler")
  }

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
      logger.log(Level.SEVERE, "Error Handling SignalProxyMessage", e)
    }
  }

  override fun handle(function: HandshakeMessage) {
    try {
      super<AuthHandler>.handle(function)
    } catch (e: Throwable) {
      logger.log(Level.SEVERE, "Error Handling HandshakeMessage", e)
    }
  }

  override fun handle(f: SignalProxyMessage.InitData) {
    logger.log(Level.FINEST, "< $f")
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
        logger.log(Level.FINEST, "< $f")
        throw ObjectNotFoundException(f.className, f.objectName)
      }
    }

    val initQueue = toInit[obj]
    if (initQueue != null) {
      initQueue.add(f)
      return
    }

    logger.log(Level.FINEST, f.toString())

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
    logger.log(Level.FINEST, "< $f")

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

  open fun cleanUp() {
    objectStorage.clear()
  }
}
