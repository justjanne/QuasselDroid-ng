/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.util.niohelpers;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class Helper {
    // Making default constructor invisible
    private Helper() {

    }

    public static void printHexDump(String prefix, @NonNull byte[] data) {
        List<String> strs = new LinkedList<>();
        Log.e("HexDump" + prefix, "========");
        String bytes = "";
        int i;
        for (i = 0; i < data.length; i++) {
            bytes += String.format("%02x ", data[i]);
            if (i > 0 && (i + 1) % 32 == 0) {
                strs.add(bytes);
                bytes = "";
            }
        }
        strs.add(bytes);
        for (int j = 0; j < strs.size(); j++) {
            Log.e("HexDump" + prefix + ":" + j, strs.get(j));
        }
    }

    public static void printHexDump(@NonNull byte[] data) {
        printHexDump("", data);
    }
}
