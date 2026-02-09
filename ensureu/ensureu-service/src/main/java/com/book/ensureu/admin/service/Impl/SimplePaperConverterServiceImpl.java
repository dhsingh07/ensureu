package com.book.ensureu.admin.service.Impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.book.ensureu.admin.constant.PaperStateStatus;
import com.book.ensureu.admin.dto.PaperCollectionDto;
import com.book.ensureu.admin.dto.SimplePaperDto;
import com.book.ensureu.admin.dto.SimpleQuestionDto;
import com.book.ensureu.admin.dto.SimpleSectionDto;
import com.book.ensureu.admin.service.SimplePaperConverterService;
import com.book.ensureu.constant.PaperCategory;
import com.book.ensureu.constant.QuestionSelectionType;
import com.book.ensureu.model.Options;
import com.book.ensureu.model.Pattern;
import com.book.ensureu.model.Problem;
import com.book.ensureu.model.Question;
import com.book.ensureu.model.QuestionData;
import com.book.ensureu.model.Sections;
import com.book.ensureu.model.Solution;
import com.book.ensureu.model.SubSections;

@Service
public class SimplePaperConverterServiceImpl implements SimplePaperConverterService {

    @Override
    public PaperCollectionDto convertToFullPaper(SimplePaperDto dto) {
        PaperCollectionDto result = new PaperCollectionDto();

        // Set metadata
        result.setPaperName(dto.getPaperName());
        result.setPaperCategory(dto.getPaperCategory());
        result.setPaperSubCategory(dto.getPaperSubCategory());
        result.setTestType(dto.getTestType());
        result.setPerQuestionScore(dto.getPerQuestionScore());
        result.setNegativeMarks(dto.getNegativeMarks());
        result.setTotalTime(dto.getTotalTimeInSeconds());
        result.setPaperStateStatus(
            dto.getPaperStateStatus() != null ? dto.getPaperStateStatus() : PaperStateStatus.DRAFT
        );

        // Derive paperType from paperCategory
        if (dto.getPaperCategory() != null) {
            result.setPaperType(PaperCategory.getParent(dto.getPaperCategory()));
        }

        // Derive paperSubCategoryName
        if (dto.getPaperSubCategory() != null) {
            result.setPaperSubCategoryName(dto.getPaperSubCategory().toString());
        }

        // Build sections
        List<Sections<SubSections<Question<Problem>>>> sectionsList = new ArrayList<>();
        int globalQNo = 1;

        if (dto.getSections() != null && !dto.getSections().isEmpty()) {
            // Sectioned mode
            for (int sIdx = 0; sIdx < dto.getSections().size(); sIdx++) {
                SimpleSectionDto sectionDto = dto.getSections().get(sIdx);
                Sections<SubSections<Question<Problem>>> section = buildSection(
                    sectionDto, sIdx + 1, dto, globalQNo
                );
                sectionsList.add(section);
                if (sectionDto.getQuestions() != null) {
                    globalQNo += sectionDto.getQuestions().size();
                }
            }
        } else if (dto.getQuestions() != null && !dto.getQuestions().isEmpty()) {
            // Flat mode: create single section
            SimpleSectionDto defaultSection = new SimpleSectionDto();
            defaultSection.setTitle("General");
            defaultSection.setQuestions(dto.getQuestions());
            defaultSection.setPerQuestionMarks(dto.getPerQuestionScore());
            defaultSection.setNegativeMarks(dto.getNegativeMarks());
            Sections<SubSections<Question<Problem>>> section = buildSection(
                defaultSection, 1, dto, globalQNo
            );
            sectionsList.add(section);
        }

        // Build Pattern
        Pattern<Sections<SubSections<Question<Problem>>>> pattern = new Pattern<>();
        pattern.setTime(dto.getTotalTimeInSeconds());
        pattern.setTitle(dto.getPaperName());
        pattern.setCreatedOn(String.valueOf(System.currentTimeMillis()));
        pattern.setSections(sectionsList);

        // Set paperType on pattern
        if (dto.getPaperCategory() != null) {
            pattern.setPaperType(PaperCategory.getParent(dto.getPaperCategory()));
        }

        result.setPattern(pattern);

        // Calculate totals
        int totalQuestions = 0;
        for (Sections<SubSections<Question<Problem>>> section : sectionsList) {
            totalQuestions += section.getQuestionCount();
        }
        result.setTotalQuestionCount(totalQuestions);
        result.setTotalScore(totalQuestions * dto.getPerQuestionScore());

        // Initialize dates
        result.initializeDatesIfMissing();

        return result;
    }

