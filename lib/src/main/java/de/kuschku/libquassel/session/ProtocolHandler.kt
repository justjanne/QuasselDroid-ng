package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.QVariantList
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import de.kuschku.libquassel.quassel.exceptions.MessageHandlingException
import de.kuschku.libquassel.quassel.exceptions.ObjectNotFoundException
import de.kuschku.libquassel.quassel.syncables.RpcHandler
import de.kuschku.libquassel.quassel.syncables.interfaces.ISyncableObject
import de.kuschku.libquassel.quassel.syncables.interfaces.invokers.Invokers
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.DEBUG
import org.threeten.bp.Instant
import java.io.Closeable

@Suppress("LeakingThis")
abstract class ProtocolHandler(
  protected val exceptionHandler: (Throwable) -> Unit
) : SignalProxy, AuthHandler, Closeable {
  protected var closed = false

  private val objectStorage: ObjectStorage = ObjectStorage(this)

  protected open var rpcHandler: RpcHandler? = null

  private val toInit = mutableMapOf<ISyncableObject, MutableList<SignalProxyMessage.SyncMessage>>()
  private val syncQueue = mutableListOf<SignalProxyMessage.SyncMessage>()

  protected var isInitializing = false

  private var currentCallClass = ""
  private var currentCallSlot = ""
  private var currentCallInstance = ""

  abstract fun onInitDone()

  private var totalInitCount = 0

  override fun handle(f: SignalProxyMessage): Boolean {
    if (closed) return true

    try {
      if (!super<SignalProxy>.handle(f)) {
        log(DEBUG, "ProtocolHandler", "No receiver registered for $f")
      }
    } catch (e: ObjectNotFoundException) {
      log(DEBUG, "ProtocolHandler", "An error has occured while processing $f", e)
    } catch (e: Throwable) {
      exceptionHandler.invoke(MessageHandlingException.SignalProxy(f, e))
    }
    return true
  }

  override fun handle(f: HandshakeMessage): Boolean {
    if (closed) return true

    try {
      if (!super<AuthHandler>.handle(f)) {
        log(DEBUG, "ProtocolHandler", "No receiver registered for $f")
      }
    } catch (e: ObjectNotFoundException) {
      log(DEBUG, "ProtocolHandler", "An error has occured while processing $f", e)
    } catch (e: Throwable) {
      exceptionHandler.invoke(MessageHandlingException.Handshake(f, e))
    }
    return true
  }

  override fun handle(f: SignalProxyMessage.InitData): Boolean {
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
    onInitStatusChanged(totalInitCount - toInit.size, totalInitCount)
    if (isInitializing && toInit.isEmpty()) {
      isInitializing = false
      onInitDone()
      syncQueue.map {
        try {
          this.handle(it)
        } catch (e: Throwable) {
          exceptionHandler.invoke(e)
        }
      }
    }
  }

  open fun onInitStatusChanged(progress: Int, total: Int) {}

  override fun handle(f: SignalProxyMessage.SyncMessage): Boolean {
    val obj = objectStorage.get(f.className, f.objectName) ?: if (isInitializing) {
      syncQueue.add(f)
      return true
    } else null

    obj?.let {
      val initQueue = toInit[it]
      if (initQueue != null) {
        initQueue.add(f)
        return true
      }

      val invoker = Invokers.get(f.className)
                    ?: throw IllegalArgumentException("Invalid classname: ${f.className}")
      currentCallClass = f.className
      currentCallInstance = f.objectName
      currentCallSlot = f.slotName
      invoker.invoke(it, f.slotName, f.params)
      currentCallClass = ""
      currentCallInstance = ""
      currentCallSlot = ""
    }
    return true
  }

  override fun handle(f: SignalProxyMessage.RpcCall): Boolean {
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

  override fun shouldSync(type: String, instance: String,
                          slot: String): Boolean = type != currentCallClass || slot != currentCallSlot || instance != currentCallInstance

  override fun callSync(type: String, instance: String, slot: String, params: QVariantList) {
    dispatch(SignalProxyMessage.SyncMessage(type, instance, slot, params))
  }

  override fun shouldRpc(slot: String): Boolean = slot != currentCallSlot

  override fun callRpc(slot: String, params: QVariantList) {
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
        toInit[syncableObject] = mutableListOf()
        totalInitCount++
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

  override fun close() {
    closed = true

    objectStorage.clear()
    toInit.clear()
  }
}
