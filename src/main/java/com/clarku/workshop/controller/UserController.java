package com.clarku.workshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.service.ISessionService;
import com.clarku.workshop.service.IUserService;

@RestController
@RequestMapping("/")
@CrossOrigin("http://localhost:3000")
public class UserController {

	@Autowired
	IUserService userService;

	@Autowired
	ISessionService session;

	@PostMapping("forget_password")
	public ResponseEntity<Boolean> forgetPassword(@RequestParam String emailId) throws GlobalException, EmailException {
		return new ResponseEntity<>(userService.resetPassword(emailId), HttpStatus.OK);
	}

}
