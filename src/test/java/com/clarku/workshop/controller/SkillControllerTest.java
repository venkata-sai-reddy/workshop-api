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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.service.ISkillsService;
import com.clarku.workshop.vo.SkillVO;

@RunWith(SpringJUnit4ClassRunner.class)
public class SkillControllerTest {

	@InjectMocks
	SkillController skillController;

	@Mock
	ISkillsService skillsService;

	@Before
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testAllSkills_Success() throws GlobalException {
		Mockito.when(skillsService.getAllSkills()).thenReturn(new ArrayList<>());
		ResponseEntity<List<SkillVO>> actual = skillController.getAllSkills();
		assertEquals(HttpStatus.OK, actual.getStatusCode());
	}
}
