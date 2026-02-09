// Subscription hooks for EnsureU

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { get, post } from '@/lib/api/client';
import { API_URLS } from '@/lib/constants/api-urls';
import { useUIStore } from '@/stores/ui-store';
import type { ApiResponse } from '@/types/api';
import type { PaperCategory } from '@/types/paper';
import type {
  SubscriptionItem,
  SubscriptionListResponse,
  SubscribeDto,
  PurchaseSubscriptionDto,
  UserSubscription,
  Pass,
  PurchaseRecord,
  SubscriptionType,
  PaperTypeDto,
  PriceMetadata,
} from '@/types/subscription';

const DEMO_MODE = process.env.NEXT_PUBLIC_DEMO_MODE === 'true';

// Helper to flatten hierarchical PAID subscription response into flat list
function flattenPaidSubscriptions(listOfPaperTypeDto: PaperTypeDto[] | undefined): SubscriptionItem[] {
  if (!listOfPaperTypeDto) return [];

  const subscriptions: SubscriptionItem[] = [];

  for (const paperType of listOfPaperTypeDto) {
    const categories = paperType.listOfCategoryDto || [];
    for (const category of categories) {
      const subCategories = category.listOfSubCategoryDto || [];
      for (const subCat of subCategories) {
        if (subCat.subscriptionDto) {
          // Add paperType and paperCategory context to the subscription
          subscriptions.push({
            ...subCat.subscriptionDto,
            paperType: subCat.subscriptionDto.paperType || paperType.paperType,
            paperCategory: subCat.subscriptionDto.paperCategory || category.paperCategory,
            paperSubCategory: subCat.subscriptionDto.paperSubCategory || subCat.paperSubCategory,
          });
        }
      }
    }
  }

  return subscriptions;
}

// Valid PaperSubCategory enum values (must match backend exactly)
const VALID_PAPER_SUB_CATEGORIES = [
  'SSC_CGL_TIER1', 'SSC_CGL_TIER2',
  'SSC_CPO_TIER1', 'SSC_CPO_TIER2',
  'SSC_CHSL_TIER1', 'SSC_CHSL_TIER2',
  'BANK_PO_PRE', 'BANK_PO_MAIN',
] as const;

// Convert display name to enum value
// e.g., "SSC CGL TIER-1" -> "SSC_CGL_TIER1"
// e.g., "SSC CGL Tier 1" -> "SSC_CGL_TIER1"
export function normalizeSubCategory(displayName: string): string {
  // If already a valid enum value, return as-is
  if (VALID_PAPER_SUB_CATEGORIES.includes(displayName as any)) {
    return displayName;
  }

  // Convert display name to enum format:
  // Replace spaces and hyphens with underscores, uppercase everything
  const normalized = displayName
    .toUpperCase()
    .replace(/[\s-]+/g, '_')  // Replace spaces and hyphens with underscore
    .replace(/TIER_(\d)/g, 'TIER$1');  // Fix TIER_1 -> TIER1

  // Check if normalized value is valid
  if (VALID_PAPER_SUB_CATEGORIES.includes(normalized as any)) {
    return normalized;
  }

  // Try common mappings
  const mappings: Record<string, string> = {
    'SSC_CGL_TIER_1': 'SSC_CGL_TIER1',
    'SSC_CGL_TIER_2': 'SSC_CGL_TIER2',
    'SSC_CPO_TIER_1': 'SSC_CPO_TIER1',
    'SSC_CPO_TIER_2': 'SSC_CPO_TIER2',
    'SSC_CHSL_TIER_1': 'SSC_CHSL_TIER1',
    'SSC_CHSL_TIER_2': 'SSC_CHSL_TIER2',
    'BANK_PO_PRELIMS': 'BANK_PO_PRE',
    'BANK_PO_MAINS': 'BANK_PO_MAIN',
  };

  return mappings[normalized] || normalized;
}

// Query keys
export const subscriptionKeys = {
  all: ['subscriptions'] as const,
  free: () => [...subscriptionKeys.all, 'free'] as const,
  paid: () => [...subscriptionKeys.all, 'paid'] as const,
  user: (paperType: string) => [...subscriptionKeys.all, 'user', paperType] as const,
  passes: () => [...subscriptionKeys.all, 'passes'] as const,
  history: (paperType: string) => [...subscriptionKeys.all, 'history', paperType] as const,
};

