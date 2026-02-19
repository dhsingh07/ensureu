package com.book.ensureu.admin.service.Impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.admin.service.FreePaperCollectionService;
import com.book.ensureu.admin.service.PaidPaperCollectionService;
import com.book.ensureu.constant.ApplicationConstant;
import com.book.ensureu.constant.QuestionAttemptedStatus;
import com.book.ensureu.constant.QuestionSelectionType;
import com.book.ensureu.constant.SectionType;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.CsvBean;
import com.book.ensureu.dto.CsvColumnBean;
import com.book.ensureu.dto.PaperInfoDataDto;
import com.book.ensureu.dto.SectionDeatilDto;
import com.book.ensureu.exception.GenericException;
import com.book.ensureu.model.FreePaperCollection;
import com.book.ensureu.model.Options;
import com.book.ensureu.model.PaidPaperCollection;
import com.book.ensureu.model.Paper;
import com.book.ensureu.model.PastPaperCollection;
import com.book.ensureu.model.Pattern;
import com.book.ensureu.model.PracticePaperCollection;
import com.book.ensureu.model.Problem;
import com.book.ensureu.model.Question;
import com.book.ensureu.model.QuestionData;
import com.book.ensureu.model.QuizPaperCollection;
import com.book.ensureu.model.Sections;
import com.book.ensureu.model.Solution;
import com.book.ensureu.model.SubSections;
import com.book.ensureu.service.PaperAggregatorService;
import com.book.ensureu.service.PaperInfoDataService;
import com.book.ensureu.service.PastPaperCollectionService;
import com.book.ensureu.service.PracticePaperCollectionService;
import com.book.ensureu.service.QuizPaperCollectionService;
import com.book.ensureu.util.HashUtil;
import com.book.ensureu.util.PaperImageUploadHelper;
import com.ensureu.commons.gcloud.util.GoogleCloudStorageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SSCPaperAggregatorServiceImpl<T> implements PaperAggregatorService<T> {

	private final Logger LOGGER = LoggerFactory.getLogger(SSCPaperAggregatorServiceImpl.class.getName());

	@Lazy
	@Autowired
	private PaidPaperCollectionService testPaperCollectionService;

	@Lazy
	@Autowired
	private FreePaperCollectionService freePaperCollectionService;

	@Lazy
	@Autowired
	private PracticePaperCollectionService practicePaperCollectionService;

	@Lazy
	@Autowired
	private QuizPaperCollectionService quizPaperCollectionService;

	@Autowired
	private PastPaperCollectionService pastPaperCollectionService;

	@Autowired
	private PaperInfoDataService paperInfoDataService;

	private String staticContentLoc;

	private String csvFileLoc;

	@Autowired
	@Lazy
	private GoogleCloudStorageUtil googleCloudStorageUtil;

	private final String imageType = "image/png";

	// only paid paper create..
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.book.assessu.service.PaperAggregatorService#createPaidPaper(java.util.
	 * List)
	 */
	@Override
	public void createPaidPaper(List<T> paperContent, String path, String imagePath) {
		if (paperContent.get(0) instanceof CsvBean) {
			csvFileLoc = path + "/";
			staticContentLoc = imagePath + "/";
			List<CsvBean> csvBeanListContent = (List<CsvBean>) paperContent;
			PaidPaperCollection paidPaperCollection = converCsvBeanToPaperCollection(csvBeanListContent);
			paidPaperCollection.setPaperStateStatus(PaperStateStatus.DRAFT);
			testPaperCollectionService.createPaidPaperInCollection(paidPaperCollection);
			ObjectMapper objectMapper = new ObjectMapper();
			String val = null;
			try {
				val = objectMapper.writeValueAsString(paidPaperCollection);
			} catch (JsonProcessingException e) {
				LOGGER.error("Error while conversion into json", e);
			}
			System.out.println(val);
		}

	}

	// this for free,practice and quiz paper create
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.book.assessu.service.PaperAggregatorService#createFreeTypePaper(java.util
	 * .List, com.book.assessu.constant.TestType)
	 */
	@Override
	public void createFreeTypePaper(List<T> paperContent, TestType testType, String path, String imagePath) {
		if (paperContent.get(0) instanceof CsvBean) {
			csvFileLoc = path + "/";
			staticContentLoc = imagePath;
			List<CsvBean> csvBeanListContent = (List<CsvBean>) paperContent;
			FreePaperCollection freePaperCollection = convertCsvBeanToFreePaperCollection(csvBeanListContent, testType);
			freePaperCollectionService.createFreePaperInCollection(freePaperCollection);
		}
	}

	/**
	 * @param csvBeanListContent
	 * @param testType
	 * @return FreePaperColletion
	 */
	private FreePaperCollection convertCsvBeanToFreePaperCollection(List<CsvBean> csvBeanListContent,
			TestType testType) {
		FreePaperCollection freePaperCollection = new FreePaperCollection();
		Pattern<Sections<SubSections<Question<Problem>>>> pattern = getPaperPattern(freePaperCollection,
				csvBeanListContent, testType);
		freePaperCollection.setPattern(pattern);
		freePaperCollection.setPaperStateStatus(PaperStateStatus.DRAFT);
		return freePaperCollection;

	}

	private <P> Pattern<Sections<SubSections<Question<Problem>>>> getPaperPattern(P p, List<CsvBean> csvBeanListContent,
			TestType testType) {

		Map<SectionType, Map<String, List<CsvBean>>> sectionVsSubSectionVsPaperObj = new HashMap<>();
		String subCategory = csvBeanListContent.get(0).getPaperSubCategory() != null
				? csvBeanListContent.get(0).getPaperSubCategory().toString()
				: null;
		if (TestType.PRACTICE.equals(testType)) {
			subCategory = null;
		}
		String id = HashUtil.hashByMD5(csvBeanListContent.get(0).getPaperType().toString(),
				csvBeanListContent.get(0).getPaperCategory().toString(), subCategory, testType.toString());
		PaperInfoDataDto paperInfoDataDto = null;
		Pattern<Sections<SubSections<Question<Problem>>>> pattern = null;
		try {
			paperInfoDataDto = paperInfoDataService.getPaperInfoDataById(id);
		} catch (GenericException e) {
			LOGGER.error("paper details not available..");
			return null;
		}

		csvBeanListContent.forEach(csvObj -> {
			CsvColumnBean csvColumnValue = (CsvColumnBean) csvObj;
			if (csvColumnValue.getSectionType().equals(SectionType.EnglishLanguage)) {
				updateSectionVsSubSectionMap(csvObj, sectionVsSubSectionVsPaperObj);
			} else if (csvColumnValue.getSectionType().equals(SectionType.GeneralAwareness)) {
				updateSectionVsSubSectionMap(csvObj, sectionVsSubSectionVsPaperObj);
			} else if (csvColumnValue.getSectionType().equals(SectionType.GeneralIntelligence)) {
				updateSectionVsSubSectionMap(csvObj, sectionVsSubSectionVsPaperObj);
			} else if (csvColumnValue.getSectionType().equals(SectionType.QuantitativeAptitude)) {
				updateSectionVsSubSectionMap(csvObj, sectionVsSubSectionVsPaperObj);
			}
		});

		System.out.println("mapSize : : " + sectionVsSubSectionVsPaperObj.size());
		if (sectionVsSubSectionVsPaperObj != null) {

			Map<String, List<CsvBean>> mapVsCsvBean = (Map<String, List<CsvBean>>) sectionVsSubSectionVsPaperObj
					.values().toArray()[0];
			List<CsvBean> csvBeanL = (List<CsvBean>) mapVsCsvBean.values().toArray()[0];
			CsvBean csvBeanOne = csvBeanL.get(0);
			CsvColumnBean csvCommBean = (CsvColumnBean) csvBeanOne;
			createParentPaperValue(p, csvBeanOne, paperInfoDataDto, testType);

			// create pattern
			LOGGER.info("create paper pattern....");
			pattern = createPatternForPaper(sectionVsSubSectionVsPaperObj, csvBeanOne, paperInfoDataDto, csvFileLoc);
		}

		return pattern;
	}

	// create pattern..

	/**
	 * @param sectionVsSubSectionVsPaperObj
	 * @param csvBeanOne
	 * @param paperInfoDataDto
	 * @return
	 */
	private Pattern<Sections<SubSections<Question<Problem>>>> createPatternForPaper(
			Map<SectionType, Map<String, List<CsvBean>>> sectionVsSubSectionVsPaperObj, CsvBean csvBeanOne,
			PaperInfoDataDto paperInfoDataDto, String fileLocation) {
		List<Sections<SubSections<Question<Problem>>>> sectionList = new LinkedList<>();
		CsvColumnBean csvCommBean = (CsvColumnBean) csvBeanOne;

		AtomicInteger count = new AtomicInteger();
		paperInfoDataDto.getSections().forEach(sectionDto -> {
			int c = count.getAndIncrement();
			sectionVsSubSectionVsPaperObj.forEach((k, v) -> {
				if (sectionDto.getSectionType().equals(k)) {
					Map<String, List<CsvBean>> mapCsvBeanList = (Map<String, List<CsvBean>>) v;
					List<CsvBean> engCsvBean = (List<CsvBean>) mapCsvBeanList.values().toArray()[0];
					List<SubSections<Question<Problem>>> subSectionsList = new LinkedList<>();
					mapCsvBeanList.forEach((x, y) -> {
						AtomicInteger val = new AtomicInteger();

						SubSections<Question<Problem>> subSectionValue = createPaperSubSections((List<CsvBean>) y,
								val.getAndIncrement(), fileLocation);
						subSectionsList.add(subSectionValue);
					});
					Sections<SubSections<Question<Problem>>> secs = addSections(engCsvBean.get(0), subSectionsList, c,
							sectionDto);
					sectionList.add(secs);

				}
			});
		});

		Pattern<Sections<SubSections<Question<Problem>>>> pattern = new Pattern<>();
		String id = HashUtil.hashByMD5(csvBeanOne.getPaperType().toString(), csvBeanOne.getPaperCategory().toString(),
				csvBeanOne.getPaperSubCategory() != null ? csvBeanOne.getPaperSubCategory().toString() : null,
				String.valueOf(new Date().getTime()));
		pattern.setId(id);
		pattern.setCreatedOn(LocalDate.now().toString());
		pattern.setPaperType(csvBeanOne.getPaperType());
		pattern.setTime(paperInfoDataDto.getTotalTime());
		pattern.setTitle(csvCommBean.getPaperName());
		pattern.setSections(sectionList);
		Paper<Sections<SubSections<Question<Problem>>>> paper = new Paper<>();
		paper.setPattern(pattern);
		return pattern;
	}

	/**
	 * @param csvBeanListContent
	 * @return
	 */
	public PaidPaperCollection converCsvBeanToPaperCollection(List<CsvBean> csvBeanListContent) {

		Map<SectionType, Map<String, List<CsvBean>>> sectionVsSubSectionVsPaperObj = new LinkedHashMap<>();
		String subCategory = csvBeanListContent.get(0).getPaperSubCategory() != null
				? csvBeanListContent.get(0).getPaperSubCategory().toString()
				: null;
		String id = HashUtil.hashByMD5(csvBeanListContent.get(0).getPaperType().toString(),
				csvBeanListContent.get(0).getPaperCategory().toString(), subCategory, TestType.PAID.toString());
		PaperInfoDataDto paperInfoDataDto = null;
		try {
			paperInfoDataDto = paperInfoDataService.getPaperInfoDataById(id);
		} catch (GenericException e) {
			LOGGER.error("paper details not available..");
			return null;
		}

		csvBeanListContent.forEach(csvObj -> {
			CsvColumnBean csvColumnValue = (CsvColumnBean) csvObj;
			if (csvColumnValue.getSectionType().equals(SectionType.EnglishLanguage)) {
				updateSectionVsSubSectionMap(csvObj, sectionVsSubSectionVsPaperObj);
			} else if (csvColumnValue.getSectionType().equals(SectionType.GeneralAwareness)) {
				updateSectionVsSubSectionMap(csvObj, sectionVsSubSectionVsPaperObj);
			} else if (csvColumnValue.getSectionType().equals(SectionType.GeneralIntelligence)) {
				updateSectionVsSubSectionMap(csvObj, sectionVsSubSectionVsPaperObj);
			} else if (csvColumnValue.getSectionType().equals(SectionType.QuantitativeAptitude)) {
				updateSectionVsSubSectionMap(csvObj, sectionVsSubSectionVsPaperObj);
			}
		});

		LOGGER.info("Map Size for Sections:{} ", sectionVsSubSectionVsPaperObj.size());
		sectionVsSubSectionVsPaperObj.forEach((k, v) -> {
			LOGGER.info("sectionName-->: {}", k);
			v.forEach((x, y) -> {
				y.forEach(yva -> {
					CsvColumnBean col = (CsvColumnBean) yva;
					LOGGER.info("subsectionName : {}", col.getSubSectionName());
				});
			});

		});

		PaidPaperCollection paidPaperCollection = new PaidPaperCollection();

		if (sectionVsSubSectionVsPaperObj != null) {
			Map<String, List<CsvBean>> mapVsCsvBean = (Map<String, List<CsvBean>>) sectionVsSubSectionVsPaperObj
					.values().toArray()[0];
			List<CsvBean> csvBeanL = (List<CsvBean>) mapVsCsvBean.values().toArray()[0];
			CsvBean csvBeanOne = csvBeanL.get(0);
			// CsvColumnBean csvCommBean = (CsvColumnBean) csvBeanOne;
			createParentPaperValue(paidPaperCollection, csvBeanL.get(0), paperInfoDataDto, TestType.PAID);
			Pattern<Sections<SubSections<Question<Problem>>>> pattern = createPatternForPaper(
					sectionVsSubSectionVsPaperObj, csvBeanOne, paperInfoDataDto, csvFileLoc);
			paidPaperCollection.setPattern(pattern);

		}
		return paidPaperCollection;

	}

	// create paperSubSections
	/**
	 * @param csvContentRow
	 * @param sNo
	 * SubSections formation with image upload to bucket with unique Question-id
	 * @return
	 */
	private SubSections<Question<Problem>> createPaperSubSections(List<CsvBean> csvContentRow, int sNo,
			String fileLocation) {

		CsvColumnBean csvColumnBean = null;
		CsvBean csvBean = null;

		String imagePath = staticContentLoc;
		List<Question<Problem>> questionList = new LinkedList<>();
		for (int i = 0; i < csvContentRow.size(); i++) {
			csvBean = csvContentRow.get(i);
			csvColumnBean = (CsvColumnBean) csvContentRow.get(i);

			// question guid id....
			String id = HashUtil.hashByMD5(csvBean.getPaperType().toString(), csvBean.getPaperCategory().toString(),
					csvBean.getPaperSubCategory() != null ? csvBean.getPaperSubCategory().toString() : null,
					csvColumnBean.getSectionName(), String.valueOf(System.nanoTime()), i + "");
			LOGGER.info("guid id:: " + id);

			Problem problem = new Problem();
			problem.setCo(java.util.Arrays.asList(String.valueOf(csvColumnBean.getCorrectOption())));
			if (csvColumnBean.getQuestionImage() != null && !csvColumnBean.getQuestionImage().isEmpty()) {

				LOGGER.info("filePath " + imagePath);
				LOGGER.info("csvColumnBean.getQuestionImage()  " + csvColumnBean.getQuestionImage());
				File fileImage = new File(imagePath + csvColumnBean.getQuestionImage());
				if (fileImage.exists()) {
					/*
					 * bufferedImage = ImageIO.read(fileImage); ByteArrayOutputStream
					 * byteArrayOutputStream=new ByteArrayOutputStream() {
					 * 
					 * @Override public synchronized byte[] toByteArray() { return this.buf; } };
					 * //it was use to write in drive. //ImageIO.write(bufferedImage, "png", new
					 * File(filePath, id + "-Q.png"));
					 * 
					 * //move to bucket ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
					 */
					try {
						PaperImageUploadHelper.imageUplaod(googleCloudStorageUtil, ApplicationConstant.BUCKET_NAME,
								fileImage, csvBean.getPaperType().toString().toLowerCase() + "/" + id + "-Q.png",
								imageType);
					} catch (Exception e) {
						LOGGER.error("File upload issue in ucket [" + id + "-Q.png]", e);
					}
					problem.setImage(id + "-Q.png");

				} else {
					LOGGER.info("File Dose not exist, need to add this file [" + id + "-Q.png]");
				}
				// need to do upload image in bucket.

				LOGGER.info("DONE....");
			}
			problem.setValue(csvColumnBean.getQuestion());
			List<Solution> solutions = new ArrayList<>();
			Solution solution = new Solution();
			solution.setAddedon(new Date().toString());

			if (csvColumnBean.getAnswerDescriptionImage() != null
					&& !csvColumnBean.getAnswerDescriptionImage().isEmpty()) {
				File fileImage = new File(imagePath + csvColumnBean.getAnswerDescriptionImage());
				if (fileImage.exists()) {
					try {
						PaperImageUploadHelper.imageUplaod(googleCloudStorageUtil, ApplicationConstant.BUCKET_NAME,
								fileImage, csvBean.getPaperType().toString().toLowerCase() + "/" + id + "-A.png",
								imageType);
					} catch (Exception e) {
						e.printStackTrace();
					}

					solution.setImage(id + "-A.png");
				} else {
					LOGGER.info("File Dose not exist, need to add this file [" + id + "-A.png]");
				}

				System.out.println("DONE....");
			}

			// solution.setImage(csvColumnBean.getAnswerDescriptionImage());
			solution.setValue(csvColumnBean.getAnswerDescription1());

			Solution solution1 = new Solution();
			solution1.setAddedon(new Date().toString());
			solution1.setImage(csvColumnBean.getAnswerDescriptionImage());
			solution1.setValue(csvColumnBean.getAnswerDescription2());

			solutions.add(solution);
			solutions.add(solution1);
			problem.setSolutions(solutions);

			List<Options> options = new ArrayList<>();
			Options option1 = new Options();
			if (csvColumnBean.getOption1_Image() != null && !csvColumnBean.getOption1_Image().isEmpty()) {
				File fileImage = new File(imagePath + csvColumnBean.getOption1_Image());
				if (fileImage.exists()) {
					try {
						PaperImageUploadHelper.imageUplaod(googleCloudStorageUtil, ApplicationConstant.BUCKET_NAME,
								fileImage, csvBean.getPaperType().toString().toLowerCase() + "/" + id + "-O1.png",
								imageType);
					} catch (Exception e) {
						LOGGER.error("Failed Option 1 ", e);
					}
					option1.setImage(id + "-O1.png");
				} else {
					LOGGER.info("File Dose not exist, need to add this file [" + id + "-O1.png]");
				}

				System.out.println("DONE....");
			}

			option1.setPrompt("1");
			option1.setValue(csvColumnBean.getOption1());

			Options option2 = new Options();
			if (csvColumnBean.getOption2_Image() != null && !csvColumnBean.getOption2_Image().isEmpty()) {
				File fileImage = new File(imagePath + csvColumnBean.getOption2_Image());
				if (fileImage.exists()) {
					try {
						PaperImageUploadHelper.imageUplaod(googleCloudStorageUtil, ApplicationConstant.BUCKET_NAME,
								fileImage, csvBean.getPaperType().toString().toLowerCase() + "/" + id + "-O2.png",
								imageType);
					} catch (Exception e) {
						LOGGER.error("Failed Option 2 ", e);
					}
					option2.setImage(id + "-O2.png");
				} else {
					LOGGER.info("File Dose not exist, need to add this file [" + id + "-O2.png]");
				}

				System.out.println("DONE....");
			}

			option2.setPrompt("2");
			option2.setValue(csvColumnBean.getOption2());

			Options option3 = new Options();
			if (csvColumnBean.getOption3_Image() != null && !csvColumnBean.getOption3_Image().isEmpty()) {

				/*
				 * Path path = Paths .get(ClassLoader.getSystemResource("csv/" +
				 * csvColumnBean.getOption3_Image()).toURI());
				 */
				File fileImage = new File(imagePath + csvColumnBean.getOption3_Image());
				if (fileImage.exists()) {
					try {
						PaperImageUploadHelper.imageUplaod(googleCloudStorageUtil, ApplicationConstant.BUCKET_NAME,
								fileImage, csvBean.getPaperType().toString().toLowerCase() + "/" + id + "-O3.png",
								imageType);
					} catch (Exception e) {
						LOGGER.error("Failed Option 3 ", e);
					}

					option3.setImage(id + "-O3.png");
				} else {
					LOGGER.info("File Dose not exist, need to add this file [" + id + "-O3.png]");
				}

				System.out.println("DONE....");
			}
			// option3.setImage(csvColumnBean.getOption3_Image());
			option3.setPrompt("3");
			option3.setValue(csvColumnBean.getOption3());

			Options option4 = new Options();

			if (csvColumnBean.getOption4_Image() != null && !csvColumnBean.getOption4_Image().isEmpty()) {
				BufferedImage bufferedImage = null;
				File fileImage = new File(imagePath + csvColumnBean.getOption4_Image());
				if (fileImage.exists()) {

					/*
					 * bufferedImage = ImageIO.read(fileImage); //ImageIO.write(bufferedImage,
					 * "png", new File(filePath, id + "-O4.png")); ByteArrayOutputStream
					 * byteArrayOutputStream=new ByteArrayOutputStream() {
					 * 
					 * @Override public synchronized byte[] toByteArray() { return this.buf; } };
					 * //move to bucket ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
					 * InputStream inputStreamImage=new
					 * ByteArrayInputStream(byteArrayOutputStream.toByteArray(),0,
					 * byteArrayOutputStream.size());
					 */
					try {
						PaperImageUploadHelper.imageUplaod(googleCloudStorageUtil, ApplicationConstant.BUCKET_NAME,
								fileImage, csvBean.getPaperType().toString().toLowerCase() + "/" + id + "-O4.png",
								imageType);
					} catch (Exception e) {
						LOGGER.error("Failed Option 4 ", e);
					}

					option4.setImage(id + "-O4.png");
				} else {
					LOGGER.info("File Dose not exist, need to add this file [" + id + "-O4.png]");
				}

				System.out.println("DONE....");
			}
			// option4.setImage(csvColumnBean.getOption4_Image());
			option4.setPrompt("4");
			option4.setValue(csvColumnBean.getOption4());

			options.add(option1);
			options.add(option2);
			options.add(option3);
			options.add(option4);
			problem.setOptions(options);

			Question<Problem> question = new Question<>();
			question.setqNo(new Long(csvColumnBean.getQuestionNumber()));
			question.setComplexityLevel(csvColumnBean.getComplexityLevel());
			question.setComplexityScore(csvColumnBean.getComplexityScore());
			question.setType("mcq");
			question.setQuestionAttemptedStatus(QuestionAttemptedStatus.NA);
			question.setQuestionType(QuestionSelectionType.RADIOBUTTON);
			question.setScore(0.0);
			question.setAverageTimeSecond(120);
			question.setMaxTimeInSecond(180);
			question.setMinTimeInSecond(100);
			question.setProblem(problem);

			question.setId(id);
			questionList.add(question);
		}
		QuestionData<Question<Problem>> questionData = new QuestionData<>();
		questionData.setQuestions(questionList);
		SubSections<Question<Problem>> subSections = new SubSections<>();
		subSections.setTitle(csvColumnBean.getSubSectionName());
		subSections.setSectionType(csvColumnBean.getSectionType());
		subSections.setSNo(sNo);
		subSections.setQuestionData(questionData);
		return subSections;
	}

	// sections creation.
	/**
	 * @param csvContentRow
	 * @param subSection
	 * @param sNo
	 * @param sectionDto
	 * add section from csv
	 * @return
	 */
	private Sections<SubSections<Question<Problem>>> addSections(CsvBean csvContentRow,
			List<SubSections<Question<Problem>>> subSection, int sNo, SectionDeatilDto sectionDto) {

		CsvColumnBean csvColumnBean = (CsvColumnBean) csvContentRow;
		Sections<SubSections<Question<Problem>>> section = new Sections<>();
		section.setSectionType(csvColumnBean.getSectionType());
		section.setTitle(csvColumnBean.getSectionName());
		section.setQuestionCount(sectionDto.getQuestionCount());
		section.setSNo(sNo);
		section.setScore(sectionDto.getScoreInSection());
		section.setSubSections(subSection);
		section.setTotalTime(sectionDto.getTotalTime());
		section.setPerQuestionMarks(sectionDto.getPerQuestionMarks());
		section.setNegativeMarks(sectionDto.getNegativeMarks());
		return section;
	}

	/**
	 * @param <P>
	 * @param paidPaperCollection
	 * @param csvContentRow
	 * @param paperInfoDataDto
	 * identify the paper type and formation of paperCollection..
	 */
	private <P> void createParentPaperValue(P t, CsvBean csvContentRow, PaperInfoDataDto paperInfoDataDto,
			TestType testType) {
		// paidPaperCollection build
		if (t instanceof PaidPaperCollection) {
			PaidPaperCollection paidPaperCollection = (PaidPaperCollection) t;
			paidPaperCollection.setPaperCategory(csvContentRow.getPaperCategory());
			paidPaperCollection.setPaperType(csvContentRow.getPaperType());
			paidPaperCollection.setPaperSubCategory(csvContentRow.getPaperSubCategory());
			CsvColumnBean csvColumnBean = (CsvColumnBean) csvContentRow;
			paidPaperCollection.setNegativeMarks(paperInfoDataDto.getNegativeMarks());
			paidPaperCollection.setPaperName(csvColumnBean.getPaperName());

			if (csvContentRow.getPaperCategory().equals(paperInfoDataDto.getPaperCategory())
					&& csvContentRow.getPaperSubCategory().equals(paperInfoDataDto.getPaperSubCategory())) {
				paidPaperCollection.setTotalScore(paperInfoDataDto.getScore());
				paidPaperCollection.setTotalTime(paperInfoDataDto.getTotalTime());
				paidPaperCollection.setTotalQuestionCount(paperInfoDataDto.getTotalQuestionCount());
			}

			paidPaperCollection.setTestType(testType);
			paidPaperCollection.setPerQuestionScore(paperInfoDataDto.getPerQuestionScore());
			long currntTime = new Date().getTime();
			paidPaperCollection.setCreateDateTime(currntTime);
			paidPaperCollection.setValidityRangeStartDateTime(currntTime);
			long yeaerTwoVal = (365 * 2 * 24 * 60 * 60 * 1000L);
			long afterTwoYearDate = yeaerTwoVal + currntTime;
			paidPaperCollection.setValidityRangeEndDateTime(afterTwoYearDate);
			// FreePaperCollection build
		} else if (t instanceof FreePaperCollection) {

			FreePaperCollection freePaperCollection = (FreePaperCollection) t;
			freePaperCollection.setPaperCategory(csvContentRow.getPaperCategory());
			freePaperCollection.setPaperType(csvContentRow.getPaperType());
			freePaperCollection.setPaperSubCategory(csvContentRow.getPaperSubCategory());
			CsvColumnBean csvColumnBean = (CsvColumnBean) csvContentRow;
			freePaperCollection.setNegativeMarks(paperInfoDataDto.getNegativeMarks());
			freePaperCollection.setPaperName(csvColumnBean.getPaperName());

			if (csvContentRow.getPaperCategory().equals(paperInfoDataDto.getPaperCategory())
					&& csvContentRow.getPaperSubCategory().equals(paperInfoDataDto.getPaperSubCategory())) {
				freePaperCollection.setTotalScore(paperInfoDataDto.getScore());
				freePaperCollection.setTotalTime(paperInfoDataDto.getTotalTime());
				freePaperCollection.setTotalQuestionCount(paperInfoDataDto.getTotalQuestionCount());
			}
			freePaperCollection.setTestType(testType);
			freePaperCollection.setPerQuestionScore(paperInfoDataDto.getPerQuestionScore());
			long currntTime = new Date().getTime();
			freePaperCollection.setCreateDateTime(currntTime);
			freePaperCollection.setValidityRangeStartDateTime(currntTime);
			long yeaerTwoVal = (365 * 2 * 24 * 60 * 60 * 1000L);
			long afterTwoYearDate = yeaerTwoVal + currntTime;
			freePaperCollection.setValidityRangeEndDateTime(afterTwoYearDate);
			// PracticePaperCollection build
		} else if (t instanceof PracticePaperCollection) {
			PracticePaperCollection practicePaperCollection = (PracticePaperCollection) t;
			practicePaperCollection.setPaperCategory(csvContentRow.getPaperCategory());
			practicePaperCollection.setPaperType(csvContentRow.getPaperType());

			practicePaperCollection.setPaperSubCategory(csvContentRow.getPaperSubCategory());
			CsvColumnBean csvColumnBean = (CsvColumnBean) csvContentRow;
			practicePaperCollection.setNegativeMarks(paperInfoDataDto.getNegativeMarks());
			practicePaperCollection.setPaperName(csvColumnBean.getPaperName());

			if (csvContentRow.getPaperCategory().equals(paperInfoDataDto.getPaperCategory())) {
				practicePaperCollection.setTotalScore(paperInfoDataDto.getScore());
				practicePaperCollection.setTotalTime(paperInfoDataDto.getTotalTime());
				practicePaperCollection.setTotalQuestionCount(paperInfoDataDto.getTotalQuestionCount());
			}
			practicePaperCollection.setTestType(testType);
			practicePaperCollection.setPerQuestionScore(paperInfoDataDto.getPerQuestionScore());
			long currntTime = new Date().getTime();
			practicePaperCollection.setCreateDateTime(currntTime);
			practicePaperCollection.setValidityRangeStartDateTime(currntTime);
			long yearTwoVal = (365 * 2 * 24 * 60 * 60 * 1000L);
			long afterTwoYearDate = yearTwoVal + currntTime;
			practicePaperCollection.setValidityRangeEndDateTime(afterTwoYearDate);
			// QuizPaperCollection build
		} else if (t instanceof QuizPaperCollection) {
			QuizPaperCollection quizPaperCollection = (QuizPaperCollection) t;
			quizPaperCollection.setPaperCategory(csvContentRow.getPaperCategory());
			quizPaperCollection.setPaperType(csvContentRow.getPaperType());
			quizPaperCollection.setPaperSubCategory(csvContentRow.getPaperSubCategory());
			CsvColumnBean csvColumnBean = (CsvColumnBean) csvContentRow;
			quizPaperCollection.setNegativeMarks(paperInfoDataDto.getNegativeMarks());
			quizPaperCollection.setPaperName(csvColumnBean.getPaperName());
			if (csvContentRow.getPaperCategory().equals(paperInfoDataDto.getPaperCategory())
					&& csvContentRow.getPaperSubCategory().equals(paperInfoDataDto.getPaperSubCategory())) {
				quizPaperCollection.setTotalScore(paperInfoDataDto.getScore());
				quizPaperCollection.setTotalTime(paperInfoDataDto.getTotalTime());
				quizPaperCollection.setTotalQuestionCount(paperInfoDataDto.getTotalQuestionCount());
			}
			quizPaperCollection.setTestType(testType);
			quizPaperCollection.setPerQuestionScore(paperInfoDataDto.getPerQuestionScore());
			long currntTime = new Date().getTime();
			quizPaperCollection.setCreateDateTime(currntTime);
			quizPaperCollection.setValidityRangeStartDateTime(currntTime);
			long yearTwoVal = (365 * 2 * 24 * 60 * 60 * 1000L);
			long afterTwoYearDate = yearTwoVal + currntTime;
			quizPaperCollection.setValidityRangeEndDateTime(afterTwoYearDate);

			// pastPaperCollection build
		} else if (t instanceof PastPaperCollection) {
			PastPaperCollection pastPaperCollection = (PastPaperCollection) t;
			pastPaperCollection.setPaperCategory(csvContentRow.getPaperCategory());
			pastPaperCollection.setPaperType(csvContentRow.getPaperType());
			pastPaperCollection.setPaperSubCategory(csvContentRow.getPaperSubCategory());
			CsvColumnBean csvColumnBean = (CsvColumnBean) csvContentRow;
			pastPaperCollection.setNegativeMarks(paperInfoDataDto.getNegativeMarks());
			pastPaperCollection.setPaperName(csvColumnBean.getPaperName());
			if (csvContentRow.getPaperCategory().equals(paperInfoDataDto.getPaperCategory())
					&& csvContentRow.getPaperSubCategory().equals(paperInfoDataDto.getPaperSubCategory())) {
				pastPaperCollection.setTotalScore(paperInfoDataDto.getScore());
				pastPaperCollection.setTotalTime(paperInfoDataDto.getTotalTime());
				pastPaperCollection.setTotalQuestionCount(paperInfoDataDto.getTotalQuestionCount());
			}
			pastPaperCollection.setTestType(testType);
			pastPaperCollection.setPerQuestionScore(paperInfoDataDto.getPerQuestionScore());
			long currntTime = new Date().getTime();
			pastPaperCollection.setCreateDateTime(currntTime);
			pastPaperCollection.setValidityRangeStartDateTime(currntTime);
			long yearTwoVal = (365 * 2 * 24 * 60 * 60 * 1000L);
			long afterTwoYearDate = yearTwoVal + currntTime;
			pastPaperCollection.setValidityRangeEndDateTime(afterTwoYearDate);
		} else {
			LOGGER.info("PaperCollection type id not valid");
		}

	}

	private Map<String, List<CsvBean>> modifyChildMap(CsvBean csvObj) {
		CsvColumnBean csvColumnValue = (CsvColumnBean) csvObj;
		Map<String, List<CsvBean>> subSectionVsCsvBean = new LinkedHashMap<>();
		List<CsvBean> CsvBeanList = new LinkedList<CsvBean>();
		CsvBeanList.add(csvObj);
		subSectionVsCsvBean.put(csvColumnValue.getSubSectionName(), CsvBeanList);

		return subSectionVsCsvBean;
	}

	// mapped with sectionVsSubSectionVsListOfQuestion.
	private void updateSectionVsSubSectionMap(CsvBean csvObj,
			Map<SectionType, Map<String, List<CsvBean>>> SectionVsSubSectionVsPaperObj) {
		CsvColumnBean csvColumnValue = (CsvColumnBean) csvObj;
		if (SectionVsSubSectionVsPaperObj.get(csvColumnValue.getSectionType()) != null) {
			Map<String, List<CsvBean>> subSectionVsCsvBean = SectionVsSubSectionVsPaperObj
					.get(csvColumnValue.getSectionType());
			if (subSectionVsCsvBean.get(csvColumnValue.getSubSectionName()) != null) {
				List<CsvBean> csvBeanList = subSectionVsCsvBean.get(csvColumnValue.getSubSectionName());
				csvBeanList.add(csvObj);
			} else {
				List<CsvBean> listBean = new LinkedList<>();
				listBean.add(csvObj);
				subSectionVsCsvBean.put(csvColumnValue.getSubSectionName(), listBean);

			}
		} else {
			Map<String, List<CsvBean>> subSectionVsCsvBean = modifyChildMap(csvObj);
			SectionVsSubSectionVsPaperObj.put(csvColumnValue.getSectionType(), subSectionVsCsvBean);

		}
	}

	@Override
	public void createPrecticePaper(List<T> paperContent, TestType testType, String path, String imagePath) {
		if (paperContent.get(0) instanceof CsvBean) {
			csvFileLoc = path + "/";
			staticContentLoc = imagePath;
			List<CsvBean> csvBeanListContent = (List<CsvBean>) paperContent;
			PracticePaperCollection practicePaperCollection = convertCsvBeanPracticePaperCollection(csvBeanListContent,
					testType);
			practicePaperCollection.setPaperStateStatus(PaperStateStatus.DRAFT);
			practicePaperCollectionService.createPracticePaperInCollection(practicePaperCollection);
		}
	}

	private PracticePaperCollection convertCsvBeanPracticePaperCollection(List<CsvBean> csvBeanListContent,
			TestType testType) {
		PracticePaperCollection practicePaperCollection = new PracticePaperCollection();
		Pattern<Sections<SubSections<Question<Problem>>>> pattern = getPaperPattern(practicePaperCollection,
				csvBeanListContent, testType);
		practicePaperCollection.setPaperStateStatus(PaperStateStatus.DRAFT);
		practicePaperCollection.setPattern(pattern);
		return practicePaperCollection;
	}

	@Override
	public void createQuizPaper(List<T> paperContent, TestType testType, String path, String imagePath) {
		if (paperContent.get(0) instanceof CsvBean) {
			csvFileLoc = path + "/";
			staticContentLoc = imagePath;
			List<CsvBean> csvBeanListContent = (List<CsvBean>) paperContent;
			QuizPaperCollection quizPaperCollection = convertCsvBeanQuizePaperCollection(csvBeanListContent, testType);
			quizPaperCollection.setPaperStateStatus(PaperStateStatus.DRAFT);
			quizPaperCollectionService.createQuizPaperInCollection(quizPaperCollection);
		}
	}

	private QuizPaperCollection convertCsvBeanQuizePaperCollection(List<CsvBean> csvBeanListContent,
			TestType testType) {
		QuizPaperCollection quizPaperCollection = new QuizPaperCollection();
		Pattern<Sections<SubSections<Question<Problem>>>> pattern = getPaperPattern(quizPaperCollection,
				csvBeanListContent, testType);
		quizPaperCollection.setPattern(pattern);
		return quizPaperCollection;
	}

	@Override
	public void createPastPaper(List<T> paperContent, TestType testType, String path, String imagePath) {
		if (paperContent.get(0) instanceof CsvBean) {
			csvFileLoc = path + "/";
			staticContentLoc = imagePath;
			List<CsvBean> csvBeanListContent = (List<CsvBean>) paperContent;
			PastPaperCollection pastPaperCollection = convertCsvBeanPastPaperCollection(csvBeanListContent, testType);
			pastPaperCollectionService.createPastPaperInCollection(pastPaperCollection);
		}
	}

	private PastPaperCollection convertCsvBeanPastPaperCollection(List<CsvBean> csvBeanListContent, TestType testType) {
		PastPaperCollection pastPaperCollection = new PastPaperCollection();
		Pattern<Sections<SubSections<Question<Problem>>>> pattern = getPaperPattern(pastPaperCollection,
				csvBeanListContent, testType);
		pastPaperCollection.setPattern(pattern);
		return pastPaperCollection;
	}

	@Override
	public PaidPaperCollection getPaidPaper(List<T> paperContent, String path, String imagePath) {

		PaidPaperCollection paidPaperCollection = null;
		if (paperContent.get(0) instanceof CsvBean) {
			csvFileLoc = path + "/";
			staticContentLoc = imagePath;
			List<CsvBean> csvBeanListContent = (List<CsvBean>) paperContent;
			paidPaperCollection = converCsvBeanToPaperCollection(csvBeanListContent);

			ObjectMapper objectMapper = new ObjectMapper();
			String val = null;
			try {
				val = objectMapper.writeValueAsString(paidPaperCollection);
			} catch (JsonProcessingException e) {
				LOGGER.error("Error while conversion into json", e);
			}
			System.out.println(val);
		}
		return null;
	}

	@Override
	public FreePaperCollection getFreeTypePaper(List<T> paperContent, TestType testType, String path,
			String imagePath) {
		FreePaperCollection freePaperCollection = null;
		if (paperContent.get(0) instanceof CsvBean) {
			csvFileLoc = path + "/";
			staticContentLoc = imagePath;
			List<CsvBean> csvBeanListContent = (List<CsvBean>) paperContent;
			freePaperCollection = convertCsvBeanToFreePaperCollection(csvBeanListContent, testType);
		}
		return freePaperCollection;
	}

	@Override
	public PracticePaperCollection getPrecticePaper(List<T> paperContent, TestType testType, String path,
			String imagePath) {
		PracticePaperCollection practicePaperCollection = null;
		if (paperContent.get(0) instanceof CsvBean) {
			csvFileLoc = path + "/";
			staticContentLoc = imagePath;
			List<CsvBean> csvBeanListContent = (List<CsvBean>) paperContent;
			practicePaperCollection = convertCsvBeanPracticePaperCollection(csvBeanListContent, testType);
		}
		return practicePaperCollection;
	}

	@Override
	public QuizPaperCollection getQuizPaper(List<T> paperContent, TestType testType, String path, String imagePath) {
		QuizPaperCollection quizPaperCollection = null;
		if (paperContent.get(0) instanceof CsvBean) {
			csvFileLoc = path + "/";
			staticContentLoc = imagePath;
			List<CsvBean> csvBeanListContent = (List<CsvBean>) paperContent;
			quizPaperCollection = convertCsvBeanQuizePaperCollection(csvBeanListContent, testType);
		}
		return quizPaperCollection;
	}

	@Override
	public PastPaperCollection getPastPaper(List<T> paperContent, TestType testType, String path, String imagePath) {
		PastPaperCollection pastPaperCollection = null;
		if (paperContent.get(0) instanceof CsvBean) {
			csvFileLoc = path + "/";
			staticContentLoc = imagePath;
			List<CsvBean> csvBeanListContent = (List<CsvBean>) paperContent;
			pastPaperCollection = convertCsvBeanPastPaperCollection(csvBeanListContent, testType);
		}
		return pastPaperCollection;
	}
}
