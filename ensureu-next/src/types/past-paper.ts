import type { PaperCategory, PaperSubCategory, PaperStatus, TestType } from './paper';

export interface PastPaperListItem {
  paperId: string;
  paperName: string;
  paperCategory: string;
  paperType: PaperCategory;
  paperSubCategory: PaperSubCategory;
  paperStatus: PaperStatus;
  testType: TestType;
  totalTime: number;
  totalScore: number;
  totalQuestionCount?: number;
  totalGetScore?: number;
  validityDate?: string;
  dateOfExamYear?: string;
  shiftOfExam?: string;
}

