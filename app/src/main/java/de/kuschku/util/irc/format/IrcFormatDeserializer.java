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

package de.kuschku.util.irc.format;


import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.kuschku.quasseldroid_ng.ui.theme.AppContext;

import static de.kuschku.util.AndroidAssert.assertNotNull;

/**
 * A helper class to turn mIRC formatted Strings into Android’s SpannableStrings with the same
 * color and format codes
 */
public class IrcFormatDeserializer {
    public static final int CODE_BOLD = 0x02;
    public static final int CODE_COLOR = 0x03;
    public static final int CODE_ITALIC = 0x1D;
    public static final int CODE_UNDERLINE = 0x1F;
    public static final int CODE_SWAP = 0x16;
    public static final int CODE_RESET = 0x0F;

    private final AppContext context;

    public IrcFormatDeserializer(AppContext context) {
        this.context = context;
    }

    /**
     * Try to read a number from a String in specified bounds
     *
     * @param str   String to be read from
     * @param start Start index (inclusive)
     * @param end   End index (exclusive)
     * @return The byte represented by the digits read from the string
     */
    public static byte readNumber(@NonNull String str, int start, int end) {
        String result = str.substring(start, end);
        if (result.isEmpty())
            return -1;
        else
            return (byte) Integer.parseInt(result, 10);
    }

    /**
     * @param str   String to be searched in
     * @param start Start position (inclusive)
     * @return Index of first character that is not a digit
     */
    private static int findEndOfNumber(@NonNull String str, int start) {
        Set<Character> validCharCodes = new HashSet<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
        String searchFrame = str.substring(start);
        int i;
        for (i = 0; i < 2 && i < searchFrame.length(); i++) {
            if (!validCharCodes.contains(searchFrame.charAt(i))) {
                break;
            }
        }
        return start + i;
    }

    @Nullable
    private static IrcFormat fromId(char id) {
        switch (id) {
            case CODE_BOLD:
                return new BoldIrcFormat();
            case CODE_ITALIC:
                return new ItalicIrcFormat();
            case CODE_UNDERLINE:
                return new UnderlineIrcFormat();
            default:
                return null;
        }
    }

    /**
     * Function to handle mIRC formatted strings
     *
     * @param str mIRC formatted String
     * @return a CharSequence with Android’s span format representing the input string
     */
    @Nullable
    public CharSequence formatString(@Nullable String str) {
        if (str == null) return null;

        SpannableStringBuilder plainText = new SpannableStringBuilder();
        FormatDescription bold = null;
        FormatDescription italic = null;
        FormatDescription underline = null;
        FormatDescription color = null;
        boolean colorize = context.settings().preferenceColors.get();

        // Iterating over every character
        for (int i = 0; i < str.length(); i++) {
            char character = str.charAt(i);
            switch (character) {
                case CODE_BOLD: {
                    if (!colorize) continue;

                    // If there is an element on stack with the same code, close it
                    if (bold != null) {
                        bold.apply(plainText, plainText.length());
                        bold = null;
                        // Otherwise create a new one
                    } else {
                        IrcFormat format = fromId(character);
                        assertNotNull(format);
                        bold = new FormatDescription(plainText.length(), format);
                    }
                }
                break;
                case CODE_ITALIC: {
                    if (!colorize) continue;

                    // If there is an element on stack with the same code, close it
                    if (italic != null) {
                        italic.apply(plainText, plainText.length());
                        italic = null;
                        // Otherwise create a new one
                    } else {
                        IrcFormat format = fromId(character);
                        assertNotNull(format);
                        italic = new FormatDescription(plainText.length(), format);
                    }
                }
                break;
                case CODE_UNDERLINE: {
                    if (!colorize) continue;

                    // If there is an element on stack with the same code, close it
                    if (underline != null) {
                        underline.apply(plainText, plainText.length());
                        underline = null;
                        // Otherwise create a new one
                    } else {
                        IrcFormat format = fromId(character);
                        assertNotNull(format);
                        underline = new FormatDescription(plainText.length(), format);
                    }
                }
                break;
                case CODE_COLOR: {
                    if (!colorize) continue;

                    int foregroundStart = i + 1;
                    int foregroundEnd = findEndOfNumber(str, foregroundStart);
                    // If we have a foreground element
                    if (foregroundEnd > foregroundStart) {
                        byte foreground = readNumber(str, foregroundStart, foregroundEnd);

                        byte background = -1;
                        int backgroundEnd = -1;
                        // If we have a background code, read it
                        if (str.length() > foregroundEnd && str.charAt(foregroundEnd) == ',') {
                            backgroundEnd = findEndOfNumber(str, foregroundEnd + 1);
                            background = readNumber(str, foregroundEnd + 1, backgroundEnd);
                        }
                        // If previous element was also a color element, try to reuse background
                        if (color != null) {
                            // Apply old format
                            color.apply(plainText, plainText.length());
                            // Reuse old background, if possible
                            if (background == -1)
                                background = ((ColorIrcFormat) color.format).background;
                        }
                        // Add new format
                        color = new FormatDescription(plainText.length(), new ColorIrcFormat(foreground, background));

                        // i points in front of the next character
                        i = ((backgroundEnd == -1) ? foregroundEnd : backgroundEnd) - 1;

                        // Otherwise assume this is a closing tag
                    } else if (color != null) {
                        color.apply(plainText, plainText.length());
                        color = null;
                    }
                }
                break;
                case CODE_SWAP: {
                    if (!colorize) continue;

                    // If we have a color tag before, apply it, and create a new one with swapped colors
                    if (color != null) {
                        color.apply(plainText, plainText.length());
                        color = new FormatDescription(plainText.length(), ((ColorIrcFormat) color.format).copySwapped());
                    }
                }
                break;
                case CODE_RESET: {
                    if (!colorize) continue;

                    // End all formatting tags
                    if (bold != null) {
                        bold.apply(plainText, plainText.length());
                        bold = null;
                    }
                    if (italic != null) {
                        italic.apply(plainText, plainText.length());
                        italic = null;
                    }
                    if (underline != null) {
                        underline.apply(plainText, plainText.length());
                        underline = null;
                    }
                    if (color != null) {
                        color.apply(plainText, plainText.length());
                        color = null;
                    }
                }
                break;
                default: {
                    // Just append it, if it’s not special
                    plainText.append(character);
                }
            }
        }

        // End all formatting tags
        if (bold != null) {
            bold.apply(plainText, plainText.length());
        }
        if (italic != null) {
            italic.apply(plainText, plainText.length());
        }
        if (underline != null) {
            underline.apply(plainText, plainText.length());
        }
        if (color != null) {
            color.apply(plainText, plainText.length());
        }
        return plainText;
    }

