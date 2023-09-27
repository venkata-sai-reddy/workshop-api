package com.clarku.workshop.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SessionVO {

	private Integer sessionId;

	private Integer userId;

	private LocalDateTime sessionStartTime;

	private LocalDateTime sessionEndTime;

	private Integer extendCount;

	private Boolean active;
}
