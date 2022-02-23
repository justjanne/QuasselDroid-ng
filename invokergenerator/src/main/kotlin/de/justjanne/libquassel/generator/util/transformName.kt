import java.util.Locale

/*
 * libquassel
 * Copyright (c) 2021 Janne Mareike Koschinski
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 */

fun transformName(name: String): String =
  name.lowercase(Locale.ROOT).replaceFirstChar {
    it.uppercase(Locale.ROOT)
  }
