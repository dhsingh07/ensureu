package com.book.ensureu.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.book.ensureu.constant.CounterEnum;
import com.book.ensureu.dto.PaperDto;
import com.book.ensureu.model.PaidPaper;
import com.book.ensureu.repository.PaidPaperRepository;
import com.book.ensureu.service.CounterService;
import com.book.ensureu.service.TestPaperService;
import com.book.ensureu.util.PaperConversionUtil;

@Service
public class TestPaperServiceImpl implements TestPaperService {

	private static final Logger LOGGER=org.slf4j.LoggerFactory.getLogger(TestPaperServiceImpl.class);
	
	@Autowired
	PaidPaperRepository testServiceRepository;
	
	@Autowired
	CounterService counterService;


	@Override
	public void createTestPaper(List<PaidPaper> testPaper) {
		testServiceRepository.saveAll(testPaper);
	}

	@Override
	public void createTestPaper(PaidPaper testPaper) {
		testServiceRepository.save(testPaper);
	}

	@Override
	public PaperDto getTestPaperById(Long id) {
		Optional<PaidPaper> testPaperOptional=testServiceRepository.findById(id);
		if(testPaperOptional.isPresent()) {
			return PaperConversionUtil.paidPaperToDto(testPaperOptional.get());
		}
		return null;
		
	}

	@Override
	public PaperDto getTestPaperByUserIdAndTestPaperId(String userId, String testPaperId) {
		PaidPaper testPaper=testServiceRepository.findByUserIdAndPaperId(userId, testPaperId);
		return PaperConversionUtil.paidPaperToDto(testPaper);
	}

	/*@Override
	public TestPaperDto getTestPaperByUserIdAndTestPaperIdAndPayment(String userId, Long testPaperId,
			boolean payment) {	
		TestPaper testPaper=testServiceRepository.findByUserIdAndTestPaperIdAndPayment(userId, testPaperId, payment);
		return TestPaperConversionUtil.tesPaperToDto(testPaper);
	}
*/
	
	/* (non-Javadoc)
	 * @see com.book.assessu.service.TestPaperService#createTestPaper(com.book.assessu.dto.TestPaperDto)
	 * convert dto to entity and save testPaper and user mapping 
	 */
	@Override
	public void createTestPaper(PaperDto testPaperDto) {
		PaidPaper testPaper = PaperConversionUtil.paidPaperDtoToModel(testPaperDto);
		testPaper.setId(counterService.increment(CounterEnum.TESTPAPER));
		LOGGER.info("User Mapping TestPaper UserId ["+ testPaper.getUserId()+"] paperId ["+testPaper.getPaperId()+"]");
		testServiceRepository.save(testPaper);

	}

	/* (non-Javadoc)
	 * @see com.book.assessu.service.TestPaperService#getTestPaperByUserId(java.lang.String)
	 * Test paper find by userId
	 */
	@Override
	public List<PaperDto> getTestPaperByUserId(String userId) {
		List<PaidPaper> testPaper=testServiceRepository.findByUserId(userId);
		return PaperConversionUtil.paidPaperToDto(testPaper);
	}

	@Override
	public List<PaperDto> getTestPaperByTestPaperId(String testPaperId) {
		return null;
	}

}
