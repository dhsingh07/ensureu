package com.book.ensureu.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.book.ensureu.constant.PaperType;
import com.book.ensureu.model.VaultModel;

@Repository
public interface VaultRespository extends MongoRepository<VaultModel, String> {

	public List<VaultModel> findByUserNameOrderByCreatedDateDesc(String userName);
	public List<VaultModel> findByUserNameAndPaperType(String userName,PaperType paperType);
	public void deleteByUserNameAndQuestionId(String userName,String questionId);
	
}
