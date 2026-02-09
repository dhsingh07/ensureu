// Paper hooks - migrated from Angular test-paper-editor services

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { get, post } from '@/lib/api/client';
import { API_URLS } from '@/lib/constants/api-urls';
import { encrypt, decrypt } from '@/lib/utils/encryption';
import { useUIStore } from '@/stores/ui-store';
import type {
  PaperData,
  PaperListItem,
  PaperCategory,
  PaperSubCategory,
  TestType,
  PaperStatus,
  Question,
} from '@/types/paper';
import type { ApiResponse, EncryptedResponse } from '@/types/api';

// Demo mode check - DISABLED for testing
const DEMO_MODE = false; // process.env.NEXT_PUBLIC_DEMO_MODE === 'true';
console.log('[use-papers] DEMO_MODE:', DEMO_MODE, '(forced false for testing)');

// Mock data for demo mode
const mockPapers: PaperListItem[] = [
  {
    paperId: 'demo-paper-1',
    paperName: 'SSC CGL Tier-1 Mock Test 1',
    paperType: 'SSC_CGL',
    paperSubCategory: 'SSC_CGL_TIER1',
    paperStatus: 'NOT_STARTED',
    testType: 'FREE',
    totalTime: 60,
    totalScore: 200,
  },
  {
    paperId: 'demo-paper-2',
    paperName: 'SSC CGL Tier-1 Mock Test 2',
    paperType: 'SSC_CGL',
    paperSubCategory: 'SSC_CGL_TIER1',
    paperStatus: 'INPROGRESS',
    testType: 'FREE',
    totalTime: 60,
    totalScore: 200,
  },
  {
    paperId: 'demo-paper-3',
    paperName: 'SSC CGL Premium Test 1',
    paperType: 'SSC_CGL',
    paperSubCategory: 'SSC_CGL_TIER1',
    paperStatus: 'NOT_STARTED',
    testType: 'PAID',
    totalTime: 60,
    totalScore: 200,
  },
  {
    paperId: 'demo-paper-4',
    paperName: 'Bank PO Prelims Mock 1',
    paperType: 'BANK_PO',
    paperSubCategory: 'BANK_PO_PRE',
    paperStatus: 'NOT_STARTED',
    testType: 'FREE',
    totalTime: 60,
    totalScore: 100,
  },
];

const OPTION_SEPARATOR = '#';

function isPaperData(value: unknown): value is PaperData {
  if (!value || typeof value !== 'object') return false;
  const maybe = value as { paper?: { pattern?: { sections?: unknown[] } } };
  return Array.isArray(maybe.paper?.pattern?.sections);
}

function toIndexArray(value: unknown): number[] {
  if (value === null || value === undefined) return [];
  if (typeof value === 'number') return value === 0 ? [] : [value];
  if (typeof value === 'string') {
    if (!value || value === '0') return [];
    return value
      .split(OPTION_SEPARATOR)
      .map((v) => Number.parseInt(v, 10))
      .filter((v) => Number.isFinite(v));
  }
  if (Array.isArray(value)) {
    return value
      .map((v) => Number.parseInt(String(v), 10))
      .filter((v) => Number.isFinite(v));
  }
  return [];
}

function normalizeQuestion(question: Question, sectionTitle?: string, customQNo?: number): Question {
  if (sectionTitle) question.sectionTitle = sectionTitle;
  if (customQNo !== undefined) question.customQNo = customQNo;

  const normalizeProblem = (problem?: Question['problem']) => {
    if (!problem) return;
    const problemAny = problem as unknown as {
      value?: string;
      image?: string;
      question?: string;
      options?: Array<{ value?: string; text?: string; image?: string; imageUrl?: string; prompt?: string }>;
    };
    if (!problemAny.question && problemAny.value) {
      problemAny.question = problemAny.value;
    }
    const so = toIndexArray(problem.so);
    const co = toIndexArray(problem.co);
    problem.so = so;
    problem.co = co;
    if (Array.isArray(problem.options)) {
      problem.options = problem.options.map((opt, idx) => ({
        ...opt,
        text: opt.text ?? (opt as { value?: string }).value,
        imageUrl: opt.imageUrl ?? (opt as { image?: string }).image,
        selected: so.includes(idx),
      }));
    }
  };

  normalizeProblem(question.problem);
  normalizeProblem(question.problemHindi);
  return question;
}