// Mock data for demo mode
const mockFreeSubscriptions: SubscriptionItem[] = [
  {
    id: 1,
    paperType: 'SSC',
    paperCategory: 'SSC CGL',
    paperSubCategory: 'SSC_CGL_TIER1',
    testType: 'FREE',
    description: 'Free mock tests for SSC CGL Tier 1 preparation',
    paperInfoList: [
      { id: 'free-1', paperName: 'SSC CGL Mock Test 1' },
      { id: 'free-2', paperName: 'SSC CGL Mock Test 2' },
      { id: 'free-3', paperName: 'SSC CGL Practice Set 1' },
    ],
  },
  {
    id: 2,
    paperType: 'BANK',
    paperCategory: 'Bank PO',
    paperSubCategory: 'BANK_PO_PRE',
    testType: 'FREE',
    description: 'Free mock tests for Bank PO Prelims',
    paperInfoList: [
      { id: 'free-4', paperName: 'Bank PO Prelims Mock 1' },
      { id: 'free-5', paperName: 'Bank PO Prelims Mock 2' },
    ],
  },
];

const mockPaidSubscriptions: SubscriptionItem[] = [
  {
    id: 10,
    paperType: 'SSC',
    paperCategory: 'SSC CGL',
    paperSubCategory: 'SSC_CGL_TIER1',
    testType: 'PAID',
    description: 'Complete SSC CGL Tier 1 preparation with 50+ mock tests',
    paperInfoList: [
      { id: 'paid-1', paperName: 'SSC CGL Premium Test 1' },
      { id: 'paid-2', paperName: 'SSC CGL Premium Test 2' },
      { id: 'paid-3', paperName: 'SSC CGL Premium Test 3' },
      { id: 'paid-4', paperName: 'SSC CGL Premium Test 4' },
      { id: 'paid-5', paperName: 'SSC CGL Premium Test 5' },
    ],
    listOfSubscriptionType: ['MONTHLY', 'QUATERLY', 'HALFYEARLY'],
    mapOfSubTypeVsPrice: {
      MONTHLY: { id: 1, price: 299, pricePerPaper: 29.9, discountedPrice: 199, discountedPricePerPaper: 19.9, discountPercentage: 33 },
      QUATERLY: { id: 2, price: 699, pricePerPaper: 23.3, discountedPrice: 499, discountedPricePerPaper: 16.6, discountPercentage: 29 },
      HALFYEARLY: { id: 3, price: 1299, pricePerPaper: 21.6, discountedPrice: 899, discountedPricePerPaper: 14.9, discountPercentage: 31 },
    },
  },
  {
    id: 11,
    paperType: 'SSC',
    paperCategory: 'SSC CPO',
    paperSubCategory: 'SSC_CPO_TIER1',
    testType: 'PAID',
    description: 'Complete SSC CPO preparation package',
    paperInfoList: [
      { id: 'paid-6', paperName: 'SSC CPO Premium Test 1' },
      { id: 'paid-7', paperName: 'SSC CPO Premium Test 2' },
      { id: 'paid-8', paperName: 'SSC CPO Premium Test 3' },
    ],
    listOfSubscriptionType: ['MONTHLY', 'QUATERLY'],
    mapOfSubTypeVsPrice: {
      MONTHLY: { id: 4, price: 249, pricePerPaper: 24.9, discountedPrice: 179, discountedPricePerPaper: 17.9, discountPercentage: 28 },
      QUATERLY: { id: 5, price: 599, pricePerPaper: 19.9, discountedPrice: 449, discountedPricePerPaper: 14.9, discountPercentage: 25 },
    },
  },
  {
    id: 12,
    paperType: 'BANK',
    paperCategory: 'Bank PO',
    paperSubCategory: 'BANK_PO_PRE',
    testType: 'PAID',
    description: 'Complete Bank PO preparation with 100+ tests',
    paperInfoList: [
      { id: 'paid-9', paperName: 'Bank PO Premium Test 1' },
      { id: 'paid-10', paperName: 'Bank PO Premium Test 2' },
      { id: 'paid-11', paperName: 'Bank PO Premium Test 3' },
      { id: 'paid-12', paperName: 'Bank PO Premium Test 4' },
    ],
    listOfSubscriptionType: ['MONTHLY', 'QUATERLY', 'HALFYEARLY'],
    mapOfSubTypeVsPrice: {
      MONTHLY: { id: 6, price: 399, pricePerPaper: 39.9, discountedPrice: 299, discountedPricePerPaper: 29.9, discountPercentage: 25 },
      QUATERLY: { id: 7, price: 899, pricePerPaper: 29.9, discountedPrice: 699, discountedPricePerPaper: 23.3, discountPercentage: 22 },
      HALFYEARLY: { id: 8, price: 1599, pricePerPaper: 26.6, discountedPrice: 1199, discountedPricePerPaper: 19.9, discountPercentage: 25 },
    },
  },
];

// ============================================
// Fetch Subscriptions (Public - No Auth Required)
// ============================================

