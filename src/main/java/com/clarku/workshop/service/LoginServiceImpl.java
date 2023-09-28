package com.clarku.workshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.exception.LoginException;
import com.clarku.workshop.repository.ILoginRepo;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.utils.Secure;
import com.clarku.workshop.vo.LoginVO;
import com.clarku.workshop.vo.UserVO;

import io.micrometer.common.util.StringUtils;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class LoginServiceImpl implements ILoginService {

	@Autowired
	private ILoginRepo loginRepo;

	@Autowired
	Secure secure;

	@Override
	public UserVO signIn(LoginVO loginDetails) throws GlobalException, LoginException {
		LoginVO userLoginDetails = validateUser(loginDetails);
		return loginRepo.retrieveUserDetails(userLoginDetails.getUserId());
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

}
