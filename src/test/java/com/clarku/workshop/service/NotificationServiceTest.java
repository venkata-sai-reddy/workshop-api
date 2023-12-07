package com.clarku.workshop.service;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.utils.EmailHelper;
import com.clarku.workshop.vo.CustomMessageVO;
import com.clarku.workshop.vo.RegisteredUserVO;
import com.clarku.workshop.vo.SignUpVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.UserVO;
import com.clarku.workshop.vo.WorkshopVO;

@RunWith(SpringRunner.class)
public class NotificationServiceTest {

	@InjectMocks
	NotificationServiceImpl notificationService;

	@Mock
	EmailHelper email;

	@Mock
	SignUpVO signUpDetails;

	@Mock
	CustomMessageVO messageVO;

	@Mock
	RegisteredUserVO registeredUser;

	@Mock
	SkillVO skillVO;

	@Mock
	UserVO userVO;

	@Mock
	WorkshopVO workshopVO;

	@Before
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
		signUpDetails = new SignUpVO();
		signUpDetails.setEmailId("user@gmail.com");
		signUpDetails.setFirstName("firstName");
		signUpDetails.setUserType("Student");
		
		userVO = new UserVO();
		userVO.setFirstName("firstName");
		userVO.setEmailId("email");
		
		workshopVO = new WorkshopVO();
		workshopVO.setWorkshopName("name");
		
