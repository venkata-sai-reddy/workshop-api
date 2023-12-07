package com.clarku.workshop.controller;

import java.util.List;

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
import com.clarku.workshop.service.IWorkshopService;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.vo.RequestVO;
import com.clarku.workshop.vo.SearchWorkshopVO;
import com.clarku.workshop.vo.SessionVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.UserVO;
import com.clarku.workshop.vo.WorkshopVO;
import com.clarku.workshop.vo.WorkshopVO.WorkshopAddValidation;
import com.clarku.workshop.vo.WorkshopVO.WorkshopUpdateValidation;
import com.clarku.workshop.vo.WorkshopsTimeLineVO;

@RestController
@RequestMapping("/workshop")
@CrossOrigin("http://localhost:3000")
public class WorkshopController {

	@Autowired
	IWorkshopService workshopService;
	
	@Autowired
	ISessionService sessionService;

	@Autowired
	IAuthService authService;

	@Autowired
	INotificationService notify;
	
	@Autowired
	IUserService userService;


	@PostMapping("/create")
	public ResponseEntity<WorkshopVO> createWorkshop(@RequestHeader HttpHeaders headers, @Validated(WorkshopAddValidation.class) @RequestBody WorkshopVO workshopDetails) throws GlobalException, EmailException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		if (!(Constants.ADMIN.equalsIgnoreCase(user.getUserType()) || Constants.INSTRUCTOR.equalsIgnoreCase(user.getUserType())) ) {
			throw new GlobalException("Don't have access to Create Workshop", HttpStatus.UNAUTHORIZED);
		}
		workshopDetails = workshopService.createWorkshop(workshopDetails, session.getUserId());
		notify.sendWorkshopCreateSuccessEmail(workshopDetails, user);
		return new ResponseEntity<>(workshopDetails, HttpStatus.OK);
	}

	@GetMapping("/view")
	public ResponseEntity<WorkshopVO> viewWorkshop(@RequestHeader HttpHeaders headers, @RequestParam Integer workshopId) throws GlobalException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		WorkshopVO workshopDetails = workshopService.getWorkshop(workshopId, user);
		return new ResponseEntity<>(workshopDetails, HttpStatus.OK);
	}

	@PostMapping("/enroll")
	public ResponseEntity<Boolean> enrollWorkshop(@RequestHeader HttpHeaders headers, @RequestBody WorkshopVO workshop) throws GlobalException, EmailException {
		SessionVO session = authService.retrieveSession(headers);
		Boolean isEnrolled = workshopService.enrollWorkshop(workshop.getWorkshopId(), session.getUserId());
		UserVO user = userService.getUser(session.getUserId());
		if (Boolean.FALSE.equals(isEnrolled)) {
			throw new GlobalException("Failed to Enroll", HttpStatus.BAD_REQUEST);
		}
		notify.sendEnrollSuccessEmail(workshop, user);
		return new ResponseEntity<>(isEnrolled, HttpStatus.OK);
	}
	
	@PostMapping("/unenroll")
	public ResponseEntity<Boolean> unEnrollWorkshop(@RequestHeader HttpHeaders headers, @RequestBody WorkshopVO workshop) throws GlobalException, EmailException {
		SessionVO session = authService.retrieveSession(headers);
		Boolean isUnEnrolled = workshopService.unEnrollWorkshop(workshop.getWorkshopId(), session.getUserId());
		UserVO user = userService.getUser(session.getUserId());
		if (Boolean.FALSE.equals(isUnEnrolled)) {
			throw new GlobalException("Failed to UnEnroll", HttpStatus.BAD_REQUEST);
		}
		notify.sendUnEnrollSuccessEmail(workshop, user);
		return new ResponseEntity<>(isUnEnrolled, HttpStatus.OK);
	}

	@PostMapping("/request")
	public ResponseEntity<Boolean> requestWorkshop(@RequestHeader HttpHeaders headers, @RequestBody List<SkillVO> skills) throws GlobalException, EmailException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		Boolean isRequestSuccess = workshopService.requestWorkshop(skills, user);
		if (Boolean.FALSE.equals(isRequestSuccess)) {
			throw new GlobalException("Failed to Request the Workshop", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(isRequestSuccess, HttpStatus.OK);
	}

	@PostMapping("/delete")
	public ResponseEntity<Boolean> deleteWorkshop(@RequestHeader HttpHeaders headers, @RequestBody WorkshopVO workshop) throws GlobalException, EmailException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		if (!(Constants.ADMIN.equalsIgnoreCase(user.getUserType()) || Constants.INSTRUCTOR.equalsIgnoreCase(user.getUserType())) ) {
			throw new GlobalException("Don't have access", HttpStatus.UNAUTHORIZED);
		}
		Boolean isDeleted = workshopService.deleteWorkshop(workshop.getWorkshopId(), user);
		if (Boolean.FALSE.equals(isDeleted)) {
			throw new GlobalException("Workshop Failed to Delete", HttpStatus.BAD_REQUEST);
		}
		notify.sendWorkshopDeletedSuccessEmail(workshop, user);
		List<String> registeredUsers = workshopService.getRegisteredWorkshopUsersEmail(workshop.getWorkshopId());
		notify.sendWorkshopCancelledEmail(workshop, registeredUsers);
		return new ResponseEntity<>(isDeleted, HttpStatus.OK);
	}

	@PostMapping("/search")
	public ResponseEntity<List<WorkshopVO>> searchWorkshop(@RequestHeader HttpHeaders headers, @RequestBody SearchWorkshopVO searchDetails) throws GlobalException, EmailException {
		SessionVO session = authService.retrieveSession(headers);
		List<WorkshopVO> workshops = workshopService.searchWorkshops(searchDetails, session.getUserId());
		return new ResponseEntity<>(workshops, HttpStatus.OK);
	}

	@GetMapping("/all")
	public ResponseEntity<WorkshopsTimeLineVO> getAllWorkshops(@RequestHeader HttpHeaders headers) throws GlobalException {
		SessionVO session = authService.retrieveSession(headers);
		WorkshopsTimeLineVO workshops = workshopService.getAllWorkshops(session.getUserId());
		return new ResponseEntity<>(workshops, HttpStatus.OK);
	}

	@GetMapping("/created")
	public ResponseEntity<WorkshopsTimeLineVO> getAllWorkshopsByUser(@RequestHeader HttpHeaders headers) throws GlobalException {
		SessionVO session = authService.retrieveSession(headers);
		UserVO user = userService.getUser(session.getUserId());
		if (!(Constants.ADMIN.equalsIgnoreCase(user.getUserType()) || Constants.INSTRUCTOR.equalsIgnoreCase(user.getUserType())) ) {
			throw new GlobalException("Don't have access", HttpStatus.UNAUTHORIZED);
		}
		WorkshopsTimeLineVO workshops = workshopService.getAllCreatedWorkshops(session.getUserId());
		return new ResponseEntity<>(workshops, HttpStatus.OK);
	}

	@GetMapping("/registered")
	public ResponseEntity<WorkshopsTimeLineVO> getAllRegisteredWorkshops(@RequestHeader HttpHeaders headers) throws GlobalException {
		SessionVO session = authService.retrieveSession(headers);
		WorkshopsTimeLineVO workshops = workshopService.getAllRegisteredWorkshops(session.getUserId());
		return new ResponseEntity<>(workshops, HttpStatus.OK);
	}

	@GetMapping("/requested")
	public ResponseEntity<List<RequestVO>> getAllRequestedWorkshops(@RequestHeader HttpHeaders headers) throws GlobalException {
		SessionVO session = authService.retrieveSession(headers);
		List<RequestVO> skills = workshopService.getAllReqestedSkills(session.getUserId());
		return new ResponseEntity<>(skills, HttpStatus.OK);
	}

	@PutMapping("/update")
	public ResponseEntity<Boolean> updateWorkshop(@RequestHeader HttpHeaders headers, @Validated(WorkshopUpdateValidation.class) @RequestBody WorkshopVO workshopDetails) throws GlobalException, EmailException {
		SessionVO session = authService.retrieveSession(headers);
		Integer userId = session.getUserId();
		UserVO user = userService.getUser(userId);
		if (!(Constants.ADMIN.equalsIgnoreCase(user.getUserType()) || Constants.INSTRUCTOR.equalsIgnoreCase(user.getUserType())) ) {
			throw new GlobalException("Don't have access to Update Workshop", HttpStatus.UNAUTHORIZED);
		}
		Boolean isUpdated = workshopService.updateWorkshop(workshopDetails, userId);
		if (Boolean.TRUE.equals(isUpdated)) {
			notify.sendWorkshopUpdateSuccessEmail(workshopDetails, user);
			List<String> registeredUsers = workshopService.getRegisteredWorkshopUsersEmail(workshopDetails.getWorkshopId());
			if (!registeredUsers.isEmpty()) {
				notify.sendUpdatedWorkshopDetailsEmail(workshopDetails, registeredUsers);
			}
		}
		return new ResponseEntity<>(true, HttpStatus.OK);
	}

}
