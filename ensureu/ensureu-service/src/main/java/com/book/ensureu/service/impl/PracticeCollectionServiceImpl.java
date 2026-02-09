package com.book.ensureu.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.book.ensureu.model.PracticePaperCollection;
import com.book.ensureu.repository.PracticeCollectionRepository;
import com.book.ensureu.service.CounterService;
import com.book.ensureu.service.PracticePaperCollectionService;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * @author jatin.bansal
 *
 */
@Service
public class PracticeCollectionServiceImpl implements PracticePaperCollectionService {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PracticeCollectionServiceImpl.class);

	@Autowired
	CounterService counterService;

	@Autowired
	PracticeCollectionRepository practicePaperRepository;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	MongoOperations mongoOperations;
	
	private static String MONGO_KEY_PAPERCATEGORY = "paperCategory";

	@Override
	public void createPracticePaperInCollection(List<PracticePaperCollection> practicePaperCollection)
			throws MongoException {
		if (practicePaperCollection != null) {
			practicePaperRepository.saveAll(practicePaperCollection);
		} else {
			LOGGER.info("Parameters must not be null");
		}
	}

	@Override
	public void createPracticePaperInCollection(PracticePaperCollection practicePaperCollection) throws MongoException {
		// TODO Auto-generated method stub
		if (practicePaperCollection != null) {
			practicePaperRepository.save(practicePaperCollection);
		} else {
			LOGGER.info("Parameters must not be null");
		}
	}

	@Override
	public PracticePaperCollection getPracticeCollectionById(String id) throws MongoException, DataAccessException {
		// TODO Auto-generated method stub
		Optional<PracticePaperCollection> res = null;
		if (id != null)
			res = practicePaperRepository.findById(id);
		else
			LOGGER.info("Parameters must not be null");
		return res != null && res.isPresent() ? res.get() : null;
	}

	@Override
	public Page<PracticePaperCollection> getAllPracticeCollection(Pageable pageable)
			throws MongoException, DataAccessException {
		// TODO Auto-generated method stub
		return practicePaperRepository.findAll(pageable);
	}
	
	@Override
	public List<DBObject> getTitleWiseCountAndQuestionsByPaperCategory(String paperCategory,String sectionTitle,String subSectionTitle) {
		MatchOperation matchOp = Aggregation.match(new Criteria(MONGO_KEY_PAPERCATEGORY).is(paperCategory));
		UnwindOperation unwind1 =  Aggregation.unwind("pattern.sections");
		MatchOperation matchSecTitle = null; 
		if(sectionTitle!=null && !sectionTitle.isEmpty()) {
			matchSecTitle = Aggregation.match(new Criteria("pattern.sections.title").is(sectionTitle));
		}
		UnwindOperation unwind2 =  Aggregation.unwind("pattern.sections.subSections");
		MatchOperation matchSubSecTitle = null; 
		if(subSectionTitle!=null && !subSectionTitle.isEmpty()) {
			matchSubSecTitle = Aggregation.match(new Criteria("pattern.sections.subSections.title").is(subSectionTitle));
		}
		UnwindOperation unwind3 =  Aggregation.unwind("pattern.sections.subSections.questionData.questions");
		Fields fields = Fields.from(Fields.field("sectionTitle","$pattern.sections.title"));
		fields = fields.and("subSectionTitle","$pattern.sections.subSections.title");
		GroupOperation groupOp = Aggregation.group(fields).push("$pattern.sections.subSections.questionData.questions")
				.as("questions")
				.count().as("questionCount");
		Aggregation aggreagation = null;
		if(matchSecTitle!=null && matchSubSecTitle!=null) {
			aggreagation = 	Aggregation.newAggregation(matchOp,unwind1,matchSecTitle,unwind2,matchSubSecTitle,unwind3,groupOp);
		}else {
			aggreagation = Aggregation.newAggregation(matchOp,unwind1,unwind2,unwind3,groupOp);
		}
		AggregationResults<DBObject> result = mongoOperations.aggregate(aggreagation,PracticePaperCollection.class, DBObject.class);
		if(result!=null){
			return result.getMappedResults();
		}else{
			return null;
		}
	}
	
	@Override
	public List<DBObject> getTitleWiseCountByPaperCategory(String paperCategory) {
		MatchOperation matchOp = Aggregation.match(new Criteria(MONGO_KEY_PAPERCATEGORY).is(paperCategory));
		UnwindOperation unwind1 =  Aggregation.unwind("pattern.sections");
		UnwindOperation unwind2 =  Aggregation.unwind("pattern.sections.subSections");
		UnwindOperation unwind3 =  Aggregation.unwind("pattern.sections.subSections.questionData.questions");
		Fields fields = Fields.from(Fields.field("sectionTitle","$pattern.sections.title"));
		fields = fields.and("subSectionTitle","$pattern.sections.subSections.title");
		GroupOperation groupOp = Aggregation.group(fields)
				.count().as("questionCount");
		Aggregation aggreagation = Aggregation.newAggregation(matchOp,unwind1,unwind2,unwind3,groupOp);
		AggregationResults<DBObject> result = mongoOperations.aggregate(aggreagation,PracticePaperCollection.class, DBObject.class);
		if(result!=null){
			return result.getMappedResults();
		}else{
			return null;
		}
	}

}
