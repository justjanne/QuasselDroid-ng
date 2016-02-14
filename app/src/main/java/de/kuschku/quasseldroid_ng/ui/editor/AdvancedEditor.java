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

import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.widget.EditText;

import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.irc.format.BoldSpan;
import de.kuschku.util.irc.format.ItalicSpan;

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
        boolean isUnderline = false;
        for (UnderlineSpan span : editText.getText().getSpans(start, end, UnderlineSpan.class)) {
            if ((editText.getText().getSpanFlags(span) & Spanned.SPAN_COMPOSING) != 0) continue;

            isUnderline = (editText.getText().getSpanStart(span) == start && editText.getText().getSpanEnd(span) == end);
            editText.getText().removeSpan(span);

            if (isUnderline) break;
        }
        if (!isUnderline) {
            editText.getText().setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }


    public void toggleBold() {
        toggleBold(editText.getSelectionStart(), editText.getSelectionEnd());
    }

    public void toggleBold(int start, int end) {
        boolean isBold = false;
        for (BoldSpan span : editText.getText().getSpans(start, end, BoldSpan.class)) {
            if ((editText.getText().getSpanFlags(span) & Spanned.SPAN_COMPOSING) != 0) continue;

            isBold = (editText.getText().getSpanStart(span) == start && editText.getText().getSpanEnd(span) == end);
            editText.getText().removeSpan(span);

            if (isBold) break;
        }
        if (!isBold) {
            editText.getText().setSpan(new BoldSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    public void toggleItalic() {
        toggleItalic(editText.getSelectionStart(), editText.getSelectionEnd());
    }

    public void toggleItalic(int start, int end) {
        boolean isItalic = false;
        for (ItalicSpan span : editText.getText().getSpans(start, end, ItalicSpan.class)) {
            if ((editText.getText().getSpanFlags(span) & Spanned.SPAN_COMPOSING) != 0) continue;

            isItalic = (editText.getText().getSpanStart(span) == start && editText.getText().getSpanEnd(span) == end);
            editText.getText().removeSpan(span);

            if (isItalic) break;
        }
        if (!isItalic) {
            editText.getText().setSpan(new ItalicSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    public void toggleForeground(@IntRange(from = 0, to = 15) int color) {
        toggleForeground(editText.getSelectionStart(), editText.getSelectionEnd(), color);
    }

    public void toggleForeground(int start, int end, @ColorInt int color) {
        boolean isColored = false;
        for (ForegroundColorSpan span : editText.getText().getSpans(start, end, ForegroundColorSpan.class)) {
            if ((editText.getText().getSpanFlags(span) & Spanned.SPAN_COMPOSING) != 0) continue;

            isColored = span.getForegroundColor() == color && (editText.getText().getSpanStart(span) == start && editText.getText().getSpanEnd(span) == end);
            editText.getText().removeSpan(span);

            if (isColored) break;
        }
        if (!isColored) {
            editText.getText().setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    public void toggleBackground(@IntRange(from = 0, to = 15) int color) {
        toggleBackground(editText.getSelectionStart(), editText.getSelectionEnd(), color);
    }

    public void toggleBackground(int start, int end, @ColorInt int color) {
        boolean isColored = false;
        for (BackgroundColorSpan span : editText.getText().getSpans(start, end, BackgroundColorSpan.class)) {
            if ((editText.getText().getSpanFlags(span) & Spanned.SPAN_COMPOSING) != 0) continue;

            isColored = span.getBackgroundColor() == color && (editText.getText().getSpanStart(span) == start && editText.getText().getSpanEnd(span) == end);
            editText.getText().removeSpan(span);

            if (isColored) break;
        }
        if (!isColored) {
            editText.getText().setSpan(new BackgroundColorSpan(color), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    @NonNull
    public String toFormatString() {
        return context.serializer().toEscapeCodes(editText.getText());
    }
}
