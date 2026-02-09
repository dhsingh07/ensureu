package com.book.ensureu.flow.analytics.dao;

import com.book.ensureu.flow.analytics.dto.*;
import com.book.ensureu.flow.analytics.model.*;
import com.book.ensureu.flow.analytics.repository.PaperStatRepository;
import com.book.ensureu.flow.analytics.repository.UserPaperStatRepository;
import com.book.ensureu.flow.analytics.repository.UserPaperTimeSeriesRepository;
import com.book.ensureu.flow.analytics.repository.QuestionStatRepository;
import com.book.ensureu.flow.analytics.transformer.PercentilePercentTransformer;
import com.book.ensureu.flow.analytics.transformer.UserQuestionTimeTransformer;
import com.book.ensureu.flow.analytics.util.KeyConversionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * get paperStat -- give percentile graph
 * get user & topper stat --  make histogram
 * time series -- give questionTimeSeries and marksTimeSeries Graph
 * userGrowth -- give user previous paper vs topper marks
 */

@Component
@AllArgsConstructor
@Slf4j
public class UserAnalyticsDao {

    private UserPaperTimeSeriesRepository userPaperTimeSeriesRepository;

    private PaperStatRepository paperStatRepository;

    private UserPaperStatRepository userPaperStatRepository;

    private QuestionStatRepository questionStatRepository;

    private PercentilePercentTransformer percentilePercentTransformer;

    private UserQuestionTimeTransformer userQuestionTimeTransformer;


    public UserAnalyticsDto buildUserAnalytics(String userId, String paperId) {

        try {
            log.info("[buildUserAnalytics] userId [{}], paperId[{}]", userId, paperId);
            UserPaperTimeSeries userPaperTimeSeries = userPaperTimeSeriesRepository.fetchUserPaperTimeSeries(userId, paperId); // throws exception if no data is found
            PaperStat paperStat = paperStatRepository.fetchPaperStat(paperId);
            List<String> topperList = paperStat.getTopperPaperStatList();
            List<String> remainingTopperList = topperList.stream()
                    .filter(id -> !id.equalsIgnoreCase(userId))
                    .collect(Collectors.toList());
            String topperId = (remainingTopperList.isEmpty()) ? userId : remainingTopperList.get(0);

            List<UserPaperStat> userPaperStatList = userPaperStatRepository.fetchUserPaperStat(userId, topperId, paperId);
            log.debug("[buildUserAnalytics] userPaperStatList [{}]",userPaperStatList);

            UserAnalyticsDto userAnalyticsDto = UserAnalyticsDto.builder().build();
            if (userPaperTimeSeries != null) {
                buildUserTimeSeries(userAnalyticsDto, userPaperTimeSeries);
            } else {
                log.error("[buildUserAnalytics] userTimeSeries is not present");
            }
            setUserScoreDto(userId, userPaperStatList, userAnalyticsDto);
            buildHistogram(userId, userAnalyticsDto, userPaperStatList);
            buildPercentile(userAnalyticsDto, paperStat);
            buildUserGrowth(userAnalyticsDto, userId);
            return userAnalyticsDto;
        } catch (Exception e) { //TODO need to take care when exception occurred and some data needs to be reverted
            log.error("[buildUserAnalytics] Exception occurred userId [{}], paperId[{}] ", userId, paperId, e);
            throw e;
        }
    }


    /**
     * This method adds userPaperStat and also update paperStat collection
     * if no paperStat is present it adds one
     *
     * @param userPaperStat
     */
    public void SaveUserPaperStat(UserPaperStat userPaperStat) {
        //addUserMarksToPaperStat(userPaperStat);
        userPaperStatRepository.saveUserPaperStat(userPaperStat);
    }


    public void saveQuestionStat(List<QuestionStat> questionStatList) {
        questionStatRepository.saveQuestionStat(questionStatList);
    }

    public List<QuestionStat> fetchQuestionStatByIdIn(List<String> questionIds) {
        return questionStatRepository.fetchQuestionStatByIdIn(questionIds);
    }

    public void saveUserPaperTimeSeries(UserPaperTimeSeries userPaperTimeSeries) {

        userPaperTimeSeriesRepository.saveUserPaperTimeSeries(userPaperTimeSeries);
    }

