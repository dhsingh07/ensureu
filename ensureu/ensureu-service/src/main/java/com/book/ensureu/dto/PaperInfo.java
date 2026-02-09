package com.book.ensureu.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PaperInfo {
    private String id;
    private String paperName;
    private Long createdDate;
    private Long validity;

}
