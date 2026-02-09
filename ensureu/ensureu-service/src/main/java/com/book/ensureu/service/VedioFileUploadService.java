package com.book.ensureu.service;

import java.io.InputStream;

public interface VedioFileUploadService {

	public void uploadVedioFile(InputStream vedioFile) throws Exception;
	public void deletVedioFile(String id) throws Exception; 
	
	
}
