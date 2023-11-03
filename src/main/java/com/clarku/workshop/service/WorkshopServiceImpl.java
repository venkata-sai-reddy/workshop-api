package com.clarku.workshop.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.repository.ISkillsRepo;
import com.clarku.workshop.repository.IUserRepo;
import com.clarku.workshop.repository.IWorkshopRepo;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.vo.RegisteredUserVO;
import com.clarku.workshop.vo.RequestVO;
import com.clarku.workshop.vo.SearchWorkshopVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.UserVO;
import com.clarku.workshop.vo.WorkshopVO;
import com.clarku.workshop.vo.WorkshopsTimeLineVO;

import io.micrometer.common.util.StringUtils;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class WorkshopServiceImpl implements IWorkshopService {

	@Autowired
	IWorkshopRepo workshopRepo;

	@Autowired
	IUserRepo userRepo;

	@Autowired
	ISkillsRepo skillRepo;

	@Autowired
	INotificationService notify;

	@Override
	public WorkshopVO createWorkshop(WorkshopVO workshopDetails, Integer userId) throws GlobalException {
		validateCreate(workshopDetails, userId);
		Boolean isCreated = workshopRepo.createWorkshop(workshopDetails);
		if (Boolean.FALSE.equals(isCreated)) {
			throw new GlobalException("Failed to created Workshop, Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		WorkshopVO savedWorkshop = workshopRepo.getLastCrtdWrkshpByUserId(userId);
		workshopDetails.setWorkshopId(savedWorkshop.getWorkshopId());
		validateAndSaveSkills(workshopDetails);
		return workshopDetails;
	}

	private void validateAndSaveSkills(WorkshopVO workshopDetails) throws GlobalException {
		List<Integer> skills = workshopDetails.getSelectedSkills().stream().map(SkillVO::getSkillId).toList();
		workshopRepo.saveWorkshopSkillMap(workshopDetails.getWorkshopId(), skills);
	}

	private void validateCreate(WorkshopVO workshopDetails, Integer userId) throws GlobalException {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		if (workshopDetails == null) {
			throw new GlobalException("Please enter valid details", HttpStatus.BAD_REQUEST);
		}
		if (StringUtils.isBlank(workshopDetails.getWorkshopName())) {
			throw new GlobalException("Please enter valid details", HttpStatus.BAD_REQUEST);
		}
		if (workshopDetails.getSelectedSkills().isEmpty()) {
			throw new GlobalException("Atleast one skill should be added", HttpStatus.BAD_REQUEST);
		}
		if (workshopDetails.getCreatedUserId() == null || !Objects.equals(workshopDetails.getCreatedUserId(), userId)) {
			workshopDetails.setCreatedUserId(userId);
		}
		if (LocalDate.parse(workshopDetails.getWorkshopDate(), dateFormat).isBefore(LocalDate.now())) {
			throw new GlobalException("Workshop date should be valid", HttpStatus.BAD_REQUEST);
		}
		if (workshopDetails.getEndTime().isBefore(workshopDetails.getStartTime())) {
			throw new GlobalException("Workshop time should be valid", HttpStatus.BAD_REQUEST);
		}
		if (workshopDetails.getCapacity() == null) {
			workshopDetails.setCapacity(Constants.WORKSHOP_DEFAULT_CAPACITY);
		}
		if (workshopDetails.getDescription() == null) {
			workshopDetails.setDescription("");
		}
		if (workshopDetails.getVenue() == null) {
			workshopDetails.setVenue(Constants.ONLINE);
		}
		checkAlreadyWorkshopExists(workshopDetails);
	}

	private void checkAlreadyWorkshopExists(WorkshopVO workshopDetails) throws GlobalException {
		List<WorkshopVO> userWorkshops = workshopRepo.getCreatedWorkshopsByDate(workshopDetails.getCreatedUserId(),
				workshopDetails.getWorkshopDate());
		if (!Constants.ONLINE.equalsIgnoreCase(workshopDetails.getVenue())) {
			List<WorkshopVO> allWorkshops = workshopRepo.getWorkshopsByDate(workshopDetails.getWorkshopDate());
			boolean overlappingVenueWorkshop = allWorkshops.stream().anyMatch(workshop -> {
				if (workshopDetails.getVenue().equalsIgnoreCase(workshop.getVenue())) {
					return workshopDetails.getStartTime().isEqual(workshop.getStartTime())
							|| workshopDetails.getEndTime().isEqual(workshop.getEndTime())
							|| (workshopDetails.getStartTime().isBefore(workshop.getEndTime())
									&& workshopDetails.getStartTime().isAfter(workshop.getStartTime()))
							|| workshopDetails.getEndTime().isAfter(workshop.getStartTime())
									&& workshopDetails.getEndTime().isBefore(workshop.getEndTime());
				}
				return false;
			});
			// Throws Global Exception if overlapping time of workshops found
			if (overlappingVenueWorkshop) {
				throw new GlobalException("Already venue occupied at this time, Please choose other venue or time",
						HttpStatus.BAD_REQUEST);
			}
		}

		boolean overlappingWorkshop = userWorkshops.stream().anyMatch(workshop -> {
			return (workshopDetails.getStartTime().isEqual(workshop.getStartTime()))
					|| (workshopDetails.getEndTime().isEqual(workshop.getEndTime()))
					|| (workshopDetails.getStartTime().isBefore(workshop.getEndTime())
							&& workshopDetails.getStartTime().isAfter(workshop.getStartTime()))
					|| workshopDetails.getEndTime().isAfter(workshop.getStartTime())
							&& workshopDetails.getEndTime().isBefore(workshop.getEndTime());
		});

		// Throws Global Exception if overlapping time of workshops found
		if (overlappingWorkshop) {
			throw new GlobalException("Already workshop exists at this time, Please choose other date or time",
					HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public List<String> getRegisteredWorkshopUsersEmail(Integer workshopId) throws GlobalException {
		List<RegisteredUserVO> user = workshopRepo.retrieveWorkshopRegisteredUser(workshopId);
		List<String> userEmails = new ArrayList<>();
		if (user != null) {
			userEmails = user.stream().map(RegisteredUserVO::getEmailId).collect(Collectors.toList());
		}
		return userEmails;
	}

	@Override
	public Boolean updateWorkshop(WorkshopVO workshopDetails, Integer userId) throws GlobalException {
		WorkshopVO workshop = workshopRepo.retrieveWorkshop(workshopDetails.getWorkshopId());
		Boolean isVenueDateChange = false;
		Map<String, Object> updatedFields =  new HashMap<>();
		if (workshopDetails.getCapacity() != null && !workshop.getCapacity().equals(workshopDetails.getCapacity())) {
			updatedFields.put("updatedCapacity", workshopDetails.getCapacity());
		}
		if (workshopDetails.getWorkshopName() != null && !workshop.getWorkshopName().equals(workshopDetails.getWorkshopName())) {
			updatedFields.put("workshopName", workshopDetails.getWorkshopName());
		}
		if (workshopDetails.getDescription() != null && !workshop.getDescription().equals(workshopDetails.getDescription())) {
			updatedFields.put("updatedDescription", workshopDetails.getDescription());
		}
		if (workshopDetails.getVenue() != null && !workshop.getVenue().equals(workshopDetails.getVenue())) {
			updatedFields.put("updatedVenue", workshopDetails.getVenue());
			isVenueDateChange = true;
		}
		if (workshopDetails.getWorkshopDate() != null && !workshop.getWorkshopDate().equals(workshopDetails.getWorkshopDate())) {
			updatedFields.put("workshopDate", workshopDetails.getWorkshopDate());
			isVenueDateChange = true;
		}
		if (workshopDetails.getStartTime() != null && !workshop.getStartTime().equals(workshopDetails.getStartTime())) {
			updatedFields.put("startTime", workshopDetails.getStartTime());
			isVenueDateChange = true;
		}
		if (workshopDetails.getEndTime() != null && !workshop.getEndTime().equals(workshopDetails.getEndTime())) {
			updatedFields.put("endTime", workshopDetails.getEndTime());
			isVenueDateChange = true;
		}
		if (workshopDetails.getSelectedSkills() != null && !workshop.getSelectedSkills().equals(workshopDetails.getSelectedSkills())) {
			updateWorkshopSkills(workshopDetails, workshop.getSelectedSkills());
		} else if(updatedFields.size() == 0) {
			throw new GlobalException("Nothing to Update", HttpStatus.BAD_REQUEST);
		}
		if(isVenueDateChange) {
			validateOverlapping(workshopDetails);
		}
		return workshopRepo.updateWorkshop(updatedFields, workshop.getWorkshopId());
	}
	
	private void validateOverlapping(WorkshopVO workshopDetails) throws GlobalException {
		List<WorkshopVO> userWorkshops = workshopRepo.getCreatedWorkshopsByDate(workshopDetails.getCreatedUserId(),
				workshopDetails.getWorkshopDate());
		userWorkshops = userWorkshops.stream().filter(workshop -> !workshop.getWorkshopId().equals(workshopDetails.getWorkshopId())).collect(Collectors.toList());
		
		if (!Constants.ONLINE.equalsIgnoreCase(workshopDetails.getVenue())) {
			List<WorkshopVO> allWorkshops = workshopRepo.getWorkshopsByDate(workshopDetails.getWorkshopDate());
			allWorkshops = allWorkshops.stream().filter(workshop -> !workshop.getWorkshopId().equals(workshopDetails.getWorkshopId())).collect(Collectors.toList());
			boolean overlappingVenueWorkshop = allWorkshops.stream().anyMatch(workshop -> {
				if (workshopDetails.getVenue().equalsIgnoreCase(workshop.getVenue())) {
					return workshopDetails.getStartTime().isEqual(workshop.getStartTime())
							|| workshopDetails.getEndTime().isEqual(workshop.getEndTime())
							|| (workshopDetails.getStartTime().isBefore(workshop.getEndTime())
									&& workshopDetails.getStartTime().isAfter(workshop.getStartTime()))
							|| workshopDetails.getEndTime().isAfter(workshop.getStartTime())
									&& workshopDetails.getEndTime().isBefore(workshop.getEndTime());
				}
				return false;
			});
			// Throws Global Exception if overlapping time of workshops found
			if (overlappingVenueWorkshop) {
				throw new GlobalException("Already venue occupied at this time, Please choose other venue or time",
						HttpStatus.BAD_REQUEST);
			}
		}

		boolean overlappingWorkshop = userWorkshops.stream().anyMatch(workshop -> {
			return (workshopDetails.getStartTime().isEqual(workshop.getStartTime()))
					|| (workshopDetails.getEndTime().isEqual(workshop.getEndTime()))
					|| (workshopDetails.getStartTime().isBefore(workshop.getEndTime())
							&& workshopDetails.getStartTime().isAfter(workshop.getStartTime()))
					|| workshopDetails.getEndTime().isAfter(workshop.getStartTime())
							&& workshopDetails.getEndTime().isBefore(workshop.getEndTime());
		});

		// Throws Global Exception if overlapping time of workshops found
		if (overlappingWorkshop) {
			throw new GlobalException("Already workshop exists at this time, Please choose other date or time",
					HttpStatus.BAD_REQUEST);
		}
	}

	private void updateWorkshopSkills(WorkshopVO workshop, List<SkillVO> existingSkills) throws GlobalException {
		HashMap<Integer, SkillVO> selectedSkillMap = new HashMap<>();
		HashMap<Integer, SkillVO> existingSkillMap = new HashMap<>();
		List<Integer> deletedSkills = new ArrayList<>();
		List<Integer> addedSkills = new ArrayList<>();
		workshop.getSelectedSkills().stream().forEach(skill -> selectedSkillMap.putIfAbsent(skill.getSkillId(), skill));
		existingSkills.stream().forEach(skill -> existingSkillMap.putIfAbsent(skill.getSkillId(), skill));
		existingSkills.stream().forEach(skill -> {
			if (!selectedSkillMap.containsKey(skill.getSkillId())) {
				deletedSkills.add(skill.getSkillId());
			}
		});
		workshop.getSelectedSkills().stream().forEach(skill -> {
			if (!existingSkillMap.containsKey(skill.getSkillId())) {
				addedSkills.add(skill.getSkillId());
			}
		});
		if (!addedSkills.isEmpty()) {
			workshopRepo.saveWorkshopSkillMap(workshop.getWorkshopId(), addedSkills);
		}
		if (!deletedSkills.isEmpty()) {
			workshopRepo.deleteWorkshopSkillMap(workshop.getWorkshopId(), deletedSkills);
		}
		
	}

	@Override
	public WorkshopsTimeLineVO getAllWorkshops() throws GlobalException {
		List<WorkshopVO> allWorkshops = workshopRepo.getAllWorkshops();
		return getWorkshopTimeLineMapper(allWorkshops);
	}

	private WorkshopsTimeLineVO getWorkshopTimeLineMapper(List<WorkshopVO> allWorkshops) {
		List<WorkshopVO> upComingWorkshops = new ArrayList<>();
		List<WorkshopVO> onGoingWorkshops = new ArrayList<>();
		List<WorkshopVO> pastWorkshops = new ArrayList<>();
		WorkshopsTimeLineVO workshops = new WorkshopsTimeLineVO();
		allWorkshops.stream().forEach(workshop -> {
			if (LocalDate.now().isEqual(LocalDate.parse(workshop.getWorkshopDate()))) {
				if( LocalDateTime.now().isAfter(workshop.getStartTime()) && LocalDateTime.now().isBefore(workshop.getEndTime())) {
					onGoingWorkshops.add(workshop);
				} 
				if ( LocalDateTime.now().isAfter(workshop.getEndTime())) {
					pastWorkshops.add(workshop);
				}
				if ( LocalDateTime.now().isBefore(workshop.getStartTime())) {
					upComingWorkshops.add(workshop);
				}
			} else if (LocalDate.now().isBefore(LocalDate.parse(workshop.getWorkshopDate())) ) {
				upComingWorkshops.add(workshop);
			} else {
				pastWorkshops.add(workshop);
			}
		});
		workshops.setUpComingWorkshops(upComingWorkshops);
		workshops.setOnGoingWorkshops(onGoingWorkshops);
		workshops.setPastWorkshops(pastWorkshops);
		return workshops;
	}

	@Override
	public WorkshopsTimeLineVO getAllCreatedWorkshops(Integer userId) throws GlobalException {
		List<WorkshopVO> allWorkshops = workshopRepo.getCreatedWorkshops(userId);
		return getWorkshopTimeLineMapper(allWorkshops);
	}

	@Override
	public WorkshopsTimeLineVO getAllRegisteredWorkshops(Integer userId) throws GlobalException {
		List<WorkshopVO> allWorkshops = workshopRepo.getEnrolledWorkshops(userId);
		if (allWorkshops == null) {
			log.error("No Workshop Registered");
			allWorkshops = new ArrayList<>();
		}
		return getWorkshopTimeLineMapper(allWorkshops);
	}

	@Override
	public WorkshopVO getWorkshop(Integer workshopId) throws GlobalException {
		WorkshopVO workshop = workshopRepo.retrieveWorkshop(workshopId);
		if (workshop == null) {
			log.error("Workshop Not exists");
			throw new GlobalException("Workshop Not Exists", HttpStatus.BAD_REQUEST);
		}
		workshop.setSelectedSkills(workshopRepo.getWorkshopSkills(workshopId));
		return workshop;
	}

	@Override
	public Boolean deleteWorkshop(Integer workshopId, UserVO user) throws GlobalException {
		WorkshopVO workshop = workshopRepo.retrieveWorkshop(workshopId);
		if (workshop == null ) {
			throw new GlobalException("Workshop does not exists", HttpStatus.BAD_REQUEST);
		}
		if (!Constants.ADMIN.equalsIgnoreCase(user.getUserType()) && !workshop.getCreatedUserId().equals(user.getUserId())) {
			throw new GlobalException("You are not Authorized", HttpStatus.UNAUTHORIZED);
		}
		return workshopRepo.deleteWorkshop(workshopId);
	}

	@Override
	public List<WorkshopVO> searchWorkshops(SearchWorkshopVO searchDetails) throws GlobalException {
		Boolean isSearchContains = false;
		List<WorkshopVO> allWorkshops = workshopRepo.getAllWorkshops(); 
		WorkshopsTimeLineVO workshops = getWorkshopTimeLineMapper(allWorkshops);
		List<WorkshopVO> searchedWorkshops = new ArrayList<>();
		if (!StringUtils.isBlank(searchDetails.getSkill())) {
			isSearchContains = true;
			searchedWorkshops = workshopRepo.getAllWorkshopsBySkills(searchDetails.getSkill());
		} else {
			searchedWorkshops.addAll(allWorkshops);
		}
		if (!StringUtils.isBlank(searchDetails.getWorkshopName())) {
			isSearchContains = true;
			searchedWorkshops = searchedWorkshops.stream().filter(workshop -> workshop.getWorkshopName().contains(searchDetails.getWorkshopName())).collect(Collectors.toList());
		}
		if (!StringUtils.isBlank(searchDetails.getInstructor())) {
			isSearchContains = true;
			searchedWorkshops = searchedWorkshops.stream().filter(workshop -> workshop.getCreatedUser().contains(searchDetails.getInstructor())).collect(Collectors.toList());
		}
		if (searchDetails.getFromDate() != null) {
			isSearchContains = true;
			searchedWorkshops = searchedWorkshops.stream().filter(workshop -> LocalDate.parse(workshop.getWorkshopDate()).isAfter(searchDetails.getFromDate())).collect(Collectors.toList());
		}
		if (searchDetails.getToDate() != null) {
			isSearchContains = true;
			searchedWorkshops = searchedWorkshops.stream().filter(workshop -> LocalDate.parse(workshop.getWorkshopDate()).isBefore(searchDetails.getToDate())).collect(Collectors.toList());
		}
		if (Boolean.FALSE.equals(isSearchContains)) {
			searchedWorkshops = workshops.getUpComingWorkshops(); 
		}
		return searchedWorkshops;
	}

	@Override
	public Boolean enrollWorkshop(Integer workshopId, Integer userId) throws GlobalException {
		List<WorkshopVO> workshops = workshopRepo.getEnrolledWorkshops(userId);
		if (workshops == null ) {
			workshops = new ArrayList<>();
		}
		Set<Integer> workshopIds = workshops.stream().map(WorkshopVO::getWorkshopId).collect(Collectors.toSet());
		if (workshopIds.contains(workshopId)) {
			throw new GlobalException("Already Enrolled", HttpStatus.BAD_REQUEST);
		}
		return workshopRepo.enrollWorkshop(workshopId, userId);
	}

	@Override
	public Boolean requestWorkshop(List<SkillVO> skills, UserVO user) throws GlobalException, EmailException {
		HashMap<String, SkillVO> skillsMap = getAllWorkshopSkillsMap();
		List<SkillVO> requestedSkills = getValidRequestedWorkshops(skillsMap, skills);
		Boolean isSaved = saveRequestedSkill(requestedSkills, user.getUserId());
		if(!requestedSkills.isEmpty() && Boolean.TRUE.equals(isSaved)) {
			String skillsName = requestedSkills.stream().map(SkillVO::getSkillName).collect(Collectors.joining(", "));
			notify.sendRequestSuccessful(user, skillsName);
		}
		checkAnyUpcomingSkillWorkshops(skillsMap, skills);
		return isSaved;
	}

	private Boolean saveRequestedSkill(List<SkillVO> requestedSkills, Integer userId) throws GlobalException {
		HashSet<String> skillNames = (HashSet<String>) skillRepo.getAllSkills().stream().map(SkillVO::getSkillName).map(String::toUpperCase)
				.collect(Collectors.toSet());
		
		List<SkillVO> saveSkills = new ArrayList<>();
		requestedSkills.forEach(skill -> {
			if (!skillNames.contains(skill.getSkillName().toUpperCase())) {
				saveSkills.add(skill);
			}
		});
		skillRepo.saveNewSkills(saveSkills);
		return workshopRepo.saveUserSkillRequest(userId, requestedSkills);
	}
	private List<SkillVO> getValidRequestedWorkshops(HashMap<String, SkillVO> skillsMap, List<SkillVO> skills) throws GlobalException {
		List<SkillVO> requestedSkills = new ArrayList<>();
		skills.stream().forEach(skill -> {
			if (!skillsMap.containsKey(skill.getSkillName())) {
				requestedSkills.add(skill);
			}
		});
		return requestedSkills;		
	}

	private HashMap<String, SkillVO> getAllWorkshopSkillsMap() throws GlobalException {
		List<WorkshopVO> workshops = workshopRepo.getAllWorkshops();
		workshops = getWorkshopTimeLineMapper(workshops).getUpComingWorkshops();
		HashMap<String, SkillVO> skillsMap = new HashMap<>();
		workshops.stream().map(WorkshopVO::getSelectedSkills).forEach(
				selectedSkills -> selectedSkills.forEach(skill -> skillsMap.putIfAbsent(skill.getSkillName(), skill)));
		return skillsMap;
	}

	private void checkAnyUpcomingSkillWorkshops(HashMap<String, SkillVO> skillsMap, List<SkillVO> skills) throws GlobalException {
		List<String> skillName = new ArrayList<>();
		skills.stream().forEach(skill -> {
			if (skillsMap.containsKey(skill.getSkillName())) {
				skillName.add(skill.getSkillName());
			}
		});
		if (!skillName.isEmpty()) {
			throw new GlobalException(
					"Workshop already exists for the skill " + skillName.stream().collect(Collectors.joining(", ")),
					HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public List<RequestVO> getAllReqestedSkills(Integer userId) throws GlobalException {
		List<RequestVO> allRequests = skillRepo.getUserRequestedSkills(userId);
		if (allRequests == null) {
			log.error("No Skills Reqested");
			allRequests = new ArrayList<>();
		}
		return allRequests;
	}

}
