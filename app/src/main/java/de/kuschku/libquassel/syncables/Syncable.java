package de.kuschku.libquassel.syncables;

import de.kuschku.libquassel.primitives.QMetaType;

public @interface Syncable {
    String name() default "";

    QMetaType.Type type() default QMetaType.Type.LastType;

    String userType() default "";

    QMetaType.Type[] paramTypes() default {};
}
