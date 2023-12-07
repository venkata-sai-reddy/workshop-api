package com.clarku.workshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.service.IAdminService;
import com.clarku.workshop.service.IAuthService;
import com.clarku.workshop.service.ILoginService;
import com.clarku.workshop.service.INotificationService;
import com.clarku.workshop.service.ISessionService;
import com.clarku.workshop.service.ISkillsService;
import com.clarku.workshop.service.IUserService;
import com.clarku.workshop.service.IWorkshopService;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.vo.CustomMessageVO;
import com.clarku.workshop.vo.SessionVO;
import com.clarku.workshop.vo.SignUpVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.UserProfileVO;
import com.clarku.workshop.vo.UserVO;
import com.clarku.workshop.vo.VenueVO;
import com.clarku.workshop.vo.WorkshopVO;

@RunWith(SpringJUnit4ClassRunner.class)
public class AdminControllerTest {

	@InjectMocks
	AdminController adminController;

	@Mock
	IWorkshopService workshopService;

	@Mock
	IAdminService adminService;

	@Mock
	ILoginService loginService;

	@Mock
	ISkillsService skillService;

	@Mock
	ISessionService sessionService;

	@Mock
	IAuthService authService;

	@Mock
	INotificationService notify;

	@Mock
	IUserService userService;

	@Mock
	SessionVO sessionVO;

	@Mock
	WorkshopVO workshopVO;

	@Mock
	UserVO userVO;

	@Mock
	List<SkillVO> skills;
	
	@Before
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
		sessionVO = new SessionVO();
		sessionVO.setUserId(1);
		
		userVO = new UserVO();
		userVO.setUserId(1);
		
