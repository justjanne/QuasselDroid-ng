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

import android.support.annotation.NonNull;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;

import java.util.Arrays;
import java.util.Locale;

import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.irc.format.spans.IrcBackgroundColorSpan;
import de.kuschku.util.irc.format.spans.IrcBoldSpan;
import de.kuschku.util.irc.format.spans.IrcForegroundColorSpan;
import de.kuschku.util.irc.format.spans.IrcItalicSpan;

public class IrcFormatSerializer {
    public static final char CODE_BOLD = 0x02;
    public static final char CODE_COLOR = 0x03;
    public static final char CODE_ITALIC = 0x1D;
    public static final char CODE_UNDERLINE = 0x1F;
    public static final char CODE_SWAP = 0x16;
    public static final char CODE_RESET = 0x0F;

    private final AppContext context;

    public IrcFormatSerializer(AppContext context) {
        this.context = context;
    }

    @NonNull
    public String toEscapeCodes(@NonNull Spanned text) {
        StringBuilder out = new StringBuilder();
        withinParagraph(out, text, 0, text.length());
        return out.toString();
    }

    private void withinParagraph(@NonNull StringBuilder out, @NonNull Spanned text,
                                 int start, int end) {
        int next;
        int foreground = -1;
        int background = -1;
        boolean bold = false;
        boolean underline = false;
        boolean italic = false;

        testLog(text);

        for (int i = start; i < end; i = next) {
            next = text.nextSpanTransition(i, end, CharacterStyle.class);
            CharacterStyle[] style = text.getSpans(i, next, CharacterStyle.class);

            Log.d("IrcFormat", "i is " + i);
            Log.d("IrcFormat", "Next is " + next);
            Log.d("IrcFormat", "Spans inbetween: " + Arrays.toString(style));

            int afterForeground = -1;
            int afterBackground = -1;
            boolean afterBold = false;
            boolean afterUnderline = false;
            boolean afterItalic = false;

            for (CharacterStyle aStyle : style) {
                if ((text.getSpanFlags(aStyle) & Spanned.SPAN_COMPOSING) != 0)
                    continue;

                if (aStyle instanceof IrcBoldSpan) {
                    afterBold = true;
                } else if (aStyle instanceof IrcItalicSpan) {
                    afterItalic = true;
                } else if (aStyle instanceof UnderlineSpan) {
                    afterUnderline = true;
                } else if (aStyle instanceof IrcForegroundColorSpan) {
                    afterForeground = ((IrcForegroundColorSpan) aStyle).mircColor;
                } else if (aStyle instanceof IrcBackgroundColorSpan) {
                    afterBackground = ((IrcBackgroundColorSpan) aStyle).mircColor;
                } else if (aStyle instanceof ForegroundColorSpan) {
                    afterForeground = context.themeUtil().res.colorToId(((ForegroundColorSpan) aStyle).getForegroundColor());
                } else if (aStyle instanceof BackgroundColorSpan) {
                    afterBackground = context.themeUtil().res.colorToId(((BackgroundColorSpan) aStyle).getBackgroundColor());
                }
            }

            if (afterBold != bold) {
                Log.d("IrcFormat", "Changing bold from " + bold + " to " + afterBold);
                out.append(CODE_BOLD);
            }

            if (afterUnderline != underline) {
                Log.d("IrcFormat", "Changing underline from " + underline + " to " + afterUnderline);
                out.append(CODE_UNDERLINE);
            }

            if (afterItalic != italic) {
                Log.d("IrcFormat", "Changing italic from " + italic + " to " + afterItalic);
                out.append(CODE_ITALIC);
            }

            if (afterForeground != foreground || afterBackground != background) {
                Log.d("IrcFormat", "Changing foreground from " + foreground + " to " + afterForeground);
                Log.d("IrcFormat", "Changing background from " + background + " to " + afterBackground);
                if (afterForeground == background && afterBackground == foreground) {
                    out.append(CODE_SWAP);
                } else {
                    out.append(CODE_COLOR);
                    if (background == afterBackground) {
                        if (afterForeground != -1) {
                            out.append(String.format(Locale.US, "%02d", afterForeground));
                        } else {
                            out.append(String.format(Locale.US, "%02d", context.themeUtil().res.colorForegroundMirc));
                        }
                    } else {
                        if (afterBackground == -1) {
                            if (afterForeground != -1) {
                                out.append(CODE_COLOR);
                                out.append(String.format(Locale.US, "%02d", afterForeground));
                            } else {
                                // Foreground changed from a value to null, we don’t set any new foreground
                                // Background changed from a value to null, we don’t set any new background
                            }
                        } else {
                            if (afterForeground != -1) {
                                out.append(String.format(Locale.US, "%02d,%02d", afterForeground, afterBackground));
                            } else {
                                out.append(String.format(Locale.US, "%02d,%02d", context.themeUtil().res.colorForegroundMirc, afterBackground));
                            }
                        }
                    }
                }
            }

            out.append(text.subSequence(i, next));

            bold = afterBold;
            italic = afterItalic;
            underline = afterUnderline;
            background = afterBackground;
            foreground = afterForeground;
        }

        if (bold || italic || underline || background != -1 || foreground != -1)
            out.append(CODE_RESET);
    }

    private void testLog(Spanned text) {
        StringBuilder out = new StringBuilder();
        int next;
        for (int i = 0; i < text.length(); i = next) {
            next = text.nextSpanTransition(i, text.length(), CharacterStyle.class);
            CharacterStyle[] styles = text.getSpans(i, next, CharacterStyle.class);

            for (CharacterStyle style : styles) {
                out.append("<").append(style.getClass().getSimpleName()).append(">");
            }

            out.append(text.subSequence(i, next));

            for (int i1 = styles.length - 1; i1 >= 0; i1--) {
                out.append("</").append(styles[i1].getClass().getSimpleName()).append(">");
            }
        }
        Log.e("IrcFormat", String.valueOf(out));
    }
}
