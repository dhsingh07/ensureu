package com.book.ensureu.common.model;

import lombok.Data;

import java.util.Date;

@Data
public class AuditData {

    private Date createdDate;
    private Date lastModified;
    private String userId;

}
