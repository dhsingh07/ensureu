package com.book.ensureu.admin.api;

import com.book.ensureu.admin.service.PriceMetaDataService;
import com.book.ensureu.common.dto.PriceMetaDataDto;
import com.book.ensureu.constant.PaperSubCategory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/admin/priceMetaData")
public class PriceMetaDataCollectionApi {

    private PriceMetaDataService priceMetaDataService;

    @GetMapping("/fetch/{subCategory}")
    public ResponseEntity<List<PriceMetaDataDto>> fetchPriceMetaData(@PathVariable PaperSubCategory paperSubCategory){
        return ResponseEntity.ok(priceMetaDataService.getPriceMetaData(paperSubCategory));
    }

    @PostMapping("/save")
    public ResponseEntity<String> savePriceMetaData(@RequestBody  PriceMetaDataDto priceMetaDataDto){
        priceMetaDataService.savePriceMetaData(priceMetaDataDto);
        return ResponseEntity.ok("saved successfully");
    }

    @PostMapping("/patch")
    public ResponseEntity<String> patchPriceMetaData(@RequestBody PriceMetaDataDto priceMetaDataDto){
        priceMetaDataService.patchPriceMetaData(priceMetaDataDto);
        return ResponseEntity.ok("patched successfully");
    }
}
