package com.book.ensureu.admin.service.Impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.admin.dto.PaperCollectionDto;
import com.book.ensureu.admin.service.FreePaperCollectionService;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.model.FreePaperCollection;
import com.book.ensureu.repository.FreePaperCollectionRepository;
import com.book.ensureu.service.CounterService;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.mongodb.MongoException;

/**
 * @author dharmendra.singh
 *
 */
@Service
public class FreePaperCollectionServiceImpl implements FreePaperCollectionService {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(FreePaperCollectionServiceImpl.class);

	@Autowired
	FreePaperCollectionRepository freePaperCollectionRepository;

	@Autowired
	CounterService counterService;

	@Override
	public void createFreePaperInCollection(List<FreePaperCollection> freePaperCollection) throws MongoException {
		LOGGER.info("List FreePaperCollection create");
		freePaperCollectionRepository.saveAll(freePaperCollection);
	}

	/**
	 * save free-Paper in freePaper collection.
	 */
	@Override
	public void createFreePaperInCollection(FreePaperCollection freePaperCollection) throws MongoException {

		LOGGER.info("FreePaperCollection create");
		if (freePaperCollection != null) {
			freePaperCollectionRepository.save(freePaperCollection);
		} else {
			LOGGER.info("FreePaperCollection should not be null");
		}

	}

	@Override
	public PaperCollectionDto getFreePaperCollectionById(String id) throws DataAccessException, MongoException {

		Optional<FreePaperCollection> freePaperCollectionOps = freePaperCollectionRepository.findById(id);
		FreePaperCollection freePaperCollection = null;
		PaperCollectionDto paperCollectionDto = null;
		if (freePaperCollectionOps.isPresent()) {
			freePaperCollection = freePaperCollectionOps.get();
			paperCollectionDto = new PaperCollectionDto();
			BeanUtils.copyProperties(freePaperCollection, paperCollectionDto);
		} else {
			LOGGER.info("Free Paper is not avalable for this id[ " + id + " ]");
		}
		return paperCollectionDto;
	}

	@Override
	public FreePaperCollection getFreePaperCollectionEntityById(String id) throws DataAccessException, MongoException {
		Optional<FreePaperCollection> freePaperCollectionOps = freePaperCollectionRepository.findById(id);
		if (freePaperCollectionOps.isPresent()) {
			return freePaperCollectionOps.get();
		} else {
			LOGGER.info("Free Paper is not avalable for this id[ " + id + " ]");
		}
		return null;
	}

	@Override
	public Page<PaperCollectionDto> getAllFreePaperColl(PaperType paperType, Pageable pageable) throws DataAccessException, MongoException {
		List<PaperStateStatus> paperStateStatus = Arrays.asList(PaperStateStatus.DRAFT, PaperStateStatus.ACTIVE,
				PaperStateStatus.APPROVED);
		Page<FreePaperCollection> pageFreePaperCollections = null;
		try {
			pageFreePaperCollections = freePaperCollectionRepository.findByPaperStateStatusIn(paperStateStatus,paperType,
					pageable);
		} catch (Exception e) {
			LOGGER.error("Paper Fetch Issue ", e.getMessage());
		}

		List<FreePaperCollection> freePaperCollections = pageFreePaperCollections.getContent();
		List<PaperCollectionDto> paperCollectionDtos = new ArrayList<PaperCollectionDto>();
		freePaperCollections.forEach(freePaperCollection -> {
			PaperCollectionDto paperCollectionDto = new PaperCollectionDto();
			BeanUtils.copyProperties(freePaperCollection, paperCollectionDto);
			paperCollectionDtos.add(paperCollectionDto);
		});

		Page<PaperCollectionDto> paperCOllPage = new PageImpl<PaperCollectionDto>(paperCollectionDtos, pageable,
				pageFreePaperCollections.getTotalPages());

		return paperCOllPage;
	}

