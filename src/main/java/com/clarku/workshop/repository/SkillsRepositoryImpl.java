package com.clarku.workshop.repository;

import java.util.ArrayList;
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
import com.clarku.workshop.vo.RequestVO;
import com.clarku.workshop.vo.SkillVO;

import lombok.extern.log4j.Log4j2;

@Repository
@Log4j2
public class SkillsRepositoryImpl implements ISkillsRepo{

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


	@Override
	public List<SkillVO> getAllSkills() throws GlobalException {
		List<SkillVO> allSkills = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		try {
			allSkills = namedParameterJdbcTemplate.query(SqlProperties.skills.get("getAllSkills"), parameters, new BeanPropertyRowMapper<>(SkillVO.class));
		} catch (DataAccessException exp) {
			log.error("SkillsRepositoryImpl :: getAllSkills(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("SkillsRepositoryImpl :: getAllSkills(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return allSkills;
	}

	@Override
	public Boolean saveNewSkills(List<SkillVO> userSkills) throws GlobalException {
		int count = 0;
		int size = userSkills.size();
		MapSqlParameterSource[] batchArgs = new MapSqlParameterSource[size];
	    IntStream.range(0, size).forEach(i -> {
	        MapSqlParameterSource args = new MapSqlParameterSource();
	        args.addValue("skillName", userSkills.get(i).getSkillName());
	        args.addValue("status", Constants.REQUESTED);
	        batchArgs[i] = args;
	    });
	    try {
			int[] updatedCount = namedParameterJdbcTemplate.batchUpdate(SqlProperties.skills.get("saveUserSkills"), batchArgs);
			log.debug("UserRepositoryImpl :: saveUserSkillsById(): {} Skills Updated", updatedCount);
			count = updatedCount.length;
	    } catch (DataAccessException exp) {
			log.error("UserRepositoryImpl :: saveUserSkillsById(): data access exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("UserRepositoryImpl :: saveUserSkillsById(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	    return count != 0; 
	}

	@Override
	public List<RequestVO> getUserRequestedSkills(Integer userId) throws GlobalException {
		List<RequestVO> allSkills = new ArrayList<>();
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("userId", userId);
		try {
			allSkills = namedParameterJdbcTemplate.query(SqlProperties.skills.get("getUserRequestedSkills"), parameters, new BeanPropertyRowMapper<>(RequestVO.class));
		} catch (DataAccessException exp) {
			log.error("SkillsRepositoryImpl :: getUserRequestedSkills(): data access exception {} {}", exp.getMessage(), exp.getCause());
		} catch (Exception exp) {
			log.error("SkillsRepositoryImpl :: getUserRequestedSkills(): exception {}", exp.getMessage());
			throw new GlobalException(Constants.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return allSkills;
	}

}
