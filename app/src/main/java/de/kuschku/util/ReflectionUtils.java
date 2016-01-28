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
            Log.e("DEBUG", m.toString());
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
