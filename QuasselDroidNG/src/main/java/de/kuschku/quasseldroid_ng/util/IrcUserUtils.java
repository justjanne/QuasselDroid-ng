package de.kuschku.quasseldroid_ng.util;

import java.nio.charset.Charset;
import java.util.Locale;

public class IrcUserUtils {
    private IrcUserUtils() {

    }

    public static int getSenderColor(String nick) {
        nick = trimEnd(nick, '_').toLowerCase(Locale.US);
        byte[] data = nick.getBytes(Charset.forName("ISO-8859-1"));
        return (0xf & CRCUtils.qChecksum(data));
    }

    public static String trimEnd(String str, char character) {
        char[] val = str.toCharArray();
        int len = val.length;
        while ((0 < len) && (val[len - 1] == character)) {
            len--;
        }
        return ((len < val.length)) ? str.substring(0, len) : str;
    }

    public static class CRCUtils {
        private CRCUtils() {

        }

        public static int qChecksum(byte[] data) {
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

    public static String getNick(String hostmask) {
        return hostmask.split("!")[0];
    }

    public static String getUser(String hostmask) {
        return getMask(hostmask).split("@")[0];
    }

    public static String getHost(String hostmask) {
        return getMask(hostmask).split("@")[1];
    }


    public static String getMask(String hostmask) {
        return hostmask.split("!")[1];
    }
}