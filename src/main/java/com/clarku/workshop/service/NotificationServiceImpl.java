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
	EmailHelper email;

	private static final String FIRST_NAME = "firstName";

	@Override
	public void sendSuccessSignUpMail(String emailId, String firstName) throws EmailException, GlobalException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(emailId);
		emailVO.setSubject(EmailConstants.WELCOME_SUBJECT);
		emailVO.setTemplateName(EmailConstants.SIGNUP_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, firstName);
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendSuccessResetPassEmail(String emailId, String firstName, String tempPass) throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(emailId);
		emailVO.setSubject(EmailConstants.RESET_PASS_SUBJECT);
		emailVO.setTemplateName(EmailConstants.RESET_PASS_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, firstName);
		variables.put("tempPass", tempPass);
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

}
