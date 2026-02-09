package com.book.ensureu.flow.analytics.service.impl;

import com.book.ensureu.dto.PaperDto;
import com.book.ensureu.exception.RuntimeEUException;
import com.book.ensureu.exception.unchecked.EntityNotFound;
import com.book.ensureu.flow.analytics.dao.UserAnalyticsDao;
import com.book.ensureu.flow.analytics.dto.*;
import com.book.ensureu.flow.analytics.model.PaperStat;
import com.book.ensureu.flow.analytics.model.QuestionStat;
import com.book.ensureu.flow.analytics.model.UserPaperStat;
import com.book.ensureu.flow.analytics.model.UserPaperTimeSeries;
import com.book.ensureu.flow.analytics.service.AnalyticsDataIngestionService;
import com.book.ensureu.flow.analytics.transformer.UserPaperStatTransformer;
import com.book.ensureu.flow.analytics.transformer.UserPaperTimeSeriesTransformer;
import com.book.ensureu.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AnalyticsDataIngestionServiceImpl implements AnalyticsDataIngestionService {

    private UserAnalyticsDao userAnalyticsDao;

    private UserPaperStatTransformer userPaperStatTransformer;

    private UserPaperTimeSeriesTransformer userPaperTimeSeriesTransformer;

    @Override
    public void saveUserPaperStat(UserPaperStatDto userPaperStatDto) {
        log.info("[saveUserPaperStat] paperId [{}], userId [{}]",userPaperStatDto.getPaperId(),userPaperStatDto.getUserId());
        UserPaperStat userPaperStat = userPaperStatTransformer.toModel(userPaperStatDto);
        userAnalyticsDao.SaveUserPaperStat(userPaperStat);
        userAnalyticsDao.addUserMarksToPaperStat(userPaperStat);

    }

    @Override
    public void saveUserTimeSeries(List<UserQuestionTimeDto> userQuestionTimeDto) {

    }

    /**
     * This method used by PaperApi to submit paperDto which is decrypted on the fly
     * from paperDto it save userPaperStat and paperStat also.
     */
    @Override
    public void saveUserPaperStatFromPaperDto(PaperDto paperDto) {
       // if(TestType.PAID.equals(paperDto.getTestType())) {
            UserPaperStatDto userPaperStatDto = ingestUserPaperStats(paperDto);
            ingestUserQuestionStats(paperDto, userPaperStatDto);
            ingestQuestionStats(userPaperStatDto);
            saveUserPaperStat(userPaperStatDto);
       // }
    }



    @Override
    public void saveUsePaperTimeSeries(UserPaperTimeSeriesDto userPaperTimeSeriesDto) {
        log.info("[saveUsePaperTimeSeries] paperId [{}],userId [{}]",userPaperTimeSeriesDto.getPaperId(),userPaperTimeSeriesDto.getUserId());
        userAnalyticsDao.saveUserPaperTimeSeries(userPaperTimeSeriesTransformer.toModel(userPaperTimeSeriesDto));
    }

    @Override
    public UserPaperTimeSeriesDto fetchUserPaperTimeSeries(String userId, String paperId) {

        UserPaperTimeSeries userPaperTimeSeries = userAnalyticsDao.fetchUserPaperTimeSeries(userId,paperId);
        if(userPaperTimeSeries == null){
            log.error("[fetchUserPaperTimeSeries] nodata found for user [{}],paperId [{}]",userId,paperId);
            throw new EntityNotFound("[fetchUserPaperTimeSeries] nodata found for user [{}],paperId [{}]");
        }

        return userPaperTimeSeriesTransformer.ToDto(userAnalyticsDao.fetchUserPaperTimeSeries(userId,paperId));
    }

    private void ingestQuestionStats(UserPaperStatDto userPaperStatDto) {
        Map<String, UserQuestionStatDto> questionStatDtoMap = userPaperStatDto.getUserQuestionStatList()
                .stream()
                .filter(f -> (f.getQuestionId() != null) ? true : false)
                .collect(Collectors.toMap(k -> k.getQuestionId(), Function.identity(),(k,v)-> k));
        List<QuestionStat> questionStatList = userAnalyticsDao.fetchQuestionStatByIdIn(questionStatDtoMap.keySet().stream().collect(Collectors.toList()));
        if (Objects.nonNull(questionStatList)) {
            // questionStatList.stream().
        } else {

        }
    }

    private void ingestPaperStat(UserPaperStatDto userPaperStatDto) {
        PaperStat paperStat = PaperStat.builder()
                .build();
    }

    private UserPaperStatDto ingestUserPaperStats(PaperDto paperDto) {
        UserPaperStatDto userPaperStats = new UserPaperStatDto();
        userPaperStats.setUserId(paperDto.getUserId());
        userPaperStats.setPaperId(paperDto.getPaperId());
        userPaperStats.setPaperName(paperDto.getPaperName());
        userPaperStats.setTestType(paperDto.getTestType());
        userPaperStats.setPaperCategory(paperDto.getPaperCategory());
        userPaperStats.setTotalScore(paperDto.getTotalGetScore());
        userPaperStats.setTotalTimeTaken(paperDto.getTotalTimeTaken());
        userPaperStats.setTotalTime(paperDto.getTotalTime());
        userPaperStats.setTotalSkipped(paperDto.getTotalSkipedCount());
        userPaperStats.setTotalCorrect(paperDto.getTotalCorrectCount());
        userPaperStats.setTotalAttempted(paperDto.getTotalAttemptedQuestionCount());
        userPaperStats.setTotalQuestions(paperDto.getTotalQuestionCount());
        userPaperStats.setCreatedAt(new Date().getTime());
        return userPaperStats;
    }

    private List<UserQuestionStatDto> ingestUserQuestionStats(PaperDto paperDto, UserPaperStatDto userPaperStatDto) {
        List<UserQuestionStatDto> userQuestionStatDtoList = new ArrayList<>();
        Pattern<Sections<SubSections<Question<Problem>>>> pattern =
                paperDto.getPaper().getPattern();
        List<Sections<SubSections<Question<Problem>>>> sectionsList = pattern.getSections();
        List<SectionHistogramDto> sectionHistogramDtos = new LinkedList<>();
        for (Sections<SubSections<Question<Problem>>> section : sectionsList) {
            SectionHistogramDto sectionHistogramDto = SectionHistogramDto.builder().build();
            sectionHistogramDtos.add(sectionHistogramDto);

            List<SubSections<Question<Problem>>> subSectionList = section.getSubSections();
            for (SubSections<Question<Problem>> subSection : subSectionList) {
                QuestionData<Question<Problem>> questionData = subSection.getQuestionData();
                List<Question<Problem>> questionList = questionData.getQuestions();
                for (Question<Problem> question : questionList) {
                    UserQuestionStatDto questionStats = UserQuestionStatDto.builder()
                            .questionId(question.getId())
                            .questionAttemptedStatus(question.getQuestionAttemptedStatus())
                            .section(section.getTitle())
                            .subSection(subSection.getTitle())
                            .timeTaken(question.getTimeTakenInSecond())
                            .type(question.getType())
                            .complexityLevel(question.getComplexityLevel())
                            .marks(question.getScore())
                            .build();
                    userQuestionStatDtoList.add(questionStats);
                }
            }
        }
        //TODO need to update QuestionStat Document
        userPaperStatDto.setUserQuestionStatList(userQuestionStatDtoList);
        return userQuestionStatDtoList;
    }


}
