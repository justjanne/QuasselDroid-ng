package de.kuschku.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.exceptions.SyncInvocationException;

public class ReflectionUtils {
    private ReflectionUtils() {

    }

    public static void invokeMethod(Object o, String name, Object[] argv) throws SyncInvocationException {
        Method m;
        try {
            m = getMethodFromSignature(name, argv.length, o.getClass());
            try {
                m.invoke(o, argv);
            } catch (Exception e) {
                e.printStackTrace();
                throw new SyncInvocationException(e, String.format("Unknown method: %s::%s with arguments: %s", o.getClass().getSimpleName(), name, Arrays.toString(argv)));
            }
        } catch (SyncInvocationException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SyncInvocationException(e, String.format("Unknown method: %s::%s with arguments: %s", o.getClass().getSimpleName(), name, Arrays.toString(argv)));
        }
    }

    public static void invokeMethod(Object o, String name, List argv) throws SyncInvocationException {
        invokeMethod(o, name, argv.toArray(new Object[argv.size()]));
    }

    private static Method getMethodFromSignature(String methodName, int parameterCount, Class cl) {
        String[] types = new String[] {};
        if (methodName.contains("(")) {
            types = methodName.substring(methodName.indexOf("(")+1, methodName.indexOf(")")).split(",");
            methodName = methodName.substring(0, methodName.indexOf("("));
        }

        List<Method> candidates = new ArrayList<>();
        for (final Method m : cl.getDeclaredMethods()) {
            if (m.getName().equals(methodName) && m.getParameterTypes().length == parameterCount) {
                candidates.add(m);
            }
        }
        if (candidates.size() == 1) return candidates.get(0);
        else if (candidates.size() > 1){
            for (Method m : candidates) {
                if (matches(types, m.getParameterTypes()))
                    return m;
            }
        }

        throw new IllegalArgumentException("Could not find a method with proper arguments");
    }

    private static boolean matches(String[] types, Class[] classes) {
        paramater_loop: for (int i = 0; i < types.length; i++) {
            Class cl = classes[i];
            while (cl != Object.class) {
                if (cl.getSimpleName().equals(types[i])) {
                    continue paramater_loop;
                } else {
                    for (Class in : cl.getInterfaces()) {
                        if (in.getSimpleName().equals(types[i])) {
                            continue paramater_loop;
                        }
                    }
                }
                cl = cl.getSuperclass();
            }
            return false;
        }
        return true;
    }
}
