package com.clarku.workshop.service;

import com.clarku.workshop.exception.EmailException;
import com.clarku.workshop.exception.GlobalException;

public interface IUserService {

	Boolean resetPassword(String emailId) throws GlobalException, EmailException;

}
