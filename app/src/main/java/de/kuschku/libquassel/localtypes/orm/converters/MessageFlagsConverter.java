package de.kuschku.libquassel.localtypes.orm.converters;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import de.kuschku.libquassel.message.Message;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class MessageFlagsConverter extends TypeConverter<Short, Message.Flags> {
    @Override
    public Short getDBValue(Message.Flags model) {
        return (short) model.flags;
    }

    @Override
    public Message.Flags getModelValue(Short data) {
        return new Message.Flags(data.byteValue());
    }
}
