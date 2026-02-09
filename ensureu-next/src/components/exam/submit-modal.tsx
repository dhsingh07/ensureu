'use client';

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { useExamStore } from '@/stores/exam-store';
import { AlertTriangle, CheckCircle, XCircle, HelpCircle } from 'lucide-react';

interface SubmitModalProps {
  open: boolean;
  onClose: () => void;
  onConfirm: () => void;
  isSubmitting?: boolean;
}

export function SubmitModal({
  open,
  onClose,
  onConfirm,
  isSubmitting,
}: SubmitModalProps) {
  const paperData = useExamStore((state) => state.paperData);

  if (!paperData) return null;

  // Calculate submission stats
  let totalQuestions = 0;
  let answered = 0;
  let notAnswered = 0;
  let markedForReview = 0;

  paperData.paper.pattern.sections.forEach((section) => {
    const questions =
      section.questionData?.questions ||
      section.subSections?.[0]?.questionData?.questions ||
      [];

    questions.forEach((q) => {
      totalQuestions++;
      const hasAnswer = q.problem.options.some((opt) => opt.selected);
      if (hasAnswer) {
        answered++;
      } else {
        notAnswered++;
      }
    });
  });

  const percentAnswered = Math.round((answered / totalQuestions) * 100);

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <AlertTriangle className="h-5 w-5 text-orange-500" />
            Submit Test
          </DialogTitle>
          <DialogDescription>
            Are you sure you want to submit this test? You won&apos;t be able to make
            changes after submission.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4 py-4">
          {/* Progress Bar */}
          <div className="space-y-2">
            <div className="flex justify-between text-sm">
              <span>Progress</span>
              <span className="font-medium">{percentAnswered}% completed</span>
            </div>
            <div className="h-2 bg-slate-200 rounded-full overflow-hidden">
              <div
                className="h-full bg-primary transition-all"
                style={{ width: `${percentAnswered}%` }}
              />
            </div>
          </div>

          {/* Stats Grid */}
          <div className="grid grid-cols-3 gap-4">
            <div className="flex flex-col items-center p-3 bg-green-50 rounded-lg">
              <CheckCircle className="h-6 w-6 text-green-600 mb-1" />
              <span className="text-2xl font-bold text-green-700">{answered}</span>
              <span className="text-xs text-green-600">Answered</span>
            </div>
            <div className="flex flex-col items-center p-3 bg-red-50 rounded-lg">
              <XCircle className="h-6 w-6 text-red-600 mb-1" />
              <span className="text-2xl font-bold text-red-700">{notAnswered}</span>
              <span className="text-xs text-red-600">Not Answered</span>
            </div>
            <div className="flex flex-col items-center p-3 bg-purple-50 rounded-lg">
              <HelpCircle className="h-6 w-6 text-purple-600 mb-1" />
              <span className="text-2xl font-bold text-purple-700">
                {markedForReview}
              </span>
              <span className="text-xs text-purple-600">Marked</span>
            </div>
          </div>

          {notAnswered > 0 && (
            <div className="flex items-start gap-2 p-3 bg-orange-50 border border-orange-200 rounded-lg">
              <AlertTriangle className="h-5 w-5 text-orange-500 flex-shrink-0 mt-0.5" />
              <p className="text-sm text-orange-700">
                You have {notAnswered} unanswered question
                {notAnswered > 1 ? 's' : ''}. Unanswered questions will be
                marked as skipped.
              </p>
            </div>
          )}
        </div>

        <DialogFooter className="gap-2 sm:gap-0">
          <Button variant="outline" onClick={onClose} disabled={isSubmitting}>
            Continue Test
          </Button>
          <Button onClick={onConfirm} disabled={isSubmitting}>
            {isSubmitting ? 'Submitting...' : 'Submit Test'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
