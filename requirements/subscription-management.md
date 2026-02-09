# Super Admin Subscription Management - Requirements Document

## Overview

This feature enables Super Admins to manage subscription packages in the EnsureU platform. Subscriptions bundle papers together for user access with defined validity periods. The system ensures papers are uniquely assigned to subscriptions and provides full lifecycle management.

---

## User Stories

### US-1: View All Subscriptions
**As a** Super Admin
**I want to** view all subscriptions across all paper types and categories
**So that I** can monitor and manage the subscription catalog

**Acceptance Criteria:**
- Display subscriptions in a filterable table
- Filter by: Paper Type (SSC/BANK), Paper Category, Paper Sub-Category, State (DRAFT/ACTIVE)
- Show key information: ID, Name, Paper Count, Validity Period, State, Created Date
- Paginated list with search capability
- Show statistics: Total subscriptions, Active count, Draft count

---

### US-2: Create New Subscription
**As a** Super Admin
**I want to** create new subscription packages
**So that I** can bundle papers for users to purchase/subscribe

**Acceptance Criteria:**
- Select Paper Type → Paper Category → Paper Sub-Category (cascading dropdowns)
- Select Test Type (FREE/PAID) - determines paper source collection:
  - FREE → papers from `freePaperCollection`
  - PAID → papers from `paidPaperCollection`
- Add subscription name and description
- Set pricing for each subscription type (DAY, MONTHLY, QUARTERLY, HALFYEARLY, YEARLY)
- Select papers using dual-list picker:
  - Shows paper unique **ID** and name
  - Only papers with `taken = false` and `paperStateStatus` in (APPROVED, ACTIVE)
  - Filter by sub-category automatically
- Set validity period (start and end dates)
- Save as DRAFT or publish as ACTIVE
- Validation: At least one paper required, pricing required for PAID subscriptions

---

### US-3: Unique Paper Assignment
**As a** Super Admin
**I want to** only see available papers when creating/editing subscriptions
**So that** papers are not duplicated across multiple active subscriptions

**Acceptance Criteria:**
- Papers are sourced from `freePaperCollection` (FREE) or `paidPaperCollection` (PAID) based on Test Type
- **Display paper's unique `id`** prominently (MongoDB _id field)
- Only show papers where:
  - `taken = false` (not in any active subscription)
  - `paperStateStatus` = APPROVED or ACTIVE
  - `paperSubCategory` matches selected sub-category
- For edit mode: Also include papers already in current subscription (even if `taken = true`)
- Display each paper with:
  - **Paper ID** (unique identifier in monospace)
  - Paper Name
  - Question Count
  - Time Limit (minutes)
  - Status Badge (APPROVED/ACTIVE)
- Display warning if attempting to add a paper that's in another subscription
- Show paper's current assignment status (Available / In Subscription: [Name])
- Search papers by ID or name
- Paginated list with "Add All" / "Remove All" bulk actions

---

### US-4: Edit Subscription
**As a** Super Admin
**I want to** edit existing subscription details
**So that I** can update pricing, papers, or descriptions

**Acceptance Criteria:**
- Edit all fields: description, pricing per subscription type
- Add/remove papers (respecting unique paper constraint)
- Cannot edit Paper Type/Category/Sub-Category after creation
- Track modification history (updatedBy, updatedAt)
- ACTIVE subscriptions: Show warning before making changes that affect existing subscribers

---

### US-5: Set Validity Period
**As a** Super Admin
**I want to** set the validity period for a subscription
**So that I** can control when the subscription is available

**Acceptance Criteria:**
- Set `createdDate` (activation start date)
- Set `validity` (expiration date/time)
- Date picker with time selection
- Validation: validity must be after createdDate
- Display validity as readable duration (e.g., "Valid for 30 days", "Expires on Dec 31, 2024")

---

### US-6: Extend Subscription Validity
**As a** Super Admin
**I want to** extend the validity of existing subscriptions
**So that I** can prolong user access when needed

**Acceptance Criteria:**
- Quick extend options: +7 days, +30 days, +90 days, +1 year
- Custom date picker for specific extension
- Show current expiry and new expiry preview before confirming
- Log extension action with reason (optional)
- Bulk extend: Select multiple subscriptions and extend together

---

### US-7: Activate/Deactivate Subscription
**As a** Super Admin
**I want to** change subscription state between DRAFT and ACTIVE
**So that I** can control subscription visibility

