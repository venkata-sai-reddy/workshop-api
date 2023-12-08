package com.clarku.workshop.service;

import static org.junit.Assert.assertNull;
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
import com.clarku.workshop.repository.IAdminRepo;
import com.clarku.workshop.vo.VenueVO;

@RunWith(SpringRunner.class)
public class AdminServiceTest {
	
	@InjectMocks
	AdminServiceImpl adminService;
	
	@Mock
	IAdminRepo adminRepo;

	@Before
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testGetVenues_Success() throws GlobalException {
		List<VenueVO> venues = new ArrayList<>();
		VenueVO venue = new VenueVO();
		venue.setVenueId("ON");
		venue.setVenueName("Online");
		venues.add(venue);
		Mockito.when(adminRepo.retrieveVenues()).thenReturn(venues);
		assertEquals(venues, adminService.getVenues());
	}

	@Test
	public void testGetVenues_NoVenueExists() throws GlobalException {
		List<VenueVO> venues = new ArrayList<>();
		Mockito.when(adminRepo.retrieveVenues()).thenReturn(venues);
		assertTrue(adminService.getVenues().isEmpty());
	}

	@Test
	public void testGetVenues_Null() throws GlobalException {
		Mockito.when(adminRepo.retrieveVenues()).thenReturn(null);
		assertNull(adminService.getVenues());
	}

}