    public void addUserMarksToPaperStat(UserPaperStat userPaperStat) {
        PaperStat paperStat = paperStatRepository.fetchPaperStat(userPaperStat.getPaperId());
        if (Objects.isNull(paperStat)) {
            paperStat = createPaperStatFromUserPaperStat(userPaperStat);
        } else {
            paperStat.getUserPaperStatList().add(userPaperStat.getUserId());
        }

        Map<String, List<String>> userMarksTreeMap = paperStat.getMarksVsUsersTreeMap();
        String scoreKey = KeyConversionUtil.getDoubleToKey(userPaperStat.getTotalScore());
        List<String> listOfUsers = userMarksTreeMap.getOrDefault(scoreKey, new LinkedList<>());
        listOfUsers.add(userPaperStat.getUserId());
        userMarksTreeMap.putIfAbsent(scoreKey, listOfUsers);
        addPercentileObjectDataAndTopperList(paperStat);
        paperStatRepository.savePaperStat(paperStat);
    }

    public UserPaperTimeSeries fetchUserPaperTimeSeries(String userId, String paperId) {
        return userPaperTimeSeriesRepository.fetchUserPaperTimeSeries(userId, paperId);
    }


    /**
     * This method calculate percentile(P)as  P = 100*(N)/T
     * Where T = total students , R = rank of current P, N = represents rank of user
     * if they are in increasing order of their marks.
     * With help of above formula it add percentileObjectDataList to paperStat object
     * along with TopperUserList
     *
     * @param paperStat
     */
    private void addPercentileObjectDataAndTopperList(PaperStat paperStat) {

        ArrayList<PercentileDataObject> percentileDataObjectList = new ArrayList<>();
        int totalStudents = paperStat.getUserPaperStatList().size();

        TreeMap<String, List<String>> treeMap = paperStat.getMarksVsUsersTreeMap();
        int rank = 1;
        for (Map.Entry<String, List<String>> e : treeMap.descendingMap().entrySet()) {

            double marks = KeyConversionUtil.getDoubleFromKey(e.getKey());
            double percentile = 100 * (totalStudents - rank + 1) / totalStudents;
            PercentileDataObject obj = PercentileDataObject.builder()
                    .marks(marks)
                    .percentile(percentile)
                    .userIds(e.getValue())  //TODO : need to add label also
                    .rank(rank)
                    .build();
            percentileDataObjectList.add(obj);
            rank += e.getValue().size();
        }
        paperStat.setTopperPaperStatList(treeMap.lastEntry().getValue());
        paperStat.setPercentileDataObjectList(percentileDataObjectList);
    }

    private PaperStat createPaperStatFromUserPaperStat(UserPaperStat userPaperStat) {

        return PaperStat.builder()
                .paperId(userPaperStat.getPaperId())
                .paperHierarchy(userPaperStat.getPaperHierarchy())
                .paperSubCategory(userPaperStat.getPaperSubCategory())
                .userPaperStatList(Stream.of(userPaperStat.getUserId()).collect(Collectors.toList()))
                .topperPaperStatList(new LinkedList<>())
                .percentileDataObjectList(new ArrayList<>())
                .build();
    }


    /**
     * buildTimeSeries graph
     * UserPaperTimeSeries won't hold null values as specified using javax constraints on UserPaperTimeSeries class
     *
     * @param userPaperTimeSeries
     * @param userAnalyticsDto
     */
    private void buildUserTimeSeries(@NotNull UserAnalyticsDto userAnalyticsDto, @NotNull UserPaperTimeSeries userPaperTimeSeries) {
        log.debug("[buildUserTimeSeries] No timeSeries data found for user [{}] paperId [{}]");
        TimeSeriesDto timeSeriesDto = TimeSeriesDto.builder().build();
        Map<Long, UserQuestionTimeDto> userQuestionTimeDtoMap = new HashMap<>();
        List<UserQuestionTimeDto> userQuestionTimeDtoList = new LinkedList<>();
        timeSeriesDto.setQuesTimeList(userPaperTimeSeries.getUserQuestionTimeList().stream().
                map(timeSeries -> {
                    userQuestionTimeDtoMap.put(timeSeries.getTimeTaken(), userQuestionTimeTransformer.ToDto(timeSeries));
                    userQuestionTimeDtoList.add(userQuestionTimeTransformer.ToDto(timeSeries));
                    return timeSeries.getTimeTaken();
                }).collect(Collectors.toList()));
        timeSeriesDto.setTimeVsUserQuestionTimeDtoMap(userQuestionTimeDtoMap);
        timeSeriesDto.setUserQuestionTimeDtoList(userQuestionTimeDtoList);

        userAnalyticsDto.setTimeSeriesDto(timeSeriesDto);

    }

