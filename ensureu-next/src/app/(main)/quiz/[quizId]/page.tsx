'use client';

import { useEffect, useState, useMemo, useCallback } from 'react';
import { useParams, useRouter, useSearchParams } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  useQuizData,
  useSaveQuiz,
  getQuizInfo,
  clearQuizInfo,
  calculateQuizScore,
  formatQuizTime,
} from '@/hooks/use-quiz';
import type { QuizData, QuizQuestion } from '@/types/quiz';
import {
  Clock,
  ChevronLeft,
  ChevronRight,
  CheckCircle2,
  XCircle,
  MinusCircle,
  Trophy,
  Target,
  ArrowLeft,
  Send,
  Loader2,
  AlertTriangle,
  Home,
  RotateCcw,
} from 'lucide-react';

// Flatten questions from nested structure
function flattenQuestions(quizData: QuizData): QuizQuestion[] {
  const questions: QuizQuestion[] = [];
  quizData.pattern.sections.forEach((section) => {
    section.subSections.forEach((subSection) => {
      questions.push(...subSection.questions);
    });
  });
  return questions;
}

// Update question in quiz data
function updateQuestionInData(
  quizData: QuizData,
  questionId: string,
  selectedOption: string
): QuizData {
  const updated = JSON.parse(JSON.stringify(quizData)) as QuizData;
  updated.pattern.sections.forEach((section) => {
    section.subSections.forEach((subSection) => {
      const question = subSection.questions.find((q) => q._id === questionId);
      if (question) {
        question.problem.so = selectedOption;
      }
    });
  });
  return updated;
}

