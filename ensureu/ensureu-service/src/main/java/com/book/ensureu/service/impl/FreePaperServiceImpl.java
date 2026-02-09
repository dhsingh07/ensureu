package com.book.ensureu.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.book.ensureu.constant.CounterEnum;
import com.book.ensureu.model.FreePaper;
import com.book.ensureu.repository.FreePaperRepository;
import com.book.ensureu.service.CounterService;
import com.book.ensureu.service.FreePaperService;
import com.mongodb.MongoException;

/**
 * @author dharmendra.singh
 *
 */
@Service
public class FreePaperServiceImpl implements FreePaperService<FreePaper> {

	private static final Logger LOGGER=org.slf4j.LoggerFactory.getLogger(FreePaperServiceImpl.class);

	@Autowired
	FreePaperRepository freePaperRepository;

	@Autowired
	CounterService counterService;

	@Override
	public void createFreePaper(List<FreePaper> practicePaper) throws MongoException{
		/*if (practicePaper != null && !practicePaper.isEmpty()) {
			for (PracticePaper practicePape : practicePaper) {
				practicePape.setId(practicePape.getPracticePaperId());
			}
			practicePaperRepository.saveAll(practicePaper);
		}*/
	}

	@Override
	public Optional<FreePaper> getFreePaperById(Long id) throws MongoException,DataAccessException{
		return freePaperRepository.findById(id);
	}

	@Override
	public Optional<FreePaper> getFreePaperByPaperIdAndUserId(String paperId, String userId) throws DataAccessException, MongoException {
		freePaperRepository.findByUserIdAndPaperId(userId, paperId);
		return Optional.empty();
	}

	@Override
	public void createFreePaper(FreePaper freePaper) throws MongoException{
		if (freePaper != null) {
			FreePaper practicePaperInDB = freePaperRepository
					.findByUserIdAndPaperId(freePaper.getUserId(), freePaper.getPaperId());
			if (practicePaperInDB != null) {
				freePaper.setId(practicePaperInDB.getId());
			}else{
				freePaper.setId(counterService.increment(CounterEnum.FREEPAPER));
			}
			freePaperRepository.save(freePaper);
		}
	}

}
