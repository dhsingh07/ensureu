'use client';

import { useState } from 'react';
import { Sparkles, Lightbulb, BookOpen, AlertCircle, Loader2, ChevronRight } from 'lucide-react';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { useWrongAnswerExplanation, useCachedExplanation } from '@/hooks/use-ai';
import type { WrongAnswerRequest, WrongAnswerExplanation as ExplanationType } from '@/types/ai';

interface WrongAnswerExplanationProps {
  questionId: string;
  questionText: string;
  options: string[];
  userAnswer: number | number[];
  correctAnswer: number | number[];
  topic?: string;
  existingSolution?: string;
  trigger?: React.ReactNode;
  disabled?: boolean;
}

export function WrongAnswerExplanationDialog({
  questionId,
  questionText,
  options,
  userAnswer,
  correctAnswer,
  topic,
  existingSolution,
  trigger,
  disabled = false,
}: WrongAnswerExplanationProps) {
  const [open, setOpen] = useState(false);
  const { mutate, isPending, data } = useWrongAnswerExplanation();
  const { data: cachedData } = useCachedExplanation(questionId);

  const explanation = data || cachedData;

  const handleOpen = (isOpen: boolean) => {
    setOpen(isOpen);
    if (isOpen && !explanation && !isPending) {
      const request: WrongAnswerRequest = {
        questionId,
        questionText,
        options,
        userAnswer,
        correctAnswer,
        topic,
        existingSolution,
      };
      mutate(request);
    }
  };

  return (
    <Dialog open={open} onOpenChange={handleOpen}>
      <DialogTrigger asChild>
        {trigger || (
          <Button
            variant="outline"
            size="sm"
            className="gap-2 text-teal-600 border-teal-200 hover:bg-teal-50"
            disabled={disabled}
          >
            <Sparkles className="h-4 w-4" />
            AI Explanation
          </Button>
        )}
      </DialogTrigger>
      <DialogContent className="max-w-2xl max-h-[85vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Sparkles className="h-5 w-5 text-teal-600" />
            AI-Powered Explanation
          </DialogTitle>
        </DialogHeader>

        {isPending ? (
          <div className="flex flex-col items-center justify-center py-12 space-y-4">
            <Loader2 className="h-8 w-8 animate-spin text-teal-600" />
            <p className="text-muted-foreground">
              Analyzing your answer and generating personalized explanation...
            </p>
          </div>
        ) : explanation ? (
          <div className="space-y-6">
            {/* Why Wrong */}
            <section className="space-y-2">
              <div className="flex items-center gap-2">
                <AlertCircle className="h-5 w-5 text-red-500" />
                <h3 className="font-semibold text-red-700">Why Your Answer is Incorrect</h3>
              </div>
              <p className="text-sm text-muted-foreground pl-7">{explanation.whyWrong}</p>
            </section>

            <Separator />

            {/* Why Correct */}
            <section className="space-y-2">
              <div className="flex items-center gap-2">
                <Lightbulb className="h-5 w-5 text-green-500" />
                <h3 className="font-semibold text-green-700">Correct Answer Explained</h3>
              </div>
              <p className="text-sm text-muted-foreground pl-7">{explanation.whyCorrect}</p>
            </section>

            <Separator />

            {/* Concept Explanation */}
            <section className="space-y-2">
              <div className="flex items-center gap-2">
                <BookOpen className="h-5 w-5 text-blue-500" />
                <h3 className="font-semibold text-blue-700">Concept Deep Dive</h3>
              </div>
              <p className="text-sm text-muted-foreground pl-7">{explanation.conceptExplanation}</p>
            </section>

            {/* Common Mistake */}
            {explanation.commonMistake && (
              <>
                <Separator />
                <section className="space-y-2 bg-amber-50 p-4 rounded-lg">
                  <h3 className="font-semibold text-amber-700">Common Mistake</h3>
                  <p className="text-sm text-amber-800">{explanation.commonMistake}</p>
                </section>
              </>
            )}

            {/* Study Tip */}
            <section className="space-y-2 bg-teal-50 p-4 rounded-lg">
              <h3 className="font-semibold text-teal-700">Study Tip</h3>
              <p className="text-sm text-teal-800">{explanation.studyTip}</p>
            </section>

            {/* Related Topics */}
            {explanation.relatedTopics && explanation.relatedTopics.length > 0 && (
              <section className="space-y-2">
                <h3 className="font-semibold text-gray-700">Related Topics to Review</h3>
                <div className="flex flex-wrap gap-2">
                  {explanation.relatedTopics.map((topic, index) => (
                    <Badge key={index} variant="secondary" className="gap-1">
                      <ChevronRight className="h-3 w-3" />
                      {topic}
                    </Badge>
                  ))}
                </div>
              </section>
            )}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center py-12 text-center">
            <AlertCircle className="h-12 w-12 text-gray-400 mb-4" />
            <p className="text-muted-foreground">
              Unable to generate explanation. Please try again.
            </p>
            <Button
              variant="outline"
              className="mt-4"
              onClick={() => {
                const request: WrongAnswerRequest = {
                  questionId,
                  questionText,
                  options,
                  userAnswer,
                  correctAnswer,
                  topic,
                  existingSolution,
                };
                mutate(request);
              }}
            >
              Retry
            </Button>
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
}

// Inline explanation component (for results page)
export function WrongAnswerExplanationInline({
  explanation,
  isLoading,
}: {
  explanation?: ExplanationType | null;
  isLoading?: boolean;
}) {
  if (isLoading) {
    return (
      <div className="flex items-center gap-2 py-4">
        <Loader2 className="h-4 w-4 animate-spin text-teal-600" />
        <span className="text-sm text-muted-foreground">Generating AI explanation...</span>
      </div>
    );
  }

  if (!explanation) return null;

  return (
    <div className="space-y-4 p-4 bg-gradient-to-br from-teal-50 to-cyan-50 rounded-lg border border-teal-100">
      <div className="flex items-center gap-2">
        <Sparkles className="h-4 w-4 text-teal-600" />
        <span className="text-sm font-medium text-teal-700">AI Explanation</span>
      </div>

      <div className="space-y-3 text-sm">
        <div>
          <span className="font-medium text-red-600">Why incorrect: </span>
          <span className="text-gray-700">{explanation.whyWrong}</span>
        </div>
        <div>
          <span className="font-medium text-green-600">Correct approach: </span>
          <span className="text-gray-700">{explanation.whyCorrect}</span>
        </div>
        <div className="bg-white/60 p-3 rounded border-l-2 border-teal-400">
          <span className="font-medium text-teal-600">Tip: </span>
          <span className="text-gray-700">{explanation.studyTip}</span>
        </div>
      </div>
    </div>
  );
}