function normalizePaperData(data: PaperData): PaperData {
  const normalized: PaperData = JSON.parse(JSON.stringify(data));
  const sections = normalized.paper?.pattern?.sections || [];

  sections.forEach((section) => {
    const subSections = section.subSections || [];
    const flattened: Question[] = [];
    let customQNo = 0;

    if (subSections.length > 0) {
      subSections.forEach((subSection) => {
        const questions = subSection.questionData?.questions || [];
        questions.forEach((q) => {
          flattened.push(normalizeQuestion(q, subSection.title, ++customQNo));
        });
      });
      section.questionData = { questions: flattened };
    } else if (section.questionData?.questions) {
      section.questionData.questions = section.questionData.questions.map((q) =>
        normalizeQuestion(q, section.title, ++customQNo)
      );
    } else if (Array.isArray((section as { questions?: Question[] }).questions)) {
      const rawQuestions = (section as { questions?: Question[] }).questions || [];
      section.questionData = {
        questions: rawQuestions.map((q) => normalizeQuestion(q, section.title, ++customQNo)),
      };
    }
  });

  return normalized;
}

function toServerOptionValue(value: unknown): number | string {
  const indices = toIndexArray(value);
  if (indices.length === 0) return 0;
  if (indices.length === 1) return indices[0];
  return indices.join(OPTION_SEPARATOR);
}

function preparePaperForSave(data: PaperData): PaperData {
  const prepared: PaperData = JSON.parse(JSON.stringify(data));
  const preparedAny = prepared as PaperData & {
    paperType?: string;
    paperName?: string;
  };
  if (!preparedAny.paperType && prepared.paper?.paperType) {
    preparedAny.paperType = prepared.paper.paperType;
  }
  if (!preparedAny.paperName && prepared.paper?.paperName) {
    preparedAny.paperName = prepared.paper.paperName;
  }
  if (prepared.paper) {
    if (!prepared.paper.paperCategory && prepared.paperCategory) {
      prepared.paper.paperCategory = prepared.paperCategory;
    }
    if (!prepared.paper.paperSubCategory && prepared.paperSubCategory) {
      prepared.paper.paperSubCategory = prepared.paperSubCategory;
    }
    if (!prepared.paper.testType && prepared.testType) {
      prepared.paper.testType = prepared.testType;
    }
  }
  const sections = prepared.paper?.pattern?.sections || [];

  sections.forEach((section) => {
    const bySubSectionTitle: Record<string, Question[]> = {};
    const questions = section.questionData?.questions || [];

    questions.forEach((question) => {
      const sectionTitle = question.sectionTitle || section.subSections?.[0]?.title || section.title;
      if (!bySubSectionTitle[sectionTitle]) {
        bySubSectionTitle[sectionTitle] = [];
      }

      delete question.sectionTitle;
      delete question.customQNo;
      if ('showAnswer' in question) {
        delete question.showAnswer;
      }

      if (question.problem) {
        question.problem.so = toServerOptionValue(question.problem.so);
        question.problem.co = toServerOptionValue(question.problem.co);
        const problemAny = question.problem as unknown as { value?: string; question?: string };
        if (!problemAny.value && problemAny.question) {
          problemAny.value = problemAny.question;
        }
      }
      if (question.problemHindi) {
        question.problemHindi.so = toServerOptionValue(question.problemHindi.so);
        question.problemHindi.co = toServerOptionValue(question.problemHindi.co);
        const problemHindiAny = question.problemHindi as unknown as { value?: string; question?: string };
        if (!problemHindiAny.value && problemHindiAny.question) {
          problemHindiAny.value = problemHindiAny.question;
        }
      }
      if (question.problem?.options) {
        question.problem.options = question.problem.options.map((opt) => ({
          ...opt,
          value: (opt as { value?: string }).value ?? opt.text,
          image: (opt as { image?: string }).image ?? opt.imageUrl,
        }));
      }
      if (question.problemHindi?.options) {
        question.problemHindi.options = question.problemHindi.options.map((opt) => ({
          ...opt,
          value: (opt as { value?: string }).value ?? opt.text,
          image: (opt as { image?: string }).image ?? opt.imageUrl,
        }));
      }

      bySubSectionTitle[sectionTitle].push(question);
    });

    if (Array.isArray(section.subSections) && section.subSections.length > 0) {
      section.subSections.forEach((subSection) => {
        const updatedQuestions = bySubSectionTitle[subSection.title];
        if (updatedQuestions) {
          subSection.questionData = { questions: updatedQuestions };
        }
      });
      delete section.questionData;
    } else if (section.questionData?.questions) {
      section.questionData.questions.forEach((question) => {
        if (question.problem) {
          question.problem.so = toServerOptionValue(question.problem.so);
          question.problem.co = toServerOptionValue(question.problem.co);
        }
        if (question.problemHindi) {
          question.problemHindi.so = toServerOptionValue(question.problemHindi.so);
          question.problemHindi.co = toServerOptionValue(question.problemHindi.co);
        }
      });
    }
  });

  return prepared;
}

