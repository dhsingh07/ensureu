# Question Bank Feature - Requirements Document

## 1. Overview

The Question Bank is a centralized repository for storing, organizing, and managing questions that can be used to create various types of assessments (Papers, Quizzes, Practice Tests). Teachers and Admins can create, edit, and organize questions by subject, topic, and difficulty level.

---

## 2. User Roles & Permissions

| Role | Permissions |
|------|-------------|
| **SUPERADMIN** | Full access - Create, Edit, Delete, Approve questions, Manage all categories |
| **ADMIN** | Create, Edit, Delete questions, View all questions |
| **TEACHER** | Create, Edit own questions, View approved questions, Submit for approval |
| **USER** | No access to Question Bank (only takes tests) |

---

## 3. Question Taxonomy (Hierarchy)

```
PaperType (Level 1)
├── SSC
│   ├── SSC_CGL (Category)
│   │   ├── TIER-1 (SubCategory)
│   │   │   ├── General Intelligence and Reasoning (Subject)
│   │   │   │   ├── Analogies (Topic)
│   │   │   │   ├── Coding-Decoding (Topic)
│   │   │   │   ├── Series (Topic)
│   │   │   │   └── ...
│   │   │   ├── General Awareness (Subject)
│   │   │   ├── Quantitative Aptitude (Subject)
│   │   │   └── English Comprehension (Subject)
│   │   └── TIER-2 (SubCategory)
│   │       ├── Quantitative Ability (Subject)
│   │       ├── English Language & Comprehension (Subject)
│   │       ├── Statistics (Subject)
│   │       └── General Studies (Finance & Economics) (Subject)
│   ├── SSC_CPO
│   │   └── ...
│   └── SSC_CHSL
│       └── ...
└── BANK
    └── BANK_PO
        ├── PRE (SubCategory)
        └── MAIN (SubCategory)
```

---

## 4. Question Entity Structure

### 4.1 MongoDB Collection: `questionBank`

```javascript
{
  "_id": ObjectId,
  "questionId": String,           // Unique identifier (auto-generated)

  // Taxonomy
  "paperType": "SSC" | "BANK",
  "paperCategory": "SSC_CGL" | "SSC_CPO" | "SSC_CHSL" | "BANK_PO",
  "paperSubCategory": "SSC_CGL_TIER1" | "SSC_CGL_TIER2" | ...,
  "subject": String,              // e.g., "General Intelligence and Reasoning"
  "topic": String,                // e.g., "Analogies", "Coding-Decoding"
  "subTopic": String,             // Optional: e.g., "Letter Analogies"

  // Question Content
  "problem": {
    "question": String,           // Question text (HTML supported)
    "questionHindi": String,      // Hindi translation (optional)
    "options": [
      {
        "key": "A",
        "value": String,          // Option text (HTML supported)
        "valueHindi": String      // Hindi translation (optional)
      },
      // ... up to 4-5 options
    ],
    "correctOption": String,      // "A", "B", "C", or "D"
    "solution": String,           // Explanation (HTML supported)
    "solutionHindi": String       // Hindi translation (optional)
  },

  // Metadata
  "questionType": "SINGLE" | "MULTIPLE",  // Single or multiple correct answers
  "difficultyLevel": "EASY" | "MEDIUM" | "HARD",
  "marks": Number,                // Default marks for this question
  "negativeMarks": Number,        // Negative marking (default 0.25 or 0.5)
  "averageTime": Number,          // Expected time in seconds

  // Media
  "hasImage": Boolean,
  "imageUrl": String,             // If question has image
  "imagePosition": "INLINE" | "BELOW_QUESTION" | "IN_OPTIONS",

  // Status & Audit
  "status": "DRAFT" | "PENDING_REVIEW" | "APPROVED" | "REJECTED" | "ARCHIVED",
  "createdBy": String,            // userId of creator (Teacher/Admin)
  "createdByName": String,        // Display name
  "createdAt": Timestamp,
  "updatedBy": String,
  "updatedAt": Timestamp,
  "approvedBy": String,           // userId of approver (Admin/SuperAdmin)
  "approvedAt": Timestamp,
  "rejectionReason": String,      // If rejected

  // Usage Tracking
  "usageCount": Number,           // How many papers use this question
  "lastUsedAt": Timestamp,
  "papersUsedIn": [String],       // Array of paper IDs

  // Tags & Search
  "tags": [String],               // For search: ["algebra", "percentage", "2024"]
  "year": Number,                 // If from previous year paper
  "source": String                // e.g., "SSC CGL 2023 Shift 1"
}
```

