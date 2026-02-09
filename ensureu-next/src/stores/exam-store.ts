// Exam Store - migrated from Angular test-paper-editor.component.ts

import { create } from 'zustand';
import type {
  PaperData,
  Section,
  Question,
  IngestDataItem,
  QuestionAttemptedStatus,
} from '@/types/paper';

interface ExamState {
  // Paper state
  paperData: PaperData | null;
  currentSection: Section | null;
  currentSectionIndex: number;
  currentQuestion: Question | null;
  currentQuestionIndex: number;

  // Display settings
  isHindi: boolean;
  fontSize: number;

  // Timer state
  paperTimeRemaining: number;
  initialTimeRemaining: number; // Track initial time to calculate elapsed
  sectionTimeTaken: number;
  questionTimeTaken: number;

  // Tracking data
  ingestData: Record<string, IngestDataItem>;
  ingestCounter: number;

  // Actions
  setPaperData: (data: PaperData) => void;
  setCurrentSection: (index: number) => void;
  setCurrentQuestion: (index: number) => void;
  toggleLanguage: () => void;
  updateFontSize: (delta: number) => void;
  selectOption: (optionIndex: number, questionType: 'RADIOBUTTON' | 'CHECKBOX') => void;
  nextQuestion: () => void;
  previousQuestion: () => void;
  tickPaperTimer: () => void;
  tickSectionTimer: () => void;
  tickQuestionTimer: () => void;
  updateIngestData: (questionId: string, data: IngestDataItem) => void;
  markForReview: () => void;
  clearResponse: () => void;
  getPaperForSave: () => PaperData | null; // Get paper with updated time for saving
  getPaperForSubmission: () => PaperData | null;
  reset: () => void;
}

const initialState = {
  paperData: null,
  currentSection: null,
  currentSectionIndex: 0,
  currentQuestion: null,
  currentQuestionIndex: 0,
  isHindi: false,
  fontSize: 12,
  paperTimeRemaining: 0,
  initialTimeRemaining: 0,
  sectionTimeTaken: 0,
  questionTimeTaken: 0,
  ingestData: {},
  ingestCounter: 0,
};

function getSectionQuestions(section?: Section | null): Question[] {
  if (!section) return [];
  return section.questionData?.questions || section.subSections?.[0]?.questionData?.questions || [];
}

