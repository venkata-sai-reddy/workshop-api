package com.clarku.workshop.repository;

import java.util.List;
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
import com.clarku.workshop.vo.SignUpVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.UserVO;

import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
public class UserRepositoryImpl implements IUserRepo{

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String USER_ID = "userId";

	@Override
	public String getUserFirstName(Integer userId) throws GlobalException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		String firstName;
		try {
			firstName = namedParameterJdbcTemplate.queryForObject(SqlProperties.user.get("getUserFirstNameById"), parameters, String.class);
		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: getUserFirstName(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: getUserFirstName(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return firstName;
	}

	@Override
	public UserVO retrieveUserDetails(Integer userId) throws GlobalException {
		UserVO userDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		try {
			userDetails = namedParameterJdbcTemplate.queryForObject(SqlProperties.user.get("getUserProfDetailsById"), parameters, new BeanPropertyRowMapper<UserVO>(UserVO.class));
		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: retrieveUserDetails(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: retrieveUserDetails(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userDetails;
	}

	@Override
	public Boolean saveSignUpUser(SignUpVO userDetails) throws GlobalException {
		Integer updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("firstName", userDetails.getFirstName());
		parameters.addValue("lastName", userDetails.getLastName());
		parameters.addValue("emailId", userDetails.getEmailId().toLowerCase());
		parameters.addValue("phoneNumber", userDetails.getPhoneNumber());
		parameters.addValue("type", userDetails.getUserType());
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.user.get("saveUserProfDetails"), parameters);
		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: saveSignUpUser(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: saveSignUpUser(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return updateCount != 0;
	}

	@Override
	public void saveUserSkillsByName(Integer userId, List<SkillVO> newSkills) throws GlobalException {
		int size = newSkills.size();
		MapSqlParameterSource[] batchArgs = new MapSqlParameterSource[size];
	    IntStream.range(0, size).forEach(i -> {
	        MapSqlParameterSource args = new MapSqlParameterSource();
	        args.addValue(USER_ID, userId);
	        args.addValue("skillName", newSkills.get(i).getSkillName());
	        batchArgs[i] = args;
	    });
	    try {
			int[] updatedCount = namedParameterJdbcTemplate.batchUpdate(SqlProperties.user.get("saveUserSkillDetailsByName"), batchArgs);
			log.debug("UserRepositoryImpl :: saveUserSkillsByName(): {} Skills Updated to user {}", updatedCount, userId);
	    } catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: saveUserSkillsByName(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: saveUserSkillsByName(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void saveUserSkillsById(Integer userId, List<SkillVO> existingSkills) throws GlobalException {
		int size = existingSkills.size();
		MapSqlParameterSource[] batchArgs = new MapSqlParameterSource[size];
	    IntStream.range(0, size).forEach(i -> {
	        MapSqlParameterSource args = new MapSqlParameterSource();
	        args.addValue(USER_ID, userId);
	        args.addValue("skillId", existingSkills.get(i).getSkillId());
	        batchArgs[i] = args;
	    });
	    try {
			int[] updatedCount = namedParameterJdbcTemplate.batchUpdate(SqlProperties.user.get("saveUserSkillDetailsById"), batchArgs);
			log.debug("UserRepositoryImpl :: saveUserSkillsById(): {} Skills Updated to user {}", updatedCount, userId);
	    } catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: saveUserSkillsById(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: saveUserSkillsById(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public List<SkillVO> retrieveUserSkills(Integer userId) throws GlobalException {
		List<SkillVO> skillDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		try {
			skillDetails = namedParameterJdbcTemplate.query(SqlProperties.skills.get("getUserSkills"), parameters, new BeanPropertyRowMapper<>(SkillVO.class));
		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: retrieveUserSkills(): data access exception {} {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: retrieveUserSkills(): exception : {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return skillDetails;
	}

}
