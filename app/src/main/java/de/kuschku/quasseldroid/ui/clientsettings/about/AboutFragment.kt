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
import dagger.android.support.DaggerFragment
import de.kuschku.quasseldroid.BuildConfig
import de.kuschku.quasseldroid.R

class AboutFragment : DaggerFragment() {
  lateinit var versionContainer: View
  lateinit var version: TextView
  lateinit var website: Button
  lateinit var source: Button
  lateinit var privacyPolicy: Button
  lateinit var authors: RecyclerView
  lateinit var acknowledgements: RecyclerView
  lateinit var translators: RecyclerView
  lateinit var libraries: RecyclerView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.preferences_about, container, false)
    this.versionContainer = view.findViewById(R.id.version_container)
    this.version = view.findViewById(R.id.version)
    this.website = view.findViewById(R.id.action_website)
    this.source = view.findViewById(R.id.action_source)
    this.privacyPolicy = view.findViewById(R.id.action_privacy_policy)
    this.authors = view.findViewById(R.id.authors)
    this.acknowledgements = view.findViewById(R.id.acknowledgements)
    this.translators = view.findViewById(R.id.translators)
    this.libraries = view.findViewById(R.id.libraries)

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
        license = apache2,
        url = "https://commons.apache.org/proper/commons-codec/"
      ),
      Library(
        name = "AutoService",
        license = apache2,
        url = "https://github.com/google/auto/tree/master/service"
      ),
      Library(
        name = "Better Link Movement Method",
        license = apache2,
        url = "https://github.com/Saketme/Better-Link-Movement-Method"
      ),
      Library(
        name = "Dagger 2",
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
        license = apache2,
        url = "https://github.com/leinardi/FloatingActionButtonSpeedDial"
      ),
      Library(
        name = "Glide",
        license = apache2,
        url = "https://bumptech.github.io/glide/"
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
        license = apache2,
        url = "https://github.com/square/kotlinpoet"
      ),
      Library(
        name = "JetBrains Java Annotations",
        license = apache2,
        url = "https://github.com/JetBrains/java-annotations"
      ),
      Library(
        name = "Kotlin Standard Library",
        license = apache2,
        url = "https://kotlinlang.org/"
      ),
      Library(
        name = "LeakCanary",
        license = apache2,
        url = "https://github.com/square/leakcanary"
      ),
      Library(
        name = "Material Components",
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
        license = apache2,
        url = "https://github.com/google/material-design-icons"
      ),
      Library(
        name = "Material Dialogs",
        license = License(
          shortName = "MIT",
          fullName = "The MIT License (MIT)",
          text = R.raw.license_materialdialogs
        ),
        url = "https://github.com/afollestad/material-dialogs"
      ),
      Library(
        name = "MaterialProgressBar",
        license = apache2,
        url = "https://github.com/DreaminginCodeZH/MaterialProgressBar"
      ),
      Library(
        name = "Quassel",
        license = License(
          shortName = "GPLv3",
          fullName = "GNU GENERAL PUBLIC LICENSE",
          text = R.raw.license_gpl_v3
        ),
        url = "https://quassel-irc.org/"
      ),
      Library(
        name = "Reactive Streams",
        license = License(
          shortName = "CC0",
          fullName = "Creative Commons CC0 1.0 Universal",
          text = R.raw.license_cc_0
        ),
        url = "http://www.reactive-streams.org/"
      ),
      Library(
        name = "ReactiveNetwork",
        license = apache2,
        url = "https://github.com/pwittchen/ReactiveNetwork"
      ),
      Library(
        name = "RecyclerView-FastScroll",
        license = apache2,
        url = "https://github.com/timusus/RecyclerView-FastScroll"
      ),
      Library(
        name = "Retrofit",
        license = apache2,
        url = "https://square.github.io/retrofit/"
      ),
      Library(
        name = "RxJava",
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
