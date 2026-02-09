package com.book.ensureu.service.audit;

import java.util.List;

import com.book.ensureu.model.UserAuditLogin;

public interface UserAuditLoginService {

	public void saveUserAuditLogin(UserAuditLogin userAuditLogin);
	public List<UserAuditLogin> getByUserId(String userId);
	
}