export default function QuizTakePage() {
  const params = useParams();
  const router = useRouter();
  const searchParams = useSearchParams();
  const quizId = params.quizId as string;
  const isViewMode = searchParams.get('view') === 'result';

  const [quizInfo] = useState(getQuizInfo);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [timeRemaining, setTimeRemaining] = useState(quizInfo?.totalTime || 600);
  const [quizState, setQuizState] = useState<QuizData | null>(null);
  const [showSubmitDialog, setShowSubmitDialog] = useState(false);
  const [showTimeUpDialog, setShowTimeUpDialog] = useState(false);
  const [isSubmitted, setIsSubmitted] = useState(isViewMode);
  const [startTime] = useState(Date.now());

  // Fetch quiz data
  const { data: fetchedQuizData, isLoading, error } = useQuizData(
    quizId,
    isViewMode ? 'DONE' : 'START',
    !!quizId
  );

  // Save mutation
  const saveMutation = useSaveQuiz();

  // Initialize quiz state when data is fetched
  useEffect(() => {
    if (fetchedQuizData && !quizState) {
      setQuizState(fetchedQuizData);
      if (fetchedQuizData.paperStatus === 'DONE') {
        setIsSubmitted(true);
      }
    }
  }, [fetchedQuizData, quizState]);

  // Timer effect
  useEffect(() => {
    if (isSubmitted || !quizState) return;

    const timer = setInterval(() => {
      setTimeRemaining((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          setShowTimeUpDialog(true);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [isSubmitted, quizState]);

  // Get flattened questions
  const questions = useMemo(() => {
    if (!quizState) return [];
    return flattenQuestions(quizState);
  }, [quizState]);

  const currentQuestion = questions[currentIndex];

  // Calculate score
  const score = useMemo(() => {
    if (!quizState) return null;
    return calculateQuizScore(quizState);
  }, [quizState]);

  // Handle option selection
  const handleSelectOption = useCallback(
    (option: string) => {
      if (!quizState || !currentQuestion || isSubmitted) return;

      // If already selected same option, deselect it
      if (currentQuestion.problem.so === option) {
        const updated = updateQuestionInData(quizState, currentQuestion._id, '');
        setQuizState(updated);
      } else {
        const updated = updateQuestionInData(quizState, currentQuestion._id, option);
        setQuizState(updated);
      }
    },
    [quizState, currentQuestion, isSubmitted]
  );

  // Handle submit
  const handleSubmit = useCallback(async () => {
    if (!quizState) return;

    const timeTaken = Math.floor((Date.now() - startTime) / 1000);
    const finalScore = calculateQuizScore(quizState);

    try {
      await saveMutation.mutateAsync({
        quizId,
        quizData: {
          ...quizState,
          totalCorrectCount: finalScore.totalCorrect,
          totalInCorrectCount: finalScore.totalIncorrect,
          totalSkipedCount: finalScore.totalSkipped,
          totalGetScore: finalScore.score,
          totalTimeTaken: timeTaken,
        },
        isDone: true,
      });

      setIsSubmitted(true);
      setShowSubmitDialog(false);
      setShowTimeUpDialog(false);
    } catch (err) {
      console.error('Failed to submit quiz:', err);
    }
  }, [quizState, quizId, startTime, saveMutation]);

  // Auto-submit on time up
  useEffect(() => {
    if (showTimeUpDialog && !isSubmitted) {
      handleSubmit();
    }
  }, [showTimeUpDialog, isSubmitted, handleSubmit]);

  // Navigation
  const goToQuestion = (index: number) => {
    if (index >= 0 && index < questions.length) {
      setCurrentIndex(index);
    }
  };

  // Handle back
  const handleBack = () => {
    clearQuizInfo();
    router.push('/quiz');
  };

  // Loading state
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <Loader2 className="h-8 w-8 animate-spin mx-auto mb-4 text-primary" />
          <p className="text-slate-600">Loading quiz...</p>
        </div>
      </div>
    );
  }

  // Error state
  if (error || !quizState) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Card className="max-w-md">
          <CardContent className="p-6 text-center">
            <AlertTriangle className="h-12 w-12 mx-auto mb-4 text-orange-500" />
            <h2 className="text-lg font-semibold mb-2">Unable to load quiz</h2>
            <p className="text-slate-600 mb-4">
              {error?.message || 'Quiz not found or no longer available'}
            </p>
            <Button onClick={handleBack}>Back to Quizzes</Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  // Results view
  if (isSubmitted && score) {
    return (
      <div className="min-h-screen bg-slate-50 py-8">
        <div className="container mx-auto px-4 max-w-3xl">
          {/* Results Header */}
          <Card className="mb-6 overflow-hidden">
            <div className="bg-gradient-to-r from-primary to-primary/80 p-6 text-white">
              <div className="flex items-center gap-3 mb-4">
                <Trophy className="h-8 w-8" />
                <h1 className="text-2xl font-bold">Quiz Completed!</h1>
              </div>
              <p className="opacity-90">{quizInfo?.paperName || 'Daily Quiz'}</p>
            </div>
            <CardContent className="p-6">
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div className="text-center p-4 bg-green-50 rounded-xl">
                  <p className="text-3xl font-bold text-green-600">{score.score}</p>
                  <p className="text-sm text-green-700">Score</p>
                </div>
                <div className="text-center p-4 bg-blue-50 rounded-xl">
                  <p className="text-3xl font-bold text-blue-600">{score.percentage}%</p>
                  <p className="text-sm text-blue-700">Percentage</p>
                </div>
                <div className="text-center p-4 bg-emerald-50 rounded-xl">
                  <p className="text-3xl font-bold text-emerald-600">{score.totalCorrect}</p>
                  <p className="text-sm text-emerald-700">Correct</p>
                </div>
                <div className="text-center p-4 bg-red-50 rounded-xl">
                  <p className="text-3xl font-bold text-red-600">{score.totalIncorrect}</p>
                  <p className="text-sm text-red-700">Incorrect</p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Question Review */}
          <Card className="mb-6">
            <CardHeader>
              <CardTitle className="text-lg">Review Answers</CardTitle>
            </CardHeader>
            <CardContent>
              {/* Question Navigation */}
              <div className="flex flex-wrap gap-2 mb-6">
                {questions.map((q, idx) => {
                  const isCorrect = q.problem.so === q.problem.co;
                  const isSkipped = !q.problem.so;
                  return (
                    <button
                      key={q._id}
                      onClick={() => setCurrentIndex(idx)}
                      className={`w-10 h-10 rounded-lg font-medium text-sm transition-colors ${
                        idx === currentIndex
                          ? 'ring-2 ring-primary ring-offset-2'
                          : ''
                      } ${
                        isSkipped
                          ? 'bg-slate-200 text-slate-700'
                          : isCorrect
                          ? 'bg-green-500 text-white'
                          : 'bg-red-500 text-white'
                      }`}
                    >
                      {idx + 1}
                    </button>
                  );
                })}
              </div>

              {/* Current Question */}
              {currentQuestion && (
                <div className="space-y-4">
                  <div className="flex items-center gap-2 text-sm text-slate-500">
                    <span>Question {currentIndex + 1} of {questions.length}</span>
                    {currentQuestion.problem.so === currentQuestion.problem.co ? (
                      <Badge className="bg-green-100 text-green-700">Correct</Badge>
                    ) : !currentQuestion.problem.so ? (
                      <Badge className="bg-slate-100 text-slate-700">Skipped</Badge>
                    ) : (
                      <Badge className="bg-red-100 text-red-700">Incorrect</Badge>
                    )}
                  </div>

                  <div
                    className="p-4 bg-slate-50 rounded-xl text-slate-800"
                    dangerouslySetInnerHTML={{ __html: currentQuestion.problem.value }}
                  />

                  <div className="grid gap-3">
                    {currentQuestion.problem.options.map((option) => {
                      const isSelected = currentQuestion.problem.so === option.prompt;
                      const isCorrect = currentQuestion.problem.co === option.prompt;

                      return (
                        <div
                          key={option.prompt}
                          className={`rounded-xl border-2 px-4 py-3 ${
                            isCorrect
                              ? 'border-green-500 bg-green-50'
                              : isSelected
                              ? 'border-red-500 bg-red-50'
                              : 'border-slate-200'
                          }`}
                        >
                          <div className="flex items-start gap-3">
                            <span className="font-semibold mt-0.5">{option.prompt}.</span>
                            <span dangerouslySetInnerHTML={{ __html: option.value }} />
                            {isCorrect && (
                              <CheckCircle2 className="h-5 w-5 text-green-600 ml-auto flex-shrink-0" />
                            )}
                            {isSelected && !isCorrect && (
                              <XCircle className="h-5 w-5 text-red-600 ml-auto flex-shrink-0" />
                            )}
                          </div>
                        </div>
                      );
                    })}
                  </div>

                  {/* Solution */}
                  {currentQuestion.problem.solutions?.[0]?.value && (
                    <div className="mt-4 p-4 bg-blue-50 rounded-xl border border-blue-200">
                      <p className="font-medium text-blue-900 mb-2">Solution:</p>
                      <div
                        className="text-blue-800 text-sm"
                        dangerouslySetInnerHTML={{
                          __html: currentQuestion.problem.solutions[0].value,
                        }}
                      />
                    </div>
                  )}

                  {/* Navigation */}
                  <div className="flex justify-between pt-4">
                    <Button
                      variant="outline"
                      onClick={() => goToQuestion(currentIndex - 1)}
                      disabled={currentIndex === 0}
                    >
                      <ChevronLeft className="h-4 w-4 mr-1" />
                      Previous
                    </Button>
                    <Button
                      onClick={() => goToQuestion(currentIndex + 1)}
                      disabled={currentIndex === questions.length - 1}
                    >
                      Next
                      <ChevronRight className="h-4 w-4 ml-1" />
                    </Button>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>

          {/* Actions */}
          <div className="flex justify-center gap-4">
            <Button variant="outline" onClick={handleBack}>
              <Home className="h-4 w-4 mr-2" />
              Back to Quizzes
            </Button>
            <Button onClick={() => router.push('/quiz')}>
              <RotateCcw className="h-4 w-4 mr-2" />
              Take Another Quiz
            </Button>
          </div>
        </div>
      </div>
    );
  }

  // Quiz taking view
  return (
    <div className="min-h-screen bg-slate-50">
      {/* Header */}
      <div className="sticky top-0 z-40 bg-white border-b shadow-sm">
        <div className="container mx-auto px-4">
          <div className="flex items-center justify-between h-14">
            <Button variant="ghost" size="sm" onClick={handleBack}>
              <ArrowLeft className="h-4 w-4 mr-2" />
              Exit
            </Button>
            <div className="text-center">
              <p className="text-sm font-medium text-slate-900">
                {quizInfo?.paperName || 'Daily Quiz'}
              </p>
              <p className="text-xs text-slate-500">
                Question {currentIndex + 1} of {questions.length}
              </p>
            </div>
            <div
              className={`flex items-center gap-2 px-3 py-1 rounded-lg ${
                timeRemaining < 60
                  ? 'bg-red-100 text-red-700'
                  : timeRemaining < 180
                  ? 'bg-orange-100 text-orange-700'
                  : 'bg-green-100 text-green-700'
              }`}
            >
              <Clock className="h-4 w-4" />
              <span className="font-mono font-medium">
                {formatQuizTime(timeRemaining)}
              </span>
            </div>
          </div>
          {/* Progress bar */}
          <Progress
            value={((currentIndex + 1) / questions.length) * 100}
            className="h-1"
          />
        </div>
      </div>

      <div className="container mx-auto px-4 py-6 max-w-4xl">
        <div className="grid gap-6 lg:grid-cols-[1fr_200px]">
          {/* Question Panel */}
          <Card>
            <CardContent className="p-6">
              {currentQuestion && (
                <div className="space-y-6">
                  {/* Question */}
                  <div
                    className="text-lg text-slate-800 leading-relaxed"
                    dangerouslySetInnerHTML={{ __html: currentQuestion.problem.value }}
                  />

                  {/* Options */}
                  <div className="grid gap-3">
                    {currentQuestion.problem.options.map((option) => {
                      const isSelected = currentQuestion.problem.so === option.prompt;

                      return (
                        <button
                          key={option.prompt}
                          onClick={() => handleSelectOption(option.prompt)}
                          className={`rounded-xl border-2 px-4 py-3 text-left transition-all ${
                            isSelected
                              ? 'border-primary bg-primary/5'
                              : 'border-slate-200 hover:border-slate-300 hover:bg-slate-50'
                          }`}
                        >
                          <div className="flex items-start gap-3">
                            <span
                              className={`w-8 h-8 rounded-lg flex items-center justify-center font-medium ${
                                isSelected
                                  ? 'bg-primary text-white'
                                  : 'bg-slate-100 text-slate-700'
                              }`}
                            >
                              {option.prompt}
                            </span>
                            <span
                              className="flex-1 pt-1"
                              dangerouslySetInnerHTML={{ __html: option.value }}
                            />
                          </div>
                        </button>
                      );
                    })}
                  </div>

                  {/* Navigation */}
                  <div className="flex justify-between pt-4 border-t">
                    <Button
                      variant="outline"
                      onClick={() => goToQuestion(currentIndex - 1)}
                      disabled={currentIndex === 0}
                    >
                      <ChevronLeft className="h-4 w-4 mr-1" />
                      Previous
                    </Button>
                    {currentIndex === questions.length - 1 ? (
                      <Button onClick={() => setShowSubmitDialog(true)}>
                        <Send className="h-4 w-4 mr-2" />
                        Submit Quiz
                      </Button>
                    ) : (
                      <Button onClick={() => goToQuestion(currentIndex + 1)}>
                        Next
                        <ChevronRight className="h-4 w-4 ml-1" />
                      </Button>
                    )}
                  </div>
                </div>
              )}
            </CardContent>
          </Card>

          {/* Question Palette */}
          <div className="hidden lg:block">
            <Card className="sticky top-20">
              <CardHeader className="pb-3">
                <CardTitle className="text-sm">Question Palette</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-5 gap-2">
                  {questions.map((q, idx) => {
                    const isAnswered = !!q.problem.so;
                    return (
                      <button
                        key={q._id}
                        onClick={() => setCurrentIndex(idx)}
                        className={`w-8 h-8 rounded text-xs font-medium transition-colors ${
                          idx === currentIndex
                            ? 'ring-2 ring-primary ring-offset-1'
                            : ''
                        } ${
                          isAnswered
                            ? 'bg-green-500 text-white'
                            : 'bg-slate-200 text-slate-700'
                        }`}
                      >
                        {idx + 1}
                      </button>
                    );
                  })}
                </div>
                <div className="mt-4 space-y-2 text-xs">
                  <div className="flex items-center gap-2">
                    <div className="w-4 h-4 rounded bg-green-500" />
                    <span className="text-slate-600">Answered ({score?.totalCorrect ?? 0 + (score?.totalIncorrect ?? 0)})</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <div className="w-4 h-4 rounded bg-slate-200" />
                    <span className="text-slate-600">Not Answered ({score?.totalSkipped ?? questions.length})</span>
                  </div>
                </div>
                <Button
                  className="w-full mt-4"
                  onClick={() => setShowSubmitDialog(true)}
                >
                  Submit Quiz
                </Button>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>

      {/* Submit Confirmation Dialog */}
      <Dialog open={showSubmitDialog} onOpenChange={setShowSubmitDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Submit Quiz?</DialogTitle>
            <DialogDescription>
              Are you sure you want to submit your quiz? You cannot change your
              answers after submission.
            </DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <div className="grid grid-cols-3 gap-4 text-center">
              <div className="p-3 bg-green-50 rounded-lg">
                <p className="text-xl font-bold text-green-600">
                  {questions.filter((q) => q.problem.so).length}
                </p>
                <p className="text-xs text-green-700">Answered</p>
              </div>
              <div className="p-3 bg-slate-100 rounded-lg">
                <p className="text-xl font-bold text-slate-600">
                  {questions.filter((q) => !q.problem.so).length}
                </p>
                <p className="text-xs text-slate-700">Unanswered</p>
              </div>
              <div className="p-3 bg-blue-50 rounded-lg">
                <p className="text-xl font-bold text-blue-600">
                  {formatQuizTime(timeRemaining)}
                </p>
                <p className="text-xs text-blue-700">Remaining</p>
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowSubmitDialog(false)}>
              Continue Quiz
            </Button>
            <Button onClick={handleSubmit} disabled={saveMutation.isPending}>
              {saveMutation.isPending ? (
                <>
                  <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                  Submitting...
                </>
              ) : (
                'Submit Quiz'
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Time Up Dialog */}
      <Dialog open={showTimeUpDialog} onOpenChange={() => {}}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <AlertTriangle className="h-5 w-5 text-orange-500" />
              Time&apos;s Up!
            </DialogTitle>
            <DialogDescription>
              Your time has expired. Your quiz is being submitted automatically.
            </DialogDescription>
          </DialogHeader>
          <div className="py-4 flex justify-center">
            <Loader2 className="h-8 w-8 animate-spin text-primary" />
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
