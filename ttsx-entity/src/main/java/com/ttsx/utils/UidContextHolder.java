package com.ttsx.utils;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-22 下午 7:54
 */
public class UidContextHolder {
    private static final ThreadLocal<String> UID_HOLDER = new ThreadLocal<>();

    public static void setUid(String uid) {
        UID_HOLDER.set(uid);
    }

    public static String getUid() {
        return UID_HOLDER.get();
    }

    public static void clearUid() {
        UID_HOLDER.remove();
    }
}
