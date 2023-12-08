package com.clarku.workshop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.repository.IAdminRepo;
import com.clarku.workshop.vo.VenueVO;

@Service
public class AdminServiceImpl implements IAdminService{

	@Autowired
	IAdminRepo adminRepo;

	@Override
	public List<VenueVO> getVenues() throws GlobalException {
		return adminRepo.retrieveVenues();
	}

}
