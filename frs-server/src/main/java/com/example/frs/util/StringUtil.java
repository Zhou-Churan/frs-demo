package com.example.frs.util;

/**
 * 字符串工具类（兼容JDK8）
 */
public class StringUtil {

    private StringUtil() {
    }

    /**
     * 判断字符串是否为null或空白字符串（等同于JDK11的String.isBlank()）
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        int len = str.length();
        if (len == 0) {
            return true;
        }
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否不为null且非空白字符串
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 判断字符串是否为null或空字符串
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串是否不为null且非空字符串
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}