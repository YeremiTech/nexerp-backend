package com.empresa.erp.auth.application.dto;

import com.empresa.erp.auth.domain.UserPresenceStatus;

public record SessionActivityResponse(
        UserPresenceStatus presenceStatus,
        String accessToken,
        Long expiresIn,
        long idleSeconds,
        long awayThresholdSeconds,
        long disconnectThresholdSeconds
) {
}
