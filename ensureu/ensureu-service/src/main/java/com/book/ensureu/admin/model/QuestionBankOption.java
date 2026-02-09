package com.book.ensureu.admin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBankOption {
    private String key;          // A, B, C, D
    private String value;        // Option text (HTML supported)
    private String valueHindi;   // Hindi translation (optional)
}
