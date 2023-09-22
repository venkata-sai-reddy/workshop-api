package com.clarku.workshop.vo;

import java.util.HashMap;

import lombok.Data;

@Data
public class EmailVO {

	private String sendTo;

	private String subject;

	private String templateName;

	private HashMap<String, String> variables;

}
