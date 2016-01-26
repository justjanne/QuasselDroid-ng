package de.kuschku.util.irc;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.text.ParcelableSpan;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.ui.ThemeUtil;

public class IrcFormatHelper {
    @NonNull private static final String scheme = "(?:(?:mailto:|(?:[+.-]?\\w)+://)|www(?=\\.\\S+\\.))";
    @NonNull private static final String authority = "(?:(?:[,.;@:]?[-\\w]+)+\\.?|\\[[0-9a-f:.]+\\])(?::\\d+)?";
    @NonNull private static final String urlChars = "(?:[,.;:]*[\\w~@/?&=+$()!%#*-])";
    @NonNull private static final String urlEnd = "(?:>|[,.;:\"]*\\s|\\b|$)";
    @NonNull private static final Pattern urlPattern = Pattern.compile(String.format("\\b(%s%s(?:/%s*)?)%s", scheme, authority, urlChars, urlEnd), Pattern.CASE_INSENSITIVE);
    @NonNull private static final Pattern channelPattern = Pattern.compile("((?:#|![A-Z0-9]{5})[^,:\\s]+(?::[^,:\\s]+)?)\\b", Pattern.CASE_INSENSITIVE);

    @NonNull
    private final ThemeUtil.Colors colors;

    public IrcFormatHelper(@NonNull ThemeUtil.Colors colors) {
        this.colors = colors;
    }

    @NonNull
    public CharSequence formatUserNick(@NonNull String nick) {
        int colorIndex = IrcUserUtils.getSenderColor(nick);
        int color = colors.senderColors[colorIndex];

        SpannableString str = new SpannableString(nick);
        str.setSpan(new ForegroundColorSpan(color), 0, nick.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, nick.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return str;
    }

    @NonNull
    public CharSequence formatIrcMessage(@NonNull String message) {
        List<FutureClickableSpan> spans = new LinkedList<>();

        SpannableString str = new SpannableString(message);
        Matcher urlMatcher = urlPattern.matcher(str);
        while (urlMatcher.find()) {
            spans.add(new FutureClickableSpan(new CustomURLSpan(urlMatcher.toString()), urlMatcher.start(), urlMatcher.end()));
        }
        for (FutureClickableSpan span : spans) {
            str.setSpan(span.span, span.start, span.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return str;
    }

    private static class FutureClickableSpan {
        @NonNull
        public final ClickableSpan span;
        public final int start;
        public final int end;

        public FutureClickableSpan(@NonNull ClickableSpan span, int start, int end) {
            this.span = span;
            this.start = start;
            this.end = end;
        }
    }

    private static class CustomURLSpan extends ClickableSpan implements ParcelableSpan {
        private final String mURL;

        public CustomURLSpan(@NonNull String url) {
            mURL = url;
        }

        public CustomURLSpan(@NonNull Parcel src) {
            mURL = src.readString();
        }

        public int getSpanTypeId() {
            return R.id.custom_url_span;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeString(mURL);
        }

        public String getURL() {
            return mURL;
        }

        @Override
        public void onClick(@NonNull View widget) {
            Log.e("TEST", "THIS IS A TEST");

            Uri uri = Uri.parse(getURL());
            Context context = widget.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.w("URLSpan", "Actvity was not found for intent, " + intent.toString());
            }
        }
    }
}
