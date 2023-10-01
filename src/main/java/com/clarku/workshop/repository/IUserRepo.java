package com.clarku.workshop.repository;

import java.util.List;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.vo.SignUpVO;
import com.clarku.workshop.vo.SkillVO;

public interface IUserRepo {

	String getUserFirstName(Integer userId) throws GlobalException;

	Boolean saveSignUpUser(SignUpVO userDetails) throws GlobalException;

	void saveUserSkillsByName(Integer userId, List<SkillVO> newSkills) throws GlobalException;

	void saveUserSkillsById(Integer userId, List<SkillVO> existingSkills) throws GlobalException;

}
