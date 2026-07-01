package com.awbp.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Duration;

@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public Duration refreshTtl(@Value("${app.refresh.ttl-days}") long ttlDays) {
        return Duration.ofDays(ttlDays);
    }

}
