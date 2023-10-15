package com.clarku.workshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.exception.LoginException;
import com.clarku.workshop.repository.LoginRepositoryImpl;
import com.clarku.workshop.repository.UserRepositoryImpl;
import com.clarku.workshop.utils.Secure;
import com.clarku.workshop.vo.LoginVO;
import com.clarku.workshop.vo.UserVO;

@RunWith(SpringRunner.class)
public class LoginServiceTest {

	@InjectMocks
	LoginServiceImpl loginService;

	@Mock
	Secure secure;

	@Mock
	LoginRepositoryImpl loginRepo;

	@Mock
	UserRepositoryImpl userRepo;

	@Mock
	LoginVO loginDetails;

	@Mock
	UserVO userDetails;

	@Before
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
		loginDetails = new LoginVO();
		userDetails = new UserVO();
		loginDetails.setUserId(1);
		loginDetails.setEmailId("user1@user.com");
		loginDetails.setPassword("hghjegjdsbfsdfb");
		loginDetails.setIsLocked(false);
		loginDetails.setFailedLoginAttempts(0);
		userDetails.setEmailId("user1@user.com");
		userDetails.setUserId(1);
	}

	@Test
	public void testSignIn_Success() throws GlobalException, LoginException {
		Mockito.when(loginRepo.retrieveUserLogin(Mockito.anyString())).thenReturn(loginDetails);
		Mockito.when(secure.getEncrypted(Mockito.anyString())).thenReturn(loginDetails.getPassword());
		Mockito.when(userRepo.retrieveUserDetails(Mockito.anyInt())).thenReturn(userDetails);
		UserVO userVO = loginService.signIn(loginDetails);
		assertEquals(userVO.getEmailId(), userDetails.getEmailId());
	}

	@Test
	public void testSignIn_TempPassSuccess() throws GlobalException, LoginException {
		loginDetails = new LoginVO();
		loginDetails.setUserId(1);
		loginDetails.setEmailId("user1@user.com");
		loginDetails.setPassword("hghjegjdsbfsdfb");
		loginDetails.setTempPassword("tyvhgvjbjhvjxfd");
		loginDetails.setIsLocked(false);
		loginDetails.setFailedLoginAttempts(0);
		Mockito.when(loginRepo.retrieveUserLogin(Mockito.anyString())).thenReturn(loginDetails);
		Mockito.when(secure.getEncrypted(Mockito.anyString())).thenReturn(loginDetails.getTempPassword());
		Mockito.when(userRepo.retrieveUserDetails(Mockito.anyInt())).thenReturn(userDetails);
		UserVO userVO = loginService.signIn(loginDetails);
		assertEquals(userVO.getEmailId(), userDetails.getEmailId());
	}

	@Test(expected = LoginException.class)
	public void testSignIn_NoUserExists() throws GlobalException, LoginException {
		Mockito.when(loginRepo.retrieveUserLogin(Mockito.anyString())).thenReturn(null);
		loginService.signIn(loginDetails);
	}

	@Test(expected = LoginException.class)
	public void testSignIn_WrongPassException() throws GlobalException, LoginException {
		Mockito.when(loginRepo.retrieveUserLogin(Mockito.anyString())).thenReturn(loginDetails);
		Mockito.when(secure.getEncrypted(Mockito.anyString())).thenReturn("hbjbdjxbkjvnd");
		loginService.signIn(loginDetails);
	}

	@Test(expected = LoginException.class)
	public void testSignIn_WrongPassManyTimeException() throws GlobalException, LoginException {
		Mockito.when(loginRepo.retrieveUserLogin(Mockito.anyString())).thenReturn(loginDetails);
		loginDetails.setFailedLoginAttempts(2);
		Mockito.when(secure.getEncrypted(Mockito.anyString())).thenReturn("hbjbdjxbkjvnd");
		loginService.signIn(loginDetails);
	}

	@Test(expected = LoginException.class)
	public void testSignIn_LockedException() throws GlobalException, LoginException {
		Mockito.when(loginRepo.retrieveUserLogin(Mockito.anyString())).thenReturn(loginDetails);
		loginDetails.setIsLocked(true);
		Mockito.when(secure.getEncrypted(Mockito.anyString())).thenReturn(loginDetails.getPassword());
		loginService.signIn(loginDetails);
	}

	@Test
	public void testUpdateLastLogin_Success() throws GlobalException, LoginException {
		Mockito.doNothing().when(loginRepo).updateLastLogin(Mockito.anyInt());
		loginService.updateLastLogin(1);
		assertTrue(true);
	}

	@Test(expected = LoginException.class)
	public void testUpdateLastLogin_Exception() throws GlobalException, LoginException {
		Mockito.doThrow(LoginException.class).when(loginRepo).updateLastLogin(Mockito.anyInt());
		loginService.updateLastLogin(2);
	}

}
