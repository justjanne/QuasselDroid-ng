package de.kuschku.libquassel.localtypes.orm.converters;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import de.kuschku.libquassel.message.Message;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class MessageTypeConverter extends TypeConverter<Integer, Message.Type> {
    @Override
    public Integer getDBValue(Message.Type model) {
        return model.value;
    }

    @Override
    public Message.Type getModelValue(Integer data) {
        return Message.Type.fromId(data);
    }
}
