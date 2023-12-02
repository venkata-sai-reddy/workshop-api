package com.clarku.workshop.repository;

import java.util.HashMap;
import java.util.List;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.vo.LoginVO;
import com.clarku.workshop.vo.SignUpVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.UserProfileVO;
import com.clarku.workshop.vo.UserVO;

public interface IUserRepo {

	UserVO retrieveUserDetails(Integer userId) throws GlobalException;

	String getUserFirstName(Integer userId) throws GlobalException;

	Boolean saveSignUpUser(SignUpVO userDetails) throws GlobalException;

	void saveUserSkillsByName(Integer userId, List<SkillVO> newSkills) throws GlobalException;

	void saveUserSkillsById(Integer userId, List<SkillVO> existingSkills) throws GlobalException;

	List<SkillVO> retrieveUserSkills(Integer userId) throws GlobalException;

	HashMap<Integer, List<UserVO>> getSkilledUsers(List<Integer> skillIds) throws GlobalException;

	LoginVO retrieveUserLoginDetails(Integer userId) throws GlobalException;

	Boolean updateUserPass(Integer userId, String createPassword) throws GlobalException;

	Boolean updateUserProf(Integer userId, HashMap<String, String> updatedDetails) throws GlobalException;

	void deleteUserSkillsById(Integer userId, List<SkillVO> deletedSkills) throws GlobalException;

	List<UserProfileVO> getUsers() throws GlobalException;

}