---

## 5. API Endpoints

### 5.1 Question CRUD

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/admin/question-bank/create` | Create new question | TEACHER, ADMIN, SUPERADMIN |
| PUT | `/admin/question-bank/update/{id}` | Update question | Owner, ADMIN, SUPERADMIN |
| GET | `/admin/question-bank/{id}` | Get question by ID | TEACHER, ADMIN, SUPERADMIN |
| DELETE | `/admin/question-bank/delete/{id}` | Delete question | Owner (DRAFT only), ADMIN, SUPERADMIN |
| GET | `/admin/question-bank/list` | List questions (paginated, filtered) | TEACHER, ADMIN, SUPERADMIN |

### 5.2 Bulk Operations

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/admin/question-bank/bulk-create` | Create multiple questions | ADMIN, SUPERADMIN |
| POST | `/admin/question-bank/bulk-update-status` | Approve/Reject multiple | ADMIN, SUPERADMIN |
| POST | `/admin/question-bank/import/csv` | Import from CSV | ADMIN, SUPERADMIN |
| GET | `/admin/question-bank/export/csv` | Export to CSV | ADMIN, SUPERADMIN |

### 5.3 Approval Workflow

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| PUT | `/admin/question-bank/submit-for-review/{id}` | Teacher submits for review | TEACHER (owner) |
| PUT | `/admin/question-bank/approve/{id}` | Approve question | ADMIN, SUPERADMIN |
| PUT | `/admin/question-bank/reject/{id}` | Reject with reason | ADMIN, SUPERADMIN |