**Acceptance Criteria:**
- DRAFT → ACTIVE: Sets `taken = true` on all papers in subscription
- ACTIVE → DRAFT: Sets `taken = false` on all papers, freeing them for other subscriptions
- Confirmation dialog before state change
- Show impact: "This will affect X papers and Y existing subscribers"
- Cannot deactivate if users have active entitlements (option to force with warning)

---

### US-8: View Subscription Details
**As a** Super Admin
**I want to** view complete subscription details
**So that I** can review all information including papers and subscriber stats

**Acceptance Criteria:**
- Display all subscription metadata
- List all papers with their details (title, subject, topic, difficulty)
- Show pricing table for all subscription types
- Show subscriber statistics: Total subscribers, Active subscribers, Revenue generated
- Show audit trail: Created by, Created at, Modified by, Modified at
- Quick actions: Edit, Extend, Activate/Deactivate, Delete (if DRAFT)

---

### US-9: Delete Subscription
**As a** Super Admin
**I want to** delete draft subscriptions
**So that I** can remove unused subscription packages

**Acceptance Criteria:**
- Only DRAFT subscriptions can be deleted
- ACTIVE subscriptions must be deactivated first
- Confirmation dialog with subscription name
- Soft delete with archived status OR hard delete (configurable)
- Free up papers (`taken = false`)

---

### US-10: Subscription Analytics
**As a** Super Admin
**I want to** view analytics for subscriptions
**So that I** can make informed business decisions

**Acceptance Criteria:**
- Dashboard with key metrics per subscription
- Subscriber count over time (chart)
- Revenue breakdown by subscription type
- Most popular subscriptions
- Expiring subscriptions (next 7/30 days)
- Papers utilization (% of papers in active subscriptions)

---

## Data Model

### Existing Subscription Model (Enhanced)

```java
@Document(collection = "subscription")
public class Subscription {
    @Id
    private String id;

    private Long subscriptionId;           // Auto-generated unique ID

    // Classification
    private PaperType paperType;           // SSC, BANK
    private PaperCategory paperCategory;   // SSC_CGL, SSC_CPO, BANK_PO
    private PaperSubCategory paperSubCategory;  // SSC_CGL_TIER1, etc.
    private TestType testType;             // FREE, PAID

    // Content
    private String name;                   // NEW: Display name
    private String description;
    private List<String> paperIds;         // Papers in this subscription
    private List<PaperInfo> listOfPaperInfo;  // Paper details

    // Validity
    private Long createdDate;              // Activation start timestamp
    private Long validity;                 // Expiration timestamp
    private Long activeDate;               // When state became ACTIVE

    // Pricing (for PAID)
    private Map<SubscriptionType, PriceMetadata> priceMap;

    // State
    private SubscriptionState state;       // DRAFT, ACTIVE

    // Audit
    private String createdBy;              // NEW
    private String createdByName;          // NEW
    private Long createdAt;                // NEW: Record creation (different from createdDate)
    private String updatedBy;              // NEW
    private Long updatedAt;                // NEW

    // Statistics (computed/cached)
    private Integer subscriberCount;       // NEW
    private Double totalRevenue;           // NEW

    public enum SubscriptionState {
        DRAFT, ACTIVE
    }
}
```

### PriceMetadata (Existing)

```java
public class PriceMetadata {
    private Double originalPrice;
    private Double discountedPrice;
    private Double discountPercentage;
    private Boolean isActive;
}
```

### SubscriptionExtensionLog (NEW)

```java
@Document(collection = "subscriptionExtensionLog")
public class SubscriptionExtensionLog {
    @Id
    private String id;

    private String subscriptionId;
    private Long previousValidity;
    private Long newValidity;
    private Long extendedDays;
    private String reason;
    private String extendedBy;
    private Long extendedAt;
}
```

---

## API Endpoints

