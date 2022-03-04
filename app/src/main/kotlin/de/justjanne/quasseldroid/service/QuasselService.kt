package de.justjanne.quasseldroid.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.room.Room
import de.justjanne.quasseldroid.persistence.AppDatabase
import java.net.InetSocketAddress

class QuasselService : Service() {
  private var runner: QuasselRunner? = null

  private val database: AppDatabase by lazy {
    Room.databaseBuilder(
      this.applicationContext,
      AppDatabase::class.java,
      "app"
    ).build()
  }

  private fun newRunner(intent: Intent): QuasselRunner {
    Log.w("QuasselService", "Creating new quassel runner")
    val address = InetSocketAddress.createUnresolved(
      requireNotNull(intent.getStringExtra("host")) {
        "Required argument 'host' missing"
      },
      intent.getIntExtra("port", 4242),
    )
    val auth = Pair(
      requireNotNull(intent.getStringExtra("username")) {
        "Required argument 'username' missing"
      },
      requireNotNull(intent.getStringExtra("password")) {
        "Required argument 'password' missing"
      },
    )
    return QuasselRunner(address, auth, database)
  }

  override fun onCreate() {
    Log.d("QuasselService", "Service created")
  }

  override fun onDestroy() {
    runner?.close()
    super.onDestroy()
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Log.d("QuasselService", "Start Command received")
    super.onStartCommand(intent, flags, startId)
    if (intent != null && this.runner == null) {
      this.runner = newRunner(intent)
    } else if (this.runner == null) {
      Log.e("QuasselService", "Could not start runner, intent missing")
    }
    return START_STICKY
  }

  override fun onBind(intent: Intent): IBinder {
    Log.d("QuasselService", "Binding")
    return QuasselBinder(this.runner ?: newRunner(intent).also { runner = it })
  }

  companion object {
    fun Context.quasselService(
      address: InetSocketAddress,
      auth: Pair<String, String>
    ) = quasselService().apply {
      putExtra("host", address.hostString)
      putExtra("port", address.port)

      val (username, password) = auth
      putExtra("username", username)
      putExtra("password", password)
    }

    fun Context.quasselService() = Intent(applicationContext, QuasselService::class.java)
  }
}
