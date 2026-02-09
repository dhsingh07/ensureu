package com.book.ensureu.flow.analytics.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.PaperDto;
import com.book.ensureu.flow.analytics.dto.UserAnalyticsDto;
import com.book.ensureu.flow.analytics.service.AnalyticsDataIngestionService;
import com.book.ensureu.flow.analytics.service.AnalyticsService;
import com.book.ensureu.model.JwtUser;
import com.book.ensureu.security.UserPrincipalService;
import com.book.ensureu.service.PaperService;
import com.book.ensureu.service.impl.PaperFactory;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/analytics")
@AllArgsConstructor
@Slf4j
public class AnalyticsApi {

    private AnalyticsService analyticsService;

    private UserPrincipalService userPrincipal;

    private AnalyticsDataIngestionService analyticsDataIngestionService;

    private PaperFactory paperFactory;


    @CrossOrigin
    @GetMapping("/v1/user")
    public @ResponseBody
    ResponseEntity<UserAnalyticsDto> getAnalytics(
            @RequestParam("paperCategory") @NotNull String paperCategory,
            @RequestParam("paperId") @NotBlank String paperId
    ) {
        log.debug("Analytics request for paper category {}, paperId {}", paperCategory, paperId);
        JwtUser jwtUser = null;
        try {
            jwtUser = userPrincipal.getCurrentUserDetails();
            String userId = jwtUser.getUsername();
            log.info("[getAnalytics] for userId: [{}] paperId [{}]", userId, paperId);
            return ResponseEntity.ok(analyticsService.getUserAnalytics(userId, paperId));
        } catch (Exception e) {
            log.error("[getAnalytics] Exception occurred while getting UserAnalytics", e);
            throw e;
        }

    }

    @PostMapping("/v1/test/userPaperStat")
    ResponseEntity<String> addUserPaperStat(@RequestParam("paperId") @NotNull String paperId) {
        JwtUser jwtUser = userPrincipal.getCurrentUserDetails();
        String userId = jwtUser.getUsername();
        log.info("[addUserPaperStat] for userId [{}], paperId [{}]", userId, paperId);
        PaperService paperService = paperFactory.getPaperService(TestType.PAID.toString());
        PaperDto paperDto = paperService.getPaperByPaperIdAndUserId(paperId, jwtUser.getUsername(),
                TestType.PAID);
        analyticsDataIngestionService.saveUserPaperStatFromPaperDto(paperDto);
        return ResponseEntity.ok("Saved successfully");

    }

}
