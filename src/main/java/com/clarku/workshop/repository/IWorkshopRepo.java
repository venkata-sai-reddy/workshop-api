package com.clarku.workshop.repository;

import java.util.List;
import java.util.Map;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.vo.RegisteredUserVO;
import com.clarku.workshop.vo.RequestVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.WorkshopVO;

public interface IWorkshopRepo {

	Boolean createWorkshop(WorkshopVO workshopDetails) throws GlobalException;

	Boolean updateWorkshop(Map<String, Object> updatedFields, Integer workshopId) throws GlobalException;

	Boolean deleteWorkshop(Integer workshopId) throws GlobalException;

	WorkshopVO retrieveWorkshop(Integer workshopId, Integer userId) throws GlobalException;

	WorkshopVO getLastCrtdWrkshpByUserId(Integer userId) throws GlobalException;

	List<WorkshopVO> getAllWorkshops(Integer userId) throws GlobalException;

	List<WorkshopVO> getCreatedWorkshops(Integer userId) throws GlobalException;

	List<WorkshopVO> getEnrolledWorkshops(Integer userId) throws GlobalException;

	Boolean saveWorkshopSkillMap(Integer workshopId, List<Integer> skills) throws GlobalException;

	List<WorkshopVO> getCreatedWorkshopsByDate(Integer createdUserId, String workshopDate) throws GlobalException;

	List<WorkshopVO> getWorkshopsByDate(String workshopDate) throws GlobalException;

	List<RegisteredUserVO> retrieveWorkshopRegisteredUser(Integer workshopId) throws GlobalException;

	List<WorkshopVO> getAllWorkshopsBySkills(String skill) throws GlobalException;

	Boolean enrollWorkshop(Integer workshopId, Integer userId) throws GlobalException;

	List<SkillVO> getWorkshopSkills(Integer workshopId) throws GlobalException;

	Boolean deleteWorkshopSkillMap(Integer workshopId, List<Integer> deletedSkills) throws GlobalException;

	Boolean saveUserSkillRequest(Integer userId, List<SkillVO> requestedSkills) throws GlobalException;

	List<RequestVO> getRecentlyRequestedSkillRequest() throws GlobalException;

	Boolean checkIsUserEnrolled(Integer userId, Integer workshopId) throws GlobalException;

	void incrementWorkshopEnrollCount(Integer workshopId) throws GlobalException;

	void decrementWorkshopEnrollCount(Integer workshopId) throws GlobalException;

	Boolean unEnrollWorkshop(Integer workshopId, Integer userId) throws GlobalException;

	void updateRequestSkillsStatus(List<Integer> skillsNotified, String status) throws GlobalException;

	Boolean isUserCreatedWorkshop(Integer userId, Integer workshopId) throws GlobalException;

	Boolean saveOrUpdateWorkshopMettingDetails(Integer workshopId, String meetingURL) throws GlobalException;

	String getWorkshopMeetingDetails(Integer workshopId) throws GlobalException;

	List<WorkshopVO> getNextTwoDaysWorkshops() throws GlobalException;

}
