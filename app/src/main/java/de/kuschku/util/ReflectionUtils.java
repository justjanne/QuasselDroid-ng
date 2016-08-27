/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.primitives.Primitives;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import de.kuschku.libquassel.exceptions.SyncInvocationException;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.util.backports.Objects;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class ReflectionUtils {
    private ReflectionUtils() {

    }

    private static void unboxList(@NonNull Object[] list) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] instanceof QVariant)
                list[i] = ((QVariant) list[i]).data;
        }
    }

    public static void invokeMethod(@NonNull Object o, @NonNull String name, @NonNull Object[] argv) throws SyncInvocationException {
        name = stripName(name);
        unboxList(argv);

        Class<?>[] classes = new Class<?>[argv.length];
        for (int i = 0; i < argv.length; i++) {
            if (argv[i] == null) classes[i] = null;
            else classes[i] = argv[i].getClass();
        }
        Method m = getMethodFromSignature(name, o.getClass(), classes);
        if (m == null)
            throw new SyncInvocationException(String.format("No method %s::%s with argument types %s", o.getClass().getSimpleName(), name, Arrays.toString(classes)));

        try {
            m.invoke(o, argv);
        } catch (Exception e) {
            throw new SyncInvocationException(e, String.format("Error invoking %s::%s with arguments %s and classes %s", o.getClass().getSimpleName(), name, Arrays.toString(argv), Arrays.toString(classes)));
        }
    }

    @NonNull
    private static String stripName(@NonNull String name) {
        return (name.contains("(")) ? name.substring(0, name.indexOf("(")) : name;
    }

    public static void invokeMethod(@NonNull Object o, @NonNull String name, @NonNull List argv) throws SyncInvocationException {
        invokeMethod(o, name, argv.toArray(new Object[argv.size()]));
    }

    @Nullable
    private static <T> Method getMethodFromSignature(String methodName, @NonNull Class<T> cl, @NonNull Class<?>[] parameterTypes) {
        Method[] methods = cl.getMethods();
        assertNotNull(methods);

        looper:
        for (Method m : methods) {
            assertNotNull(m);
            assertNotNull(m.getParameterTypes());

            if (Objects.equals(m.getName(), methodName) && m.getParameterTypes().length == parameterTypes.length) {
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> mParam = m.getParameterTypes()[i];
                    Class<?> vParam = parameterTypes[i];

                    // Can’t check type of null values, so we’ll assume it will work
                    if (vParam == null) continue;

                    assertNotNull(vParam);

                    if (mParam.isPrimitive() && Primitives.isWrapperType(vParam))
                        vParam = Primitives.unwrap(vParam);

                    if (mParam != vParam && !mParam.isAssignableFrom(vParam)) {
                        continue looper;
                    }
                }
                return m;
            }
        }

        return null;
    }
}
