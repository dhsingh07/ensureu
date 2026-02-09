package com.book.ensureu.admin.api;

import com.book.ensureu.admin.service.SubscriptionService;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.dto.SubscriptionDto;
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
@RequestMapping("/admin/subscription")
public class SubscriptionCollectionApi {

    private SubscriptionService subscriptionService;

    @GetMapping("/fetch/{subCategory}")
    public ResponseEntity<List<SubscriptionDto>> fetchSubscription(@PathVariable("subCategory")PaperSubCategory paperSubCategory,
                                                  @RequestHeader("crDate")Long crDate
                                                           ){
     log.debug("[fetchSubscription] get subscription subCategory [{}], crDate [{}]",paperSubCategory,crDate);
     return ResponseEntity.ok(subscriptionService.fetchSubscription(paperSubCategory, crDate));
    }

    @PostMapping("/create")
    public ResponseEntity<String> createSubscription(@RequestBody  SubscriptionDto subscriptionDto){
        log.debug("createSubscription controller");
        subscriptionService.createSubscription(subscriptionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Created");
    }

    @PatchMapping("/patch")
    public ResponseEntity<SubscriptionDto> patchSubscription(@RequestBody SubscriptionDto subscriptionDto){

        log.debug("[patchSubscription] controller");
        return ResponseEntity.ok(subscriptionService.patchSubscription(subscriptionDto));

    }

}
