'use client';

import { useEffect, useRef } from 'react';
import { Clock } from 'lucide-react';
import { cn } from '@/lib/utils';
import { useExamStore } from '@/stores/exam-store';

interface TimerProps {
  onTimeUp?: () => void;
  paused?: boolean;
}

export function Timer({ onTimeUp, paused = false }: TimerProps) {
  const paperTimeRemaining = useExamStore((state) => state.paperTimeRemaining);
  const tickPaperTimer = useExamStore((state) => state.tickPaperTimer);
  const tickSectionTimer = useExamStore((state) => state.tickSectionTimer);
  const tickQuestionTimer = useExamStore((state) => state.tickQuestionTimer);
  const intervalRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    if (paused) {
      return () => {};
    }
    intervalRef.current = setInterval(() => {
      tickPaperTimer();
      tickSectionTimer();
      tickQuestionTimer();
    }, 1000);

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [paused, tickPaperTimer, tickSectionTimer, tickQuestionTimer]);

  useEffect(() => {
    if (paperTimeRemaining <= 0 && onTimeUp) {
      onTimeUp();
    }
  }, [paperTimeRemaining, onTimeUp]);

  const formatTime = (seconds: number) => {
    const hrs = Math.floor(seconds / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;

    if (hrs > 0) {
      return `${hrs.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const getTimerColor = () => {
    if (paperTimeRemaining <= 60) return 'text-red-600 bg-red-50';
    if (paperTimeRemaining <= 300) return 'text-orange-600 bg-orange-50';
    return 'text-slate-700 bg-slate-100';
  };

  return (
    <div
      className={cn(
        'flex items-center gap-2 px-4 py-2 rounded-lg font-mono text-lg font-semibold',
        getTimerColor()
      )}
    >
      <Clock className="h-5 w-5" />
      <span>{formatTime(paperTimeRemaining)}</span>
    </div>
  );
}
