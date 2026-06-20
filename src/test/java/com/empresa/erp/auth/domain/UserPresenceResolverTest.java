package com.empresa.erp.auth.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPresenceResolverTest {

    @Test
    void resolve_shouldReturnAvailableWhenBelowAwayThreshold() {
        assertThat(UserPresenceResolver.resolve(120, 210, 420))
                .isEqualTo(UserPresenceStatus.AVAILABLE);
    }

    @Test
    void resolve_shouldReturnAwayWhenBetweenThresholds() {
        assertThat(UserPresenceResolver.resolve(300, 210, 420))
                .isEqualTo(UserPresenceStatus.AWAY);
    }

    @Test
    void resolve_shouldReturnDisconnectedWhenAtOrAboveDisconnectThreshold() {
        assertThat(UserPresenceResolver.resolve(420, 210, 420))
                .isEqualTo(UserPresenceStatus.DISCONNECTED);
        assertThat(UserPresenceResolver.resolve(500, 210, 420))
                .isEqualTo(UserPresenceStatus.DISCONNECTED);
    }
}
