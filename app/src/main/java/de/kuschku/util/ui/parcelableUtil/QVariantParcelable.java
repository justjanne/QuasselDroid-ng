package de.kuschku.util.ui.parcelableUtil;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.IOException;

import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.QMetaTypeRegistry;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@SuppressWarnings("unchecked")
public class QVariantParcelable<T> extends QVariant<T> implements Parcelable {
    @NonNull
    public Creator<QVariantParcelable> CREATOR = new Creator<QVariantParcelable>() {
        @NonNull
        @Override
        public QVariantParcelable createFromParcel(@NonNull Parcel source) {
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
                        throw new IllegalArgumentException("Can’t deserialize type " + type.name);
                }
                return new QVariantParcelable<>(type.name, data);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @NonNull
        @Override
        public QVariantParcelable[] newArray(int size) {
            return new QVariantParcelable[size];
        }
    };

    public QVariantParcelable(@NonNull String typeName, T data) {
        super(typeName, data);
    }

    public QVariantParcelable(@NonNull QVariant value) {
        super(value.type, (T) value.data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {


        dest.writeInt(type.type.getValue());
        switch (type.type) {
            case Int:
                assertNotNull(data);
                dest.writeInt((Integer) data);
                break;
            case QString:
                dest.writeString((String) data);
                break;
            case Bool:
                assertNotNull(data);
                dest.writeInt(((Boolean) data) ? 1 : 0);
                break;
            default:
                throw new IllegalArgumentException("Can’t serialize type " + type.name);
        }
    }
}