    private void buildHistogram(String userId, UserAnalyticsDto userAnalyticsDto, List<UserPaperStat> userPaperStatList) {

        List<QuestionSpeedCompDto> questionSpeedCompDtoList = buildQuestionSeriesCompList(userId, userPaperStatList);
        List<SectionHistogramDto> sectionHistogramDtoList = buildSectionHistogramDtoList(userId, userPaperStatList);
        userAnalyticsDto.setTimeHistogramList(questionSpeedCompDtoList);
        userAnalyticsDto.setSectionHistogramDtoList(sectionHistogramDtoList);

    }

    private void buildPercentile(UserAnalyticsDto userAnalyticsDto, PaperStat paperStat) {

        userAnalyticsDto.setPercentileList(percentilePercentTransformer.toModel(paperStat.getPercentileDataObjectList()));
    }

    private List<SectionHistogramDto> buildSectionHistogramDtoList(String userId, List<UserPaperStat> userPaperStatList) {

        List<UserQuestionStat> userQuestionStatList, topperQuestionStatList;
        Map<String, SectionHistogramDto> sectionHistogramDtoMap = new HashMap<>();
        int rank0 = 0, rank1 = 1;
        if (!userPaperStatList.get(rank0).getUserId().equalsIgnoreCase(userId)) {
            rank0 = 1;
            rank1 = 0;
        }
        if (userPaperStatList.size() <= 1) {  // this is used when user is himself topper
            rank0 = 0;
            rank1 = 0;
        }

        userQuestionStatList = userPaperStatList.get(rank0).getUserQuestionStatList();
        topperQuestionStatList = userPaperStatList.get(rank1).getUserQuestionStatList();
        for (int i = 0; i < userQuestionStatList.size(); i++) {
            QuestionSpeedCompDto questionSpeedCompDto = transformToQuestionSpeedComp(userQuestionStatList.get(i), topperQuestionStatList.get(i));
            SectionHistogramDto sectionHistogramDto = sectionHistogramDtoMap.getOrDefault(questionSpeedCompDto.getSection(), SectionHistogramDto.builder().
                    questionSpeedCompDtoList(new LinkedList<>()).
                    build());
            sectionHistogramDto.setTotalMarks(sectionHistogramDto.getTotalMarks() + questionSpeedCompDto.getUserMarks());
            sectionHistogramDto.setTotalQuestions(sectionHistogramDto.getTotalQuestions() + 1);
            if (questionSpeedCompDto.getUserMarks() > 0) {
                sectionHistogramDto.setTotalRightQuestions(sectionHistogramDto.getTotalRightQuestions() + 1);
            } else if (questionSpeedCompDto.getUserMarks() == 0) {
                sectionHistogramDto.setTotalSkipped(sectionHistogramDto.getTotalSkipped() + 1);
            } else {
                sectionHistogramDto.setTotalWrongQuestions(sectionHistogramDto.getTotalWrongQuestions() + 1);
            }
            sectionHistogramDto.getQuestionSpeedCompDtoList().add(questionSpeedCompDto);
            sectionHistogramDto.setSection(questionSpeedCompDto.getSection());
            sectionHistogramDtoMap.putIfAbsent(sectionHistogramDto.getSection(),sectionHistogramDto);
        }
        return new LinkedList<>(sectionHistogramDtoMap.values());
    }

