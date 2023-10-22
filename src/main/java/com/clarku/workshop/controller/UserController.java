package com.clarku.workshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.service.IAuthService;
import com.clarku.workshop.service.ISessionService;
import com.clarku.workshop.service.IUserService;
import com.clarku.workshop.vo.SessionVO;
import com.clarku.workshop.vo.UserVO;

@RestController
@RequestMapping("/")
@CrossOrigin("http://localhost:3000")
public class UserController {

	@Autowired
	IUserService userService;

	@Autowired
	IAuthService authService;

	@Autowired
	ISessionService sessionService;

	@PostMapping("forget_password")
	public ResponseEntity<Boolean> forgetPassword(@RequestBody String emailId) throws GlobalException, EmailException {
		return new ResponseEntity<>(userService.resetPassword(emailId), HttpStatus.OK);
	}

	@GetMapping("authuser")
	public ResponseEntity<UserVO> getUser(@RequestHeader HttpHeaders headers) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		user.setSession(sessionDetails);
		user.setSkills(userService.getUserSkills(user.getUserId()));
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

}
