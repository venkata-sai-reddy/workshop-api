/**
 * 
 */
package com.clarku.workshop.vo;

import java.util.List;

import lombok.Data;

@Data
public class UserVO {

	private Integer userId;

	private String firstName;

	private String lastName;

	private String emailId;

	private String phoneNumber;

	private String userType;

	private Boolean isTempPass;

	private List<SkillVO> skills;

	private List<SkillVO> newSkills;

	private SessionVO session;
}