### Base Path: `/admin/subscription`

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/list` | List all subscriptions with filters | SUPERADMIN |
| GET | `/{id}` | Get subscription details | SUPERADMIN |
| POST | `/create` | Create new subscription | SUPERADMIN |
| PUT | `/{id}` | Update subscription | SUPERADMIN |
| PATCH | `/{id}/state` | Change subscription state | SUPERADMIN |
| PATCH | `/{id}/extend` | Extend subscription validity | SUPERADMIN |
| DELETE | `/{id}` | Delete draft subscription | SUPERADMIN |
| GET | `/stats` | Get subscription statistics | SUPERADMIN |
| GET | `/available-papers` | Get papers not in active subscriptions | SUPERADMIN |
| POST | `/bulk-extend` | Extend multiple subscriptions | SUPERADMIN |

---

## API Request/Response DTOs

### SubscriptionListParams

```typescript
interface SubscriptionListParams {
  paperType?: 'SSC' | 'BANK';
  paperCategory?: string;
  paperSubCategory?: string;
  testType?: 'FREE' | 'PAID';
  state?: 'DRAFT' | 'ACTIVE';
  search?: string;           // Search by name/description
  page?: number;
  size?: number;
  sortBy?: 'createdAt' | 'validity' | 'subscriberCount';
  sortDir?: 'asc' | 'desc';
}
```

### SubscriptionAdminDto (Response)

```typescript
interface SubscriptionAdminDto {
  id: string;
  subscriptionId: number;

  // Classification
  paperType: string;
  paperCategory: string;
  paperSubCategory: string;
  testType: 'FREE' | 'PAID';

  // Content
  name: string;
  description: string;
  paperCount: number;
  papers: PaperInfoDto[];

  // Validity
  createdDate: number;       // Activation start
  validity: number;          // Expiration
  validityDays: number;      // Computed remaining days
  isExpired: boolean;

  // Pricing
  pricing: {
    [key in SubscriptionType]?: PriceMetadataDto;
  };

  // State
  state: 'DRAFT' | 'ACTIVE';

  // Stats
  subscriberCount: number;
  activeSubscribers: number;
  totalRevenue: number;

  // Audit
  createdBy: string;
  createdByName: string;
  createdAt: number;
  updatedBy?: string;
  updatedAt?: number;
}
```

### SubscriptionCreateDto (Request)

```typescript
interface SubscriptionCreateDto {
  // Classification (required)
  paperType: 'SSC' | 'BANK';
  paperCategory: string;
  paperSubCategory: string;
  testType: 'FREE' | 'PAID';

  // Content
  name: string;              // Required
  description?: string;
  paperIds: string[];        // At least 1 required

  // Validity
  createdDate: number;       // Activation start timestamp
  validity: number;          // Expiration timestamp

  // Pricing (required for PAID)
  pricing?: {
    [key in SubscriptionType]?: {
      originalPrice: number;
      discountedPrice: number;
      discountPercentage?: number;
      isActive: boolean;
    };
  };

  // State
  state: 'DRAFT' | 'ACTIVE';
}
```

### SubscriptionUpdateDto (Request)

```typescript
interface SubscriptionUpdateDto {
  name?: string;
  description?: string;
  paperIds?: string[];
  validity?: number;
  pricing?: {
    [key in SubscriptionType]?: PriceMetadataDto;
  };
}
```

### SubscriptionExtendDto (Request)

```typescript
interface SubscriptionExtendDto {
  extendDays?: number;       // Quick extend
  newValidity?: number;      // Or set specific date
  reason?: string;
}
```

### SubscriptionStateDto (Request)

```typescript
interface SubscriptionStateDto {
  state: 'DRAFT' | 'ACTIVE';
  force?: boolean;           // Force deactivation even with active subscribers
}
```

### AvailablePapersParams

```typescript
interface AvailablePapersParams {
  paperType: string;
  paperCategory: string;
  paperSubCategory: string;
  excludeSubscriptionId?: string;  // Exclude papers from this subscription (for edit)
  search?: string;
  page?: number;
  size?: number;
}
```

### SubscriptionStatsDto (Response)

```typescript
interface SubscriptionStatsDto {
  totalSubscriptions: number;
  activeSubscriptions: number;
  draftSubscriptions: number;
  totalPapersInSubscriptions: number;
  availablePapers: number;
  totalSubscribers: number;
  totalRevenue: number;
  expiringIn7Days: number;
  expiringIn30Days: number;

  byPaperType: {
    [key: string]: {
      total: number;
      active: number;
      subscribers: number;
    };
  };

