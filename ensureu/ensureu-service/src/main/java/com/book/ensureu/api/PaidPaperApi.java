package com.book.ensureu.api;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.book.ensureu.dto.PaperDto;
import com.book.ensureu.model.PaidPaper;
import com.book.ensureu.service.TestPaperService;

/**
 * @author dharmendra.singh
 *
 */
@RestController
@RequestMapping("/paidpaper")
public class PaidPaperApi {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PaidPaperApi.class);

	@Autowired
	TestPaperService paidPaperService;

	@CrossOrigin
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public void saveTestPaper(@RequestBody PaidPaper testPaper) {
		try {
			paidPaperService.createTestPaper(testPaper);
		} catch (Exception ex) {
			LOGGER.error("Save TestPaper ", ex);
		}
	}

	@CrossOrigin
	@RequestMapping(value = "/saveorupdate", method = RequestMethod.POST)
	public void saveTestPaper(@RequestBody PaperDto testPaperDto) {
		try {
			paidPaperService.createTestPaper(testPaperDto);
		} catch (Exception ex) {
			LOGGER.error("Save and update TestPaper ", ex);
		}

	}

	@CrossOrigin
	@RequestMapping(value = "/getbyid/{id}", method = RequestMethod.GET)
	public PaperDto getTestPaperById(@PathVariable(value = "id") final Long id) {

		try {
			return paidPaperService.getTestPaperById(id);
		} catch (Exception ex) {
			LOGGER.error("getTestPaperById "+id, ex);
		}
		return null;

	}

	@CrossOrigin
	@RequestMapping(value = "/getbytestpaperid/{id}", method = RequestMethod.GET)
	public List<PaperDto> getTestPaperByTestPaperId(@PathVariable(value = "testPaperId") final String testPaperId) {

		try {
			return paidPaperService.getTestPaperByTestPaperId(testPaperId);
		} catch (Exception ex) {
			LOGGER.error("getTestPaperByTestPaperId "+testPaperId, ex);
		}
		return null;

	}

	@CrossOrigin
	@RequestMapping(value = "/getbyuseridandpaperid", method = RequestMethod.GET)
	public PaperDto getTestPaperByUserIdAndTestPaperId(@RequestParam(value = "userId") final String userId,
			@RequestParam(value = "testPaperId") final String testPaperId) {

		LOGGER.info("getTestPaperByUserIdAndTestPaperId {}",userId);
		try {
			return paidPaperService.getTestPaperByUserIdAndTestPaperId(userId, testPaperId);
		} catch (Exception ex) {
			LOGGER.error("getTestPaperByUserIdAndTestPaperId "+userId+" testPaperId  "+ testPaperId, ex);
		}
		return null;

	}
}
