package de.kuschku.util.ui;

import android.content.Context;
import android.support.annotation.NonNull;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;

public class DateTimeFormatHelper {
    @NonNull
    private final Context context;

    public DateTimeFormatHelper(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    public DateTimeFormatter getTimeFormatter() {
        return getTimeFormatter(context);
    }

    @NonNull
    public DateTimeFormatter getDateFormatter() {
        return getDateFormatter(context);
    }

    @NonNull
    public DateTimeFormatter getLongDateFormatter() {
        return getLongDateFormatter(context);
    }

    @NonNull
    public  DateTimeFormatter getMediumDateFormatter() {
        return getMediumDateFormatter(context);
    }

    @NonNull
    public static DateTimeFormatter getTimeFormatter(Context ctx) {
        return DateTimeFormat.forPattern(((SimpleDateFormat) android.text.format.DateFormat.getTimeFormat(ctx)).toLocalizedPattern());
    }

    @NonNull
    public static DateTimeFormatter getDateFormatter(Context ctx) {
        return DateTimeFormat.forPattern(((SimpleDateFormat) android.text.format.DateFormat.getDateFormat(ctx)).toLocalizedPattern());
    }

    @NonNull
    public static DateTimeFormatter getLongDateFormatter(Context ctx) {
        return DateTimeFormat.forPattern(((SimpleDateFormat) android.text.format.DateFormat.getLongDateFormat(ctx)).toLocalizedPattern());
    }

    @NonNull
    public static DateTimeFormatter getMediumDateFormatter(Context ctx) {
        return DateTimeFormat.forPattern(((SimpleDateFormat) android.text.format.DateFormat.getMediumDateFormat(ctx)).toLocalizedPattern());
    }
}
