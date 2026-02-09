'use client';

import { useEffect, useState, useCallback, useRef } from 'react';
import { useRouter, useParams } from 'next/navigation';
import { Card, CardContent } from '@/components/ui/card';
import { useExamStore } from '@/stores/exam-store';
import { useUIStore } from '@/stores/ui-store';
import { useTestPaper, useSaveAnswer, useSubmitPaper, getPaperInfo, clearPaperInfo } from '@/hooks/use-papers';
import {
  Timer,
  SectionTabs,
  QuestionDisplay,
  QuestionNavigation,
  SubmitModal,
  SubmitSummaryModal,
  ExamControls,
} from '@/components/exam';
import { Loader2, AlertCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';

const AUTO_SAVE_INTERVAL = 5 * 60 * 1000; // 5 minutes

export default function ExamPage() {
  const router = useRouter();
  const params = useParams();
  const examId = params.examId as string;

  const [showSubmitModal, setShowSubmitModal] = useState(false);
  const [showSummaryModal, setShowSummaryModal] = useState(false);
  const [summaryData, setSummaryData] = useState<ReturnType<typeof getPaperForSubmission>>(null);
  const [submitLocked, setSubmitLocked] = useState(false);
  const [paperInfo, setPaperInfoState] = useState<ReturnType<typeof getPaperInfo>>(null);
  const autoSaveRef = useRef<NodeJS.Timeout | null>(null);

  const setPaperData = useExamStore((state) => state.setPaperData);
  const getPaperForSave = useExamStore((state) => state.getPaperForSave);
  const getPaperForSubmission = useExamStore((state) => state.getPaperForSubmission);
  const paperData = useExamStore((state) => state.paperData);
  const reset = useExamStore((state) => state.reset);
  const showAlert = useUIStore((state) => state.showAlert);

  // Get paper info from session storage
  useEffect(() => {
    const info = getPaperInfo();
    if (info && info.paperId === examId) {
      setPaperInfoState(info);
    } else {
      showAlert('error', 'Invalid exam session. Redirecting...');
      router.push('/home');
    }
  }, [examId, router, showAlert]);

  const isReadOnly = paperInfo?.paperStatus === 'DONE' || paperInfo?.readOnly;

  // Fetch paper data - disable after submission to prevent refetch errors
  const {
    data: fetchedPaperData,
    isLoading,
    error,
    refetch,
    isFetching,
    isStale,
  } = useTestPaper(
    paperInfo?.testType || 'FREE',
    paperInfo?.paperStatus || 'START',
    examId,
    !!paperInfo && !submitLocked // Disable query after submission
  );

  // Debug: Log query state
  console.log('[ExamPage] Query state:', {
    paperInfo: !!paperInfo,
    enabled: !!paperInfo,
    isLoading,
    isFetching,
    isStale,
    hasData: !!fetchedPaperData,
    error: error?.message,
  });

  // Save mutation
  const saveMutation = useSaveAnswer();

  // Submit mutation
  const submitMutation = useSubmitPaper();

  // Set paper data when fetched
  useEffect(() => {
    console.log('[ExamPage] fetchedPaperData changed:', fetchedPaperData);
    console.log('[ExamPage] Paper name:', fetchedPaperData?.paper?.paperName);
    console.log('[ExamPage] First section:', fetchedPaperData?.paper?.pattern?.sections?.[0]?.title);
    console.log('[ExamPage] First question:', fetchedPaperData?.paper?.pattern?.sections?.[0]?.questionData?.questions?.[0]?.problem?.question?.substring(0, 100));

    if (fetchedPaperData) {
      setPaperData(fetchedPaperData);
    }
  }, [fetchedPaperData, setPaperData]);

  // Auto-save functionality
  const handleSave = useCallback(() => {
    if (isReadOnly || submitLocked) return;
    const dataToSave = getPaperForSave();
    if (dataToSave) {
      saveMutation.mutate(dataToSave, {
        onSuccess: () => {
          showAlert('success', 'Progress saved');
        },
      });
    }
  }, [isReadOnly, submitLocked, getPaperForSave, saveMutation, showAlert]);

  // Set up auto-save
  useEffect(() => {
    if (paperData && !isReadOnly && !submitLocked) {
      autoSaveRef.current = setInterval(() => {
        handleSave();
      }, AUTO_SAVE_INTERVAL);
    }

    return () => {
      if (autoSaveRef.current) {
        clearInterval(autoSaveRef.current);
      }
    };
  }, [paperData, isReadOnly, submitLocked, handleSave]);

  // Handle time up
  const handleTimeUp = useCallback(() => {
    if (isReadOnly) return;
    showAlert('warning', 'Time is up! Submitting your test...');
    handleSubmit();
  }, [isReadOnly, showAlert]);

  // Handle submit
  const handleSubmit = useCallback(() => {
    if (isReadOnly) return;
    setSubmitLocked(true);
    if (autoSaveRef.current) {
      clearInterval(autoSaveRef.current);
      autoSaveRef.current = null;
    }
    const submissionData = getPaperForSubmission();
    if (submissionData) {
      submitMutation.mutate(submissionData, {
        onSuccess: () => {
          setSummaryData(submissionData);
          setShowSummaryModal(true);
          setShowSubmitModal(false);
        },
        onError: () => {
          setShowSubmitModal(false);
          setSubmitLocked(false);
        },
      });
    }
  }, [isReadOnly, getPaperForSubmission, submitMutation]);

  // Warn before leaving
  useEffect(() => {
    const handleBeforeUnload = (e: BeforeUnloadEvent) => {
      if (paperData) {
        e.preventDefault();
        e.returnValue = '';
      }
    };

    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => window.removeEventListener('beforeunload', handleBeforeUnload);
  }, [paperData]);

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      reset();
    };
  }, [reset]);

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <Loader2 className="h-8 w-8 animate-spin mx-auto text-primary" />
          <p className="mt-4 text-slate-600">Loading your test...</p>
        </div>
      </div>
    );
  }

  // Don't show error if we already have paper data or if summary modal is showing
  if (error && !paperData && !showSummaryModal) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Card className="max-w-md">
          <CardContent className="pt-6 text-center">
            <AlertCircle className="h-12 w-12 text-red-500 mx-auto mb-4" />
            <h2 className="text-xl font-semibold mb-2">Failed to Load Test</h2>
            <p className="text-slate-600 mb-4">
              We couldn&apos;t load your test. Please try again.
            </p>
            <div className="flex gap-2 justify-center">
              <Button variant="outline" onClick={() => router.push('/home')}>
                Go Back
              </Button>
              <Button onClick={() => refetch()}>Retry</Button>
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }

  if (!paperData) {
    return null;
  }

  return (
    <div className="min-h-screen bg-slate-100">
      {/* Top Bar */}
      <div className="sticky top-0 z-50 bg-white border-b shadow-sm">
        <div className="container mx-auto px-4 py-3">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-lg font-semibold text-slate-900">
                {paperData.paper.paperName}
              </h1>
              <p className="text-sm text-slate-500">
                {paperData.paper.paperType.replace('_', ' ')}
              </p>
            </div>
            <Timer onTimeUp={handleTimeUp} paused={isReadOnly} />
          </div>
        </div>
      </div>

      {/* Section Tabs */}
      <div className="bg-white border-b">
        <div className="container mx-auto px-4 py-2">
          <SectionTabs />
        </div>
      </div>

      {/* Main Content */}
      <div className="container mx-auto px-4 py-6">
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
          {/* Question Display - Left */}
          <div className="lg:col-span-3">
            <Card>
              <CardContent className="p-6">
              <QuestionDisplay readOnly={isReadOnly} />
              <ExamControls
                onSave={handleSave}
                onSubmit={() => setShowSubmitModal(true)}
                isSaving={saveMutation.isPending}
                readOnly={isReadOnly}
              />
              </CardContent>
            </Card>
          </div>

          {/* Question Navigation - Right */}
          <div className="lg:col-span-1">
            <Card className="sticky top-32">
              <CardContent className="p-4">
                <h3 className="font-semibold mb-4">Questions</h3>
                <QuestionNavigation />
              </CardContent>
            </Card>
          </div>
        </div>
      </div>

      {/* Submit Modal */}
      <SubmitModal
        open={showSubmitModal && !isReadOnly}
        onClose={() => setShowSubmitModal(false)}
        onConfirm={handleSubmit}
        isSubmitting={submitMutation.isPending}
      />
      <SubmitSummaryModal
        open={showSummaryModal}
        data={summaryData}
        onClose={() => {
          setShowSummaryModal(false);
          clearPaperInfo();
          reset();
          router.push('/home');
        }}
        onViewResults={() => {
          setShowSummaryModal(false);
          clearPaperInfo();
          reset();
          router.push(`/home/results-analysis/${examId}`);
        }}
      />
    </div>
  );
}
