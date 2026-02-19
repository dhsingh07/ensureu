// API URL constants - migrated from Angular utils/constants/main.ts

export const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8282/api/';
export const AI_SERVICE_URL = process.env.NEXT_PUBLIC_AI_SERVICE_URL || 'http://localhost:8000/';

export const API_URLS = {
  // Auth
  AUTH: {
    TOKEN: 'auth/token',
    REFRESH: 'auth/refresh',
    PROVIDER_TOKEN: 'auth/providertoken',
  },

  // User
  USER: {
    CREATE: 'user/create',
    SAVE_PRESENT: 'user/savepresent',
    GET_BY_USERNAME: 'user/getbyusername',
    PROFILE_UPDATE: 'user/profile/update',
  },

  // OTP
  OTP: {
    GENERATE: 'otp/generate',
    VALIDATE: 'otp/validate',
    GET: 'otp/get',
  },

  // Dashboard
  DASHBOARD: {
    FREE_PAPER: 'subscription/getAllType/FREE',
    PAID_PAPER: 'subscription/getAllType/PAID',
    TESTIMONIALS: 'testimonials/list',
  },

  // Home / Test List
  HOME: {
    PAPER_TYPE_DATA: 'papermetadata/paperType',
    TEST_LIST: 'paper/user/list/paperType',
    MISSED_LIST: 'paper/user/missed/paperType',
    COMPLETED_LIST: 'paper/user/paperType',
  },

  // Paper / Exam
  PAPER: {
    GET: 'paper',
    GET_ENCRYPTED: 'paper/v1/user/mapping',
    SAVE: 'paper/save',
    SAVE_ENCRYPTED: 'paper/v1/save',
    COUNT: 'paper/count',
  },

  // Past Paper
  PAST_PAPER: {
    GET: 'pastpaper',
    GET_ENCRYPTED: 'pastpaper/v1/user/mapping',
    SAVE: 'pastpaper/save',
    SAVE_ENCRYPTED: 'pastpaper/v1/save',
    LIST: 'pastpaper/user/list/paperType',
    COUNT: 'pastpaper/count',
  },

  // Practice Paper
  PRACTICE: {
    GET: 'practicepaper/getbyid',
    COUNT: 'practicepaper/count',
    QUESTIONS_COUNT: 'practicepaper/count/questions',
  },

  // Analytics
  ANALYTICS: {
    USER: 'analytics/v1/user',
    USER_PAPER_STAT: 'analytics/v1/test/userPaperStat',
  },

  // Data Ingest
  DATA_INGEST: {
    USER_TIME_SERIES: 'dataIngest/v1/userTimeSeries',
    USER_PAPER_STAT: 'dataIngest/v1/userPaperStat',
    FETCH_TIME_SERIES: 'dataIngest/v1/fetch/userTimeSeries',
  },

  // Subscription
  SUBSCRIPTION: {
    SUBSCRIBE: 'subscription/subscribe',
    GET_USER_SUBSCRIPTION: 'subscription/getUserSubscription/paperType',
    GET_ALL_USER_SUBSCRIPTION: 'subscription/getAllUserSubscription/paperType',
    PURCHASE: 'subscription/purchaseSubscriptions',
    GET_SUBSCRIPTIONS: 'subscription/getSubscriptions/paperType',
    GET_ALL_PASS: 'subscription/getAllPass',
  },

  // Vault (Saved Questions)
  VAULT: {
    SAVE: 'vault/save',
    LIST: 'vault/user/list',
    DELETE: 'vault/user/delete',
  },

  // Blog
  BLOG: {
    CREATE: 'blog/create',
    UPDATE: 'blog/update',
    PARTIAL_UPDATE: 'blog/partial/update',
    FETCH_ALL: 'blog/fetch/all',
    FETCH_BY_ID: 'blog/fetch/byId',
    FETCH_BY_CATEGORY: 'blog/fetch/byCategory',
    FETCH_BY_USER: 'blog/fetch/byUser',
    SEARCH: 'blog/searchByTitle',
    DELETE: 'blog/delete',
    ADD_COMMENT: 'blog/add/comments',
    FETCH_COMMENTS: 'blog/fetch/comments',
    UPLOAD_IMAGE: 'blog/upload/image',
    CATEGORY_ADD: 'blog/category/add',
    CATEGORY_ADD_LIST: 'blog/category/add/list',
    CATEGORY_FETCH_ALL: 'blog/category/fetch/all',
    CATEGORY_COUNT: 'blog/category/fetch/categories_count',
    TAGS_FETCH_ALL: 'blog/tags/fetch/all',
    TAGS_ADD: 'blog/tags/add',
    TAGS_ADD_LIST: 'blog/tags/add/list',
  },

  // Quiz
  QUIZ: {
    LIST: 'quiz/user/list/paperType',
    GET_ENCRYPTED: 'quiz/v1/user/mapping',
    SAVE_ENCRYPTED: 'quiz/v1/save',
    COMPLETED: 'quiz/user/paperType',
    // Admin/Collection endpoints
    COLLECTION_CREATE: 'quizPaperColl/create',
    COLLECTION_GET_BY_ID: 'quizPaperColl/getbyid',
    COLLECTION_LIST: 'quizPaperColl/list',
    COLLECTION_UPDATE: 'quizPaperColl/update',
    COLLECTION_DELETE: 'quizPaperColl/delete',
  },

  // Admin
  ADMIN: {
    // Dashboard
    STATISTICS: 'admin/dashboard/statistics',

    // Feature Config
    FEATURE_CONFIG_FETCH: 'admin/feature-config/fetch',
    FEATURE_CONFIG_UPDATE: 'admin/feature-config/update',

    // Paper Management
    PAPER_SAVE: 'admin/paper/save',
    PAPER_UPDATE_STATE: 'admin/paper/save',
    PAPER_LIST: 'admin/paper/list',
    PAPER_GET_BY_ID: 'admin/paper/getbyid',
    PAPER_DELETE: 'admin/paper/delete',
    PAPER_UPLOAD_IMAGE: 'admin/paper/upload/image',
    PAPER_SIMPLE_SAVE: 'admin/paper/simple/save',
    PAPER_SIMPLE_PREVIEW: 'admin/paper/simple/preview',

    // User Management
    USER_LIST: 'admin/user-management/list',
    USER_GET: 'admin/user-management/get',
    USER_SEARCH: 'admin/user-management/search',
    USER_ASSIGN_ROLE: 'admin/user-management/assign-role',

    // Question Bank
    QUESTION_BANK_CREATE: 'admin/question-bank/create',
    QUESTION_BANK_UPDATE: 'admin/question-bank/update',
    QUESTION_BANK_GET: 'admin/question-bank',
    QUESTION_BANK_DELETE: 'admin/question-bank/delete',
    QUESTION_BANK_LIST: 'admin/question-bank/list',
    QUESTION_BANK_MY_QUESTIONS: 'admin/question-bank/my-questions',
    QUESTION_BANK_SUBMIT_FOR_REVIEW: 'admin/question-bank/submit-for-review',
    QUESTION_BANK_APPROVE: 'admin/question-bank/approve',
    QUESTION_BANK_REJECT: 'admin/question-bank/reject',
    QUESTION_BANK_PENDING: 'admin/question-bank/pending',
    QUESTION_BANK_STATS: 'admin/question-bank/stats',
    QUESTION_BANK_SEARCH: 'admin/question-bank/search',
  },

  // Image
  IMAGE: {
    FILE: 'image/file',
  },

  // AI Service
  AI: {
    HEALTH: 'ai/health',
    GENERATE_QUESTIONS: 'ai/questions/generate',
    EXPLAIN_WRONG: 'ai/questions/explain-wrong',
    GET_HINT: 'ai/questions/hint',
    VALIDATE_QUESTION: 'ai/questions/validate',
    ANALYZE_EXAM: 'ai/analysis/exam',
    STUDY_PLAN: 'ai/analysis/study-plan',
    // Analysis History
    ANALYSIS_HISTORY: 'ai/analysis/history', // + /{userId}
    ANALYSIS_MONTHLY: 'ai/analysis/monthly-summary', // + /{userId}
    ANALYSIS_BY_EXAM: 'ai/analysis/exam', // + /{userId}/{examId}
    ANALYSIS_WEAK_AREAS: 'ai/analysis/weak-areas', // + /{userId}
    // LLM Configuration (via Java backend)
    LLM_PROVIDERS: 'admin/llm-config/providers',
    LLM_CONFIG_CURRENT: 'admin/llm-config/current',
    LLM_CONFIG_UPDATE: 'admin/llm-config/update',
    LLM_CONFIG_TEST: 'admin/llm-config/test',
  },
} as const;