		messageVO = new CustomMessageVO();
		messageVO.setSendTo(new ArrayList<>());
		messageVO.setSubject("subject");
		messageVO.setMessage("message");
	}

	@Test
	public void testSignUpMail_Success() throws GlobalException, EmailException {
		notificationService.sendSuccessSignUpMail(signUpDetails);
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void testSignUpMail_EmailException() throws GlobalException, EmailException {
		signUpDetails.setUserType("Instructor");
		Mockito.doThrow(EmailException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendSuccessSignUpMail(signUpDetails);
	}

	@Test(expected = GlobalException.class)
	public void testSignUpMail_GlobalException() throws GlobalException, EmailException {
		Mockito.doThrow(GlobalException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendSuccessSignUpMail(signUpDetails);
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

	@Test
	public void testSendWorkshopCreateSuccessEmail_Success() throws GlobalException, EmailException {
		notificationService.sendWorkshopCreateSuccessEmail(workshopVO, userVO);
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void testSendWorkshopCreateSuccessEmail_EmailException() throws GlobalException, EmailException {
		Mockito.doThrow(EmailException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendWorkshopCreateSuccessEmail(workshopVO, userVO);
	}

	@Test
	public void sendUpdatedWorkshopDetailsEmail_Success() throws GlobalException, EmailException {
		notificationService.sendUpdatedWorkshopDetailsEmail(workshopVO, new ArrayList<>());
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void sendUpdatedWorkshopDetailsEmail_EmailException() throws GlobalException, EmailException {
		Mockito.doThrow(EmailException.class).when(email).sendEMailMultipleBCC(Mockito.any());
		notificationService.sendUpdatedWorkshopDetailsEmail(workshopVO, new ArrayList<>());
	}

	@Test
	public void sendWorkshopUpdateSuccessEmail_Success() throws GlobalException, EmailException {
		notificationService.sendWorkshopUpdateSuccessEmail(workshopVO, userVO);
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void sendWorkshopUpdateSuccessEmail_EmailException() throws GlobalException, EmailException {
		Mockito.doThrow(EmailException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendWorkshopUpdateSuccessEmail(workshopVO, userVO);
	}

	@Test
	public void sendWorkshopDeletedSuccessEmail_Success() throws GlobalException, EmailException {
		notificationService.sendWorkshopDeletedSuccessEmail(workshopVO, userVO);
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void sendWorkshopDeletedSuccessEmail_EmailException() throws GlobalException, EmailException {
		Mockito.doThrow(EmailException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendWorkshopDeletedSuccessEmail(workshopVO, userVO);
	}

	@Test
	public void sendEnrollSuccessEmail_Success() throws GlobalException, EmailException {
		notificationService.sendEnrollSuccessEmail(workshopVO, userVO);
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void sendEnrollSuccessEmail_EmailException() throws GlobalException, EmailException {
		Mockito.doThrow(EmailException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendEnrollSuccessEmail(workshopVO, userVO);
	}

	@Test
	public void sendWorkshopCancelledEmail_Success() throws GlobalException, EmailException {
		notificationService.sendWorkshopCancelledEmail(workshopVO, new ArrayList<>());
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void sendWorkshopCancelledEmail_EmailException() throws GlobalException, EmailException {
		Mockito.doThrow(EmailException.class).when(email).sendEMailMultipleBCC(Mockito.any());
		notificationService.sendWorkshopCancelledEmail(workshopVO, new ArrayList<>());
	}

	@Test
	public void sendRequestSuccessful_Success() throws GlobalException, EmailException {
		notificationService.sendRequestSuccessful(userVO, "name");
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void sendRequestSuccessful_EmailException() throws GlobalException, EmailException {
		Mockito.doThrow(EmailException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendRequestSuccessful(userVO, "name");
	}

	@Test
	public void sendSkillRequestMail_Success() throws GlobalException, EmailException {
		List<UserVO> usersList = new ArrayList<>();
		UserVO user = new UserVO();
		List<SkillVO> skillsList = new ArrayList<>();
		SkillVO skill = new SkillVO();
		skillsList.add(skill );
		skill.setSkillName("name");
		skill.setSkillId(1);
		user.setSkills(skillsList);
		user.setEmailId("emailId");
		usersList.add(user);
		notificationService.sendSkillRequestMail(usersList , 1, 2L);
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void sendSkillRequestMail_EmailException() throws GlobalException, EmailException {
		List<UserVO> usersList = new ArrayList<>();
		UserVO user = new UserVO();
		List<SkillVO> skillsList = new ArrayList<>();
		SkillVO skill = new SkillVO();
		skillsList.add(skill );
		skill.setSkillName("name");
		skill.setSkillId(1);
		user.setSkills(skillsList);
		user.setEmailId("emailId");
		usersList.add(user);
		Mockito.doThrow(EmailException.class).when(email).sendEMailMultipleBCC(Mockito.any());
		notificationService.sendSkillRequestMail(usersList, 1, 2L);
	}

	@Test
	public void sendUnEnrollSuccessEmail_Success() throws GlobalException, EmailException {
		notificationService.sendUnEnrollSuccessEmail(workshopVO, userVO);
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void sendUnEnrollSuccessEmail_EmailException() throws GlobalException, EmailException {
		Mockito.doThrow(EmailException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendUnEnrollSuccessEmail(workshopVO, userVO);
	}

	@Test
	public void sendPasswordChangeEmail_Success() throws GlobalException, EmailException {
		notificationService.sendPasswordChangeEmail("email", "firstName");
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void sendPasswordChangeEmail_EmailException() throws GlobalException, EmailException {
		Mockito.doThrow(EmailException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendPasswordChangeEmail("email", "firstName");
	}

	@Test
	public void sendProfileUpdateEmail_Success() throws GlobalException, EmailException {
		notificationService.sendProfileUpdateEmail("email", "firstName");
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void sendProfileUpdateEmail_EmailException() throws GlobalException, EmailException {
		Mockito.doThrow(EmailException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendProfileUpdateEmail("email", "firstName");
	}

	@Test
	public void sendCustomMessageToUsers_Success() throws GlobalException, EmailException {
		notificationService.sendCustomMessageToUsers(userVO, messageVO);
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void sendCustomMessageToUsers_EmailException() throws GlobalException, EmailException {
		Mockito.doThrow(EmailException.class).when(email).sendEMailMultipleBCC(Mockito.any());
		notificationService.sendCustomMessageToUsers(userVO, messageVO);
	}

	@Test
	public void sendSuccessCreateUserMail_Success() throws GlobalException, EmailException {
		notificationService.sendSuccessCreateUserMail(signUpDetails);
		assertTrue(true);
	}

	@Test
	public void sendSuccessCreateUserMail_InstructorSuccess() throws GlobalException, EmailException {
		signUpDetails.setUserType(Constants.INSTRUCTOR);
		notificationService.sendSuccessCreateUserMail(signUpDetails);
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void sendSuccessCreateUserMail_EmailException() throws GlobalException, EmailException {
		Mockito.doThrow(EmailException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendSuccessCreateUserMail(signUpDetails);
	}

	@Test
	public void sendSuccessTemporaryPassword_Success() throws GlobalException, EmailException {
		signUpDetails.setCreatePassword("pass");
		notificationService.sendSuccessTemporaryPassword(signUpDetails);
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void sendSuccessTemporaryPassword_EmailException() throws GlobalException, EmailException {
		signUpDetails.setCreatePassword("pass");
		Mockito.doThrow(EmailException.class).when(email).sendEMail(Mockito.any());
		notificationService.sendSuccessTemporaryPassword(signUpDetails);
	}

	@Test
	public void sendUpcomingWorkshopNotification_Success() throws GlobalException, EmailException {
		workshopVO.setCreatedUser("userName");
		workshopVO.setWorkshopDate("12/10/2023");
		workshopVO.setVenue("Venue");
		workshopVO.setStartTime(LocalDateTime.now());
		workshopVO.setMeetingURL("url");
		workshopVO.setRegisteredUsers(new ArrayList<>());
		notificationService.sendUpcomingWorkshopNotification(workshopVO, 2);
		assertTrue(true);
	}

	@Test(expected = EmailException.class)
	public void sendUpcomingWorkshopNotification_EmailException() throws GlobalException, EmailException {
		workshopVO.setCreatedUser("userName");
		workshopVO.setWorkshopDate("12/10/2023");
		workshopVO.setVenue("Venue");
		workshopVO.setStartTime(LocalDateTime.now());
		workshopVO.setMeetingURL("url");
		workshopVO.setRegisteredUsers(new ArrayList<>());
		Mockito.doThrow(EmailException.class).when(email).sendEMailMultipleBCC(Mockito.any());
		notificationService.sendUpcomingWorkshopNotification(workshopVO, 2);
	}
	
}
