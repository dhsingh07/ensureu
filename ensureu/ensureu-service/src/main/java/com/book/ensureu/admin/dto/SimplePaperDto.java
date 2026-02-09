package com.book.ensureu.admin.dto;

import java.io.Serializable;
import java.util.List;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.TestType;

import lombok.Data;

@Data
public class SimplePaperDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String paperName;
    private PaperCategory paperCategory;
    private PaperSubCategory paperSubCategory;
    private TestType testType;
    private PaperStateStatus paperStateStatus;
    private long totalTimeInSeconds;
    private double perQuestionScore;
    private double negativeMarks;

    // Flat mode: all questions in a single default section
    private List<SimpleQuestionDto> questions;

    // Sectioned mode: questions grouped by sections
    private List<SimpleSectionDto> sections;
}
