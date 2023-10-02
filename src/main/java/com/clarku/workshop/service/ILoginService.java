package com.clarku.workshop.service;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.exception.LoginException;
import com.clarku.workshop.vo.LoginVO;
import com.clarku.workshop.vo.SignUpVO;
import com.clarku.workshop.vo.UserVO;

public interface ILoginService {

	UserVO signIn(LoginVO loginDetails) throws GlobalException, LoginException;

	void updateLastLogin(Integer integer) throws LoginException;

	Boolean signUpUser(SignUpVO userDetails) throws GlobalException;

}
