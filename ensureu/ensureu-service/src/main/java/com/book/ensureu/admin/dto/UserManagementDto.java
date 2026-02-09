package com.book.ensureu.admin.dto;

import java.io.Serializable;
import java.util.List;

import com.book.ensureu.constant.RoleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserManagementDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String userName;
    private String emailId;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private List<RoleType> roleTypes;
    private Long createDate;
    private Long modifiedDate;
}
