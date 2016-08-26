package de.kuschku.libquassel.localtypes.orm.converters;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import de.kuschku.libquassel.primitives.types.BufferInfo;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class BufferTypeConverter extends TypeConverter<Short, BufferInfo.Type> {
    @Override
    public Short getDBValue(BufferInfo.Type model) {
        return model.id;
    }

    @Override
    public BufferInfo.Type getModelValue(Short data) {
        return BufferInfo.Type.fromId(data);
    }
}
