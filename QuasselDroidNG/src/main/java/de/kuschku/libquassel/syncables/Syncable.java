package de.kuschku.libquassel.syncables;

import android.support.annotation.NonNull;

import de.kuschku.libquassel.primitives.QMetaType;

public @interface Syncable {
    @NonNull String name() default "";

    @NonNull QMetaType.Type type() default QMetaType.Type.LastType;

    @NonNull String userType() default "";

    @NonNull QMetaType.Type[] paramTypes() default {};
}
