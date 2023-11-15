package com.clarku.workshop.vo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePassVO {

	@NotEmpty(message = "Password should not be empty", groups = { ChangePassValidation.class })
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$&*-_.?])[A-Za-z0-9!@#$&*-_.?]{8,20}$", message = "Current Password should match requirements", groups = { ChangePassValidation.class })
	private String currentPassword;

	@NotEmpty(message = "Password should not be empty", groups = { ChangePassValidation.class })
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$&*-_.?])[A-Za-z0-9!@#$&*-_.?]{8,20}$", message = "Create Password should match requirements", groups = { ChangePassValidation.class })
	private String createPassword;

	@NotEmpty(message = "Password should not be empty", groups = { ChangePassValidation.class })
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$&*-_.?])[A-Za-z0-9!@#$&*-_.?]{8,20}$", message = "Create Password should match requirements", groups = { ChangePassValidation.class })
	private String confirmPassword;

	public interface ChangePassValidation {
		
	}
}