// Helper to extract PaperType from PaperCategory
// SSC_CGL, SSC_CPO, SSC_CHSL -> SSC
// BANK_PO -> BANK
function getPaperType(category: PaperCategory): string {
  if (category.startsWith('SSC_')) return 'SSC';
  if (category.startsWith('BANK_')) return 'BANK';
  // Default: return the first part before underscore
  return category.split('_')[0];
}

// Query keys factory
export const paperKeys = {
  all: ['papers'] as const,
  lists: () => [...paperKeys.all, 'list'] as const,
  list: (category: PaperCategory, subCategory: PaperSubCategory) =>
    [...paperKeys.lists(), category, subCategory] as const,
  details: () => [...paperKeys.all, 'detail'] as const,
  detail: (paperId: string) => [...paperKeys.details(), paperId] as const,
  freePaid: (category: PaperCategory, subCategory: PaperSubCategory) =>
    [...paperKeys.all, 'freePaid', category, subCategory] as const,
  missed: (category: PaperCategory, subCategory: PaperSubCategory) =>
    [...paperKeys.all, 'missed', category, subCategory] as const,
  completed: (category: PaperCategory, subCategory: PaperSubCategory) =>
    [...paperKeys.all, 'completed', category, subCategory] as const,
};

// Get free/paid test list
// Backend API: paper/user/list/paperType/{paperType}?paperCategory={paperCategory}
// - paperType: SSC, BANK (extracted from category)
// - paperCategory: SSC_CGL, SSC_CPO, BANK_PO (the category itself)
export function useTestList(
  category: PaperCategory,
  subCategory: PaperSubCategory,
  enabled = true
) {
  return useQuery({
    queryKey: paperKeys.list(category, subCategory),
    queryFn: async () => {
      // Demo mode - return mock data
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 300));
        return mockPapers.filter(
          (p) => p.paperType === category && p.paperSubCategory === subCategory
        );
      }

      // Backend expects:
      // - paperType in path: SSC, BANK
      // - paperCategory in query: SSC_CGL, SSC_CPO, etc.
      const paperType = getPaperType(category);
      const url = `${API_URLS.HOME.TEST_LIST}/${paperType}?paperCategory=${category}`;
      const response = await get<PaperListItem[] | ApiResponse<PaperListItem[]>>(url);

      // Handle both response formats:
      // 1. Direct array: [...]
      // 2. Wrapped: { body: [...] }
      if (Array.isArray(response)) {
        return response;
      }
      return response.body || [];
    },
    enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
}

// Get missed tests
// Backend API: paper/user/missed/paperType/{paperType}?testType=PAID&paperCategory={paperCategory}
export function useMissedTests(
  category: PaperCategory,
  subCategory: PaperSubCategory,
  enabled = true
) {
  return useQuery({
    queryKey: paperKeys.missed(category, subCategory),
    queryFn: async () => {
      // Demo mode - return empty
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 200));
        return [];
      }

      const paperType = getPaperType(category);
      const response = await get<PaperListItem[] | ApiResponse<PaperListItem[]>>(
        `${API_URLS.HOME.MISSED_LIST}/${paperType}?testType=PAID&paperCategory=${category}`
      );
      if (Array.isArray(response)) {
        return response;
      }
      return response.body || [];
    },
    enabled,
    staleTime: 5 * 60 * 1000,
  });
}

// Get completed tests
// Backend API: paper/user/paperType/{paperType}?paperStatus=DONE&paperCategory={paperCategory}
export function useCompletedTests(
  category: PaperCategory,
  subCategory: PaperSubCategory,
  enabled = true
) {
  return useQuery({
    queryKey: paperKeys.completed(category, subCategory),
    queryFn: async () => {
      // Demo mode - return empty
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 200));
        return [];
      }

      const paperType = getPaperType(category);
      const response = await get<PaperListItem[] | ApiResponse<PaperListItem[]>>(
        `${API_URLS.HOME.COMPLETED_LIST}/${paperType}?paperStatus=DONE&paperCategory=${category}`
      );
      if (Array.isArray(response)) {
        return response;
      }
      return response.body || [];
    },
    enabled,
    staleTime: 5 * 60 * 1000,
  });
}

