package com.book.ensureu.service;

import com.book.ensureu.model.ServiceCallTrace;

public interface ServiceCallTraceService {

	public void saveServceCallTrace(ServiceCallTrace serviceCallTrace);

	public ServiceCallTrace getServceCallTraceById(String id);

	public void deleteServceCallTraceById(String id);
}
