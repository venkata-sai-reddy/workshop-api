package com.clarku.workshop.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginVO {

	private Integer userId;

	@Email(message = "Email should be valid", groups = { LoginValidation.class })
	@NotNull(message = "Email should be valid", groups = { LoginValidation.class })
	private String emailId;

	@NotEmpty(message = "Password should not be empty", groups = { LoginValidation.class })
	private String password;
	
	private String tempPassword;

	private Boolean isAdmin;

	private Integer failedLoginAttempts;

	private Boolean isLocked;

	public interface LoginValidation {

	}

}