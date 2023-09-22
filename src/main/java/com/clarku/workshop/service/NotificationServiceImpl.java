package com.clarku.workshop.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.utils.EmailConstants;
import com.clarku.workshop.utils.EmailHelper;
import com.clarku.workshop.vo.EmailVO;

@Service
public class NotificationServiceImpl implements INotificationService {

	@Autowired
	private EmailHelper email;

	@Override
	public void sendSuccessSignUpMail(String emailId, String firstName) throws EmailException, GlobalException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(emailId);
		emailVO.setSubject(EmailConstants.WELCOME_SUBJECT);
		emailVO.setTemplateName(EmailConstants.SIGNUP_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put("firstName", firstName);
		email.sendEMail(emailVO);
	}

}
