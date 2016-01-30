package de.kuschku.quasseldroid_ng.ui.editor;

import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.widget.EditText;

import de.kuschku.quasseldroid_ng.ui.theme.AppContext;

public class AdvancedEditor {
    private EditText editText;
    private FormattingHelper helper;

    public AdvancedEditor(AppContext context, EditText editText) {
        this.helper = new FormattingHelper(context);
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

    public String toFormatString() {
        return helper.toEscapeCodes(editText.getText());
    }
}
