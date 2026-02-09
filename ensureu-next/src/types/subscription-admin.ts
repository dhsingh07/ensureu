// Types for Super Admin Subscription Management

import { PaperType, PaperCategory, PaperSubCategory } from './paper';

// ==========================================
// Enums and Constants
// ==========================================

export type SubscriptionState = 'DRAFT' | 'ACTIVE';

export type TestType = 'FREE' | 'PAID';

export type SubscriptionType = 'DAY' | 'MONTHLY' | 'QUATERLY' | 'HALFYEARLY' | 'YEARLY';

export const SUBSCRIPTION_TYPE_LABELS: Record<SubscriptionType, string> = {
  DAY: '1 Day',
  MONTHLY: '1 Month',
  QUATERLY: '3 Months',
  HALFYEARLY: '6 Months',
  YEARLY: '1 Year',
};

export const SUBSCRIPTION_TYPE_DAYS: Record<SubscriptionType, number> = {
  DAY: 1,
  MONTHLY: 30,
  QUATERLY: 90,
  HALFYEARLY: 180,
  YEARLY: 365,
};

// ==========================================
// DTOs
// ==========================================

export interface PriceMetadataDto {
  originalPrice: number;
  discountedPrice: number;
  discountPercentage?: number;
  isActive: boolean;
}

export interface PaperSelectionDto {
  id: string;
  paperName: string;
  paperType: PaperType;
  paperCategory: PaperCategory;
  paperSubCategory: PaperSubCategory;
  testType: TestType;
  paperSubCategoryName?: string;
  totalQuestionCount: number;
  totalScore: number;
  negativeMarks: number;
  totalTime: number;           // milliseconds
  totalTimeMinutes: number;    // computed
  paperStateStatus: string;
  createDateTime: number;
  taken: boolean;
  isSelected?: boolean;
  takenBySubscriptionId?: string;
  takenBySubscriptionName?: string;
}

export interface SubscriptionAdminDto {
  id: string;
  subscriptionId: number;

  // Classification
  paperType: PaperType;
  paperCategory: PaperCategory;
  paperSubCategory: PaperSubCategory;
  testType: TestType;

  // Content
  name: string;
  description?: string;
  paperCount: number;
  paperIds: string[];
  papers?: PaperSelectionDto[];

  // Validity
  createdDate: number;         // Activation start
  validity: number;            // Expiration
  validityDays: number;        // Remaining days
  isExpired: boolean;

  // Pricing
  pricing?: Record<SubscriptionType, PriceMetadataDto>;

  // State
  state: SubscriptionState;

  // Stats
  subscriberCount?: number;
  activeSubscribers?: number;
  totalRevenue?: number;

  // Audit
  createdBy?: string;
  createdByName?: string;
  createdAt?: number;
  updatedBy?: string;
  updatedAt?: number;
}

export interface SubscriptionCreateDto {
  paperType: PaperType;
  paperCategory: PaperCategory;
  paperSubCategory: PaperSubCategory;
  testType: TestType;
  name: string;
  description?: string;
  paperIds: string[];
  createdDate: number;
  validity: number;
  pricing?: Record<SubscriptionType, PriceMetadataDto>;
  state: SubscriptionState;
}

export interface SubscriptionUpdateDto {
  name?: string;
  description?: string;
  paperIds?: string[];
  createdDate?: number;
  validity?: number;
  pricing?: Record<SubscriptionType, PriceMetadataDto>;
}

export interface SubscriptionExtendDto {
  extendDays?: number;
  newValidity?: number;
  reason?: string;
}

export interface SubscriptionStatsDto {
  totalSubscriptions: number;
  activeSubscriptions: number;
  draftSubscriptions: number;
  totalPapersInSubscriptions?: number;
  availablePapers: number;
  totalSubscribers?: number;
  totalRevenue?: number;
  expiringIn7Days: number;
  expiringIn30Days: number;
  freeStats?: {
    total: number;
    active: number;
    revenue?: number;
  };
  paidStats?: {
    total: number;
    active: number;
    revenue?: number;
  };
}

// ==========================================
// Request Params
// ==========================================

export interface SubscriptionListParams {
  paperType?: PaperType;
  paperCategory?: PaperCategory;
  paperSubCategory?: PaperSubCategory;
  testType?: TestType;
  state?: SubscriptionState;
  search?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}

export interface AvailablePapersParams {
  testType: TestType;
  paperSubCategory: PaperSubCategory;
  excludeSubscriptionId?: string;
  search?: string;
  page?: number;
  size?: number;
}

// ==========================================
// Paginated Response
// ==========================================

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// ==========================================
// Helper Functions
// ==========================================

export function formatValidity(validityMs: number): string {
  const now = Date.now();
  const diff = validityMs - now;

  if (diff < 0) {
    return 'Expired';
  }

  const days = Math.floor(diff / (24 * 60 * 60 * 1000));

  if (days === 0) {
    return 'Expires today';
  } else if (days === 1) {
    return '1 day remaining';
  } else if (days < 30) {
    return `${days} days remaining`;
  } else if (days < 365) {
    const months = Math.floor(days / 30);
    return `${months} month${months > 1 ? 's' : ''} remaining`;
  } else {
    const years = Math.floor(days / 365);
    return `${years} year${years > 1 ? 's' : ''} remaining`;
  }
}

export function formatPrice(price: number): string {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(price);
}

export function calculateDiscountPercentage(original: number, discounted: number): number {
  if (original <= 0) return 0;
  return Math.round(((original - discounted) / original) * 100);
}

export function msToMinutes(ms: number): number {
  return Math.round(ms / 60000);
}
