'use client';

import { Button } from '@/components/ui/button';
import { useExamStore } from '@/stores/exam-store';
import {
  ChevronLeft,
  ChevronRight,
  Bookmark,
  RotateCcw,
  Save,
} from 'lucide-react';

interface ExamControlsProps {
  onSave?: () => void;
  onSubmit: () => void;
  isSaving?: boolean;
  readOnly?: boolean;
}

export function ExamControls({ onSave, onSubmit, isSaving, readOnly = false }: ExamControlsProps) {
  const nextQuestion = useExamStore((state) => state.nextQuestion);
  const previousQuestion = useExamStore((state) => state.previousQuestion);
  const clearResponse = useExamStore((state) => state.clearResponse);
  const markForReview = useExamStore((state) => state.markForReview);
  const currentQuestionIndex = useExamStore((state) => state.currentQuestionIndex);
  const currentSection = useExamStore((state) => state.currentSection);

  const questions =
    currentSection?.questionData?.questions ||
    currentSection?.subSections?.[0]?.questionData?.questions ||
    [];

  const isFirstQuestion = currentQuestionIndex === 0;
  const isLastQuestion = currentQuestionIndex === questions.length - 1;

  return (
    <div className="flex flex-wrap items-center justify-between gap-4 pt-4 border-t">
      {/* Left Controls */}
      <div className="flex items-center gap-2">
        <Button
          variant="outline"
          size="sm"
          onClick={clearResponse}
          disabled={readOnly}
          className="gap-1"
        >
          <RotateCcw className="h-4 w-4" />
          Clear
        </Button>
        <Button
          variant="outline"
          size="sm"
          onClick={markForReview}
          disabled={readOnly}
          className="gap-1"
        >
          <Bookmark className="h-4 w-4" />
          Mark for Review
        </Button>
      </div>

      {/* Center Controls - Navigation */}
      <div className="flex items-center gap-2">
        <Button
          variant="outline"
          onClick={previousQuestion}
          disabled={isFirstQuestion}
          className="gap-1"
        >
          <ChevronLeft className="h-4 w-4" />
          Previous
        </Button>
        <Button
          variant="outline"
          onClick={nextQuestion}
          disabled={isLastQuestion}
          className="gap-1"
        >
          Next
          <ChevronRight className="h-4 w-4" />
        </Button>
      </div>

      {/* Right Controls */}
      <div className="flex items-center gap-2">
        {onSave && !readOnly && (
          <Button
            variant="outline"
            size="sm"
            onClick={onSave}
            disabled={isSaving}
            className="gap-1"
          >
            <Save className="h-4 w-4" />
            {isSaving ? 'Saving...' : 'Save'}
          </Button>
        )}
        {!readOnly && (
          <Button onClick={onSubmit} className="bg-green-600 hover:bg-green-700">
            Submit Test
          </Button>
        )}
      </div>
    </div>
  );
}
