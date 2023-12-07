package com.clarku.workshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

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
import com.clarku.workshop.service.IAuthService;
import com.clarku.workshop.service.INotificationService;
import com.clarku.workshop.service.ISessionService;
import com.clarku.workshop.service.UserServiceImpl;
import com.clarku.workshop.vo.ChangePassVO;
import com.clarku.workshop.vo.SessionVO;
import com.clarku.workshop.vo.UserVO;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserControllerTest {

	@InjectMocks
	UserController userController;

	@Mock
	UserServiceImpl userService;

	@Mock
	IAuthService authService;

	@Mock
	INotificationService notify;

	@Mock
	ISessionService sessionService;

	@Mock
	SessionVO sessionVO;

	@Mock
	UserVO userVO;

	@Mock
	ChangePassVO passVO;

	@Before
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
		sessionVO = new SessionVO();
		sessionVO.setUserId(1);
		
		userVO = new UserVO();
		userVO.setUserId(1);
		
		passVO = new ChangePassVO();
	}

	@Test
	public void testForgetPassword_Success() throws GlobalException, EmailException {
		Mockito.when(userService.resetPassword(Mockito.any())).thenReturn(true);
		ResponseEntity<Boolean> actual = userController.forgetPassword("user@email.com");
		assertEquals(HttpStatus.OK, actual.getStatusCode());
		assertTrue(actual.getBody());
	}

	@Test(expected = GlobalException.class)
	public void testForgetPassword_Exception() throws GlobalException, EmailException {
		Mockito.when(userService.resetPassword(Mockito.any()))
				.thenThrow(new GlobalException("", HttpStatus.BAD_REQUEST));
		userController.forgetPassword("user@email.com");
	}

	@Test
	public void testAuthenticateUser_Success() throws GlobalException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		Mockito.when(userService.getUserSkills(Mockito.anyInt())).thenReturn(new ArrayList<>());
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<UserVO> actual = userController.getUser(headers);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testAuthenticateUser_Exception() throws GlobalException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		userController.getUser(headers);
	}

	@Test
	public void testUserProfile_Success() throws GlobalException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		Mockito.when(userService.getUserSkills(Mockito.anyInt())).thenReturn(new ArrayList<>());
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<UserVO> actual = userController.getUserProfile(headers, 1);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testUserProfile_InValidException() throws GlobalException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		userController.getUserProfile(headers, 1);
	}

	@Test(expected = GlobalException.class)
	public void testUserProfile_UnAuthException() throws GlobalException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		HttpHeaders headers = new HttpHeaders();
		userController.getUserProfile(headers, 2);
	}

	@Test
	public void testChangePass_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.changePassword(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setFirstName("name");
		userVO.setEmailId("email@email.com");
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<Boolean> actual = userController.changePassword(headers, passVO);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testChangePass_InValidException() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		userController.changePassword(headers, passVO);
	}

	@Test
	public void testChangePass_Fail() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.changePassword(Mockito.anyInt(), Mockito.any())).thenReturn(false);
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<Boolean> actual = userController.changePassword(headers, passVO);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test
	public void testUpdateProf_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.updateUser(Mockito.any(), Mockito.any())).thenReturn(true);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		UserVO user = new UserVO();
		user.setFirstName("name");
		userVO.setEmailId("email@email.com");
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<Boolean> actual = userController.updateProfile(headers, user);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testUpdateProf_InValidException() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		UserVO user = new UserVO();
		HttpHeaders headers = new HttpHeaders();
		userController.updateProfile(headers, user);
	}

	@Test
	public void testUpdateProf_Fail() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.updateUser(Mockito.any(), Mockito.any())).thenReturn(false);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		UserVO user = new UserVO();
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<Boolean> actual = userController.updateProfile(headers, user);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}
}
