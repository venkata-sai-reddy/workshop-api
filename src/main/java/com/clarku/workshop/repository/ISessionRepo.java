package com.clarku.workshop.repository;

import com.clarku.workshop.exception.GlobalException;
import com.clarku.workshop.vo.SessionVO;

public interface ISessionRepo {

	SessionVO retrieveSessionDetails(Integer userId) throws GlobalException;

	void createSession(Integer userId) throws GlobalException;

	Boolean isActiveSessionExists(Integer userId) throws GlobalException;

	void endSession(Integer sessionId) throws GlobalException;

	void updateSession(Integer sessionId) throws GlobalException;

	Boolean isSessionActive(Integer sessionId) throws GlobalException;

}