// Get test paper data (with encryption)
export function useTestPaper(
  testType: TestType,
  paperStatus: PaperStatus,
  paperId: string,
  enabled = true
) {
  return useQuery({
    queryKey: paperKeys.detail(paperId),
    queryFn: async () => {
      // Demo mode - return mock paper data
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 500));
        const mockPaperData: PaperData = {
          paperId,
          paperCategory: 'SSC_CGL',
          paperSubCategory: 'SSC_CGL_TIER1',
          paperStatus: 'INPROGRESS',
          totalTimeTaken: 0,
          totalGetScore: 0,
          totalAttemptedQuestionCount: 0,
          totalCorrectCount: 0,
          totalInCorrectCount: 0,
          totalSkipedCount: 0,
          paper: {
            id: paperId,
            paperName: 'Demo Test Paper',
            paperType: 'SSC_CGL',
            paperSubCategory: 'SSC_CGL_TIER1',
            testType: 'FREE',
            totalTime: 60,
            totalScore: 200,
            perQuestionScore: 2,
            negativeMarks: 0.5,
            pattern: {
              sections: [
                {
                  id: 'section-1',
                  title: 'General Intelligence',
                  sectionType: 'DEFAULT',
                  subSections: [],
                  timeTakenSecond: 0,
                  questionData: {
                    questions: [
                      {
                        id: 'q1',
                        qNo: 1,
                        problem: {
                          question: 'What is 2 + 2?',
                          options: [
                            { prompt: 'A', text: '3', selected: false },
                            { prompt: 'B', text: '4', selected: false },
                            { prompt: 'C', text: '5', selected: false },
                            { prompt: 'D', text: '6', selected: false },
                          ],
                          so: [],
                          co: ['1'],
                        },
                        type: 'MCQ',
                        questionType: 'RADIOBUTTON',
                        complexityLevel: 'EASY',
                        complexityScore: 1,
                        timeTakenInSecond: 0,
                      },
                      {
                        id: 'q2',
                        qNo: 2,
                        problem: {
                          question: 'What is the capital of India?',
                          options: [
                            { prompt: 'A', text: 'Mumbai', selected: false },
                            { prompt: 'B', text: 'New Delhi', selected: false },
                            { prompt: 'C', text: 'Kolkata', selected: false },
                            { prompt: 'D', text: 'Chennai', selected: false },
                          ],
                          so: [],
                          co: ['1'],
                        },
                        type: 'MCQ',
                        questionType: 'RADIOBUTTON',
                        complexityLevel: 'EASY',
                        complexityScore: 1,
                        timeTakenInSecond: 0,
                      },
                    ],
                  },
                },
              ],
            },
          },
        };
        return mockPaperData;
      }

      console.log('[useTestPaper] Fetching paper:', { testType, paperStatus, paperId });
      console.log('[useTestPaper] URL:', `${API_URLS.PAPER.GET_ENCRYPTED}/${testType}/${paperStatus}/${paperId}`);

      const response = await get<EncryptedResponse>(
        `${API_URLS.PAPER.GET_ENCRYPTED}/${testType}/${paperStatus}/${paperId}`
      );

      console.log('[useTestPaper] API response:', response);
      console.log('[useTestPaper] Response body (encrypted):', response.body?.substring(0, 100) + '...');

      if (response?.body) {
        // Decrypt the response
        const decrypted = decrypt<PaperData>(response.body, true);
        const normalized = normalizePaperData(decrypted);
        console.log('[useTestPaper] Decrypted paper:', normalized);
        console.log('[useTestPaper] Paper name:', normalized?.paper?.paperName);
        console.log('[useTestPaper] Sections:', normalized?.paper?.pattern?.sections?.length);
        return normalized;
      }

      if (typeof response === 'string') {
        const decrypted = decrypt<PaperData>(response, true);
        return normalizePaperData(decrypted);
      }

      if (isPaperData(response)) {
        return normalizePaperData(response);
      }

      throw new Error('Invalid encrypted response from server');
    },
    enabled: enabled && !!paperId,
    staleTime: 0, // Always fetch fresh data for exams
    gcTime: 0, // Don't cache exam data
  });
}

// Get test paper data (unencrypted - for development)
export function useTestPaperUnencrypted(
  testType: TestType,
  paperStatus: PaperStatus,
  paperId: string,
  enabled = true
) {
  return useQuery({
    queryKey: paperKeys.detail(paperId),
    queryFn: async () => {
      const response = await get<ApiResponse<PaperData>>(
        `${API_URLS.PAPER.GET}/${testType}/${paperId}`
      );
      return response.body;
    },
    enabled: enabled && !!paperId,
    staleTime: 0,
  });
}

// Save test paper answer (encrypted)
export function useSaveAnswer() {
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (paperData: PaperData) => {
      // Demo mode - simulate save
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 300));
        return;
      }

      const prepared = preparePaperForSave(paperData);
      const encrypted = encrypt(prepared, true);
      return post<void>(API_URLS.PAPER.SAVE_ENCRYPTED, { body: encrypted });
    },
    onError: () => {
      showAlert('error', 'Failed to save your progress. Please try again.');
    },
  });
}