// Get FREE subscriptions for landing page
export function useFreeSubscriptions(enabled = true) {
  return useQuery({
    queryKey: subscriptionKeys.free(),
    queryFn: async () => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 300));
        return mockFreeSubscriptions;
      }

      const response = await get<SubscriptionListResponse | ApiResponse<SubscriptionListResponse>>(
        API_URLS.DASHBOARD.FREE_PAPER
      );

      // Handle both response formats:
      // 1. Direct: { listOfFreeSubscription: [...] }
      // 2. Wrapped: { body: { listOfFreeSubscription: [...] } }
      if ('listOfFreeSubscription' in response) {
        return response.listOfFreeSubscription || [];
      }
      if ('body' in response && response.body) {
        return response.body.listOfFreeSubscription || [];
      }
      return [];
    },
    enabled,
    staleTime: 10 * 60 * 1000, // 10 minutes
  });
}

// Get PAID subscriptions for landing page
export function usePaidSubscriptions(enabled = true) {
  return useQuery({
    queryKey: subscriptionKeys.paid(),
    queryFn: async () => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 300));
        return mockPaidSubscriptions;
      }

      const response = await get<SubscriptionListResponse | ApiResponse<SubscriptionListResponse>>(
        API_URLS.DASHBOARD.PAID_PAPER
      );

      // PAID subscriptions use hierarchical structure: listOfPaperTypeDto
      // We need to flatten it into a list of SubscriptionItem
      // Handle both response formats:
      // 1. Direct: { listOfPaperTypeDto: [...] }
      // 2. Wrapped: { body: { listOfPaperTypeDto: [...] } }
      if ('listOfPaperTypeDto' in response) {
        return flattenPaidSubscriptions(response.listOfPaperTypeDto);
      }
      if ('body' in response && response.body) {
        return flattenPaidSubscriptions(response.body.listOfPaperTypeDto);
      }
      return [];
    },
    enabled,
    staleTime: 10 * 60 * 1000,
  });
}

// ============================================
// User Subscription Actions (Auth Required)
// ============================================

// Subscribe to a FREE subscription
export function useSubscribe() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (data: SubscribeDto) => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 500));
        return { success: true };
      }

      // Normalize paperSubCategory to match backend enum format
      const normalizedData: SubscribeDto = {
        ...data,
        paperSubCategory: normalizeSubCategory(data.paperSubCategory),
      };

      return post<ApiResponse<{ success: boolean }>>(
        API_URLS.SUBSCRIPTION.SUBSCRIBE,
        normalizedData
      );
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: subscriptionKeys.all });
      queryClient.invalidateQueries({ queryKey: ['papers'] });
      showAlert('success', 'Successfully subscribed! Papers are now available.');
    },
    onError: (error: Error) => {
      showAlert('error', error.message || 'Failed to subscribe. Please try again.');
    },
  });
}

// Purchase a PAID subscription
export function usePurchaseSubscription() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (data: PurchaseSubscriptionDto) => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 1000));
        return { success: true, transactionId: `TXN-${Date.now()}` };
      }

      return post<ApiResponse<{ success: boolean; transactionId?: string }>>(
        API_URLS.SUBSCRIPTION.PURCHASE,
        data
      );
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: subscriptionKeys.all });
      queryClient.invalidateQueries({ queryKey: ['papers'] });
      showAlert('success', 'Purchase successful! Premium papers are now available.');
    },
    onError: (error: Error) => {
      showAlert('error', error.message || 'Purchase failed. Please try again.');
    },
  });
}

// ============================================
// User Subscription Status (Auth Required)
// ============================================

// Get user's current subscriptions for a specific paper type
export function useUserSubscriptions(paperType: string, enabled = true) {
  return useQuery({
    queryKey: subscriptionKeys.user(paperType),
    queryFn: async () => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 300));
        return [] as UserSubscription[];
      }

      const response = await get<ApiResponse<UserSubscription[]>>(
        `${API_URLS.SUBSCRIPTION.GET_USER_SUBSCRIPTION}/${paperType}`
      );

      if (Array.isArray(response)) {
        return response as UserSubscription[];
      }
      return response.body || [];
    },
    enabled: enabled && !!paperType,
    staleTime: 2 * 60 * 1000,
  });
}

