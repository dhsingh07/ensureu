'use client';

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '@/lib/api/client';
import { API_URLS } from '@/lib/constants/api-urls';
import {
  QuestionBankItem,
  QuestionBankCreatePayload,
  QuestionBankStats,
  QuestionBankListParams,
} from '@/types/question-bank';

interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

// List questions with filters
export function useQuestionBankList(params: QuestionBankListParams) {
  return useQuery({
    queryKey: ['question-bank', 'list', params],
    queryFn: async () => {
      const queryParams = new URLSearchParams();
      if (params.paperType) queryParams.append('paperType', params.paperType);
      if (params.paperCategory) queryParams.append('paperCategory', params.paperCategory);
      if (params.paperSubCategory) queryParams.append('paperSubCategory', params.paperSubCategory);
      if (params.subject) queryParams.append('subject', params.subject);
      if (params.topic) queryParams.append('topic', params.topic);
      if (params.difficultyLevel) queryParams.append('difficultyLevel', params.difficultyLevel);
      if (params.status) queryParams.append('status', params.status);
      if (params.createdBy) queryParams.append('createdBy', params.createdBy);
      queryParams.append('page', String(params.page || 0));
      queryParams.append('size', String(params.size || 20));

      const response = await apiClient.get<PageResponse<QuestionBankItem>>(
        `${API_URLS.ADMIN.QUESTION_BANK_LIST}?${queryParams.toString()}`
      );
      return response.data;
    },
  });
}

// Get my questions
export function useMyQuestions(status?: string, page = 0, size = 20) {
  return useQuery({
    queryKey: ['question-bank', 'my-questions', status, page, size],
    queryFn: async () => {
      const queryParams = new URLSearchParams();
      if (status) queryParams.append('status', status);
      queryParams.append('page', String(page));
      queryParams.append('size', String(size));

      const response = await apiClient.get<PageResponse<QuestionBankItem>>(
        `${API_URLS.ADMIN.QUESTION_BANK_MY_QUESTIONS}?${queryParams.toString()}`
      );
      return response.data;
    },
  });
}

// Get question by ID
export function useQuestionBankItem(id: string | undefined) {
  return useQuery({
    queryKey: ['question-bank', 'item', id],
    queryFn: async () => {
      const response = await apiClient.get<QuestionBankItem>(
        `${API_URLS.ADMIN.QUESTION_BANK_GET}/${id}`
      );
      return response.data;
    },
    enabled: !!id,
  });
}

// Get pending approvals
export function usePendingApprovals(page = 0, size = 20) {
  return useQuery({
    queryKey: ['question-bank', 'pending', page, size],
    queryFn: async () => {
      const response = await apiClient.get<PageResponse<QuestionBankItem>>(
        `${API_URLS.ADMIN.QUESTION_BANK_PENDING}?page=${page}&size=${size}`
      );
      return response.data;
    },
  });
}

// Get statistics
export function useQuestionBankStats() {
  return useQuery({
    queryKey: ['question-bank', 'stats'],
    queryFn: async () => {
      const response = await apiClient.get<QuestionBankStats>(
        API_URLS.ADMIN.QUESTION_BANK_STATS
      );
      return response.data;
    },
  });
}

// Search questions
export function useQuestionBankSearch(query: string, page = 0, size = 20) {
  return useQuery({
    queryKey: ['question-bank', 'search', query, page, size],
    queryFn: async () => {
      const response = await apiClient.get<PageResponse<QuestionBankItem>>(
        `${API_URLS.ADMIN.QUESTION_BANK_SEARCH}?q=${encodeURIComponent(query)}&page=${page}&size=${size}`
      );
      return response.data;
    },
    enabled: query.length > 0,
  });
}

// Create question
export function useCreateQuestion() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (payload: QuestionBankCreatePayload) => {
      const response = await apiClient.post<QuestionBankItem>(
        API_URLS.ADMIN.QUESTION_BANK_CREATE,
        payload
      );
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['question-bank'] });
    },
  });
}

// Update question
export function useUpdateQuestion() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id, payload }: { id: string; payload: QuestionBankCreatePayload }) => {
      const response = await apiClient.put<QuestionBankItem>(
        `${API_URLS.ADMIN.QUESTION_BANK_UPDATE}/${id}`,
        payload
      );
      return response.data;
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['question-bank'] });
      queryClient.invalidateQueries({ queryKey: ['question-bank', 'item', variables.id] });
    },
  });
}

// Delete question
export function useDeleteQuestion() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: string) => {
      await apiClient.delete(`${API_URLS.ADMIN.QUESTION_BANK_DELETE}/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['question-bank'] });
    },
  });
}

// Submit for review
export function useSubmitForReview() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: string) => {
      const response = await apiClient.put<QuestionBankItem>(
        `${API_URLS.ADMIN.QUESTION_BANK_SUBMIT_FOR_REVIEW}/${id}`
      );
      return response.data;
    },
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: ['question-bank'] });
      queryClient.invalidateQueries({ queryKey: ['question-bank', 'item', id] });
    },
  });
}

// Approve question
export function useApproveQuestion() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: string) => {
      const response = await apiClient.put<QuestionBankItem>(
        `${API_URLS.ADMIN.QUESTION_BANK_APPROVE}/${id}`
      );
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['question-bank'] });
    },
  });
}

// Reject question
export function useRejectQuestion() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id, reason }: { id: string; reason: string }) => {
      const response = await apiClient.put<QuestionBankItem>(
        `${API_URLS.ADMIN.QUESTION_BANK_REJECT}/${id}?reason=${encodeURIComponent(reason)}`
      );
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['question-bank'] });
    },
  });
}
