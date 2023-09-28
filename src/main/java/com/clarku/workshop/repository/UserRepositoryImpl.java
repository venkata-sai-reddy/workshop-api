package com.clarku.workshop.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.clarku.workshop.config.SqlProperties;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.utils.Constants;

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

}
