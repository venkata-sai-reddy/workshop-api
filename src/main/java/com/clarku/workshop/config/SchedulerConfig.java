package com.clarku.workshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfig {
	
	@Value("${scheduler.notificationCronExp}")
    private String scheduledTime;

	@Value("${scheduler.fourAMCronTimeExp}")
    private String fourAMCronTime;

    public String getFourAMCronTime() {
        return fourAMCronTime;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

}
