/**
 * 
 */
package com.clarku.workshop.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RegisteredUserVO {

	private Integer userId;

	private String firstName;

	private String lastName;

	private String emailId;

	private String userType;
	
	private LocalDateTime registeredDate;
}
