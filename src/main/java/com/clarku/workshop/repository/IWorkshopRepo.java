package com.clarku.workshop.repository;

import java.util.List;
import java.util.Map;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.vo.RegisteredUserVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.WorkshopVO;

public interface IWorkshopRepo {

	Boolean createWorkshop(WorkshopVO workshopDetails) throws GlobalException;

	Boolean updateWorkshop(Map<String, Object> updatedFields) throws GlobalException;

	Boolean deleteWorkshop(Integer workshopId) throws GlobalException;

	WorkshopVO retrieveWorkshop(Integer workshopId) throws GlobalException;

	WorkshopVO getLastCrtdWrkshpByUserId(Integer userId) throws GlobalException;

	List<WorkshopVO> getAllWorkshops() throws GlobalException;

	List<WorkshopVO> getCreatedWorkshops(Integer userId) throws GlobalException;

	List<WorkshopVO> getEnrolledWorkshops(Integer userId) throws GlobalException;

	Boolean saveWorkshopSkillMap(Integer workshopId, List<Integer> skills) throws GlobalException;

	List<WorkshopVO> getCreatedWorkshopsByDate(Integer createdUserId, String workshopDate) throws GlobalException;

	List<WorkshopVO> getWorkshopsByDate(String workshopDate) throws GlobalException;

	List<RegisteredUserVO> retrieveWorkshopRegisteredUser(Integer workshopId) throws GlobalException;

	List<WorkshopVO> getAllWorkshopsBySkills(String skill) throws GlobalException;

	Boolean enrollWorkshop(Integer workshopId, Integer userId) throws GlobalException;

	List<SkillVO> getWorkshopSkills(Integer workshopId) throws GlobalException;

}
