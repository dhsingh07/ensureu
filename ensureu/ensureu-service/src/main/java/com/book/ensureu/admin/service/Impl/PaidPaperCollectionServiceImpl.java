package com.book.ensureu.admin.service.Impl;

import java.util.*;
import java.util.stream.Collectors;

import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.dto.PaperInfo;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.book.ensureu.repository.PaidPaperCollectionRepository;
import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.admin.dto.PaperCollectionDto;
import com.book.ensureu.admin.service.PaidPaperCollectionService;
import com.book.ensureu.constant.PaperType;
import com.book.ensureu.model.FreePaperCollection;
import com.book.ensureu.model.PaidPaperCollection;
import com.book.ensureu.service.CounterService;
import com.mongodb.MongoException;

/**
 * @author dharmendra.singh
 *
 */
@Service
public class PaidPaperCollectionServiceImpl implements PaidPaperCollectionService {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PaidPaperCollectionServiceImpl.class);

	@Autowired
	PaidPaperCollectionRepository testPaperCollectionRepository;

	@Autowired
	CounterService counterService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void createTestPaperInCollection(List<PaidPaperCollection> paidPaperCollectionList) {
		testPaperCollectionRepository.saveAll(paidPaperCollectionList);

	}

	@Override
	public void createPaidPaperInCollection(PaidPaperCollection paidPaperCollection) {
		try {
		if (paidPaperCollection != null && paidPaperCollection.getId()!=null) {
			PaidPaperCollection paidPaperCollectionInDb = getPaidPaperCollectionById(paidPaperCollection.getId());
			if (paidPaperCollectionInDb != null && paidPaperCollectionInDb.getId() != null) {
				paidPaperCollection.setId(paidPaperCollectionInDb.getId());
			} else {
				//testPaperCollection.setId(counterService.increment(CounterEnum.TESTPAPERCOLLECTION));
			}
		}
		LOGGER.info("[paidPaperColl type--->" + paidPaperCollection.getPaperType() + "paidpaper collection id: "
				+ paidPaperCollection.getId());
		testPaperCollectionRepository.save(paidPaperCollection);
		
		}catch(Exception ex) {
			LOGGER.info("paidpaperCollection save id {}",
					paidPaperCollection.getId(), ex);
			throw ex;
		}
	}

	@Override
	public PaperCollectionDto getTestPaperCollectionById(String id) throws MongoException, DataAccessException {
		
		Optional<PaidPaperCollection> paidPaper=testPaperCollectionRepository.findById(id);
		PaperCollectionDto paperCollectionDto=null;
		if(paidPaper.isPresent()) {
			PaidPaperCollection paperCollection= paidPaper.get();
			paperCollectionDto=new PaperCollectionDto();
			BeanUtils.copyProperties(paperCollection, paperCollectionDto);
		}
		return paperCollectionDto;
	}

	@Override
	public Page<PaperCollectionDto> getAllPaidPaperCollection(PaperType PaperType,Pageable pageable) throws MongoException, DataAccessException {	
		
		List<PaperStateStatus> paperStateStatus=Arrays.asList(PaperStateStatus.DRAFT,PaperStateStatus.ACTIVE,PaperStateStatus.APPROVED);
		Page<PaidPaperCollection> pagePaidPaperCollection = null;
		try {
			pagePaidPaperCollection = testPaperCollectionRepository.findByPaperStateStatusIn(paperStateStatus,PaperType,pageable);
		} catch (Exception e) {
			LOGGER.error("Paper Fetch Issue {}",e.getMessage());
		}
		List<PaidPaperCollection> paidPaperCollections=pagePaidPaperCollection.getContent();
		List<PaperCollectionDto> paperCollectionDtos=new ArrayList<PaperCollectionDto>();
		paidPaperCollections.forEach(paidPaperCollection->{
			PaperCollectionDto paperCollectionDto=new PaperCollectionDto();
			BeanUtils.copyProperties(paidPaperCollection, paperCollectionDto);
			paperCollectionDtos.add(paperCollectionDto);
		});
		
		Page<PaperCollectionDto> paperCOllPage=new PageImpl<PaperCollectionDto>(paperCollectionDtos, pageable, paidPaperCollections.size());
		
		return paperCOllPage;
	}


	private PaidPaperCollection getPaidPaperCollectionById(String id) throws MongoException, DataAccessException {
		
		Optional<PaidPaperCollection> paidPaper=testPaperCollectionRepository.findById(id);
		PaidPaperCollection paperCollection=null;
		if(paidPaper.isPresent()) {
			 paperCollection= paidPaper.get();
		}
		return paperCollection;
	}

	@Override
	public void updatePaidPaperState(String id, PaperStateStatus paperStateStatus) {

		Optional<PaidPaperCollection> paidPaperCollectionOptional = testPaperCollectionRepository.findById(id);
		if (paidPaperCollectionOptional.isPresent()) {
			PaidPaperCollection paidPaperCollection = paidPaperCollectionOptional.get();
			if (paperStateStatus.equals(PaperStateStatus.ACTIVE)
					&& paidPaperCollection.getPaperStateStatus().equals(PaperStateStatus.DRAFT)) {
				paidPaperCollection.setPaperStateStatus(paperStateStatus);
			} else if (paperStateStatus.equals(PaperStateStatus.APPROVED)
					&& paidPaperCollection.getPaperStateStatus().equals(PaperStateStatus.ACTIVE)) {
				paidPaperCollection.setPaperStateStatus(paperStateStatus);
			} else {
				throw new IllegalAccessError("Status is worng to update");
			}
		}		
	}


	@Override
	public List<PaperInfo> fetchPaperInfoList(PaperSubCategory paperSubCategory, Pageable pageable, Boolean taken) {
		taken = (taken!=null)? taken : true;
        List<PaidPaperCollection> paidPaperCollectionList = testPaperCollectionRepository.findByPaperSubCategoryAndTaken(paperSubCategory,taken,pageable);
		LOGGER.info("[fetchPaperInfoList] paperSubCategory {} paidPaperCollectionList {}",paperSubCategory,paidPaperCollectionList);
		if(Objects.isNull(paidPaperCollectionList) || paidPaperCollectionList.isEmpty()){
        	return new LinkedList<>();}

        return    paidPaperCollectionList.stream().map(paidPaperCollection ->
        	          PaperInfo.builder()
					 .id(paidPaperCollection.getId())
					 .paperName(paidPaperCollection.getPaperName())
					 .createdDate(paidPaperCollection.getCreateDateTime())
					 .validity(paidPaperCollection.getValidityRangeEndDateTime())
					 .build()
		       ).collect(Collectors.toList());
	}

	@Override
	public List<PaperInfo> fetchFreshPaperInfoList(List<String> paperIds) {
		List<PaidPaperCollection> paidPaperCollections = testPaperCollectionRepository.findByIdIn(paperIds);

		LOGGER.info("[fetchPaperInfoList] paperIds {} paidPaperCollections {}", paperIds, paidPaperCollections);

		if (paidPaperCollections == null || paidPaperCollections.isEmpty()) {
			return new LinkedList<>();
		}

		// Filter out any that are already taken
		return paidPaperCollections.stream()
				.filter(p -> !p.isTaken())
				.map(p -> PaperInfo.builder()
						.id(p.getId())
						.paperName(p.getPaperName())
						.createdDate(p.getCreateDateTime())
						.validity(p.getValidityRangeEndDateTime())
						.build())
				.collect(Collectors.toList());
	}



	@Override
	public void setTakenPaidPaperCollectionFlag(List<String> paperIdList, boolean flag){
		Query query = Query.query(Criteria.where("id").in(paperIdList));
		Update update = Update.update("taken",true);
		mongoTemplate.findAndModify(query,update, PaidPaperCollection.class);
	}

}
