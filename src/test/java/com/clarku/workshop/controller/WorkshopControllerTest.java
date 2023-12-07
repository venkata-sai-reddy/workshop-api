package com.clarku.workshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

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
import com.clarku.workshop.service.IUserService;
import com.clarku.workshop.service.IWorkshopService;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.vo.RequestVO;
import com.clarku.workshop.vo.SearchWorkshopVO;
import com.clarku.workshop.vo.SessionVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.UserVO;
import com.clarku.workshop.vo.WorkshopVO;
import com.clarku.workshop.vo.WorkshopsTimeLineVO;

@RunWith(SpringJUnit4ClassRunner.class)
public class WorkshopControllerTest {

	@InjectMocks
	WorkshopController workshopController;

	@Mock
	IWorkshopService workshopService;

	@Mock
	ISessionService sessionService;

	@Mock
	IAuthService authService;

	@Mock
	INotificationService notify;

	@Mock
	IUserService userService;

	@Mock
	SessionVO sessionVO;

	@Mock
	WorkshopVO workshopVO;

	@Mock
	UserVO userVO;

	@Mock
	List<SkillVO> skills;
	
	@Before
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
		sessionVO = new SessionVO();
		sessionVO.setUserId(1);
		
		userVO = new UserVO();
		userVO.setUserId(1);
		
