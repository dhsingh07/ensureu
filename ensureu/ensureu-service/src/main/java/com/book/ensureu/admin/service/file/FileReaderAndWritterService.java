package com.book.ensureu.admin.service.file;

import java.util.List;

public interface FileReaderAndWritterService {

	public <T> List<T> readFile(T clazz);
	
	public <T> List<T> readFile(String files,T clazz);

	public void writeFile(String path);
}