    private Sections<SubSections<Question<Problem>>> buildSection(
            SimpleSectionDto sectionDto, int sNo, SimplePaperDto paperDto, int startQNo) {

        Sections<SubSections<Question<Problem>>> section = new Sections<>();
        section.setTitle(sectionDto.getTitle());
        section.setSNo(sNo);

        int questionCount = sectionDto.getQuestions() != null ? sectionDto.getQuestions().size() : 0;
        section.setQuestionCount(questionCount);

        double perQ = sectionDto.getPerQuestionMarks() > 0
            ? sectionDto.getPerQuestionMarks() : paperDto.getPerQuestionScore();
        double negM = sectionDto.getNegativeMarks() > 0
            ? sectionDto.getNegativeMarks() : paperDto.getNegativeMarks();

        section.setPerQuestionMarks(perQ);
        section.setNegativeMarks(negM);
        section.setScore(questionCount * perQ);

        // Build single SubSection
        SubSections<Question<Problem>> subSection = new SubSections<>();
        subSection.setTitle(sectionDto.getTitle());
        subSection.setSNo(1);
        subSection.setQuestionCount(questionCount);
        subSection.setScore(questionCount * perQ);

        // Build QuestionData
        QuestionData<Question<Problem>> questionData = new QuestionData<>();
        List<Question<Problem>> questionList = new ArrayList<>();

        if (sectionDto.getQuestions() != null) {
            for (int i = 0; i < sectionDto.getQuestions().size(); i++) {
                SimpleQuestionDto qDto = sectionDto.getQuestions().get(i);
                Question<Problem> question = buildQuestion(qDto, startQNo + i);
                questionList.add(question);
            }
        }

        questionData.setQuestions(questionList);
        subSection.setQuestionData(questionData);

        section.setSubSections(Arrays.asList(subSection));
        return section;
    }

    private Question<Problem> buildQuestion(SimpleQuestionDto qDto, int qNo) {
        Question<Problem> question = new Question<>();
        question.setqNo((long) qNo);
        question.setType("mcq");
        question.setQuestionType(QuestionSelectionType.RADIOBUTTON);
        question.setMinTimeInSecond(60);
        question.setMaxTimeInSecond(180);
        question.setAverageTimeSecond(120);

        Problem problem = new Problem();
        problem.setValue(qDto.getQuestionText());
        problem.setCo(qDto.getCorrectOption());
        problem.setSo(0);
        problem.setImage(qDto.getQuestionImage());

        // Build 4 options
        List<Options> options = new ArrayList<>();
        options.add(buildOption(qDto.getOptionA(), "1"));
        options.add(buildOption(qDto.getOptionB(), "2"));
        options.add(buildOption(qDto.getOptionC(), "3"));
        options.add(buildOption(qDto.getOptionD(), "4"));
        problem.setOptions(options);

        // Build solution if explanation provided
        if (qDto.getExplanation() != null && !qDto.getExplanation().trim().isEmpty()) {
            Solution solution = new Solution();
            solution.setValue(qDto.getExplanation());
            solution.setAddedon(String.valueOf(System.currentTimeMillis()));
            problem.setSolutions(Arrays.asList(solution));
        }

        question.setProblem(problem);
        return question;
    }

    private Options buildOption(String value, String prompt) {
        Options option = new Options();
        option.setValue(value);
        option.setPrompt(prompt);
        return option;
    }
}