		workshopVO = new WorkshopVO();
		skills = new ArrayList<>();
	}
	
	@Test
	public void testAllVenues_Success() throws GlobalException {
		Mockito.when(adminService.getVenues()).thenReturn(new ArrayList<>());
		ResponseEntity<List<VenueVO>> actual = adminController.getVenues();
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test
	public void testAllUsers_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(userService.getUsers()).thenReturn(new ArrayList<>());
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<List<UserProfileVO>> actual = adminController.getAllUsers(headers);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testCreateWorkshop_UnAuthException() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		HttpHeaders headers = new HttpHeaders();
		adminController.getAllUsers(headers);
	}

	@Test(expected = GlobalException.class)
	public void testCreateWorkshop_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		adminController.getAllUsers(headers);
	}

	@Test
	public void testUserProf_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(userService.getUsersDetails(Mockito.anyInt())).thenReturn(new UserProfileVO());
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<UserProfileVO> actual = adminController.getUserProf(headers, 1);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testUserProf_UnAuthException() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		HttpHeaders headers = new HttpHeaders();
		adminController.getUserProf(headers, 1);
	}

	@Test(expected = GlobalException.class)
	public void testUserProf_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		adminController.getUserProf(headers, 1);
	}

	@Test
	public void testUpdateUser_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(userService.updateUser(Mockito.any())).thenReturn(true);
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<Boolean> actual = adminController.updateUser(headers, new UserProfileVO());
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testUpdateUser_UnAuthException() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		HttpHeaders headers = new HttpHeaders();
		adminController.updateUser(headers, new UserProfileVO());
	}

	@Test(expected = GlobalException.class)
	public void testUpdateUser_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		adminController.updateUser(headers, new UserProfileVO());
	}

	@Test
	public void testDeleteUser_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(userService.deleteUser(Mockito.anyInt())).thenReturn(true);
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<Boolean> actual = adminController.deleteUser(headers, 1);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testDeleteUser_UnAuthException() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		HttpHeaders headers = new HttpHeaders();
		adminController.deleteUser(headers, 1);
	}

	@Test(expected = GlobalException.class)
	public void testDeleteUser_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		adminController.deleteUser(headers, 1);
	}

	@Test
	public void testRequestedskills_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(skillService.getAllRequestedSkills()).thenReturn(new ArrayList<>());
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<List<SkillVO>> actual = adminController.getRequestedSkills(headers);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testRequestedskills_UnAuthException() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		HttpHeaders headers = new HttpHeaders();
		adminController.getRequestedSkills(headers);
	}

	@Test(expected = GlobalException.class)
	public void testRequestedskills_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		adminController.getRequestedSkills(headers);
	}

	@Test
	public void testAllSkills_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(skillService.getAllSystemSkills()).thenReturn(new ArrayList<>());
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<List<SkillVO>> actual = adminController.getAllSkills(headers);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testAllSkills_UnAuthException() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		HttpHeaders headers = new HttpHeaders();
		adminController.getAllSkills(headers);
	}

	@Test(expected = GlobalException.class)
	public void testAllSkills_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		adminController.getAllSkills(headers);
	}

	@Test
	public void testAddNewSkills_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(skillService.addNewSkills(Mockito.anyList())).thenReturn(true);
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<Boolean> actual = adminController.addNewSkills(headers, new ArrayList<>());
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testAddNewSkills_UnAuthException() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		HttpHeaders headers = new HttpHeaders();
		adminController.addNewSkills(headers, new ArrayList<>());
	}

	@Test(expected = GlobalException.class)
	public void testAddNewSkills_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		adminController.addNewSkills(headers, new ArrayList<>());
	}

	@Test
	public void testUpdateRequestedSkills_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(skillService.updateRequestedSkills(Mockito.any())).thenReturn(true);
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<Boolean> actual = adminController.updateRequestedSkills(headers, new SkillVO());
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testUpdateRequestedSkills_UnAuthException() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		HttpHeaders headers = new HttpHeaders();
		adminController.updateRequestedSkills(headers, new SkillVO());
	}

	@Test(expected = GlobalException.class)
	public void testUpdateRequestedSkills_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		adminController.updateRequestedSkills(headers, new SkillVO());
	}

	@Test
	public void testCreateUser_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(loginService.createUser(Mockito.any())).thenReturn(true);
		Mockito.when(userService.generateTempPassword(Mockito.any())).thenReturn("pas");
		HttpHeaders headers = new HttpHeaders();
		SignUpVO userDetails = new SignUpVO();
		userDetails.setUserId(1);
		ResponseEntity<Boolean> actual = adminController.createUser(headers, userDetails );
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testCreateUser_CreateFailed() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(loginService.createUser(Mockito.any())).thenReturn(false);
		Mockito.when(userService.generateTempPassword(Mockito.any())).thenReturn("pas");
		HttpHeaders headers = new HttpHeaders();
		SignUpVO userDetails = new SignUpVO();
		userDetails.setUserId(1);
		adminController.createUser(headers, userDetails );
	}

	@Test(expected = GlobalException.class)
	public void testCreateUser_PassFailed() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(loginService.createUser(Mockito.any())).thenReturn(true);
		Mockito.when(userService.generateTempPassword(Mockito.any())).thenReturn(null);
		HttpHeaders headers = new HttpHeaders();
		SignUpVO userDetails = new SignUpVO();
		userDetails.setUserId(1);
		adminController.createUser(headers, userDetails );
	}

	@Test(expected = GlobalException.class)
	public void testCreateUser_UnAuthException() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		HttpHeaders headers = new HttpHeaders();
		SignUpVO userDetails = new SignUpVO();
		adminController.createUser(headers, userDetails );
	}

	@Test(expected = GlobalException.class)
	public void testCreateUser_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		SignUpVO userDetails = new SignUpVO();
		adminController.createUser(headers, userDetails );
	}

	@Test
	public void testGenerateTempPass_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(userService.generateTempPassword(Mockito.any())).thenReturn("pas");
		HttpHeaders headers = new HttpHeaders();
		UserProfileVO userDetails = new UserProfileVO();
		userDetails.setUserId(1);
		userDetails.setEmailId("email");
		userDetails.setFirstName("name");
		ResponseEntity<Boolean> actual = adminController.generateTemporaryPassword(headers, userDetails );
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testGenerateTempPass_PassFailed() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(userService.generateTempPassword(Mockito.any())).thenReturn(null);
		HttpHeaders headers = new HttpHeaders();
		UserProfileVO userDetails = new UserProfileVO();
		userDetails.setUserId(1);
		adminController.generateTemporaryPassword(headers, userDetails );
	}

	@Test(expected = GlobalException.class)
	public void testGenerateTempPass_UnAuthException() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		HttpHeaders headers = new HttpHeaders();
		UserProfileVO userDetails = new UserProfileVO();
		adminController.generateTemporaryPassword(headers, userDetails );
	}

	@Test(expected = GlobalException.class)
	public void testGenerateTempPass_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		UserProfileVO userDetails = new UserProfileVO();
		adminController.generateTemporaryPassword(headers, userDetails );
	}

	@Test
	public void testNotifyUsers_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.INSTRUCTOR);
		userVO.setUserId(1);
		userVO.setEmailId("email");
		WorkshopVO workshop = new WorkshopVO();
		workshop.setCreatedUserId(1);
		workshop.setRegisteredUsers(new ArrayList<>());
		Mockito.when(workshopService.getWorkshop(Mockito.any(), Mockito.any())).thenReturn(workshop);
		HttpHeaders headers = new HttpHeaders();
		SignUpVO userDetails = new SignUpVO();
		userDetails.setUserId(1);
		CustomMessageVO messageVO = new CustomMessageVO();
		messageVO.setUserId("1");
		List<String> list = new ArrayList<>();
		messageVO.setSendTo(list);
		ResponseEntity<Boolean> actual = adminController.notifyUsers(headers, messageVO );
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testNotifyUsers_Failed() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.INSTRUCTOR);
		userVO.setUserId(1);
		userVO.setEmailId("email");
		WorkshopVO workshop = new WorkshopVO();
		workshop.setCreatedUserId(2);
		workshop.setRegisteredUsers(new ArrayList<>());
		Mockito.when(workshopService.getWorkshop(Mockito.any(), Mockito.any())).thenReturn(workshop);
		HttpHeaders headers = new HttpHeaders();
		SignUpVO userDetails = new SignUpVO();
		userDetails.setUserId(1);
		CustomMessageVO messageVO = new CustomMessageVO();
		messageVO.setUserId("1");
		adminController.notifyUsers(headers, messageVO );
	}

	@Test
	public void testNotifyUsers_SendToSuccess() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.INSTRUCTOR);
		userVO.setUserId(1);
		userVO.setEmailId("email");
		WorkshopVO workshop = new WorkshopVO();
		workshop.setCreatedUserId(1);
		workshop.setRegisteredUsers(new ArrayList<>());
		Mockito.when(workshopService.getWorkshop(Mockito.any(), Mockito.any())).thenReturn(workshop);
		HttpHeaders headers = new HttpHeaders();
		SignUpVO userDetails = new SignUpVO();
		userDetails.setUserId(1);
		CustomMessageVO messageVO = new CustomMessageVO();
		messageVO.setUserId("1");
		List<String> list = new ArrayList<>();
		list.add("email");
		messageVO.setSendTo(list);
		ResponseEntity<Boolean> actual = adminController.notifyUsers(headers, messageVO );
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test
	public void testNotifyUsers_AdminSuccess() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		userVO.setUserId(1);
		userVO.setEmailId("email");
		WorkshopVO workshop = new WorkshopVO();
		workshop.setCreatedUserId(1);
		workshop.setRegisteredUsers(new ArrayList<>());
		Mockito.when(workshopService.getWorkshop(Mockito.any(), Mockito.any())).thenReturn(workshop);
		HttpHeaders headers = new HttpHeaders();
		SignUpVO userDetails = new SignUpVO();
		userDetails.setUserId(1);
		CustomMessageVO messageVO = new CustomMessageVO();
		messageVO.setUserId("1");
		ResponseEntity<Boolean> actual = adminController.notifyUsers(headers, messageVO );
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testNotifyUsers_CreateFailed() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(loginService.createUser(Mockito.any())).thenReturn(false);
		Mockito.when(userService.generateTempPassword(Mockito.any())).thenReturn("pas");
		HttpHeaders headers = new HttpHeaders();
		SignUpVO userDetails = new SignUpVO();
		userDetails.setUserId(1);
		adminController.createUser(headers, userDetails );
	}

	@Test(expected = GlobalException.class)
	public void testNotifyUsers_PassFailed() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(loginService.createUser(Mockito.any())).thenReturn(true);
		Mockito.when(userService.generateTempPassword(Mockito.any())).thenReturn(null);
		HttpHeaders headers = new HttpHeaders();
		SignUpVO userDetails = new SignUpVO();
		userDetails.setUserId(1);
		adminController.createUser(headers, userDetails );
	}

	@Test(expected = GlobalException.class)
	public void testNotifyUsers_UnAuthException() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		HttpHeaders headers = new HttpHeaders();
		adminController.notifyUsers(headers, new CustomMessageVO() );
	}

	@Test(expected = GlobalException.class)
	public void testNotifyUsers_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		adminController.notifyUsers(headers, new CustomMessageVO() );
	}
	
}
