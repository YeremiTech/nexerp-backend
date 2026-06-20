package com.empresa.erp.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.session.presence")
@Getter
@Setter
public class SessionPresenceProperties {

    /** Segundos sin interacción para pasar a ausente. */
    private long awaySeconds = 210;

    /** Segundos sin interacción para cerrar la sesión por inactividad. */
    private long disconnectSeconds = 420;
}
