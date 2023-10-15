package com.clarku.workshop.service;

import java.util.List;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.vo.SearchWorkshopVO;
import com.clarku.workshop.vo.UserVO;
import com.clarku.workshop.vo.WorkshopVO;
import com.clarku.workshop.vo.WorkshopsTimeLineVO;

public interface IWorkshopService {

	WorkshopVO createWorkshop(WorkshopVO workshopDetails, Integer userId) throws GlobalException;

	List<String> getRegisteredWorkshopUsersEmail(Integer workshopId) throws GlobalException;

	Boolean updateWorkshop(WorkshopVO workshopDetails, Integer userId) throws GlobalException;

	WorkshopsTimeLineVO getAllWorkshops() throws GlobalException;

	WorkshopsTimeLineVO getAllCreatedWorkshops(Integer userId) throws GlobalException;

	WorkshopsTimeLineVO getAllRegisteredWorkshops(Integer userId) throws GlobalException;

	WorkshopVO getWorkshop(Integer workshopId) throws GlobalException;

	Boolean deleteWorkshop(Integer workshopId, UserVO user) throws GlobalException;

	List<WorkshopVO> searchWorkshops(SearchWorkshopVO searchDetails) throws GlobalException;

	Boolean enrollWorkshop(Integer workshopId, Integer userId) throws GlobalException;

}
