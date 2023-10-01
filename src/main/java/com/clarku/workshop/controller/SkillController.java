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
import com.clarku.workshop.service.ISessionService;
import com.clarku.workshop.service.ISkillsService;
import com.clarku.workshop.vo.SkillVO;

@RestController
@RequestMapping("/")
@CrossOrigin("http://localhost:3000")
public class SkillController {

	@Autowired
	ISkillsService skillsService;

	@Autowired
	ISessionService session;

	@GetMapping("skills")
	public ResponseEntity<List<SkillVO>> getAllSkills( ) throws GlobalException {
		return new ResponseEntity<>(skillsService.getAllSkills(), HttpStatus.OK);
	}

}
