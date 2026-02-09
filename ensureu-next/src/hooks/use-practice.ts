import { useQuery } from '@tanstack/react-query';
import { get } from '@/lib/api/client';
import { API_URLS } from '@/lib/constants/api-urls';
import type { ApiResponse } from '@/types/api';
import type { PaperCategory } from '@/types/paper';
import type { PracticeQuestionGroup, PracticeSectionInfo } from '@/types/practice';

const DEMO_MODE = process.env.NEXT_PUBLIC_DEMO_MODE === 'true';

// MongoDB aggregation response format from backend
interface MongoSectionResult {
  _id: {
    sectionTitle: string;
    subSectionTitle: string;
  };
  questionCount: number;
}

interface MongoQuestionResult {
  _id: {
    sectionTitle: string;
    subSectionTitle: string;
  };
  questionCount: number;
  questions: Array<{
    _id: string;
    problem: {
      value: string;
      options: Array<{ prompt: string; value: string }>;
      co: string;
      solutions?: Array<{ value: string }>;
    };
  }>;
}

// Transform MongoDB result to flat format
function transformSectionResult(result: MongoSectionResult): PracticeSectionInfo {
  return {
    sectionTitle: result._id?.sectionTitle || '',
    subSectionTitle: result._id?.subSectionTitle || '',
    questionCount: result.questionCount || 0,
  };
}

function transformQuestionResult(result: MongoQuestionResult): PracticeQuestionGroup {
  return {
    sectionTitle: result._id?.sectionTitle || '',
    subSectionTitle: result._id?.subSectionTitle || '',
    questionCount: result.questionCount || 0,
    // Ensure co is always a string for comparison with option.prompt
    questions: (result.questions || []).map((q) => ({
      ...q,
      problem: {
        ...q.problem,
        co: String(q.problem.co), // Convert number to string
      },
    })),
  };
}

const demoSections: PracticeSectionInfo[] = [
  { sectionTitle: 'General Intelligence', subSectionTitle: 'Analogy', questionCount: 12 },
  { sectionTitle: 'General Intelligence', subSectionTitle: 'Classification', questionCount: 10 },
  { sectionTitle: 'English Language', subSectionTitle: 'Grammar', questionCount: 14 },
  { sectionTitle: 'Quantitative Aptitude', subSectionTitle: 'Percentage', questionCount: 8 },
];

const demoQuestions: PracticeQuestionGroup[] = [
  {
    sectionTitle: 'General Intelligence',
    subSectionTitle: 'Analogy',
    questionCount: 3,
    questions: [
      {
        _id: 'demo-q-1',
        problem: {
          value: 'Book is to Reading as Fork is to ____.',
          options: [
            { prompt: 'A', value: 'Drawing' },
            { prompt: 'B', value: 'Writing' },
            { prompt: 'C', value: 'Stirring' },
            { prompt: 'D', value: 'Eating' },
          ],
          co: 'D',
          solutions: [{ value: 'Fork is used for eating.' }],
        },
      },
      {
        _id: 'demo-q-2',
        problem: {
          value: 'Bird is to Fly as Fish is to ____.',
          options: [
            { prompt: 'A', value: 'Swim' },
            { prompt: 'B', value: 'Run' },
            { prompt: 'C', value: 'Jump' },
            { prompt: 'D', value: 'Dig' },
          ],
          co: 'A',
          solutions: [{ value: 'Fish swim in water.' }],
        },
      },
      {
        _id: 'demo-q-3',
        problem: {
          value: 'Pen is to Write as Knife is to ____.',
          options: [
            { prompt: 'A', value: 'Cut' },
            { prompt: 'B', value: 'Paint' },
            { prompt: 'C', value: 'Eat' },
            { prompt: 'D', value: 'Drive' },
          ],
          co: 'A',
          solutions: [{ value: 'Knife is used to cut.' }],
        },
      },
    ],
  },
];

export function usePracticeSections(category: PaperCategory) {
  return useQuery({
    queryKey: ['practice', 'sections', category],
    queryFn: async () => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 200));
        return demoSections;
      }

      const response = await get<MongoSectionResult[] | ApiResponse<MongoSectionResult[]>>(
        `${API_URLS.PRACTICE.COUNT}/${category}`
      );

      // Handle both response formats and transform MongoDB result
      let results: MongoSectionResult[] = [];
      if (Array.isArray(response)) {
        results = response;
      } else if ('body' in response && response.body) {
        results = response.body;
      }

      // Transform MongoDB aggregation results to flat format
      return results.map(transformSectionResult);
    },
    enabled: !!category,
    staleTime: 5 * 60 * 1000,
  });
}

export function usePracticeQuestions(
  category: PaperCategory,
  sectionTitle?: string,
  subSectionTitle?: string,
  enabled = true
) {
  return useQuery({
    queryKey: ['practice', 'questions', category, sectionTitle, subSectionTitle],
    queryFn: async () => {
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 200));
        return demoQuestions;
      }

      if (!sectionTitle || !subSectionTitle) {
        return [];
      }
      const url = `${API_URLS.PRACTICE.QUESTIONS_COUNT}/${category}?sectionTitle=${encodeURIComponent(
        sectionTitle
      )}&subSectionTitle=${encodeURIComponent(subSectionTitle)}`;
      const response = await get<MongoQuestionResult[] | ApiResponse<MongoQuestionResult[]>>(url);

      // Handle both response formats and transform MongoDB result
      let results: MongoQuestionResult[] = [];
      if (Array.isArray(response)) {
        results = response;
      } else if ('body' in response && response.body) {
        results = response.body;
      }

      // Transform MongoDB aggregation results to flat format
      return results.map(transformQuestionResult);
    },
    enabled: !!category && !!sectionTitle && !!subSectionTitle && enabled,
    staleTime: 5 * 60 * 1000,
  });
}

