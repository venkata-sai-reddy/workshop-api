package com.clarku.workshop.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.repository.SessionRepositoryImpl;
import com.clarku.workshop.vo.SessionVO;

@RunWith(SpringJUnit4ClassRunner.class)
public class SessionServiceTest {

	@InjectMocks
	SessionServiceImpl sessionService;

	@Mock
	SessionRepositoryImpl sessionRepo;

	@Mock
	SessionVO sessionVO;

	@Before
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
		sessionVO = new SessionVO();
		sessionVO.setSessionId(1);
	}

	@Test
	public void testCreateSession_Success() throws GlobalException {
		Mockito.when(sessionRepo.isActiveSessionExists(Mockito.anyInt())).thenReturn(false);
		Mockito.when(sessionRepo.retrieveSessionDetails(Mockito.anyInt())).thenReturn(sessionVO);
		SessionVO session = sessionService.createSession(1);
		assertEquals(session.getSessionId(), sessionVO.getSessionId());
	}

	@Test(expected = GlobalException.class)
	public void testCreateSession_AlreadyExistsException() throws GlobalException {
		Mockito.when(sessionRepo.isActiveSessionExists(Mockito.anyInt())).thenReturn(true);
		sessionService.createSession(1);
	}

	@Test(expected = GlobalException.class)
	public void testCreateSession_Exception() throws GlobalException {
		Mockito.when(sessionRepo.isActiveSessionExists(Mockito.anyInt())).thenReturn(false);
		Mockito.when(sessionRepo.retrieveSessionDetails(Mockito.anyInt())).thenThrow(GlobalException.class);
		sessionService.createSession(1);
	}

	@Test
	public void testGetSession_Success() throws GlobalException {
		Mockito.when(sessionRepo.retrieveSessionDetails(Mockito.anyInt())).thenReturn(sessionVO);
		SessionVO session = sessionService.getSession(1);
		assertEquals(session.getSessionId(), sessionVO.getSessionId());
	}

	@Test(expected = GlobalException.class)
	public void testGetSession_Exception() throws GlobalException {
		Mockito.when(sessionRepo.retrieveSessionDetails(Mockito.anyInt())).thenThrow(GlobalException.class);
		sessionService.getSession(1);
	}

	@Test
	public void testEndSession_Success() throws GlobalException {
		Mockito.when(sessionRepo.isSessionActive(Mockito.anyInt())).thenReturn(true);
		sessionService.endSession(sessionVO.getSessionId());
		assertTrue(true);
	}

	@Test(expected = GlobalException.class)
	public void testEndSession_AlreadyExistsException() throws GlobalException {
		Mockito.when(sessionRepo.isSessionActive(Mockito.anyInt())).thenReturn(false);
		sessionService.endSession(sessionVO.getSessionId());
	}

	@Test(expected = GlobalException.class)
	public void testEndSession_Exception() throws GlobalException {
		Mockito.doThrow(GlobalException.class).when(sessionRepo).endSession(Mockito.anyInt());
		sessionService.endSession(sessionVO.getSessionId());
	}

	@Test
	public void testExtendSession_Success() throws GlobalException {
		Mockito.doNothing().when(sessionRepo).updateSession(Mockito.anyInt());
		sessionService.extendSession(1);
		assertTrue(true);
	}

	@Test(expected = GlobalException.class)
	public void testExtendSession_Exception() throws GlobalException {
		Mockito.doThrow(GlobalException.class).when(sessionRepo).updateSession(Mockito.anyInt());
		sessionService.extendSession(1);
	}

	@Test
	public void testIsSessionExists_Success() throws GlobalException {
		Mockito.when(sessionRepo.isActiveSessionExists(Mockito.anyInt())).thenReturn(false);
		assertFalse(sessionService.isSessionExists(2));
	}

	@Test
	public void testIsSessionExists_SessionExists() throws GlobalException {
		Mockito.when(sessionRepo.isActiveSessionExists(Mockito.anyInt())).thenReturn(true);
		assertTrue(sessionService.isSessionExists(1));
	}

	@Test(expected = GlobalException.class)
	public void testIsSessionExists_Exception() throws GlobalException {
		Mockito.when(sessionRepo.isActiveSessionExists(Mockito.anyInt())).thenThrow(GlobalException.class);
		sessionService.isSessionExists(3);
	}

}
