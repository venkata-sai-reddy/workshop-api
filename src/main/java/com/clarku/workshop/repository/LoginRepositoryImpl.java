package com.clarku.workshop.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.clarku.workshop.config.SqlProperties;
import com.clarku.workshop.exception.LoginException;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.vo.LoginVO;
import com.clarku.workshop.vo.UserVO;

import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
public class LoginRepositoryImpl implements ILoginRepo {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String USER_ID = "userId";

	private static final String EMAIL_ID = "emailId";

	@Override
	public LoginVO retrieveUserLogin(String emailId) throws LoginException {
		LoginVO userLoginDetails;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(EMAIL_ID, emailId);
		try {
			userLoginDetails = namedParameterJdbcTemplate.queryForObject(SqlProperties.login.get("getLoginDetailsByEmailId"), parameters, new BeanPropertyRowMapper<LoginVO>(LoginVO.class));
		} catch (EmptyResultDataAccessException exp) {
			log.error("LoginRepositoryImpl :: retrieveUserLogin(): User {} not found in system with error {}", emailId,	exp.getMessage());
			throw new LoginException(Constants.NOT_REGISTERED_EXP, HttpStatus.UNAUTHORIZED);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: retrieveUserLogin(): data access exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: retrieveUserLogin(): exception : {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userLoginDetails;
	}

	@Override
	public UserVO retrieveUserDetails(Integer userId) throws LoginException {
		UserVO userDetails;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(USER_ID, userId);
		try {
			userDetails = namedParameterJdbcTemplate.queryForObject(SqlProperties.user.get("getUserProfDetailsById"), parameters, new BeanPropertyRowMapper<UserVO>(UserVO.class));
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: retrieveUserDetails(): data access exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: retrieveUserDetails(): exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userDetails;
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
	public Boolean isUserExists(String emailId) throws LoginException {
		int count = 0;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(EMAIL_ID, emailId);
		try {
			count = namedParameterJdbcTemplate.queryForObject(SqlProperties.user.get("isUserExistsByEmail"), parameters, Integer.class);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: isUserExists(): data access exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: isUserExists(): exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return count != 0;
	}

	@Override
	public Integer getUserId(String emailId) throws LoginException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue(EMAIL_ID, emailId);
		Integer userId;
		try {
			userId = namedParameterJdbcTemplate.queryForObject(SqlProperties.user.get("getUserIdByEmail"), parameters, Integer.class);
		} catch (DataAccessException exp) {
			log.error("LoginRepositoryImpl :: getUserId(): data access exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("LoginRepositoryImpl :: getUserId(): exception {}", exp.getMessage());
			throw new LoginException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return userId;
	}

}