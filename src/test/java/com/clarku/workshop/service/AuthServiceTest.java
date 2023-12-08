package com.clarku.workshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.repository.ISessionRepo;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.vo.SessionVO;

@RunWith(SpringRunner.class)
public class AuthServiceTest {
	
	@InjectMocks
	AuthServiceImpl authService;
	
	@Mock
	ISessionRepo sessionRepo;
	
	@Mock
	SessionVO session;
	
	@Mock
	HttpHeaders headers;

	@Before
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
		
		session = new SessionVO();
		
		headers = new HttpHeaders();
		headers.add(Constants.HEADER_SESSION_ID, "1");
	}

	@Test
	public void testRetrieveSession_Success() throws GlobalException {
		Mockito.when(sessionRepo.retrieveSession(Mockito.anyInt())).thenReturn(session);
		assertEquals(session, authService.retrieveSession(headers));
	}

	@Test(expected = GlobalException.class)
	public void testGetVenues_NullSession() throws GlobalException {
		Mockito.when(sessionRepo.retrieveSession(Mockito.anyInt())).thenReturn(null);
		authService.retrieveSession(headers);
	}

	@Test(expected = GlobalException.class)
	public void testGetVenues_Exception() throws GlobalException {
		authService.retrieveSession(new HttpHeaders());
	}

}