    private interface IrcFormat {
        void applyTo(@NonNull SpannableStringBuilder editable, int from, int to);

        byte id();
    }

    public interface ColorSupplier {
        @ColorInt
        int ircColor(byte color);
    }

    private static class FormatDescription {
        public final int start;
        @NonNull
        public final IrcFormat format;

        public FormatDescription(int start, @NonNull IrcFormat format) {
            this.start = start;
            this.format = format;
        }

        public void apply(@NonNull SpannableStringBuilder editable, int end) {
            format.applyTo(editable, start, end);
        }
    }

    private static class ItalicIrcFormat implements IrcFormat {
        @Override
        public void applyTo(@NonNull SpannableStringBuilder editable, int from, int to) {
            editable.setSpan(new ItalicSpan(), from, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        @Override
        public byte id() {
            return CODE_ITALIC;
        }
    }

    private static class UnderlineIrcFormat implements IrcFormat {
        @Override
        public void applyTo(@NonNull SpannableStringBuilder editable, int from, int to) {
            editable.setSpan(new UnderlineSpan(), from, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        @Override
        public byte id() {
            return CODE_UNDERLINE;
        }
    }

    private static class BoldIrcFormat implements IrcFormat {
        @Override
        public void applyTo(@NonNull SpannableStringBuilder editable, int from, int to) {
            editable.setSpan(new BoldSpan(), from, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        @Override
        public byte id() {
            return CODE_BOLD;
        }
    }

    private class ColorIrcFormat implements IrcFormat {
        private final byte foreground;
        private final byte background;

        public ColorIrcFormat(byte foreground, byte background) {
            this.foreground = foreground;
            this.background = background;
        }

        @Override
        public void applyTo(@NonNull SpannableStringBuilder editable, int from, int to) {
            int[] mircColors = context.themeUtil().res.mircColors;
            if (foreground != -1) {
                editable.setSpan(new ForegroundColorSpan(mircColors[foreground % 16]), from, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            if (background != -1) {
                editable.setSpan(new BackgroundColorSpan(mircColors[background % 16]), from, to, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }

        @NonNull
        public ColorIrcFormat copySwapped() {
            return new ColorIrcFormat(background, foreground);
        }

        @Override
        public byte id() {
            return CODE_COLOR;
        }
    }
}
