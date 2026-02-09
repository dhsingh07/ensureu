'use client';

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '@/lib/api/client';
import {
  SubscriptionAdminDto,
  SubscriptionCreateDto,
  SubscriptionUpdateDto,
  SubscriptionExtendDto,
  SubscriptionStatsDto,
  SubscriptionListParams,
  AvailablePapersParams,
  PaperSelectionDto,
  PageResponse,
  SubscriptionState,
  TestType,
} from '@/types/subscription-admin';
import { PaperSubCategory } from '@/types/paper';

interface ApiResponse<T> {
  status: number;
  body: T;
  message: string;
}

// ==========================================
// Query Keys
// ==========================================

export const subscriptionAdminKeys = {
  all: ['subscription-admin'] as const,
  lists: () => [...subscriptionAdminKeys.all, 'list'] as const,
  list: (params: SubscriptionListParams) => [...subscriptionAdminKeys.lists(), params] as const,
  details: () => [...subscriptionAdminKeys.all, 'detail'] as const,
  detail: (id: string) => [...subscriptionAdminKeys.details(), id] as const,
  stats: () => [...subscriptionAdminKeys.all, 'stats'] as const,
  expiring: (days: number) => [...subscriptionAdminKeys.all, 'expiring', days] as const,
  availablePapers: (params: AvailablePapersParams) => [...subscriptionAdminKeys.all, 'available-papers', params] as const,
  subscriptionPapers: (id: string) => [...subscriptionAdminKeys.all, 'subscription-papers', id] as const,
};

// ==========================================
// List & Get Hooks
// ==========================================

/**
 * List subscriptions with filters
 */
export function useSubscriptionList(params: SubscriptionListParams = {}) {
  return useQuery({
    queryKey: subscriptionAdminKeys.list(params),
    queryFn: async () => {
      const searchParams = new URLSearchParams();

      if (params.paperType) searchParams.append('paperType', params.paperType);
      if (params.paperCategory) searchParams.append('paperCategory', params.paperCategory);
      if (params.paperSubCategory) searchParams.append('paperSubCategory', params.paperSubCategory);
      if (params.testType) searchParams.append('testType', params.testType);
      if (params.state) searchParams.append('state', params.state);
      if (params.search) searchParams.append('search', params.search);
      if (params.page !== undefined) searchParams.append('page', params.page.toString());
      if (params.size !== undefined) searchParams.append('size', params.size.toString());
      if (params.sortBy) searchParams.append('sortBy', params.sortBy);
      if (params.sortDir) searchParams.append('sortDir', params.sortDir);

      const response = await apiClient.get<ApiResponse<PageResponse<SubscriptionAdminDto>>>(
        `/admin/subscription-management/list?${searchParams.toString()}`
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message);
      }

      return response.data.body;
    },
  });
}

/**
 * Get subscription by ID
 */
export function useSubscription(id: string | undefined) {
  return useQuery({
    queryKey: subscriptionAdminKeys.detail(id || ''),
    queryFn: async () => {
      if (!id) throw new Error('Subscription ID required');

      const response = await apiClient.get<ApiResponse<SubscriptionAdminDto>>(
        `/admin/subscription-management/${id}`
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message);
      }

      return response.data.body;
    },
    enabled: !!id,
  });
}

/**
 * Get subscription statistics
 */
export function useSubscriptionStats() {
  return useQuery({
    queryKey: subscriptionAdminKeys.stats(),
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<SubscriptionStatsDto>>(
        '/admin/subscription-management/stats'
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message);
      }

      return response.data.body;
    },
  });
}

/**
 * Get expiring subscriptions
 */
export function useExpiringSubscriptions(days: number = 30) {
  return useQuery({
    queryKey: subscriptionAdminKeys.expiring(days),
    queryFn: async () => {
      const response = await apiClient.get<ApiResponse<SubscriptionAdminDto[]>>(
        `/admin/subscription-management/expiring?days=${days}`
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message);
      }

      return response.data.body;
    },
  });
}

// ==========================================
// Create & Update Mutations
// ==========================================

/**
 * Create subscription
 */
export function useCreateSubscription() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (dto: SubscriptionCreateDto) => {
      const response = await apiClient.post<ApiResponse<SubscriptionAdminDto>>(
        '/admin/subscription-management/create',
        dto
      );

      if (response.data.status !== 201 && response.data.status !== 200) {
        throw new Error(response.data.message);
      }

      return response.data.body;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.lists() });
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.stats() });
    },
  });
}

/**
 * Update subscription
 */
export function useUpdateSubscription() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id, dto }: { id: string; dto: SubscriptionUpdateDto }) => {
      const response = await apiClient.put<ApiResponse<SubscriptionAdminDto>>(
        `/admin/subscription-management/${id}`,
        dto
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message);
      }

      return response.data.body;
    },
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.lists() });
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.detail(data.id) });
    },
  });
}

