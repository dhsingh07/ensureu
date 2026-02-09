package com.book.ensureu.admin.dto;

import java.io.Serializable;

import com.book.ensureu.constant.RoleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleAssignmentDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private RoleType roleType;
}
