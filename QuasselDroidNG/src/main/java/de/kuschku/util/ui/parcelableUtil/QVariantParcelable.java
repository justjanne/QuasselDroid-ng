package de.kuschku.util.ui.parcelableUtil;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;

import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.QMetaTypeRegistry;
import de.kuschku.libquassel.primitives.types.QVariant;

public class QVariantParcelable<T> extends QVariant<T> implements Parcelable {
    public Creator<QVariantParcelable> CREATOR = new Creator<QVariantParcelable>() {
        @Override
        public QVariantParcelable createFromParcel(Parcel source) {
            try {
                QMetaType type = QMetaTypeRegistry.getType(QMetaType.Type.fromId(source.readInt()));
                Object data;
                switch (type.type) {
                    case Int:
                        data = source.readInt();
                        break;
                    case QByteArray:
                    case QString:
                        data = source.readString();
                        break;
                    case Bool:
                        data = (source.readInt() > 0);
                        break;
                    default:
                        throw new IllegalArgumentException("Can’t deserialize type "+type.name);
                }
                return new QVariantParcelable<>(type.name, data);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public QVariantParcelable[] newArray(int size) {
            return new QVariantParcelable[size];
        }
    };

    public QVariantParcelable(String typeName, T data) {
        super(typeName, data);
    }

    public QVariantParcelable(QVariant value) {
        super(value.type, (T) value.data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type.type.getValue());
        switch (type.type) {
            case Int:
                dest.writeInt((Integer) data);
                break;
            case QString:
                dest.writeString((String) data);
                break;
            case Bool:
                dest.writeInt(((Boolean) data) ? 1 : 0);
                break;
            default:
                throw new IllegalArgumentException("Can’t serialize type "+type.name);
        }
    }
}
