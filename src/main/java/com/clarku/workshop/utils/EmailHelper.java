package com.clarku.workshop.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.vo.EmailVO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailHelper {

	private static final Logger log = LoggerFactory.getLogger(EmailHelper.class);

	@Autowired
	JavaMailSenderImpl mailer;

	@Autowired
	TemplateEngine templateEngine;

	public void sendEMail(EmailVO emailVO) throws GlobalException, EmailException {
		MimeMessage mimeMessage = mailer.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
		Context context = new Context();
		if (!emailVO.getVariables().isEmpty()) {
			emailVO.getVariables().forEach((var, val) -> context.setVariable(var, val));
		}

		try {
			helper.setTo(emailVO.getSendTo());
			helper.setSubject(emailVO.getSubject());
			String htmlContent = templateEngine.process(emailVO.getTemplateName(), context);
			helper.setText(htmlContent, true);
			mailer.send(mimeMessage);
		} catch (MessagingException exp) {
			log.error("Failed to send the Notification" + exp.getMessage());
			throw new EmailException("Failed to Send Email, Please try again", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception exp) {
			log.error("Failed to send the Notification" + exp.getMessage());
			throw new GlobalException("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