    private List<QuestionSpeedCompDto> buildQuestionSeriesCompList(String userId, List<UserPaperStat> userPaperStatList) {

        List<UserQuestionStat> userQuestionStatList, topperQuestionStatList;
        List<QuestionSpeedCompDto> questionSpeedCompDtoList = new LinkedList<>();
        int rank0 = 0, rank1 = 1;
        if (userPaperStatList.get(rank0).getUserId() != userId) {
            rank0 = 1;
            rank1 = 0;
        }
        if (userPaperStatList.size() <= 1) {  // this is used when user is himself topper
            rank0 = 0;
            rank1 = 0;
        }

        userQuestionStatList = userPaperStatList.get(rank0).getUserQuestionStatList();
        topperQuestionStatList = userPaperStatList.get(rank1).getUserQuestionStatList();
        for (int i = 0; i < userQuestionStatList.size(); i++) {
            questionSpeedCompDtoList.add(transformToQuestionSpeedComp(userQuestionStatList.get(i), topperQuestionStatList.get(i)));
        }
        return questionSpeedCompDtoList;
    }

    private QuestionSpeedCompDto transformToQuestionSpeedComp(UserQuestionStat userQuestionStat, UserQuestionStat topperQuestionStat) {

        return QuestionSpeedCompDto.builder().
                questionId(userQuestionStat.getQuestionId()).
                questionNumber(userQuestionStat.getQuestionNumber()).
                timeTakenByUser(userQuestionStat.getTimeTaken()).
                timeTakenByTopper(topperQuestionStat.getTimeTaken()).
                userMarks(userQuestionStat.getMarks()).
                topperMarks(topperQuestionStat.getMarks()).
                section(userQuestionStat.getSection()).
                subSection(userQuestionStat.getSubSection()).
                type(userQuestionStat.getType()).
                build();
    }


    private void buildUserGrowth(UserAnalyticsDto userAnalyticsDto, String userId) {

        List<UserPaperStat> userPaperStatList = userPaperStatRepository.fetchPreviousPaper(userId, 20); //TODO : need to make property file driven
        Map<String, UserGrowthPointDto> userGrowthPointDtoMap = new HashMap<>();
        List<String> paperIds = userPaperStatList.stream().map(obj -> {
            UserGrowthPointDto userGrowthPointDto = UserGrowthPointDto.builder()
                    .userMarks(KeyConversionUtil.safeDoubleValue(obj.getTotalScore()))
                    .paperId(obj.getPaperId())
                    .paperName(obj.getPaperName())
                    .build();
            userGrowthPointDtoMap.putIfAbsent(obj.getPaperId(), userGrowthPointDto);
            return obj.getPaperId();
        }).collect(Collectors.toList());
        paperStatRepository.fetchPaperStatByIdIn(paperIds).forEach(obj -> {
            UserGrowthPointDto userGrowthPointDto = userGrowthPointDtoMap.get(obj.getPaperId());
            userGrowthPointDto.setTopperMarks(
                    KeyConversionUtil.getDoubleFromKey(obj.getMarksVsUsersTreeMap().lastKey())
            );
            obj.getPercentileDataObjectList().forEach(percentileObj -> {
                List<String> userIdList = percentileObj.getUserIds();
                userIdList.forEach(str ->{
                    if(str.equalsIgnoreCase(userId)){
                        userGrowthPointDto.setUserPercentile(percentileObj.getPercentile());
                    }
                });
            });
        });
        userAnalyticsDto.setUserGrowthDto(UserGrowthDto.builder()
                .paperIds(paperIds)
                .userGrowthPointDtoMap(userGrowthPointDtoMap)
                .build());
    }

    /**
     * @param userPaperStatList will contain 2 objects - topper & user
     */
    private void setUserScoreDto(String userId, List<UserPaperStat> userPaperStatList, UserAnalyticsDto userAnalyticsDto) {

        if (userPaperStatList.get(0).getUserId().equalsIgnoreCase(userId)) {
            userAnalyticsDto.setUserScoreDto(userPaperStatToUserScoreDto(userPaperStatList.get(0)));
        } else {
            userAnalyticsDto.setUserScoreDto(userPaperStatToUserScoreDto(userPaperStatList.get(1)));
        }
    }

    private UserScoreDto userPaperStatToUserScoreDto(UserPaperStat userPaperStat) {
        return UserScoreDto.builder().
                maxPossibleScore(userPaperStat.getMaxPossibleScore()).
                //paperDescription(userPaperStat)
                        userId(userPaperStat.getUserId()).
                        score(userPaperStat.getTotalScore()).
                //.rank(userPaperStat.get)
                        build();
    }

}
