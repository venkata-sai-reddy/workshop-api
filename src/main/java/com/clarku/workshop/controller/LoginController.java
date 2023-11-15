package com.clarku.workshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.exception.LoginException;
import com.clarku.workshop.service.ILoginService;
import com.clarku.workshop.service.INotificationService;
import com.clarku.workshop.service.ISessionService;
import com.clarku.workshop.service.IUserService;
import com.clarku.workshop.vo.LoginVO;
import com.clarku.workshop.vo.LoginVO.LoginValidation;
import com.clarku.workshop.vo.SessionVO;
import com.clarku.workshop.vo.SignUpVO;
import com.clarku.workshop.vo.SignUpVO.SignUpValidation;
import com.clarku.workshop.vo.UserVO;

@RestController
@RequestMapping("/")
@CrossOrigin("http://localhost:3000")
public class LoginController {

	@Autowired
	ILoginService loginService;

	@Autowired
	IUserService userService;

	@Autowired
	ISessionService session;

	@Autowired
	INotificationService notify;

	@PostMapping("login")
	public ResponseEntity<UserVO> signIn(@Validated(LoginValidation.class) @RequestBody LoginVO loginDetails) throws GlobalException, LoginException {
		UserVO userDetails = loginService.signIn(loginDetails);
		loginService.updateLastLogin(userDetails.getUserId());
		SessionVO userSession = session.createSession(userDetails.getUserId());
		userDetails.setSkills(userService.getUserSkills(userDetails.getUserId()));
		userDetails.setSession(userSession);
		return new ResponseEntity<>(userDetails, HttpStatus.OK);
	}

	@PostMapping("signup")
	public ResponseEntity<Boolean> signUp(@Validated(SignUpValidation.class) @RequestBody SignUpVO userDetails) throws GlobalException, EmailException {
		Boolean isSignUpSuccess = loginService.signUpUser(userDetails);
		if (Boolean.TRUE.equals(isSignUpSuccess)) {
			notify.sendSuccessSignUpMail(userDetails);
		}
		return new ResponseEntity<>(isSignUpSuccess, HttpStatus.OK);
	}

	@PostMapping("logout")
	public ResponseEntity<Boolean> logout(@RequestBody Integer sessionId) throws GlobalException {
		session.endSession(sessionId);
		return new ResponseEntity<>(true, HttpStatus.OK);
	}

}
