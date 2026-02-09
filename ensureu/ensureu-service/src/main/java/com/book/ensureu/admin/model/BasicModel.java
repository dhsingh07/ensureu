package com.book.ensureu.admin.model;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BasicModel {

	@Id
	private String id;
	private String createdBy;
	private String modifiedBy;
	private Long createdDate;
	private Long modifiedDate;
}
