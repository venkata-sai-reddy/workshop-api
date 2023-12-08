package com.clarku.workshop.service;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.clarku.workshop.repository.ISkillsRepo;
import com.clarku.workshop.repository.LoginRepositoryImpl;
import com.clarku.workshop.repository.UserRepositoryImpl;
import com.clarku.workshop.utils.EmailHelper;
import com.clarku.workshop.utils.Secure;
import com.clarku.workshop.vo.ChangePassVO;
import com.clarku.workshop.vo.LoginVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.UserProfileVO;
import com.clarku.workshop.vo.UserVO;

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
	ISkillsRepo skillRepo;

	@Mock
	LoginVO loginDetails;

	@Mock
	UserVO userVO;

	@Before
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
		loginDetails = new LoginVO();
		userVO = new UserVO();
		userVO.setUserId(1);
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

	@Test
	public void testGetUser_Success() throws GlobalException {
		Mockito.when(userRepo.retrieveUserDetails(Mockito.anyInt())).thenReturn(userVO);
		assertEquals(userVO.getUserId(), userService.getUser(1).getUserId());
	}

	@Test
	public void testGetUsers_Success() throws GlobalException {
		Mockito.when(userRepo.getUsers()).thenReturn(new ArrayList<>());
		assertNotNull(userService.getUsers());
	}

	@Test
	public void testGetUsers_Failed() throws GlobalException {
		Mockito.when(userRepo.getUsers()).thenReturn(null);
		assertNull(userService.getUsers());
	}

	@Test
	public void testDeleteUsers_Success() throws GlobalException {
		Mockito.when(userRepo.deleteUser(Mockito.anyInt())).thenReturn(true);
		assertTrue(userService.deleteUser(1));
	}

	@Test
	public void testDeleteUsers_Failed() throws GlobalException {
		Mockito.when(userRepo.deleteUser(Mockito.anyInt())).thenReturn(false);
		assertFalse(userService.deleteUser(1));
	}

	@Test
	public void testGenerateTempPassword_Success() throws GlobalException {
		Mockito.when(secure.generateTempPass()).thenReturn("password");
		Mockito.when(secure.getEncrypted(Mockito.anyString())).thenReturn("hfbfbjbjsbcjs");
		assertEquals("password", userService.generateTempPassword(1));
	}

	@Test
	public void testGetUserSkills_Success() throws GlobalException {
		List<SkillVO> list = new ArrayList<>();
		Mockito.when(userRepo.retrieveUserSkills(Mockito.anyInt())).thenReturn(list );
		assertEquals(list, userService.getUserSkills(1));
	}

	@Test
	public void testGetUsersDetails_Success() throws GlobalException {
		UserProfileVO user = new UserProfileVO();
		Mockito.when(userRepo.getUserDetails(Mockito.anyInt())).thenReturn(user);
		Mockito.when(userRepo.retrieveUserSkills(Mockito.anyInt())).thenReturn(new ArrayList<>());
		assertEquals(user, userService.getUsersDetails(1));
	}

	@Test(expected = GlobalException.class)
	public void testGetUsersDetails_Exception() throws GlobalException {
		Mockito.when(userRepo.getUserDetails(Mockito.anyInt())).thenReturn(null);
		userService.getUsersDetails(1);
	}

	@Test
	public void testChangePassword_Success() throws GlobalException, EmailException {
		loginDetails.setPassword("hdjubgurbvkdjnvidjf");
		loginDetails.setTempPassword(null);
		Mockito.when(userRepo.retrieveUserLoginDetails(Mockito.anyInt())).thenReturn(loginDetails);
		Mockito.when(secure.getEncrypted(Mockito.anyString())).thenReturn("hdjubgurbvkdjnvidjf");
		ChangePassVO passVO = new ChangePassVO();
		passVO.setCurrentPassword("pass");
		passVO.setConfirmPassword("conPass");
		passVO.setCreatePassword("conPass");
		Mockito.when(userRepo.updateUserPass(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		assertTrue(userService.changePassword(1, passVO));
	}

	@Test
	public void testChangePassword_Failed() throws GlobalException, EmailException {
		loginDetails.setTempPassword("hdjubgurbvkdjnvidjf");
		Mockito.when(userRepo.retrieveUserLoginDetails(Mockito.anyInt())).thenReturn(loginDetails);
		Mockito.when(secure.getEncrypted(Mockito.anyString())).thenReturn("hdjubgurbvkdjnvidjf");
		ChangePassVO passVO = new ChangePassVO();
		passVO.setCurrentPassword("pass");
		passVO.setConfirmPassword("conPass");
		passVO.setCreatePassword("conPass");
		Mockito.when(userRepo.updateUserPass(Mockito.anyInt(), Mockito.any())).thenReturn(false);
		assertFalse(userService.changePassword(1, passVO));
	}

	@Test(expected = GlobalException.class)
	public void testChangePassword_Exception() throws GlobalException, EmailException {
		loginDetails.setPassword("hdjubgurbvkdjnvid");
		Mockito.when(userRepo.retrieveUserLoginDetails(Mockito.anyInt())).thenReturn(loginDetails);
		Mockito.when(secure.getEncrypted(Mockito.anyString())).thenReturn("hdjubgurbvkdjnvidjf");
		ChangePassVO passVO = new ChangePassVO();
		passVO.setCurrentPassword("pass");
		passVO.setConfirmPassword("conPass");
		passVO.setCreatePassword("conPass");
		Mockito.when(userRepo.updateUserPass(Mockito.anyInt(), Mockito.any())).thenReturn(false);
		userService.changePassword(1, passVO);
	}

	@Test(expected = GlobalException.class)
	public void testChangePassword_TempPassException() throws GlobalException, EmailException {
		loginDetails.setTempPassword("hdjubgurbvkdjnvid");
		Mockito.when(userRepo.retrieveUserLoginDetails(Mockito.anyInt())).thenReturn(loginDetails);
		Mockito.when(secure.getEncrypted(Mockito.anyString())).thenReturn("hdjubgurbvkdjnvidjf");
		ChangePassVO passVO = new ChangePassVO();
		passVO.setCurrentPassword("pass");
		passVO.setConfirmPassword("conPass");
		passVO.setCreatePassword("conPass");
		Mockito.when(userRepo.updateUserPass(Mockito.anyInt(), Mockito.any())).thenReturn(false);
		userService.changePassword(1, passVO);
	}

	@Test(expected = GlobalException.class)
	public void testChangePassword_WrongPassException() throws GlobalException, EmailException {
		loginDetails.setPassword("hdjubgurbvkdjnvidjf");
		Mockito.when(userRepo.retrieveUserLoginDetails(Mockito.anyInt())).thenReturn(loginDetails);
		Mockito.when(secure.getEncrypted(Mockito.anyString())).thenReturn("hdjubgurbvkdjnvidjf");
		ChangePassVO passVO = new ChangePassVO();
		passVO.setCurrentPassword("password");
		passVO.setConfirmPassword("password");
		passVO.setCreatePassword("password");
		Mockito.when(userRepo.updateUserPass(Mockito.anyInt(), Mockito.any())).thenReturn(false);
		userService.changePassword(1, passVO);
	}

	@Test(expected = GlobalException.class)
	public void testChangePassword_WrongconfirmException() throws GlobalException, EmailException {
		loginDetails.setPassword("hdjubgurbvkdjnvidjf");
		Mockito.when(userRepo.retrieveUserLoginDetails(Mockito.anyInt())).thenReturn(loginDetails);
		Mockito.when(secure.getEncrypted(Mockito.anyString())).thenReturn("hdjubgurbvkdjnvidjf");
		ChangePassVO passVO = new ChangePassVO();
		passVO.setCurrentPassword("pass");
		passVO.setConfirmPassword("conPass");
		passVO.setCreatePassword("createPass");
		Mockito.when(userRepo.updateUserPass(Mockito.anyInt(), Mockito.any())).thenReturn(false);
		userService.changePassword(1, passVO);
	}

	@Test
	public void testUpdateUser_firstName_Success() throws GlobalException {
		List<SkillVO> list = new ArrayList<>();
		Mockito.when(userRepo.retrieveUserSkills(Mockito.anyInt())).thenReturn(list );
		Mockito.when(userRepo.updateUserProf(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		UserVO existingUser = new UserVO();
		UserVO updatedUser = new UserVO();
		existingUser.setFirstName("name");
		existingUser.setLastName("last");
		existingUser.setEmailId("email");
		existingUser.setPhoneNumber("num");
		updatedUser.setFirstName("first");
		updatedUser.setLastName("last");
		updatedUser.setEmailId("email");
		updatedUser.setPhoneNumber("num");
		updatedUser.setNewSkills(list);
		updatedUser.setSkills(list);
		assertTrue(userService.updateUser(existingUser, updatedUser));
	}
	
	@Test
	public void testUpdateUser_False() throws GlobalException {
		List<SkillVO> list = new ArrayList<>();
		Mockito.when(userRepo.retrieveUserSkills(Mockito.anyInt())).thenReturn(list );
		Mockito.when(userRepo.updateUserProf(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		UserVO existingUser = new UserVO();
		UserVO updatedUser = new UserVO();
		existingUser.setFirstName("name");
		existingUser.setLastName("last");
		existingUser.setEmailId("email");
		existingUser.setPhoneNumber("num");
		updatedUser.setFirstName("name");
		updatedUser.setLastName("last");
		updatedUser.setEmailId("email");
		updatedUser.setPhoneNumber("num");
		updatedUser.setSkills(list);
		assertFalse(userService.updateUser(existingUser, updatedUser));
	}

	@Test
	public void testUpdateUser_allFields_Success() throws GlobalException {
		List<SkillVO> list = new ArrayList<>();
		SkillVO skill = new SkillVO();
		skill.setSkillId(1);
		skill.setSkillName("name");
		skill.setStatus("Approved");
		list.add(skill );
		Mockito.when(userRepo.retrieveUserSkills(Mockito.anyInt())).thenReturn(list );
		Mockito.when(userRepo.updateUserProf(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		UserVO existingUser = new UserVO();
		UserVO updatedUser = new UserVO();
		existingUser.setFirstName("name");
		existingUser.setLastName("last");
		existingUser.setEmailId("email");
		existingUser.setPhoneNumber("num");
		updatedUser.setFirstName("first");
		updatedUser.setLastName("name");
		updatedUser.setEmailId("emailid");
		updatedUser.setPhoneNumber("number");
		updatedUser.setNewSkills(null);
		updatedUser.setSkills(list);
		assertTrue(userService.updateUser(existingUser, updatedUser));
	}

	@Test
	public void testUpdateUser_deleteSkills_Success() throws GlobalException {
		List<SkillVO> list = new ArrayList<>();
		SkillVO skill = new SkillVO();
		skill.setSkillId(1);
		skill.setSkillName("name");
		skill.setStatus("Approved");
		list.add(skill );
		SkillVO skill1 = new SkillVO();
		skill1.setSkillId(2);
		skill1.setSkillName("skill0");
		skill1.setStatus("Approved");
		list.add(skill1 );
		Mockito.when(userRepo.retrieveUserSkills(Mockito.anyInt())).thenReturn(list);
		Mockito.when(userRepo.updateUserProf(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		UserVO existingUser = new UserVO();
		UserVO updatedUser = new UserVO();
		existingUser.setUserId(1);
		existingUser.setFirstName("name");
		existingUser.setLastName("last");
		existingUser.setEmailId("email");
		existingUser.setPhoneNumber("num");
		updatedUser.setFirstName("first");
		updatedUser.setLastName("name");
		updatedUser.setEmailId("emailid");
		updatedUser.setPhoneNumber("number");
		updatedUser.setNewSkills(null);
		List<SkillVO> list1 = new ArrayList<>();
		list1.add(skill);
		updatedUser.setSkills(list1);
		assertTrue(userService.updateUser(existingUser, updatedUser));
	}

	@Test
	public void testUpdateUser_lastName_Success() throws GlobalException {
		List<SkillVO> list = new ArrayList<>();
		Mockito.when(userRepo.retrieveUserSkills(Mockito.anyInt())).thenReturn(list );
		Mockito.when(userRepo.updateUserProf(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		UserVO existingUser = new UserVO();
		UserVO updatedUser = new UserVO();
		existingUser.setFirstName("name");
		existingUser.setLastName("last");
		existingUser.setEmailId("email");
		existingUser.setPhoneNumber("num");
		updatedUser.setFirstName("name");
		updatedUser.setLastName("name");
		updatedUser.setEmailId("email");
		updatedUser.setPhoneNumber("num");
		updatedUser.setNewSkills(list);
		updatedUser.setSkills(list);
		assertTrue(userService.updateUser(existingUser, updatedUser));
	}

	@Test
	public void testUpdateUser_phone_Success() throws GlobalException {
		List<SkillVO> list = new ArrayList<>();
		Mockito.when(userRepo.retrieveUserSkills(Mockito.anyInt())).thenReturn(list );
		Mockito.when(userRepo.updateUserProf(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		UserVO existingUser = new UserVO();
		UserVO updatedUser = new UserVO();
		existingUser.setFirstName("name");
		existingUser.setLastName("last");
		existingUser.setEmailId("email");
		existingUser.setPhoneNumber("num");
		updatedUser.setFirstName("name");
		updatedUser.setLastName("last");
		updatedUser.setEmailId("email");
		updatedUser.setPhoneNumber("number");
		updatedUser.setNewSkills(list);
		updatedUser.setSkills(list);
		assertTrue(userService.updateUser(existingUser, updatedUser));
	}

	@Test
	public void testAdminUpdateUser_allFields_Success() throws GlobalException {
		List<SkillVO> list = new ArrayList<>();
		SkillVO skill = new SkillVO();
		skill.setSkillId(1);
		skill.setSkillName("name");
		skill.setStatus("Approved");
		list.add(skill );
		UserProfileVO existingUser = new UserProfileVO();
		UserProfileVO updatedUser = new UserProfileVO();
		existingUser.setUserId(1);
		existingUser.setFirstName("name");
		existingUser.setLastName("last");
		existingUser.setEmailId("email");
		existingUser.setPhoneNumber("num");
		existingUser.setIsActive(true);
		existingUser.setIsLocked(false);
		updatedUser.setIsActive(true);
		updatedUser.setIsLocked(false);
		updatedUser.setUserId(1);
		updatedUser.setFirstName("first");
		updatedUser.setLastName("name");
		updatedUser.setEmailId("emailid");
		updatedUser.setPhoneNumber("number");
		updatedUser.setIsActive(false);
		updatedUser.setIsLocked(true);
		updatedUser.setNewSkills(null);
		updatedUser.setSkills(list);
		Mockito.when(userRepo.getUserDetails(Mockito.anyInt())).thenReturn(existingUser);
		Mockito.when(userRepo.retrieveUserSkills(Mockito.anyInt())).thenReturn(list );
		Mockito.when(userRepo.updateUserProf(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		Mockito.when(userRepo.updateLoginDetails(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		
		assertTrue(userService.updateUser(updatedUser));
	}

	@Test
	public void testAdminUpdateUser_firstName_Success() throws GlobalException {
		List<SkillVO> list = new ArrayList<>();
		Mockito.when(userRepo.retrieveUserSkills(Mockito.anyInt())).thenReturn(list );
		Mockito.when(userRepo.updateUserProf(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		UserProfileVO existingUser = new UserProfileVO();
		UserProfileVO updatedUser = new UserProfileVO();
		existingUser.setFirstName("name");
		existingUser.setLastName("last");
		existingUser.setEmailId("email");
		existingUser.setPhoneNumber("num");
		existingUser.setIsActive(true);
		existingUser.setIsLocked(false);
		updatedUser.setIsActive(true);
		updatedUser.setIsLocked(false);
		updatedUser.setUserId(1);
		updatedUser.setFirstName("first");
		updatedUser.setLastName("last");
		updatedUser.setEmailId("email");
		updatedUser.setPhoneNumber("num");
		updatedUser.setNewSkills(list);
		updatedUser.setSkills(list);
		Mockito.when(userRepo.getUserDetails(Mockito.anyInt())).thenReturn(existingUser);
		Mockito.when(userRepo.retrieveUserSkills(Mockito.anyInt())).thenReturn(list );
		Mockito.when(userRepo.updateUserProf(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		Mockito.when(userRepo.updateLoginDetails(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		assertTrue(userService.updateUser(updatedUser));
	}
	
	@Test
	public void testAdminUpdateUser_False() throws GlobalException {
		List<SkillVO> list = new ArrayList<>();
		UserProfileVO existingUser = new UserProfileVO();
		UserProfileVO updatedUser = new UserProfileVO();
		existingUser.setFirstName("name");
		existingUser.setLastName("last");
		existingUser.setEmailId("email");
		existingUser.setPhoneNumber("num");
		existingUser.setIsActive(true);
		existingUser.setIsLocked(false);
		updatedUser.setIsActive(true);
		updatedUser.setIsLocked(false);
		updatedUser.setUserId(1);
		updatedUser.setFirstName("name");
		updatedUser.setLastName("last");
		updatedUser.setEmailId("email");
		updatedUser.setPhoneNumber("num");
		updatedUser.setSkills(list);
		Mockito.when(userRepo.getUserDetails(Mockito.anyInt())).thenReturn(existingUser);
		Mockito.when(userRepo.retrieveUserSkills(Mockito.anyInt())).thenReturn(list );
		Mockito.when(userRepo.updateUserProf(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		Mockito.when(userRepo.updateLoginDetails(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		assertFalse(userService.updateUser(updatedUser));
	}

	@Test
	public void testAdminUpdateUser_deleteSkills_Success() throws GlobalException {
		List<SkillVO> list = new ArrayList<>();
		SkillVO skill = new SkillVO();
		skill.setSkillId(1);
		skill.setSkillName("name");
		skill.setStatus("Approved");
		list.add(skill );
		SkillVO skill1 = new SkillVO();
		skill1.setSkillId(2);
		skill1.setSkillName("skill0");
		skill1.setStatus("Approved");
		list.add(skill1 );
		UserProfileVO existingUser = new UserProfileVO();
		UserProfileVO updatedUser = new UserProfileVO();
		existingUser.setUserId(1);
		existingUser.setFirstName("name");
		existingUser.setLastName("last");
		existingUser.setEmailId("email");
		existingUser.setPhoneNumber("num");
		existingUser.setIsActive(true);
		existingUser.setIsLocked(false);
		updatedUser.setIsActive(true);
		updatedUser.setIsLocked(false);
		updatedUser.setUserId(1);
		updatedUser.setFirstName("first");
		updatedUser.setLastName("name");
		updatedUser.setEmailId("emailid");
		updatedUser.setPhoneNumber("number");
		updatedUser.setNewSkills(null);
		List<SkillVO> list1 = new ArrayList<>();
		list1.add(skill);
		updatedUser.setSkills(list1);
		Mockito.when(userRepo.getUserDetails(Mockito.anyInt())).thenReturn(existingUser);
		Mockito.when(userRepo.retrieveUserSkills(Mockito.anyInt())).thenReturn(list );
		Mockito.when(userRepo.updateUserProf(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		Mockito.when(userRepo.updateLoginDetails(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		assertTrue(userService.updateUser(updatedUser));
	}

	@Test
	public void testAdminUpdateUser_lastName_Success() throws GlobalException {
		List<SkillVO> list = new ArrayList<>();
		UserProfileVO existingUser = new UserProfileVO();
		UserProfileVO updatedUser = new UserProfileVO();
		existingUser.setFirstName("name");
		existingUser.setLastName("last");
		existingUser.setEmailId("email");
		existingUser.setPhoneNumber("num");
		existingUser.setIsActive(true);
		existingUser.setIsLocked(false);
		updatedUser.setIsActive(true);
		updatedUser.setIsLocked(false);
		updatedUser.setUserId(1);
		updatedUser.setFirstName("name");
		updatedUser.setLastName("name");
		updatedUser.setEmailId("email");
		updatedUser.setPhoneNumber("num");
		updatedUser.setNewSkills(list);
		updatedUser.setSkills(list);
		Mockito.when(userRepo.getUserDetails(Mockito.anyInt())).thenReturn(existingUser);
		Mockito.when(userRepo.retrieveUserSkills(Mockito.anyInt())).thenReturn(list );
		Mockito.when(userRepo.updateUserProf(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		Mockito.when(userRepo.updateLoginDetails(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		assertTrue(userService.updateUser(updatedUser));
	}

	@Test
	public void testAdminUpdateUser_phone_Success() throws GlobalException {
		List<SkillVO> list = new ArrayList<>();
		UserProfileVO existingUser = new UserProfileVO();
		UserProfileVO updatedUser = new UserProfileVO();
		existingUser.setFirstName("name");
		existingUser.setLastName("last");
		existingUser.setEmailId("email");
		existingUser.setPhoneNumber("num");
		existingUser.setIsActive(true);
		existingUser.setIsLocked(false);
		updatedUser.setIsActive(true);
		updatedUser.setIsLocked(false);
		updatedUser.setUserId(1);
		updatedUser.setFirstName("name");
		updatedUser.setLastName("last");
		updatedUser.setEmailId("email");
		updatedUser.setPhoneNumber("number");
		updatedUser.setNewSkills(list);
		updatedUser.setSkills(list);
		Mockito.when(userRepo.getUserDetails(Mockito.anyInt())).thenReturn(existingUser);
		Mockito.when(userRepo.retrieveUserSkills(Mockito.anyInt())).thenReturn(list );
		Mockito.when(userRepo.updateUserProf(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		Mockito.when(userRepo.updateLoginDetails(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		assertTrue(userService.updateUser(updatedUser));
	}
	
}
