package com.book.ensureu.admin.api;

import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.dto.PaperInfo;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.admin.dto.PaperCollectionDto;
import com.book.ensureu.admin.service.PaidPaperCollectionService;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.model.PaidPaperCollection;
import com.mongodb.MongoException;

import java.util.List;

/**
 * @author dharmendra.singh
 *
 */
@RestController
@RequestMapping("/admin/paidpapercoll")
public class PaidPaperCollectionApi {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PaidPaperCollectionApi.class);

	@Autowired
	PaidPaperCollectionService testPaperCollectionService;

	// this is for only system user access
	@CrossOrigin
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void saveTestPaper(@RequestBody PaperCollectionDto paidPaperCollectionDto) {
		LOGGER.info("saveTestPaper ==={}", 1);
		try {
			PaidPaperCollection paidPaperCollection = new PaidPaperCollection();
			paidPaperCollectionDto.initializeDatesIfMissing();
			BeanUtils.copyProperties(paidPaperCollectionDto, paidPaperCollection);
			testPaperCollectionService.createPaidPaperInCollection(paidPaperCollection);
		} catch (Exception ex) {
			LOGGER.error("saveTestPaperCollection ", ex);
		}
	}
	
	@CrossOrigin
	@RequestMapping(value = "/save", method = RequestMethod.PUT)
	public void updatePaper(@RequestParam(value="id",required=true) String id, @RequestParam(value="paperState",required=true) PaperStateStatus paperState) {
		try {
			testPaperCollectionService.updatePaidPaperState(id, paperState);
		} catch (MongoException ex) {
			LOGGER.error("saveFreePaper ", ex);
		}
	}
	

	@CrossOrigin
	@RequestMapping(value = "/getbyid/{id}", method = RequestMethod.GET)
	public PaperCollectionDto getPaidPaperById(@PathVariable(value = "id") final String id) {
		LOGGER.info("getPaidPaper by ID {}", id);
		try {
			return testPaperCollectionService.getTestPaperCollectionById(id);
		} catch (Exception ex) {
			LOGGER.error("getPaidPaperById " + id, ex);
		}
		return null;
	}

	@CrossOrigin
	@RequestMapping(value = "/list/{paperType}", method = RequestMethod.GET)
	public Page<PaperCollectionDto> getAllPaidPaper(
			@PathVariable(value = "paperType", required = true) String paperType,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size) {
		LOGGER.info("getAllPaidPaper list {}", 1);
		try {
			Pageable pageable = PageRequest.of(page, size);
			return testPaperCollectionService.getAllPaidPaperCollection(PaperType.valueOf(paperType), pageable);
		} catch (Exception ex) {
			LOGGER.error("getAllPaidPaper ", ex);
		}
		return null;
	}

	@GetMapping("/list/{paperSubCategory}")
	public ResponseEntity<List<PaperInfo>> fetchPaperInfoListBySubCategory(
			@PathVariable("paperSubCategory")PaperSubCategory paperSubCategory,
			@RequestParam(value = "page", required = true) int page,
	       @RequestParam(value = "size", required = true) int size,
			@RequestParam(value = "taken", required = true) Boolean taken ) {

		LOGGER.info("fetchPaperInfoListBySubCategory paperSubCategory {} page {} size {}", paperSubCategory,page,size);
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(testPaperCollectionService.fetchPaperInfoList(paperSubCategory,pageable,taken));
	}
}
