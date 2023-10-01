package com.clarku.workshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.service.ISessionService;
import com.clarku.workshop.service.IWorkshopService;

@RestController
@RequestMapping("/workshop")
@CrossOrigin("http://localhost:3000")
public class WorkshopController {

	@Autowired
	IWorkshopService workshopService;

	@Autowired
	ISessionService session;

	@PostMapping("/")
	public ResponseEntity<Boolean> forgetPassword() throws GlobalException, EmailException {
		return new ResponseEntity<>(true, HttpStatus.OK);
	}

}
