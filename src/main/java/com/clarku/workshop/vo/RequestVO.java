package com.clarku.workshop.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RequestVO {
	
	private Integer skillId;

	private String skillName;

	private String requestedUser;

	private Integer requestedUserId;

	private LocalDateTime requestedDate;
	
}
