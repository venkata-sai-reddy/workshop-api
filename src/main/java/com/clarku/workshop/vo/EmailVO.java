package com.clarku.workshop.vo;

import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class EmailVO {

	private String sendTo;

	private String sendBCCTo;

	private String sendCCTo;

	private List<String> sendMultipeTo;

	private List<String> sendMultipleBCCTo;

	private List<String> sendMultipleCCTo;

	private String subject;

	private String templateName;

	private HashMap<String, String> variables;

}
