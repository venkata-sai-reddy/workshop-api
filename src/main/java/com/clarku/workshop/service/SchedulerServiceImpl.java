package com.clarku.workshop.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.repository.IUserRepo;
import com.clarku.workshop.repository.IWorkshopRepo;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.vo.RequestVO;
import com.clarku.workshop.vo.UserVO;
import com.clarku.workshop.vo.WorkshopVO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SchedulerServiceImpl implements ISchedulerService {

	@Autowired
	IWorkshopRepo workshopRepo;

	@Autowired
	IUserRepo userRepo;

	@Autowired
	INotificationService notify;

	@Override
	public void checkRequestsAndNotify() {
		try {
			List<RequestVO> requests = workshopRepo.getRecentlyRequestedSkillRequest();
			if (requests != null && !requests.isEmpty()) {
				log.info("{} requests are recorded", requests.size());
				Set<Integer> skillIds = requests.stream().map(RequestVO::getSkillId).collect(Collectors.toSet());
				List<Integer> skillsNotified = new ArrayList<>();
				log.info("Skills requested are {}", skillIds.size());
				HashMap<Integer, List<UserVO>> skillUserList = userRepo.getSkilledUsers(skillIds.stream().toList());
				HashMap<Integer, Long> skillRequestCount = (HashMap<Integer, Long>) skillIds.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
				skillRequestCount.forEach((skillId, count) -> {
					if(skillUserList.containsKey(skillId)) {
						try {
							log.info("Sending Notification to the Instructor for skillId {} ", skillId);
							notify.sendSkillRequestMail(skillUserList.get(skillId), skillId, count);
						} catch (GlobalException | EmailException e) {
							log.error("Failed to send Email for the skill Request {} ", skillId);
						}
						skillsNotified.add(skillId);
					}
				});
				workshopRepo.updateRequestSkillsStatus(skillsNotified, Constants.NOTIFIED);
			}
		} catch (GlobalException exp) {
			log.error("Error while scheduler job run {}", exp.getCause());
		}
	}

	@Override
	public void notifyWorkshopsPrior() {
		try {
			List<WorkshopVO> workshops = workshopRepo.getNextTwoDaysWorkshops();
			if (workshops != null && !workshops.isEmpty()) {
				log.info("{} Workshops notified", workshops.size());
				List<WorkshopVO> oneDayPriorWorkshops = workshops.stream().filter(workshop -> workshop.getStartTime().isBefore(LocalDateTime.now().plusDays(1))).toList();
				List<WorkshopVO> twoDaysPriorWorkshops = workshops.stream().filter(workshop -> workshop.getStartTime().isAfter(LocalDateTime.now().plusDays(1))).toList();
				if (!oneDayPriorWorkshops.isEmpty()) {
					oneDayPriorWorkshops.forEach(workshop -> {
						try {
							workshop.setRegisteredUsers(workshopRepo.retrieveWorkshopRegisteredUser(workshop.getWorkshopId()));
							notify.sendUpcomingWorkshopNotification(workshop, 1);
						} catch (GlobalException e) {
							log.error("Error while retirving registered users {}", e.getCause());
						} catch (EmailException e) {
							log.error("Error while sending notification to users {}", e.getCause());
						}
					});
					
				}
				if (!twoDaysPriorWorkshops.isEmpty()) {
					twoDaysPriorWorkshops.forEach(workshop -> {
						try {
							workshop.setRegisteredUsers(workshopRepo.retrieveWorkshopRegisteredUser(workshop.getWorkshopId()));
							notify.sendUpcomingWorkshopNotification(workshop, 2);
						} catch (GlobalException e) {
							log.error("Error while retirving registered users {}", e.getCause());
						} catch (EmailException e) {
							log.error("Error while sending notification to users {}", e.getCause());
						}
					});
				}
			}
		} catch (GlobalException exp) {
			log.error("Error while scheduler job run {}", exp.getCause());
		}
	}

	@Override
	public void notifyOneDayPrior() {
		try {
			List<RequestVO> requests = workshopRepo.getRecentlyRequestedSkillRequest();
			if (requests != null && !requests.isEmpty()) {
				log.info("{} requests are recorded", requests.size());
				Set<Integer> skillIds = requests.stream().map(RequestVO::getSkillId).collect(Collectors.toSet());
				List<Integer> skillsNotified = new ArrayList<>();
				log.info("Skills requested are {}", skillIds.size());
				HashMap<Integer, List<UserVO>> skillUserList = userRepo.getSkilledUsers(skillIds.stream().toList());
				HashMap<Integer, Long> skillRequestCount = (HashMap<Integer, Long>) skillIds.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
				skillRequestCount.forEach((skillId, count) -> {
					if(skillUserList.containsKey(skillId)) {
						try {
							log.info("Sending Notification to the Instructor for skillId {} ", skillId);
							notify.sendSkillRequestMail(skillUserList.get(skillId), skillId, count);
						} catch (GlobalException | EmailException e) {
							log.error("Failed to send Email for the skill Request {} ", skillId);
						}
						skillsNotified.add(skillId);
					}
				});
				workshopRepo.updateRequestSkillsStatus(skillsNotified, Constants.NOTIFIED);
			}
		} catch (GlobalException exp) {
			log.error("Error while scheduler job run {}", exp.getCause());
		}
	}

}
