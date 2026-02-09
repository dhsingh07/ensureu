package com.ensureu.commons.notification.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ensureu.commons.notification.data.Employee;
import com.ensureu.commons.notification.service.SampleEmployeeService;

@Service
public class SampleEmployeeServiceImpl implements SampleEmployeeService {

	private static final Logger LOGGER=org.slf4j.LoggerFactory.getLogger(SampleEmployeeService.class);

	@Autowired
	private RestTemplate restTemplate;
	
	String uri="http://dummy.restapiexample.com/api/v1/employee/";
	
	@Override
	public CompletableFuture<String> findEmployee(String id) {
		String url=String.format(uri+"%S", id);
		System.out.println("url "+url);
		String employee=restTemplate.getForObject(url, String.class);
		System.out.println("emp   "+employee);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(employee);
	}

	@Override
	public CompletableFuture<List<String>> findAllEmployee() {
		return null;
	}

	@Override
	public void createEmployee(Employee employee) {
		
	}

	@Override
	public void updateEmployee(Employee employee) {
		
	}

	@Override
	public void deleteEmployee(String id) {
		
	}
	
	

}
