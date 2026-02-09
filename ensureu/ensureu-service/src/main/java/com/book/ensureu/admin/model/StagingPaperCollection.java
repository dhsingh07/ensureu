package com.book.ensureu.admin.model;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Document(collection="stagingPaperCollection")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StagingPaperCollection<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8011261860136620777L;
	
	private String id;
	private PaperType paperType;
	private PaperSubCategory paperSubCategory;
	private PaperCategory paperCategory;
	private TestType testType;
	private PaperStateStatus paperStateStatus;
	private T paperCollection;
	private Long createdDate;
	private Long modifiedDate;
	private String createdBy;
	private String modifiedBy;
	

}
