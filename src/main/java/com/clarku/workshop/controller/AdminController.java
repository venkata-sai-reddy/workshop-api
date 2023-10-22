package com.clarku.workshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.service.IAdminService;
import com.clarku.workshop.service.ISessionService;
import com.clarku.workshop.vo.VenueVO;

@RestController
@RequestMapping("/admin")
@CrossOrigin("http://localhost:3000")
public class AdminController {

	@Autowired
	IAdminService adminService;

	@Autowired
	ISessionService session;

	@GetMapping("/venues")
	public ResponseEntity<List<VenueVO>> getVenues() throws GlobalException {
		
		return new ResponseEntity<>(adminService.getVenues(), HttpStatus.OK);
	}

}
