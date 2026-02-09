package com.book.ensureu.aop;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.book.ensureu.model.ProviderOauthToken;
import com.book.ensureu.model.UserAuditLogin;
import com.book.ensureu.security.JwtAuthenticationRequest;
import com.book.ensureu.service.audit.UserAuditLoginService;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class UserAuditLoginAspect {

@Autowired
private UserAuditLoginService userAuditLoginService;

@Around("@annotation(com.book.ensureu.annotation.UserAuditLogin) and args(jwtAuthenticationRequest)")
public Object userAuditLoginService(ProceedingJoinPoint proceedingJoinPoint, JwtAuthenticationRequest jwtAuthenticationRequest) {
	UserAuditLogin userAuditLogin=UserAuditLogin.builder()
			.userId(jwtAuthenticationRequest.getUsername())
			.ipAddress(jwtAuthenticationRequest.getIpAddress())
			.country(jwtAuthenticationRequest.getCountry())
			.city(jwtAuthenticationRequest.getCity())
			.regione(jwtAuthenticationRequest.getRegion())
			.createDate(new Date().getTime())
			.modifiedDate(new Date().getTime())
			.build();
	userAuditLoginService.saveUserAuditLogin(userAuditLogin);
	log.info("save is done for Ensureu provider audit login.");
	Object object = null;
	try {
		object = proceedingJoinPoint.proceed();
	} catch (Throwable e) {
		log.error("Proceeding join point ", e);
	}
	return object;
}

@Around("@annotation(com.book.ensureu.annotation.UserAuditLogin) and args(providerOauthTokenRequest)")
public Object userAuditLoginService(ProceedingJoinPoint proceedingJoinPoint, ProviderOauthToken providerOauthTokenRequest) {
	UserAuditLogin userAuditLogin=UserAuditLogin.builder()
			.userId(providerOauthTokenRequest.getUsername())
			.ipAddress(providerOauthTokenRequest.getIpAddress())
			.country(providerOauthTokenRequest.getCountry())
			.city(providerOauthTokenRequest.getCity())
			.regione(providerOauthTokenRequest.getRegion())
			.createDate(new Date().getTime())
			.modifiedDate(new Date().getTime())
			.build();
	userAuditLoginService.saveUserAuditLogin(userAuditLogin);
	log.info("save is done for poviderUser(Google,Facwbook) audit login.");
	Object object = null;
	try {
		object = proceedingJoinPoint.proceed();
	} catch (Throwable e) {
		log.error("Proceeding join point ", e);
	}
	return object;
}
	
}
