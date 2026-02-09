package com.book.ensureu.service;

import com.book.ensureu.constant.CounterEnum;
/**
 * @author dharmendra.singh
 *
 */
public interface CounterService {
	
	Long increment(CounterEnum counterEnum);

	boolean createandInitialize();

}
