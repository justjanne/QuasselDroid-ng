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

package de.kuschku.quasseldroid_ng.ui.editor;

import android.support.annotation.NonNull;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;

import java.util.Locale;

import de.kuschku.quasseldroid_ng.ui.theme.AppContext;

public class FormattingHelper {
    private final AppContext context;

    public FormattingHelper(AppContext context) {
        this.context = context;
    }

    @NonNull
    public String toEscapeCodes(@NonNull Spanned text) {
        StringBuilder out = new StringBuilder();
        withinParagraph(out, text, 0, text.length());
        return out.toString();
    }

    public int colorToId(int color) {
        int[] colors = context.themeUtil().res.mircColors;
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == color)
                return i;
        }
        return 0;
    }

    private void withinParagraph(@NonNull StringBuilder out, @NonNull Spanned text,
                                 int start, int end) {
        int next;
        for (int i = start; i < end; i = next) {
            next = text.nextSpanTransition(i, end, CharacterStyle.class);
            CharacterStyle[] style = text.getSpans(i, next,
                    CharacterStyle.class);

            boolean jump = false;

            for (int j = 0; j < style.length; j++) {
                if (jump) {
                    jump = false;
                } else {
                    if ((text.getSpanFlags(style[j]) & Spanned.SPAN_COMPOSING) != 0)
                        continue;

                    if (style[j] instanceof BoldSpan) {
                        out.append((char) 0x02);
                    } else if (style[j] instanceof ItalicSpan) {
                        out.append((char) 0x1D);
                    } else if (style[j] instanceof UnderlineSpan) {
                        out.append((char) 0x1F);
                    } else if (style[j] instanceof ForegroundColorSpan) {
                        int fg;
                        int bg;
                        fg = colorToId(((ForegroundColorSpan) style[j]).getForegroundColor());

                        if ((j + 1 < style.length) && (style[j + 1] instanceof BackgroundColorSpan)) {
                            bg = colorToId(((BackgroundColorSpan) style[j + 1]).getBackgroundColor());
                        } else {
                            bg = 99;
                        }

                        out.append((char) 0x03);
                        out.append(String.format(Locale.US, "%02d,%02d", fg, bg));

                        jump = true;
                    } else if (style[j] instanceof BackgroundColorSpan) {
                        int fg;
                        int bg;
                        if ((j + 1 < style.length) && (style[j + 1] instanceof ForegroundColorSpan)) {
                            fg = colorToId(((ForegroundColorSpan) style[j + 1]).getForegroundColor());
                        } else {
                            fg = 99;
                        }

                        bg = colorToId(((BackgroundColorSpan) style[j]).getBackgroundColor());

                        out.append((char) 0x03);
                        out.append(String.format(Locale.US, "%02d,%02d", fg, bg));

                        jump = true;
                    }
                }
            }

            out.append(text.subSequence(i, next));

            for (int j = style.length - 1; j >= 0; j--) {
                if ((text.getSpanFlags(style[j]) & Spanned.SPAN_COMPOSING) != 0)
                    continue;

                if (style[j] instanceof ForegroundColorSpan) {
                    out.append((char) 0x03);
                }
                if (style[j] instanceof BackgroundColorSpan) {
                    out.append((char) 0x03);
                }
                if (style[j] instanceof UnderlineSpan) {
                    out.append((char) 0x1F);
                }
                if (style[j] instanceof BoldSpan) {
                    out.append((char) 0x02);
                }
                if (style[j] instanceof ItalicSpan) {
                    out.append((char) 0x1D);
                }
            }
        }
    }
}
