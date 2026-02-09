// Question Bank types for EnsureU

import { PaperType, PaperCategory, PaperSubCategory } from './paper';

export type QuestionBankStatus = 'DRAFT' | 'PENDING_REVIEW' | 'APPROVED' | 'REJECTED' | 'ARCHIVED';

export type DifficultyLevel = 'EASY' | 'MEDIUM' | 'HARD';

export type QuestionTypeEnum = 'SINGLE' | 'MULTIPLE';

export interface QuestionBankOption {
  key: string;          // A, B, C, D
  value: string;        // Option text (HTML supported)
  valueHindi?: string;  // Hindi translation (optional)
}

export interface QuestionBankProblem {
  question: string;         // Question text (HTML supported)
  questionHindi?: string;   // Hindi translation (optional)
  options: QuestionBankOption[];
  correctOption: string;    // "A", "B", "C", or "D"
  solution?: string;        // Explanation (HTML supported)
  solutionHindi?: string;   // Hindi translation (optional)
}

export interface QuestionBankItem {
  id: string;
  questionId: string;

  // Taxonomy
  paperType: PaperType;
  paperCategory: PaperCategory;
  paperSubCategory: PaperSubCategory;
  subject: string;
  topic: string;
  subTopic?: string;

  // Question Content
  problem: QuestionBankProblem;

  // Metadata
  questionType: QuestionTypeEnum;
  difficultyLevel: DifficultyLevel;
  marks: number;
  negativeMarks: number;
  averageTime: number;

  // Media
  hasImage?: boolean;
  imageUrl?: string;
  imagePosition?: string;

  // Status & Audit
  status: QuestionBankStatus;
  createdBy: string;
  createdByName: string;
  createdAt: number;
  updatedBy?: string;
  updatedAt?: number;
  approvedBy?: string;
  approvedAt?: number;
  rejectionReason?: string;

  // Usage Tracking
  usageCount?: number;
  lastUsedAt?: number;
  papersUsedIn?: string[];

  // Tags & Search
  tags?: string[];
  year?: number;
  source?: string;
}

export interface QuestionBankCreatePayload {
  paperType: PaperType;
  paperCategory: PaperCategory;
  paperSubCategory: PaperSubCategory;
  subject: string;
  topic: string;
  subTopic?: string;
  problem: QuestionBankProblem;
  questionType: QuestionTypeEnum;
  difficultyLevel: DifficultyLevel;
  marks: number;
  negativeMarks: number;
  averageTime: number;
  hasImage?: boolean;
  imageUrl?: string;
  imagePosition?: string;
  tags?: string[];
  year?: number;
  source?: string;
  submitForReview?: boolean;
}

export interface QuestionBankStats {
  totalQuestions: number;
  draftCount: number;
  pendingReviewCount: number;
  approvedCount: number;
  rejectedCount: number;
  archivedCount: number;
  questionsBySubject?: Record<string, number>;
  questionsByDifficulty?: Record<string, number>;
  questionsByCategory?: Record<string, number>;
  myTotalQuestions?: number;
  myDraftCount?: number;
  myPendingCount?: number;
  myApprovedCount?: number;
  myRejectedCount?: number;
}

export interface QuestionBankListParams {
  paperType?: PaperType;
  paperCategory?: PaperCategory;
  paperSubCategory?: PaperSubCategory;
  subject?: string;
  topic?: string;
  difficultyLevel?: DifficultyLevel;
  status?: QuestionBankStatus;
  createdBy?: string;
  page?: number;
  size?: number;
}

// Subject and Topic data for SSC CGL TIER-1
export const SSC_CGL_TIER1_SUBJECTS = [
  {
    name: 'General Intelligence and Reasoning',
    topics: [
      'Analogies',
      'Classification',
      'Series',
      'Coding-Decoding',
      'Blood Relations',
      'Direction & Distance',
      'Order & Ranking',
      'Syllogism',
      'Venn Diagrams',
      'Puzzles',
      'Statement & Conclusions',
      'Non-verbal Reasoning',
    ],
  },
  {
    name: 'General Awareness',
    topics: [
      'History',
      'Geography',
      'Polity',
      'Economy',
      'Current Affairs',
      'Physics',
      'Chemistry',
      'Biology',
      'Computer Awareness',
      'Sports',
      'Awards & Honours',
      'Books & Authors',
    ],
  },
  {
    name: 'Quantitative Aptitude',
    topics: [
      'Number System',
      'Simplification',
      'Percentage',
      'Ratio & Proportion',
      'Average',
      'Profit & Loss',
      'Simple & Compound Interest',
      'Time & Work',
      'Time, Speed & Distance',
      'Algebra',
      'Geometry',
      'Mensuration',
      'Trigonometry',
      'Data Interpretation',
    ],
  },
  {
    name: 'English Comprehension',
    topics: [
      'Reading Comprehension',
      'Cloze Test',
      'Error Spotting',
      'Sentence Improvement',
      'Fill in the Blanks',
      'Synonyms & Antonyms',
      'One Word Substitution',
      'Idioms & Phrases',
      'Spelling Correction',
      'Active/Passive Voice',
      'Direct/Indirect Speech',
    ],
  },
];

export const getTopicsForSubject = (subject: string): string[] => {
  const subjectData = SSC_CGL_TIER1_SUBJECTS.find((s) => s.name === subject);
  return subjectData?.topics || [];
};
