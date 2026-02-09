package com.book.ensureu.util;

import java.util.ArrayList;
import java.util.List;

import com.book.ensureu.constant.PaperStatus;
import com.book.ensureu.constant.PaperSubCategory;
import com.book.ensureu.constant.TestType;
import com.book.ensureu.dto.PaperCollectionDto;
import com.book.ensureu.dto.PaperDto;
import com.book.ensureu.model.PastPaper;
import com.book.ensureu.model.PastPaperCollection;

public class PastPaperConversionUtil {

	public static PastPaper pastPaperDtoToModel(PaperDto testPaperDto) {

		PastPaper pastPaper = null;
		if (testPaperDto != null) {
			pastPaper = new PastPaper();

			pastPaper.setUserId(testPaperDto.getUserId());
			pastPaper.setTotalScore(testPaperDto.getTotalScore());
			pastPaper.setPaperId(testPaperDto.getPaperId());
			pastPaper.setStartTestTime(testPaperDto.getStartTestTime());
			pastPaper.setEndTestTime(testPaperDto.getEndTestTime());
			pastPaper.setPaperName(testPaperDto.getPaperName());

			pastPaper.setPrice(testPaperDto.getPrice());
			pastPaper.setPayment(testPaperDto.isPayment());
			pastPaper.setPaperStatus(testPaperDto.getPaperStatus());
			pastPaper.setPaperValidityStartDate(testPaperDto.getPaperValidityStartDate());
			pastPaper.setPaperValidityEndDate(testPaperDto.getPaperValidityEndDate());
			pastPaper.setCreateDateTime(testPaperDto.getCreateDateTime());
			pastPaper.setId(testPaperDto.getId());

			pastPaper.setPaperType(testPaperDto.getPaperType());
			pastPaper.setPaperCategory(testPaperDto.getPaperCategory());
			pastPaper.setPaperSubCategory(testPaperDto.getPaperSubCategory());
			pastPaper.setPaperSubCategoryName(testPaperDto.getPaperSubCategoryName());
			pastPaper.setTestType(testPaperDto.getTestType());

			pastPaper.setTotalAttemptedQuestionCount(testPaperDto.getTotalAttemptedQuestionCount());
			pastPaper.setTotalCorrectCount(testPaperDto.getTotalCorrectCount());
			pastPaper.setTotalSkipedCount(testPaperDto.getTotalSkipedCount());
			pastPaper.setTotalInCorrectCount(testPaperDto.getTotalInCorrectCount());
			pastPaper.setTotalScore(testPaperDto.getTotalScore());
			pastPaper.setTotalGetScore(testPaperDto.getTotalGetScore());
			pastPaper.setTotalTime(testPaperDto.getTotalTime());
			pastPaper.setTotalTimeTaken(testPaperDto.getTotalTimeTaken());
			pastPaper.setTotalQuestionCount(testPaperDto.getTotalQuestionCount());
			pastPaper.setNegativeMarks(testPaperDto.getNegativeMarks());
			pastPaper.setPerQuestionScore(testPaperDto.getPerQuestionScore());
			
			pastPaper.setDateOfExam(testPaperDto.getDateOfExam());
			pastPaper.setDateOfExamYear(testPaperDto.getDateOfExamYear());
			pastPaper.setCutOffMark(testPaperDto.getCutOffMark());
			pastPaper.setShiftOfExam(testPaperDto.getShiftOfExam());
			

			PastPaperCollection testPaperCollection = new PastPaperCollection();
			PaperCollectionDto testPaperCollectionDto = testPaperDto.getPaper();
			if(testPaperCollectionDto!=null) {
			testPaperCollection.setId(testPaperCollectionDto.getId());
			testPaperCollection.setPaperName(testPaperCollectionDto.getPaperName());
			PaperSubCategory paperSubCotag = testPaperCollectionDto.getPaperSubCategory();
			testPaperCollection.setPaperSubCategory(paperSubCotag);
			testPaperCollection.setPaperSubCategoryName(paperSubCotag.toString());
			testPaperCollection.setTestType(testPaperCollectionDto.getTestType());
			testPaperCollection.setPaperType(testPaperCollectionDto.getPaperType());
			
			testPaperCollection.setTotalQuestionCount(testPaperCollectionDto.getTotalQuestionCount());
			testPaperCollection.setNegativeMarks(testPaperCollectionDto.getNegativeMarks());
			testPaperCollection.setPerQuestionScore(testPaperCollectionDto.getPerQuestionScore());
			testPaperCollection.setTotalQuestionCount(testPaperCollectionDto.getTotalQuestionCount());
			testPaperCollection.setTotalScore(testPaperCollectionDto.getTotalScore());
			testPaperCollection.setTotalTime(testPaperCollectionDto.getTotalTime());
			testPaperCollection.setTotalTakenTime(testPaperCollectionDto.getTotalTakenTime());
			testPaperCollection.setTotalGetScore(testPaperCollectionDto.getTotalGetScore());
			
			testPaperCollection.setPattern(testPaperCollectionDto.getPattern());
			pastPaper.setPaper(testPaperCollection);
			}
		}
		return pastPaper;

	}
	
	
	public static PaperDto pastPaperToDto(PastPaper pastPaper) {

		PaperDto pastPaperDto = null;
		if (pastPaper != null) {
			pastPaperDto = new PaperDto();
			pastPaperDto.setUserId(pastPaper.getUserId());
			pastPaperDto.setTotalScore(pastPaper.getTotalScore());
			pastPaperDto.setPaperId(pastPaper.getPaperId());
			pastPaperDto.setStartTestTime(pastPaper.getStartTestTime());
			pastPaperDto.setEndTestTime(pastPaper.getEndTestTime());
			pastPaperDto.setPaperName(pastPaper.getPaperName());

			pastPaperDto.setPrice(pastPaper.getPrice());
			pastPaperDto.setPayment(pastPaper.isPayment());
			pastPaperDto.setPaperStatus(pastPaper.getPaperStatus());
			pastPaperDto.setPaperValidityStartDate(pastPaper.getPaperValidityStartDate());
			pastPaperDto.setPaperValidityEndDate(pastPaper.getPaperValidityEndDate());
			pastPaperDto.setCreateDateTime(pastPaper.getCreateDateTime());
			pastPaperDto.setId(pastPaper.getId());

			pastPaperDto.setPaperType(pastPaper.getPaperType());
			pastPaperDto.setPaperCategory(pastPaper.getPaperCategory());
			pastPaperDto.setPaperSubCategory(pastPaper.getPaperSubCategory());
			pastPaperDto.setPaperSubCategoryName(pastPaper.getPaperSubCategoryName());
			pastPaperDto.setTestType(pastPaper.getTestType());

			pastPaperDto.setTotalAttemptedQuestionCount(pastPaper.getTotalAttemptedQuestionCount());
			pastPaperDto.setTotalCorrectCount(pastPaper.getTotalCorrectCount());
			pastPaperDto.setTotalSkipedCount(pastPaper.getTotalSkipedCount());
			pastPaperDto.setTotalInCorrectCount(pastPaper.getTotalInCorrectCount());
			pastPaperDto.setTotalScore(pastPaper.getTotalScore());
			pastPaperDto.setTotalGetScore(pastPaper.getTotalGetScore());
			pastPaperDto.setTotalTime(pastPaper.getTotalTime());
			pastPaperDto.setTotalTimeTaken(pastPaper.getTotalTimeTaken());
			pastPaperDto.setTotalQuestionCount(pastPaper.getTotalQuestionCount());
			pastPaperDto.setNegativeMarks(pastPaper.getNegativeMarks());
			pastPaperDto.setPerQuestionScore(pastPaper.getPerQuestionScore());
			
			pastPaperDto.setDateOfExam(pastPaper.getDateOfExam());
			pastPaperDto.setDateOfExamYear(pastPaper.getDateOfExamYear());
			pastPaperDto.setCutOffMark(pastPaper.getCutOffMark());
			pastPaperDto.setShiftOfExam(pastPaper.getShiftOfExam());

			PaperCollectionDto paperCollectionDto = new PaperCollectionDto();
			PastPaperCollection testPaperCollection = pastPaper.getPaper();
			if(testPaperCollection!=null) {
			paperCollectionDto.setId(testPaperCollection.getId());
			paperCollectionDto.setPaperName(testPaperCollection.getPaperName());
			PaperSubCategory paperSubCotag = testPaperCollection.getPaperSubCategory();
			paperCollectionDto.setPaperSubCategory(paperSubCotag);
			paperCollectionDto.setPaperSubCategoryName(paperSubCotag.toString());
			paperCollectionDto.setTestType(testPaperCollection.getTestType());
			paperCollectionDto.setPaperType(testPaperCollection.getPaperType());
			
			paperCollectionDto.setTotalQuestionCount(testPaperCollection.getTotalQuestionCount());
			paperCollectionDto.setNegativeMarks(testPaperCollection.getNegativeMarks());
			paperCollectionDto.setPerQuestionScore(testPaperCollection.getPerQuestionScore());
			paperCollectionDto.setTotalQuestionCount(testPaperCollection.getTotalQuestionCount());
			paperCollectionDto.setTotalScore(testPaperCollection.getTotalScore());
			paperCollectionDto.setTotalTime(testPaperCollection.getTotalTime());
			paperCollectionDto.setTotalTakenTime(testPaperCollection.getTotalTakenTime());
			paperCollectionDto.setTotalGetScore(testPaperCollection.getTotalGetScore());
			
			paperCollectionDto.setPattern(testPaperCollection.getPattern());
			pastPaperDto.setPaper(paperCollectionDto);
			}
			
		}
		return pastPaperDto;
	}
	
