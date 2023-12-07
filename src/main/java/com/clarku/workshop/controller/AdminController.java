package com.clarku.workshop.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.clarku.workshop.service.IAdminService;
import com.clarku.workshop.service.IAuthService;
import com.clarku.workshop.service.ILoginService;
import com.clarku.workshop.service.INotificationService;
import com.clarku.workshop.service.ISkillsService;
import com.clarku.workshop.service.IUserService;
import com.clarku.workshop.service.IWorkshopService;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.vo.CustomMessageVO;
import com.clarku.workshop.vo.RegisteredUserVO;
import com.clarku.workshop.vo.SessionVO;
import com.clarku.workshop.vo.SignUpVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.UserProfileVO;
import com.clarku.workshop.vo.UserVO;
import com.clarku.workshop.vo.VenueVO;
import com.clarku.workshop.vo.WorkshopVO;

@RestController
@RequestMapping("/admin")
@CrossOrigin("http://localhost:3000")
public class AdminController {

	@Autowired
	IAdminService adminService;

	@Autowired
	IUserService userService;

	@Autowired
	IAuthService authService;

	@Autowired
	ILoginService loginService;

	@Autowired
	IWorkshopService workshopService;

	@Autowired
	ISkillsService skillService;

	@Autowired
	INotificationService notify;

	@GetMapping("/venues")
	public ResponseEntity<List<VenueVO>> getVenues() throws GlobalException {
		return new ResponseEntity<>(adminService.getVenues(), HttpStatus.OK);
	}
	
	@GetMapping("/users")
	public ResponseEntity<List<UserProfileVO>> getAllUsers(@RequestHeader HttpHeaders headers) throws GlobalException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		if (!Constants.ADMIN.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Don't have access to Page", HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
	}
	
	@GetMapping("/user")
	public ResponseEntity<UserProfileVO> getUserProf(@RequestHeader HttpHeaders headers, @RequestParam Integer userId) throws GlobalException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		if (!Constants.ADMIN.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Don't have access to Page", HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(userService.getUsersDetails(userId), HttpStatus.OK);
	}

	@PutMapping("/user/update")
	public ResponseEntity<Boolean> updateUser(@RequestHeader HttpHeaders headers, @RequestBody UserProfileVO userDetails) throws GlobalException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		if (!Constants.ADMIN.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Don't have access to Page", HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(userService.updateUser(userDetails), HttpStatus.OK);
	}

	@DeleteMapping("/user/delete")
	public ResponseEntity<Boolean> deleteUser(@RequestHeader HttpHeaders headers, @RequestParam Integer userId) throws GlobalException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		if (!Constants.ADMIN.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Don't have access to Page", HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(userService.deleteUser(userId), HttpStatus.OK);
	}

	@GetMapping("/skills/requested")
	public ResponseEntity<List<SkillVO>> getRequestedSkills(@RequestHeader HttpHeaders headers) throws GlobalException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		if (!Constants.ADMIN.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Don't have access to Page", HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(skillService.getAllRequestedSkills(), HttpStatus.OK);
	}

	@GetMapping("/skills/all")
	public ResponseEntity<List<SkillVO>> getAllSkills(@RequestHeader HttpHeaders headers) throws GlobalException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		if (!Constants.ADMIN.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Don't have access to Page", HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(skillService.getAllSystemSkills(), HttpStatus.OK);
	}

	@PostMapping("/skills/add")
	public ResponseEntity<Boolean> addNewSkills(@RequestHeader HttpHeaders headers, @RequestBody List<SkillVO> skillDetails) throws GlobalException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		if (!Constants.ADMIN.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Don't have access to Page", HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(skillService.addNewSkills(skillDetails), HttpStatus.OK);
	}

	@PutMapping("/skills/update")
	public ResponseEntity<Boolean> updateRequestedSkills(@RequestHeader HttpHeaders headers, @RequestBody SkillVO skill) throws GlobalException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		if (!Constants.ADMIN.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Don't have access to Page", HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(skillService.updateRequestedSkills(skill), HttpStatus.OK);
	}

	@PostMapping("/createuser")
	public ResponseEntity<Boolean> createUser(@RequestHeader HttpHeaders headers, @RequestBody SignUpVO createdUser) throws GlobalException, EmailException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		if (!Constants.ADMIN.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Don't have access to Page", HttpStatus.UNAUTHORIZED);
		}
		Boolean isUserCreated = loginService.createUser(createdUser);
		if (Boolean.FALSE.equals(isUserCreated)) {
			throw new GlobalException("Failed to Create User", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		notify.sendSuccessCreateUserMail(createdUser);
		String generatedCredentials = userService.generateTempPassword(createdUser.getUserId());
		if (generatedCredentials == null) {
			throw new GlobalException("Failed to generate temporary credentials", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		createdUser.setConfirmPassword(generatedCredentials);
		createdUser.setCreatePassword(generatedCredentials);
		notify.sendSuccessTemporaryPassword(createdUser);
		return new ResponseEntity<>(isUserCreated, HttpStatus.OK);
	}

	@PostMapping("/temppassword")
	public ResponseEntity<Boolean> generateTemporaryPassword(@RequestHeader HttpHeaders headers, @RequestBody UserProfileVO createdUser) throws GlobalException, EmailException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		if (!Constants.ADMIN.equalsIgnoreCase(user.getUserType())) {
			throw new GlobalException("Don't have access to Peform Action", HttpStatus.UNAUTHORIZED);
		}
		String generatedCredentials = userService.generateTempPassword(createdUser.getUserId());
		if (generatedCredentials == null) {
			throw new GlobalException("Failed to generate temporary credentials", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		notify.sendSuccessResetPassEmail(createdUser.getEmailId(), createdUser.getFirstName(), generatedCredentials);
		return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
	}

	@PostMapping("/workshop/notify")
	public ResponseEntity<Boolean> notifyUsers(@RequestHeader HttpHeaders headers, @RequestBody CustomMessageVO messageVO) throws GlobalException, EmailException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		WorkshopVO workshop;
		if (!(Constants.ADMIN.equalsIgnoreCase(user.getUserType()) || Constants.INSTRUCTOR.equalsIgnoreCase(user.getUserType())) ) {
			throw new GlobalException("Don't have access to perform the action", HttpStatus.UNAUTHORIZED);
		} else {
			workshop = workshopService.getWorkshop(messageVO.getWorkshopId(), user);
			if (!workshop.getCreatedUserId().equals(user.getUserId())) {
				throw new GlobalException("Don't have access to perform the action", HttpStatus.UNAUTHORIZED);
			}
		}
		if (messageVO.getSendTo() == null || messageVO.getSendTo().isEmpty()) {
			List<String> registeredUsers = workshop.getRegisteredUsers().stream().map(RegisteredUserVO :: getEmailId).collect(Collectors.toList());
			registeredUsers.add(user.getEmailId());
			messageVO.setSendTo(registeredUsers);
		}
		notify.sendCustomMessageToUsers(user, messageVO);
		return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
	}
}
