package com.book.ensureu.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.book.ensureu.dto.PaperCollectionDto;
import com.book.ensureu.dto.PaperDto;
import com.book.ensureu.model.QuizPaper;
import com.book.ensureu.model.QuizPaperCollection;

public class QuizPaperConversionUtil {

	public static QuizPaper quizPaperDtoToModel(PaperDto paperDto) {
		QuizPaper quizPaper=new QuizPaper();
		if(paperDto!=null) {
			quizPaper.setId(paperDto.getId());
			quizPaper.setUserId(paperDto.getUserId());
			quizPaper.setTotalScore(paperDto.getTotalScore());
			quizPaper.setPaperId(paperDto.getPaperId());
			quizPaper.setStartTestTime(paperDto.getStartTestTime());
			quizPaper.setEndTestTime(paperDto.getEndTestTime());
			quizPaper.setPaperName(paperDto.getPaperName());
			quizPaper.setPrice(paperDto.getPrice());
			quizPaper.setPayment(paperDto.isPayment());
			quizPaper.setPaperStatus(paperDto.getPaperStatus());
			quizPaper.setPaperValidityStartDate(paperDto.getPaperValidityStartDate());
			quizPaper.setPaperValidityEndDate(paperDto.getPaperValidityEndDate());
			quizPaper.setCreateDateTime(paperDto.getCreateDateTime());
			quizPaper.setId(paperDto.getId());

			quizPaper.setPaperType(paperDto.getPaperType());
			quizPaper.setPaperCategory(paperDto.getPaperCategory());
			quizPaper.setPaperSubCategory(paperDto.getPaperSubCategory());
			quizPaper.setPaperSubCategoryName(paperDto.getPaperSubCategoryName());
			quizPaper.setTestType(paperDto.getTestType());

			quizPaper.setTotalAttemptedQuestionCount(paperDto.getTotalAttemptedQuestionCount());
			quizPaper.setTotalCorrectCount(paperDto.getTotalCorrectCount());
			quizPaper.setTotalSkipedCount(paperDto.getTotalSkipedCount());
			quizPaper.setTotalInCorrectCount(paperDto.getTotalInCorrectCount());
			quizPaper.setTotalScore(paperDto.getTotalScore());
			quizPaper.setTotalGetScore(paperDto.getTotalGetScore());
			quizPaper.setTotalTime(paperDto.getTotalTime());
			quizPaper.setTotalTimeTaken(paperDto.getTotalTimeTaken());
			quizPaper.setTotalQuestionCount(paperDto.getTotalQuestionCount());
			quizPaper.setNegativeMarks(paperDto.getNegativeMarks());
			quizPaper.setPerQuestionScore(paperDto.getPerQuestionScore());
			
			QuizPaperCollection quizPaperCollection = new QuizPaperCollection();
			PaperCollectionDto paperCollDto=paperDto.getPaper();
			if(paperCollDto!=null) {
				try {
					BeanUtils.copyProperties(paperCollDto, quizPaperCollection);
					quizPaper.setPaper(quizPaperCollection);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}
		return quizPaper;
	}

	public static PaperDto quizPaperToDto(QuizPaper quizPaper) {
		PaperDto paperDto=null;
		if(quizPaper!=null) {
			paperDto = new PaperDto();
			paperDto.setUserId(quizPaper.getUserId());
			paperDto.setTotalScore(quizPaper.getTotalScore());
			paperDto.setPaperId(quizPaper.getPaperId());
			paperDto.setStartTestTime(quizPaper.getStartTestTime());
			paperDto.setEndTestTime(quizPaper.getEndTestTime());
			paperDto.setPaperName(quizPaper.getPaperName());

			paperDto.setPrice(quizPaper.getPrice());
			paperDto.setPayment(quizPaper.isPayment());
			paperDto.setPaperStatus(quizPaper.getPaperStatus());
			paperDto.setPaperValidityStartDate(quizPaper.getPaperValidityStartDate());
			paperDto.setPaperValidityEndDate(quizPaper.getPaperValidityEndDate());
			paperDto.setCreateDateTime(quizPaper.getCreateDateTime());
			paperDto.setId(quizPaper.getId());

			paperDto.setPaperType(quizPaper.getPaperType());
			paperDto.setPaperCategory(quizPaper.getPaperCategory());
			paperDto.setPaperSubCategory(quizPaper.getPaperSubCategory());
			paperDto.setPaperSubCategoryName(quizPaper.getPaperSubCategoryName());
			paperDto.setTestType(quizPaper.getTestType());

			paperDto.setTotalAttemptedQuestionCount(quizPaper.getTotalAttemptedQuestionCount());
			paperDto.setTotalCorrectCount(quizPaper.getTotalCorrectCount());
			paperDto.setTotalSkipedCount(quizPaper.getTotalSkipedCount());
			paperDto.setTotalInCorrectCount(quizPaper.getTotalInCorrectCount());
			paperDto.setTotalScore(quizPaper.getTotalScore());
			paperDto.setTotalGetScore(quizPaper.getTotalGetScore());
			paperDto.setTotalTime(quizPaper.getTotalTime());
			paperDto.setTotalTimeTaken(quizPaper.getTotalTimeTaken());
			paperDto.setTotalQuestionCount(quizPaper.getTotalQuestionCount());
			paperDto.setNegativeMarks(quizPaper.getNegativeMarks());
			paperDto.setPerQuestionScore(quizPaper.getPerQuestionScore());

			PaperCollectionDto paperCollectionDto = new PaperCollectionDto();
			QuizPaperCollection quizPaperCollection = quizPaper.getPaper();
			
			if(quizPaperCollection!=null) {
				BeanUtils.copyProperties(quizPaperCollection, paperCollectionDto);
				paperDto.setPaper(paperCollectionDto);
			}
		}
		return paperDto;
	}

	public static List<PaperDto> quizPaperToDto(List<QuizPaper> quizPaperList) {
		List<PaperDto> paperDtoList=new ArrayList<PaperDto>();
		if(quizPaperList!=null && !quizPaperList.isEmpty()) {
			quizPaperList.forEach(quizPaper->{
				paperDtoList.add(quizPaperToDto(quizPaper));
			});
		}
		return paperDtoList;
	}

}
