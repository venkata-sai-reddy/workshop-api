package com.clarku.workshop.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.utils.EmailConstants;
import com.clarku.workshop.utils.EmailHelper;
import com.clarku.workshop.vo.CustomMessageVO;
import com.clarku.workshop.vo.EmailVO;
import com.clarku.workshop.vo.RegisteredUserVO;
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
		variables.put("emailId", userDetails.getEmailId());
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

	@Override
	public void sendUnEnrollSuccessEmail(WorkshopVO workshop, UserVO user) throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(user.getEmailId());
		emailVO.setSubject(EmailConstants.WORKSHOP_SUCCESS_UNENROLL_SUB);
		emailVO.setTemplateName(EmailConstants.WORSHOP_UNENROLL_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, user.getFirstName());
		variables.put(WORKSHOP_NAME, workshop.getWorkshopName());
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendPasswordChangeEmail(String emailId, String firstName) throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(emailId);
		emailVO.setSubject(EmailConstants.USER_PASS_CHANGED_SUB);
		emailVO.setTemplateName(EmailConstants.USER_PASS_CHANGE_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, firstName);
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendProfileUpdateEmail(String emailId, String firstName) throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(emailId);
		emailVO.setSubject(EmailConstants.USER_PROFILE_CHANGED_SUB);
		emailVO.setTemplateName(EmailConstants.USER_PROFILE_CHANGE_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, firstName);
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendCustomMessageToUsers(UserVO user, CustomMessageVO messageVO)
			throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendMultipleBCCTo(messageVO.getSendTo());
		emailVO.setSubject(messageVO.getSubject());
		emailVO.setTemplateName(EmailConstants.CUSTOM_MESSAGE_NOTIFCATION);
		HashMap<String, String> variables = new HashMap<>();
		variables.put("message", messageVO.getMessage());
		variables.put("userName", user.getFirstName());
		emailVO.setVariables(variables);
		email.sendEMailMultipleBCC(emailVO);
		
	}

	@Override
	public void sendSuccessCreateUserMail(SignUpVO createdUser) throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(createdUser.getEmailId());
		emailVO.setSubject(EmailConstants.WELCOME_SUBJECT);
		emailVO.setTemplateName(createdUser.getUserType().equalsIgnoreCase(Constants.INSTRUCTOR) ? EmailConstants.SIGNUP_INSTRUCTOT_SUCCESS_TEMPLATE : EmailConstants.SIGNUP_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, createdUser.getFirstName());
		variables.put("emailId", createdUser.getEmailId());
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendSuccessTemporaryPassword(SignUpVO createdUser) throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		emailVO.setSendTo(createdUser.getEmailId());
		emailVO.setSubject(EmailConstants.TEMP_PASS_SUBJECT);
		emailVO.setTemplateName(EmailConstants.TEMP_PASS_SUCCESS_TEMPLATE);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(FIRST_NAME, createdUser.getFirstName());
		variables.put("tempPass", createdUser.getCreatePassword());
		emailVO.setVariables(variables);
		email.sendEMail(emailVO);
	}

	@Override
	public void sendUpcomingWorkshopNotification(WorkshopVO workshop, int days) throws GlobalException, EmailException {
		EmailVO emailVO = new EmailVO();
		List<String> userEmails = workshop.getRegisteredUsers().stream().map(RegisteredUserVO::getEmailId).toList();
		emailVO.setSendMultipleBCCTo(userEmails);
		emailVO.setSubject(EmailConstants.WORKSHOP_NOTIFICATION_SUB + workshop.getWorkshopName()+ " in "+String.valueOf(days));
		emailVO.setTemplateName(EmailConstants.WORKSHOP_NOTIFICATION_TEMP);
		HashMap<String, String> variables = new HashMap<>();
		variables.put(WORKSHOP_NAME, workshop.getWorkshopName());
		variables.put("days", String.valueOf(days));
		variables.put("instructor", workshop.getCreatedUser());
		variables.put("workshopDate", workshop.getWorkshopDate());
		variables.put("venue", workshop.getVenue());
		variables.put("startTime", String.valueOf(workshop.getStartTime().getHour()) + " : "+String.valueOf(workshop.getStartTime().getMinute()));
		variables.put("meetingUrl", workshop.getMeetingURL());
		emailVO.setVariables(variables);
		email.sendEMailMultipleBCC(emailVO);
	}

}
