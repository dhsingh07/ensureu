'use client';

import { useState, useCallback } from 'react';
import { Lightbulb, Loader2, ChevronRight, Lock } from 'lucide-react';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogFooter,
} from '@/components/ui/dialog';
import { Progress } from '@/components/ui/progress';
import { useQuestionHint, useCachedHint } from '@/hooks/use-ai';
import type { HintResponse } from '@/types/ai';
import { cn } from '@/lib/utils';

interface AIHintProps {
  questionId: string;
  questionText: string;
  topic?: string;
  correctAnswer?: string;
  solution?: string;
  maxHints?: number;
  trigger?: React.ReactNode;
  disabled?: boolean;
}

export function AIHintDialog({
  questionId,
  questionText,
  topic,
  correctAnswer,
  solution,
  maxHints = 3,
  trigger,
  disabled = false,
}: AIHintProps) {
  const [open, setOpen] = useState(false);
  const [currentLevel, setCurrentLevel] = useState(1);
  const [hints, setHints] = useState<HintResponse[]>([]);
  const { mutate, isPending } = useQuestionHint();

  // Try to get cached hints
  const { data: cachedHint1 } = useCachedHint(questionId, 1);
  const { data: cachedHint2 } = useCachedHint(questionId, 2);
  const { data: cachedHint3 } = useCachedHint(questionId, 3);

  const allHints = hints.length > 0 ? hints : [cachedHint1, cachedHint2, cachedHint3].filter(Boolean) as HintResponse[];

  const handleGetHint = useCallback(() => {
    mutate(
      {
        questionId,
        questionText,
        topic,
        correctAnswer,
        solution,
        hintLevel: currentLevel,
      },
      {
        onSuccess: (data) => {
          if (data) {
            setHints((prev) => {
              const existing = prev.find((h) => h.hintLevel === data.hintLevel);
              if (existing) return prev;
              return [...prev, data].sort((a, b) => a.hintLevel - b.hintLevel);
            });
            setCurrentLevel(data.hintLevel + 1);
          }
        },
      }
    );
  }, [currentLevel, questionId, questionText, topic, correctAnswer, solution, mutate]);

  const handleOpen = (isOpen: boolean) => {
    setOpen(isOpen);
    if (isOpen && allHints.length === 0 && !isPending) {
      handleGetHint();
    }
  };

  const progressPercent = (allHints.length / maxHints) * 100;

  return (
    <Dialog open={open} onOpenChange={handleOpen}>
      <DialogTrigger asChild>
        {trigger || (
          <Button
            variant="outline"
            size="sm"
            className="gap-2 text-amber-600 border-amber-200 hover:bg-amber-50"
            disabled={disabled}
          >
            <Lightbulb className="h-4 w-4" />
            Get Hint
          </Button>
        )}
      </DialogTrigger>
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Lightbulb className="h-5 w-5 text-amber-500" />
            Progressive Hints
          </DialogTitle>
        </DialogHeader>

        <div className="space-y-4">
          {/* Progress indicator */}
          <div className="space-y-2">
            <div className="flex items-center justify-between text-sm">
              <span className="text-muted-foreground">Hints revealed</span>
              <span className="font-medium">{allHints.length} / {maxHints}</span>
            </div>
            <Progress value={progressPercent} className="h-2" />
          </div>

          {/* Hints list */}
          <div className="space-y-3">
            {[1, 2, 3].map((level) => {
              const hint = allHints.find((h) => h.hintLevel === level);
              const isLocked = !hint && level > allHints.length + 1;
              const isNext = !hint && level === allHints.length + 1;

              return (
                <div
                  key={level}
                  className={cn(
                    'p-4 rounded-lg border transition-all',
                    hint
                      ? 'bg-amber-50 border-amber-200'
                      : isLocked
                        ? 'bg-gray-50 border-gray-200'
                        : 'bg-white border-amber-300 border-dashed'
                  )}
                >
                  <div className="flex items-start gap-3">
                    <div
                      className={cn(
                        'w-6 h-6 rounded-full flex items-center justify-center text-xs font-medium',
                        hint
                          ? 'bg-amber-500 text-white'
                          : isLocked
                            ? 'bg-gray-300 text-gray-500'
                            : 'bg-amber-100 text-amber-600'
                      )}
                    >
                      {isLocked ? <Lock className="h-3 w-3" /> : level}
                    </div>
                    <div className="flex-1">
                      <div className="flex items-center justify-between mb-1">
                        <span
                          className={cn(
                            'text-sm font-medium',
                            hint ? 'text-amber-700' : 'text-gray-500'
                          )}
                        >
                          Hint Level {level}
                          {level === 1 && ' (Gentle nudge)'}
                          {level === 2 && ' (More specific)'}
                          {level === 3 && ' (Almost the answer)'}
                        </span>
                      </div>
                      {hint ? (
                        <p className="text-sm text-gray-700">{hint.hint}</p>
                      ) : isLocked ? (
                        <p className="text-sm text-gray-400">
                          Reveal previous hints first
                        </p>
                      ) : isNext && isPending ? (
                        <div className="flex items-center gap-2 text-sm text-amber-600">
                          <Loader2 className="h-4 w-4 animate-spin" />
                          Generating hint...
                        </div>
                      ) : (
                        <Button
                          variant="ghost"
                          size="sm"
                          className="text-amber-600 hover:text-amber-700 hover:bg-amber-100 -ml-2"
                          onClick={handleGetHint}
                          disabled={isPending}
                        >
                          <ChevronRight className="h-4 w-4 mr-1" />
                          Reveal hint
                        </Button>
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>

          {/* Warning message */}
          {allHints.length === maxHints && (
            <p className="text-sm text-center text-amber-600 bg-amber-50 p-3 rounded-lg">
              All hints revealed! Try solving the question now.
            </p>
          )}
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={() => setOpen(false)}>
            Continue solving
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

// Compact hint button for exam page
export function AIHintButton({
  questionId,
  questionText,
  topic,
  correctAnswer,
  solution,
  className,
}: AIHintProps & { className?: string }) {
  return (
    <AIHintDialog
      questionId={questionId}
      questionText={questionText}
      topic={topic}
      correctAnswer={correctAnswer}
      solution={solution}
      trigger={
        <Button
          variant="ghost"
          size="sm"
          className={cn('gap-1.5 text-amber-600 hover:text-amber-700 hover:bg-amber-50', className)}
        >
          <Lightbulb className="h-4 w-4" />
          Hint
        </Button>
      }
    />
  );
}
