'use client';

import { useMemo, useState } from 'react';
import type { ElementType } from 'react';
import { useRouter } from 'next/navigation';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { CATEGORIES } from '@/lib/constants/api-urls';
import { usePastPapers } from '@/hooks/use-previous-papers';
import { savePaperInfo } from '@/hooks/use-papers';
import type { PaperCategory, PaperStatus, PaperSubCategory } from '@/types/paper';
import {
  BookOpenCheck,
  ChevronDown,
  ChevronUp,
  Clock,
  FileText,
  ArrowRight,
  Play,
  RefreshCcw,
  Eye,
} from 'lucide-react';

const statusLabels: Record<PaperStatus, string> = {
  NOT_STARTED: 'New',
  START: 'New',
  INPROGRESS: 'In Progress',
  DONE: 'Completed',
};

const statusIcons: Record<PaperStatus, ElementType> = {
  NOT_STARTED: Play,
  START: Play,
  INPROGRESS: RefreshCcw,
  DONE: Eye,
};

export default function PreviousPapersPage() {
  const router = useRouter();
  const [limitCount, setLimitCount] = useState(6);
  const [selectedExam, setSelectedExam] = useState<{
    paperType: string;
    paperSubType: string;
    paperSubCode: PaperCategory;
  } | null>(null);
  const [paperFilter, setPaperFilter] = useState({
    START: true,
    INPROGRESS: true,
    DONE: true,
  });

  const examList = useMemo(() => {
    return CATEGORIES.flatMap((group) =>
      group.subCategories.map((sub) => ({
        paperType: group.name,
        paperSubType: sub.label,
        paperSubCode: sub.name as PaperCategory,
      }))
    );
  }, []);

  const { data: paperList, isLoading } = usePastPapers(
    selectedExam?.paperType || '',
    selectedExam?.paperSubCode || '',
    !!selectedExam
  );

  const toggleFilterExam = () => {
    setLimitCount((prev) => (prev === 6 ? examList.length : 6));
  };

  const startPaper = (paper: {
    paperId: string;
    paperStatus: PaperStatus;
    paperType: PaperCategory;
    paperSubCategory: PaperSubCategory;
    testType: 'FREE' | 'PAID' | 'MISSED' | 'PASTPAPER' | 'QUIZ';
    totalTime?: number;
    totalScore?: number;
    paperName?: string;
  }) => {
    savePaperInfo({
      paperId: paper.paperId,
      paperStatus: paper.paperStatus,
      paperCategory: paper.paperType,
      paperSubCategory: paper.paperSubCategory,
      testType: paper.testType,
      totalTime: paper.totalTime,
      totalScore: paper.totalScore,
      paperName: paper.paperName,
    });
    if (paper.paperStatus === 'START' || paper.paperStatus === 'NOT_STARTED') {
      router.push('/instruction');
    } else {
      router.push('/testPaper');
    }
  };

  return (
    <div className="container mx-auto px-4 py-10">
      <div className="text-center space-y-3 mb-8">
        <Badge className="mx-auto w-fit" variant="secondary">
          Past Exams
        </Badge>
        <h1 className="text-3xl md:text-4xl font-bold text-slate-900">
          Practice Previous Year Papers
        </h1>
        <p className="text-slate-600">
          Master real exam patterns with authentic previous year papers.
        </p>
      </div>

      <Card className="border border-slate-200 mb-8">
        <CardContent className="p-6 space-y-4">
          <div className="flex flex-wrap items-center justify-between gap-3">
            <div className="flex items-center gap-2 text-lg font-semibold text-slate-900">
              <BookOpenCheck className="h-5 w-5" />
              Select your exam
            </div>
            <Button variant="ghost" size="sm" onClick={toggleFilterExam}>
              {limitCount === 6 ? (
                <>
                  Show all <ChevronDown className="ml-1 h-4 w-4" />
                </>
              ) : (
                <>
                  Show less <ChevronUp className="ml-1 h-4 w-4" />
                </>
              )}
            </Button>
          </div>
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {examList.slice(0, limitCount).map((exam) => (
              <button
                key={exam.paperSubCode}
                onClick={() => setSelectedExam(exam)}
                className={`rounded-xl border p-4 text-left transition ${
                  selectedExam?.paperSubCode === exam.paperSubCode
                    ? 'border-slate-900 bg-slate-900 text-white'
                    : 'border-slate-200 hover:border-slate-300'
                }`}
              >
                <div className="text-sm uppercase tracking-wide text-slate-500">
                  {exam.paperType}
                </div>
                <div className="text-lg font-semibold">{exam.paperSubType}</div>
                <div className="text-xs opacity-80">Previous year papers</div>
              </button>
            ))}
          </div>
        </CardContent>
      </Card>

      {!selectedExam && (
        <Card className="border border-slate-200">
          <CardContent className="p-6 text-center text-slate-500">
            Select an exam category to view available papers.
          </CardContent>
        </Card>
      )}

      {selectedExam && (
        <div className="space-y-6">
          <div className="flex flex-wrap items-center justify-between gap-4">
            <div className="flex items-center gap-2 text-lg font-semibold text-slate-900">
              <FileText className="h-5 w-5" />
              Available papers
              <Badge variant="secondary">{selectedExam.paperSubType}</Badge>
            </div>
            <div className="flex flex-wrap gap-2">
              {(['START', 'INPROGRESS', 'DONE'] as const).map((status) => (
                <Button
                  key={status}
                  variant={paperFilter[status] ? 'default' : 'outline'}
                  size="sm"
                  onClick={() =>
                    setPaperFilter((prev) => ({
                      ...prev,
                      [status]: !prev[status],
                    }))
                  }
                >
                  {statusLabels[status]}
                </Button>
              ))}
            </div>
          </div>

          {isLoading && (
            <Card className="border border-slate-200">
              <CardContent className="p-6 text-slate-500">Loading papers...</CardContent>
            </Card>
          )}

          {!isLoading && paperList?.length === 0 && (
            <Card className="border border-slate-200">
              <CardContent className="p-6 text-center text-slate-500">
                No papers available for {selectedExam.paperSubType} yet.
              </CardContent>
            </Card>
          )}

          <div className="grid gap-6 md:grid-cols-2 xl:grid-cols-3">
            {paperList
              ?.filter((paper) => {
                const statusKey = paper.paperStatus === 'NOT_STARTED' ? 'START' : paper.paperStatus;
                return paperFilter[statusKey];
              })
              .map((paper) => {
                const statusKey = paper.paperStatus === 'NOT_STARTED' ? 'START' : paper.paperStatus;
                const StatusIcon = statusIcons[statusKey];
                return (
                  <Card key={paper.paperId} className="border border-slate-200">
                    <CardContent className="p-6 space-y-4">
                      <div className="flex items-center justify-between">
                        <Badge variant="secondary">
                          {paper.dateOfExamYear || 'Previous'}
                        </Badge>
                        <Badge>{statusLabels[paper.paperStatus]}</Badge>
                      </div>
                      <div>
                        <div className="text-sm text-slate-500">
                          {paper.paperCategory}
                        </div>
                        <div className="text-lg font-semibold text-slate-900">
                          {paper.paperName}
                        </div>
                        {paper.shiftOfExam && (
                          <div className="flex items-center gap-1 text-xs text-slate-500">
                            <Clock className="h-3 w-3" />
                            {paper.shiftOfExam}
                          </div>
                        )}
                      </div>
                      <div className="grid grid-cols-2 gap-3 text-sm">
                        <div className="rounded-lg bg-slate-50 p-3 text-center">
                          <div className="text-lg font-semibold">
                            {paper.totalQuestionCount || '--'}
                          </div>
                          <div className="text-xs text-slate-500">Questions</div>
                        </div>
                        <div className="rounded-lg bg-slate-50 p-3 text-center">
                          <div className="text-lg font-semibold">
                            {paper.totalScore}
                          </div>
                          <div className="text-xs text-slate-500">Max Marks</div>
                        </div>
                      </div>
                      <Button
                        className="w-full gap-2"
                        onClick={() => startPaper(paper)}
                      >
                        <StatusIcon className="h-4 w-4" />
                        {paper.paperStatus === 'INPROGRESS'
                          ? 'Resume'
                          : paper.paperStatus === 'DONE'
                            ? 'View Results'
                            : 'Start Paper'}
                        <ArrowRight className="h-4 w-4" />
                      </Button>
                    </CardContent>
                  </Card>
                );
              })}
          </div>
        </div>
      )}
    </div>
  );
}