	@Override
	public List<PaperCollectionDto> getFreePaperCollectionByIds(List<String> ids)
			throws DataAccessException, MongoException {
		List<FreePaperCollection> freePaperCollections = freePaperCollectionRepository.findByIdIn(ids);
		List<PaperCollectionDto> paperCollectionDtos = new ArrayList<PaperCollectionDto>();
		freePaperCollections.forEach(freePaper -> {
			PaperCollectionDto paperCollectionDto = new PaperCollectionDto();
			BeanUtils.copyProperties(freePaper, paperCollectionDto);
			paperCollectionDtos.add(paperCollectionDto);
		});
		return paperCollectionDtos;
	}

	@Override
	public Page<PaperCollectionDto> getAllFreePaperCollByTestType(Pageable pageable, String testType)
			throws DataAccessException, MongoException {
		Page<FreePaperCollection> pageFreePaperCollections = freePaperCollectionRepository.findByTestType(pageable,
				testType);
		List<FreePaperCollection> freePaperCollections = pageFreePaperCollections.getContent();
		List<PaperCollectionDto> paperCollectionDtos = new ArrayList<PaperCollectionDto>();
		freePaperCollections.forEach(freePaperCollection -> {
			PaperCollectionDto paperCollectionDto = new PaperCollectionDto();
			BeanUtils.copyProperties(freePaperCollection, paperCollectionDto);
			paperCollectionDtos.add(paperCollectionDto);
		});

		Page<PaperCollectionDto> paperCOllPage = new PageImpl<PaperCollectionDto>(paperCollectionDtos, pageable,
				pageFreePaperCollections.getTotalPages());

		return paperCOllPage;
	}

	@Override
	public void updateFreePaperState(String id, PaperStateStatus paperStateStatus) {
		updateFreePaperStateWithValidity(id, paperStateStatus, null, null);
	}

	@Override
	public void updateFreePaperStateWithValidity(String id, PaperStateStatus paperStateStatus,
			Long validityStartDate, Long validityEndDate) {

		Optional<FreePaperCollection> freePaperCollectionOptional = freePaperCollectionRepository.findById(id);
		if (freePaperCollectionOptional.isPresent()) {
			FreePaperCollection freePaperCollection = freePaperCollectionOptional.get();

			// Update status
			freePaperCollection.setPaperStateStatus(paperStateStatus);

			// Update validity dates if provided (typically for ACTIVE status)
			if (validityStartDate != null) {
				freePaperCollection.setValidityRangeStartDateTime(validityStartDate);
			}
			if (validityEndDate != null) {
				freePaperCollection.setValidityRangeEndDateTime(validityEndDate);
			}

			// Save the updated paper
			freePaperCollectionRepository.save(freePaperCollection);
			LOGGER.info("Updated Free Paper state to {} for id {}", paperStateStatus, id);
		} else {
			LOGGER.warn("Free Paper not found for id {}", id);
			throw new IllegalArgumentException("Paper not found with id: " + id);
		}
	}

	@Override
	public void deleteFreePaper(String id) throws IllegalArgumentException {
		Optional<FreePaperCollection> freePaperCollectionOptional = freePaperCollectionRepository.findById(id);
		if (freePaperCollectionOptional.isPresent()) {
			FreePaperCollection freePaperCollection = freePaperCollectionOptional.get();

			// Only allow deletion of DRAFT papers (SUPERADMIN access is enforced at API level)
			if (freePaperCollection.getPaperStateStatus() == PaperStateStatus.ACTIVE ||
					freePaperCollection.getPaperStateStatus() == PaperStateStatus.APPROVED) {
				LOGGER.warn("Cannot delete ACTIVE/APPROVED paper: {}", id);
				throw new IllegalArgumentException("Cannot delete paper with status: " + freePaperCollection.getPaperStateStatus());
			}

			freePaperCollectionRepository.deleteById(id);
			LOGGER.info("Deleted Free Paper with id {}", id);
		} else {
			LOGGER.warn("Free Paper not found for deletion: {}", id);
			throw new IllegalArgumentException("Paper not found with id: " + id);
		}
	}

}
