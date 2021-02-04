package de.kuschku.quasseldroid

import de.kuschku.quasseldroid.protocol.ChainedByteBuffer
import de.kuschku.quasseldroid.protocol.IntSerializer
import de.kuschku.quasseldroid.protocol.UIntSerializer
import de.kuschku.quasseldroid.util.CoroutineChannel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.InetSocketAddress
import java.nio.ByteBuffer

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testNetworking() {
    runBlocking {
      val sizeBuffer = ByteBuffer.allocateDirect(4)
      val sendBuffer = ChainedByteBuffer(direct = true)
      val channel = CoroutineChannel()
      channel.connect(InetSocketAddress("kuschku.de", 4242))
      val readBuffer = ByteBuffer.allocateDirect(4)
      UIntSerializer.serialize(sendBuffer, 0x42b3_3f00u)
      IntSerializer.serialize(sendBuffer, 2)
      UIntSerializer.serialize(sendBuffer, 0x8000_0000u)
      channel.write(sendBuffer)
      channel.read(readBuffer)
      readBuffer.flip()
      println(IntSerializer.deserialize(readBuffer))
    }
  }
}

