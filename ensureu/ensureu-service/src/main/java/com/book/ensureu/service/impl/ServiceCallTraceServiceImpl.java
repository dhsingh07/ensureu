package com.book.ensureu.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.book.ensureu.constant.ServiceName;
import com.book.ensureu.model.ServiceCallTrace;
import com.book.ensureu.repository.ServiceCallTraceRepository;
import com.book.ensureu.service.ServiceCallTraceService;
import com.book.ensureu.util.HashUtil;

@Service
public class ServiceCallTraceServiceImpl implements ServiceCallTraceService {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ServiceCallTraceServiceImpl.class);

	@Autowired
	private ServiceCallTraceRepository serviceCallTraceRepository;

	@Override
	public void saveServceCallTrace(ServiceCallTrace serviceCallTrace) {

		Optional<ServiceCallTrace> serviceCallTraceDbOptional = null;
		if (serviceCallTrace != null) {
			ServiceName serviceName = serviceCallTrace.getServiceName();
			String userName = serviceCallTrace.getUsername();
			String id = HashUtil.hashByMD5(serviceName.toString(), userName);
			if (serviceCallTrace.getId() != null) {
				LOGGER.info("Save id "+serviceCallTrace.getId());
				if (id.equals(serviceCallTrace.getId())) {
					serviceCallTraceDbOptional = serviceCallTraceRepository.findById(serviceCallTrace.getId());
					if (serviceCallTraceDbOptional.isPresent()) {
						ServiceCallTrace serviceCallTraceDb = serviceCallTraceDbOptional.get();
						int count=serviceCallTraceDb.getServiceCallCount();
						int limitCount=serviceCallTraceDb.getServiceCallLimit();
						if(limitCount<=count) {
							throw new IllegalAccessError("Attempt limit exceeded, please try after some time");
						}
						serviceCallTrace.setExpirySeviceCallTime(LocalDateTime.now());
						serviceCallTrace.setModifiedDateTime(LocalDateTime.now());
						serviceCallTrace.setServiceCallCount(count + 1);
					}else {
						serviceCallTrace.setId(id);
						serviceCallTrace.setCreateDateTime(LocalDateTime.now());
						serviceCallTrace.setExpirySeviceCallTime(LocalDateTime.now());
						serviceCallTrace.setModifiedDateTime(LocalDateTime.now());
						serviceCallTrace.setServiceCallCount(1);
					}
					serviceCallTraceRepository.save(serviceCallTrace);
					LOGGER.info("Sucessfully save");
				} else {
					throw new IllegalAccessError("Some thing wrong in request");
				}

			} /*else {
				serviceCallTrace.setId(id);
				serviceCallTrace.setCreateDateTime(LocalDateTime.now());
				serviceCallTrace.setExpirySeviceCallTime(LocalDateTime.now());
				serviceCallTrace.setModifiedDateTime(LocalDateTime.now());
				serviceCallTrace.setServiceCallCount(1);
			}*/
			
		}

	}

	@Override
	public ServiceCallTrace getServceCallTraceById(String id) {
		Optional<ServiceCallTrace> serviceCallTraceDbOptional = serviceCallTraceRepository.findById(id);
		ServiceCallTrace serviceCallTrace = null;
		if (serviceCallTraceDbOptional.isPresent()) {
			serviceCallTrace = serviceCallTraceDbOptional.get();
		}
		return serviceCallTrace;
	}

	@Override
	public void deleteServceCallTraceById(String id) {
		serviceCallTraceRepository.deleteById(id);
	}

}
