package org.delcom.app.configs;

import java.util.UUID;

public class AuthContext {
    private static final ThreadLocal<UUID> userIdHolder = new ThreadLocal<>();

    public static void setUserId(UUID userId) {
        userIdHolder.set(userId);
    }

    public static UUID getUserId() {
        return userIdHolder.get();
    }

    public static void clear() {
        userIdHolder.remove();
    }
}

