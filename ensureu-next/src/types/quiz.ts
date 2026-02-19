// Quiz types for daily quiz feature

import type { PaperCategory, PaperType, TestType, PaperStatus } from './paper';

export type QuizStateStatus = 'DRAFT' | 'APPROVED' | 'ACTIVE';

export interface QuizOption {
  prompt: string; // A, B, C, D
  value: string;  // Option text (may contain HTML)
}

export interface QuizSolution {
  value: string;
}

export interface QuizProblem {
  value: string;        // Question text (may contain HTML)
  valueHindi?: string;  // Hindi translation
  options: QuizOption[];
  co: number | string;  // Correct option - number (0-3) for backend, string (A-D) for display
  so?: number | string; // Selected option - number (0-3) for backend, string (A-D) for display
  solutions?: QuizSolution[];
}

export interface QuizQuestion {
  _id: string;
  problem: QuizProblem;
  quesTime?: number;    // Time spent on question
}

export interface QuizQuestionData {
  questions: QuizQuestion[];
  skip?: string;
}

export interface QuizSubSection {
  title?: string;
  subSectionTitle?: string;  // Legacy field
  questionData?: QuizQuestionData;
  questions?: QuizQuestion[];  // Legacy field - direct questions
}

export interface QuizSection {
  title?: string;
  sectionTitle?: string;  // Legacy field
  subSections: QuizSubSection[];
}

export interface QuizPattern {
  sections: QuizSection[];
}

// Quiz template (admin-managed)
export interface Quiz {
  id: string;
  paperName: string;
  paperType: PaperType;
  paperCategory: PaperCategory;
  paperSubCategory?: string;
  paperSubCategoryName?: string;
  testType: TestType;
  totalQuestionCount: number;
  totalScore: number;
  totalTime: number; // in seconds
  negativeMarks: number;
  perQuestionScore: number;
  paperStateStatus: QuizStateStatus;
  priority?: number;
  createDateTime?: number;
  validityRangeStartDateTime?: number;
  validityRangeEndDateTime?: number;
  pattern?: QuizPattern;
  taken?: boolean;
  totalTakenTime?: number;
}

// Quiz list item (for display in lists)
export interface QuizListItem {
  id: string;
  paperId?: string;
  paperName: string;
  paperType: PaperType;
  paperCategory: PaperCategory;
  testType: TestType;
  totalQuestionCount: number;
  totalScore: number;
  totalTime: number;
  negativeMarks: number;
  perQuestionScore: number;
  paperStatus?: PaperStatus;
  paperStateStatus?: QuizStateStatus;
  taken?: boolean;
  // User attempt data (if completed)
  totalGetScore?: number;
  totalCorrectCount?: number;
  totalInCorrectCount?: number;
  totalSkipedCount?: number;
  totalTimeTaken?: number;
}

// Quiz data for taking the quiz (from encrypted API)
export interface QuizData {
  paperId: string;
  paperName: string;
  paperType: PaperType;
  paperCategory: PaperCategory;
  testType: TestType;
  totalQuestionCount: number;
  totalScore: number;
  totalTime: number;
  negativeMarks: number;
  perQuestionScore: number;
  pattern: QuizPattern;
  paperStatus: PaperStatus;
  // User attempt tracking
  totalGetScore?: number;
  totalCorrectCount?: number;
  totalInCorrectCount?: number;
  totalSkipedCount?: number;
  startTestTime?: number;
  endTestTime?: number;
  totalTimeTaken?: number;
}

// Quiz submission payload
export interface QuizSubmission {
  paperId: string;
  testType: TestType;
  paperStatus: PaperStatus;
  pattern: QuizPattern;
  totalCorrectCount: number;
  totalInCorrectCount: number;
  totalSkipedCount: number;
  totalAttemptedQuestionCount: number;
  totalGetScore: number;
  totalTimeTaken: number;
}

// Admin: Create/Update quiz payload
export interface QuizCreatePayload {
  paperName: string;
  paperType: PaperType;
  paperCategory: PaperCategory;
  paperSubCategory?: string;
  totalQuestionCount: number;
  totalScore: number;
  totalTime: number; // in seconds
  negativeMarks: number;
  perQuestionScore: number;
  paperStateStatus: QuizStateStatus;
  priority?: number;
  validityRangeStartDateTime?: number;
  validityRangeEndDateTime?: number;
  pattern: QuizPattern;
}

// Quiz attempt info (stored in session)
export interface QuizInfo {
  quizId: string;
  paperName: string;
  paperCategory: PaperCategory;
  totalTime: number;
  totalScore: number;
  totalQuestionCount: number;
  negativeMarks: number;
  perQuestionScore: number;
}
