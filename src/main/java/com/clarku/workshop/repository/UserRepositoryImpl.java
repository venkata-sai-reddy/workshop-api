package com.clarku.workshop.repository;

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
import com.clarku.workshop.vo.LoginVO;
import com.clarku.workshop.vo.SignUpVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.UserProfileVO;
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

	@Override
	public HashMap<Integer, List<UserVO>> getSkilledUsers(List<Integer> skillIds) throws GlobalException {
		HashMap<Integer, List<UserVO>> userDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("skillIds", skillIds);
		try {
			List<Map<String, Object>> usersResultMap = namedParameterJdbcTemplate.queryForList(SqlProperties.user.get("getSkilledUsersList"), parameters);
			userDetails = wrapUserDetails(usersResultMap);

		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: getSkilledUsers(): data access exception {} {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: getSkilledUsers(): exception : {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userDetails;
	}

	private HashMap<Integer, List<UserVO>> wrapUserDetails(List<Map<String, Object>> usersResultMap) {
		HashMap<Integer,  List<UserVO>> skillIdMap = new HashMap<>();
		usersResultMap.stream().forEach(user -> {
			SkillVO skill = new SkillVO();
            skill.setSkillId(Integer.parseInt(user.get("skillId").toString()));
			skill.setSkillName(user.getOrDefault("skillName", "").toString());
			skill.setStatus(user.getOrDefault("status", "").toString());
			UserVO userDetails = new UserVO();
			userDetails.setEmailId(user.getOrDefault("emailId", "").toString());
			userDetails.setFirstName(user.getOrDefault("firstName", "").toString());
			userDetails.setLastName(user.getOrDefault("lastName", "").toString());
			userDetails.setUserId(Integer.parseInt(user.getOrDefault("userId", "").toString()));
			userDetails.setUserType(user.getOrDefault("userType", "").toString());
			userDetails.setSkills(new ArrayList<>());
			userDetails.getSkills().add(skill);
			if( skillIdMap.containsKey(Integer.parseInt(user.get("skillId").toString()))){
				skillIdMap.get(user.get("skillId")).add(userDetails);
			} else {
				List<UserVO> users = new ArrayList<>();
				users.add(userDetails);
				skillIdMap.put(Integer.parseInt(user.get("skillId").toString()), users);
			}
		});
		return skillIdMap;
	}

	@Override
	public LoginVO retrieveUserLoginDetails(Integer userId) throws GlobalException {
		LoginVO userLoginDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		try {
			userLoginDetails = namedParameterJdbcTemplate.queryForObject(SqlProperties.login.get("getLoginDetails"), parameters, new BeanPropertyRowMapper<LoginVO>(LoginVO.class));
		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: retrieveUserLoginDetails(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: retrieveUserLoginDetails(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userLoginDetails;
	}

	@Override
	public Boolean updateUserPass(Integer userId, String password) throws GlobalException {
		Integer updateCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("userId", userId);
		parameters.addValue("password", password);
		try {
			updateCount = namedParameterJdbcTemplate.update(SqlProperties.login.get("saveUserPass"), parameters);
		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: updateUserPass(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: updateUserPass(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return updateCount != 0;
	}

	@Override
	public Boolean updateUserProf(Integer userId, HashMap<String, String> updatedFields) throws GlobalException {
		int updateCount = 0;
		Boolean isFirstDone = false;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		StringBuilder updateUserQuery = new StringBuilder(SqlProperties.user.get("updateUserDynamicFields"));

		parameters.addValue(USER_ID, userId);
		if(updatedFields.containsKey("firstName")) {
			parameters.addValue("firstName", updatedFields.get("firstName"));
			updateUserQuery.append(" first_name = :firstName ");
			isFirstDone = true;
		}
		if (updatedFields.containsKey("lastName")) {
			parameters.addValue("lastName", updatedFields.get("lastName"));
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateUserQuery.append(",");
			}
			updateUserQuery.append(" last_name = :lastName ");
			isFirstDone = true;
		}
		if(updatedFields.containsKey("phoneNumber")) {
			parameters.addValue("phoneNumber", updatedFields.get("phoneNumber"));
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateUserQuery.append(",");
			}
			updateUserQuery.append(" phone_number = :phoneNumber ");
			isFirstDone = true;
		}
		if (updatedFields.containsKey("emailId")) {
			parameters.addValue("emailId", updatedFields.get("emailId"));
			if (Boolean.TRUE.equals(isFirstDone)) { 
				updateUserQuery.append(",");
			}
			updateUserQuery.append(" email_id = :emailId ");
			isFirstDone = true;
		}
		updateUserQuery.append(" WHERE user_id = :userId;");
		try {
			if (Boolean.TRUE.equals(isFirstDone)) {
				updateCount = namedParameterJdbcTemplate.update(updateUserQuery.toString(), parameters);
			}
		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: updateUserProf(): data access exception {} {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: updateUserProf(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return updateCount != 0;
	}

	@Override
	public void deleteUserSkillsById(Integer userId, List<SkillVO> deletedSkills) throws GlobalException {
		int size = deletedSkills.size();
		MapSqlParameterSource[] batchArgs = new MapSqlParameterSource[size];
	    IntStream.range(0, size).forEach(i -> {
	        MapSqlParameterSource args = new MapSqlParameterSource();
	        args.addValue(USER_ID, userId);
	        args.addValue("skillId", deletedSkills.get(i).getSkillId());
	        batchArgs[i] = args;
	    });
	    try {
			int[] updatedCount = namedParameterJdbcTemplate.batchUpdate(SqlProperties.user.get("deleteUserSkillsById"), batchArgs);
			log.debug("UserRepositoryImpl :: deleteUserSkillsById(): {} Skills Updated to user {}", updatedCount, userId);
	    } catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: deleteUserSkillsById(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: deleteUserSkillsById(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public List<UserProfileVO> getUsers() throws GlobalException {
		List<UserProfileVO> allUsers = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		try {
			allUsers = namedParameterJdbcTemplate.query(SqlProperties.admin.get("getAllUsers"), parameters, new BeanPropertyRowMapper<>(UserProfileVO.class));
		} catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: getUsers(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: getUsers(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return allUsers;
	}

}
