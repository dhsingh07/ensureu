package com.book.ensureu.api;

import com.book.ensureu.common.dto.TestSeriesDto;
import com.book.ensureu.model.JwtUser;
import com.book.ensureu.security.UserPrincipalService;
import com.book.ensureu.service.TestSeriesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/series")
public class TestSeriesApi {

    private UserPrincipalService userPrincipal;

    private TestSeriesService testSeriesService;

    @CrossOrigin
    @GetMapping("/all")
    public ResponseEntity<List<TestSeriesDto>> getTestSeries() {
        JwtUser jwtUser = userPrincipal.getCurrentUserDetails();
        String userId = jwtUser.getUsername();
        log.info("Fetch series  for userId: {}", userId);
        return ResponseEntity.ok(testSeriesService.getTestSeries(new Date().getTime(), true));

    }


    @CrossOrigin
    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestHeader("series") String seriesId) {
        JwtUser jwtUser = userPrincipal.getCurrentUserDetails();
        String userId = jwtUser.getUsername();
        log.info("Series subscribe for userId: {}, testSeriesId: {}", userId, seriesId);
        return ResponseEntity.ok(testSeriesService.subscribeTestSeries(userId, seriesId));
    }

}
