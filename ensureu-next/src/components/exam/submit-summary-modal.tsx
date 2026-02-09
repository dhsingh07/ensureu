'use client';

import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import type { PaperData } from '@/types/paper';

interface SubmitSummaryModalProps {
  open: boolean;
  data: PaperData | null;
  onClose: () => void;
  onViewResults: () => void;
}

function formatTime(seconds: number) {
  const hrs = Math.floor(seconds / 3600);
  const mins = Math.floor((seconds % 3600) / 60);
  const secs = seconds % 60;
  if (hrs > 0) {
    return `${hrs}h ${mins}m ${secs}s`;
  }
  return `${mins}m ${secs}s`;
}

export function SubmitSummaryModal({
  open,
  data,
  onClose,
  onViewResults,
}: SubmitSummaryModalProps) {
  if (!data) return null;

  const sections = data.paper?.pattern?.sections || [];

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-3xl">
        <DialogHeader>
          <DialogTitle>Paper Completed</DialogTitle>
        </DialogHeader>

        <div className="space-y-4">
          <div className="text-sm text-slate-500">
            {data.paperName || data.paper?.paperName}
          </div>

          <div className="overflow-x-auto">
            <table className="min-w-full text-sm">
              <thead className="text-slate-500">
                <tr>
                  <th className="text-left py-2">Section</th>
                  <th className="text-right py-2">Total</th>
                  <th className="text-right py-2">Answered</th>
                  <th className="text-right py-2">Unanswered</th>
                  <th className="text-right py-2">Correct</th>
                  <th className="text-right py-2">Incorrect</th>
                  <th className="text-right py-2">Score</th>
                  <th className="text-right py-2">Time</th>
                </tr>
              </thead>
              <tbody className="text-slate-700">
                {sections.map((section, index) => {
                  const total = (section.correctCount || 0) + (section.inCorrectCount || 0) + (section.skipedCount || 0);
                  return (
                    <tr key={index} className="border-t">
                      <td className="py-2 pr-4">{section.title}</td>
                      <td className="py-2 text-right">{total}</td>
                      <td className="py-2 text-right">{(section.correctCount || 0) + (section.inCorrectCount || 0)}</td>
                      <td className="py-2 text-right">{section.skipedCount || 0}</td>
                      <td className="py-2 text-right">{section.correctCount || 0}</td>
                      <td className="py-2 text-right">{section.inCorrectCount || 0}</td>
                      <td className="py-2 text-right">{section.scoreInSection || section.score || 0}</td>
                      <td className="py-2 text-right">{formatTime(section.timeTakenSecond || 0)}</td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>

        <DialogFooter className="gap-2 sm:gap-0">
          <Button variant="outline" onClick={onClose}>Close</Button>
          <Button onClick={onViewResults}>View Results</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
