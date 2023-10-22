package com.clarku.workshop.service;

import java.util.List;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.vo.SignUpVO;
import com.clarku.workshop.vo.UserVO;
import com.clarku.workshop.vo.WorkshopVO;

public interface INotificationService {

	void sendSuccessSignUpMail(SignUpVO userDetails) throws GlobalException, EmailException;

	void sendSuccessResetPassEmail(String emailId, String firstName, String tempPass) throws GlobalException, EmailException;

	void sendWorkshopCreateSuccessEmail(WorkshopVO workshopDetails, UserVO user) throws GlobalException, EmailException;

	void sendUpdatedWorkshopDetailsEmail(WorkshopVO workshopDetails, List<String> registeredUsers) throws GlobalException, EmailException;

	void sendWorkshopUpdateSuccessEmail(WorkshopVO workshopDetails, UserVO user) throws GlobalException, EmailException;

	void sendWorkshopDeletedSuccessEmail(WorkshopVO workshop, UserVO user) throws GlobalException, EmailException;

	void sendEnrollSuccessEmail(WorkshopVO workshop, UserVO user) throws GlobalException, EmailException;

	void sendWorkshopCancelledEmail(WorkshopVO workshop, List<String> registeredUsers) throws GlobalException, EmailException;

}
