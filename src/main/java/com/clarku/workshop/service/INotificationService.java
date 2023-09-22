package com.clarku.workshop.service;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;

public interface INotificationService {

	void sendSuccessSignUpMail(String emailId, String firstName) throws GlobalException, EmailException ;

}