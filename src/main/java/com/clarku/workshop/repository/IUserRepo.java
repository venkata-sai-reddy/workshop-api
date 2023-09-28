package com.clarku.workshop.repository;

import com.clarku.workshop.exception.GlobalException;

public interface IUserRepo {

	String getUserFirstName(Integer userId) throws GlobalException;

}