### 5.4 Statistics & Search

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/admin/question-bank/stats` | Get question bank statistics | ADMIN, SUPERADMIN |
| GET | `/admin/question-bank/search` | Full-text search | TEACHER, ADMIN, SUPERADMIN |
| GET | `/admin/question-bank/subjects` | Get subjects for category | ALL |
| GET | `/admin/question-bank/topics/{subject}` | Get topics for subject | ALL |

---

## 6. Frontend Pages

### 6.1 Question Bank Dashboard (`/admin/question-bank`)

**For TEACHER:**
- Stats: My Questions (Draft, Pending, Approved, Rejected)
- Quick actions: Create Question, View My Questions
- Recent activity

**For ADMIN/SUPERADMIN:**
- Overall stats: Total questions by category, status, subject
- Pending approvals count
- Questions added this week/month
- Top contributors (Teachers)

### 6.2 Question List (`/admin/question-bank/list`)

**Features:**
- Filterable table with columns:
  - Question ID
  - Subject / Topic
  - Category / SubCategory
  - Difficulty
  - Status
  - Created By
  - Created At
  - Actions (Edit, View, Approve, Reject, Delete)
- Filters:
  - Paper Type (SSC/BANK)
  - Category (SSC_CGL, SSC_CPO, etc.)
  - SubCategory (TIER-1, TIER-2)
  - Subject
  - Topic
  - Difficulty Level
  - Status
  - Created By (for Admin)
  - Date Range
- Search by question text, tags
- Bulk selection and actions
- Pagination

### 6.3 Create/Edit Question (`/admin/question-bank/create`, `/admin/question-bank/edit/[id]`)

**Form Fields:**
1. **Taxonomy Selection**
   - Paper Type dropdown
   - Category dropdown (filtered by Paper Type)
   - SubCategory dropdown (filtered by Category)
   - Subject dropdown (filtered by SubCategory)
   - Topic input/dropdown (with autocomplete from existing topics)
   - SubTopic input (optional)

2. **Question Content**
   - Question text (Rich text editor with HTML support)
   - Question Hindi text (optional, expandable)
   - Add image button (upload or URL)

3. **Options**
   - 4 option fields (A, B, C, D)
   - Each with English and Hindi text
   - Radio button to select correct answer
   - Option to add 5th option if needed

4. **Solution**
   - Explanation text (Rich text editor)
   - Explanation Hindi (optional)

5. **Metadata**
   - Difficulty Level (Easy/Medium/Hard)
   - Question Type (Single/Multiple correct)
   - Marks (default based on category)
   - Negative marks
   - Expected time (seconds)
   - Tags (comma-separated or chips)
   - Source/Year (optional)

6. **Actions**
   - Save as Draft
   - Submit for Review (Teacher)
   - Save & Approve (Admin/SuperAdmin)
   - Preview

### 6.4 Question Preview Modal

- Shows question as it would appear in exam
- Toggle English/Hindi
- Shows correct answer highlighted
- Shows solution

### 6.5 Pending Approvals (`/admin/question-bank/pending`)

**For ADMIN/SUPERADMIN:**
- List of questions pending review
- Quick approve/reject actions
- Bulk approve/reject
- Filter by subject, teacher

---

## 7. Paper Creation Integration

### 7.1 Select Questions from Bank

When creating a paper, allow:
1. **Manual Selection**
   - Browse question bank
   - Filter by subject, topic, difficulty
   - Add individual questions to paper

2. **Auto-Generation**
   - Specify: Subject, Topic, Difficulty distribution, Count
   - System randomly selects approved questions
   - Option to regenerate or swap questions

3. **Import from Previous Paper**
   - Select existing paper
   - Import questions (creates copies in bank if not exists)

---

## 8. Database Indexes

```javascript
// Primary indexes
{ "questionId": 1 }
{ "paperType": 1, "paperCategory": 1, "paperSubCategory": 1 }
{ "subject": 1, "topic": 1 }
{ "status": 1 }
{ "createdBy": 1 }
{ "createdAt": -1 }

// Compound indexes for common queries
{ "paperCategory": 1, "subject": 1, "status": 1 }
{ "status": 1, "createdAt": -1 }

