package com.clarku.workshop.repository;

import java.util.List;

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
import com.clarku.workshop.vo.VenueVO;

import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
public class AdminRepositoryImpl implements IAdminRepo{

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public List<VenueVO> retrieveVenues() throws GlobalException {
		List<VenueVO> venues = null;
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		try {
			venues = namedParameterJdbcTemplate.query(SqlProperties.admin.get("getAllVenues"), parameters, new BeanPropertyRowMapper<>(VenueVO.class));
		} catch (DataAccessException exp) {
			log.error("AdminRepositoryImpl :: retrieveVenues(): data access exception {} {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("AdminRepositoryImpl :: retrieveVenues(): exception : {} {}", exp.getMessage(), exp.getCause());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return venues;
	}

}
