// Admin hooks - migrated from Angular admin.service.ts

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { get, post, put, del } from '@/lib/api/client';
import { API_URLS } from '@/lib/constants/api-urls';
import { useUIStore } from '@/stores/ui-store';
import type {
  DashboardStatistics,
  FeatureConfig,
  UserManagementItem,
  AdminPaperListItem,
} from '@/types/admin';
import type { PaperCategory, TestType } from '@/types/paper';

// Query keys factory
export const adminKeys = {
  all: ['admin'] as const,
  dashboard: () => [...adminKeys.all, 'dashboard'] as const,
  papers: () => [...adminKeys.all, 'papers'] as const,
  paperList: (category: PaperCategory, freePaid: TestType) =>
    [...adminKeys.papers(), category, freePaid] as const,
  users: () => [...adminKeys.all, 'users'] as const,
  userList: (page: number, size: number) =>
    [...adminKeys.users(), 'list', page, size] as const,
  userSearch: (query: string) => [...adminKeys.users(), 'search', query] as const,
  featureConfig: () => [...adminKeys.all, 'featureConfig'] as const,
};

// Dashboard statistics
export function useDashboardStats() {
  return useQuery({
    queryKey: adminKeys.dashboard(),
    queryFn: async () => {
      const response = await get<DashboardStatistics>(
        API_URLS.ADMIN.STATISTICS
      );
      return response;
    },
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
}

// Helper to get paperType (SSC/BANK) from paperCategory (SSC_CGL, BANK_PO, etc.)
function getPaperTypeFromCategory(category: PaperCategory): 'SSC' | 'BANK' {
  if (category.startsWith('SSC')) return 'SSC';
  if (category.startsWith('BANK')) return 'BANK';
  return 'SSC';
}

// Normalize paper response from backend
function normalizePaper(paper: any): AdminPaperListItem {
  return {
    ...paper,
    paperId: paper.id || paper.paperId, // Backend uses 'id'
    totalQuestions: paper.totalQuestionCount || paper.totalQuestions,
    status: paper.paperStateStatus || paper.status || 'DRAFT',
  };
}

// Paper list for admin (requires pagination params)
export function useAdminPaperList(
  category: PaperCategory,
  freePaid: TestType,
  page = 0,
  size = 50,
  enabled = true
) {
  // Backend expects paperType (SSC/BANK), not paperCategory (SSC_CGL/BANK_PO)
  const paperType = getPaperTypeFromCategory(category);

  return useQuery({
    queryKey: adminKeys.paperList(category, freePaid),
    queryFn: async () => {
      const response = await get<{ content: any[]; totalElements: number } | any[]>(
        `${API_URLS.ADMIN.PAPER_LIST}/${paperType}/${freePaid}?page=${page}&size=${size}`
      );

      let papers: any[] = [];

      // Handle both paginated and array responses
      if (response && 'content' in response) {
        papers = response.content || [];
      } else {
        papers = response || [];
      }

      // Normalize papers and filter by category
      return papers
        .map(normalizePaper)
        .filter(p => {
          // Filter by specific category if provided
          const matchesCategory = p.paperCategory === category ||
                                  (p.paperCategory as string)?.startsWith(category.split('_')[0]);
          return matchesCategory;
        });
    },
    enabled,
    staleTime: 2 * 60 * 1000,
  });
}

// User management - list users
export function useUserList(page = 0, size = 20) {
  return useQuery({
    queryKey: adminKeys.userList(page, size),
    queryFn: async () => {
      const response = await get<{
        content: UserManagementItem[];
        totalElements: number;
        totalPages: number;
      }>(`${API_URLS.ADMIN.USER_LIST}?page=${page}&size=${size}`);
      return response;
    },
    staleTime: 60 * 1000,
  });
}

// User management - search users
export function useUserSearch(query: string, enabled = true) {
  return useQuery({
    queryKey: adminKeys.userSearch(query),
    queryFn: async () => {
      const response = await get<UserManagementItem[]>(
        `${API_URLS.ADMIN.USER_SEARCH}?query=${encodeURIComponent(query)}`
      );
      return response || [];
    },
    enabled: enabled && query.length >= 2,
    staleTime: 30 * 1000,
  });
}

// User management - assign role
export function useAssignRole() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async ({
      userId,
      role,
    }: {
      userId: string;
      role: string;
    }) => {
      return put<void>(API_URLS.ADMIN.USER_ASSIGN_ROLE, {
        userId,
        role,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: adminKeys.users() });
      showAlert('success', 'Role assigned successfully');
    },
    onError: () => {
      showAlert('error', 'Failed to assign role');
    },
  });
}

// Feature config response type from API
interface FeatureConfigResponse {
  id: string;
  features: Record<string, boolean>;
  updatedDate?: number;
  updatedBy?: string | null;
}

