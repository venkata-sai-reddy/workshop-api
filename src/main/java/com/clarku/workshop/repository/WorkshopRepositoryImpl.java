package com.clarku.workshop.repository;

import java.time.LocalDate;
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
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.WorkshopVO;

import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
public class WorkshopRepositoryImpl implements IWorkshopRepo{

	private static final String USER_ID = "userId";

	private static final String WORKSHOP_ID = "workshopId";

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
	public Boolean updateWorkshop(Map<String, Object> updatedFields) throws GlobalException {
		int updateCount = 0;
		Boolean isFirstDone = false;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		StringBuilder updateWorkshopQuery = new StringBuilder(SqlProperties.workshop.get("updateWorkshopDynamicFields"));
		updatedFields.forEach((param, value) -> parameters.addValue(param, value));
		if(updatedFields.containsKey("workshopName")) {
			updateWorkshopQuery.append(" workshop_name = :workshopName ");
			isFirstDone = true;
		}
		if (updatedFields.containsKey("updatedDescription")) {
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateWorkshopQuery.append(",");
			}
			updateWorkshopQuery.append(" description = :updatedDescription ");
			isFirstDone = true;
		}
		if(updatedFields.containsKey("updatedVenue")) {
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateWorkshopQuery.append(",");
			}
			updateWorkshopQuery.append(" venue = :updatedVenue ");
			isFirstDone = true;
		}
		if (updatedFields.containsKey("updatedCapacity")) {
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateWorkshopQuery.append(",");
			}
			updateWorkshopQuery.append(" capacity = :updatedCapacity ");
			isFirstDone = true;
		}
		if(updatedFields.containsKey("workshopDate")) {
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateWorkshopQuery.append(",");
			}
			updateWorkshopQuery.append(" workshop_date = :workshopDate ");
			isFirstDone = true;
		}
		if (updatedFields.containsKey("startTime")) {
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateWorkshopQuery.append(",");
			}
			updateWorkshopQuery.append(" start_time = :startTime ");
			isFirstDone = true;
		}
		if (updatedFields.containsKey("endTime")) {
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateWorkshopQuery.append(",");
			}
			updateWorkshopQuery.append(" end_time = :endTime ");
		}
		updateWorkshopQuery.append(" WHERE workshop_id = :workshopId;");
		try {
			updateCount = namedParameterJdbcTemplate.update(updateWorkshopQuery.toString(), parameters);
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: updateWorkshop(): data access exception {}", exp.getMessage());
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
			log.error("WorkshopRepositoryImpl :: deleteWorkshop(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: deleteWorkshop(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return deletedCount != 0;
	}

	@Override
	public WorkshopVO retrieveWorkshop(Integer workshopId) throws GlobalException {
		WorkshopVO workshopDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(WORKSHOP_ID, workshopId);
		try {
			workshopDetails = namedParameterJdbcTemplate.queryForObject(SqlProperties.workshop.get("getWorkshopById"), parameters, new BeanPropertyRowMapper<WorkshopVO>(WorkshopVO.class));
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: getLastCrtdWrkshpByUserId(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: getLastCrtdWrkshpByUserId(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return workshopDetails;
	}

	@Override
	public List<WorkshopVO> getAllWorkshops() throws GlobalException {
		List<WorkshopVO> workshopDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		try {
			workshopDetails = namedParameterJdbcTemplate.query(SqlProperties.workshop.get("getAllWorkshops"), parameters,  new BeanPropertyRowMapper<>(WorkshopVO.class));
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: getAllWorkshops(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: getAllWorkshops(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return workshopDetails;
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
			workshopDetails = namedParameterJdbcTemplate.query(SqlProperties.workshop.get("getAllEnrolledWorkshops"), parameters, new BeanPropertyRowMapper<>(WorkshopVO.class));
		} catch (DataAccessException exp) {
			log.error("WorkshopRepositoryImpl :: getEnrolledWorkshops(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("WorkshopRepositoryImpl :: getEnrolledWorkshops(): exception : {}", exp.getMessage());
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


}
