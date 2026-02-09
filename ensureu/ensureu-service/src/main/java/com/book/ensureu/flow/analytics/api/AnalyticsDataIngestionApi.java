package com.book.ensureu.flow.analytics.api;

import com.book.ensureu.flow.analytics.dto.UserPaperStatDto;
import com.book.ensureu.flow.analytics.dto.UserPaperTimeSeriesDto;
import com.book.ensureu.flow.analytics.service.AnalyticsDataIngestionService;
import com.book.ensureu.model.JwtUser;
import com.book.ensureu.security.UserPrincipalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/dataIngest")
public class AnalyticsDataIngestionApi {

    private AnalyticsDataIngestionService analyticsDataIngestionService;

    private UserPrincipalService userPrincipal;


    @PostMapping("/v1/userTimeSeries")
    ResponseEntity<String> submitUserTimeSeries(@RequestBody UserPaperTimeSeriesDto userPaperTimeSeriesDto){
        log.info("[submitUserTimeSeries] for userId [{}], paperId [{}]",userPaperTimeSeriesDto.getUserId(),userPaperTimeSeriesDto.getPaperId());
        analyticsDataIngestionService.saveUsePaperTimeSeries(userPaperTimeSeriesDto);
        return ResponseEntity.ok("Saved successfully");
    }

    @PostMapping("/v1/userPaperStat")
    ResponseEntity<String> submitUserPaperStat(@RequestBody UserPaperStatDto userPaperStatDto){

        log.info("[submitUserPaperStat] for userId [{}], paperId [{}]",userPaperStatDto.getUserId(),userPaperStatDto.getPaperId());
        analyticsDataIngestionService.saveUserPaperStat(userPaperStatDto);
        return ResponseEntity.ok("Saved successfully");

    }

    @PostMapping("/v1/fetch/userTimeSeries")
    ResponseEntity<UserPaperTimeSeriesDto> fetchUserTimeSeries(@RequestParam("paperId") String paperId){

        JwtUser jwtUser = userPrincipal.getCurrentUserDetails();
        String userId = jwtUser.getUsername();
        log.info("[fetchUserTimeSeries] for userId [{}], paperId [{}]",userId,paperId);
        return ResponseEntity.ok(analyticsDataIngestionService.fetchUserPaperTimeSeries(userId,paperId));
    }

}
