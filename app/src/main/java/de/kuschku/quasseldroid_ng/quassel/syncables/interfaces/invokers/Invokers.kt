package de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.invokers

import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.*
import de.kuschku.quasseldroid_ng.util.helpers.Logger
import de.kuschku.quasseldroid_ng.util.helpers.debug
import de.kuschku.quasseldroid_ng.util.helpers.warn

object Invokers {
  private val registry = mutableMapOf<String, Invoker<*>>()
  fun get(name: String) = registry[name]

  val RPC: Invoker<IRpcHandler>?

  val size
    get() = registry.size

  init {
    register(invoker<IAliasManager>())
    register(invoker<IBacklogManager>())
    register(invoker<IBufferSyncer>())
    register(invoker<IBufferViewConfig>())
    register(invoker<IBufferViewManager>())
    register(invoker<ICertManager>())
    register(invoker<ICoreInfo>())
    register(invoker<IDccConfig>())
    register(invoker<IIdentity>())
    register(invoker<IIgnoreListManager>())
    register(invoker<IIrcChannel>())
    register(invoker<IIrcListHelper>())
    register(invoker<IIrcUser>())
    register(invoker<INetwork>())
    register(invoker<INetworkConfig>())
    register(invoker<ITransfer>())
    register(invoker<ITransferManager>())

    RPC = invoker()

    Logger.debug("Invokers::init", "$size invokers registered")
  }

  private inline fun <reified T> invoker(): Invoker<T>? = getInvoker(T::class.java)

  private fun <T> getInvoker(type: Class<T>): Invoker<T>? {
    val syncable: Syncable? = type.getAnnotation(Syncable::class.java)
    if (syncable == null) {
      Logger.warn("Invokers::register", "Invoker not annotated: ${type.canonicalName}")
      return null
    }

    val packageName = "${type.`package`.name}.invokers"
    val className = "${syncable.name}Invoker"
    val klass = Class.forName("$packageName.$className")
    val invoker = klass.getDeclaredField("INSTANCE").get(null)
    if (invoker !is Invoker<*>) {
      Logger.warn("Invokers::register",
                  "Invoker not of proper type: ${type.canonicalName} != ${invoker.javaClass.canonicalName}")
      return null
    }

    return invoker as Invoker<T>?
  }

  private fun <T> register(invoker: Invoker<T>?) {
    if (invoker != null)
      registry.put(invoker.className, invoker)
  }
}
