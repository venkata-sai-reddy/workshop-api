package com.clarku.workshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.service.IAuthService;
import com.clarku.workshop.service.INotificationService;
import com.clarku.workshop.service.ISessionService;
import com.clarku.workshop.service.IUserService;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.vo.ChangePassVO;
import com.clarku.workshop.vo.ChangePassVO.ChangePassValidation;
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
	INotificationService notify;

	@Autowired
	ISessionService sessionService;

	@GetMapping("forget_password")
	public ResponseEntity<Boolean> forgetPassword(@RequestParam("emailId") String emailId) throws GlobalException, EmailException {
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

	@GetMapping("user/profile")
	public ResponseEntity<UserVO> getUserProfile(@RequestHeader HttpHeaders headers, @RequestParam Integer userId) throws GlobalException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		if (!user.getUserType().equals(Constants.ADMIN) && user.getUserId() != userId ) {
			throw new GlobalException("Unauthorised to access", HttpStatus.UNAUTHORIZED);
		}
		UserVO searchedUser = userService.getUser(userId);
		return new ResponseEntity<>(searchedUser, HttpStatus.OK);
	}

	@PostMapping("user/changepassword")
	public ResponseEntity<Boolean> changePassword(@RequestHeader HttpHeaders headers, @Validated(ChangePassValidation.class) @RequestBody ChangePassVO passVO) throws GlobalException, EmailException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		Boolean isChanged = userService.changePassword(sessionDetails.getUserId(), passVO);

		if (Boolean.TRUE.equals(isChanged)) {
			UserVO user = userService.getUser(sessionDetails.getUserId());
			notify.sendPasswordChangeEmail(user.getEmailId(), user.getFirstName());
		}		
		return new ResponseEntity<>(isChanged, HttpStatus.OK);
	}

	@PutMapping("user/update")
	public ResponseEntity<Boolean> updateProfile(@RequestHeader HttpHeaders headers, @RequestBody UserVO updatedUser) throws GlobalException, EmailException {
		SessionVO sessionDetails = authService.retrieveSession(headers);
		UserVO user = userService.getUser(sessionDetails.getUserId());
		Boolean isUpdated = userService.updateUser(user, updatedUser);

		if (Boolean.TRUE.equals(isUpdated)) {
			notify.sendProfileUpdateEmail(user.getEmailId(), updatedUser.getFirstName());
		}		
		return new ResponseEntity<>(isUpdated, HttpStatus.OK);
	}

}
