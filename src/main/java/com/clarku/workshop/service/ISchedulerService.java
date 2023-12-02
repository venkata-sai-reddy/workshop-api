package com.clarku.workshop.service;

public interface ISchedulerService {

	void checkRequestsAndNotify();

	void notifyWorkshopsPrior();

	void notifyOneDayPrior();

}
