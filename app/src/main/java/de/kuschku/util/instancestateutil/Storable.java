package de.kuschku.util.instancestateutil;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class Storable {
    public boolean onRestoreInstanceState(@NonNull Bundle in) {
        try {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Store.class)) {
                    Store annotation = field.getAnnotation(Store.class);
                    String name = annotation.name().isEmpty() ? field.getName() : annotation.name();
                    Store.Type type = annotation.type() == Store.Type.INVALID ? getTypeFromClass(field.getType()) : annotation.type();
                    loadField(in, type, name, field);
                }
            }
            return true;
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    public boolean onSaveInstanceState(@NonNull Bundle out) {
        try {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Store.class)) {
                    Store annotation = field.getAnnotation(Store.class);
                    String name = annotation.name().isEmpty() ? field.getName() : annotation.name();
                    Store.Type type = annotation.type() == Store.Type.INVALID ? getTypeFromClass(field.getType()) : annotation.type();
                    storeField(out, type, name, field.get(this));
                }
            }
            return true;
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    private void storeField(@NonNull Bundle out, @NonNull Store.Type type, @NonNull String name, Object data) {
        switch (type) {
            case BOOLEAN:
                out.putBoolean(name, (Boolean) data);
                break;
            case BOOLEAN_ARRAY:
                out.putBooleanArray(name, (boolean[]) data);
                break;
            case BYTE:
                out.putByte(name, (byte) data);
                break;
            case BYTE_ARRAY:
                out.putByteArray(name, (byte[]) data);
                break;
            case CHAR:
                out.putChar(name, (char) data);
                break;
            case CHAR_ARRAY:
                out.putCharArray(name, (char[]) data);
                break;
            case SHORT:
                out.putShort(name, (short) data);
                break;
            case SHORT_ARRAY:
                out.putShortArray(name, (short[]) data);
                break;
            case INT:
                out.putInt(name, (int) data);
                break;
            case INT_ARRAY:
                out.putIntArray(name, (int[]) data);
                break;
            case INTEGER_ARRAYLIST:
                out.putIntegerArrayList(name, (ArrayList<Integer>) data);
                break;
            case LONG:
                out.putLong(name, (long) data);
                break;
            case LONG_ARRAY:
                out.putLongArray(name, (long[]) data);
                break;
            case FLOAT:
                out.putFloat(name, (float) data);
                break;
            case FLOAT_ARRAY:
                out.putFloatArray(name, (float[]) data);
                break;
            case DOUBLE:
                out.putDouble(name, (double) data);
                break;
            case DOUBLE_ARRAY:
                out.putDoubleArray(name, (double[]) data);
                break;
            case STRING:
                out.putString(name, (String) data);
                break;
            case STRING_ARRAY:
                out.putStringArray(name, (String[]) data);
                break;
            case STRING_ARRAYLIST:
                out.putStringArrayList(name, (ArrayList<String>) data);
                break;
            case CHARSEQUENCE:
                out.putCharSequence(name, (CharSequence) data);
                break;
            case CHARSEQUENCE_ARRAY:
                out.putCharSequenceArray(name, (CharSequence[]) data);
                break;
            case CHARSEQUENCE_ARRAYLIST:
                out.putCharSequenceArrayList(name, (ArrayList<CharSequence>) data);
                break;
            case PARCELABLE:
                out.putParcelable(name, (Parcelable) data);
                break;
            case PARCELABLE_ARRAY:
                out.putParcelableArray(name, (Parcelable[]) data);
                break;
            case PARCELABLE_ARRAYLIST:
                out.putParcelableArrayList(name, (ArrayList<? extends Parcelable>) data);
                break;
            case SPARSEPARCELABLE_ARRAY:
                out.putSparseParcelableArray(name, (SparseArray<? extends Parcelable>) data);
                break;
            case SERIALIZABLE:
                out.putSerializable(name, (Serializable) data);
                break;
            case BUNDLE:
                out.putBundle(name, (Bundle) data);
                break;
        }
    }

    private void loadField(@NonNull Bundle in, @NonNull Store.Type type, @NonNull String name, @NonNull Field field) throws IllegalAccessException {
        if (!in.containsKey(name)) return;

        switch (type) {
            case BOOLEAN:
                field.setBoolean(this, in.getBoolean(name));
                break;
            case BOOLEAN_ARRAY:
                field.set(this, in.getBooleanArray(name));
                break;
            case BYTE:
                field.setByte(this, in.getByte(name));
                break;
            case BYTE_ARRAY:
                field.set(this, in.getByteArray(name));
                break;
            case CHAR:
                field.setChar(this, in.getChar(name));
                break;
            case CHAR_ARRAY:
                field.set(this, in.getCharArray(name));
                break;
            case SHORT:
                field.setShort(this, in.getShort(name));
                break;
            case SHORT_ARRAY:
                field.set(this, in.getShortArray(name));
                break;
            case INT:
                field.setInt(this, in.getInt(name));
                break;
            case INT_ARRAY:
                field.set(this, in.getIntArray(name));
                break;
            case INTEGER_ARRAYLIST:
                field.set(this, in.getIntegerArrayList(name));
                break;
            case LONG:
                field.setLong(this, in.getLong(name));
                break;
            case LONG_ARRAY:
                field.set(this, in.getLongArray(name));
                break;
            case FLOAT:
                field.setFloat(this, in.getFloat(name));
                break;
            case FLOAT_ARRAY:
                field.set(this, in.getFloatArray(name));
                break;
            case DOUBLE:
                field.setDouble(this, in.getDouble(name));
                break;
            case DOUBLE_ARRAY:
                field.set(this, in.getDoubleArray(name));
                break;
            case STRING:
                field.set(this, in.getString(name));
                break;
            case STRING_ARRAY:
                field.set(this, in.getStringArray(name));
                break;
            case STRING_ARRAYLIST:
                field.set(this, in.getStringArrayList(name));
                break;
            case CHARSEQUENCE:
                field.set(this, in.getCharSequence(name));
                break;
            case CHARSEQUENCE_ARRAY:
                field.set(this, in.getCharSequenceArray(name));
                break;
            case CHARSEQUENCE_ARRAYLIST:
                field.set(this, in.getCharSequenceArrayList(name));
                break;
            case PARCELABLE:
                field.set(this, in.getParcelable(name));
                break;
            case PARCELABLE_ARRAY:
                field.set(this, in.getParcelableArray(name));
                break;
            case PARCELABLE_ARRAYLIST:
                field.set(this, in.getParcelableArrayList(name));
                break;
            case SPARSEPARCELABLE_ARRAY:
                field.set(this, in.getSparseParcelableArray(name));
                break;
            case SERIALIZABLE:
                field.set(this, in.getSerializable(name));
                break;
            case BUNDLE:
                field.set(this, in.getBundle(name));
                break;
        }
    }

    @NonNull
    private Store.Type getTypeFromClass(@NonNull Class cl) {
        if (boolean.class.isAssignableFrom(cl)) return Store.Type.BOOLEAN;
        if (boolean[].class.isAssignableFrom(cl)) return Store.Type.BOOLEAN_ARRAY;
        if (byte.class.isAssignableFrom(cl)) return Store.Type.BYTE;
        if (byte[].class.isAssignableFrom(cl)) return Store.Type.BYTE_ARRAY;
        if (char.class.isAssignableFrom(cl)) return Store.Type.CHAR;
        if (char[].class.isAssignableFrom(cl)) return Store.Type.CHAR_ARRAY;
        if (short.class.isAssignableFrom(cl)) return Store.Type.SHORT;
        if (short[].class.isAssignableFrom(cl)) return Store.Type.SHORT_ARRAY;
        if (int.class.isAssignableFrom(cl)) return Store.Type.INT;
        if (int[].class.isAssignableFrom(cl)) return Store.Type.INT_ARRAY;
        if (long.class.isAssignableFrom(cl)) return Store.Type.LONG;
        if (long[].class.isAssignableFrom(cl)) return Store.Type.LONG_ARRAY;
        if (float.class.isAssignableFrom(cl)) return Store.Type.FLOAT;
        if (float[].class.isAssignableFrom(cl)) return Store.Type.FLOAT_ARRAY;
        if (double.class.isAssignableFrom(cl)) return Store.Type.DOUBLE;
        if (double[].class.isAssignableFrom(cl)) return Store.Type.DOUBLE_ARRAY;
        if (String.class.isAssignableFrom(cl)) return Store.Type.STRING;
        if (String[].class.isAssignableFrom(cl)) return Store.Type.STRING_ARRAY;
        if (CharSequence.class.isAssignableFrom(cl)) return Store.Type.CHARSEQUENCE;
        if (CharSequence[].class.isAssignableFrom(cl)) return Store.Type.CHARSEQUENCE_ARRAY;
        if (Parcelable.class.isAssignableFrom(cl)) return Store.Type.PARCELABLE;
        if (Parcelable[].class.isAssignableFrom(cl)) return Store.Type.PARCELABLE_ARRAY;
        if (Serializable.class.isAssignableFrom(cl)) return Store.Type.SERIALIZABLE;
        if (Bundle.class.isAssignableFrom(cl)) return Store.Type.BUNDLE;
        return Store.Type.INVALID;
    }
}
