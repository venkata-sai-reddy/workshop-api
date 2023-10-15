package com.clarku.workshop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.repository.ISessionRepo;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.vo.SessionVO;

@Service
public class AuthServiceImpl implements IAuthService {

	@Autowired
	ISessionRepo sessionRepo;

	@Override
	public SessionVO retrieveSession(HttpHeaders headers) throws GlobalException {
		List<String> sessionId = headers.get(Constants.HEADER_SESSION_ID);
		if(sessionId == null || sessionId.isEmpty()) {
			throw new GlobalException("Invalid Session, Please login", HttpStatus.UNAUTHORIZED);
		}
		SessionVO session = sessionRepo.retrieveSession(Integer.parseInt(sessionId.get(0)));
		if(session == null || Boolean.TRUE.equals(ObjectUtils.isEmpty(session)) ) {
			throw new GlobalException("Invalid Session", HttpStatus.UNAUTHORIZED);
		}
		return session;
	}

}