// Save test paper answer (unencrypted - for development)
export function useSaveAnswerUnencrypted() {
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (paperData: PaperData) => {
      return post<void>(API_URLS.PAPER.SAVE, paperData);
    },
    onError: () => {
      showAlert('error', 'Failed to save your progress. Please try again.');
    },
  });
}

// Submit test paper
export function useSubmitPaper() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (paperData: PaperData) => {
      // Demo mode - simulate submit
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 500));
        return;
      }

      const submissionData: PaperData = {
        ...paperData,
        paperStatus: 'DONE',
        endTestTime: Date.now(),
      };
      const prepared = preparePaperForSave(submissionData);
      const encrypted = encrypt(prepared, true);
      return post<void>(API_URLS.PAPER.SAVE_ENCRYPTED, { body: encrypted });
    },
    onSuccess: async (_data, variables) => {
      // Only invalidate list queries, not the detail query (to avoid refetch errors during summary modal)
      queryClient.invalidateQueries({ queryKey: paperKeys.lists() });
      queryClient.invalidateQueries({ queryKey: paperKeys.missed(variables.paperCategory, variables.paperSubCategory) });
      queryClient.invalidateQueries({ queryKey: paperKeys.completed(variables.paperCategory, variables.paperSubCategory) });
      // Trigger analytics ingestion for PAID tests (backend endpoint uses PAID service)
      if (variables?.testType === 'PAID') {
        try {
          await post<void>(`${API_URLS.ANALYTICS.USER_PAPER_STAT}?paperId=${variables.paperId}`);
        } catch {
          // Analytics ingestion failure should not block user flow
        }
      }
      showAlert('success', 'Test submitted successfully!');
    },
    onError: () => {
      showAlert('error', 'Failed to submit test. Please try again.');
    },
  });
}

// Get paper info for starting a test
export interface PaperInfo {
  paperId: string;
  testType: TestType;
  paperStatus: PaperStatus;
  paperCategory: PaperCategory;
  paperSubCategory?: PaperSubCategory;
  totalTime?: number;
  totalScore?: number;
  paperName?: string;
  readOnly?: boolean;
}

export function savePaperInfo(info: PaperInfo) {
  if (typeof window !== 'undefined') {
    sessionStorage.setItem('paperInfo', JSON.stringify(info));
  }
}

export function getPaperInfo(): PaperInfo | null {
  if (typeof window !== 'undefined') {
    const stored = sessionStorage.getItem('paperInfo');
    if (stored) {
      try {
        return JSON.parse(stored);
      } catch {
        return null;
      }
    }
  }
  return null;
}

export function clearPaperInfo() {
  if (typeof window !== 'undefined') {
    sessionStorage.removeItem('paperInfo');
  }
}

// ============================================
// Public APIs (no auth required) - for landing page
// ============================================

// Subscription item type (returned by subscription/getAllType/* APIs)
export interface SubscriptionItem {
  id: number;
  paperType: string; // e.g., "SSC", "BANK"
  paperCategory: string; // e.g., "SSC CGL", "Bank PO" (display names)
  paperSubCategory: string; // e.g., "SSC CGL TIER-1"
  testType: string; // "FREE" or "PAID"
  description?: string;
  validity?: number;
  paperIds?: string[];
  paperInfoList?: { id: string; paperName: string }[];
  mapOfSubTypeVsPrice?: Record<string, number>;
}

// Query keys for public data
export const publicKeys = {
  all: ['public'] as const,
  freeSubscriptions: () => [...publicKeys.all, 'free-subscriptions'] as const,
  paidSubscriptions: () => [...publicKeys.all, 'paid-subscriptions'] as const,
  paperMetadata: (category: PaperCategory) =>
    [...publicKeys.all, 'metadata', category] as const,
};

// Mock data for public subscriptions
const mockFreeSubscriptions: SubscriptionItem[] = [
  {
    id: 1,
    paperType: 'SSC',
    paperCategory: 'SSC CGL',
    paperSubCategory: 'SSC CGL TIER-1',
    description: 'Free mock tests for SSC CGL Tier 1',
    testType: 'FREE',
    paperInfoList: [{ id: '1', paperName: 'Mock Test 1' }, { id: '2', paperName: 'Mock Test 2' }],
  },
  {
    id: 2,
    paperType: 'BANK',
    paperCategory: 'Bank PO',
    paperSubCategory: 'Bank PO Prelims',
    description: 'Free mock tests for Bank PO Prelims',
    testType: 'FREE',
    paperInfoList: [{ id: '3', paperName: 'Mock Test 1' }],
  },
];

