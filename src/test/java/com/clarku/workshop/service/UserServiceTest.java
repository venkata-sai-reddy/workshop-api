package com.clarku.workshop.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.repository.LoginRepositoryImpl;
import com.clarku.workshop.repository.UserRepositoryImpl;
import com.clarku.workshop.utils.EmailHelper;
import com.clarku.workshop.utils.Secure;
import com.clarku.workshop.vo.LoginVO;

@RunWith(SpringRunner.class)
public class UserServiceTest {

	@InjectMocks
	UserServiceImpl userService;

	@Mock
	NotificationServiceImpl notification;
	
	@Mock
	EmailHelper email;

	@Mock
	Secure secure;

	@Mock
	LoginRepositoryImpl loginRepo;

	@Mock
	UserRepositoryImpl userRepo;

	@Mock
	LoginVO loginDetails;

	@Before
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
		loginDetails = new LoginVO();
	}

	@Test
	public void testResetPassword_Success() throws GlobalException, EmailException {
		loginDetails.setIsLocked(false);
		loginDetails.setUserId(1);
		loginDetails.setEmailId("email@gmail.com");
		Mockito.when(loginRepo.retrieveUserLogin(Mockito.anyString())).thenReturn(loginDetails);
		Mockito.when(secure.getEncrypted(Mockito.anyString())).thenReturn("hdjubgurbvkdjnvidjf");
		Mockito.when(loginRepo.saveTempPassword(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		Mockito.when(userRepo.getUserFirstName(Mockito.anyInt())).thenReturn("user");
		assertTrue(userService.resetPassword("email@gmail.com"));
	}

	@Test(expected = GlobalException.class)
	public void testResetPassword_NoUserExists() throws GlobalException, EmailException {
		Mockito.when(loginRepo.retrieveUserLogin(Mockito.anyString())).thenReturn(null);
		userService.resetPassword("email@gmail.com");
	}

	@Test(expected = GlobalException.class)
	public void testResetPassword_UserLocked() throws GlobalException, EmailException {
		loginDetails.setIsLocked(true);
		Mockito.when(loginRepo.retrieveUserLogin(Mockito.anyString())).thenReturn(loginDetails);
		userService.resetPassword("email@gmail.com");
	}

	@Test
	public void testResetPassword_Notify() throws GlobalException, EmailException {
		loginDetails.setIsLocked(false);
		loginDetails.setUserId(1);
		loginDetails.setEmailId("email@gmail.com");
		Mockito.when(loginRepo.retrieveUserLogin(Mockito.anyString())).thenReturn(loginDetails);
		Mockito.when(secure.getEncrypted(Mockito.anyString())).thenReturn("hdjubgurbvkdjnvidjf");
		Mockito.when(loginRepo.saveTempPassword(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		Mockito.when(userRepo.getUserFirstName(Mockito.anyInt())).thenReturn("user");
		assertTrue(userService.resetPassword("email@gmail.com"));
	}

	@Test
	public void testResetPassword_Failed() throws GlobalException, EmailException {
		loginDetails.setIsLocked(false);
		loginDetails.setUserId(1);
		loginDetails.setEmailId("email@gmail.com");
		Mockito.when(loginRepo.retrieveUserLogin(Mockito.anyString())).thenReturn(loginDetails);
		Mockito.when(secure.getEncrypted(Mockito.anyString())).thenReturn("hdjubgurbvkdjnvidjf");
		Mockito.when(loginRepo.saveTempPassword(Mockito.anyInt(), Mockito.any())).thenReturn(false);
		Mockito.when(userRepo.getUserFirstName(Mockito.anyInt())).thenReturn("user");
		assertFalse(userService.resetPassword("email@gmail.com"));
	}

}