/**
 * Delete subscription
 */
export function useDeleteSubscription() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: string) => {
      const response = await apiClient.delete<ApiResponse<boolean>>(
        `/admin/subscription-management/${id}`
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message);
      }

      return response.data.body;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.lists() });
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.stats() });
    },
  });
}

// ==========================================
// State Management Mutations
// ==========================================

/**
 * Activate subscription
 */
export function useActivateSubscription() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (id: string) => {
      const response = await apiClient.post<ApiResponse<SubscriptionAdminDto>>(
        `/admin/subscription-management/${id}/activate`
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message);
      }

      return response.data.body;
    },
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.lists() });
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.detail(data.id) });
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.stats() });
    },
  });
}

/**
 * Deactivate subscription
 */
export function useDeactivateSubscription() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id, force = false }: { id: string; force?: boolean }) => {
      const response = await apiClient.post<ApiResponse<SubscriptionAdminDto>>(
        `/admin/subscription-management/${id}/deactivate?force=${force}`
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message);
      }

      return response.data.body;
    },
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.lists() });
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.detail(data.id) });
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.stats() });
    },
  });
}

// ==========================================
// Validity Management Mutations
// ==========================================

/**
 * Extend subscription validity
 */
export function useExtendValidity() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id, dto }: { id: string; dto: SubscriptionExtendDto }) => {
      const response = await apiClient.patch<ApiResponse<SubscriptionAdminDto>>(
        `/admin/subscription-management/${id}/extend`,
        dto
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message);
      }

      return response.data.body;
    },
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.lists() });
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.detail(data.id) });
    },
  });
}

/**
 * Bulk extend subscriptions
 */
export function useBulkExtendValidity() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ ids, dto }: { ids: string[]; dto: SubscriptionExtendDto }) => {
      const response = await apiClient.post<ApiResponse<SubscriptionAdminDto[]>>(
        `/admin/subscription-management/bulk-extend?ids=${ids.join(',')}`,
        dto
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message);
      }

      return response.data.body;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.lists() });
      queryClient.invalidateQueries({ queryKey: subscriptionAdminKeys.stats() });
    },
  });
}

// ==========================================
// Paper Selection Hooks
// ==========================================

/**
 * Get available papers for subscription
 */
export function useAvailablePapers(params: AvailablePapersParams | null) {
  return useQuery({
    queryKey: subscriptionAdminKeys.availablePapers(params || {} as AvailablePapersParams),
    queryFn: async () => {
      if (!params) throw new Error('Parameters required');

      const searchParams = new URLSearchParams();
      searchParams.append('testType', params.testType);
      searchParams.append('paperSubCategory', params.paperSubCategory);
      if (params.excludeSubscriptionId) searchParams.append('excludeSubscriptionId', params.excludeSubscriptionId);
      if (params.search) searchParams.append('search', params.search);
      if (params.page !== undefined) searchParams.append('page', params.page.toString());
      if (params.size !== undefined) searchParams.append('size', params.size.toString());

      const response = await apiClient.get<ApiResponse<PageResponse<PaperSelectionDto>>>(
        `/admin/subscription-management/available-papers?${searchParams.toString()}`
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message);
      }

      return response.data.body;
    },
    enabled: !!params?.testType && !!params?.paperSubCategory,
  });
}

/**
 * Get papers in a subscription
 */
export function useSubscriptionPapers(subscriptionId: string | undefined) {
  return useQuery({
    queryKey: subscriptionAdminKeys.subscriptionPapers(subscriptionId || ''),
    queryFn: async () => {
      if (!subscriptionId) throw new Error('Subscription ID required');

      const response = await apiClient.get<ApiResponse<PaperSelectionDto[]>>(
        `/admin/subscription-management/${subscriptionId}/papers`
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message);
      }

      return response.data.body;
    },
    enabled: !!subscriptionId,
  });
}

/**
 * Check paper availability
 */
export function useCheckPapersAvailability() {
  return useMutation({
    mutationFn: async ({
      paperIds,
      testType,
      excludeSubscriptionId,
    }: {
      paperIds: string[];
      testType: TestType;
      excludeSubscriptionId?: string;
    }) => {
      const searchParams = new URLSearchParams();
      searchParams.append('testType', testType);
      if (excludeSubscriptionId) searchParams.append('excludeSubscriptionId', excludeSubscriptionId);

      const response = await apiClient.post<ApiResponse<PaperSelectionDto[]>>(
        `/admin/subscription-management/check-papers?${searchParams.toString()}`,
        paperIds
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message);
      }

      return response.data.body;
    },
  });
}
