package com.book.ensureu.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.book.ensureu.constant.CounterEnum;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.PaperStatus;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.AttemptedPaperDto;
import com.book.ensureu.dto.PaperDto;
import com.book.ensureu.model.PastPaper;
import com.book.ensureu.model.PastPaperCollection;
import com.book.ensureu.repository.PastPaperCollectionRepository;
import com.book.ensureu.repository.PastPaperRepository;
import com.book.ensureu.util.PastPaperConversionUtil;

@Service
@Qualifier("pastPaper")
public class PastPaperServiceImpl implements PaperService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PastPaperServiceImpl.class.getName());

	
	@Autowired
	@Lazy
	PastPaperCollectionRepository pastPaperCollectionRepository;
	
	@Autowired
	@Lazy
	PastPaperRepository pastPaperRepository;
	
	@Autowired
	CounterService counterService;
	
	
	/* (non-Javadoc)
	 * save past paper
	 * @see com.book.ensureu.service.PaperService#savePaper(com.book.ensureu.dto.PaperDto)
	 */
	@Override
	public void savePaper(PaperDto paperDto) {
		
		PastPaper pastPaper = PastPaperConversionUtil.pastPaperDtoToModel(paperDto);
		if (paperDto.getId() == null) {
			pastPaper.setId(counterService.increment(CounterEnum.PASTPAPER));
		}
		LOGGER.info("User Mapping pastPaper UserId [" + pastPaper.getUserId() + "] paperId [" + pastPaper.getPaperId()
				+ "]");
		pastPaperRepository.save(pastPaper);
	}


	@Override
	public void updatePaper(PaperDto paperDto) {
		LOGGER.info("Update User with past paper..");
		PastPaper pastPaper = PastPaperConversionUtil.pastPaperDtoToModel(paperDto);
		LOGGER.info("User Mapping PastPaper UserId [" + pastPaper.getUserId() + "] paperId [" + pastPaper.getPaperId()
				+ "]");
		pastPaperRepository.save(pastPaper);
	}

	/* (non-Javadoc)
	 * To-DO...
	 * @see com.book.ensureu.service.PaperService#submitPaper(com.book.ensureu.dto.AttemptedPaperDto)
	 */
	@Override
	public void submitPaper(AttemptedPaperDto attemptedPaper) {

	}

	@Override
	public PaperDto getPaperById(Long id, TestType typePaper) {
		Optional<PastPaper> pastPaperOptional = pastPaperRepository.findById(id);
		if (pastPaperOptional.isPresent()) {
			return PastPaperConversionUtil.pastPaperToDto(pastPaperOptional.get());
		}
		return null;
	}

	@Override
	public List<PaperDto> getPaperByPaperId(String paperId, TestType typePaper) {
		return null;
	}

	@Override
	public PaperDto getPaperByPaperIdAndUserId(String paperId, String userId, TestType typePaper) {
		return null;
	}

	@Override
	public List<PaperDto> getPaperByUserId(String userId) {
		return null;
	}

	@Override
	public List<PaperDto> getPaperByUserIdAndPaperType(String userId, PaperType paperType) {
		return null;
	}

	@Override
	public List<PaperDto> getPaperByUserIdAndPaperTypeAndTestType(String userId, PaperType paperType,
			TestType testType) {
		return null;
	}

	@Override
	public PaperDto paperMappedUserByPaperStatus(String userId, TestType testType, PaperStatus paperStatus,
			String paperId) {
		
		if (PaperStatus.DONE.equals(paperStatus)) {
			PastPaper pastPaperFromDb = pastPaperRepository
					.findByUserIdAndPaperId(userId, paperId);
			if (pastPaperFromDb != null) {
				return PastPaperConversionUtil.pastPaperToDto(pastPaperFromDb);
			} else {
				LOGGER.info("PaperId is not valid");
				return null;
			}

		} else {
			
			PastPaper pastPaperFromDb = pastPaperRepository
					.findByUserIdAndPaperId(userId, paperId);
			
			if(pastPaperFromDb!=null && pastPaperFromDb.getPaperStatus().equals(PaperStatus.DONE)) {
				LOGGER.info("Paper is already completed");
				return null;
			}
			
			/*if (paperStatus.equals(paperStatus.START)) {
				if (pastPaperFromDb != null) {
					return PastPaperConversionUtil.pastPaperToDto(pastPaperFromDb);
				} else {

					// need to add validity date to paper...
					// valididy date from subscription and user entitlemt
					// collection.

					Optional<PastPaperCollection> pastPaperCollOpt = pastPaperCollectionRepository
							.findById(paperId);
					if (pastPaperCollOpt.isPresent()) {
						PastPaperCollection pastPaperCollection = pastPaperCollOpt
								.get();
						PastPaper pastPaper = PastPaperConversionUtil.createPastPaperByPastCollectionPaper(pastPaperCollection, testType, userId,"UserMapped");
						pastPaper.setId(counterService
								.increment(CounterEnum.PASTPAPER));
						pastPaper = pastPaperRepository.save(pastPaper);
						return PastPaperConversionUtil.pastPaperToDto(pastPaper);

					} else {
						throw new IllegalArgumentException(
								"PaperId is not valid");
					}
				}
			} else if (paperStatus.equals(paperStatus.RESUME)
					|| paperStatus.equals(paperStatus.INPROGRESS)) {
				return PastPaperConversionUtil.pastPaperToDto(pastPaperFromDb);
			}*/
			
			//always start test and overwrite submitted paper in user pastPaper collection.
			if(paperStatus.equals(paperStatus.START)) {
				Optional<PastPaperCollection> pastPaperCollOpt = pastPaperCollectionRepository
						.findById(paperId);
				PastPaper pastPaper=null;
				PastPaperCollection pastPaperCollection=null;
				if (pastPaperCollOpt.isPresent()) {
					 pastPaperCollection = pastPaperCollOpt
							.get();
					 pastPaper = PastPaperConversionUtil.createPastPaperByPastCollectionPaper(pastPaperCollection, testType, userId,"UserMapped");

					//return PastPaperConversionUtil.pastPaperToDto(pastPaper);

				} else {
					throw new IllegalArgumentException(
							"PaperId is not valid");
				}
				
				if (pastPaperFromDb != null) {
					 pastPaper.setId(pastPaperFromDb.getId());
					return PastPaperConversionUtil.pastPaperToDto(pastPaper);
				} else {
					pastPaper.setId(counterService
							.increment(CounterEnum.PASTPAPER));
					pastPaper = pastPaperRepository.save(pastPaper);
					return PastPaperConversionUtil.pastPaperToDto(pastPaper);
			}
			}else if (paperStatus.equals(paperStatus.RESUME)
					|| paperStatus.equals(paperStatus.INPROGRESS)) {
				return PastPaperConversionUtil.pastPaperToDto(pastPaperFromDb);
			}
		}
		
		return null;
	}


	@Override
	public long getAllCountPaper() {
		return 0;
	}

	@Override
	public long getPaperCountByPaperTypeAndTestType(PaperType paperType, TestType testType) {
		return 0;
	}

	@Override
	public long getPaperCountByPaperTypeAndTestTypeAndPaperCategory(PaperType paperType, TestType testType,
			PaperCategory paperCategory) {
		return 0;
	}

	@Override
	public long getPaperCountByPaperTypeAndTestTypeAndPaperCategoryAndPaperSubCategory(PaperType paperType,
			TestType testType, PaperCategory paperCategory, PaperSubCategory paperSubCategory) {
		return 0;
	}

	@Override
	public long getPaperCountByPaperType(PaperType paperType) {
		return 0;
	}

	@Override
	public long getAllCountPaperCollectionByPaperType(PaperType paperType, TestType Paper) {
		return 0;
	}

	@Override
	public List<PaperDto> getPaperStatusDetailsByPaperIds(String userId, List<String> paperIds, TestType testType)
			throws Exception {
		return null;
	}

	@Override
	public List<PaperDto> getPaperStatusDetailsByStatusAndPaperType(String userId, TestType testType,
			PaperType paperType, PaperStatus paperStatus, PaperCategory paperCategory) throws Exception {
		
		List<PaperDto> paperDto = null;
		try {
			List<PastPaper> pastPaperList = null;
			LOGGER.info("getPaperStatusDetailsByStatusAndPaperType PAST userId [" + userId + "] paperStatus ["
					+ paperStatus + "]");
			if (paperCategory != null) {
				pastPaperList = pastPaperRepository.findPastPaperUsingUserIdAndPaperStatusAndPaperCategory(userId,
						paperStatus, paperType, paperCategory);
			} else {
				pastPaperList = pastPaperRepository.findPastPaperUsingUserIdAndPaperStatus(userId, paperStatus,
						paperType);
			}
			if (pastPaperList != null && !pastPaperList.isEmpty()) {
				paperDto = PastPaperConversionUtil.pastPaperToDto(pastPaperList);
			}
		} catch (Exception ex) {
			LOGGER.error("error getPaperStatusDetailsByStatusAndPaperType ", ex);
			throw ex;
		}
		return paperDto;
	}

	
	/* (non-Javadoc)
	 * NA applicable for pastpaper
	 * @see com.book.ensureu.service.PaperService#getMissedPapersByUsers(java.lang.String, com.book.ensureu.constant.TestType, com.book.ensureu.constant.PaperType, com.book.ensureu.constant.PaperCategory)
	 */
	@Override
	public List<PaperDto> getMissedPapersByUsers(String userId, TestType testType, PaperType paperType,
			PaperCategory paperCategory) throws Exception {
		return null;
	}

	@Override
	public List<PaperDto> getPaperStatusDetailsByPaperCateoryORTestType(String userId, TestType testType,
			PaperType paperType, PaperCategory paperCategory) throws Exception {

		List<PastPaper> pastPapers = new ArrayList<>();

		List<PastPaperCollection> pastPaperCollections = pastPaperCollectionRepository
				.findByPaperTypeAndPaperCategory(paperType, paperCategory);

		if (userId != null) {
			List<PastPaper> pastPaperList = pastPaperRepository.findPastPaperUserIdAndPaperTypeAndPaperCategory(userId,
					paperType, paperCategory);

			if (pastPaperList != null && !pastPaperList.isEmpty()) {
				Set<String> paperIdSet = pastPaperList.stream().map(pastPaper -> pastPaper.getPaperId())
						.collect(Collectors.toSet());
				List<PastPaperCollection> paperCollList = pastPaperCollections.stream()
						.filter(paperColl -> !paperIdSet.contains(paperColl.getId())).collect(Collectors.toList());
				List<PastPaper> pastPapersColl = new ArrayList<>();
				paperCollList.forEach(pastPaperColl -> {
					pastPapersColl.add(PastPaperConversionUtil.createPastPaperByPastCollectionPaper(pastPaperColl,
							testType, userId, null));
				});

				return PastPaperConversionUtil.pastPaperToDto(
						Stream.of(pastPaperList, pastPapersColl).flatMap(p -> p.stream()).collect(Collectors.toList()));
			}
		}

		pastPaperCollections.forEach(pastPaperColl -> {
			pastPapers.add(PastPaperConversionUtil.createPastPaperByPastCollectionPaper(pastPaperColl, testType, userId,
					null));
		});
		return PastPaperConversionUtil.pastPaperToDto(pastPapers);
	}

}