const mockPaidSubscriptions: SubscriptionItem[] = [
  {
    id: 3,
    paperType: 'SSC',
    paperCategory: 'SSC CGL',
    paperSubCategory: 'SSC CGL TIER-1',
    description: 'Complete SSC CGL preparation',
    testType: 'PAID',
    paperInfoList: [{ id: '4', paperName: 'Premium Test 1' }, { id: '5', paperName: 'Premium Test 2' }],
    mapOfSubTypeVsPrice: { MONTHLY: 499, QUARTERLY: 999 },
  },
  {
    id: 4,
    paperType: 'BANK',
    paperCategory: 'Bank PO',
    paperSubCategory: 'Bank PO Prelims',
    description: 'Complete Bank PO preparation',
    testType: 'PAID',
    paperInfoList: [{ id: '6', paperName: 'Premium Test 1' }],
    mapOfSubTypeVsPrice: { MONTHLY: 599, QUARTERLY: 1199 },
  },
];

// API response type for subscription endpoints
interface SubscriptionResponse {
  listOfPaperTypeDto: unknown;
  listOfFreeSubscription?: SubscriptionItem[];
  listOfPaidSubscription?: SubscriptionItem[];
}

// Get free subscriptions (public - no auth required)
// Angular API: subscription/getAllType/FREE
export function useFreeSubscriptions(enabled = true) {
  return useQuery({
    queryKey: publicKeys.freeSubscriptions(),
    queryFn: async () => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 300));
        return mockFreeSubscriptions;
      }

      const response = await get<ApiResponse<SubscriptionResponse>>(
        API_URLS.DASHBOARD.FREE_PAPER
      );
      return response.body?.listOfFreeSubscription || [];
    },
    enabled,
    staleTime: 10 * 60 * 1000, // 10 minutes
  });
}

// Get paid subscriptions (public - no auth required)
// Angular API: subscription/getAllType/PAID
export function usePaidSubscriptions(enabled = true) {
  return useQuery({
    queryKey: publicKeys.paidSubscriptions(),
    queryFn: async () => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 300));
        return mockPaidSubscriptions;
      }

      const response = await get<ApiResponse<SubscriptionResponse>>(
        API_URLS.DASHBOARD.PAID_PAPER
      );
      return response.body?.listOfPaidSubscription || [];
    },
    enabled,
    staleTime: 10 * 60 * 1000, // 10 minutes
  });
}

// Get paper metadata for a category (subcategories info)
// Angular API: papermetadata/paperType/${paperType}
export function usePaperMetadata(category: PaperCategory, enabled = true) {
  return useQuery({
    queryKey: publicKeys.paperMetadata(category),
    queryFn: async () => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 200));
        // Return mock metadata based on category
        const mockMetadata: Record<PaperCategory, { name: PaperSubCategory; label: string }[]> = {
          SSC_CGL: [
            { name: 'SSC_CGL_TIER1', label: 'Tier 1' },
            { name: 'SSC_CGL_TIER2', label: 'Tier 2' },
          ],
          SSC_CPO: [
            { name: 'SSC_CPO_TIER1', label: 'Tier 1' },
            { name: 'SSC_CPO_TIER2', label: 'Tier 2' },
          ],
          SSC_CHSL: [
            { name: 'SSC_CHSL_TIER1', label: 'Tier 1' },
            { name: 'SSC_CHSL_TIER2', label: 'Tier 2' },
          ],
          BANK_PO: [
            { name: 'BANK_PO_PRE', label: 'Prelims' },
            { name: 'BANK_PO_MAIN', label: 'Mains' },
          ],
        };
        return { paperType: category, subCategories: mockMetadata[category] || [] };
      }

      const response = await get<ApiResponse<{
        paperType: PaperCategory;
        subCategories: { name: PaperSubCategory; label: string }[];
      }>>(`${API_URLS.HOME.PAPER_TYPE_DATA}/${category}`);
      return response.body;
    },
    enabled,
    staleTime: 30 * 60 * 1000, // 30 minutes - metadata rarely changes
  });
}

// ============================================
// Analytics/Results APIs - for results analysis page
// ============================================

// Paper result/analytics data type
export interface PaperResultData {
  paperId: string;
  paperName: string;
  paperCategory: string;
  totalScore: number;
  obtainedScore: number;
  totalQuestions: number;
  correctCount: number;
  incorrectCount: number;
  skippedCount: number;
  timeTaken: number; // seconds
  percentile?: number;
  rank?: number;
  totalParticipants?: number;
  sections?: {
    title: string;
    totalQuestions: number;
    correct: number;
    incorrect: number;
    skipped: number;
    score: number;
    maxScore: number;
  }[];
}

