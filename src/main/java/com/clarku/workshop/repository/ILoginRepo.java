package com.clarku.workshop.repository;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.exception.LoginException;
import com.clarku.workshop.vo.LoginVO;
import com.clarku.workshop.vo.UserVO;

public interface ILoginRepo {

	UserVO retrieveUserDetails(Integer userId) throws LoginException;

	LoginVO retrieveUserLogin(String emailId) throws GlobalException;

	Boolean isUserExists(String emailId) throws GlobalException;

	Integer getUserId(String emailId) throws GlobalException;

	void updateLastLogin(Integer userId) throws LoginException;

	void lockUserAccount(Integer userId) throws LoginException;

	void updateUnSuccessAttempt(Integer userId) throws LoginException;

	Boolean saveTempPassword(Integer userId, String tempPass) throws GlobalException;

}
