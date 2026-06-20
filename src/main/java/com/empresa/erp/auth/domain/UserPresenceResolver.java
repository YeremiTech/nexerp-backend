package com.empresa.erp.auth.domain;

public final class UserPresenceResolver {

    private UserPresenceResolver() {
    }

    public static UserPresenceStatus resolve(long idleSeconds, long awaySeconds, long disconnectSeconds) {
        if (idleSeconds >= disconnectSeconds) {
            return UserPresenceStatus.DISCONNECTED;
        }
        if (idleSeconds >= awaySeconds) {
            return UserPresenceStatus.AWAY;
        }
        return UserPresenceStatus.AVAILABLE;
    }
}
