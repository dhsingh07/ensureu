package com.book.ensureu.service.audit.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.book.ensureu.constant.CounterEnum;
import com.book.ensureu.model.UserAuditLogin;
import com.book.ensureu.repository.UserAuditLoginReposiotry;
import com.book.ensureu.service.CounterService;
import com.book.ensureu.service.audit.UserAuditLoginService;

@Service
public class UserAuditLoginServiceImpl implements UserAuditLoginService {

	@Autowired
	@Lazy
	private UserAuditLoginReposiotry userAuditLoginReposiotry;

	@Autowired
	private CounterService counterService;

	@Override
	public void saveUserAuditLogin(UserAuditLogin userAuditLogin) {
		userAuditLogin.setId(counterService.increment(CounterEnum.USERAUDIT));
		userAuditLoginReposiotry.save(userAuditLogin);
	}

	@Override
	public List<UserAuditLogin> getByUserId(String userId) {
		return userAuditLoginReposiotry.findByUserId(userId);
	}

}
