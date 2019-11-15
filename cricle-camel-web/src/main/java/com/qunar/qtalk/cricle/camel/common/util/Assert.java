package com.qunar.qtalk.cricle.camel.common.util;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Collection;

/**
 * Created by haoling.wang on 2019/1/8.
 */
public final class Assert {

    private Assert() {
    }

    /* argument not null */

    public static void assertArgNotNull(Object object, String message) {
        assertArgNotNull(object, message, null);
    }

    public static void assertArgNotNull(Object object, String message, Throwable cause) {
        if (object == null) {
            throw new IllegalArgumentException(message, cause);
        }
    }

    /* argument has length (includes not-null checking) */

    public static void assertArgHasLength(String string, String message) {
        assertArgHasLength(string, message, null);
    }

    public static void assertArgHasLength(String string, String message, Throwable cause) {
        assertArgNotNull(string, message, cause);

        if (string.length() == 0) {
            throw new IllegalArgumentException(message, cause);
        }
    }

    /* argument not empty (includes not-null checking) */

    public static void assertArgNotEmpty(Collection<?> collection, String message) {
        assertArgNotEmpty(collection, message, null);
    }

    public static void assertArgNotEmpty(Collection<?> collection, String message, Throwable cause) {
        assertArgNotNull(collection, message, cause);

        if (collection.size() == 0) {
            throw new IllegalArgumentException(message, cause);
        }
    }

    /* argument true */

    public static void assertArg(boolean argumentCondition, String message) {
        assertArg(argumentCondition, message, null);
    }

    public static void assertArg(boolean argumentCondition, String message, Throwable cause) {
        if (!argumentCondition) {
            throw new IllegalArgumentException(message, cause);
        }
    }

    /* state true */

    public static void assertState(boolean state, String message) {
        assertState(state, message, null);
    }

    public static void assertState(boolean state, String message, Throwable cause) {
        if (!state) {
            throw new IllegalStateException(message, cause);
        }
    }

    /* state not null */

    public static void assertStateNotNull(Object object, String message) {
        assertStateNotNull(object, message, null);
    }

    public static void assertStateNotNull(Object object, String message, Throwable cause) {
        if (object == null) {
            throw new IllegalStateException(message, cause);
        }
    }

    /* state has length (includes not-null checking) */

    public static void assertStateHasLength(String string, String message) {
        assertStateHasLength(string, message, null);
    }

    public static void assertStateHasLength(String string, String message, Throwable cause) {
        // first check for null
        assertStateNotNull(string, message, cause);

        if (string.length() == 0) {
            throw new IllegalStateException(message, cause);
        }
    }

    /* state not empty (includes not-null checking) */

    public static void assertStateNotEmpty(Collection<?> collection, String message) {
        assertStateNotEmpty(collection, message, null);
    }

    public static void assertStateNotEmpty(Collection<?> collection, String message, Throwable cause) {
        // first check for null
        assertStateNotNull(collection, message, cause);

        if (collection.size() == 0) {
            throw new IllegalStateException(message, cause);
        }
    }

    /* state null */

    public static void assertStateNull(Object object, String message) {
        assertStateNull(object, message, null);
    }

    public static void assertStateNull(Object object, String message, Throwable cause) {
        if (object != null) {
            throw new IllegalStateException(message, cause);
        }
    }

    public static void isNumber(String num, String message) {
        if (!NumberUtils.isCreatable(num)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isPositiveNumber(String num, String message) {
        if (!NumberUtils.isCreatable(num) ||
                NumberUtils.toDouble(num, NumberUtils.DOUBLE_ZERO) <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
