package com.clarku.workshop.service;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.exception.LoginException;
import com.clarku.workshop.repository.ISkillsRepo;
import com.clarku.workshop.repository.LoginRepositoryImpl;
import com.clarku.workshop.repository.UserRepositoryImpl;
import com.clarku.workshop.utils.Secure;
import com.clarku.workshop.vo.LoginVO;
import com.clarku.workshop.vo.SignUpVO;
import com.clarku.workshop.vo.SkillVO;
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
	ISkillsRepo skillRepo;

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

	@Test(expected = LoginException.class)
	public void testSignIn_InActiveException() throws GlobalException, LoginException {
		Mockito.when(loginRepo.retrieveUserLogin(Mockito.anyString())).thenReturn(loginDetails);
		loginDetails.setIsActive(false);
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

	@Test
	public void testSignUp_Success() throws GlobalException, LoginException {
		Mockito.when(loginRepo.getUserId(Mockito.anyString())).thenReturn(1);
		Mockito.when(loginRepo.saveLoginDetails(Mockito.any())).thenReturn(true);
		Mockito.when(loginRepo.isUserExists(Mockito.anyString())).thenReturn(false);
		SignUpVO signUpDetails = new SignUpVO();
		signUpDetails.setConfirmPassword("pass");
		signUpDetails.setCreatePassword("pass");
		signUpDetails.setEmailId("email");
		Boolean isSignUp = loginService.signUpUser(signUpDetails );
		assertTrue(isSignUp);
	}

	@Test(expected = GlobalException.class)
	public void testSignUp_PassMismatch() throws GlobalException, LoginException {
		Mockito.when(loginRepo.getUserId(Mockito.anyString())).thenReturn(1);
		Mockito.when(loginRepo.saveLoginDetails(Mockito.any())).thenReturn(true);
		Mockito.when(loginRepo.isUserExists(Mockito.anyString())).thenReturn(false);
		SignUpVO signUpDetails = new SignUpVO();
		signUpDetails.setConfirmPassword("pass");
		signUpDetails.setCreatePassword("pas");
		signUpDetails.setEmailId("email");
		loginService.signUpUser(signUpDetails );
	}

	@Test(expected = GlobalException.class)
	public void testSignUp_UserExists() throws GlobalException, LoginException {
		Mockito.when(loginRepo.getUserId(Mockito.anyString())).thenReturn(1);
		Mockito.when(loginRepo.saveLoginDetails(Mockito.any())).thenReturn(true);
		Mockito.when(loginRepo.isUserExists(Mockito.anyString())).thenReturn(true);
		SignUpVO signUpDetails = new SignUpVO();
		signUpDetails.setEmailId("email");
		loginService.signUpUser(signUpDetails );
	}

	@Test
	public void testSignUp_InstructorSuccess() throws GlobalException, LoginException {
		Mockito.when(loginRepo.getUserId(Mockito.anyString())).thenReturn(1);
		Mockito.when(loginRepo.saveLoginDetails(Mockito.any())).thenReturn(true);
		Mockito.when(loginRepo.isUserExists(Mockito.anyString())).thenReturn(false);
		SignUpVO signUpDetails = new SignUpVO();
		signUpDetails.setConfirmPassword("pass");
		signUpDetails.setCreatePassword("pass");
		signUpDetails.setEmailId("email");
		List<SkillVO> skills = new ArrayList<>();
		SkillVO skill = new SkillVO();
		skill.setSkillId(1);
		skill.setSkillName("name");
		skill.setStatus("Approved");
		SkillVO newSkill = new SkillVO();
		newSkill.setSkillName("new");
		skills.add(skill);
		signUpDetails.setExistingSkills(skills);
		List<SkillVO> newSkills = new ArrayList<>();
		newSkills.add(newSkill);
		signUpDetails.setNewSkills(newSkills);
		Boolean isSignUp = loginService.signUpUser(signUpDetails );
		assertTrue(isSignUp);
	}

	@Test
	public void testCreateUser_Success() throws GlobalException, LoginException {
		Mockito.when(loginRepo.saveLoginDetails(Mockito.any())).thenReturn(true);
		Mockito.when(userRepo.saveSignUpUser(Mockito.any())).thenReturn(true);
		Mockito.when(loginRepo.isUserExists(Mockito.anyString())).thenReturn(false);
		SignUpVO signUpDetails = new SignUpVO();
		signUpDetails.setEmailId("email");
		Boolean isCreated = loginService.createUser(signUpDetails );
		assertTrue(isCreated);
	}

	@Test(expected = GlobalException.class)
	public void testCreateUser_UserExists() throws GlobalException, LoginException {
		Mockito.when(loginRepo.isUserExists(Mockito.anyString())).thenReturn(true);
		SignUpVO signUpDetails = new SignUpVO();
		signUpDetails.setEmailId("email");
		loginService.createUser(signUpDetails );
	}

	@Test
	public void testCreateUser_SaveFail() throws GlobalException, LoginException {
		Mockito.when(loginRepo.saveLoginDetails(Mockito.any())).thenReturn(false);
		Mockito.when(userRepo.saveSignUpUser(Mockito.any())).thenReturn(false);
		Mockito.when(loginRepo.isUserExists(Mockito.anyString())).thenReturn(false);
		SignUpVO signUpDetails = new SignUpVO();
		signUpDetails.setEmailId("email");
		assertFalse(loginService.createUser(signUpDetails));
	}

	@Test
	public void testCreateUser_LoginSaveFail() throws GlobalException, LoginException {
		Mockito.when(loginRepo.saveLoginDetails(Mockito.any())).thenReturn(false);
		Mockito.when(userRepo.saveSignUpUser(Mockito.any())).thenReturn(true);
		Mockito.when(loginRepo.isUserExists(Mockito.anyString())).thenReturn(false);
		SignUpVO signUpDetails = new SignUpVO();
		signUpDetails.setEmailId("email");
		assertFalse(loginService.createUser(signUpDetails));
	}

	@Test
	public void testCreateUser_ProfSaveFail() throws GlobalException, LoginException {
		Mockito.when(loginRepo.saveLoginDetails(Mockito.any())).thenReturn(true);
		Mockito.when(userRepo.saveSignUpUser(Mockito.any())).thenReturn(false);
		Mockito.when(loginRepo.isUserExists(Mockito.anyString())).thenReturn(false);
		SignUpVO createUserDetails = new SignUpVO();
		createUserDetails.setEmailId("email");
		assertFalse(loginService.createUser(createUserDetails));
	}

}
