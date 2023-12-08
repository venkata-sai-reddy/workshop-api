package com.clarku.workshop.service;

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
import com.clarku.workshop.repository.ISkillsRepo;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.vo.SkillVO;

@RunWith(SpringRunner.class)
public class SkillServiceTest {
	
	@InjectMocks
	SkillsServiceImpl skillService;
	
	@Mock
	ISkillsRepo skillRepo;

	@Mock
	List<SkillVO> skills;

	@Before
	public void initMocks() {
		MockitoAnnotations.openMocks(this);
		skills = new ArrayList<>();
		SkillVO approvedSkill = new SkillVO();
		SkillVO rejectedSkill = new SkillVO();
		SkillVO requestedSkill = new SkillVO();
		approvedSkill.setSkillId(1);
		approvedSkill.setSkillName("skill1");
		approvedSkill.setStatus(Constants.APPROVED);
		rejectedSkill.setSkillId(2);
		rejectedSkill.setSkillName("skill2");
		rejectedSkill.setStatus(Constants.REJECTED);
		requestedSkill.setSkillId(3);
		requestedSkill.setSkillName("skill3");
		requestedSkill.setStatus(Constants.REQUESTED);
		skills.add(approvedSkill);
		skills.add(rejectedSkill);
		skills.add(requestedSkill);
	}

	@Test
	public void testGetAllSkills_Success() throws GlobalException {
		Mockito.when(skillRepo.getAllSkills()).thenReturn(skills);
		assertEquals(2, skillService.getAllSkills().size());
	}

	@Test
	public void testGetAllRequestedSkills_Success() throws GlobalException {
		Mockito.when(skillRepo.getAllSkills()).thenReturn(skills);
		assertEquals(1, skillService.getAllRequestedSkills().size());
	}

	@Test
	public void testUpdateRequestedSkills_Success() throws GlobalException {
		Mockito.when(skillRepo.updateSkillStatus(Mockito.any())).thenReturn(true);
		assertTrue(skillService.updateRequestedSkills(new SkillVO()));
	}

	@Test
	public void testGetAllSystemSkills_Success() throws GlobalException {
		Mockito.when(skillRepo.getAllSkills()).thenReturn(skills);
		assertEquals(3, skillService.getAllSystemSkills().size());
	}

	@Test
	public void testAddNewSkills_Success() throws GlobalException {
		Mockito.when(skillRepo.saveNewSkillsByAdmin(Mockito.anyList())).thenReturn(true);
		assertTrue(skillService.addNewSkills(new ArrayList<>()));
	}

	@Test
	public void testGetAllRequestedSkillWorkshops_Success() throws GlobalException {
		Mockito.when(skillRepo.getAllRequestedSkills()).thenReturn(new ArrayList<>());
		assertTrue(skillService.getAllRequestedSkillWorkshops().isEmpty());
	}

}
