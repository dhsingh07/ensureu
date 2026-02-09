package com.book.ensureu.admin.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class SimpleQuestionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String questionText;
    private String questionImage;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int correctOption;
    private String explanation;
}
