package com.clarku.workshop.service;

import java.util.List;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.vo.VenueVO;

public interface IAdminService {

	List<VenueVO> getVenues() throws GlobalException;

}