export interface PaperAnalysisData {
  paperId: string;
  paperName: string;
  paperCategory: string;
  testType: TestType;
  totalScore: number;
  obtainedScore: number;
  totalQuestions: number;
  correctCount: number;
  incorrectCount: number;
  skippedCount: number;
  timeTaken: number; // seconds
  percentile?: number;
  rank?: number;
  totalParticipants?: number;
  sections: {
    title: string;
    totalQuestions: number;
    correct: number;
    incorrect: number;
    skipped: number;
    score: number;
    maxScore: number;
    timeTaken: number;
  }[];
}

export interface UserAnalyticsData {
  userScoreDto?: {
    userId?: string;
    name?: string;
    paperDescription?: string;
    score?: number;
    maxPossibleScore?: number;
    totalSubmissionsPerCategory?: number;
    rank?: number;
  };
  percentileList?: {
    marks: number;
    percentile: number;
    label?: string;
    rank: number;
    userIds?: string[];
  }[];
  timeHistogramList?: unknown[];
  sectionHistogramDtoList?: {
    section?: string;
    subSection?: string;
    totalRightQuestions?: number;
    totalWrongQuestions?: number;
    totalSkipped?: number;
    totalQuestions?: number;
    totalMarks?: number;
    questionSpeedCompDtoList?: {
      question_id?: string;
      question_number?: number;
      time_taken_by_user?: number;
      time_taken_by_topper?: number;
      average_time?: number;
      user_marks?: number;
      topper_marks?: number;
      avg_marks?: number;
    }[];
  }[];
  timeSeriesDto?: {
    userQuestionTimeDtoList?: {
      questionAttemptedStatus?: string;
      timeTaken?: number;
    }[];
  };
  userGrowthDto?: {
    userGrowthPointDtoMap?: Record<string, {
      paperName?: string;
      avgMarks?: number;
      topperMarks?: number;
      userMarks?: number;
    }>;
  };
}

// Query keys for analytics
export const analyticsKeys = {
  all: ['analytics'] as const,
  paperResult: (paperId: string) => [...analyticsKeys.all, 'paper-result', paperId] as const,
  paperAnalysis: (paperId: string) => [...analyticsKeys.all, 'paper-analysis', paperId] as const,
  userStats: (paperId: string, paperCategory: PaperCategory) =>
    [...analyticsKeys.all, 'user-stats', paperCategory, paperId] as const,
};

// Mock data for demo mode
const mockPaperResult: PaperResultData = {
  paperId: '',
  paperName: 'SSC CGL Tier-1 Mock Test 1',
  paperCategory: 'SSC_CGL',
  totalScore: 200,
  obtainedScore: 156,
  totalQuestions: 100,
  correctCount: 78,
  incorrectCount: 12,
  skippedCount: 10,
  timeTaken: 3540,
  percentile: 85,
  rank: 1250,
  totalParticipants: 8500,
  sections: [
    {
      title: 'General Intelligence & Reasoning',
      totalQuestions: 25,
      correct: 20,
      incorrect: 3,
      skipped: 2,
      score: 37,
      maxScore: 50,
    },
    {
      title: 'General Awareness',
      totalQuestions: 25,
      correct: 18,
      incorrect: 4,
      skipped: 3,
      score: 32,
      maxScore: 50,
    },
    {
      title: 'Quantitative Aptitude',
      totalQuestions: 25,
      correct: 22,
      incorrect: 2,
      skipped: 1,
      score: 42,
      maxScore: 50,
    },
    {
      title: 'English Comprehension',
      totalQuestions: 25,
      correct: 18,
      incorrect: 3,
      skipped: 4,
      score: 33,
      maxScore: 50,
    },
  ],
};

async function fetchDonePaperData(paperId: string): Promise<PaperData | null> {
  const testTypes: TestType[] = ['PAID', 'FREE'];
  for (const testType of testTypes) {
    try {
      const response = await get<EncryptedResponse | ApiResponse<PaperData> | string>(
        `${API_URLS.PAPER.GET_ENCRYPTED}/${testType}/DONE/${paperId}`
      );

      if (typeof response === 'string') {
        const decrypted = decrypt<PaperData>(response, true);
        return normalizePaperData(decrypted);
      }

      if (response && typeof response === 'object' && 'body' in response && typeof response.body === 'string') {
        const decrypted = decrypt<PaperData>(response.body, true);
        return normalizePaperData(decrypted);
      }

      if (isPaperData(response)) {
        return normalizePaperData(response);
      }
    } catch {
      // Try next test type
    }
  }
  return null;
}

