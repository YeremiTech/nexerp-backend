package com.enterprise.erp.auth.application.dto;

import com.enterprise.erp.auth.domain.UserPresenceStatus;

public record SessionActivityResponse(
        UserPresenceStatus presenceStatus,
        String accessToken,
        Long expiresIn,
        long idleSeconds,
        long awayThresholdSeconds,
        long disconnectThresholdSeconds
) {
}