		workshopVO = new WorkshopVO();
		skills = new ArrayList<>();
	}

	@Test
	public void testCreateWorkshop_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.INSTRUCTOR);
		Mockito.when(workshopService.createWorkshop(workshopVO, null)).thenReturn(workshopVO);
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<WorkshopVO> actual = workshopController.createWorkshop(headers, workshopVO);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}
	
	@Test
	public void testCreateWorkshop_AdminSuccess() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(workshopService.createWorkshop(workshopVO, null)).thenReturn(workshopVO);
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<WorkshopVO> actual = workshopController.createWorkshop(headers, workshopVO);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testCreateWorkshop_UnAuthException() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		HttpHeaders headers = new HttpHeaders();
		workshopController.createWorkshop(headers, workshopVO);
	}

	@Test(expected = GlobalException.class)
	public void testCreateWorkshop_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		workshopController.createWorkshop(headers, workshopVO);
	}

	@Test
	public void testViewWorkshop_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		Mockito.when(workshopService.getWorkshop(1, userVO)).thenReturn(workshopVO);
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<WorkshopVO> actual = workshopController.viewWorkshop(headers, 1);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testViewWorkshop_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		workshopController.viewWorkshop(headers, 1);
	}

	@Test
	public void testEnrollWorkshop_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		Mockito.when(workshopService.enrollWorkshop(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
		HttpHeaders headers = new HttpHeaders();
		workshopVO.setWorkshopId(1);
		ResponseEntity<Boolean> actual = workshopController.enrollWorkshop(headers, workshopVO);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}
	
	@Test(expected = GlobalException.class)
	public void testEnrollWorkshop_Failed() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		Mockito.when(workshopService.enrollWorkshop(Mockito.anyInt(), Mockito.anyInt())).thenReturn(false);
		HttpHeaders headers = new HttpHeaders();
		workshopVO.setWorkshopId(1);
		workshopController.enrollWorkshop(headers, workshopVO);
		
	}

	@Test(expected = GlobalException.class)
	public void testEnrollWorkshop_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		workshopController.enrollWorkshop(headers, workshopVO);
	}

	@Test
	public void testUnEnrollWorkshop_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		Mockito.when(workshopService.unEnrollWorkshop(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
		HttpHeaders headers = new HttpHeaders();
		workshopVO.setWorkshopId(1);
		ResponseEntity<Boolean> actual = workshopController.unEnrollWorkshop(headers, workshopVO);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}
	
	@Test(expected = GlobalException.class)
	public void testUnEnrollWorkshop_Failed() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		Mockito.when(workshopService.unEnrollWorkshop(Mockito.anyInt(), Mockito.anyInt())).thenReturn(false);
		HttpHeaders headers = new HttpHeaders();
		workshopVO.setWorkshopId(1);
		workshopController.unEnrollWorkshop(headers, workshopVO);
		
	}

	@Test(expected = GlobalException.class)
	public void testUnEnrollWorkshop_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		workshopController.unEnrollWorkshop(headers, workshopVO);
	}

	@Test
	public void testRequestWorkshop_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		Mockito.when(workshopService.requestWorkshop(Mockito.any(), Mockito.any())).thenReturn(true);
		HttpHeaders headers = new HttpHeaders();
		workshopVO.setWorkshopId(1);
		ResponseEntity<Boolean> actual = workshopController.requestWorkshop(headers, skills);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}
	
	@Test(expected = GlobalException.class)
	public void testRequestWorkshop_Failed() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		Mockito.when(workshopService.requestWorkshop(Mockito.any(), Mockito.any())).thenReturn(false);
		HttpHeaders headers = new HttpHeaders();
		workshopVO.setWorkshopId(1);
		workshopController.requestWorkshop(headers, skills);
	}

	@Test(expected = GlobalException.class)
	public void testRequestWorkshop_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		workshopController.requestWorkshop(headers, skills);
	}

	@Test
	public void testDeleteWorkshop_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.INSTRUCTOR);
		Mockito.when(workshopService.getRegisteredWorkshopUsersEmail(Mockito.anyInt())).thenReturn(new ArrayList<>());
		Mockito.when(workshopService.deleteWorkshop(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		workshopVO.setWorkshopId(1);
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<Boolean> actual = workshopController.deleteWorkshop(headers, workshopVO);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testDeleteWorkshop_Failed() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.INSTRUCTOR);
		workshopVO.setWorkshopId(1);
		Mockito.when(workshopService.deleteWorkshop(Mockito.anyInt(), Mockito.any())).thenReturn(false);
		HttpHeaders headers = new HttpHeaders();
		workshopController.deleteWorkshop(headers, workshopVO);
	}

	@Test
	public void testDeleteWorkshop_AdminSuccess() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(workshopService.getRegisteredWorkshopUsersEmail(Mockito.anyInt())).thenReturn(new ArrayList<>());
		Mockito.when(workshopService.deleteWorkshop(Mockito.anyInt(), Mockito.any())).thenReturn(true);
		workshopVO.setWorkshopId(1);
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<Boolean> actual = workshopController.deleteWorkshop(headers, workshopVO);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testDeleteWorkshop_UnAuthException() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		workshopVO.setWorkshopId(1);
		HttpHeaders headers = new HttpHeaders();
		workshopController.deleteWorkshop(headers, workshopVO);
	}

	@Test(expected = GlobalException.class)
	public void testDeleteWorkshop_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		workshopController.deleteWorkshop(headers, workshopVO);
	}

	@Test
	public void testSearchWorkshop_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(workshopService.searchWorkshops(Mockito.any(), Mockito.anyInt())).thenReturn(new ArrayList<>());
		HttpHeaders headers = new HttpHeaders();
		SearchWorkshopVO searchDetails = new SearchWorkshopVO();
		ResponseEntity<List<WorkshopVO>> actual = workshopController.searchWorkshop(headers, searchDetails);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testSearchWorkshop_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		SearchWorkshopVO searchDetails = new SearchWorkshopVO();
		workshopController.searchWorkshop(headers, searchDetails);
	}

	@Test
	public void testAllWorkshop_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(workshopService.getAllWorkshops(Mockito.anyInt())).thenReturn(new WorkshopsTimeLineVO());
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<WorkshopsTimeLineVO> actual = workshopController.getAllWorkshops(headers);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testAllWorkshop_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		workshopController.getAllWorkshops(headers);
	}

	@Test
	public void testRegisteredWorkshop_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(workshopService.getAllRegisteredWorkshops(Mockito.anyInt())).thenReturn(new WorkshopsTimeLineVO());
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<WorkshopsTimeLineVO> actual = workshopController.getAllRegisteredWorkshops(headers);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testRegisteredWorkshop_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		workshopController.getAllRegisteredWorkshops(headers);
	}

	@Test
	public void testRequestedWorkshop_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(workshopService.getAllReqestedSkills(Mockito.anyInt())).thenReturn(new ArrayList<>());
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<List<RequestVO>> actual = workshopController.getAllRequestedWorkshops(headers);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testRequestedWorkshop_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		workshopController.getAllRequestedWorkshops(headers);
	}

	@Test
	public void testAllWorkshopByUser_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.INSTRUCTOR);
		Mockito.when(workshopService.getAllCreatedWorkshops( Mockito.any())).thenReturn(new WorkshopsTimeLineVO());
		HttpHeaders headers = new HttpHeaders();
		workshopVO.setWorkshopId(1);
		ResponseEntity<WorkshopsTimeLineVO> actual = workshopController.getAllWorkshopsByUser(headers);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test
	public void testAllWorkshopByUser_AdminSuccess() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(workshopService.getAllCreatedWorkshops( Mockito.any())).thenReturn(new WorkshopsTimeLineVO());
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<WorkshopsTimeLineVO> actual = workshopController.getAllWorkshopsByUser(headers);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testAllWorkshopByUser_Failed() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		Mockito.when(workshopService.getAllCreatedWorkshops( Mockito.any())).thenReturn(new WorkshopsTimeLineVO());
		HttpHeaders headers = new HttpHeaders();
		workshopController.getAllWorkshopsByUser(headers);
	}

	@Test(expected = GlobalException.class)
	public void testAllWorkshopByUser_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		workshopController.getAllWorkshopsByUser(headers);
	}

	@Test
	public void testUpdateWorkshop_Success() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.INSTRUCTOR);
		Mockito.when(workshopService.updateWorkshop(Mockito.any(), Mockito.anyInt())).thenReturn(true);
		Mockito.when(workshopService.getRegisteredWorkshopUsersEmail(Mockito.anyInt())).thenReturn(new ArrayList<>() );
		HttpHeaders headers = new HttpHeaders();
		workshopVO.setWorkshopId(1);
		ResponseEntity<Boolean> actual = workshopController.updateWorkshop(headers, workshopVO);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test
	public void testUpdateWorkshop_AdminSuccess() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		userVO.setUserType(Constants.ADMIN);
		Mockito.when(workshopService.updateWorkshop(Mockito.any(), Mockito.anyInt())).thenReturn(true);
		List<String> list = new ArrayList<>();
		list.add("user");
		Mockito.when(workshopService.getRegisteredWorkshopUsersEmail(Mockito.anyInt())).thenReturn(list );
		HttpHeaders headers = new HttpHeaders();
		workshopVO.setWorkshopId(1);
		ResponseEntity<Boolean> actual = workshopController.updateWorkshop(headers, workshopVO);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}

	@Test(expected = GlobalException.class)
	public void testUpdateWorkshop_Failed() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenReturn(sessionVO);
		Mockito.when(userService.getUser(Mockito.anyInt())).thenReturn(userVO);
		HttpHeaders headers = new HttpHeaders();
		workshopController.updateWorkshop(headers, workshopVO);
	}

	@Test(expected = GlobalException.class)
	public void testUpdateWorkshop_Exception() throws GlobalException, EmailException {
		Mockito.when(authService.retrieveSession(Mockito.any())).thenThrow(new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED));
		HttpHeaders headers = new HttpHeaders();
		workshopController.updateWorkshop(headers, workshopVO);
	}

	
}
