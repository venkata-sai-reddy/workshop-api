package com.clarku.workshop.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.utils.EmailConstants;
import com.clarku.workshop.utils.EmailHelper;
import com.clarku.workshop.vo.EmailVO;
import com.clarku.workshop.vo.SignUpVO;
import com.clarku.workshop.vo.SkillVO;
import com.clarku.workshop.vo.UserVO;
import com.clarku.workshop.vo.WorkshopVO;

@Service
public class NotificationServiceImpl implements INotificationService {

	@Autowired
	EmailHelper email;

	private static final String FIRST_NAME = "firstName";

	private static final String WORKSHOP_NAME = "workshopName";

	private static final String SKILL_NAME = "skillName";

	@Override
	public void sendSuccessSignUpMail(SignUpVO userDetails) throws EmailException, GlobalException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(userDetails.getEmailId());
		emailVO.setSubject(EmailConstants.WELCOME_SUBJECT);
		emailVO.setTemplateName(userDetails.getUserType().equalsIgnoreCase(Constants.INSTRUCTOR) ? EmailConstants.SIGNUP_INSTRUCTOT_SUCCESS_TEMPLATE : EmailConstants.SIGNUP_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, userDetails.getFirstName());
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

	@Override
	public void sendWorkshopCreateSuccessEmail(WorkshopVO workshopDetails, UserVO user)
			throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(user.getEmailId());
		emailVO.setSubject(EmailConstants.WORKSHOP_SUCCESS_CREATED_SUB);
		emailVO.setTemplateName(EmailConstants.WORSHOP_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, user.getFirstName());
		variables.put(WORKSHOP_NAME, workshopDetails.getWorkshopName());
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendUpdatedWorkshopDetailsEmail(WorkshopVO workshopDetails, List<String> registeredUsers) throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendMultipleBCCTo(registeredUsers);
		emailVO.setSubject(EmailConstants.WORKSHOP_UPDATED_SUB+" : "+workshopDetails.getWorkshopName());
		emailVO.setTemplateName(EmailConstants.WORSHOP_UPDATE_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(WORKSHOP_NAME, workshopDetails.getWorkshopName());
		emailVO.setVariables(variables);
		email.sendEMailMultipleBCC(emailVO);
	}

	@Override
	public void sendWorkshopUpdateSuccessEmail(WorkshopVO workshopDetails, UserVO user) throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(user.getEmailId());
		emailVO.setSubject(EmailConstants.WORKSHOP_UPDATED_SUCCESS_SUB);
		emailVO.setTemplateName(EmailConstants.WORSHOP_UPDATED_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, user.getFirstName());
		variables.put(WORKSHOP_NAME, workshopDetails.getWorkshopName());
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendWorkshopDeletedSuccessEmail(WorkshopVO workshop, UserVO user) throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(user.getEmailId());
		emailVO.setSubject(EmailConstants.WORKSHOP_DELETED_SUCCESS_SUB);
		emailVO.setTemplateName(EmailConstants.WORSHOP_DELETE_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, user.getFirstName());
		variables.put(WORKSHOP_NAME, workshop.getWorkshopName());
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendEnrollSuccessEmail(WorkshopVO workshop, UserVO user) throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(user.getEmailId());
		emailVO.setSubject(EmailConstants.WORKSHOP_SUCCESS_ENROLL_SUB);
		emailVO.setTemplateName(EmailConstants.WORSHOP_ENROLL_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, user.getFirstName());
		variables.put(WORKSHOP_NAME, workshop.getWorkshopName());
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendWorkshopCancelledEmail(WorkshopVO workshop, List<String> registeredUsers) throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendMultipleBCCTo(registeredUsers);
		emailVO.setSubject(EmailConstants.WORKSHOP_CANCELED_SUB+" : "+workshop.getWorkshopName());
		emailVO.setTemplateName(EmailConstants.WORSHOP_CANCELLED_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(WORKSHOP_NAME, workshop.getWorkshopName());
		emailVO.setVariables(variables);
		email.sendEMailMultipleBCC(emailVO);
	}

	@Override
	public void sendRequestSuccessful(UserVO user, String skillsName) throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(user.getEmailId());
		emailVO.setSubject(EmailConstants.WORKSHOP_REQUEST_SUCCESS_SUB);
		emailVO.setTemplateName(EmailConstants.WORSHOP_REQUEST_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, user.getFirstName());
		variables.put(SKILL_NAME, skillsName);
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendSkillRequestMail(List<UserVO> userVO, Integer skillId, Long count) throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		Map<Integer, SkillVO> userSkills = userVO.stream().findAny().orElse(new UserVO()).getSkills().stream().collect(Collectors.toMap(SkillVO::getSkillId, skill -> skill));
		List<String> userEmails = userVO.stream().map(UserVO::getEmailId).toList();
		emailVO.setSendMultipleBCCTo(userEmails);
		emailVO.setSubject(EmailConstants.USER_SKILL_REQUEST+" : "+userSkills.get(skillId).getSkillName());
		emailVO.setTemplateName(EmailConstants.USER_SKILL_REQUEST_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(SKILL_NAME, userSkills.get(skillId).getSkillName());
		variables.put("count", String.valueOf(count));
		emailVO.setVariables(variables);
		email.sendEMailMultipleBCC(emailVO);
	}

}
