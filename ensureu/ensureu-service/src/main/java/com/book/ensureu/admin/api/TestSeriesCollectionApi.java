package com.book.ensureu.admin.api;

import com.book.ensureu.admin.service.TestSeriesService;
import com.book.ensureu.common.dto.TestSeriesDto;
import com.book.ensureu.constant.PaperCategory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/admin/testSeries")
public class TestSeriesCollectionApi {

    private TestSeriesService testSeriesService;

    @GetMapping("/get")
    public ResponseEntity<List<TestSeriesDto>> fetchTestSeries(@RequestHeader("paperCategory")PaperCategory paperCategory,
                                                               @RequestHeader("crDate")Date crDate,
                                                               @RequestHeader("validity") Date validity
                                                               ){
        log.debug("[fetchTestSeries] controller");
        return ResponseEntity.ok(testSeriesService.fetchTestSeries(paperCategory, crDate, validity));
    }

    @PostMapping("/create")
    ResponseEntity<String> createTestSeries(@RequestBody TestSeriesDto testSeriesDto){
        log.debug("[createTestSeries] controller");
        testSeriesService.createTestSeries(testSeriesDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Created");
    }

    @PatchMapping("/patch")
    ResponseEntity<String> patchTestSeries(@RequestBody TestSeriesDto testSeriesDto){
        log.debug("[patchTestSeries] controller");
        testSeriesService.patchTestSeries(testSeriesDto);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Created");
    }


}
