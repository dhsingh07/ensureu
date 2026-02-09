package com.book.ensureu.model;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaperHierarchy {

    private String id;

    private PaperType paperType;

    private TestType testType;

    private PaperCategory paperCategory;

    private PaperSubCategory paperSubCategory;

}
