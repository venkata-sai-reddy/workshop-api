package com.clarku.workshop.service;

import static org.junit.Assert.assertTrue;

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
import com.clarku.workshop.utils.EmailHelper;

@RunWith(SpringRunner.class)
public class NotificationServiceTest {

	@InjectMocks
	NotificationServiceImpl notificationService;

	@Mock
	EmailHelper email;

	@Before
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testSignUpMail_Success() throws GlobalException, EmailException {
		notificationService.sendSuccessSignUpMail("user@username.com", "username");
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void testSignUpMail_EmailException() throws GlobalException, EmailException {
		Mockito.doThrow(EmailException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendSuccessSignUpMail("user@gmail.com", "firstName");
	}

	@Test(expected = GlobalException.class)
	public void testSignUpMail_GlobalException() throws GlobalException, EmailException {
		Mockito.doThrow(GlobalException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendSuccessSignUpMail("username@user.com", "firstname");
	}
	
	@Test
	public void testResetPassMail_Success() throws GlobalException, EmailException {
		notificationService.sendSuccessResetPassEmail("user@username.com", "username", "h22he@3bG");
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void testResetPassMail_EmailException() throws GlobalException, EmailException {
		Mockito.doThrow(EmailException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendSuccessResetPassEmail("user@gmail.com", "firstName", "Abcd!23");
	}

	@Test(expected = GlobalException.class)
	public void testResetPassMail_GlobalException() throws GlobalException, EmailException {
		Mockito.doThrow(GlobalException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendSuccessResetPassEmail("username@user.com", "firstname", "Des#2102");
	}

}
