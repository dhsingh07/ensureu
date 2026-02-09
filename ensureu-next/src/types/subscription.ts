// Subscription types for EnsureU

import type { PaperCategory, PaperSubCategory } from './paper';

// Subscription duration types (matching backend exactly)
export type SubscriptionType = 'DAY' | 'MONTHLY' | 'QUATERLY' | 'HALFYEARLY' | 'YEARLY';

// Price metadata for each subscription type
export interface PriceMetadata {
  id: number;
  price: number;
  pricePerPaper: number;
  discountedPrice: number;
  discountedPricePerPaper: number;
  discountPercentage: number;
  minPaperCount?: number;
  extraPaperCount?: number;
  totalPaperCount?: number;
  numberOfPapers?: number;
  validity?: number | null;
  paperType?: string | null;
  paperCategory?: string | null;
  paperSubCategory?: string | null;
  subscriptionType?: string | null;
}

// Subscription state
export type SubscriptionState = 'DRAFT' | 'ACTIVE';

// Purchase status
export type PurchaseStatus = 'INPROGRESS' | 'COMPLETED' | 'FAILED';

// Entitlement type
export type EntitlementType = 'SUBSCRIPTION' | 'TEST_SERIES';

// Paper info within subscription
export interface SubscriptionPaperInfo {
  id: string;
  paperName: string;
  paperType?: string;
  paperSubCategory?: string;
}

// Subscription item (from /subscription/getAllType/* API)
export interface SubscriptionItem {
  id: number;
  paperType: string; // e.g., "SSC", "BANK"
  paperCategory: string; // e.g., "SSC CGL", "Bank PO" (display names)
  paperSubCategory: string; // e.g., "SSC CGL TIER-1"
  testType: 'FREE' | 'PAID';
  description?: string;
  validity?: number; // milliseconds
  paperIds?: string[];
  paperInfoList?: SubscriptionPaperInfo[];
  listOfSubscriptionType?: SubscriptionType[];
  mapOfSubTypeVsPrice?: Partial<Record<SubscriptionType, PriceMetadata>>;
  listOfSubscriptionIds?: number[];
  state?: SubscriptionState;
}

// DTO for subscribing to a subscription
// Must match backend SubscribedDto fields used in SubscriptionServiceImpl.subscribe()
export interface SubscribeDto {
  id: number; // subscription ID
  paperType: string; // e.g., "SSC", "BANK" - REQUIRED for entitlement check
  paperCategory: string; // e.g., "SSC_CGL" - REQUIRED for entitlement check
  paperSubCategory: string; // e.g., "SSC_CGL_TIER1" - REQUIRED to find subscription
  testType: 'FREE' | 'PAID'; // REQUIRED
  subscriptionType?: SubscriptionType; // DAY, MONTHLY, etc.
  listOfSubscriptionIds: number[]; // REQUIRED - list containing the subscription ID
}

// DTO for purchasing a paid subscription
// Must match backend PurchaseSubscriptionsDto
export interface PurchaseSubscriptionDto {
  listOfSubscriptionIds: number[];
  subscriptionType: SubscriptionType;
  actualPrice: number;
  validity?: number; // calculated based on subscriptionType
  paperType: string; // e.g., "SSC", "BANK" - REQUIRED
  paperCategory: string; // e.g., "SSC_CGL" - REQUIRED
  paperSubCategory: string; // e.g., "SSC_CGL_TIER1" - REQUIRED
  testType: 'PAID'; // Always PAID for purchase
}

// User entitlement (what papers user has access to)
export interface UserEntitlement {
  id: string;
  userId: string;
  subscriptionId: number;
  paperId: string;
  paperType: string;
  paperCategory: string;
  subscriptionType: SubscriptionType;
  validity: number; // timestamp when access expires
  active: boolean;
  entitlementType: EntitlementType;
  createdDate: number;
}

// User's subscription record (matches SubscribedDto from backend)
export interface UserSubscription {
  id: string | number;
  subscriptionId?: number;
  paperType: string;
  paperCategory: string;
  paperSubCategory: string;
  subscriptionType: SubscriptionType;
  testType?: 'FREE' | 'PAID';
  description?: string;
  validity: number;
  active?: boolean;
  createdDate?: number;
  paperCount?: number;
  paperIds?: string[];
  listOfPaperInfo?: SubscriptionPaperInfo[];
  listOfSubscriptionIds?: number[];
}

// Purchase history record
export interface PurchaseRecord {
  id: string;
  listOfSubscriptionIds: number[];
  userId: string;
  subscriptionType: SubscriptionType;
  validity: number;
  purchaseStatus: PurchaseStatus;
  actualPrice: number;
  createdDate: number;
  modifiedDate?: number;
}

// Pass (special access package)
export interface Pass {
  id: string;
  name: string;
  description?: string;
  paperType: string;
  validity: number;
  active: boolean;
  price?: number;
}

// Hierarchical structure for PAID subscriptions
export interface PaperSubCategoryDto {
  paperSubCategory: string;
  subscriptionDto: SubscriptionItem;
}

export interface PaperCategoryDto {
  paperCategory: string;
  listOfSubCategoryDto?: PaperSubCategoryDto[];
}

export interface PaperTypeDto {
  paperType: string;
  listOfCategoryDto?: PaperCategoryDto[];
}

// API response for getAllType endpoint
// FREE: { body: { listOfFreeSubscription: [...] } }
// PAID: { body: { listOfPaperTypeDto: [...] } } - hierarchical structure
export interface SubscriptionListResponse {
  listOfPaperTypeDto?: PaperTypeDto[];
  listOfFreeSubscription?: SubscriptionItem[];
}

// Pricing display helper
export const SUBSCRIPTION_LABELS: Record<SubscriptionType, string> = {
  DAY: 'Daily',
  MONTHLY: 'Monthly',
  QUATERLY: 'Quarterly',
  HALFYEARLY: 'Half Yearly',
  YEARLY: 'Yearly',
};

// Validity in milliseconds for each subscription type
export const SUBSCRIPTION_VALIDITY: Record<SubscriptionType, number> = {
  DAY: 24 * 60 * 60 * 1000, // 1 day
  MONTHLY: 30 * 24 * 60 * 60 * 1000, // 30 days
  QUATERLY: 90 * 24 * 60 * 60 * 1000, // 90 days
  HALFYEARLY: 180 * 24 * 60 * 60 * 1000, // 180 days
  YEARLY: 365 * 24 * 60 * 60 * 1000, // 365 days
};
