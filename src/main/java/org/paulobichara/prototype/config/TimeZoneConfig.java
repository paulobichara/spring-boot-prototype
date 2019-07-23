package org.paulobichara.prototype.config;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeZoneConfig {

    private static final String TIMEZONE = "UTC";

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE));
    }

}
