package com.clarku.workshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.service.IAdminService;
import com.clarku.workshop.service.ISessionService;

@RestController
@RequestMapping("/admin")
@CrossOrigin("http://localhost:3000")
public class AdminController {

	@Autowired
	IAdminService adminService;

	@Autowired
	ISessionService session;

	@GetMapping("/users")
	public ResponseEntity<Boolean> getAllUsers(@RequestParam String sessionId) throws GlobalException, EmailException {
		return new ResponseEntity<>(true, HttpStatus.OK);
	}

}
