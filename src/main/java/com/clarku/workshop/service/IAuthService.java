package com.clarku.workshop.service;

import org.springframework.http.HttpHeaders;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.vo.SessionVO;

public interface IAuthService {
	
	SessionVO retrieveSession(HttpHeaders headers) throws GlobalException;

}
