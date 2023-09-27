package com.clarku.workshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.exception.LoginException;
import com.clarku.workshop.service.LoginServiceImpl;
import com.clarku.workshop.service.SessionServiceImpl;
import com.clarku.workshop.vo.LoginVO;
import com.clarku.workshop.vo.SessionVO;
import com.clarku.workshop.vo.UserVO;

@RunWith(SpringJUnit4ClassRunner.class)
public class LoginControllerTest {

	@InjectMocks
	LoginController loginController;

	@Mock
	LoginServiceImpl loginService;

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
		ResponseEntity<UserVO> actual = loginController.signIn(loginDetails);

		assertEquals(HttpStatus.OK, actual.getStatusCode());
		assertEquals(1, actual.getBody().getSession().getSessionId());
	}

	@Test(expected = LoginException.class)
	public void signInTest_UnAuthorised() throws GlobalException, LoginException {
		Mockito.when(loginService.signIn(Mockito.any())).thenThrow(new LoginException("", HttpStatus.UNAUTHORIZED));
		loginController.signIn(loginDetails);
	}

}
