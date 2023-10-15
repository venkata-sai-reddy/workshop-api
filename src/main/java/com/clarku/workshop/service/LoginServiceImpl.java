package com.clarku.workshop.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.exception.LoginException;
import com.clarku.workshop.repository.ILoginRepo;
import com.clarku.workshop.repository.ISkillsRepo;
import com.clarku.workshop.repository.IUserRepo;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.utils.Secure;
import com.clarku.workshop.vo.LoginVO;
import com.clarku.workshop.vo.SignUpVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.UserVO;

import io.micrometer.common.util.StringUtils;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class LoginServiceImpl implements ILoginService {

	@Autowired
	private ILoginRepo loginRepo;

	@Autowired
	private IUserRepo userRepo;
	
	@Autowired
	private ISkillsRepo skillsRepo;

	@Autowired
	Secure secure;

	@Override
	public UserVO signIn(LoginVO loginDetails) throws GlobalException, LoginException {
		LoginVO userLoginDetails = validateUser(loginDetails);
		return userRepo.retrieveUserDetails(userLoginDetails.getUserId());
	}

	private LoginVO validateUser(LoginVO loginDetails) throws GlobalException, LoginException {
		LoginVO userLoginDetails = loginRepo.retrieveUserLogin(loginDetails.getEmailId());
		String encryptPass = secure.getEncrypted(loginDetails.getPassword());
		if (userLoginDetails == null) {
			log.error("LoginServiceImpl :: validateUser(): User {} ", loginDetails.getEmailId());
			throw new LoginException(Constants.NOT_REGISTERED_EXP, HttpStatus.UNAUTHORIZED); 
		}
		if (Boolean.TRUE.equals(userLoginDetails.getIsLocked())) {
			log.error("LoginServiceImpl:: validateUser() : " + Constants.USER_LOCKED_EXP);
			throw new LoginException(Constants.USER_LOCKED_EXP, HttpStatus.UNAUTHORIZED);
		}
		String actualPass = userLoginDetails.getPassword();
		if (StringUtils.isNotBlank(userLoginDetails.getTempPassword())) {
			actualPass = userLoginDetails.getTempPassword();
		}
		if (!encryptPass.equals(actualPass)) {
			if (userLoginDetails.getFailedLoginAttempts().equals(Integer.valueOf(2))) {
				loginRepo.lockUserAccount(userLoginDetails.getUserId());
			} else {
				loginRepo.updateUnSuccessAttempt(userLoginDetails.getUserId());
			}
			log.error("LoginServiceImpl:: validateUser() : " + Constants.LOGIN_WRONG_PASS_EXP);
			throw new LoginException(Constants.LOGIN_WRONG_PASS_EXP, HttpStatus.UNAUTHORIZED);
		}
		return userLoginDetails;
	}

	@Override
	public void updateLastLogin(Integer userId) throws LoginException {
		loginRepo.updateLastLogin(userId);
	}

	@Override
	public Boolean signUpUser(SignUpVO userDetails) throws GlobalException {
		validateSignUpDetails(userDetails);
		userRepo.saveSignUpUser(userDetails);
		Boolean isSaved = loginRepo.saveLoginDetails(userDetails);
		Integer userId = loginRepo.getUserId(userDetails.getEmailId());
		if (!ObjectUtils.isEmpty(userDetails.getNewSkills())) {
			validateAndSaveNewSkills(userId, userDetails.getNewSkills());
		}
		if (!ObjectUtils.isEmpty(userDetails.getExistingSkills())) {
			addSkillsToUser(userId, userDetails.getExistingSkills());
		}
		return isSaved;
	}

	private void addSkillsToUser(Integer userId, List<SkillVO> existingSkills) throws GlobalException {
		userRepo.saveUserSkillsById(userId, existingSkills);
	}

	private void validateAndSaveNewSkills(Integer userId, List<SkillVO> newSkills) throws GlobalException {
		HashSet<String> skillNames = (HashSet<String>) skillsRepo.getAllSkills().stream().map(SkillVO::getSkillName)
				.collect(Collectors.toSet());
		List<SkillVO> saveSkills = new ArrayList<>();
		newSkills.forEach(skill -> {
			if (!skillNames.contains(skill.getSkillName().toUpperCase())) {
				saveSkills.add(skill);
			}
		});
		skillsRepo.saveNewSkills(saveSkills);
		userRepo.saveUserSkillsByName(userId, newSkills);
	}

	private void validateSignUpDetails(SignUpVO userDetails) throws GlobalException {
		if (Boolean.TRUE.equals(loginRepo.isUserExists(userDetails.getEmailId()))) {
			throw new GlobalException(Constants.USER_EXISTS_SIGNUP_EXP, HttpStatus.BAD_REQUEST);
		}
		if (!userDetails.getCreatePassword().equals(userDetails.getConfirmPassword())) {
			throw new GlobalException(Constants.CREATE_CONFIRM_PASS_MISSMATCH_EXP, HttpStatus.BAD_REQUEST);
		}
		userDetails.setConfirmPassword(secure.getEncrypted(userDetails.getConfirmPassword()));
	}

}
