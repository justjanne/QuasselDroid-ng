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

package de.kuschku.util.irc;

import android.support.annotation.NonNull;

import java.nio.charset.Charset;
import java.util.Locale;

public class IrcUserUtils {
    private IrcUserUtils() {

    }

    public static int getSenderColor(@NonNull String nick) {
        nick = trimEnd(nick, '_').toLowerCase(Locale.US);
        byte[] data = nick.getBytes(Charset.forName("ISO-8859-1"));
        return (0xf & CRCUtils.qChecksum(data));
    }

    @NonNull
    private static String trimEnd(@NonNull String str, char character) {
        char[] val = str.toCharArray();
        int len = val.length;
        while ((0 < len) && (val[len - 1] == character)) {
            len--;
        }
        return ((len < val.length)) ? str.substring(0, len) : str;
    }

    @NonNull
    public static String getNick(@NonNull String hostmask) {
        return hostmask.split("!", -1)[0];
    }

    @NonNull
    public static String getUser(@NonNull String hostmask) {
        return getMask(hostmask).split("@", -1)[0];
    }

    @NonNull
    public static String getHost(@NonNull String hostmask) {
        return getMask(hostmask).split("@", -1)[1];
    }

    @NonNull
    public static String getMask(@NonNull String hostmask) {
        return hostmask.split("!", -1)[1];
    }

    public static class CRCUtils {
        private CRCUtils() {

        }

        public static int qChecksum(@NonNull byte[] data) {
            int crc = 0xffff;
            int crcHighBitMask = 0x8000;

            for (byte b : data) {
                int c = reflect(b, 8);
                for (int j = 0x80; j > 0; j >>= 1) {
                    int highBit = crc & crcHighBitMask;
                    crc <<= 1;
                    if ((c & j) > 0) {
                        highBit ^= crcHighBitMask;
                    }
                    if (highBit > 0) {
                        crc ^= 0x1021;
                    }
                }
            }

            crc = reflect(crc, 16);
            crc ^= 0xffff;
            crc &= 0xffff;

            return crc;
        }

        private static int reflect(int crc, int n) {
            int j = 1, crcout = 0;
            for (int i = (1 << (n - 1)); i > 0; i >>= 1) {
                if ((crc & i) > 0) {
                    crcout |= j;
                }
                j <<= 1;
            }
            return crcout;
        }
    }
}
