package de.kuschku.libquassel.localtypes.orm.converters;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.joda.time.DateTime;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class DateTimeConverter extends TypeConverter<Long, DateTime> {
    @Override
    public Long getDBValue(DateTime model) {
        return model.getMillis();
    }

    @Override
    public DateTime getModelValue(Long data) {
        return new DateTime(data.longValue());
    }
}
