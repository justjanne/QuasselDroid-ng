package de.kuschku.util;

import android.support.annotation.NonNull;

import com.google.common.primitives.Primitives;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import de.kuschku.libquassel.exceptions.SyncInvocationException;
import de.kuschku.libquassel.primitives.types.QVariant;

public class ReflectionUtils {
    private ReflectionUtils() {

    }

    private static void unboxList(Object[] list){
        for (int i = 0; i < list.length; i++) {
            if (list[i] instanceof QVariant)
                list[i] = ((QVariant) list[i]).data;
        }
    }

    public static void invokeMethod(Object o, String name, Object[] argv) throws SyncInvocationException {
        name = stripName(name);
        unboxList(argv);

        Class<?>[] classes = new Class<?>[argv.length];
        for (int i = 0; i < argv.length; i++) {
            classes[i] = argv[i].getClass();
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
    private static String stripName(String name) {
        return (name.contains("(")) ? name.substring(0, name.indexOf("(")) : name;
    }

    public static void invokeMethod(Object o, String name, List argv) throws SyncInvocationException {
        invokeMethod(o, name, argv.toArray(new Object[argv.size()]));
    }

    private static Method getMethodFromSignature(String methodName, Class cl, Class<?>[] parameterTypes) {
        Method[] methods = cl.getMethods();
        looper:
        for (Method m : methods) {
            if (m.getName().equals(methodName) && m.getParameterTypes().length == parameterTypes.length) {
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class mParam = m.getParameterTypes()[i];
                    Class vParam = parameterTypes[i];
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
