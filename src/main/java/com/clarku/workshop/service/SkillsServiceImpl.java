package com.clarku.workshop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.repository.ISkillsRepo;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.vo.SkillVO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SkillsServiceImpl implements ISkillsService {

	@Autowired
	private ISkillsRepo skillRepo;

	@Override
	public List<SkillVO> getAllSkills() throws GlobalException {
		List<SkillVO> allSkills = skillRepo.getAllSkills();
		log.info("SkillsServiceImpl : getAllSkills() :: Filtering the skilling other than rejected");
		return allSkills.stream().filter(skill -> !skill.getStatus().equalsIgnoreCase(Constants.REJECTED)).toList();
	}

	@Override
	public List<SkillVO> getAllRequestedSkills() throws GlobalException {
		List<SkillVO> allSkills = skillRepo.getAllSkills();
		log.info("SkillsServiceImpl : getAllRequestedSkills() :: Filtering the skilling only Requested");
		return allSkills.stream().filter(skill -> skill.getStatus().equalsIgnoreCase(Constants.REQUESTED)).toList();
	}

	@Override
	public Boolean updateRequestedSkills(SkillVO skill) throws GlobalException {
		
		return skillRepo.updateSkillStatus(skill);
	}

}