	public static List<PaperDto> pastPaperToDto(List<PastPaper> pastPaperList) {
		List<PaperDto> paperTestDtoList = null;
		if (pastPaperList != null && !pastPaperList.isEmpty()) {
			paperTestDtoList = new ArrayList<PaperDto>();
			for (PastPaper pastPaper : pastPaperList) {
				PaperDto testP = pastPaperToDto(pastPaper);
				paperTestDtoList.add(testP);
			}
		}
		return paperTestDtoList;

	}
	
	
	public static PastPaper createPastPaperByPastCollectionPaper(PastPaperCollection pastPaperColl, TestType testType,
			String userId, String userMapped) {

		PastPaper pastPaper = null;
		if (pastPaperColl != null) {
			pastPaper = new PastPaper();
			pastPaper.setPaperId(pastPaperColl.getId());
			if (userMapped != null) {
				pastPaper.setPaper(pastPaperColl);
				pastPaper.setPaperStatus(PaperStatus.INPROGRESS);
				pastPaper.setCreateDateTime(System.currentTimeMillis());
			} else {
				pastPaper.setPaperStatus(PaperStatus.START);
			}
			pastPaper.setPaperCategory(pastPaperColl.getPaperCategory());
			pastPaper.setPaperSubCategory(pastPaperColl.getPaperSubCategory());
			pastPaper.setPaperType(pastPaperColl.getPaperType());
			pastPaper.setPaperName(pastPaperColl.getPaperName());
			pastPaper.setPaperSubCategoryName(pastPaperColl.getPaperSubCategoryName());
			pastPaper.setTestType(testType);
			pastPaper.setUserId(userId);
			pastPaper.setTotalScore(pastPaperColl.getTotalScore());
			pastPaper.setTotalTime(pastPaperColl.getTotalTime());
			pastPaper.setNegativeMarks(pastPaperColl.getNegativeMarks());
			pastPaper.setTotalQuestionCount(pastPaperColl.getTotalQuestionCount());
			pastPaper.setPerQuestionScore(pastPaperColl.getPerQuestionScore());
			pastPaper.setTotalGetScore(pastPaperColl.getTotalGetScore());
			
			//past specific paper
			pastPaper.setDateOfExam(pastPaperColl.getDateOfExam());
			pastPaper.setDateOfExamYear(pastPaperColl.getDateOfExamYear());
			pastPaper.setCutOffMark(pastPaperColl.getCutOffMark());
			pastPaper.setShiftOfExam(pastPaperColl.getShiftOfExam());
		}

		return pastPaper;
	}
	
	
}
