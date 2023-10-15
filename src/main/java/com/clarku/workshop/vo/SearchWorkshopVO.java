package com.clarku.workshop.vo;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SearchWorkshopVO {

	private String workshopName;

	private String skill;

	private LocalDate fromDate;

	private LocalDate toDate;

	private String instructor;

}
