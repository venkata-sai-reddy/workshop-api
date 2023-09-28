package com.clarku.workshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.repository.ISessionRepo;
import com.clarku.workshop.utils.Constants;
import com.clarku.workshop.vo.SessionVO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SessionServiceImpl implements ISessionService {

	@Autowired
	ISessionRepo sessionRepo;

	@Override
	public SessionVO createSession(Integer userId) throws GlobalException {
		if (Boolean.TRUE.equals(isSessionExists(userId))) {
			log.error("SessionServiceImpl :: createSession(): Session already exists.");
			throw new GlobalException(Constants.SESSION_ALREADY_EXISTS_EXP, HttpStatus.UNAUTHORIZED);
		}
		sessionRepo.createSession(userId);
		return getSession(userId);
	}

	@Override
	public SessionVO getSession(Integer userId) throws GlobalException {
		return sessionRepo.retrieveSessionDetails(userId);
	}

	@Override
	public void endSession(Integer sessionId) throws GlobalException {
		if (Boolean.FALSE.equals(sessionRepo.isSessionActive(sessionId))) {
			log.error("SessionServiceImpl :: endSession(): No Active Session Exists.");
			throw new GlobalException(Constants.INVALID_SESSION_EXP, HttpStatus.UNAUTHORIZED);
		}
		sessionRepo.endSession(sessionId);
	}

	@Override
	public void extendSession(Integer sessionId) throws GlobalException {
		sessionRepo.updateSession(sessionId);
	}

	@Override
	public Boolean isSessionExists(Integer userId) throws GlobalException {
		return sessionRepo.isActiveSessionExists(userId);
	}

}
