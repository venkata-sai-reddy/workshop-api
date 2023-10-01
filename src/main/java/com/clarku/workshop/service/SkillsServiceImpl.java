package com.clarku.workshop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.repository.ISkillsRepo;
import com.clarku.workshop.vo.SkillVO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SkillsServiceImpl implements ISkillsService {

	@Autowired
	private ISkillsRepo skillRepo;

	@Override
	public List<SkillVO> getAllSkills() throws GlobalException {
		return skillRepo.getAllSkills();
	}

}
