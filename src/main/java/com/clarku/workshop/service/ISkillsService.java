package com.clarku.workshop.service;

import java.util.List;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.vo.SkillVO;

public interface ISkillsService {

	List<SkillVO> getAllSkills() throws GlobalException;

	List<SkillVO> getAllSystemSkills() throws GlobalException;

	List<SkillVO> getAllRequestedSkills() throws GlobalException;

	Boolean updateRequestedSkills(SkillVO skill) throws GlobalException;

	Boolean addNewSkills(List<SkillVO> skillDetails) throws GlobalException;

}
