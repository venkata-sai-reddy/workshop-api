package com.clarku.workshop.vo;

import java.util.List;

import lombok.Data;

@Data
public class WorkshopsTimeLineVO {

	private List<WorkshopVO> pastWorkshops;

	private List<WorkshopVO> onGoingWorkshops;

	private List<WorkshopVO> upComingWorkshops;

}
