package com.clarku.workshop.vo;

import java.util.List;

import lombok.Data;

@Data
public class CustomMessageVO {

	private String firstName;

	private String userId;

	private String subject;

	private String message;

	private List<String> sendTo;

	private Integer workshopId;
}
