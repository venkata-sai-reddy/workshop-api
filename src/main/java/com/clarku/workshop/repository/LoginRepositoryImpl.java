package com.clarku.workshop.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.clarku.workshop.config.SqlProperties;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.exception.LoginException;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.vo.LoginVO;
import com.clarku.workshop.vo.SignUpVO;

import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
public class LoginRepositoryImpl implements ILoginRepo {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String USER_ID = "userId";

	private static final String EMAIL_ID = "emailId";

	@Override
	public LoginVO retrieveUserLogin(String emailId) throws GlobalException {
		LoginVO userLoginDetails = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(EMAIL_ID, emailId.toLowerCase());
		try {
			userLoginDetails = namedParameterJdbcTemplate.queryForObject(SqlProperties.login.get("getLoginDetailsByEmailId"), parameters, new BeanPropertyRowMapper<LoginVO>(LoginVO.class));
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: retrieveUserLogin(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: retrieveUserLogin(): exception : {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userLoginDetails;
	}

	@Override
	public void lockUserAccount(Integer userId) throws LoginException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		parameters.addValue("locked", true);
		try {
			namedParameterJdbcTemplate.update(SqlProperties.login.get("lockUserAccount"), parameters);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: lockUserAccount(): data access exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: lockUserAccount(): exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void updateUnSuccessAttempt(Integer userId) throws LoginException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		try {
			namedParameterJdbcTemplate.update(SqlProperties.login.get("updateFailedAttempts"), parameters);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: updateUnSuccessAttempt(): data access exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: updateUnSuccessAttempt(): exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void updateLastLogin(Integer userId) throws LoginException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		try {
			namedParameterJdbcTemplate.update(SqlProperties.login.get("updateLastLogin"), parameters);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: updateLastLogin(): data access exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: updateLastLogin(): exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public Boolean isUserExists(String emailId) throws GlobalException {
		int count = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(EMAIL_ID, emailId.toLowerCase());
		try {
			count = namedParameterJdbcTemplate.queryForObject(SqlProperties.user.get("isUserExistsByEmail"), parameters, Integer.class);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: isUserExists(): data access exception {}", exp.getMessage());
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: isUserExists(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return count != 0;
	}

	@Override
	public Integer getUserId(String emailId) throws GlobalException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(EMAIL_ID, emailId.toLowerCase());
		Integer userId;
		try {
			userId = namedParameterJdbcTemplate.queryForObject(SqlProperties.user.get("getUserIdByEmail"), parameters, Integer.class);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: getUserId(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: getUserId(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userId;
	}

	@Override
	public Boolean saveTempPassword(Integer userId, String tempPass) throws GlobalException {
		int updatedCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		parameters.addValue("tempPassword", tempPass);
		try {
			updatedCount = namedParameterJdbcTemplate.update(SqlProperties.login.get("saveTempPassQuery"), parameters);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: saveTempPassword(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: saveTempPassword(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return updatedCount == 1;
	}

	@Override
	public Boolean saveLoginDetails(SignUpVO userDetails) throws GlobalException {
		int updatedCount = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(EMAIL_ID, userDetails.getEmailId().toLowerCase());
		parameters.addValue("password", userDetails.getConfirmPassword());
		try {
			updatedCount = namedParameterJdbcTemplate.update(SqlProperties.login.get("saveLoginDetails"), parameters);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: saveLoginDetails(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: saveLoginDetails(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return updatedCount == 1;
	}

}
