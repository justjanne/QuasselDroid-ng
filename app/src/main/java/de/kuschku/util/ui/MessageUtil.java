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
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.kuschku.util.ui;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import de.kuschku.quasseldroid_ng.ui.theme.ThemeUtil;

public class MessageUtil {
    // Transparent in ARGB
    private static final int COLOR_TRANSPARENT = 0x00000000;

    /**
     * Parse mIRC style codes in IrcMessage
     */
    @NonNull
    public static SpannableString parseStyleCodes(@NonNull ThemeUtil themeUtil, @NonNull String content, boolean parse) {
        if (!parse) {
            return new SpannableString(content
                    .replaceAll("\\x02", "")
                    .replaceAll("\\x0F", "")
                    .replaceAll("\\x1D", "")
                    .replaceAll("\\x1F", "")
                    .replaceAll("\\x03[0-9]{1,2}(,[0-9]{1,2})?", "")
                    .replaceAll("\\x03", ""));
        }

        final char boldIndicator = 2;
        final char normalIndicator = 15;
        final char italicIndicator = 29;
        final char underlineIndicator = 31;
        final char colorIndicator = 3;

        if (content.indexOf(boldIndicator) == -1
                && content.indexOf(italicIndicator) == -1
                && content.indexOf(underlineIndicator) == -1
                && content.indexOf(colorIndicator) == -1)
            return new SpannableString(content);

        SpannableStringBuilder newString = new SpannableStringBuilder(content);

        int start, end, endSearchOffset, startIndicatorLength, style, fg, bg;
        while (true) {
            content = newString.toString();
            end = -1;
            startIndicatorLength = 1;
            style = 0;
            fg = -1;
            bg = -1;

            // Colors?
            start = content.indexOf(colorIndicator);

            if (start != -1) {
                // Note that specifying colour codes here is optional, as the same indicator will cancel existing colours
                endSearchOffset = start + 1;
                if (endSearchOffset < content.length()) {
                    if (Character.isDigit(content.charAt(endSearchOffset))) {
                        if (endSearchOffset + 1 < content.length() && Character.isDigit(content.charAt(endSearchOffset + 1))) {
                            fg = Integer.parseInt(content.substring(endSearchOffset, endSearchOffset + 2));
                            endSearchOffset += 2;
                        } else {
                            fg = Integer.parseInt(content.substring(endSearchOffset, endSearchOffset + 1));
                            endSearchOffset += 1;
                        }

                        if (endSearchOffset < content.length() && content.charAt(endSearchOffset) == ',') {
                            if (endSearchOffset + 1 < content.length() && Character.isDigit(content.charAt(endSearchOffset + 1))) {
                                endSearchOffset++;
                                if (endSearchOffset + 1 < content.length() && Character.isDigit(content.charAt(endSearchOffset + 1))) {
                                    bg = Integer.parseInt(content.substring(endSearchOffset, endSearchOffset + 2));
                                    endSearchOffset += 2;
                                } else {
                                    bg = Integer.parseInt(content.substring(endSearchOffset, endSearchOffset + 1));
                                    endSearchOffset += 1;
                                }
                            }
                        }
                    }
                }
                startIndicatorLength = endSearchOffset - start;

                end = content.indexOf(colorIndicator, endSearchOffset);
            }

            if (start == -1) {
                start = content.indexOf(boldIndicator);
                if (start != -1) {
                    end = content.indexOf(boldIndicator, start + 1);
                    style = Typeface.BOLD;
                }
            }

            if (start == -1) {
                start = content.indexOf(italicIndicator);
                if (start != -1) {
                    end = content.indexOf(italicIndicator, start + 1);
                    style = Typeface.ITALIC;
                }
            }

            if (start == -1) {
                start = content.indexOf(underlineIndicator);
                if (start != -1) {
                    end = content.indexOf(underlineIndicator, start + 1);
                    style = -1;
                }
            }

            if (start == -1)
                break;

            int norm = content.indexOf(normalIndicator, start + 1);
            if (norm != -1 && (end == -1 || norm < end))
                end = norm;

            if (end == -1)
                end = content.length();

            if (end - (start + startIndicatorLength) > 0) {
                // Only set spans if there's any text between start & end
                if (style == -1) {
                    newString.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                } else {
                    newString.setSpan(new StyleSpan(style), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }

                if (fg != -1 && themeUtil.res.mircColors[fg] != COLOR_TRANSPARENT) {
                    newString.setSpan(new ForegroundColorSpan(themeUtil.res.mircColors[fg]), start, end,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                if (bg != -1 && themeUtil.res.mircColors[fg] != COLOR_TRANSPARENT) {
                    newString.setSpan(new BackgroundColorSpan(themeUtil.res.mircColors[fg]), start, end,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }

            // Intentionally don't remove "normal" indicators or color here, as they are multi-purpose
            if (end < content.length() && (content.charAt(end) == boldIndicator
                    || content.charAt(end) == italicIndicator
                    || content.charAt(end) == underlineIndicator))
                newString.delete(end, end + 1);

            newString.delete(start, start + startIndicatorLength);
        }

        // NOW we remove the "normal" and color indicator
        while (true) {
            content = newString.toString();
            int normPos = content.indexOf(normalIndicator);
            if (normPos != -1)
                newString.delete(normPos, normPos + 1);

            int colorPos = content.indexOf(colorIndicator);
            if (colorPos != -1)
                newString.delete(colorPos, colorPos + 1);

            if (normPos == -1 && colorPos == -1)
                break;
        }

        return new SpannableString(newString);
    }
}
