package com.clarku.workshop.vo;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkshopVO {

	@NotNull(message = "Please choose a valid workshop", groups = { WorkshopUpdateValidation.class })
	private Integer workshopId;

	@NotBlank(message = "Please enter a valid workshop name", groups = { WorkshopAddValidation.class })
	private String workshopName;

	private String description;

	private String venue;

	private String meetingURL;

	private Integer capacity;

	private Integer enrollCount;

	private Integer createdUserId;

	private String createdUser;

	private LocalDateTime createdDate;
	
	@NotBlank(message = "Please enter a valid workshop name", groups = { WorkshopAddValidation.class })
	private String workshopDate;
	
	@NotNull(message = "Please enter a valid workshop name", groups = { WorkshopAddValidation.class })
	private LocalDateTime startTime;
	
	@NotNull(message = "Please enter a valid workshop name", groups = { WorkshopAddValidation.class })
	private LocalDateTime endTime;

	private List<SkillVO> selectedSkills;

	private List<RegisteredUserVO> registeredUsers;

	private Boolean isUserEnrolled;

	public interface WorkshopAddValidation {
	}

	public interface WorkshopUpdateValidation {
	}

}
