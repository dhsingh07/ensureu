package com.book.ensureu.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.book.ensureu.constant.ServiceName;
import com.book.ensureu.constant.UserLoginType;
import com.book.ensureu.dto.UserOtpDto;
import com.book.ensureu.model.ServiceCallTrace;
import com.book.ensureu.service.ServiceCallTraceService;
import com.book.ensureu.util.HashUtil;

@Aspect
@Component
public class ServiceCallLimitAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCallLimitAspect.class);
	@Autowired
	private ServiceCallTraceService serviceCallTraceService;

	@Around("@annotation(com.book.ensureu.annotation.ServiceCallLimit) and args(userOtpDto)")
	public Object serviceCallLimit(ProceedingJoinPoint joinPoint, UserOtpDto userOtpDto) throws Throwable {

		long start = System.currentTimeMillis();
		System.out.println("userOtpDto  " + userOtpDto);
		System.out.println("Start time " + start);

		LOGGER.info("userOtpDto  " + userOtpDto);

		String userName = userOtpDto.getUserName();
		ServiceName serviceName = ServiceName.OTPSERVICE;
		String id = HashUtil.hashByMD5(serviceName.toString(), userName);
		int callCount = 0;
		ServiceCallTrace serviceCallTraceDb = serviceCallTraceService
				.getServceCallTraceById(HashUtil.hashByMD5(serviceName.toString(), userName));
		if (serviceCallTraceDb != null) {
			int callLimit = serviceCallTraceDb.getServiceCallLimit();
			callCount = serviceCallTraceDb.getServiceCallCount();
			if (callLimit <= callCount) {
				LOGGER.info("Attempt limit exceeded, please try after some time  ");
				throw new IllegalAccessError("Attempt limit exceeded, please try after some time");
			}
		}

		ServiceCallTrace serviceCallTrace = new ServiceCallTrace(id, userOtpDto.getUserName(), serviceName, callCount,
				3, UserLoginType.SIGNUP);
		serviceCallTraceService.saveServceCallTrace(serviceCallTrace);

		LOGGER.info("Save done.... ");
		Object proceed = joinPoint.proceed();
		long executionTime = System.currentTimeMillis() - start;
		LOGGER.info(joinPoint.getSignature() + " Executed in " + executionTime + "ms");
		return proceed;
	}

}