function buildAnalysisFromPaper(data: PaperData): PaperAnalysisData {
  const totalQuestions = data.totalCorrectCount + data.totalInCorrectCount + data.totalSkipedCount ||
    data.totalQuestionCount ||
    data.paper?.totalQuestionCount ||
    0;
  const totalScore = data.totalScore || data.paper?.totalScore || (data.paper?.perQuestionScore || 0) * totalQuestions;
  const obtainedScore = data.totalGetScore || data.paper?.totalGetScore || 0;
  const sections = (data.paper?.pattern?.sections || []).map((section) => {
    const total = (section.correctCount || 0) + (section.inCorrectCount || 0) + (section.skipedCount || 0);
    const maxScore = (section.questionCount || total) * (data.paper?.perQuestionScore || 0);
    return {
      title: section.title,
      totalQuestions: total,
      correct: section.correctCount || 0,
      incorrect: section.inCorrectCount || 0,
      skipped: section.skipedCount || 0,
      score: section.scoreInSection || section.score || 0,
      maxScore,
      timeTaken: section.timeTakenSecond || 0,
    };
  });

  return {
    paperId: data.paperId,
    paperName: data.paperName || data.paper?.paperName || '',
    paperCategory: data.paperCategory || data.paper?.paperCategory || '',
    testType: data.testType || data.paper?.testType || 'PAID',
    totalScore,
    obtainedScore,
    totalQuestions,
    correctCount: data.totalCorrectCount || 0,
    incorrectCount: data.totalInCorrectCount || 0,
    skippedCount: data.totalSkipedCount || 0,
    timeTaken: data.totalTimeTaken || 0,
    percentile: data.percentile,
    sections,
  };
}

// Get paper result/analytics
// Angular API: analytics/v1/test/userPaperStat/{paperId}
export function usePaperResult(paperId: string, enabled = true) {
  return useQuery({
    queryKey: analyticsKeys.paperResult(paperId),
    queryFn: async () => {
      // Demo mode - return mock data
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 300));
        return { ...mockPaperResult, paperId };
      }

      const response = await get<ApiResponse<PaperResultData>>(
        `${API_URLS.ANALYTICS.USER_PAPER_STAT}/${paperId}`
      );
      return response.body || null;
    },
    enabled: enabled && !!paperId,
    staleTime: 2 * 60 * 1000, // 2 minutes
  });
}

export function usePaperAnalysis(paperId: string, enabled = true) {
  return useQuery({
    queryKey: analyticsKeys.paperAnalysis(paperId),
    queryFn: async () => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 300));
        return buildAnalysisFromPaper({
          paperId: mockPaperResult.paperId,
          paperCategory: mockPaperResult.paperCategory as PaperCategory,
          paperSubCategory: 'SSC_CGL_TIER1',
          paperStatus: 'DONE',
          totalTimeTaken: mockPaperResult.timeTaken,
          totalGetScore: mockPaperResult.obtainedScore,
          totalAttemptedQuestionCount: mockPaperResult.correctCount + mockPaperResult.incorrectCount,
          totalCorrectCount: mockPaperResult.correctCount,
          totalInCorrectCount: mockPaperResult.incorrectCount,
          totalSkipedCount: mockPaperResult.skippedCount,
          paper: {
            id: mockPaperResult.paperId,
            paperName: mockPaperResult.paperName,
            paperType: 'SSC_CGL',
            paperSubCategory: 'SSC_CGL_TIER1',
            testType: 'FREE',
            totalTime: 60,
            totalScore: mockPaperResult.totalScore,
            perQuestionScore: 2,
            negativeMarks: 0.5,
            pattern: { sections: [] },
          },
          totalScore: mockPaperResult.totalScore,
          totalTime: 3600,
          totalQuestionCount: mockPaperResult.totalQuestions,
        });
      }

      const donePaper = await fetchDonePaperData(paperId);
      if (!donePaper) return null;
      return buildAnalysisFromPaper(donePaper);
    },
    enabled: enabled && !!paperId,
    staleTime: 2 * 60 * 1000,
  });
}

// Get user overall analytics
// Angular API: analytics/v1/user
export function useUserAnalytics(
  paperCategory: PaperCategory,
  paperId: string,
  enabled = true
) {
  return useQuery({
    queryKey: analyticsKeys.userStats(paperId, paperCategory),
    queryFn: async () => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 300));
        return null;
      }

      const response = await get<UserAnalyticsData>(
        `${API_URLS.ANALYTICS.USER}?paperCategory=${paperCategory}&paperId=${paperId}`
      );
      return response || null;
    },
    enabled: enabled && !!paperCategory && !!paperId,
    staleTime: 5 * 60 * 1000,
  });
}
