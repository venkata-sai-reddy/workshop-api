package com.clarku.workshop.repository;

import java.util.List;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.vo.RequestVO;
import com.clarku.workshop.vo.SkillVO;

public interface ISkillsRepo {

	List<SkillVO> getAllSkills() throws GlobalException;
	
	Boolean saveNewSkills(List<SkillVO> skills) throws GlobalException;

	List<RequestVO> getUserRequestedSkills(Integer userId) throws GlobalException;

	Boolean updateSkillStatus(SkillVO skill) throws GlobalException;

}
