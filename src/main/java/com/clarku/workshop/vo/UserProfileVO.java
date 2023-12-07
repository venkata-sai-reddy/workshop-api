package com.clarku.workshop.vo;

import java.util.List;

import lombok.Data;

@Data
public class UserProfileVO {
	
	private Integer userId;

	private String firstName;

	private String lastName;

	private String emailId;

	private String phoneNumber;

	private String userType;

	private List<SkillVO> skills;

	private List<SkillVO> newSkills;
	
	private Boolean isActive;
	
	private Boolean isLocked;

}
