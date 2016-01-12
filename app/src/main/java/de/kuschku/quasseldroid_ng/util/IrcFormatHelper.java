package de.kuschku.quasseldroid_ng.util;


import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class IrcFormatHelper {
    private final ThemeUtil.Colors colors;

    public IrcFormatHelper(ThemeUtil.Colors colors) {
        this.colors = colors;
    }

    public CharSequence formatUserNick(String nick) {
        int colorIndex = IrcUserUtils.getSenderColor(nick);
        int color = colors.senderColors[colorIndex];

        SpannableString str = new SpannableString(nick);
        str.setSpan(new ForegroundColorSpan(color), 0, nick.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, nick.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return str;
    }

    public CharSequence formatIrcMessage(String message) {
        SpannableString str = new SpannableString(message);
        return str;
    }
}
