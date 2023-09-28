package com.clarku.workshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.clarku.workshop.service.UserServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserControllerTest {

	@InjectMocks
	UserController userController;

	@Mock
	UserServiceImpl userService;

	@Before
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
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

}
