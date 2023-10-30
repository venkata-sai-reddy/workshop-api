package com.clarku.workshop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.clarku.workshop.service.ISchedulerService;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class Schedulers {

	@Autowired
    private SchedulerConfig schedulerConfig;

	@Autowired
	private ISchedulerService schedulerService;

    @Scheduled(cron = "#{@schedulerConfig.getScheduledTime()}")
    public void checkRequestsAndNotify() {
    	log.info("Scheduler :: checkRequestAndNotify() :: Scheduler job to check new skill request is started");
        schedulerService.checkRequestsAndNotify();
    	log.info("Scheduler :: checkRequestAndNotify() :: Scheduler job to check new skill request is Ended");
    }
}
