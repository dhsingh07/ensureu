// Admin types for EnsureU

import type { RoleType, Role } from './auth';
import type { PaperCategory, PaperStateStatus } from './paper';

export interface DashboardStatistics {
  totalPapers: number;
  totalFreePapers: number;
  totalPaidPapers: number;
  totalUsers: number;
  activeSubscriptions: number;
  freePapersByCategory: Record<PaperCategory, number>;
  paidPapersByCategory: Record<PaperCategory, number>;
  freePapersByState: Record<PaperStateStatus, number>;
  paidPapersByState: Record<PaperStateStatus, number>;
}

export interface FeatureConfig {
  id: string;
  featureName: string;
  enabled: boolean;
  description?: string;
}

export interface FeatureConfigLegacy {
  features: Record<string, boolean>;
}

export interface FeatureToggle {
  key: string;
  label: string;
  icon: string;
  enabled: boolean;
}

export interface UserManagementItem {
  userId: string;
  id?: number;
  userName: string;
  firstName: string;
  lastName: string;
  email?: string;
  emailId?: string;
  phone?: string;
  mobileNumber?: string;
  roles: string[];
  currentRole?: RoleType;
  createdAt?: string;
  saving?: boolean;
}

export interface AdminPaperListItem {
  id?: string; // Backend returns 'id' not 'paperId'
  paperId?: string; // Frontend uses this
  paperName: string;
  paperType?: 'SSC' | 'BANK'; // Root type (SSC or BANK)
  paperCategory?: PaperCategory; // Specific category (SSC_CGL, BANK_PO, etc.)
  paperSubCategory?: string;
  testType: 'FREE' | 'PAID';
  totalScore: number;
  totalTime: number;
  totalQuestionCount?: number; // Backend uses this
  totalQuestions?: number; // Frontend alias
  negativeMarks?: number;
  perQuestionScore?: number;
  status?: PaperStateStatus;
  paperStateStatus?: PaperStateStatus;
  description?: string;
  instructions?: string;
  createdAt?: string;
  updatedAt?: string;
  // Validity dates (timestamps)
  validityRangeStartDateTime?: number;
  validityRangeEndDateTime?: number;
  createDateTime?: number;
  // Pattern with sections and questions (for full paper data)
  pattern?: {
    sections: Array<{
      id: string;
      title: string;
      sectionType: string;
      subSections?: Array<{
        id: string;
        title: string;
        questionData?: {
          questions: Array<any>;
        };
      }>;
      questionData?: {
        questions: Array<any>;
      };
      timeTakenSecond: number;
    }>;
  };
}

export interface RoleAssignmentDto {
  userId: number;
  roleType: RoleType;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface AdminPaperItem {
  id: string;
  paperName: string;
  paperCategory: PaperCategory;
  paperSubCategory: string;
  testType: 'FREE' | 'PAID';
  totalScore: number;
  totalTime: number;
  negativeMarks: number;
  perQuestionScore: number;
  status: PaperStateStatus;
  createdAt: string;
  updatedAt: string;
}

export interface NotificationConfig {
  categoryName: string;
  isEnabled: boolean;
  selectedPaperType: string;
  selectedNotificationType: string;
  day: {
    isSun: boolean;
    isMon: boolean;
    isTue: boolean;
    isWed: boolean;
    isThr: boolean;
    isFri: boolean;
    isSat: boolean;
  };
  week: {
    isWeek1: boolean;
    isWeek2: boolean;
    isWeek3: boolean;
    isWeek4: boolean;
    isWeek5: boolean;
  };
  repeat: boolean;
  notificationLists: NotificationItem[];
}

export interface NotificationItem {
  body: string;
  editable?: boolean;
}
