package com.book.ensureu.flow.analytics.transformer;

import com.book.ensureu.flow.analytics.dto.UserPaperStatDto;
import com.book.ensureu.flow.analytics.model.UserPaperStat;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class UserPaperStatTransformer implements Transformer<UserPaperStat, UserPaperStatDto> {

    private UserQuestionStatTransformer userQuestionStatTransformer;

    @Override
    public UserPaperStat toModel(UserPaperStatDto from) {

        return UserPaperStat.builder()
                .id(from.getId())
                .completed(from.isCompleted())
                .createdAt(from.getCreatedAt())
                .freeze(from.isFreeze())
                .maxPossibleScore(from.getMaxPossibleScore())
                .testType(from.getTestType())
                .paperCategory(from.getPaperCategory())
                .paperHierarchy(from.getPaperHierarchy())
                .paperId(from.getPaperId())
                .paperName(from.getPaperName())
                .totalAttempted(from.getTotalAttempted())
                .totalCorrect(from.getTotalCorrect())
                .totalQuestions(from.getTotalQuestions())
                .totalScore(from.getTotalScore())
                .totalSkipped(from.getTotalSkipped())
                .totalTime(from.getTotalTime())
                .totalTimeTaken(from.getTotalTimeTaken())
                .userId(from.getUserId())
                .userQuestionStatList(userQuestionStatTransformer.toModel(from.getUserQuestionStatList()))
                .build();
    }

    @Override
    public List<UserPaperStat> toModel(List<UserPaperStatDto> from) {
        return null;
    }

    @Override
    public UserPaperStatDto ToDto(UserPaperStat from) {
        return null;
    }

    @Override
    public List<UserPaperStatDto> toDto(List<UserPaperStat> from) {
        return null;
    }


}
