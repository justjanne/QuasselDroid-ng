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

import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.widget.EditText;

import com.google.common.base.Function;

import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.irc.format.spans.Copyable;
import de.kuschku.util.irc.format.spans.IrcBackgroundColorSpan;
import de.kuschku.util.irc.format.spans.IrcBoldSpan;
import de.kuschku.util.irc.format.spans.IrcForegroundColorSpan;
import de.kuschku.util.irc.format.spans.IrcItalicSpan;
import de.kuschku.util.irc.format.spans.IrcUnderlineSpan;

public class AdvancedEditor {
    private final AppContext context;
    private final EditText editText;

    public AdvancedEditor(AppContext context, EditText editText) {
        this.context = context;
        this.editText = editText;
    }

    public void toggleUnderline() {
        toggleUnderline(editText.getSelectionStart(), editText.getSelectionEnd());
    }

    public void toggleUnderline(int start, int end) {
        if (start == end)
            return;

        boolean isUnderline = removeSpans(start, end, UnderlineSpan.class, styleSpan -> {
            if (styleSpan instanceof IrcUnderlineSpan) {
                return (IrcUnderlineSpan) styleSpan;
            } else {
                return new IrcUnderlineSpan();
            }
        }, false);
        if (!isUnderline) {
            editText.getText().setSpan(new IrcUnderlineSpan(), start, end, Spanned.SPAN_MARK_MARK);
        }
    }


    public void toggleBold() {
        toggleBold(editText.getSelectionStart(), editText.getSelectionEnd());
    }

    public void toggleBold(int start, int end) {
        if (start == end)
            return;

        boolean isBold = removeSpans(start, end, StyleSpan.class, styleSpan -> {
            if (styleSpan instanceof IrcBoldSpan) {
                return (IrcBoldSpan) styleSpan;
            } else {
                return styleSpan.getStyle() == Typeface.BOLD ? new IrcBoldSpan() : null;
            }
        }, false);
        if (!isBold) {
            editText.getText().setSpan(new IrcBoldSpan(), start, end, Spanned.SPAN_MARK_MARK);
        }
    }

    public void toggleItalic() {
        toggleItalic(editText.getSelectionStart(), editText.getSelectionEnd());
    }

    public void toggleItalic(int start, int end) {
        if (start == end)
            return;

        boolean isItalic = removeSpans(start, end, StyleSpan.class, styleSpan -> {
            if (styleSpan instanceof IrcItalicSpan) {
                return (IrcItalicSpan) styleSpan;
            } else {
                return styleSpan.getStyle() == Typeface.ITALIC ? new IrcItalicSpan() : null;
            }
        }, false);
        if (!isItalic) {
            editText.getText().setSpan(new IrcItalicSpan(), start, end, Spanned.SPAN_MARK_MARK);
        }
    }

    public void toggleForeground(@IntRange(from = -1, to = 15) int color) {
        toggleForeground(editText.getSelectionStart(), editText.getSelectionEnd(), color);
    }

    public void toggleForeground(int start, int end, int color) {
        removeSpans(start, end, ForegroundColorSpan.class, foregroundColorSpan -> {
            if ((foregroundColorSpan instanceof IrcForegroundColorSpan)) {
                return (IrcForegroundColorSpan) foregroundColorSpan;
            } else {
                int id = context.themeUtil().res.colorToId(foregroundColorSpan.getForegroundColor());
                if (id != -1) {
                    return new IrcForegroundColorSpan(id, context.themeUtil().res.mircColors[id]);
                } else {
                    return null;
                }
            }
        }, true);

        if (color != -1) {
            editText.getText().setSpan(new IrcForegroundColorSpan(color, context.themeUtil().res.mircColors[color]), start, end, Spanned.SPAN_MARK_MARK);
        }
    }

    private <T extends Copyable<T>, U> boolean removeSpans(int start, int end, Class<U> group, Function<U, T> transformer, boolean removeInvalid) {
        if (start == end)
            return false;

        boolean removedAny = false;

        for (U raw : editText.getText().getSpans(start, end, group)) {
            int spanFlags = editText.getText().getSpanFlags(raw);
            if ((spanFlags & Spanned.SPAN_COMPOSING) != 0) continue;

            int spanEnd = editText.getText().getSpanEnd(raw);
            int spanStart = editText.getText().getSpanStart(raw);

            T span = transformer.apply(raw);
            if (span != raw) {
                if (span == null) {
                    if (removeInvalid)
                        editText.getText().removeSpan(raw);
                    continue;
                } else {
                    editText.getText().removeSpan(raw);
                    editText.getText().setSpan(span, spanStart, spanEnd, spanFlags);
                }
            }

            boolean endIsIn = (spanEnd <= end && spanEnd >= start);
            boolean endIsAfter = (spanEnd >= end);

            boolean startIsIn = (spanStart <= end && spanStart >= start);
            boolean startIsBefore = (spanStart < start);

            if (endIsIn && startIsIn) {
                editText.getText().removeSpan(span);
                removedAny = true;
            } else if (endIsIn) {
                editText.getText().setSpan(span, spanStart, start, spanFlags);
                removedAny = true;
            } else if (startIsIn) {
                editText.getText().setSpan(span, end, spanEnd, spanFlags);
                removedAny = true;
            } else if (startIsBefore && endIsAfter) {
                editText.getText().setSpan(span, spanStart, start, spanFlags);
                editText.getText().setSpan(span.copy(), end, spanEnd, spanFlags);
                removedAny = true;
            }
        }
        return removedAny;
    }

    public void toggleBackground(@IntRange(from = -1, to = 15) int color) {
        toggleBackground(editText.getSelectionStart(), editText.getSelectionEnd(), color);
    }

    public void toggleBackground(int start, int end, @ColorInt int color) {
        removeSpans(start, end, BackgroundColorSpan.class, backgroundColorSpan -> {
            if ((backgroundColorSpan instanceof IrcBackgroundColorSpan)) {
                return (IrcBackgroundColorSpan) backgroundColorSpan;
            } else {
                int id = context.themeUtil().res.colorToId(backgroundColorSpan.getBackgroundColor());
                if (id != -1) {
                    return new IrcBackgroundColorSpan(id, context.themeUtil().res.mircColors[id]);
                } else {
                    return null;
                }
            }
        }, true);

        if (color != -1) {
            editText.getText().setSpan(new IrcBackgroundColorSpan(color, context.themeUtil().res.mircColors[color]), start, end, Spanned.SPAN_MARK_MARK);
        }
    }

    @NonNull
    public String toFormatString() {
        return context.serializer().toEscapeCodes(editText.getText());
    }
}
