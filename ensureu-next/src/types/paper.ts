// Paper and Exam types for EnsureU

export type PaperType = 'SSC' | 'BANK';

export type PaperCategory = 'SSC_CGL' | 'SSC_CPO' | 'SSC_CHSL' | 'BANK_PO';

export type PaperSubCategory =
  | 'SSC_CGL_TIER1'
  | 'SSC_CGL_TIER2'
  | 'SSC_CHSL_TIER1'
  | 'SSC_CHSL_TIER2'
  | 'SSC_CPO_TIER1'
  | 'SSC_CPO_TIER2'
  | 'BANK_PO_PRE'
  | 'BANK_PO_MAIN';

export type TestType = 'FREE' | 'PAID' | 'MISSED' | 'PASTPAPER' | 'QUIZ';

export type PaperStatus = 'NOT_STARTED' | 'START' | 'INPROGRESS' | 'DONE';

export type PaperStateStatus = 'DRAFT' | 'APPROVED' | 'ACTIVE';

export type QuestionType = 'RADIOBUTTON' | 'CHECKBOX';

export type QuestionAttemptedStatus = 'CORRECT' | 'INCORRECT' | 'SKIP' | 'NA';

export interface Option {
  prompt: string;
  key?: string; // alternative field for prompt
  text: string;
  value?: string; // alternative field for text
  imageUrl?: string;
  selected?: boolean;
  correct?: boolean;
}

export interface Solution {
  text?: string;
  value?: string;
}

export interface Problem {
  question: string;
  value?: string; // alternative field for question text
  options: Option[];
  so: string | number | string[] | number[]; // selected options (can be indices or prompts)
  co: string | number | string[] | number[]; // correct options (can be indices or prompts)
  solution?: string;
  solutions?: Solution[] | string[]; // array of solutions
  explanation?: string;
}

export interface Question {
  id: string;
  qNo: number;
  customQNo?: number;
  problem: Problem;
  problemHindi?: Problem;
  type: string;
  questionType: QuestionType;
  complexityLevel: string;
  complexityScore: number;
  score?: number;
  timeTakenInSecond: number;
  questionAttemptedStatus?: QuestionAttemptedStatus;
  showAnswer?: boolean;
  sectionTitle?: string;
}

export interface SubSection {
  id: string;
  title: string;
  questionData: {
    questions: Question[];
  };
  score?: number;
  scoreInSubSection?: number;
  timeTakenSecond?: number;
  correctCount?: number;
  inCorrectCount?: number;
  skipedCount?: number;
}

export interface Section {
  id: string;
  title: string;
  sectionType: string;
  subSections: SubSection[];
  questionData?: {
    questions: Question[];
  };
  timeTakenSecond: number;
  scoreInSection?: number;
  score?: number;
  questionCount?: number;
  correctCount?: number;
  inCorrectCount?: number;
  skipedCount?: number;
}

export interface Pattern {
  sections: Section[];
}

export interface Paper {
  id: string;
  paperName: string;
  paperType: PaperCategory;
  paperCategory?: PaperCategory; // Alias for paperType
  paperSubCategory: PaperSubCategory;
  testType: TestType;
  totalTime: number;
  totalScore: number;
  totalQuestionCount?: number;
  perQuestionScore: number;
  negativeMarks: number;
  pattern: Pattern;
  totalGetScore?: number;
  status?: PaperStateStatus;
}

export interface PaperData {
  paperId: string;
  paperCategory: PaperCategory;
  paperSubCategory: PaperSubCategory;
  paperStatus: PaperStatus;
  paperName?: string;
  paperType?: string;
  testType?: TestType;
  paper: Paper;
  totalTimeTaken: number;
  totalGetScore: number;
  totalAttemptedQuestionCount: number;
  totalCorrectCount: number;
  totalInCorrectCount: number;
  totalSkipedCount: number;
  totalScore?: number;
  totalTime?: number;
  totalQuestionCount?: number;
  percentile?: number;
  startTestTime?: number;
  endTestTime?: number;
}

export interface PaperListItem {
  paperId: string;
  paperName: string;
  paperType: PaperCategory;
  paperSubCategory: PaperSubCategory;
  paperStatus: PaperStatus;
  testType: TestType;
  totalTime: number;
  totalScore: number;
  totalGetScore?: number;
  validityDate?: string;
}

export interface IngestDataItem {
  questionId: string;
  questionNo: number;
  timeTaken: number;
  questionAttemptedStatus: QuestionAttemptedStatus;
  marks: number;
  order: number;
}

export interface PaperMetaData {
  paperType: PaperCategory;
  subCategories: {
    name: PaperSubCategory;
    label: string;
  }[];
}

export interface CategoryItem {
  name: string;
  label: string;
  icon: string;
  subCategories: {
    name: string;
    label: string;
  }[];
}
