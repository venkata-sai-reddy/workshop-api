package com.clarku.workshop.service;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.vo.SessionVO;

public interface ISessionService {

	SessionVO createSession(Integer userId) throws GlobalException;

	SessionVO getSession(Integer userId) throws GlobalException;

	void endSession(Integer sessionId) throws GlobalException;

	void extendSession(Integer sessionId) throws GlobalException;

	Boolean isSessionExists(Integer userId) throws GlobalException;

}
