/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.clientsettings.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import dagger.android.support.DaggerFragment
import de.kuschku.quasseldroid.BuildConfig
import de.kuschku.quasseldroid.R

class AboutFragment : DaggerFragment() {

  @BindView(R.id.version)
  lateinit var version: TextView

  @BindView(R.id.action_website)
  lateinit var website: Button

  @BindView(R.id.action_github)
  lateinit var github: Button

  @BindView(R.id.action_privacy_policy)
  lateinit var privacyPolicy: Button

  @BindView(R.id.contributors)
  lateinit var contributors: RecyclerView

  @BindView(R.id.libraries)
  lateinit var libraries: RecyclerView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.preferences_about, container, false)
    ButterKnife.bind(this, view)

    version.text = BuildConfig.VERSION_NAME

    website.setOnClickListener {
      val intent = Intent(Intent.ACTION_VIEW)
      intent.data = Uri.parse("https://quasseldroid.info/")
      context?.startActivity(intent)
    }

    github.setOnClickListener {
      val intent = Intent(Intent.ACTION_VIEW)
      intent.data = Uri.parse("https://github.com/justjanne/quasseldroid-ng")
      context?.startActivity(intent)
    }

    privacyPolicy.setOnClickListener {
      val intent = Intent(Intent.ACTION_VIEW)
      intent.data = Uri.parse("http://quasseldroid.info/privacy-policy/")
      context?.startActivity(intent)
    }

    val apache2 = License(
      shortName = "Apache-2.0",
      fullName = "Apache License",
      text = R.string.license_apache_2
    )

    libraries.layoutManager = LinearLayoutManager(context)
    libraries.adapter = LibraryAdapter(listOf(
      Library(
        name = "Android Architecture Components: Lifecycle",
        version = "1.1.1",
        license = apache2,
        url = "https://android.googlesource.com/platform/frameworks/support/+/master/lifecycle"
      ),
      Library(
        name = "Android Architecture Components: Paging",
        version = "1.0.0-alpha7",
        license = apache2,
        url = "https://android.googlesource.com/platform/frameworks/support/+/master/paging"
      ),
      Library(
        name = "Android Architecture Components: Persistence",
        version = "1.1.0-beta3",
        license = apache2,
        url = "https://android.googlesource.com/platform/frameworks/support/+/master/persistence"
      ),
      Library(
        name = "Android Architecture Components: Room",
        version = "1.1.0-beta3",
        license = apache2,
        url = "https://android.googlesource.com/platform/frameworks/support/+/master/persistence"
      ),
      Library(
        name = "Android Sliding Up Panel",
        version = "3.5.0",
        license = apache2,
        url = "https://github.com/umano/AndroidSlidingUpPanel"
      ),
      Library(
        name = "Android Support Library",
        version = "27.1.1",
        license = apache2,
        url = "https://android.googlesource.com/platform/frameworks/support/+/master"
      ),
      Library(
        name = "Android Support Library: Constraint Layout",
        version = "1.1.0-beta6",
        license = apache2,
        url = "https://android.googlesource.com/platform/frameworks/opt/sherpa/+/studio-3.0/constraintlayout"
      ),
      Library(
        name = "atinject",
        license = apache2,
        url = "https://code.google.com/archive/p/atinject/"
      ),
      Library(
        name = "AutoService",
        version = "1.0-rc4",
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
        version = "8.8.1",
        license = apache2,
        url = "http://jakewharton.github.io/butterknife/"
      ),
      Library(
        name = "Dagger 2",
        version = "2.15",
        license = apache2,
        url = "https://google.github.io/dagger/"
      ),
      Library(
        name = "Dracula",
        license = License(
          shortName = "MIT",
          fullName = "The MIT License (MIT)",
          text = R.string.license_dracula
        ),
        url = "https://draculatheme.com/"
      ),
      Library(
        name = "Glide",
        version = "4.6.1",
        license = apache2,
        url = "https://bumptech.github.io/glide/"
      ),
      Library(
        name = "GlobTransformer",
        version = "4.6.1",
        license = License(
          shortName = "CC BY-SA 3.0",
          fullName = "Creative Commons Attribution-ShareAlike 3.0 Unported",
          text = R.string.license_cc_by_sa_3_0
        ),
        url = "https://bumptech.github.io/glide/"
      ),
      Library(
        name = "Gson",
        version = "2.8.2",
        license = apache2,
        url = "https://github.com/google/gson"
      ),
      Library(
        name = "Gruvbox",
        license = License(
          shortName = "MIT",
          fullName = "The MIT License (MIT)",
          text = R.string.license_gruvbox
        ),
        url = "https://github.com/morhetz/gruvbox"
      ),
      Library(
        name = "JavaPoet",
        version = "1.10.0",
        license = apache2,
        url = "https://github.com/square/javapoet"
      ),
      Library(
        name = "JetBrains Java Annotations",
        version = "16.0.1",
        license = apache2,
        url = "https://github.com/JetBrains/java-annotations"
      ),
      Library(
        name = "Kotlin Standard Library",
        version = "1.2.41",
        license = apache2,
        url = "https://kotlinlang.org/"
      ),
      Library(
        name = "LeakCanary",
        version = "1.5.4",
        license = apache2,
        url = "https://github.com/square/leakcanary"
      ),
      Library(
        name = "Material Design Icons: Community",
        license = License(
          shortName = "SIL Open Font License v1.1",
          fullName = "SIL OPEN FONT LICENSE",
          text = R.string.license_materialdesignicons
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
          text = R.string.license_materialdialogs
        ),
        url = "https://github.com/afollestad/material-dialogs"
      ),
      Library(
        name = "MaterialProgressBar",
        version = "1.4.2",
        license = apache2,
        url = "https://github.com/DreaminginCodeZH/MaterialProgressBar"
      ),
      Library(
        name = "Quassel",
        version = "0.13.0",
        license = License(
          shortName = "GPLv3",
          fullName = "GNU GENERAL PUBLIC LICENSE",
          text = R.string.license_gpl_v3
        ),
        url = "https://quassel-irc.org/"
      ),
      Library(
        name = "Reactive Streams",
        version = "1.0.2",
        license = License(
          shortName = "CC0",
          fullName = "Creative Commons CC0 1.0 Universal",
          text = R.string.license_cc_0
        ),
        url = "https://github.com/ReactiveX/RxJava"
      ),
      Library(
        name = "Retrofit",
        version = "2.4.0",
        license = apache2,
        url = "https://square.github.io/retrofit/"
      ),
      Library(
        name = "RxJava",
        version = "2.1.9",
        license = apache2,
        url = "https://github.com/ReactiveX/RxJava"
      ),
      Library(
        name = "Solarized",
        license = License(
          shortName = "MIT",
          fullName = "The MIT License (MIT)",
          text = R.string.license_solarized
        ),
        url = "http://ethanschoonover.com/solarized"
      ),
      Library(
        name = "ThreeTen backport project",
        version = "1.3.6",
        license = License(
          shortName = "BSD 3-clause",
          text = R.string.license_threetenbp
        ),
        url = "http://www.threeten.org/threetenbp/"
      )
    ))
    libraries.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    ViewCompat.setNestedScrollingEnabled(libraries, false)

    contributors.layoutManager = LinearLayoutManager(context)
    contributors.adapter = ContributorAdapter(listOf(
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
      ),
      Contributor(
        name = "Janne Koschinski",
        nickName = "justJanne",
        description = getString(R.string.contributor_description_justjanne)
      )
    ))
    contributors.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    ViewCompat.setNestedScrollingEnabled(contributors, false)

    return view
  }
}
