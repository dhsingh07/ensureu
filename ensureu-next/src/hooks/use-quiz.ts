// Quiz hooks for daily quiz feature

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { get, post } from '@/lib/api/client';
import { API_URLS } from '@/lib/constants/api-urls';
import { decrypt, encrypt } from '@/lib/utils/encryption';
import type { PaperCategory, PaperType, PaperStatus } from '@/types/paper';
import type { Quiz, QuizListItem, QuizData, QuizCreatePayload, QuizInfo } from '@/types/quiz';

const QUIZ_INFO_KEY = 'quizInfo';

// Query keys
export const quizKeys = {
  all: ['quiz'] as const,
  lists: () => [...quizKeys.all, 'list'] as const,
  list: (paperType: PaperType, paperCategory: PaperCategory) =>
    [...quizKeys.lists(), paperType, paperCategory] as const,
  completed: (paperType: PaperType, paperCategory?: PaperCategory) =>
    [...quizKeys.all, 'completed', paperType, paperCategory] as const,
  detail: (quizId: string) => [...quizKeys.all, 'detail', quizId] as const,
  collection: () => [...quizKeys.all, 'collection'] as const,
  collectionList: () => [...quizKeys.collection(), 'list'] as const,
};

// Session storage helpers
export function saveQuizInfo(info: QuizInfo): void {
  if (typeof window !== 'undefined') {
    sessionStorage.setItem(QUIZ_INFO_KEY, JSON.stringify(info));
  }
}

export function getQuizInfo(): QuizInfo | null {
  if (typeof window === 'undefined') return null;
  const stored = sessionStorage.getItem(QUIZ_INFO_KEY);
  if (!stored) return null;
  try {
    return JSON.parse(stored) as QuizInfo;
  } catch {
    return null;
  }
}

export function clearQuizInfo(): void {
  if (typeof window !== 'undefined') {
    sessionStorage.removeItem(QUIZ_INFO_KEY);
  }
}

// Fetch available quizzes for a category
export function useQuizList(paperType: PaperType, paperCategory: PaperCategory) {
  return useQuery({
    queryKey: quizKeys.list(paperType, paperCategory),
    queryFn: async () => {
      const response = await get<QuizListItem[]>(
        `${API_URLS.QUIZ.LIST}/${paperType}?paperCategory=${paperCategory}`
      );
      return response || [];
    },
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
}

// Fetch completed quizzes
export function useCompletedQuizzes(
  paperType: PaperType,
  paperCategory?: PaperCategory
) {
  return useQuery({
    queryKey: quizKeys.completed(paperType, paperCategory),
    queryFn: async () => {
      let url = `${API_URLS.QUIZ.COMPLETED}/${paperType}?paperStatus=DONE`;
      if (paperCategory) {
        url += `&paperCategory=${paperCategory}`;
      }
      const response = await get<QuizListItem[]>(url);
      return response || [];
    },
    staleTime: 5 * 60 * 1000,
  });
}

// Fetch quiz data for taking the quiz (encrypted)
export function useQuizData(
  quizId: string,
  paperStatus: PaperStatus = 'START',
  enabled = true
) {
  return useQuery({
    queryKey: quizKeys.detail(quizId),
    queryFn: async () => {
      const response = await get<{ body: string; status: number; message: string }>(
        `${API_URLS.QUIZ.GET_ENCRYPTED}/QUIZ/${paperStatus}/${quizId}`
      );

      if (!response?.body) {
        throw new Error('No quiz data received');
      }

      // Decrypt the response (with date enrichment)
      const decrypted = decrypt<QuizData>(response.body, true);
      return decrypted;
    },
    enabled: enabled && !!quizId,
    staleTime: 0, // Always refetch quiz data
    gcTime: 0,
  });
}

// Save quiz progress/submission
export function useSaveQuiz() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: {
      quizId: string;
      quizData: QuizData;
      isDone?: boolean;
    }) => {
      const { quizId, quizData, isDone } = data;

      // Prepare submission data
      const submissionData = {
        ...quizData,
        paperStatus: isDone ? 'DONE' : 'INPROGRESS',
        testType: 'QUIZ',
      };

      // Encrypt the data (with date enrichment)
      const encrypted = encrypt(submissionData, true);

      await post(API_URLS.QUIZ.SAVE_ENCRYPTED, { body: encrypted });

      return { success: true };
    },
    onSuccess: (_, variables) => {
      // Invalidate quiz queries
      queryClient.invalidateQueries({ queryKey: quizKeys.all });
    },
  });
}

// Admin: Fetch quiz collection list
export function useQuizCollectionList(page = 0, size = 20) {
  return useQuery({
    queryKey: [...quizKeys.collectionList(), page, size],
    queryFn: async () => {
      const response = await get<{
        content: Quiz[];
        totalElements: number;
        totalPages: number;
        number: number;
        size: number;
      }>(`${API_URLS.QUIZ.COLLECTION_LIST}?page=${page}&size=${size}`);
      return response;
    },
    staleTime: 2 * 60 * 1000, // 2 minutes
  });
}

// Admin: Create quiz
export function useCreateQuiz() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (payload: QuizCreatePayload) => {
      await post(API_URLS.QUIZ.COLLECTION_CREATE, {
        ...payload,
        testType: 'QUIZ',
      });
      return { success: true };
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: quizKeys.collectionList() });
    },
  });
}

// Admin: Get quiz by ID
export function useQuizById(quizId: string, enabled = true) {
  return useQuery({
    queryKey: [...quizKeys.collection(), quizId],
    queryFn: async () => {
      const response = await get<Quiz>(
        `${API_URLS.QUIZ.COLLECTION_GET_BY_ID}/${quizId}`
      );
      return response;
    },
    enabled: enabled && !!quizId,
  });
}

// Calculate quiz score
export function calculateQuizScore(
  quizData: QuizData
): {
  totalCorrect: number;
  totalIncorrect: number;
  totalSkipped: number;
  totalAttempted: number;
  score: number;
  maxScore: number;
  percentage: number;
} {
  let totalCorrect = 0;
  let totalIncorrect = 0;
  let totalSkipped = 0;

  quizData.pattern.sections.forEach((section) => {
    section.subSections.forEach((subSection) => {
      subSection.questions.forEach((question) => {
        const { co, so } = question.problem;
        if (!so) {
          totalSkipped++;
        } else if (so === co) {
          totalCorrect++;
        } else {
          totalIncorrect++;
        }
      });
    });
  });

  const totalAttempted = totalCorrect + totalIncorrect;
  const score =
    totalCorrect * quizData.perQuestionScore -
    totalIncorrect * quizData.negativeMarks;
  const maxScore = quizData.totalScore;
  const percentage = maxScore > 0 ? Math.round((score / maxScore) * 100) : 0;

  return {
    totalCorrect,
    totalIncorrect,
    totalSkipped,
    totalAttempted,
    score: Math.max(0, score), // Don't go negative
    maxScore,
    percentage: Math.max(0, percentage),
  };
}

// Format time for display
export function formatQuizTime(seconds: number): string {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins}:${secs.toString().padStart(2, '0')}`;
}
