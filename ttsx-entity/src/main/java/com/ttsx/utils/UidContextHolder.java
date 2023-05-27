package com.ttsx.utils;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-22 下午 7:54
 */
public class UidContextHolder {
    private static final ThreadLocal<String> UidHolder = new ThreadLocal<>();

    public static void setUid(String uid) {
        UidHolder.set(uid);
    }

    public static String getUid() {
        return UidHolder.get();
    }

    public static void clearUid() {
        UidHolder.remove();
    }
}
