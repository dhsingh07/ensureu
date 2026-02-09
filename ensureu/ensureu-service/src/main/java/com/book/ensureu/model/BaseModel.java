package com.book.ensureu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class BaseModel {
private String createdBy;
private String modifiedBy;
private Long createDate;
private Long modifiedDate;



}
