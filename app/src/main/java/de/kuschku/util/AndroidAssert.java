package de.kuschku.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import junit.framework.Assert;

import de.kuschku.quasseldroid_ng.BuildConfig;

/**
 * Class to provide the Assert functionality of JUnit at runtime for debug builds
 */
@SuppressWarnings({"unused", "WeakerAccess", "Contract"})
public class AndroidAssert extends Assert {
    private AndroidAssert() {
    }

    public static void assertTrue(Throwable message, boolean condition) {
        if (BuildConfig.DEBUG) {
            if (!condition) fail(message);
        }
    }

    public static void assertTrue(String message, boolean condition) {
        if (BuildConfig.DEBUG) {
            if (!condition) fail(message);
        }
    }

    public static void assertTrue(boolean condition) {
        if (BuildConfig.DEBUG) {
            if (!condition) fail();
        }
    }

    public static void assertFalse(Throwable message, boolean condition) {
        if (BuildConfig.DEBUG) {
            if (condition) fail(message);
        }
    }

    public static void assertFalse(String message, boolean condition) {
        if (BuildConfig.DEBUG) {
            if (condition) fail(message);
        }
    }

    public static void assertFalse(boolean condition) {
        if (BuildConfig.DEBUG) {
            if (condition) fail();
        }
    }

    public static void fail(Throwable message) {
        if (BuildConfig.DEBUG) {
            throw new RuntimeException(message);
        }
    }

    public static void fail(String message) {
        if (BuildConfig.DEBUG) {
            throw new AssertionError(message);
        }
    }

    public static void fail() {
        if (BuildConfig.DEBUG) {
            throw new AssertionError();
        }
    }

    public static void assertEquals(Throwable message, Object expected, Object actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(String message, Object expected, Object actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(Object expected, Object actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(Throwable message, String expected, String actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(String message, String expected, String actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(String expected, String actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(Throwable message, double expected, double actual, double delta) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }


    public static void assertEquals(String message, double expected, double actual, double delta) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(double expected, double actual, double delta) {
        if (BuildConfig.DEBUG) {
            assertTrue(Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(Throwable message, float expected, float actual, float delta) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(String message, float expected, float actual, float delta) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(float expected, float actual, float delta) {
        if (BuildConfig.DEBUG) {
            assertTrue(Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(Throwable message, long expected, long actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(String message, long expected, long actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(long expected, long actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(Throwable message, boolean expected, boolean actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(String message, boolean expected, boolean actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(boolean expected, boolean actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(Throwable message, byte expected, byte actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(String message, byte expected, byte actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(byte expected, byte actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(Throwable message, char expected, char actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(String message, char expected, char actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(char expected, char actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(Throwable message, short expected, short actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(String message, short expected, short actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(short expected, short actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(Throwable message, int expected, int actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(String message, int expected, int actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, Objects.equals(expected, actual));
        }
    }

    public static void assertEquals(int expected, int actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(Objects.equals(expected, actual));
        }
    }

    public static void assertNotNull(@Nullable Object object) {
        if (BuildConfig.DEBUG) {
            assertTrue(object != null);
        }
    }

    public static void assertNotNull(@Nullable String message, @Nullable Object object) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, object != null);
        }
    }

    public static void assertNotNull(Throwable message, @Nullable Object object) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, object != null);
        }
    }

    public static void assertNull(@Nullable Object object) {
        if (BuildConfig.DEBUG) {
            assertTrue(object == null);
        }
    }

    public static void assertNull(String message, @Nullable Object object) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, object == null);
        }
    }

    public static void assertNull(Throwable message, @Nullable Object object) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, object == null);
        }
    }


    public static void assertSame(String message, Object expected, Object actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, expected == actual);
        }
    }

    public static void assertSame(Throwable message, Object expected, Object actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, expected == actual);
        }
    }

    public static void assertSame(Object expected, Object actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(expected == actual);
        }
    }

    public static void assertNotSame(Throwable message, Object expected, Object actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, expected != actual);
        }
    }

    public static void assertNotSame(String message, Object expected, Object actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(message, expected != actual);
        }
    }

    public static void assertNotSame(Object expected, Object actual) {
        if (BuildConfig.DEBUG) {
            assertTrue(expected != actual);
        }
    }

    public static void failSame(String message, Object expected, Object actual) {
        if (BuildConfig.DEBUG) {
            assertNotSame(message, expected, actual);
        }
    }

    public static void failNotSame(String message, Object expected, Object actual) {
        if (BuildConfig.DEBUG) {
            assertSame(message, expected, actual);
        }
    }

    public static void failNotEquals(String message, Object expected, Object actual) {
        if (BuildConfig.DEBUG) {
            assertEquals(message, expected, actual);
        }
    }

    public static void assertEquals(@NonNull int... elements) {
        if (BuildConfig.DEBUG) {
            if (elements.length > 0) {
                int first = elements[0];
                for (int i = 1; i < elements.length; i++) {
                    if (first != elements[i])
                        fail();
                }
            }
        }
    }
}
