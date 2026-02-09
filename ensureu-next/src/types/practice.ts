export interface PracticeSectionInfo {
  sectionTitle: string;
  subSectionTitle: string;
  questionCount: number;
}

export interface PracticeOption {
  prompt: string;
  value: string;
}

export interface PracticeProblem {
  value: string;
  options: PracticeOption[];
  co: string;
  so?: string;
  solutions?: { value: string }[];
}

export interface PracticeQuestion {
  _id: string;
  problem: PracticeProblem;
}

export interface PracticeQuestionGroup {
  sectionTitle: string;
  subSectionTitle: string;
  questionCount: number;
  questions: PracticeQuestion[];
}

export interface PracticeStorageEntry {
  co: string;
  so: string;
  quesTime: number;
}

// Nested structure: category -> sectionTitle -> subSectionTitle -> questionId -> entry
export type PracticeCategoryData = Record<
  string,  // sectionTitle
  Record<
    string,  // subSectionTitle
    Record<string, PracticeStorageEntry>  // questionId -> entry
  >
>;

export interface PracticeStorage {
  editTime?: number;
  [category: string]: number | undefined | PracticeCategoryData;
}

