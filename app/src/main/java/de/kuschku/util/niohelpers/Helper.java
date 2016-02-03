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

public class Helper {
    // Making default constructor invisible
    private Helper() {

    }

    public static void printHexDump(@NonNull byte[] data) {
        Log.e("HexDump", "Hexdump following: ");
        String bytes = "";
        String text = "";
        int i;
        for (i = 0; i < data.length; i++) {
            bytes += String.format("%02x ", data[i]);
            text += encodeChar(data[1]);
            if (i > 0 && (i + 1) % 8 == 0) {
                Log.e("HexDump", String.format("%08x ", i - 7) + bytes + text);
                bytes = "";
                text = "";
            }
        }
        Log.e("HexDump", String.format("%08x ", i - 7) + bytes + text);
    }

    private static char encodeChar(byte data) {
        if (data < 127 && data > 32) return (char) data;
        else return '.';
    }
}
