package de.kuschku.util.ui;

import android.content.Context;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;

public class DateFormatHelper {
    private DateFormatHelper() {

    }

    public static DateTimeFormatter getTimeFormatter(Context ctx) {
        return DateTimeFormat.forPattern(((SimpleDateFormat) android.text.format.DateFormat.getTimeFormat(ctx)).toLocalizedPattern());
    }
}