  byTestType: {
    FREE: { total: number; active: number; };
    PAID: { total: number; active: number; revenue: number; };
  };
}
```

---

## UI Components

### 1. Subscription List Page (`/admin/subscriptions`)

```
+----------------------------------------------------------+
|  Subscription Management                    [+ Create New] |
+----------------------------------------------------------+
|  Stats: Total: 45 | Active: 32 | Draft: 13 | Expiring: 5  |
+----------------------------------------------------------+
| Filters:                                                   |
| [Paper Type ▼] [Category ▼] [Sub-Category ▼] [State ▼]    |
| [Search by name...                           ] [Search]    |
+----------------------------------------------------------+
| ☐ | ID      | Name           | Papers | Validity  | State  |
|---|---------|----------------|--------|-----------|--------|
| ☐ | SUB-001 | SSC CGL Tier1  | 25     | 30 days   | ACTIVE |
| ☐ | SUB-002 | Bank PO Prelim | 15     | Expired   | ACTIVE |
| ☐ | SUB-003 | New Package    | 10     | 60 days   | DRAFT  |
+----------------------------------------------------------+
| [Bulk Extend] [Bulk Activate]          Page 1 of 5 [< >]  |
+----------------------------------------------------------+
```

### 2. Create/Edit Subscription Page

```
+----------------------------------------------------------+
| ← Back to List         Create New Subscription             |
+----------------------------------------------------------+
| CLASSIFICATION                                             |
| Paper Type:     [SSC ▼]                                   |
| Category:       [SSC CGL ▼]                               |
| Sub-Category:   [Tier 1 ▼]                                |
| Test Type:      [PAID ▼]                                  |
+----------------------------------------------------------+
| DETAILS                                                    |
| Name:           [SSC CGL Tier-1 Package              ]    |
| Description:    [Complete practice package for...    ]    |
+----------------------------------------------------------+
| VALIDITY                                                   |
| Start Date:     [📅 Feb 15, 2024]                         |
| End Date:       [📅 Mar 15, 2024]                         |
| Duration:       30 days                                    |
+----------------------------------------------------------+
| PRICING (PAID only)                                        |
| ┌─────────────┬───────────┬────────────┬─────────┐        |
| │ Type        │ Original  │ Discounted │ Active  │        |
| ├─────────────┼───────────┼────────────┼─────────┤        |
| │ DAY         │ ₹49       │ ₹29        │ [✓]     │        |
| │ MONTHLY     │ ₹299      │ ₹199       │ [✓]     │        |
| │ QUARTERLY   │ ₹799      │ ₹599       │ [✓]     │        |
| │ HALFYEARLY  │ ₹1499     │ ₹999       │ [ ]     │        |
| │ YEARLY      │ ₹2499     │ ₹1499      │ [ ]     │        |
| └─────────────┴───────────┴────────────┴─────────┘        |
+----------------------------------------------------------+
| SELECT PAPERS                                              |
| Available (45)                    Selected (12)            |
| ┌─────────────────────┐          ┌─────────────────────┐  |
| │ ☐ Paper 1 - Math    │   [>>]   │ ☐ Paper A - GK      │  |
| │ ☐ Paper 2 - English │          │ ☐ Paper B - Quant   │  |
| │ ☐ Paper 3 - GK      │   [<<]   │ ☐ Paper C - Reason  │  |
| │ ☐ Paper 4 - Reason  │          │                     │  |
| └─────────────────────┘          └─────────────────────┘  |
| [Search papers...]                                         |
+----------------------------------------------------------+
|               [Save as Draft]    [Save & Activate]         |
+----------------------------------------------------------+
```

### 3. Subscription Detail Page

```
+----------------------------------------------------------+
| ← Back        SUB-001: SSC CGL Tier-1 Package              |
|                                          [Edit] [Extend]   |
+----------------------------------------------------------+
| Status: [ACTIVE]  Papers: 25  Subscribers: 156  ₹45,200   |
+----------------------------------------------------------+
| TABS: [Details] [Papers] [Pricing] [Subscribers] [History] |
+----------------------------------------------------------+
| Details Tab:                                               |
| Paper Type:       SSC                                      |
| Category:         SSC CGL                                  |
| Sub-Category:     Tier 1                                   |
| Test Type:        PAID                                     |
| Description:      Complete practice package...             |
|                                                            |
| Validity:                                                  |
| Start:            Feb 15, 2024                            |
| End:              Mar 15, 2024                            |
| Remaining:        18 days                                  |
|                              [Extend +7d] [Extend +30d]    |
|                                                            |
| Audit:                                                     |
| Created By:       admin@ensureu.com on Feb 1, 2024        |
| Last Modified:    superadmin@ensureu.com on Feb 10, 2024  |
+----------------------------------------------------------+
| Quick Actions:                                             |
| [Deactivate]  [Duplicate]  [Delete]                       |
+----------------------------------------------------------+
```

### 4. Extend Validity Dialog

```
+------------------------------------------+
|        Extend Subscription Validity       |
+------------------------------------------+
| Current Expiry:  Mar 15, 2024            |
|                                          |
| Quick Extend:                            |
| [+7 days] [+30 days] [+90 days] [+1 year]|
|                                          |
| Or select date:                          |
| New Expiry:   [📅 Apr 15, 2024        ]  |
|                                          |
| Preview:      30 days extension          |
|               (Mar 15 → Apr 15, 2024)    |
|                                          |
| Reason (optional):                       |
| [Promotional extension for Holi      ]   |
|                                          |
|            [Cancel]    [Extend]          |
+------------------------------------------+
```

---

## Business Rules

### BR-1: Paper Uniqueness
- A paper can only be in ONE active subscription at a time
- Papers are marked with `taken = true` when subscription becomes ACTIVE
- Papers are freed (`taken = false`) when subscription is deactivated or deleted

### BR-2: Subscription State Transitions
```
DRAFT ──────> ACTIVE
  │              │
  │              │ (deactivate)
  │              ▼
  │           DRAFT
  │
  ▼
