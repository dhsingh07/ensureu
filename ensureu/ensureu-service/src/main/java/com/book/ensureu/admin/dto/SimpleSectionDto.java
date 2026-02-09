package com.book.ensureu.admin.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class SimpleSectionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private double perQuestionMarks;
    private double negativeMarks;
    private List<SimpleQuestionDto> questions;
}
