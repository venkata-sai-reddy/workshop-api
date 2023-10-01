package com.clarku.workshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.repository.ILoginRepo;
import com.clarku.workshop.repository.IUserRepo;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.utils.Secure;
import com.clarku.workshop.vo.LoginVO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserRepo userRepo;

	@Autowired
	private ILoginRepo loginRepo;

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

}