// Text index for search
{ "problem.question": "text", "tags": "text" }
```

---

## 9. Implementation Phases

### Phase 1: Core Question Bank (MVP)
- [ ] Backend: Question model & repository
- [ ] Backend: CRUD APIs
- [ ] Backend: List with filters & pagination
- [ ] Frontend: Question list page
- [ ] Frontend: Create/Edit question form
- [ ] Frontend: Basic search & filters

### Phase 2: Approval Workflow
- [ ] Backend: Status management APIs
- [ ] Backend: Approval/Rejection workflow
- [ ] Frontend: Pending approvals page
- [ ] Frontend: Status badges & actions
- [ ] Notifications for status changes

### Phase 3: Advanced Features
- [ ] Bulk import/export (CSV)
- [ ] Rich text editor with image upload
- [ ] Hindi translation support
- [ ] Full-text search
- [ ] Usage tracking

### Phase 4: Paper Integration
- [ ] Question picker in paper creation
- [ ] Auto-generation from bank
- [ ] Question usage analytics

---

## 10. Subject & Topic Master Data

### SSC CGL TIER-1 Subjects & Topics

**1. General Intelligence and Reasoning**
- Analogies (Semantic, Symbolic, Figural)
- Classification
- Series (Number, Letter, Figural)
- Coding-Decoding
- Blood Relations
- Direction & Distance
- Order & Ranking
- Syllogism
- Venn Diagrams
- Puzzles
- Statement & Conclusions
- Non-verbal Reasoning (Paper Folding, Mirror Image, etc.)

**2. General Awareness**
- Static GK (History, Geography, Polity, Economy)
- Current Affairs
- Science (Physics, Chemistry, Biology)
- Computer Awareness
- Sports
- Awards & Honours
- Books & Authors

**3. Quantitative Aptitude**
- Number System
- Simplification & Approximation
- Percentage
- Ratio & Proportion
- Average
- Profit & Loss
- Simple & Compound Interest
- Time & Work
- Time, Speed & Distance
- Algebra
- Geometry
- Mensuration
- Trigonometry
- Data Interpretation

**4. English Comprehension**
- Reading Comprehension
- Cloze Test
- Error Spotting
- Sentence Improvement
- Fill in the Blanks
- Synonyms & Antonyms
- One Word Substitution
- Idioms & Phrases
- Spelling Correction
- Active/Passive Voice
- Direct/Indirect Speech

---

## 11. UI Mockup References

### Question Bank List View
```
┌────────────────────────────────────────────────────────────────────┐
│ Question Bank                                    [+ Create Question]│
├────────────────────────────────────────────────────────────────────┤
│ Filters:                                                           │
│ [Paper Type ▼] [Category ▼] [Subject ▼] [Difficulty ▼] [Status ▼] │
│ [Search questions...                                    ] [Search] │
├────────────────────────────────────────────────────────────────────┤
│ ☐ │ ID      │ Question           │ Subject    │ Diff │ Status    │
│───┼─────────┼────────────────────┼────────────┼──────┼───────────│
│ ☐ │ QB-001  │ What is the cap... │ GK         │ Easy │ ✓ Approved│
│ ☐ │ QB-002  │ Solve: 2x + 3 =... │ Quant      │ Med  │ ⏳ Pending │
│ ☐ │ QB-003  │ Find the odd on... │ Reasoning  │ Hard │ 📝 Draft  │
└────────────────────────────────────────────────────────────────────┘
```

### Create Question Form
```
┌────────────────────────────────────────────────────────────────────┐
│ Create New Question                                                │
├────────────────────────────────────────────────────────────────────┤
│ ┌─ Classification ─────────────────────────────────────────────┐  │
│ │ Paper Type: [SSC ▼]     Category: [SSC_CGL ▼]               │  │
│ │ SubCategory: [TIER-1 ▼] Subject: [Quantitative Aptitude ▼]  │  │
│ │ Topic: [Percentage              ] Difficulty: [Medium ▼]    │  │
│ └───────────────────────────────────────────────────────────────┘  │
│                                                                    │
│ ┌─ Question ───────────────────────────────────────────────────┐  │
│ │ ┌──────────────────────────────────────────────────────────┐ │  │
│ │ │ Rich Text Editor                                         │ │  │
│ │ │ If 20% of a number is 40, then the number is:           │ │  │
│ │ └──────────────────────────────────────────────────────────┘ │  │
│ │ [+ Add Hindi Translation]  [📷 Add Image]                    │  │
│ └───────────────────────────────────────────────────────────────┘  │
│                                                                    │
│ ┌─ Options ────────────────────────────────────────────────────┐  │
│ │ ○ A: [200                    ]                               │  │
│ │ ● B: [200 ✓ Correct          ]                               │  │
│ │ ○ C: [400                    ]                               │  │
│ │ ○ D: [100                    ]                               │  │
│ └───────────────────────────────────────────────────────────────┘  │
│                                                                    │
│ ┌─ Solution ───────────────────────────────────────────────────┐  │
│ │ Let the number be x.                                         │  │
│ │ 20% of x = 40                                                │  │
│ │ x = 40 × (100/20) = 200                                      │  │
│ └───────────────────────────────────────────────────────────────┘  │
│                                                                    │
│ Tags: [percentage] [basic] [tier-1] [+ Add]                       │
│                                                                    │
│ [Save as Draft]  [Submit for Review]  [Preview]                   │
└────────────────────────────────────────────────────────────────────┘
```

---

## 12. Success Metrics

- Total questions in bank
- Questions by category/subject
- Questions approved per week
- Average time to approval
- Questions used in papers
- Teacher contribution leaderboard
- Question quality score (based on user performance data)
