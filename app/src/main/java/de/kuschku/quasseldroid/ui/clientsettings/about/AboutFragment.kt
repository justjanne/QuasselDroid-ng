/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.quasseldroid.ui.clientsettings.about

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import dagger.android.support.DaggerFragment
import de.kuschku.quasseldroid.BuildConfig
import de.kuschku.quasseldroid.R

class AboutFragment : DaggerFragment() {

  @BindView(R.id.version_container)
  lateinit var versionContainer: View

  @BindView(R.id.version)
  lateinit var version: TextView

  @BindView(R.id.action_website)
  lateinit var website: Button

  @BindView(R.id.action_source)
  lateinit var source: Button

  @BindView(R.id.action_donate)
  lateinit var donate: Button

  @BindView(R.id.action_privacy_policy)
  lateinit var privacyPolicy: Button

  @BindView(R.id.authors)
  lateinit var authors: RecyclerView

  @BindView(R.id.acknowledgements)
  lateinit var acknowledgements: RecyclerView

  @BindView(R.id.translators)
  lateinit var translators: RecyclerView

  @BindView(R.id.libraries)
  lateinit var libraries: RecyclerView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.preferences_about, container, false)
    ButterKnife.bind(this, view)

    version.text = BuildConfig.VERSION_NAME

    versionContainer.setOnClickListener {
      val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      clipboard.setPrimaryClip(ClipData.newPlainText(null, BuildConfig.VERSION_NAME))
      Toast.makeText(requireContext(), R.string.info_copied_version, Toast.LENGTH_LONG).show()
    }

    website.setOnClickListener {
      context?.startActivity(Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://quasseldroid.info/")
      })
    }

    source.setOnClickListener {
      context?.startActivity(Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://git.kuschku.de/justJanne/QuasselDroid-ng")
      })
    }

    donate.setOnClickListener {
      context?.startActivity(Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://www.patreon.com/justjanne")
      })
    }

    privacyPolicy.setOnClickListener {
      context?.startActivity(Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("http://quasseldroid.info/privacy-policy/")
      })
    }

    val apache2 = License(
      shortName = "Apache-2.0",
      fullName = "Apache License",
      text = R.raw.license_apache_2
    )

    libraries.layoutManager = LinearLayoutManager(context)
    libraries.itemAnimator = null
    libraries.adapter = LibraryAdapter(listOf(
      Library(
        name = "AndroidX",
        license = apache2,
        url = "https://developer.android.com/jetpack/androidx/"
      ),
      Library(
        name = "atinject",
        license = apache2,
        url = "https://code.google.com/archive/p/atinject/"
      ),
      Library(
        name = "Apache Commons Codec",
        version = "1.13",
        license = apache2,
        url = "https://commons.apache.org/proper/commons-codec/"
      ),
      Library(
        name = "AutoService",
        version = "1.0-rc6",
        license = apache2,
        url = "https://github.com/google/auto/tree/master/service"
      ),
      Library(
        name = "Better Link Movement Method",
        version = "2.1.0",
        license = apache2,
        url = "https://github.com/Saketme/Better-Link-Movement-Method"
      ),
      Library(
        name = "Butter Knife",
        version = "10.1.0",
        license = apache2,
        url = "http://jakewharton.github.io/butterknife/"
      ),
      Library(
        name = "Dagger 2",
        version = "2.24",
        license = apache2,
        url = "https://google.github.io/dagger/"
      ),
      Library(
        name = "Dracula",
        license = License(
          shortName = "MIT",
          fullName = "The MIT License (MIT)",
          text = R.raw.license_dracula
        ),
        url = "https://draculatheme.com/"
      ),
      Library(
        name = "FloatingActionButtonSpeedDial",
        version = "3.0.0",
        license = apache2,
        url = "https://github.com/leinardi/FloatingActionButtonSpeedDial"
      ),
      Library(
        name = "Glide",
        version = "4.9.0",
        license = apache2,
        url = "https://bumptech.github.io/glide/"
      ),
      Library(
        name = "Gson",
        version = "2.8.5",
        license = apache2,
        url = "https://github.com/google/gson"
      ),
      Library(
        name = "Gruvbox",
        license = License(
          shortName = "MIT",
          fullName = "The MIT License (MIT)",
          text = R.raw.license_gruvbox
        ),
        url = "https://github.com/morhetz/gruvbox"
      ),
      Library(
        name = "KotlinPoet",
        version = "1.3.0",
        license = apache2,
        url = "https://github.com/square/kotlinpoet"
      ),
      Library(
        name = "JetBrains Java Annotations",
        version = "17.0.0",
        license = apache2,
        url = "https://github.com/JetBrains/java-annotations"
      ),
      Library(
        name = "Kotlin Standard Library",
        version = "1.5.10",
        license = apache2,
        url = "https://kotlinlang.org/"
      ),
      Library(
        name = "LeakCanary",
        version = "1.6.3",
        license = apache2,
        url = "https://github.com/square/leakcanary"
      ),
      Library(
        name = "Material Components",
        version = "1.1.0-alpha10",
        license = apache2,
        url = "https://github.com/material-components/material-components-android"
      ),
      Library(
        name = "Material Design Icons: Community",
        license = License(
          shortName = "SIL Open Font License v1.1",
          fullName = "SIL OPEN FONT LICENSE",
          text = R.raw.license_materialdesignicons
        ),
        url = "https://github.com/Templarian/MaterialDesign"
      ),
      Library(
        name = "Material Design Icons: Google",
        version = "3.0.1",
        license = apache2,
        url = "https://github.com/google/material-design-icons"
      ),
      Library(
        name = "Material Dialogs",
        version = "0.9.6.0",
        license = License(
          shortName = "MIT",
          fullName = "The MIT License (MIT)",
          text = R.raw.license_materialdialogs
        ),
        url = "https://github.com/afollestad/material-dialogs"
      ),
      Library(
        name = "MaterialProgressBar",
        version = "1.6.1",
        license = apache2,
        url = "https://github.com/DreaminginCodeZH/MaterialProgressBar"
      ),
      Library(
        name = "Quassel",
        version = "0.13.1",
        license = License(
          shortName = "GPLv3",
          fullName = "GNU GENERAL PUBLIC LICENSE",
          text = R.raw.license_gpl_v3
        ),
        url = "https://quassel-irc.org/"
      ),
      Library(
        name = "Reactive Streams",
        version = "1.0.2",
        license = License(
          shortName = "CC0",
          fullName = "Creative Commons CC0 1.0 Universal",
          text = R.raw.license_cc_0
        ),
        url = "http://www.reactive-streams.org/"
      ),
      Library(
        name = "ReactiveNetwork",
        version = "3.0.6",
        license = apache2,
        url = "https://github.com/pwittchen/ReactiveNetwork"
      ),
      Library(
        name = "RecyclerView-FastScroll",
        version = "2.0.",
        license = apache2,
        url = "https://github.com/timusus/RecyclerView-FastScroll"
      ),
      Library(
        name = "Retrofit",
        version = "2.6.1",
        license = apache2,
        url = "https://square.github.io/retrofit/"
      ),
      Library(
        name = "RxJava",
        version = "2.2.12",
        license = apache2,
        url = "https://github.com/ReactiveX/RxJava"
      ),
      Library(
        name = "Solarized",
        license = License(
          shortName = "MIT",
          fullName = "The MIT License (MIT)",
          text = R.raw.license_solarized
        ),
        url = "http://ethanschoonover.com/solarized"
      ),
      Library(
        name = "ThreeTen backport project",
        version = "1.4.10",
        license = License(
          shortName = "BSD 3-clause",
          text = R.raw.license_threetenbp
        ),
        url = "http://www.threeten.org/threetenbp/"
      )
    ))
    libraries.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    ViewCompat.setNestedScrollingEnabled(libraries, false)

    authors.layoutManager = LinearLayoutManager(context)
    authors.itemAnimator = null
    authors.adapter = ContributorAdapter(listOf(
      Contributor(
        name = "Janne Mareike Koschinski",
        nickName = "justJanne",
        description = getString(R.string.contributor_description_justjanne)
      )
    ))
    authors.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    ViewCompat.setNestedScrollingEnabled(authors, false)

    acknowledgements.layoutManager = LinearLayoutManager(context)
    acknowledgements.itemAnimator = null
    acknowledgements.adapter = ContributorAdapter(listOf(
      Contributor(
        name = "Frederik M. J. Vestre",
        nickName = "freqmod",
        description = getString(R.string.contributor_description_freqmod)
      ),
      Contributor(
        name = "Martin “Java Sucks” Sandsmark",
        nickName = "sandsmark",
        description = getString(R.string.contributor_description_sandsmark)
      ),
      Contributor(
        name = "Magnus Fjell",
        nickName = "magnuf",
        description = getString(R.string.contributor_description_magnuf)
      ),
      Contributor(
        name = "Ken Børge Viktil",
        nickName = "Kenji",
        description = getString(R.string.contributor_description_kenji)
      )
    ))
    acknowledgements.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    ViewCompat.setNestedScrollingEnabled(acknowledgements, false)

    translators.layoutManager = LinearLayoutManager(context)
    translators.itemAnimator = null
    translators.adapter = TranslatorAdapter(listOf(
      Translator(
        name = "Janne Mareike Koschinski",
        language = R.string.preference_language_entry_de
      ),
      Translator(
        name = "xi <xi@nuxi.ca>",
        language = R.string.preference_language_entry_fr_ca
      ),
      Translator(
        name = "Francesco Roberto",
        language = R.string.preference_language_entry_it
      ),
      Translator(
        name = "TDa_",
        language = R.string.preference_language_entry_lt
      ),
      Translator(
        name = "Robbe Van der Gucht",
        language = R.string.preference_language_entry_nl
      ),
      Translator(
        name = "Exterminador",
        language = R.string.preference_language_entry_pt
      ),
      Translator(
        name = "Luka Ilić",
        language = R.string.preference_language_entry_sr
      )
    ))
    translators.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    ViewCompat.setNestedScrollingEnabled(translators, false)

    return view
  }
}
