package com.clarku.workshop.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.repository.ILoginRepo;
import com.clarku.workshop.repository.ISkillsRepo;
import com.clarku.workshop.repository.IUserRepo;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.utils.Secure;
import com.clarku.workshop.vo.ChangePassVO;
import com.clarku.workshop.vo.LoginVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.UserProfileVO;
import com.clarku.workshop.vo.UserVO;

import io.micrometer.common.util.StringUtils;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserRepo userRepo;

	@Autowired
	private ILoginRepo loginRepo;

	@Autowired
	private ISkillsRepo skillRepo;

	@Autowired
	Secure secure;

	@Autowired
	INotificationService notify;

	@Override
	public Boolean resetPassword(String emailId) throws GlobalException, EmailException {
		LoginVO userLoginDetails = loginRepo.retrieveUserLogin(emailId);
		if (userLoginDetails == null) {
			log.error("LoginServiceImpl:: forgetPassword(): User Not Exixts with emailId : {}", emailId);
			throw new GlobalException(Constants.USER_NOT_EXISTS_FORGET_PASS_EXP, HttpStatus.BAD_REQUEST);
		}
		if (Boolean.TRUE.equals(userLoginDetails.getIsLocked())) {
			log.error("LoginServiceImpl:: forgetPassword(): User Locked : {}", emailId);
			throw new GlobalException(Constants.USER_LOCKED_EXP, HttpStatus.BAD_REQUEST);
		}
		return generateAndSaveTemporaryPass(userLoginDetails);
	}

	private Boolean generateAndSaveTemporaryPass(LoginVO userLoginDetails) throws GlobalException, EmailException {
		String tempPass = secure.generateTempPass();
		String userFirstName = userRepo.getUserFirstName(userLoginDetails.getUserId());
		Boolean isSaved = loginRepo.saveTempPassword(userLoginDetails.getUserId(), secure.getEncrypted(tempPass));
		if (Boolean.TRUE.equals(isSaved)) {
			notify.sendSuccessResetPassEmail(userLoginDetails.getEmailId(), userFirstName, tempPass);
		}
		return isSaved;
	}

	@Override
	public UserVO getUser(Integer userId) throws GlobalException {
		return userRepo.retrieveUserDetails(userId);
	}

	@Override
	public List<SkillVO> getUserSkills(Integer userId) throws GlobalException {
		return userRepo.retrieveUserSkills(userId);
	}

	@Override
	public Boolean changePassword(Integer userId, ChangePassVO passVO) throws GlobalException {
		LoginVO loginDetails = userRepo.retrieveUserLoginDetails(userId);
		if (loginDetails.getTempPassword() != null && !StringUtils.isBlank(loginDetails.getTempPassword())) {
			if (!secure.getEncrypted(passVO.getCurrentPassword()).equals(loginDetails.getTempPassword())) {
				throw new GlobalException("Wrong Current Password", HttpStatus.BAD_REQUEST);
			}
		}else {
			if (!secure.getEncrypted(passVO.getCurrentPassword()).equals(loginDetails.getPassword())) {
				throw new GlobalException("Wrong Current Password", HttpStatus.BAD_REQUEST);
			}
		}
		if (passVO.getCurrentPassword().equals(passVO.getCreatePassword())) {
			throw new GlobalException("Existing and New password should not be same", HttpStatus.BAD_REQUEST);
		}
		if (!passVO.getCreatePassword().equals(passVO.getConfirmPassword())) {
			throw new GlobalException("Create and Confirm password should be same", HttpStatus.BAD_REQUEST);
		}
		return userRepo.updateUserPass(userId, secure.getEncrypted(passVO.getCreatePassword()));
	}

	@Override
	public Boolean updateUser(UserVO existingUser, UserVO updatedUser) throws GlobalException {
		HashMap<String, String> updatedDetails = new HashMap<>();
		if (!existingUser.getFirstName().equals(updatedUser.getFirstName())) {
			updatedDetails.put("firstName", updatedUser.getFirstName());
		}
		if (!existingUser.getLastName().equals(updatedUser.getLastName())) {
			updatedDetails.put("lastName", updatedUser.getLastName());
		}
		if (!existingUser.getPhoneNumber().equals(updatedUser.getPhoneNumber())) {
			updatedDetails.put("phoneNumber", updatedUser.getPhoneNumber());
		}
		if (!existingUser.getEmailId().equals(updatedUser.getEmailId())) {
			updatedDetails.put("emailId", updatedUser.getEmailId());
		}
		Boolean isUpdated = checkSkillsChange(existingUser, updatedUser);

		return isUpdated || userRepo.updateUserProf(existingUser.getUserId(), updatedDetails);
	}

	private Boolean checkSkillsChange(UserVO existingUser, UserVO updatedUser) throws GlobalException {
		List<SkillVO> userSkills = userRepo.retrieveUserSkills(existingUser.getUserId());
		List<SkillVO> addedSkills = new ArrayList<>();
		List<SkillVO> deletedSkills = new ArrayList<>();
		Map<Integer, String> userSkillsMap = userSkills.stream().collect(Collectors.toMap(SkillVO::getSkillId, skill -> skill.getSkillName().toLowerCase()));
		updatedUser.getSkills().stream().forEach(skill -> { 
			if (!userSkillsMap.containsKey(skill.getSkillId())) {
				addedSkills.add(skill);
			}
		});
		Map<Integer, String> updatedUserSkillsMap = updatedUser.getSkills().stream().collect(Collectors.toMap(SkillVO::getSkillId, skill -> skill.getSkillName().toLowerCase()));
		userSkills.stream().forEach(skill -> { 
			if (!updatedUserSkillsMap.containsKey(skill.getSkillId())) {
				deletedSkills.add(skill);
			}
		});
		
		userRepo.saveUserSkillsById(existingUser.getUserId(), addedSkills);
		userRepo.deleteUserSkillsById(existingUser.getUserId(), deletedSkills);
		if (updatedUser.getNewSkills() != null) {
			skillRepo.saveNewSkills(updatedUser.getNewSkills());
			userRepo.saveUserSkillsByName(existingUser.getUserId(), updatedUser.getNewSkills());
		}
		return !addedSkills.isEmpty() || !deletedSkills.isEmpty() || updatedUser.getNewSkills() != null;
	}

	private Boolean updateSkillsChange(UserProfileVO existingUser, UserProfileVO updatedUser) throws GlobalException {
		List<SkillVO> userSkills = userRepo.retrieveUserSkills(existingUser.getUserId());
		List<SkillVO> addedSkills = new ArrayList<>();
		List<SkillVO> deletedSkills = new ArrayList<>();
		Map<Integer, String> userSkillsMap = userSkills.stream().collect(Collectors.toMap(SkillVO::getSkillId, skill -> skill.getSkillName().toLowerCase()));
		updatedUser.getSkills().stream().forEach(skill -> { 
			if (!userSkillsMap.containsKey(skill.getSkillId())) {
				addedSkills.add(skill);
			}
		});
		Map<Integer, String> updatedUserSkillsMap = updatedUser.getSkills().stream().collect(Collectors.toMap(SkillVO::getSkillId, skill -> skill.getSkillName().toLowerCase()));
		userSkills.stream().forEach(skill -> { 
			if (!updatedUserSkillsMap.containsKey(skill.getSkillId())) {
				deletedSkills.add(skill);
			}
		});
		
		userRepo.saveUserSkillsById(existingUser.getUserId(), addedSkills);
		userRepo.deleteUserSkillsById(existingUser.getUserId(), deletedSkills);
		if (updatedUser.getNewSkills() != null) {
			skillRepo.saveNewSkillsByAdmin(updatedUser.getNewSkills());
			userRepo.saveUserSkillsByName(existingUser.getUserId(), updatedUser.getNewSkills());
		}
		return !addedSkills.isEmpty() || !deletedSkills.isEmpty() || updatedUser.getNewSkills() != null;
	}

	@Override
	public List<UserProfileVO> getUsers() throws GlobalException {
		return userRepo.getUsers();
	}

	@Override
	public String generateTempPassword(Integer userId) throws GlobalException {
		String tempPass = secure.generateTempPass();
		loginRepo.saveTempPassword(userId, secure.getEncrypted(tempPass));
		return tempPass;
	}

	@Override
	public UserProfileVO getUsersDetails(Integer userId) throws GlobalException {
		UserProfileVO userDetails = userRepo.getUserDetails(userId);
		if (userDetails == null) {
			throw new GlobalException("User Not Found", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		userDetails.setSkills(userRepo.retrieveUserSkills(userId));
		return userDetails;
	}

	@Override
	public Boolean deleteUser(Integer userId) throws GlobalException {
		return userRepo.deleteUser(userId);
	}

	@Override
	public Boolean updateUser(UserProfileVO updatedUser) throws GlobalException {
		UserProfileVO existingUser = userRepo.getUserDetails(updatedUser.getUserId());
		HashMap<String, String> updatedDetails = new HashMap<>();
		HashMap<String, String> loginUpdatedDetails = new HashMap<>();
		if (!existingUser.getFirstName().equals(updatedUser.getFirstName())) {
			updatedDetails.put("firstName", updatedUser.getFirstName());
		}
		if (!existingUser.getLastName().equals(updatedUser.getLastName())) {
			updatedDetails.put("lastName", updatedUser.getLastName());
		}
		if (!existingUser.getPhoneNumber().equals(updatedUser.getPhoneNumber())) {
			updatedDetails.put("phoneNumber", updatedUser.getPhoneNumber());
		}
		if (!existingUser.getIsActive().equals(updatedUser.getIsActive())) {
			loginUpdatedDetails.put("isActive", String.valueOf(updatedUser.getIsActive()));
		}
		if (!existingUser.getIsLocked().equals(updatedUser.getIsLocked())) {
			loginUpdatedDetails.put("isLocked", String.valueOf(updatedUser.getIsLocked()));
			loginUpdatedDetails.put("attempts", "0");
		}
		Boolean isSkillsUpdated = updateSkillsChange(existingUser, updatedUser);
		Boolean isUserProfUpdated = Boolean.FALSE;
		Boolean isLoginUpdated = Boolean.FALSE;
		if (!updatedDetails.isEmpty()) {
			isUserProfUpdated = userRepo.updateUserProf(existingUser.getUserId(), updatedDetails);
		}
		if (!loginUpdatedDetails.isEmpty()) {
			isLoginUpdated = userRepo.updateLoginDetails(existingUser.getUserId(), loginUpdatedDetails);
		}
		return isSkillsUpdated || isUserProfUpdated || isLoginUpdated;
	}

}