(delete)
```

### BR-3: Validity Rules
- `createdDate` = When subscription becomes available to users
- `validity` = When subscription expires
- `validity` must be > `createdDate`
- Subscriptions with `validity` < now are marked as expired but remain ACTIVE
- Users with entitlements continue access until their personal entitlement expires

### BR-4: Pricing Rules (PAID Subscriptions)
- At least one subscription type must have `isActive = true`
- `discountedPrice` must be ≤ `originalPrice`
- `discountPercentage` is calculated automatically if not provided

### BR-5: Deletion Rules
- Only DRAFT subscriptions can be deleted
- ACTIVE subscriptions must be deactivated first
- Cannot deactivate if users have active entitlements (unless forced)

### BR-6: Edit Restrictions
- Cannot change: `paperType`, `paperCategory`, `paperSubCategory`, `testType` after creation
- These are immutable to maintain data integrity with existing entitlements

---

## Security

- All endpoints require SUPERADMIN role
- Existing `/admin/subscription/*` endpoints to be restricted to SUPERADMIN (currently SUPERADMIN only per WebSecurityConfig)
- Audit trail for all create/update/delete operations
- Rate limiting on bulk operations

---

## Error Handling

| Error Code | Message | Scenario |
|------------|---------|----------|
| SUB-001 | "Paper already in active subscription" | Adding taken paper |
| SUB-002 | "Cannot delete active subscription" | Delete ACTIVE subscription |
| SUB-003 | "Validity must be after start date" | Invalid date range |
| SUB-004 | "At least one paper required" | Empty paper list |
| SUB-005 | "Pricing required for paid subscription" | Missing prices |
| SUB-006 | "Cannot deactivate: X users have active access" | Active entitlements |
| SUB-007 | "Subscription not found" | Invalid ID |
| SUB-008 | "Cannot modify immutable fields" | Edit paperType etc. |

---

## Migration Notes

### Existing Data
- Add `name` field to existing subscriptions (derive from category/subcategory)
- Add `createdBy`, `createdAt`, `updatedBy`, `updatedAt` fields (set to system/null)
- Ensure all existing subscriptions have valid `createdDate` and `validity`

### API Compatibility
- Existing `/admin/subscription/fetch/{subCategory}` continues to work
- Existing `/admin/subscription/create` and `/patch` enhanced with new fields
- New endpoints added alongside existing ones

---

## Implementation Phases

### Phase 1: Core CRUD
- List subscriptions with filters
- View subscription details
- Create subscription with paper selection
- Edit subscription

### Phase 2: State Management
- Activate/deactivate subscription
- Paper uniqueness validation
- Conflict detection and resolution

### Phase 3: Validity Management
- Set validity dates
- Extend validity (single and bulk)
- Extension logging

### Phase 4: Analytics
- Subscription statistics
- Subscriber counts
- Revenue tracking
- Expiration alerts

---

## Paper Selection - Source Collections

### Paper Sources

Papers for subscriptions come from two MongoDB collections based on Test Type:

| Test Type | Collection | Model Class |
|-----------|------------|-------------|
| FREE | `freePaperCollection` | `FreePaperCollection` |
| PAID | `paidPaperCollection` | `PaidPaperCollection` |

### Paper Model Fields (Both Collections)

```java
// Unique Identifier
String id;                              // MongoDB _id, unique paper ID

// Classification
PaperType paperType;                    // SSC, BANK
PaperCategory paperCategory;            // SSC_CGL, SSC_CPO, BANK_PO
PaperSubCategory paperSubCategory;      // SSC_CGL_TIER1, etc.
TestType testType;                      // FREE, PAID

// Paper Details
String paperName;                       // Display name (e.g., "SSC CGL Mock Test 1")
String paperSubCategoryName;            // Sub-category display name
int totalQuestionCount;                 // Number of questions
double totalScore;                      // Maximum marks
double negativeMarks;                   // Negative marking value
double perQuestionScore;                // Marks per question
long totalTime;                         // Time limit in milliseconds

// Validity
Long createDateTime;                    // Paper creation timestamp
Long validityRangeStartDateTime;        // When paper becomes available
Long validityRangeEndDateTime;          // When paper expires

// Status
boolean taken;                          // TRUE = in active subscription, FALSE = available
int priorty;                            // Display priority (typo in original - "priorty")
PaperStateStatus paperStateStatus;      // DRAFT, APPROVED, ACTIVE
```

### Available Papers Query Logic

When selecting papers for a subscription, show papers that meet ALL criteria:

```sql
-- For FREE subscriptions (freePaperCollection)
WHERE testType = 'FREE'
  AND paperSubCategory = {selected_sub_category}
  AND taken = false
  AND paperStateStatus IN ('APPROVED', 'ACTIVE')

-- For PAID subscriptions (paidPaperCollection)
WHERE testType = 'PAID'
  AND paperSubCategory = {selected_sub_category}
  AND taken = false
  AND paperStateStatus IN ('APPROVED', 'ACTIVE')

-- For EDIT mode: Also include papers already in current subscription
OR id IN ({current_subscription_paper_ids})
```

### Paper Selection UI

```
+------------------------------------------------------------------+
| SELECT PAPERS FOR SUBSCRIPTION                                    |
| Test Type: PAID | Sub-Category: SSC_CGL_TIER1                    |
+------------------------------------------------------------------+
| Search: [Search by paper name or ID...        ] [🔍]             |
+------------------------------------------------------------------+
| AVAILABLE PAPERS (23)                 SELECTED PAPERS (5)         |
| ┌─────────────────────────────────┐  ┌─────────────────────────┐ |
| │ ☐ 65f2a1b3c4d5e6f7a8b9c0d1     │  │ ☐ 65f1a0b2c3d4e5f6a7b8 │ |
| │   SSC CGL Mock Test 12         │  │   SSC CGL Mock Test 1   │ |
| │   Questions: 100 | Time: 60min │  │   Questions: 100        │ |
| │   Status: APPROVED             │  │                         │ |
| │                                │  │ ☐ 65f1a0b2c3d4e5f6a7b9 │ |
| │ ☐ 65f2a1b3c4d5e6f7a8b9c0d2     │  │   SSC CGL Mock Test 2   │ |
| │   SSC CGL Mock Test 13         │  │   Questions: 100        │ |
| │   Questions: 100 | Time: 60min │  │                         │ |
| │   Status: ACTIVE               │  │ ☐ 65f1a0b2c3d4e5f6a7c0 │ |
| │                                │  │   SSC CGL Mock Test 3   │ |
| │ ☐ 65f2a1b3c4d5e6f7a8b9c0d3     │  │   Questions: 100        │ |
| │   SSC CGL Previous Year 2023   │  │                         │ |
| │   Questions: 100 | Time: 60min │  │                         │ |
| │   Status: APPROVED             │  │                         │ |
| └─────────────────────────────────┘  └─────────────────────────┘ |
|        [Add Selected >>]                [<< Remove Selected]      |
|        [Add All >>]                     [<< Remove All]           |
+------------------------------------------------------------------+
| Paper Legend:                                                     |
| 🟢 ACTIVE - Live paper | 🟡 APPROVED - Ready to use              |
| Showing papers with taken=false only                              |
+------------------------------------------------------------------+
```

### Paper Info Display in Selection

Each paper card shows:
- **Paper ID** (unique identifier) - displayed in monospace font
- **Paper Name** - display title
- **Question Count** - total questions
- **Time Limit** - in minutes (converted from milliseconds)
- **Status Badge** - APPROVED (yellow) or ACTIVE (green)
- **Negative Marking** - if applicable

### API Endpoint for Available Papers

```
GET /admin/subscription/available-papers
```

**Request Parameters:**
```typescript
interface AvailablePapersRequest {
  testType: 'FREE' | 'PAID';           // Required - determines collection
  paperSubCategory: string;             // Required - filter by sub-category
  excludeSubscriptionId?: string;       // Optional - for edit mode
  search?: string;                      // Optional - search by name/id
  page?: number;                        // Default: 0
  size?: number;                        // Default: 20
}
```

**Response:**
```typescript
interface AvailablePapersResponse {
  papers: PaperSelectionDto[];
  totalCount: number;
  page: number;
  size: number;
  totalPages: number;
}

interface PaperSelectionDto {
  id: string;                          // Unique paper ID
  paperName: string;
  paperType: string;
  paperCategory: string;
  paperSubCategory: string;
  totalQuestionCount: number;
  totalScore: number;
  negativeMarks: number;
  totalTime: number;                   // milliseconds
  totalTimeMinutes: number;            // computed: totalTime / 60000
  paperStateStatus: string;            // APPROVED, ACTIVE
  createDateTime: number;

  // Computed fields
  isSelected?: boolean;                // For edit mode - already in subscription
  takenBySubscription?: string;        // If taken, show which subscription (for warnings)
}
```

### Backend Service Method

```java
// In SubscriptionServiceAdminImpl

public Page<PaperSelectionDto> getAvailablePapers(
    TestType testType,
    PaperSubCategory paperSubCategory,
    String excludeSubscriptionId,
    String search,
    Pageable pageable
) {
    if (testType == TestType.FREE) {
        // Query freePaperCollectionRepository
        return freePaperCollectionRepository
            .findByPaperSubCategoryAndTakenAndPaperStateStatusIn(
                paperSubCategory,
                false,  // taken = false
                Arrays.asList(PaperStateStatus.APPROVED, PaperStateStatus.ACTIVE),
                pageable
            );
    } else {
        // Query paidPaperCollectionRepository
        return paidPaperCollectionRepository
            .findByPaperSubCategoryAndTakenAndPaperStateStatusIn(
                paperSubCategory,
                false,  // taken = false
                Arrays.asList(PaperStateStatus.APPROVED, PaperStateStatus.ACTIVE),
                pageable
            );
    }
}
```

### Setting Paper Taken Flag

When subscription state changes:

```java
// Activate subscription - mark papers as taken
public void activateSubscription(Subscription subscription) {
    List<String> paperIds = subscription.getPaperIds();
    TestType testType = subscription.getTestType();

    if (testType == TestType.FREE) {
        freePaperCollectionRepository.setTakenFlag(paperIds, true);
    } else {
        paidPaperCollectionRepository.setTakenFlag(paperIds, true);
    }

    subscription.setState(SubscriptionState.ACTIVE);
    subscriptionRepository.save(subscription);
}

// Deactivate subscription - release papers
public void deactivateSubscription(Subscription subscription) {
    List<String> paperIds = subscription.getPaperIds();
    TestType testType = subscription.getTestType();

    if (testType == TestType.FREE) {
        freePaperCollectionRepository.setTakenFlag(paperIds, false);
    } else {
        paidPaperCollectionRepository.setTakenFlag(paperIds, false);
    }

    subscription.setState(SubscriptionState.DRAFT);
    subscriptionRepository.save(subscription);
}
```

---

## Dependencies

- **Backend**: FreePaperCollection and PaidPaperCollection have `taken` boolean field (exists)
- **Backend**: Repositories need new query methods for filtering by taken and status
- **Frontend**: React Query hooks for subscription operations
- **Frontend**: Date picker component (use existing shadcn/ui)
- **Frontend**: Dual-list selector component for paper selection

---

## Testing Checklist

- [ ] Create subscription with valid papers
- [ ] Prevent duplicate paper assignment
- [ ] State transition DRAFT → ACTIVE → DRAFT
- [ ] Extend validity with various options
- [ ] Delete draft subscription
- [ ] Prevent delete of active subscription
- [ ] Filter and search subscriptions
- [ ] Bulk extend multiple subscriptions
- [ ] Price calculation and validation
- [ ] Date validation (validity > createdDate)
- [ ] Audit trail logging
- [ ] Permission checks (SUPERADMIN only)
- [ ] Paper ID displayed correctly in selection UI
- [ ] Papers from correct collection based on testType
- [ ] Taken flag updates correctly on activate/deactivate
- [ ] Edit mode shows papers already in subscription

---

## New Repository Methods Required

### FreePaperCollectionRepository

```java
// Find available papers for subscription selection
@Query("{ 'paperSubCategory': ?0, 'taken': ?1, 'paperStateStatus': { $in: ?2 } }")
Page<FreePaperCollection> findByPaperSubCategoryAndTakenAndPaperStateStatusIn(
    PaperSubCategory paperSubCategory,
    boolean taken,
    List<PaperStateStatus> statuses,
    Pageable pageable
);

// Bulk update taken flag
@Query("{ '_id': { $in: ?0 } }")
@Update("{ '$set': { 'taken': ?1 } }")
void updateTakenFlagByIdIn(List<String> ids, boolean taken);

// Count available papers
long countByPaperSubCategoryAndTakenAndPaperStateStatusIn(
    PaperSubCategory paperSubCategory,
    boolean taken,
    List<PaperStateStatus> statuses
);
```

### PaidPaperCollectionRepository

```java
// Find available papers for subscription selection
@Query("{ 'paperSubCategory': ?0, 'taken': ?1, 'paperStateStatus': { $in: ?2 } }")
Page<PaidPaperCollection> findByPaperSubCategoryAndTakenAndPaperStateStatusIn(
    PaperSubCategory paperSubCategory,
    boolean taken,
    List<PaperStateStatus> statuses,
    Pageable pageable
);

// Bulk update taken flag
@Query("{ '_id': { $in: ?0 } }")
@Update("{ '$set': { 'taken': ?1 } }")
void updateTakenFlagByIdIn(List<String> ids, boolean taken);

// Count available papers
long countByPaperSubCategoryAndTakenAndPaperStateStatusIn(
    PaperSubCategory paperSubCategory,
    boolean taken,
    List<PaperStateStatus> statuses
);

// Find papers by IDs (for edit mode - get current subscription papers)
List<PaidPaperCollection> findByIdIn(List<String> ids);
```

---

## Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                    SUBSCRIPTION MANAGEMENT FLOW                      │
└─────────────────────────────────────────────────────────────────────┘

1. CREATE SUBSCRIPTION
   ┌──────────┐     ┌──────────────────┐     ┌────────────────┐
   │ Select   │────▶│ Query Papers     │────▶│ Display        │
   │ TestType │     │ (FREE/PAID coll) │     │ Available      │
   │ + SubCat │     │ taken=false      │     │ Papers + IDs   │
   └──────────┘     └──────────────────┘     └────────────────┘
                                                     │
                                                     ▼
                                             ┌────────────────┐
                                             │ Select Papers  │
                                             │ (store IDs)    │
                                             └────────────────┘
                                                     │
                    ┌────────────────────────────────┴───────────┐
                    ▼                                            ▼
            ┌──────────────┐                           ┌──────────────┐
            │ Save DRAFT   │                           │ Save ACTIVE  │
            │ (taken=false)│                           │ (taken=true) │
            └──────────────┘                           └──────────────┘

2. ACTIVATE SUBSCRIPTION
   ┌──────────────┐     ┌────────────────────┐     ┌──────────────┐
   │ Subscription │────▶│ Get paperIds from  │────▶│ Update papers│
   │ DRAFT→ACTIVE│     │ subscription       │     │ taken=true   │
   └──────────────┘     └────────────────────┘     └──────────────┘

3. DEACTIVATE SUBSCRIPTION
   ┌──────────────┐     ┌────────────────────┐     ┌──────────────┐
   │ Subscription │────▶│ Get paperIds from  │────▶│ Update papers│
   │ ACTIVE→DRAFT│     │ subscription       │     │ taken=false  │
   └──────────────┘     └────────────────────┘     └──────────────┘

4. PAPER COLLECTIONS
   ┌───────────────────────────────────────────────────────────────┐
   │                                                               │
   │   freePaperCollection              paidPaperCollection        │
   │   ┌─────────────────────┐          ┌─────────────────────┐   │
   │   │ id: "65f2a1b3..."   │          │ id: "65f3b2c4..."   │   │
   │   │ paperName: "Test 1" │          │ paperName: "Mock 1" │   │
   │   │ taken: false ✓      │          │ taken: true ✗       │   │
   │   │ paperStateStatus:   │          │ paperStateStatus:   │   │
   │   │   APPROVED          │          │   ACTIVE            │   │
   │   └─────────────────────┘          └─────────────────────┘   │
   │                                                               │
   │   Used for: FREE subscriptions     Used for: PAID subs       │
   └───────────────────────────────────────────────────────────────┘
```
