package com.book.ensureu.flow.analytics.transformer;

import com.book.ensureu.flow.analytics.dto.UserQuestionStatDto;
import com.book.ensureu.flow.analytics.model.UserQuestionStat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserQuestionStatTransformer implements Transformer<UserQuestionStat, UserQuestionStatDto> {

    @Override
    public UserQuestionStat toModel(UserQuestionStatDto from) {
        return UserQuestionStat.builder()
                .questionId(from.getQuestionId())
                .timeTaken(from.getTimeTaken())
                .questionAttemptedStatus(from.getQuestionAttemptedStatus())
                .marks(from.getMarks())
                .section(from.getSection())
                .subSection(from.getSubSection())
                .complexityLevel(from.getComplexityLevel())
                .type(from.getType())
                .marks((null!=from.getMarks()) ? from.getMarks() :0)
                .build();
    }

    @Override
    public List<UserQuestionStat> toModel(List<UserQuestionStatDto> from) {
        return from.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public UserQuestionStatDto ToDto(UserQuestionStat from) {
        return null;
    }

    @Override
    public List<UserQuestionStatDto> toDto(List<UserQuestionStat> from) {
        return null;
    }
}