export const useExamStore = create<ExamState>((set, get) => ({
  ...initialState,

  setPaperData: (data) => {
    const firstSection = data.paper.pattern.sections[0];
    const questions = getSectionQuestions(firstSection);
    const firstQuestion = questions[0] || null;
    // Use totalTime from root level (in seconds), fallback to paper.totalTime
    // Both totalTime and totalTimeTaken are in seconds from backend
    const totalTimeSeconds = data.totalTime || data.paper.totalTime;
    const timeRemaining = totalTimeSeconds - (data.totalTimeTaken || 0);

    set({
      paperData: data,
      currentSection: firstSection,
      currentSectionIndex: 0,
      currentQuestion: firstQuestion,
      currentQuestionIndex: 0,
      paperTimeRemaining: timeRemaining,
      initialTimeRemaining: timeRemaining, // Track initial time for elapsed calculation
      sectionTimeTaken: firstSection?.timeTakenSecond || 0,
      questionTimeTaken: firstQuestion?.timeTakenInSecond || 0,
    });
  },

  setCurrentSection: (index) => {
    const { paperData } = get();
    if (!paperData) return;

    const section = paperData.paper.pattern.sections[index];
    const questions = getSectionQuestions(section);
    const firstQuestion = questions[0] || null;

    set({
      currentSection: section,
      currentSectionIndex: index,
      currentQuestion: firstQuestion,
      currentQuestionIndex: 0,
      sectionTimeTaken: section?.timeTakenSecond || 0,
      questionTimeTaken: firstQuestion?.timeTakenInSecond || 0,
    });
  },

  setCurrentQuestion: (index) => {
    const { currentSection } = get();
    if (!currentSection) return;

    const questions = getSectionQuestions(currentSection);
    const question = questions[index];

    if (question) {
      set({
        currentQuestion: question,
        currentQuestionIndex: index,
        questionTimeTaken: question.timeTakenInSecond || 0,
      });
    }
  },

  toggleLanguage: () => set((state) => ({ isHindi: !state.isHindi })),

  updateFontSize: (delta) =>
    set((state) => ({
      fontSize: Math.min(18, Math.max(6, state.fontSize + delta)),
    })),

  selectOption: (optionIndex, questionType) => {
    const { currentQuestion, isHindi, paperData, currentSectionIndex, currentQuestionIndex } = get();
    if (!currentQuestion || !paperData) return;

    const problem = isHindi && currentQuestion.problemHindi
      ? currentQuestion.problemHindi
      : currentQuestion.problem;

    const updatedOptions = problem.options.map((opt, idx) => {
      if (questionType === 'RADIOBUTTON') {
        return { ...opt, selected: idx === optionIndex };
      } else {
        return idx === optionIndex ? { ...opt, selected: !opt.selected } : opt;
      }
    });

    // Update the question in the paper data
    const updatedPaper = { ...paperData };
    const section = updatedPaper.paper.pattern.sections[currentSectionIndex];
    const questions = getSectionQuestions(section);

    if (questions[currentQuestionIndex]) {
      const targetProblem = isHindi && questions[currentQuestionIndex].problemHindi
        ? questions[currentQuestionIndex].problemHindi
        : questions[currentQuestionIndex].problem;

      if (targetProblem) {
        targetProblem.options = updatedOptions;

        // Update selected options
        const selectedIndices = updatedOptions
          .map((opt, idx) => (opt.selected ? idx : -1))
          .filter((idx) => idx !== -1);
        targetProblem.so = selectedIndices;
      }
    }

    set({
      paperData: updatedPaper,
      currentQuestion: {
        ...currentQuestion,
        problem: isHindi && currentQuestion.problemHindi
          ? currentQuestion.problem
          : { ...problem, options: updatedOptions, so: updatedOptions.map((opt, idx) => opt.selected ? idx : -1).filter(idx => idx !== -1) },
        problemHindi: isHindi && currentQuestion.problemHindi
          ? { ...currentQuestion.problemHindi, options: updatedOptions }
          : currentQuestion.problemHindi,
      },
    });
  },

  nextQuestion: () => {
    const { currentQuestionIndex, currentSectionIndex, currentSection, paperData } = get();
    if (!currentSection || !paperData) return;

    const questions = getSectionQuestions(currentSection);

    if (currentQuestionIndex < questions.length - 1) {
      get().setCurrentQuestion(currentQuestionIndex + 1);
    } else if (currentSectionIndex < paperData.paper.pattern.sections.length - 1) {
      get().setCurrentSection(currentSectionIndex + 1);
    }
  },

  previousQuestion: () => {
    const { currentQuestionIndex, currentSectionIndex, paperData } = get();
    if (!paperData) return;

    if (currentQuestionIndex > 0) {
      get().setCurrentQuestion(currentQuestionIndex - 1);
    } else if (currentSectionIndex > 0) {
      const prevSection = paperData.paper.pattern.sections[currentSectionIndex - 1];
      const prevQuestions = getSectionQuestions(prevSection);
      const lastQuestionIndex = prevQuestions.length - 1;

      set({
        currentSection: prevSection,
        currentSectionIndex: currentSectionIndex - 1,
      });
      get().setCurrentQuestion(lastQuestionIndex);
    }
  },

  tickPaperTimer: () =>
    set((state) => ({
      paperTimeRemaining: Math.max(0, state.paperTimeRemaining - 1),
    })),

  tickSectionTimer: () =>
    set((state) => ({
      sectionTimeTaken: state.sectionTimeTaken + 1,
    })),

  tickQuestionTimer: () =>
    set((state) => ({
      questionTimeTaken: state.questionTimeTaken + 1,
    })),

  updateIngestData: (questionId, data) =>
    set((state) => ({
      ingestData: { ...state.ingestData, [questionId]: data },
      ingestCounter: state.ingestCounter + 1,
    })),

  markForReview: () => {
    const { currentQuestion } = get();
    if (!currentQuestion) return;
    // Implementation for mark for review
  },

  clearResponse: () => {
    const { currentQuestion, paperData, currentSectionIndex, currentQuestionIndex, isHindi } = get();
    if (!currentQuestion || !paperData) return;

    const updatedPaper = { ...paperData };
    const section = updatedPaper.paper.pattern.sections[currentSectionIndex];
    const questions = getSectionQuestions(section);

    if (questions[currentQuestionIndex]) {
      const problem = isHindi && questions[currentQuestionIndex].problemHindi
        ? questions[currentQuestionIndex].problemHindi
        : questions[currentQuestionIndex].problem;

      if (problem) {
        problem.options = problem.options.map((opt) => ({ ...opt, selected: false }));
        problem.so = [];
      }
    }

    set({ paperData: updatedPaper });
    get().setCurrentQuestion(currentQuestionIndex);
  },

  getPaperForSave: () => {
    const { paperData, initialTimeRemaining, paperTimeRemaining } = get();
    if (!paperData) return null;

    // Calculate elapsed time since exam started/resumed
    const elapsedTime = initialTimeRemaining - paperTimeRemaining;
    const totalTimeTaken = (paperData.totalTimeTaken || 0) + elapsedTime;

    // Return paper data with updated time
    return {
      ...paperData,
      totalTimeTaken,
    };
  },

  getPaperForSubmission: () => {
    const { paperData, initialTimeRemaining, paperTimeRemaining } = get();
    if (!paperData) return null;

    // Calculate elapsed time since exam started/resumed
    const elapsedTime = initialTimeRemaining - paperTimeRemaining;
    const totalTimeTaken = (paperData.totalTimeTaken || 0) + elapsedTime;

    // Calculate scores and update paper data for submission
    const submissionData: PaperData = {
      ...paperData,
      paperStatus: 'DONE',
      endTestTime: Date.now(),
      totalTimeTaken,
    };

    // Calculate total counts
    let totalCorrect = 0;
    let totalIncorrect = 0;
    let totalSkipped = 0;
    let totalScore = 0;
    let sectionsTotalTime = 0;

    submissionData.paper.pattern.sections.forEach((section) => {
      const questions = getSectionQuestions(section);
      let sectionCorrect = 0;
      let sectionIncorrect = 0;
      let sectionSkipped = 0;
      let sectionScore = 0;
      let sectionTimeTaken = 0;
      const subSectionStats: Record<string, { questions: Question[]; correct: number; incorrect: number; skipped: number; score: number; time: number; }> = {};

      questions.forEach((question) => {
        const problem = question.problem;
        const selectedOptions = problem.options
          .map((opt, idx) => (opt.selected ? idx : -1))
          .filter((idx) => idx !== -1);
        const coValue = problem.co;
        const coArray = Array.isArray(coValue) ? coValue : (coValue != null ? [coValue] : []);
        const correctOptions = coArray.map((c) => parseInt(String(c)));

        let status: QuestionAttemptedStatus = 'SKIP';

        if (selectedOptions.length > 0) {
          const isCorrect =
            selectedOptions.length === correctOptions.length &&
            selectedOptions.every((s) => correctOptions.includes(s));

          if (isCorrect) {
            status = 'CORRECT';
            totalCorrect++;
            totalScore += paperData.paper.perQuestionScore;
            sectionCorrect++;
            sectionScore += paperData.paper.perQuestionScore;
          } else {
            status = 'INCORRECT';
            totalIncorrect++;
            totalScore -= paperData.paper.negativeMarks;
            sectionIncorrect++;
            sectionScore -= paperData.paper.negativeMarks;
          }
        } else {
          totalSkipped++;
          sectionSkipped++;
        }

        question.questionAttemptedStatus = status;
        question.score = status === 'CORRECT'
          ? paperData.paper.perQuestionScore
          : status === 'INCORRECT'
          ? -paperData.paper.negativeMarks
          : 0;

        const questionTime = question.timeTakenInSecond || 0;
        sectionTimeTaken += questionTime;

        const subTitle = question.sectionTitle || section.subSections?.[0]?.title || section.title;
        if (!subSectionStats[subTitle]) {
          subSectionStats[subTitle] = { questions: [], correct: 0, incorrect: 0, skipped: 0, score: 0, time: 0 };
        }
        subSectionStats[subTitle].questions.push(question);
        subSectionStats[subTitle].time += questionTime;
        if (status === 'CORRECT') {
          subSectionStats[subTitle].correct += 1;
          subSectionStats[subTitle].score += paperData.paper.perQuestionScore;
        } else if (status === 'INCORRECT') {
          subSectionStats[subTitle].incorrect += 1;
          subSectionStats[subTitle].score -= paperData.paper.negativeMarks;
        } else {
          subSectionStats[subTitle].skipped += 1;
        }
      });

      section.correctCount = sectionCorrect;
      section.inCorrectCount = sectionIncorrect;
      section.skipedCount = sectionSkipped;
      section.scoreInSection = sectionScore;
      section.score = sectionScore;
      section.timeTakenSecond = sectionTimeTaken;
      sectionsTotalTime += sectionTimeTaken;

      if (Array.isArray(section.subSections) && section.subSections.length > 0) {
        section.subSections.forEach((subSection) => {
          const stats = subSectionStats[subSection.title];
          if (stats) {
            subSection.questionData = { questions: stats.questions };
            subSection.correctCount = stats.correct;
            subSection.inCorrectCount = stats.incorrect;
            subSection.skipedCount = stats.skipped;
            subSection.score = stats.score;
            subSection.scoreInSubSection = stats.score;
            subSection.timeTakenSecond = stats.time;
          }
        });
      }
    });

    submissionData.totalCorrectCount = totalCorrect;
    submissionData.totalInCorrectCount = totalIncorrect;
    submissionData.totalSkipedCount = totalSkipped;
    submissionData.totalGetScore = Math.max(0, totalScore);
    submissionData.totalAttemptedQuestionCount = totalCorrect + totalIncorrect;
    // totalTimeTaken is already calculated from elapsed timer above
    submissionData.totalQuestionCount = submissionData.totalQuestionCount || (totalCorrect + totalIncorrect + totalSkipped);
    submissionData.totalScore = submissionData.totalScore || paperData.totalScore || paperData.paper.totalScore;
    submissionData.totalTime = submissionData.totalTime || paperData.totalTime || paperData.paper.totalTime;
    submissionData.paper.totalGetScore = Math.max(0, totalScore);

    return submissionData;
  },

  reset: () => set(initialState),
}));

// Convenience hooks
export const usePaperData = () => useExamStore((state) => state.paperData);
export const useCurrentQuestion = () => useExamStore((state) => state.currentQuestion);
export const useTimeRemaining = () => useExamStore((state) => state.paperTimeRemaining);
