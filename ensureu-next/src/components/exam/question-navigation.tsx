'use client';

import { useExamStore } from '@/stores/exam-store';
import { cn } from '@/lib/utils';
import type { Question } from '@/types/paper';

interface QuestionStatus {
  answered: boolean;
  markedForReview: boolean;
  visited: boolean;
}

function getQuestionStatus(question: Question): QuestionStatus {
  const problem = question.problem;
  const hasSelectedOption = problem.options.some((opt) => opt.selected);

  return {
    answered: hasSelectedOption,
    markedForReview: false, // Will be implemented with ingest data
    visited: question.timeTakenInSecond > 0,
  };
}

function getStatusColor(status: QuestionStatus, isCurrent: boolean): string {
  if (isCurrent) {
    return 'ring-2 ring-primary ring-offset-2';
  }
  if (status.markedForReview && status.answered) {
    return 'bg-purple-500 text-white';
  }
  if (status.markedForReview) {
    return 'bg-purple-200 text-purple-800';
  }
  if (status.answered) {
    return 'bg-green-500 text-white';
  }
  if (status.visited) {
    return 'bg-red-400 text-white';
  }
  return 'bg-slate-200 text-slate-600';
}

export function QuestionNavigation() {
  const paperData = useExamStore((state) => state.paperData);
  const currentSection = useExamStore((state) => state.currentSection);
  const currentQuestionIndex = useExamStore((state) => state.currentQuestionIndex);
  const setCurrentQuestion = useExamStore((state) => state.setCurrentQuestion);

  if (!paperData || !currentSection) return null;

  const questions =
    currentSection.questionData?.questions ||
    currentSection.subSections?.[0]?.questionData?.questions ||
    [];

  // Calculate statistics
  const stats = questions.reduce(
    (acc, q) => {
      const status = getQuestionStatus(q);
      if (status.answered) acc.answered++;
      else if (status.visited) acc.notAnswered++;
      else acc.notVisited++;
      if (status.markedForReview) acc.markedForReview++;
      return acc;
    },
    { answered: 0, notAnswered: 0, notVisited: 0, markedForReview: 0 }
  );

  return (
    <div className="space-y-4">
      {/* Legend */}
      <div className="grid grid-cols-2 gap-2 text-xs">
        <div className="flex items-center gap-2">
          <span className="w-4 h-4 rounded bg-green-500" />
          <span>Answered ({stats.answered})</span>
        </div>
        <div className="flex items-center gap-2">
          <span className="w-4 h-4 rounded bg-red-400" />
          <span>Not Answered ({stats.notAnswered})</span>
        </div>
        <div className="flex items-center gap-2">
          <span className="w-4 h-4 rounded bg-slate-200" />
          <span>Not Visited ({stats.notVisited})</span>
        </div>
        <div className="flex items-center gap-2">
          <span className="w-4 h-4 rounded bg-purple-500" />
          <span>Marked ({stats.markedForReview})</span>
        </div>
      </div>

      {/* Question Grid */}
      <div className="grid grid-cols-5 gap-2">
        {questions.map((question, index) => {
          const status = getQuestionStatus(question);
          const isCurrent = index === currentQuestionIndex;

          return (
            <button
              key={question.id || index}
              onClick={() => setCurrentQuestion(index)}
              className={cn(
                'w-10 h-10 rounded-lg text-sm font-medium transition-all hover:scale-105',
                getStatusColor(status, isCurrent)
              )}
            >
              {question.customQNo || index + 1}
            </button>
          );
        })}
      </div>
    </div>
  );
}
