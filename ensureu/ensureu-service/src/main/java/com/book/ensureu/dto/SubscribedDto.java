package com.book.ensureu.dto;

import java.util.List;

import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.SubscriptionType;
import com.book.ensureu.constant.TestType;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SubscribedDto {

    private Long id;

    private SubscriptionType subscriptionType;

    private PaperType paperType;

    private TestType testType;

    private PaperCategory paperCategory;

    private PaperSubCategory paperSubCategory;

    private String description;

    private Long validity;

    private List<String> paperIds;

    private List<PaperInfo> listOfPaperInfo;

    private List<Long> listOfSubscriptionIds;

    private Long createdDate;

}
