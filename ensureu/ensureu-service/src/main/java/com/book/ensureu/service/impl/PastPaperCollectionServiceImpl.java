package com.book.ensureu.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.book.ensureu.model.PastPaperCollection;
import com.book.ensureu.repository.PastPaperCollectionRepository;
import com.book.ensureu.service.PastPaperCollectionService;
import com.mongodb.MongoException;

@Service
public class PastPaperCollectionServiceImpl implements PastPaperCollectionService {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PastPaperCollectionServiceImpl.class);

	@Autowired
	private PastPaperCollectionRepository pastPaperCollectionRepository;

	@Override
	public void createPastPaperInCollection(List<PastPaperCollection> pastPaperCollection) throws MongoException {
		pastPaperCollectionRepository.saveAll(pastPaperCollection);
	}

	@Override
	public void createPastPaperInCollection(PastPaperCollection pastPaperCollection) throws MongoException {
		try {
			if (pastPaperCollection != null && pastPaperCollection.getId() != null) {
				PastPaperCollection pastPaperCollectionInDb = getPastPaperCollectionById(pastPaperCollection.getId());
				if (pastPaperCollectionInDb != null && pastPaperCollectionInDb.getId() != null) {
					
					//to do for selecte fileds only.
					
					pastPaperCollection.setId(pastPaperCollectionInDb.getId());
				}
			}
			LOGGER.info("[pastPaperColl type--->" + pastPaperCollection.getPaperType() + "pastpaper collection id: "
					+ pastPaperCollection.getId());
			pastPaperCollectionRepository.save(pastPaperCollection);

		} catch (Exception ex) {
			LOGGER.info("pastpaperCollection save--->" + pastPaperCollection.getId(), ex);
			throw ex;
		}
	}

	@Override
	public PastPaperCollection getPastPaperCollectionById(String id) throws MongoException, DataAccessException {
		Optional<PastPaperCollection> pastPaper = pastPaperCollectionRepository.findById(id);
		if (pastPaper.isPresent()) {
			return pastPaper.get();
		}
		return null;
	}

	@Override
	public Page<PastPaperCollection> getAllPastPaperCollection(Pageable pageable)
			throws MongoException, DataAccessException {
		return pastPaperCollectionRepository.findAll(pageable);
	}

}
