package com.clarku.workshop.repository;

import java.util.List;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.vo.VenueVO;

public interface IAdminRepo {

	List<VenueVO> retrieveVenues() throws GlobalException;

}