// Get ALL user's subscriptions across all paper types (SSC and BANK)
export function useAllUserSubscriptions(enabled = true) {
  return useQuery({
    queryKey: [...subscriptionKeys.all, 'user', 'all'] as const,
    queryFn: async () => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 300));
        return [] as UserSubscription[];
      }

      // Fetch from both SSC and BANK paper types
      const [sscResponse, bankResponse] = await Promise.all([
        get<ApiResponse<UserSubscription[]>>(
          `${API_URLS.SUBSCRIPTION.GET_ALL_USER_SUBSCRIPTION}/SSC`
        ).catch(() => ({ body: [] })),
        get<ApiResponse<UserSubscription[]>>(
          `${API_URLS.SUBSCRIPTION.GET_ALL_USER_SUBSCRIPTION}/BANK`
        ).catch(() => ({ body: [] })),
      ]);

      const sscSubs = Array.isArray(sscResponse) ? sscResponse : (sscResponse.body || []);
      const bankSubs = Array.isArray(bankResponse) ? bankResponse : (bankResponse.body || []);

      return [...sscSubs, ...bankSubs] as UserSubscription[];
    },
    enabled,
    staleTime: 2 * 60 * 1000,
  });
}

// Get ALL user's subscription history across all paper types
export function useAllSubscriptionHistory(enabled = true) {
  return useQuery({
    queryKey: [...subscriptionKeys.all, 'history', 'all'] as const,
    queryFn: async () => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 300));
        return [] as PurchaseRecord[];
      }

      // Fetch from both SSC and BANK paper types
      const [sscResponse, bankResponse] = await Promise.all([
        get<ApiResponse<PurchaseRecord[]>>(
          `${API_URLS.SUBSCRIPTION.GET_SUBSCRIPTIONS}/SSC`
        ).catch(() => ({ body: [] })),
        get<ApiResponse<PurchaseRecord[]>>(
          `${API_URLS.SUBSCRIPTION.GET_SUBSCRIPTIONS}/BANK`
        ).catch(() => ({ body: [] })),
      ]);

      const sscHistory = Array.isArray(sscResponse) ? sscResponse : (sscResponse.body || []);
      const bankHistory = Array.isArray(bankResponse) ? bankResponse : (bankResponse.body || []);

      return [...sscHistory, ...bankHistory] as PurchaseRecord[];
    },
    enabled,
    staleTime: 2 * 60 * 1000,
  });
}

// Get user's subscription/purchase history
export function useSubscriptionHistory(paperType: string, enabled = true) {
  return useQuery({
    queryKey: subscriptionKeys.history(paperType),
    queryFn: async () => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 300));
        return [] as PurchaseRecord[];
      }

      const response = await get<ApiResponse<PurchaseRecord[]>>(
        `${API_URLS.SUBSCRIPTION.GET_SUBSCRIPTIONS}/${paperType}`
      );

      if (Array.isArray(response)) {
        return response as PurchaseRecord[];
      }
      return response.body || [];
    },
    enabled: enabled && !!paperType,
    staleTime: 2 * 60 * 1000,
  });
}

// Get all available passes
export function useAllPasses(enabled = true) {
  return useQuery({
    queryKey: subscriptionKeys.passes(),
    queryFn: async () => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 300));
        return [] as Pass[];
      }

      const response = await get<ApiResponse<Pass[]>>(
        API_URLS.SUBSCRIPTION.GET_ALL_PASS
      );

      if (Array.isArray(response)) {
        return response as Pass[];
      }
      return response.body || [];
    },
    enabled,
    staleTime: 5 * 60 * 1000,
  });
}

// ============================================
// Helper Functions
// ============================================

// Calculate validity timestamp based on subscription type
export function calculateValidity(subscriptionType: SubscriptionType): number {
  const validityDurations: Record<SubscriptionType, number> = {
    DAY: 24 * 60 * 60 * 1000,
    MONTHLY: 30 * 24 * 60 * 60 * 1000,
    QUATERLY: 90 * 24 * 60 * 60 * 1000,
    HALFYEARLY: 180 * 24 * 60 * 60 * 1000,
    YEARLY: 365 * 24 * 60 * 60 * 1000,
  };
  return Date.now() + (validityDurations[subscriptionType] || validityDurations.MONTHLY);
}

// Format price for display
export function formatPrice(price: number): string {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(price);
}

// Check if subscription is expired
export function isSubscriptionExpired(validity: number | undefined | null): boolean {
  if (!validity || validity <= 0) return true;
  // Handle both seconds and milliseconds timestamps
  const adjustedValidity = validity < 946684800000 ? validity * 1000 : validity;
  return Date.now() > adjustedValidity;
}

// Get remaining days in subscription
export function getRemainingDays(validity: number | undefined | null): number {
  if (!validity || validity <= 0) return 0;
  // Handle both seconds and milliseconds timestamps
  const adjustedValidity = validity < 946684800000 ? validity * 1000 : validity;
  const remaining = adjustedValidity - Date.now();
  if (remaining <= 0) return 0;
  return Math.ceil(remaining / (24 * 60 * 60 * 1000));
}
