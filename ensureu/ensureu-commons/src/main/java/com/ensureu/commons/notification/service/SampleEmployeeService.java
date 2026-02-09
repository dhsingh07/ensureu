package com.ensureu.commons.notification.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.ensureu.commons.notification.data.Employee;

public interface SampleEmployeeService {

	public CompletableFuture<String> findEmployee(String id);
	public CompletableFuture<List<String>> findAllEmployee();
	public void createEmployee(Employee employee);
	public void updateEmployee(Employee  employee);
	public void deleteEmployee(String id);
	
}
