package com.clarku.workshop.service;

import java.util.List;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.UserVO;

public interface IUserService {

	Boolean resetPassword(String emailId) throws GlobalException, EmailException;

	UserVO getUser(Integer userId) throws GlobalException;

	List<SkillVO> getUserSkills(Integer userId) throws GlobalException;

}
