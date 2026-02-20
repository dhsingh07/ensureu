# SSC Paper CSV Upload Format

## Overview
This document describes the CSV format for uploading complete SSC papers to the EnsureU platform.

## CSV Structure

Each row represents **one question**. Paper-level and section-level metadata are repeated on each row.

### Column Definitions

| Column # | Column Name | Required | Description | Example Values |
|----------|-------------|----------|-------------|----------------|
| 1 | paperType | Yes | Paper type enum | SSC, BANK |
| 2 | paperCategory | Yes | Paper category enum | SSC_CGL, SSC_CPO, SSC_CHSL |
| 3 | paperSubCategory | Yes | Paper subcategory enum | SSC_CGL_TIER1, SSC_CGL_TIER2 |
| 4 | paperName | Yes | Display name of the paper | "ssc-tier-paper-2" |
| 5 | sectionName | Yes | Section title | "English Language" |
| 6 | SectionType | Yes | Section type enum | EnglishLanguage, QuantitativeAptitude, GeneralIntelligence, GeneralAwareness, Statistics |
| 7 | subSectionName | No | Sub-topic name (optional) | "Error Spotting Grammer" |
| 8 | questionNumber | Yes | Question sequence number | 1, 2, 3... |
| 9 | question | Yes | Question text (HTML allowed) | "In the following question..." |
| 10 | questionImage | No | Question image URL | |
| 11 | option1 | Yes | Option 1 text | "1" or actual option text |
| 12 | option1_Image | No | Option 1 image URL | |
| 13 | option2 | Yes | Option 2 text | "2" |
| 14 | option2_Image | No | Option 2 image URL | |
| 15 | option3 | Yes | Option 3 text | "3" |
| 16 | option3_Image | No | Option 3 image URL | |
| 17 | option4 | Yes | Option 4 text | "No Error" |
| 18 | option4_Image | No | Option 4 image URL | |
| 19 | correctOption | Yes | Correct answer (1, 2, 3, or 4) | 2 |
| 20 | answerDescription1 | No | Solution/explanation text (HTML) | "No sooner is used to show..." |
| 21 | answerDescriptionImage | No | Solution image URL | |
| 22 | complexityLevel | No | Difficulty level | easy, medium, hard |
| 23 | complexityScore | No | Complexity score | 5 |
| 24 | type | No | Question type | mcq |
| 25 | totalScore | No | Total paper score | 200 |

### Optional Columns (for paper settings)

| Column Name | Default | Description |
|-------------|---------|-------------|
| testType | FREE | FREE or PAID |
| totalTimeMinutes | 60 | Total paper duration in minutes |
| perQuestionMarks | 2 | Marks for correct answer |
| negativeMarks | 0.5 | Marks deducted for wrong answer |

---

## Enum Values Reference

### paperType
- `SSC` - Staff Selection Commission
- `BANK` - Banking Exams

### paperCategory
- `SSC_CGL` - SSC Combined Graduate Level
- `SSC_CPO` - SSC Central Police Organisation
- `SSC_CHSL` - SSC Combined Higher Secondary Level
- `BANK_PO` - Bank Probationary Officer

### paperSubCategory
- `SSC_CGL_TIER1` - SSC CGL Tier-1
- `SSC_CGL_TIER2` - SSC CGL Tier-2
- `SSC_CHSL_TIER1` - SSC CHSL Tier-1
- `SSC_CHSL_TIER2` - SSC CHSL Tier-2
- `SSC_CPO_TIER1` - SSC CPO Tier-1
- `SSC_CPO_TIER2` - SSC CPO Tier-2

### SectionType
- `GeneralIntelligence` - General Intelligence & Reasoning
- `QuantitativeAptitude` - Quantitative Aptitude / Maths
- `EnglishLanguage` - English Language & Comprehension
- `GeneralAwareness` - General Awareness / GK
- `Statistics` - Statistics (for Tier-2)

### complexityLevel
- `easy`
- `medium`
- `hard`

---

## Sample CSV

```csv
paperType,paperCategory,paperSubCategory,paperName,sectionName,SectionType,subSectionName,questionNumber,question,questionImage,option1,option1_Image,option2,option2_Image,option3,option3_Image,option4,option4_Image,correctOption,answerDescription1,answerDescriptionImage,complexityLevel,complexityScore,type,totalScore
SSC,SSC_CGL,SSC_CGL_TIER1,ssc-tier-paper-2,English Language,EnglishLanguage,Error Spotting Grammer,1,"In the following question, some part of the sentence may have errors. Find out which part of the sentence has an error and select the appropriate option. If a sentence is free from error, select 'No Error'.

No sooner did I come out of my home (1)/ when it started raining heavily (2)/ which drenched me completely. (3)/ No error",,1,,2,,3,,No Error,,2,"No sooner is used to show that one thing happened immediately after another thing. It is often used with the past indefinite or past perfect, and usually followed by than.<br><br>The correct sentence would be:<br><br>No sooner did I come out of my home than it started raining heavily which drenched me completely.",,medium,5,mcq,200
SSC,SSC_CGL,SSC_CGL_TIER1,ssc-tier-paper-2,English Language,EnglishLanguage,Error Spotting Grammer,2,"Find the error: (A) He is one of / (B) those students who / (C) has passed / (D) the examination.",,A,,B,,C,,D,,3,"'who' refers to 'students' (plural), so it should be 'have passed' instead of 'has passed'.",,medium,5,mcq,200
SSC,SSC_CGL,SSC_CGL_TIER1,ssc-tier-paper-2,Quantitative Aptitude,QuantitativeAptitude,Arithmetic,3,"If 15% of a number is 45, what is the number?",,300,,250,,200,,350,,1,"Let the number be x. 15% of x = 45. x = 45 × 100/15 = 300",,easy,5,mcq,200
```

---

## Processing Rules

1. **Paper Identification**: Papers are grouped by `paperName + paperCategory`
2. **Test Type**: If `testType` column is not present, defaults to FREE
3. **Section Grouping**: Questions with the same `sectionName` are grouped into one section
4. **SubSection Grouping**: If `subSectionName` is provided, questions are further grouped
5. **Question Ordering**: Questions are ordered by `questionNumber`
6. **Correct Option**: Use numeric values (1, 2, 3, 4) for `correctOption`

---

## Upload API

**Endpoint:** `POST /admin/paper/upload/csv`

**Request:**
- Content-Type: `multipart/form-data`
- Body: `file` (CSV file)

**Response:**
```json
{
  "status": 1,
  "message": "Paper uploaded successfully",
  "body": {
    "paperId": "csv_1234567890",
    "paperName": "ssc-tier-paper-2",
    "testType": "FREE",
    "totalQuestions": 100,
    "sections": 4
  }
}
```

---

## Notes

1. **HTML Support**: Question text, options, and answer descriptions can contain HTML for formatting (`<br>`, `<strong>`, `&nbsp;`, etc.)
2. **Images**: If image URLs are provided, they should be accessible URLs or will be resolved relative to the storage service
3. **Unicode**: Full UTF-8 encoding supported for special characters
4. **Excel Export**: When saving from Excel, use "CSV UTF-8 (Comma delimited)" format
5. **Large Papers**: Papers with 100+ questions are supported
