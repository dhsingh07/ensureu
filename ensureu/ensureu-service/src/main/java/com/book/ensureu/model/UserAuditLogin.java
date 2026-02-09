package com.book.ensureu.model;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@Builder
@Document(collection="userAuditLogin")
public class UserAuditLogin implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4543946818806681594L;
	
	private Long id;
	private String userId;
	private String ipAddress;
	private String country;
	private String city;
	private String regione;
	private Long createDate;
	private Long modifiedDate;
	
}
