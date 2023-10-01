/**
 * 
 */
package com.clarku.workshop.vo;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignUpVO {

	@NotNull(message = "First name should be provided", groups = { SignUpValidation.class })
	@Pattern(regexp = "^(?=.*[A-Z])[A-Za-z][A-Za-z. ]{1,20}$", message = "Enter Valid First Name", groups = { SignUpValidation.class })
	private String firstName;

	@NotNull(message = "Last name should be provided", groups = { SignUpValidation.class })
	@Pattern(regexp = "^(?=.*[A-Z])[A-Za-z][A-Za-z. ]{1,20}$", message = "Enter Valid Last Name", groups = { SignUpValidation.class })
	private String lastName;

	@Email(message = "Email should be valid", groups = { SignUpValidation.class })
	@NotNull(message = "Email should be valid", groups = { SignUpValidation.class })
	private String emailId;

	@NotNull(message = "Phone number should be provided", groups = { SignUpValidation.class })
	@Pattern(regexp = "^[1-9][0-9]{9}$", message = "Phone number should match requirements", groups = { SignUpValidation.class })
	private String phoneNumber;

	@NotNull(message = "user type should be selected", groups = { SignUpValidation.class })
	private String userType;

	private List<SkillVO> existingSkills;

	private List<SkillVO> newSkills;

	@NotEmpty(message = "Password should not be empty", groups = { SignUpValidation.class })
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z0-9-+!@#$&*_.?]{8,20}$", message = "Password should match requirements", groups = { SignUpValidation.class })
	private String createPassword;

	@NotEmpty(message = "Password should not be empty", groups = { SignUpValidation.class })
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z0-9-+!@#$&*_.?]{8,20}$", message = "Password should match requirements", groups = { SignUpValidation.class })
	private String confirmPassword;

	public interface SignUpValidation {
	}
}
