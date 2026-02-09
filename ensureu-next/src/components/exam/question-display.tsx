'use client';

import { useExamStore } from '@/stores/exam-store';
import { cn } from '@/lib/utils';
import { Languages, ZoomIn, ZoomOut } from 'lucide-react';
import { Button } from '@/components/ui/button';
import type { QuestionType } from '@/types/paper';

interface QuestionDisplayProps {
  readOnly?: boolean;
}

export function QuestionDisplay({ readOnly = false }: QuestionDisplayProps) {
  const currentQuestion = useExamStore((state) => state.currentQuestion);
  const currentQuestionIndex = useExamStore((state) => state.currentQuestionIndex);
  const isHindi = useExamStore((state) => state.isHindi);
  const fontSize = useExamStore((state) => state.fontSize);
  const toggleLanguage = useExamStore((state) => state.toggleLanguage);
  const updateFontSize = useExamStore((state) => state.updateFontSize);
  const selectOption = useExamStore((state) => state.selectOption);

  if (!currentQuestion) {
    return (
      <div className="flex items-center justify-center h-64 text-slate-500">
        No question available
      </div>
    );
  }

  const problem = isHindi && currentQuestion.problemHindi
    ? currentQuestion.problemHindi
    : currentQuestion.problem;

  const questionType: QuestionType = currentQuestion.questionType || 'RADIOBUTTON';
  const hasHindiVersion = !!currentQuestion.problemHindi;

  const handleOptionSelect = (index: number) => {
    if (readOnly) return;
    selectOption(index, questionType);
  };

  return (
    <div className="space-y-6">
      {/* Question Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="px-3 py-1 bg-primary text-white rounded-full text-sm font-medium">
            Q{currentQuestion.customQNo || currentQuestionIndex + 1}
          </span>
          <span className="text-sm text-slate-500">
            {questionType === 'CHECKBOX' ? 'Multiple Select' : 'Single Select'}
          </span>
        </div>
        <div className="flex items-center gap-2">
          {hasHindiVersion && (
            <Button
              variant="outline"
              size="sm"
              onClick={toggleLanguage}
              className="gap-1"
            >
              <Languages className="h-4 w-4" />
              {isHindi ? 'English' : 'Hindi'}
            </Button>
          )}
          <Button
            variant="outline"
            size="icon"
            onClick={() => updateFontSize(-2)}
            disabled={fontSize <= 6}
          >
            <ZoomOut className="h-4 w-4" />
          </Button>
          <Button
            variant="outline"
            size="icon"
            onClick={() => updateFontSize(2)}
            disabled={fontSize >= 18}
          >
            <ZoomIn className="h-4 w-4" />
          </Button>
        </div>
      </div>

      {/* Question Text */}
      <div
        className="p-4 bg-slate-50 rounded-lg"
        style={{ fontSize: `${fontSize}px` }}
      >
        <div
          className="prose max-w-none"
          dangerouslySetInnerHTML={{ __html: problem.question }}
        />
      </div>

      {/* Options */}
      <div className="space-y-3">
        {problem.options.map((option, index) => (
          <button
            key={index}
            onClick={() => handleOptionSelect(index)}
            disabled={readOnly}
            className={cn(
              'w-full p-4 text-left rounded-lg border-2 transition-all',
              option.selected
                ? 'border-primary bg-primary/5'
                : 'border-slate-200 hover:border-slate-300 hover:bg-slate-50'
            )}
            style={{ fontSize: `${fontSize}px` }}
          >
            <div className="flex items-start gap-3">
              <span
                className={cn(
                  'flex-shrink-0 w-8 h-8 flex items-center justify-center rounded-full border-2 font-medium',
                  option.selected
                    ? 'border-primary bg-primary text-white'
                    : 'border-slate-300 text-slate-600'
                )}
              >
                {option.prompt || String.fromCharCode(65 + index)}
              </span>
              <div className="flex-1">
                {option.imageUrl ? (
                  <img
                    src={option.imageUrl}
                    alt={`Option ${option.prompt || index + 1}`}
                    className="max-w-full h-auto rounded"
                  />
                ) : (
                  <div
                    className="prose max-w-none"
                    dangerouslySetInnerHTML={{ __html: option.text }}
                  />
                )}
              </div>
            </div>
          </button>
        ))}
      </div>

      {/* Question Info */}
      <div className="flex items-center gap-4 text-sm text-slate-500 pt-4 border-t">
        <span>Marks: +{currentQuestion.score || 2}</span>
        <span>Negative: -{currentQuestion.score ? currentQuestion.score * 0.25 : 0.5}</span>
        {currentQuestion.sectionTitle && (
          <span>Section: {currentQuestion.sectionTitle}</span>
        )}
      </div>
    </div>
  );
}