// Store the full config for updates
let cachedFeatureConfig: FeatureConfigResponse | null = null;

// Feature config - fetch (converts API format to array for UI)
export function useFeatureConfig() {
  return useQuery({
    queryKey: adminKeys.featureConfig(),
    queryFn: async () => {
      const response = await get<FeatureConfigResponse>(
        API_URLS.ADMIN.FEATURE_CONFIG_FETCH
      );
      // Cache for updates
      cachedFeatureConfig = response;
      // Convert features map to array format for UI
      if (response && response.features) {
        return Object.entries(response.features).map(([key, value]) => ({
          id: key,
          featureName: key, // Keep original camelCase key as featureName
          enabled: value,
        })) as FeatureConfig[];
      }
      return [];
    },
    staleTime: 5 * 60 * 1000,
  });
}

// Feature config - update
export function useUpdateFeatureConfig() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (config: FeatureConfig) => {
      if (!cachedFeatureConfig) {
        // Fetch if not cached
        cachedFeatureConfig = await get<FeatureConfigResponse>(
          API_URLS.ADMIN.FEATURE_CONFIG_FETCH
        );
      }
      // Update the specific feature using the original key (id)
      const updatedFeatures = { ...cachedFeatureConfig.features };
      updatedFeatures[config.id] = config.enabled;

      return put<void>(API_URLS.ADMIN.FEATURE_CONFIG_UPDATE, {
        id: cachedFeatureConfig.id,
        features: updatedFeatures,
      });
    },
    onSuccess: () => {
      cachedFeatureConfig = null; // Clear cache to force refresh
      queryClient.invalidateQueries({ queryKey: adminKeys.featureConfig() });
      showAlert('success', 'Feature config updated');
    },
    onError: () => {
      showAlert('error', 'Failed to update feature config');
    },
  });
}

// Get paper by ID
export function useAdminPaperById(paperId: string, testType: string = 'FREE', enabled = true) {
  return useQuery({
    queryKey: [...adminKeys.papers(), 'detail', testType, paperId],
    queryFn: async () => {
      // Backend expects: /admin/paper/getbyid/{testType}/{id}
      const response = await get<AdminPaperListItem>(
        `${API_URLS.ADMIN.PAPER_GET_BY_ID}/${testType}/${paperId}`
      );
      return response;
    },
    enabled: enabled && !!paperId && paperId !== 'new',
    staleTime: 2 * 60 * 1000,
  });
}

// Paper CRUD operations
export function useCreatePaper() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (paperData: Partial<AdminPaperListItem>) => {
      return post<AdminPaperListItem>(API_URLS.ADMIN.PAPER_SAVE, paperData);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: adminKeys.papers() });
      showAlert('success', 'Paper created successfully');
    },
    onError: () => {
      showAlert('error', 'Failed to create paper');
    },
  });
}

export function useUpdatePaper() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (paperData: Partial<AdminPaperListItem>) => {
      // Backend uses POST for both create and update
      return post<AdminPaperListItem>(API_URLS.ADMIN.PAPER_SAVE, paperData);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: adminKeys.papers() });
      showAlert('success', 'Paper updated successfully');
    },
    onError: () => {
      showAlert('error', 'Failed to update paper');
    },
  });
}

export function useUpdatePaperStatus() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      paperId,
      status,
      testType = 'FREE',
      validityStartDate,
      validityEndDate,
    }: {
      paperId: string;
      status: string;
      testType?: string;
      validityStartDate?: number;
      validityEndDate?: number;
    }) => {
      // Build query parameters
      let url = `${API_URLS.ADMIN.PAPER_UPDATE_STATE}/${testType}?id=${paperId}&paperState=${status}`;

      if (validityStartDate) {
        url += `&validityStartDate=${validityStartDate}`;
      }
      if (validityEndDate) {
        url += `&validityEndDate=${validityEndDate}`;
      }

      return put<void>(url, {});
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: adminKeys.papers() });
    },
  });
}

// Delete paper (SUPERADMIN only)
export function useDeletePaper() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async ({
      paperId,
      testType = 'FREE',
    }: {
      paperId: string;
      testType?: string;
    }) => {
      return del<{ body: boolean; status: number; message: string }>(
        `${API_URLS.ADMIN.PAPER_DELETE}/${testType}/${paperId}`
      );
    },
    onSuccess: (response) => {
      queryClient.invalidateQueries({ queryKey: adminKeys.papers() });
      showAlert('success', response?.message || 'Paper deleted successfully');
    },
    onError: (error: any) => {
      const message = error?.response?.data?.message || 'Failed to delete paper';
      showAlert('error', message);
    },
  });
}