// App configuration constants
export const APP_CONFIG = {
  REFRESH_TOKEN_INTERVAL: 50 * 60 * 1000, // 50 minutes
  AUTO_SAVE_INTERVAL: 5 * 60 * 1000, // 5 minutes
  BLOG_PAGE_LIMIT: 10,
  DEFAULT_PAGE_SIZE: 10,
} as const;

// Category definitions
export const CATEGORIES = [
  {
    name: 'SSC',
    label: 'SSC',
    icon: 'GraduationCap',
    subCategories: [
      { name: 'SSC_CGL', label: 'SSC CGL' },
      { name: 'SSC_CPO', label: 'SSC CPO' },
      { name: 'SSC_CHSL', label: 'SSC CHSL' },
    ],
  },
  {
    name: 'BANK',
    label: 'Bank',
    icon: 'Building2',
    subCategories: [
      { name: 'BANK_PO', label: 'Bank PO' },
    ],
  },
] as const;

// Role definitions
export const ROLES = {
  USER: 'USER',
  ADMIN: 'ADMIN',
  SUPERADMIN: 'SUPERADMIN',
  TEACHER: 'TEACHER',
} as const;

// Status messages
export const MESSAGES = {
  UNAUTHORIZED: 'Unauthorised Access. Please login again.',
  CONNECTION_ERROR: 'Unable to connect to server. Please check your connection.',
  SUCCESS: 'Operation completed successfully.',
  ERROR: 'An error occurred. Please try again.',
} as const;
