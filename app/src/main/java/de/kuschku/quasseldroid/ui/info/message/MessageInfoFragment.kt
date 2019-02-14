/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.info.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.Gson
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.util.Optional
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.dao.find
import de.kuschku.quasseldroid.persistence.db.QuasselDatabase
import de.kuschku.quasseldroid.util.attachment.AttachmentData
import de.kuschku.quasseldroid.util.attachments.AttachmentApi
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.BetterLinkMovementMethod
import de.kuschku.quasseldroid.util.ui.LinkLongClickMenuHelper
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class MessageInfoFragment : ServiceBoundFragment() {
  @BindView(R.id.list)
  lateinit var list: RecyclerView

  @Inject
  lateinit var database: QuasselDatabase

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.info_message, container, false)
    ButterKnife.bind(this, view)

    val adapter = MessageAttachmentAdapter(true)
    list.adapter = adapter
    list.layoutManager = LinearLayoutManager(list.context)
    list.itemAnimator = DefaultItemAnimator()

    viewModel.session.toLiveData().observe(this, Observer {
      runInBackground {
        val movementMethod = BetterLinkMovementMethod.newInstance()
        movementMethod.setOnLinkLongClickListener(LinkLongClickMenuHelper())

        val messageId = MsgId(arguments?.getLong("messageId") ?: -1)
        val message = database.message().find(messageId)

        val retrofit = Retrofit.Builder()
          .baseUrl("http://192.168.178.29:8080/")
          .addConverterFactory(GsonConverterFactory.create())
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .build()

        val api = retrofit.create(AttachmentApi::class.java)

        val gson = Gson()

        val ircCloudEmbeds = listOf(
          "{\"ts\":1448400805,\"title_link\":\"https://medium.com/slack-developer-blog/everything-you-ever-wanted-to-know-about-unfurling-but-were-afraid-to-ask-or-how-to-make-your-e64b4bb9254\",\"title\":\"Everything you ever wanted to know about unfurling but were afraid to ask /or/ How to make your…\",\"text\":\"Let’s start with the most obvious question first. This is what an “unfurl” is:\",\"service_name\":\"Medium\",\"service_icon\":\"https://cdn-images-1.medium.com/fit/c/304/304/1*a1O3xhOq8KWSibZF6Ze5xQ.png\",\"image_width\":170,\"image_url\":\"https://cdn-images-1.medium.com/max/1200/1*QOMaDLcO8rExD0ctBV3BWg.png\",\"image_height\":250,\"image_bytes\":695475,\"from_url\":\"https://medium.com/slack-developer-blog/everything-you-ever-wanted-to-know-about-unfurling-but-were-afraid-to-ask-or-how-to-make-your-e64b4bb9254\",\"fields\":[{\"value\":\"10 min read\",\"title\":\"Reading time\",\"short\":true}]}",
          "{\"title_link\":\"https://www.youtube.com/watch?v=_Cd9FYO9Rh4\",\"title\":\"Scorpions   Berlin Philharmonic Orchestra   Rock You Like a Hurricane\",\"service_url\":\"https://www.youtube.com/\",\"service_name\":\"YouTube\",\"service_icon\":\"https://a.slack-edge.com/2089/img/unfurl_icons/youtube.png\",\"from_url\":\"https://www.youtube.com/watch?v=_Cd9FYO9Rh4\",\"author_name\":\"Nathan Allen\",\"author_link\":\"https://www.youtube.com/user/27caboose\"}",
          "{\"title_link\":\"http://www.kn-online.de/Nachrichten/Panorama/Lehrerin-fuehrt-Netflix-Experiment-durch-das-Ergebnis-erschreckt\",\"title\":\"Lehrerin führt Netflix-Experiment durch – das Ergebnis erschreckt\",\"text\":\"Rebecca Schiller aus Potsdam unterrichtet am Marie-Curie-Gymnasium im Havelland. Als „Frau Lehrerin“ ist sie auf Twitter eine kleine Berühmtheit – vor allem, seit sie dort eine unkonventionelle Unterrichtsmethode veröffentlicht hat.\",\"service_name\":\"KN - Kieler Nachrichten\",\"service_icon\":\"http://www.kn-online.de/bundles/molasset/images/sites/desktop/kn/apple-touch-icon.png\",\"image_width\":500,\"image_url\":\"http://www.kn-online.de/var/storage/images/rnd/nachrichten/panorama/uebersicht/lehrerin-fuehrt-netflix-experiment-durch-das-ergebnis-erschreckt/712106427-8-ger-DE/Lehrerin-fuehrt-Netflix-Experiment-durch-das-Ergebnis-erschreckt_reference_2_1.jpg\",\"image_height\":250,\"image_bytes\":65450,\"from_url\":\"http://www.kn-online.de/Nachrichten/Panorama/Lehrerin-fuehrt-Netflix-Experiment-durch-das-Ergebnis-erschreckt\"}"
        ).map {
          gson.fromJson(it, AttachmentData::class.java)
        }

        val urls = listOf(
          "https://quasseldroid.info/",
          "https://www.youtube.com/watch?v=IfXMN3VhikA",
          "https://twitter.com/dw_politik/status/1092872739445104640",
          "https://soundcloud.com/kevin-manthei/sto-ds9-main-title",
          "https://twitter.com/raketenlurch/status/1093991675209416704",
          "https://arxius.io/i/25287151",
          "https://i.imgur.com/W8DjyWk.jpg",
          "https://imgur.com/W8DjyWk",
          "https://rp-online.de/panorama/deutschland/buchenbach-transporter-hindert-krankenwagen-mit-kind-an-bord-minutenlang-am-ueberholen_aid-35758071",
          "http://m.kn-online.de/Nachrichten/Panorama/Lehrerin-fuehrt-Netflix-Experiment-durch-das-Ergebnis-erschreckt",
          "https://media.ccc.de/v/35c3-9904-the_social_credit_system",
          "https://medium.com/slack-developer-blog/everything-you-ever-wanted-to-know-about-unfurling-but-were-afraid-to-ask-or-how-to-make-your-e64b4bb9254"
        )

        activity?.runOnUiThread {
          combineLatest(urls.map {
            api.retrieve(it)
              .map { Optional.of(it) }
              .onErrorReturnItem(Optional.empty())
          }).map {
            it.mapNotNull(Optional<AttachmentData>::orNull)
          }.toLiveData().observe(this, Observer {
            adapter.submitList(ircCloudEmbeds + it)
          })
        }
      }
    })

    return view
  }
}
