package com.book.ensureu.admin.api;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.admin.dto.PaperCollectionDto;
import com.book.ensureu.admin.dto.SimplePaperDto;
import com.book.ensureu.admin.service.FreePaperCollectionService;
import com.book.ensureu.admin.service.PaidPaperCollectionService;
import com.book.ensureu.admin.service.SimplePaperConverterService;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.model.FreePaperCollection;
import com.book.ensureu.model.PaidPaperCollection;

@RestController
@RequestMapping("/admin/paper/simple")
public class SimplePaperApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePaperApi.class);

    @Autowired
    private SimplePaperConverterService converterService;

    @Autowired
    private FreePaperCollectionService freePaperCollectionService;

    @Autowired
    private PaidPaperCollectionService testPaperCollectionService;

    @CrossOrigin
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<?> saveSimplePaper(@RequestBody SimplePaperDto simplePaperDto) {
        LOGGER.info("saveSimplePaper paperName={}", simplePaperDto.getPaperName());
        try {
            PaperCollectionDto fullPaper = converterService.convertToFullPaper(simplePaperDto);

            if (TestType.FREE.equals(simplePaperDto.getTestType())) {
                FreePaperCollection entity = new FreePaperCollection();
                BeanUtils.copyProperties(fullPaper, entity);
                freePaperCollectionService.createFreePaperInCollection(entity);
            } else {
                PaidPaperCollection entity = new PaidPaperCollection();
                BeanUtils.copyProperties(fullPaper, entity);
                testPaperCollectionService.createPaidPaperInCollection(entity);
            }

            return ResponseEntity.ok(
                Collections.singletonMap("message", "Paper created successfully")
            );
        } catch (Exception ex) {
            LOGGER.error("saveSimplePaper error", ex);
            return ResponseEntity.badRequest().body(
                Collections.singletonMap("error", ex.getMessage())
            );
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/preview", method = RequestMethod.POST)
    public ResponseEntity<PaperCollectionDto> previewSimplePaper(@RequestBody SimplePaperDto simplePaperDto) {
        LOGGER.info("previewSimplePaper paperName={}", simplePaperDto.getPaperName());
        try {
            PaperCollectionDto fullPaper = converterService.convertToFullPaper(simplePaperDto);
            return ResponseEntity.ok(fullPaper);
        } catch (Exception ex) {
            LOGGER.error("previewSimplePaper error", ex);
            return ResponseEntity.badRequest().build();
        }
    }
}
