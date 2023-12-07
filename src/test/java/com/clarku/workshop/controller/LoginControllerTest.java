package com.clarku.workshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.exception.LoginException;
import com.clarku.workshop.service.INotificationService;
import com.clarku.workshop.service.IUserService;
import com.clarku.workshop.service.LoginServiceImpl;
import com.clarku.workshop.service.SessionServiceImpl;
import com.clarku.workshop.vo.LoginVO;
import com.clarku.workshop.vo.SessionVO;
import com.clarku.workshop.vo.SignUpVO;
import com.clarku.workshop.vo.UserVO;

@RunWith(SpringJUnit4ClassRunner.class)
public class LoginControllerTest {

	@InjectMocks
	LoginController loginController;

	@Mock
	LoginServiceImpl loginService;

	@Mock
	IUserService userService;

	@Mock
	INotificationService notifyservice;

	@Mock
	SessionServiceImpl sessionService;

	@Mock
	LoginVO loginDetails;

	@Mock
	UserVO userDetails;

	@Mock
	SessionVO sessionVO;

	@Before
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
		loginDetails = new LoginVO();
		userDetails = new UserVO();
		sessionVO = new SessionVO();
		loginDetails.setEmailId("user1@user.com");
		loginDetails.setPassword("hghjegjdsbfsdfb");
		sessionVO.setSessionId(1);
		userDetails.setEmailId("user1@user.com");
		userDetails.setUserId(1);
	}

	@Test
	public void signInTest_Success() throws GlobalException, LoginException {
		Mockito.when(loginService.signIn(Mockito.any())).thenReturn(userDetails);
		Mockito.doNothing().when(loginService).updateLastLogin(Mockito.anyInt());
		Mockito.when(sessionService.createSession(Mockito.anyInt())).thenReturn(sessionVO);
		Mockito.when(userService.getUserSkills(Mockito.anyInt())).thenReturn(new ArrayList<>());
		ResponseEntity<UserVO> actual = loginController.signIn(loginDetails);
		
		assertEquals(HttpStatus.OK, actual.getStatusCode());
		assertEquals(1, actual.getBody().getSession().getSessionId());
	}

	@Test(expected = LoginException.class)
	public void signInTest_UnAuthorised() throws GlobalException, LoginException {
		Mockito.when(loginService.signIn(Mockito.any())).thenThrow(new LoginException("", HttpStatus.UNAUTHORIZED));
		loginController.signIn(loginDetails);
	}
	
	@Test
	public void signUpTest_Success() throws GlobalException, LoginException, EmailException {
		Mockito.when(loginService.signUpUser(Mockito.any())).thenReturn(true);
		SignUpVO userDetails = new SignUpVO();
		ResponseEntity<Boolean> actual = loginController.signUp(userDetails);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}
	
	@Test
	public void signUpTest_Fail() throws GlobalException, LoginException, EmailException {
		Mockito.when(loginService.signUpUser(Mockito.any())).thenReturn(false);
		SignUpVO userDetails = new SignUpVO();
		ResponseEntity<Boolean> actual = loginController.signUp(userDetails);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test
	public void logoutTest_Success() throws GlobalException, LoginException, EmailException {
		Mockito.doNothing().when(sessionService).endSession(Mockito.anyInt());
		ResponseEntity<Boolean> actual = loginController.logout(1);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}
}
