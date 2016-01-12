package de.kuschku.util.niohelpers;

public class Helper {
    // Making default constructor invisible
    private Helper() {

    }

    public static void printHexDump(byte[] data) {
        System.out.println("Hexdump following: ");
        String bytes = "";
        String text = "";
        int i;
        for (i = 0; i < data.length; i++) {
            bytes += String.format("%02x ", data[i]);
            text += encodeChar(data[1]);
            if (i > 0 && (i + 1) % 8 == 0) {
                System.out.println(String.format("%08x ", i - 7) + bytes + text);
                bytes = "";
                text = "";
            }
        }
        System.out.println(String.format("%08x ", i - 7) + bytes + text);
    }

    private static char encodeChar(byte data) {
        if (data < 127 && data > 32) return (char) data;
        else return '.';
    }
}
