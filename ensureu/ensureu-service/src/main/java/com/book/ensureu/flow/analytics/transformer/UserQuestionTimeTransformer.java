package com.book.ensureu.flow.analytics.transformer;

import com.book.ensureu.flow.analytics.dto.UserQuestionTimeDto;
import com.book.ensureu.flow.analytics.model.UserQuestionTime;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserQuestionTimeTransformer implements Transformer<UserQuestionTime, UserQuestionTimeDto> {
    @Override
    public UserQuestionTime toModel(UserQuestionTimeDto from) {
        return UserQuestionTime.builder()
                .questionAttemptedStatus(from.getQuestionAttemptedStatus())
                .questionId(from.getQuestionId())
                .questionNo(from.getQuestionNo())
                .timeTaken(from.getTimeTaken())
                .build();
    }

    @Override
    public List<UserQuestionTime> toModel(List<UserQuestionTimeDto> from) {
        return from.stream().map(obj -> toModel(obj)).collect(Collectors.toList());
    }

    @Override
    public UserQuestionTimeDto ToDto(UserQuestionTime from) {
      return UserQuestionTimeDto.builder()
              .questionId(from.getQuestionId())
              .timeTaken(from.getTimeTaken())
              .questionNo(from.getQuestionNo())
              .questionAttemptedStatus(from.getQuestionAttemptedStatus())
              //. marks(from.get) TODO need to add
              .build();
    }

    @Override
    public List<UserQuestionTimeDto> toDto(List<UserQuestionTime> from) {
        return from.stream().map(obj -> ToDto(obj)).collect(Collectors.toList());
    }
}
