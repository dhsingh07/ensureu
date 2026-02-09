package com.book.ensureu.admin.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBankProblem {
    private String question;         // Question text (HTML supported)
    private String questionHindi;    // Hindi translation (optional)
    private List<QuestionBankOption> options;
    private String correctOption;    // "A", "B", "C", or "D"
    private String solution;         // Explanation (HTML supported)
    private String solutionHindi;    // Hindi translation (optional)
}
