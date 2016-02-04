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
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.ui.MessageUtil;

public class IrcFormatHelper {
    @NonNull
    private static final String scheme = "(?:(?:mailto:|(?:[+.-]?\\w)+://)|www(?=\\.\\S+\\.))";
    @NonNull
    private static final String authority = "(?:(?:[,.;@:]?[-\\w]+)+\\.?|\\[[0-9a-f:.]+\\])(?::\\d+)?";
    @NonNull
    private static final String urlChars = "(?:[,.;:]*[\\w~@/?&=+$()!%#*-])";
    @NonNull
    private static final String urlEnd = "(?:>|[,.;:\"]*\\s|\\b|$)";
    @NonNull
    private static final Pattern urlPattern = Pattern.compile(String.format("\\b(%s%s(?:/%s*)?)%s", scheme, authority, urlChars, urlEnd), Pattern.CASE_INSENSITIVE);
    @NonNull
    private static final Pattern channelPattern = Pattern.compile("((?:#|![A-Z0-9]{5})[^,:\\s]+(?::[^,:\\s]+)?)\\b", Pattern.CASE_INSENSITIVE);

    @NonNull
    private final AppContext context;

    public IrcFormatHelper(@NonNull AppContext context) {
        this.context = context;
    }

    @NonNull
    public CharSequence formatUserNick(@NonNull String nick) {
        int colorIndex = IrcUserUtils.getSenderColor(nick);
        int color = context.themeUtil().res.senderColors[colorIndex];

        SpannableString str = new SpannableString(nick);
        str.setSpan(new ForegroundColorSpan(color), 0, nick.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, nick.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return str;
    }

    @NonNull
    public CharSequence formatIrcMessage(@NonNull String message) {
        List<FutureClickableSpan> spans = new LinkedList<>();

        SpannableString str = new SpannableString(MessageUtil.parseStyleCodes(context.themeUtil(), message, context.settings().mircColors.get()));
        Matcher urlMatcher = urlPattern.matcher(str);
        while (urlMatcher.find()) {
            spans.add(new FutureClickableSpan(new CustomURLSpan(urlMatcher.group()), urlMatcher.start(), urlMatcher.end()));
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
