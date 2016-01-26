package de.kuschku.util.instancestateutil;

import android.support.annotation.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Store {
    @NonNull String name() default "";

    @NonNull Type type() default Type.INVALID;

    enum Type {
        INVALID,
        BOOLEAN,
        BOOLEAN_ARRAY,
        BYTE,
        BYTE_ARRAY,
        CHAR,
        CHAR_ARRAY,
        SHORT,
        SHORT_ARRAY,
        INT,
        INT_ARRAY,
        INTEGER_ARRAYLIST,
        LONG,
        LONG_ARRAY,
        FLOAT,
        FLOAT_ARRAY,
        DOUBLE,
        DOUBLE_ARRAY,
        STRING,
        STRING_ARRAY,
        STRING_ARRAYLIST,
        CHARSEQUENCE,
        CHARSEQUENCE_ARRAY,
        CHARSEQUENCE_ARRAYLIST,
        PARCELABLE,
        PARCELABLE_ARRAY,
        PARCELABLE_ARRAYLIST,
        SPARSEPARCELABLE_ARRAY,
        SERIALIZABLE,
        BUNDLE,
    }
}
