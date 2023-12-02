package com.clarku.workshop.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.clarku.workshop.config.SqlProperties;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.vo.RegisteredUserVO;
import com.clarku.workshop.vo.RequestVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.WorkshopVO;

import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
public class WorkshopRepositoryImpl implements IWorkshopRepo{

	private static final String USER_ID = "userId";

	private static final String WORKSHOP_ID = "workshopId";

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0");

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public Boolean createWorkshop(WorkshopVO workshopDetails) throws GlobalException {
		Integer updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("workshopName", workshopDetails.getWorkshopName());
		parameters.addValue("description", workshopDetails.getDescription());
		parameters.addValue("capacity", workshopDetails.getCapacity());
		parameters.addValue(USER_ID, workshopDetails.getCreatedUserId());
		parameters.addValue("venue", workshopDetails.getVenue());
		parameters.addValue("workshopDate", LocalDate.parse(workshopDetails.getWorkshopDate()));
		parameters.addValue("startTime", workshopDetails.getStartTime());
		parameters.addValue("endTime", workshopDetails.getEndTime());
		parameters.addValue("createdDate", LocalDateTime.now().format(DATE_TIME_FORMATTER).toString());
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.workshop.get("saveWorkshop"), parameters);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: createWorkshop(): data access exception {}, {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: createWorkshop(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return updateCount != 0;
	}

	@Override
	public WorkshopVO getLastCrtdWrkshpByUserId(Integer userId) throws GlobalException {
		WorkshopVO workshopDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		try {
			workshopDetails = namedParameterJdbcTemplate.queryForObject(SqlProperties.workshop.get("getLatestWorkshopByUserId"), parameters, new BeanPropertyRowMapper<WorkshopVO>(WorkshopVO.class));
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: getLastCrtdWrkshpByUserId(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: getLastCrtdWrkshpByUserId(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return workshopDetails;
	}

	@Override
	public Boolean saveWorkshopSkillMap(Integer workshopId, List<Integer> skills) throws GlobalException {
		int size = skills.size();
		int[] updatedCount = null;
		MapSqlParameterSource[] batchArgs = new MapSqlParameterSource[size];
	    IntStream.range(0, size).forEach(i -> {
	        MapSqlParameterSource args = new MapSqlParameterSource();
	        args.addValue(WORKSHOP_ID, workshopId);
	        args.addValue("skillId", skills.get(i));
	        batchArgs[i] = args;
	    });
	    try {
			updatedCount = namedParameterJdbcTemplate.batchUpdate(SqlProperties.workshop.get("saveWorkshopSkillsInfo"), batchArgs);
			log.debug("WorkshopRepositoryImpl :: saveWorkshopSkillMap(): {} Skills Updated to user workshop Id {}", updatedCount, workshopId);
	    } catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: saveWorkshopSkillMap(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: saveWorkshopSkillMap(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return (updatedCount != null && updatedCount.length != 0);
	}
	
	@Override
	public Boolean deleteWorkshopSkillMap(Integer workshopId, List<Integer> skills) throws GlobalException {
		int size = skills.size();
		int[] updatedCount = null;
		MapSqlParameterSource[] batchArgs = new MapSqlParameterSource[size];
	    IntStream.range(0, size).forEach(i -> {
	        MapSqlParameterSource args = new MapSqlParameterSource();
	        args.addValue(WORKSHOP_ID, workshopId);
	        args.addValue("skillId", skills.get(i));
	        batchArgs[i] = args;
	    });
	    try {
			updatedCount = namedParameterJdbcTemplate.batchUpdate(SqlProperties.workshop.get("deleteWorkshopSkillsInfo"), batchArgs);
			log.debug("WorkshopRepositoryImpl :: deleteWorkshopSkillMap(): {} Skills Updated to user workshop Id {}", updatedCount, workshopId);
	    } catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: deleteWorkshopSkillMap(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: deleteWorkshopSkillMap(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return (updatedCount != null && updatedCount.length != 0);
	}

	@Override
	public Boolean updateWorkshop(Map<String, Object> updatedFields, Integer workshopId) throws GlobalException {
		int updateCount = 0;
		Boolean isFirstDone = false;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		StringBuilder updateWorkshopQuery = new StringBuilder(SqlProperties.workshop.get("updateWorkshopDynamicFields"));
		
		parameters.addValue(WORKSHOP_ID, workshopId);
		if(updatedFields.containsKey("workshopName")) {
			parameters.addValue("workshopName", updatedFields.get("workshopName"));
			updateWorkshopQuery.append(" workshop_name = :workshopName ");
			isFirstDone = true;
		}
		if (updatedFields.containsKey("updatedDescription")) {
			parameters.addValue("updatedDescription", updatedFields.get("updatedDescription"));
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateWorkshopQuery.append(",");
			}
			updateWorkshopQuery.append(" description = :updatedDescription ");
			isFirstDone = true;
		}
		if(updatedFields.containsKey("updatedVenue")) {
			parameters.addValue("updatedVenue", updatedFields.get("updatedVenue"));
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateWorkshopQuery.append(",");
			}
			updateWorkshopQuery.append(" venue = :updatedVenue ");
			isFirstDone = true;
		}
		if (updatedFields.containsKey("updatedCapacity")) {
			parameters.addValue("updatedCapacity", updatedFields.get("updatedCapacity"));
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateWorkshopQuery.append(",");
			}
			updateWorkshopQuery.append(" capacity = :updatedCapacity ");
			isFirstDone = true;
		}
		if(updatedFields.containsKey("workshopDate")) {
			parameters.addValue("workshopDate", LocalDate.parse(updatedFields.get("workshopDate").toString()));
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateWorkshopQuery.append(",");
			}
			updateWorkshopQuery.append(" workshop_date = :workshopDate ");
			isFirstDone = true;
		}
		if (updatedFields.containsKey("startTime")) {
			parameters.addValue("startTime", updatedFields.get("startTime"));
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateWorkshopQuery.append(",");
			}
			updateWorkshopQuery.append(" start_time = :startTime ");
			isFirstDone = true;
		}
		if (updatedFields.containsKey("endTime")) {
			parameters.addValue("endTime", updatedFields.get("endTime"));
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateWorkshopQuery.append(",");
			}
			updateWorkshopQuery.append(" end_time = :endTime ");
			isFirstDone = true;
		}
		updateWorkshopQuery.append(" WHERE workshop_id = :workshopId;");
		try {
			if (Boolean.TRUE.equals(isFirstDone)) {
				updateCount = namedParameterJdbcTemplate.update(updateWorkshopQuery.toString(), parameters);
			}
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: updateWorkshop(): data access exception {} {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: updateWorkshop(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return updateCount != 0;
	}

	@Override
	public Boolean deleteWorkshop(Integer workshopId) throws GlobalException {
		int deletedCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(WORKSHOP_ID, workshopId);
		try {
			deletedCount = namedParameterJdbcTemplate.update(SqlProperties.workshop.get("deleteWorkshopById"), parameters);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: deleteWorkshop(): data access exception {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: deleteWorkshop(): exception {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return deletedCount != 0;
	}

	@Override
	public WorkshopVO retrieveWorkshop(Integer workshopId, Integer userId) throws GlobalException {
		WorkshopVO workshopDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(WORKSHOP_ID, workshopId);
		parameters.addValue(USER_ID, userId);
		try {
			List<Map<String, Object>> workshopResultMap = namedParameterJdbcTemplate.queryForList(SqlProperties.workshop.get("getWorkshopById"), parameters);
			workshopDetails = wrapWorkshopDetails(workshopResultMap).get(0);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: getLastCrtdWrkshpByUserId(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: getLastCrtdWrkshpByUserId(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return workshopDetails;
	}

	@Override
	public List<WorkshopVO> getAllWorkshops(Integer userId) throws GlobalException {
		List<WorkshopVO> workshopDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		try {
			List<Map<String, Object>> workshopResultMap = namedParameterJdbcTemplate.queryForList(SqlProperties.workshop.get("getAllWorkshops"), parameters);
			workshopDetails = wrapWorkshopDetails(workshopResultMap);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: getAllWorkshops(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: getAllWorkshops(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return workshopDetails;
	}
	
	private List<WorkshopVO> wrapWorkshopDetails(List<Map<String, Object>> workshopResult) {
		List<WorkshopVO> workshops = new ArrayList<>();
		HashMap<Integer, WorkshopVO> workshopMap = new HashMap<>();
		workshopResult.stream().forEach(workshop -> {
			if( workshopMap.containsKey(Integer.parseInt(workshop.get(WORKSHOP_ID).toString()))){
				SkillVO skill = new SkillVO();
				skill.setSkillId(Integer.parseInt(workshop.get("skillId").toString()));
				skill.setSkillName(workshop.getOrDefault("skillName", "").toString());
				skill.setStatus(workshop.getOrDefault("status", "").toString());
				workshopMap.get(workshop.get(WORKSHOP_ID)).getSelectedSkills().add(skill);
			} else {
				WorkshopVO workshopVO = new WorkshopVO();
				workshopVO.setWorkshopId(Integer.parseInt(workshop.get(WORKSHOP_ID).toString()));
	            workshopVO.setWorkshopName(workshop.getOrDefault("workshopName", "").toString());
	            workshopVO.setCapacity(Integer.parseInt(workshop.getOrDefault("capacity", 30).toString()));
	            workshopVO.setEnrollCount(Integer.parseInt(workshop.getOrDefault("enrollCount", "0").toString()));
	            workshopVO.setIsUserEnrolled(Boolean.parseBoolean(workshop.getOrDefault("enrolled", "false").toString()));
	            workshopVO.setDescription(workshop.getOrDefault("description", "").toString());
	            workshopVO.setCreatedDate(LocalDateTime.parse(workshop.getOrDefault("createdDate", "").toString(), DATE_TIME_FORMATTER));
	            workshopVO.setCreatedUser(workshop.getOrDefault("createdUser", "").toString());
	            workshopVO.setCreatedUserId(Integer.parseInt(workshop.getOrDefault("createdUserId", 0).toString()));
	            workshopVO.setEndTime(LocalDateTime.parse(workshop.getOrDefault("endTime", "").toString(), DATE_TIME_FORMATTER));
	            workshopVO.setStartTime(LocalDateTime.parse(workshop.getOrDefault("startTime", "").toString(), DATE_TIME_FORMATTER));
	            workshopVO.setVenue(workshop.getOrDefault("venue", "").toString());
	            workshopVO.setWorkshopDate(workshop.getOrDefault("workshopDate", "").toString());
	            workshopVO.setMeetingURL(String.valueOf(workshop.getOrDefault("meetingUrl", "")));
	            workshopVO.setSelectedSkills(new ArrayList<>());
	            SkillVO skill = new SkillVO();
				skill.setSkillId(Integer.parseInt(workshop.get("skillId").toString()));
				skill.setSkillName(workshop.getOrDefault("skillName", "").toString());
				skill.setStatus(workshop.getOrDefault("status", "").toString());
				workshopVO.getSelectedSkills().add(skill);
				workshopMap.put(workshopVO.getWorkshopId(), workshopVO);
			}
		});
		workshopMap.forEach((workshopId, workshop) -> workshops.add(workshop));
		return workshops;
	}

	@Override
	public List<WorkshopVO> getCreatedWorkshops(Integer userId) throws GlobalException {
		List<WorkshopVO> workshopDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		try {
			workshopDetails = namedParameterJdbcTemplate.query(SqlProperties.workshop.get("getAllUserWorkshopsByUserId"), parameters, new BeanPropertyRowMapper<>(WorkshopVO.class));
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: getCreatedWorkshops(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: getCreatedWorkshops(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return workshopDetails;
	}

	@Override
	public List<WorkshopVO> getEnrolledWorkshops(Integer userId) throws GlobalException {
		List<WorkshopVO> workshopDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		try {
			List<Map<String, Object>> workshopResultMap = namedParameterJdbcTemplate.queryForList(SqlProperties.workshop.get("getAllEnrolledWorkshops"), parameters);
			workshopDetails = wrapWorkshopDetails(workshopResultMap);
		
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: getEnrolledWorkshops(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: getEnrolledWorkshops(): exception : {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return workshopDetails;
	}

	@Override
	public List<WorkshopVO> getCreatedWorkshopsByDate(Integer userId, String workshopDate)
			throws GlobalException {
		List<WorkshopVO> workshopDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		parameters.addValue("workshopDate", LocalDate.parse(workshopDate));
		try {
			workshopDetails = namedParameterJdbcTemplate.query(SqlProperties.workshop.get("getUserWorkshopsByDate"), parameters, new BeanPropertyRowMapper<>(WorkshopVO.class));
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: getCreatedWorkshopsByDate(): data access exception {} {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: getCreatedWorkshopsByDate(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return workshopDetails;
	}

	@Override
	public List<WorkshopVO> getWorkshopsByDate(String workshopDate) throws GlobalException {
		List<WorkshopVO> workshopDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("workshopDate", LocalDate.parse(workshopDate));
		try {
			workshopDetails = namedParameterJdbcTemplate.query(SqlProperties.workshop.get("getWorkshopsByDate"), parameters, new BeanPropertyRowMapper<>(WorkshopVO.class));
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: getWorkshopsByDate(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: getWorkshopsByDate(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return workshopDetails;
	}

	@Override
	public List<RegisteredUserVO> retrieveWorkshopRegisteredUser(Integer workshopId) throws GlobalException {
		List<RegisteredUserVO> registeredUsers = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(WORKSHOP_ID, workshopId);
		try {
			registeredUsers = namedParameterJdbcTemplate.query(SqlProperties.workshop.get("getWorkshopRegisteredUsers"), parameters, new BeanPropertyRowMapper<>(RegisteredUserVO.class));
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: retrieveWorkshopRegisteredUser(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: retrieveWorkshopRegisteredUser(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return registeredUsers;
	}

	@Override
	public List<WorkshopVO> getAllWorkshopsBySkills(String skill) throws GlobalException {
		List<WorkshopVO> workshopDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("skill", skill);
		try {
			workshopDetails = namedParameterJdbcTemplate.query(SqlProperties.workshop.get("getAllWorkshopsBySkill"), parameters, new BeanPropertyRowMapper<>(WorkshopVO.class));
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: getAllWorkshopsBySkills(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: getAllWorkshopsBySkills(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return workshopDetails;
	}

	@Override
	public Boolean enrollWorkshop(Integer workshopId, Integer userId) throws GlobalException {
		Integer saveCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(WORKSHOP_ID, workshopId);
		parameters.addValue(USER_ID, userId);
		parameters.addValue("registeredDate", LocalDateTime.now().format(DATE_TIME_FORMATTER).toString());
		try {
			saveCount = namedParameterJdbcTemplate.update(SqlProperties.workshop.get("saveUserEnrollment"), parameters);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: enrollWorkshop(): data access exception {}, {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: enrollWorkshop(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return saveCount != 0;
	}

	@Override
	public Boolean unEnrollWorkshop(Integer workshopId, Integer userId) throws GlobalException {
		Integer saveCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(WORKSHOP_ID, workshopId);
		parameters.addValue(USER_ID, userId);
		try {
			saveCount = namedParameterJdbcTemplate.update(SqlProperties.workshop.get("deleteUserEnrollment"), parameters);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: unEnrollWorkshop(): data access exception {}, {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: unEnrollWorkshop(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return saveCount != 0;
	}

	@Override
	public List<SkillVO> getWorkshopSkills(Integer workshopId) throws GlobalException {
		List<SkillVO> skillDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(WORKSHOP_ID, workshopId);
		try {
			skillDetails = namedParameterJdbcTemplate.query(SqlProperties.skills.get("getSkillOfWorkshop"), parameters, new BeanPropertyRowMapper<>(SkillVO.class));
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: getWorkshopSkills(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: getWorkshopSkills(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return skillDetails;
	}

	@Override
	public Boolean saveUserSkillRequest(Integer userId, List<SkillVO> requestedSkills) throws GlobalException {
		int[] saveCount = null;
		int size = requestedSkills.size();
		String requestedDate = LocalDateTime.now().format(DATE_TIME_FORMATTER).toString();
		MapSqlParameterSource[] batchArgs = new MapSqlParameterSource[size];
	    IntStream.range(0, size).forEach(i -> {
	        MapSqlParameterSource args = new MapSqlParameterSource();
	        args.addValue(USER_ID, userId);
	        args.addValue("skillName", requestedSkills.get(i).getSkillName());
	        args.addValue("requestedDate", requestedDate);
	        args.addValue("status", Constants.REQUESTED);
	        batchArgs[i] = args;
	    });
	    try {
			saveCount = namedParameterJdbcTemplate.batchUpdate(SqlProperties.workshop.get("saveUserSkillRequest"), batchArgs);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: saveUserSkillRequest(): data access exception {}, {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: saveUserSkillRequest(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return saveCount != null;
	}

	@Override
	public List<RequestVO> getRecentlyRequestedSkillRequest() throws GlobalException {
		List<RequestVO> requestDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		try {
			requestDetails = namedParameterJdbcTemplate.query(SqlProperties.workshop.get("getRecentRequestedSkills"), parameters, new BeanPropertyRowMapper<>(RequestVO.class));
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: getRecentlyRequestedSkillRequest(): data access exception {} {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: getWorkgetRecentlyRequestedSkillRequestshopSkills(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return requestDetails;
	}

	@Override
	public Boolean checkIsUserEnrolled(Integer userId, Integer workshopId) throws GlobalException {
		List<Map<String, Object>> result = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(WORKSHOP_ID, workshopId);
		parameters.addValue(USER_ID, userId);
		try {
			result = namedParameterJdbcTemplate.queryForList(SqlProperties.workshop.get("checkIsUserEnrolled"), parameters);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: checkIsUserEnrolled(): data access exception {}, {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: checkIsUserEnrolled(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return !result.isEmpty();
	}

	@Override
	public void incrementWorkshopEnrollCount(Integer workshopId) throws GlobalException {
		Integer updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(WORKSHOP_ID, workshopId);
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.workshop.get("incrementWorkshopEnrollCount"), parameters);
			log.info("WorkshopRepositoryImpl :: incrementWorkshopEnrollCount(): Update Count : {}", updateCount);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: incrementWorkshopEnrollCount(): data access exception {}, {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: incrementWorkshopEnrollCount(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void decrementWorkshopEnrollCount(Integer workshopId) throws GlobalException {
		Integer updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(WORKSHOP_ID, workshopId);
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.workshop.get("decrementWorkshopEnrollCount"), parameters);
			log.info("WorkshopRepositoryImpl :: decrementWorkshopEnrollCount(): Update Count : {}", updateCount);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: decrementWorkshopEnrollCount(): data access exception {}, {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: decrementWorkshopEnrollCount(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void updateRequestSkillsStatus(List<Integer> skillsNotified, String status) throws GlobalException {
		int updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("skillIds", skillsNotified);
		parameters.addValue("status", status);
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.workshop.get("updateSkillRequestNotified"), parameters);
			log.info("Updated the requested to notified count : {}", updateCount);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: updateRequestSkillsStatus(): data access exception {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: updateRequestSkillsStatus(): exception {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Boolean isUserCreatedWorkshop(Integer userId, Integer workshopId) throws GlobalException {
		List<Map<String, Object>> result = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(WORKSHOP_ID, workshopId);
		parameters.addValue(USER_ID, userId);
		try {
			result = namedParameterJdbcTemplate.queryForList(SqlProperties.workshop.get("checkIsUserCreatedWorkshop"), parameters);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: isUserCreatedWorkshop(): data access exception {}, {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: isUserCreatedWorkshop(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return !result.isEmpty();
	}

	@Override
	public Boolean saveOrUpdateWorkshopMettingDetails(Integer workshopId, String meetingURL) throws GlobalException {
		int saveCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(WORKSHOP_ID, workshopId);
		parameters.addValue("meetingUrl", meetingURL);
		try {
			saveCount = namedParameterJdbcTemplate.update(SqlProperties.workshop.get("saveWorkshopMeetingdetails"), parameters);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: saveWorkshopMettingDetails(): data access exception {}, {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: saveWorkshopMettingDetails(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return saveCount != 0;
	}

	@Override
	public String getWorkshopMeetingDetails(Integer workshopId) throws GlobalException {
		String meetingUrl = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(WORKSHOP_ID, workshopId);
		try {
			meetingUrl = namedParameterJdbcTemplate.queryForObject(SqlProperties.workshop.get("getWorkshopMeetingURL"), parameters, String.class);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: getWorkshopMeetingDetails(): data access exception {}, {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: getWorkshopMeetingDetails(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return meetingUrl;
	}

	@Override
	public List<WorkshopVO> getNextTwoDaysWorkshops() throws GlobalException {
		List<WorkshopVO> workshopDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		try {
			List<Map<String, Object>> workshopResultMap = namedParameterJdbcTemplate.queryForList(SqlProperties.workshop.get("getNextTwoDaysWorkshops"), parameters);
			workshopDetails = wrapWorkshopDetails(workshopResultMap);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: getNextTwoDaysWorkshops(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: getNextTwoDaysWorkshops(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return workshopDetails;
	}

}
