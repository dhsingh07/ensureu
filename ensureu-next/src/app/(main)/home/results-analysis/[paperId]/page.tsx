'use client';

import { useState, useMemo } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { usePaperAnalysis, useTestPaper, getPaperInfo } from '@/hooks/use-papers';
import { useAuthStore } from '@/stores/auth-store';
import type { PaperCategory, Problem } from '@/types/paper';
import type { ExamAnalysisRequest } from '@/types/ai';
import {
  WrongAnswerExplanationDialog,
  WrongAnswerExplanationInline,
  ExamAnalysisCard,
  StudyPlanDialog,
} from '@/components/ai';
import {
  CheckCircle,
  CheckCircle2,
  XCircle,
  MinusCircle,
  Trophy,
  Clock,
  Target,
  ArrowLeft,
  Share2,
  RotateCcw,
  AlertCircle,
  Loader2,
  ChevronLeft,
  ChevronRight,
  FileText,
  Lightbulb,
  Filter,
  Sparkles,
  Calendar,
} from 'lucide-react';

type QuestionFilter = 'all' | 'correct' | 'incorrect' | 'skipped';

interface FlatQuestion {
  id: string;
  index: number;
  sectionTitle: string;
  problem: Problem;
}

export default function ResultsAnalysisPage() {
  const params = useParams();
  const router = useRouter();
  const paperId = params.paperId as string;

  const [activeTab, setActiveTab] = useState<'overview' | 'questions'>('overview');
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [questionFilter, setQuestionFilter] = useState<QuestionFilter>('all');

  // Get user info for AI features
  const user = useAuthStore((state) => state.user);

  // Fetch paper result summary
  const { data: result, isLoading: loadingResult, error: resultError } = usePaperAnalysis(paperId);

  // Fetch full paper data with questions (for review)
  const { data: paperData, isLoading: loadingPaper } = useTestPaper(
    result?.testType || 'FREE',
    'DONE',
    paperId,
    !!result // Only fetch after we have the result
  );

  const formatTime = (seconds: number) => {
    if (!seconds) return '0s';
    const hrs = Math.floor(seconds / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;

    if (hrs > 0) {
      return `${hrs}h ${mins}m ${secs}s`;
    }
    if (mins > 0) {
      return `${mins}m ${secs}s`;
    }
    return `${secs}s`;
  };

  // Flatten all questions from paper data
  const allQuestions = useMemo<FlatQuestion[]>(() => {
    if (!paperData?.paper?.pattern?.sections) return [];

    const questions: FlatQuestion[] = [];
    let index = 0;

    paperData.paper.pattern.sections.forEach((section) => {
      const sectionTitle = section.title || 'General';
      section.questionData?.questions?.forEach((q) => {
        questions.push({
          id: q.id || `q-${index}`,
          index: index++,
          sectionTitle,
          problem: q.problem,
        });
      });
    });

    return questions;
  }, [paperData]);

  // Filter questions based on selected filter
  const filteredQuestions = useMemo(() => {
    return allQuestions.filter((q) => {
      const { so, co } = q.problem;
      const soValue = Array.isArray(so) ? so[0] : so;
      const coValue = Array.isArray(co) ? co[0] : co;

      switch (questionFilter) {
        case 'correct':
          return soValue && String(soValue) === String(coValue);
        case 'incorrect':
          return soValue && String(soValue) !== String(coValue);
        case 'skipped':
          return !soValue;
        default:
          return true;
      }
    });
  }, [allQuestions, questionFilter]);

  const currentQuestion = filteredQuestions[currentQuestionIndex];

  // Get question status
  const getQuestionStatus = (problem: Problem): 'correct' | 'incorrect' | 'skipped' => {
    const soValue = Array.isArray(problem.so) ? problem.so[0] : problem.so;
    const coValue = Array.isArray(problem.co) ? problem.co[0] : problem.co;

    if (!soValue) return 'skipped';
    return String(soValue) === String(coValue) ? 'correct' : 'incorrect';
  };

  // Loading state
  if (loadingResult) {
    return (
      <div className="container mx-auto px-4 py-8">
        <Button
          variant="ghost"
          onClick={() => router.push('/home')}
          className="mb-6 gap-2"
        >
          <ArrowLeft className="h-4 w-4" />
          Back to Home
        </Button>
        <div className="flex flex-col items-center justify-center py-16">
          <Loader2 className="h-12 w-12 animate-spin text-primary mb-4" />
          <p className="text-slate-600">Loading results...</p>
        </div>
      </div>
    );
  }

  // Error state
  if (resultError || !result) {
    return (
      <div className="container mx-auto px-4 py-8">
        <Button
          variant="ghost"
          onClick={() => router.push('/home')}
          className="mb-6 gap-2"
        >
          <ArrowLeft className="h-4 w-4" />
          Back to Home
        </Button>
        <Card className="max-w-md mx-auto">
          <CardContent className="pt-6 text-center">
            <AlertCircle className="h-12 w-12 text-red-500 mx-auto mb-4" />
            <h2 className="text-xl font-semibold mb-2">Failed to Load Results</h2>
            <p className="text-slate-600 mb-4">
              We couldn&apos;t load the test results. Please try again.
            </p>
            <Button onClick={() => router.push('/home')}>
              Return to Home
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  const scorePercentage = result.totalScore > 0
    ? Math.round((result.obtainedScore / result.totalScore) * 100)
    : 0;
  const attemptedQuestions = result.correctCount + result.incorrectCount;

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Back Button */}
      <Button
        variant="ghost"
        onClick={() => router.push('/home')}
        className="mb-6 gap-2"
      >
        <ArrowLeft className="h-4 w-4" />
        Back to Home
      </Button>

      {/* Header */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-slate-900 mb-2">
          {result.paperName}
        </h1>
        <p className="text-slate-600">Test Results & Analysis</p>
      </div>

      {/* Tabs */}
      <Tabs id="results-tabs" value={activeTab} onValueChange={(v) => setActiveTab(v as typeof activeTab)} className="space-y-6">
        <TabsList>
          <TabsTrigger value="overview" className="gap-2">
            <Trophy className="h-4 w-4" />
            Overview
          </TabsTrigger>
          <TabsTrigger value="questions" className="gap-2">
            <FileText className="h-4 w-4" />
            Review Answers ({allQuestions.length})
          </TabsTrigger>
        </TabsList>

        {/* Overview Tab */}
        <TabsContent value="overview" className="space-y-6">
          {/* Score Overview */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <Card className="bg-gradient-to-br from-primary to-primary/80 text-white">
              <CardContent className="p-6">
                <div className="flex items-center gap-4">
                  <Trophy className="h-10 w-10 opacity-80" />
                  <div>
                    <p className="text-3xl font-bold">
                      {result.obtainedScore}/{result.totalScore}
                    </p>
                    <p className="text-sm opacity-80">Total Score</p>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardContent className="p-6">
                <div className="flex items-center gap-4">
                  <div className="p-3 bg-green-100 rounded-lg">
                    <CheckCircle className="h-6 w-6 text-green-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold text-slate-900">
                      {result.correctCount}
                    </p>
                    <p className="text-sm text-slate-500">Correct</p>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardContent className="p-6">
                <div className="flex items-center gap-4">
                  <div className="p-3 bg-red-100 rounded-lg">
                    <XCircle className="h-6 w-6 text-red-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold text-slate-900">
                      {result.incorrectCount}
                    </p>
                    <p className="text-sm text-slate-500">Incorrect</p>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardContent className="p-6">
                <div className="flex items-center gap-4">
                  <div className="p-3 bg-slate-100 rounded-lg">
                    <MinusCircle className="h-6 w-6 text-slate-600" />
                  </div>
                  <div>
                    <p className="text-2xl font-bold text-slate-900">
                      {result.skippedCount}
                    </p>
                    <p className="text-sm text-slate-500">Skipped</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Performance Stats */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <Card>
              <CardHeader>
                <CardTitle className="text-lg flex items-center gap-2">
                  <Target className="h-5 w-5 text-primary" />
                  Accuracy
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-center">
                  <p className="text-4xl font-bold text-primary mb-2">
                    {attemptedQuestions > 0
                      ? Math.round((result.correctCount / attemptedQuestions) * 100)
                      : 0}
                    %
                  </p>
                  <Progress value={scorePercentage} className="h-2 mb-2" />
                  <p className="text-sm text-slate-500">
                    {result.correctCount} out of {attemptedQuestions} attempted
                  </p>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="text-lg flex items-center gap-2">
                  <Clock className="h-5 w-5 text-primary" />
                  Time Taken
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-center">
                  <p className="text-4xl font-bold text-slate-900 mb-2">
                    {formatTime(result.timeTaken)}
                  </p>
                  <p className="text-sm text-slate-500">
                    Avg {Math.round(result.timeTaken / result.totalQuestions)}s
                    per question
                  </p>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="text-lg flex items-center gap-2">
                  <Trophy className="h-5 w-5 text-primary" />
                  Ranking
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-center">
                  <p className="text-4xl font-bold text-slate-900 mb-2">
                    #{result.rank || '-'}
                  </p>
                  <p className="text-sm text-slate-500">
                    {result.percentile && result.totalParticipants ? (
                      <>Top {100 - result.percentile}% of {result.totalParticipants.toLocaleString()} participants</>
                    ) : (
                      'Ranking data not available'
                    )}
                  </p>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Section-wise Analysis */}
          {result.sections && result.sections.length > 0 && (
            <Card>
              <CardHeader>
                <CardTitle>Section-wise Analysis</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="overflow-x-auto">
                  <table className="min-w-full text-sm">
                    <thead className="text-slate-500 border-b">
                      <tr>
                        <th className="text-left py-3">Section</th>
                        <th className="text-right py-3">Questions</th>
                        <th className="text-right py-3">Correct</th>
                        <th className="text-right py-3">Incorrect</th>
                        <th className="text-right py-3">Skipped</th>
                        <th className="text-right py-3">Score</th>
                        <th className="text-right py-3">Time</th>
                      </tr>
                    </thead>
                    <tbody className="text-slate-700">
                      {result.sections.map((section, index) => (
                        <tr key={index} className="border-b last:border-0">
                          <td className="py-3 pr-4 font-medium">{section.title}</td>
                          <td className="py-3 text-right">{section.totalQuestions}</td>
                          <td className="py-3 text-right text-green-600">{section.correct}</td>
                          <td className="py-3 text-right text-red-600">{section.incorrect}</td>
                          <td className="py-3 text-right text-slate-500">{section.skipped}</td>
                          <td className="py-3 text-right font-medium">{section.score}/{section.maxScore}</td>
                          <td className="py-3 text-right">{formatTime(section.timeTaken)}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </CardContent>
            </Card>
          )}

          {/* AI Analysis Card */}
          {user?.id && (
            <ExamAnalysisCard
              examData={{
                userId: user.id,
                examId: paperId,
                examName: result.paperName,
                score: result.obtainedScore,
                totalMarks: result.totalScore,
                timeTakenMinutes: Math.round(result.timeTaken / 60),
                totalTimeMinutes: result.totalQuestions, // Approximate 1 min per question
                sectionScores: result.sections?.map((section) => ({
                  sectionName: section.title,
                  score: section.score,
                  maxScore: section.maxScore,
                  percentage: section.maxScore > 0 ? (section.score / section.maxScore) * 100 : 0,
                  questionsAttempted: section.correct + section.incorrect,
                  questionsCorrect: section.correct,
                })) || [],
                questionResults: [], // Required by Python API
              }}
            />
          )}

          {/* Actions */}
          <div className="flex flex-wrap gap-4">
            <Button
              onClick={() => setActiveTab('questions')}
              className="gap-2"
            >
              <FileText className="h-4 w-4" />
              Review All Answers
            </Button>
            <StudyPlanDialog
              examName={result.paperName}
              weakTopics={result.sections
                ?.filter((s) => s.correct / s.totalQuestions < 0.5)
                .map((s) => s.title)}
              strongTopics={result.sections
                ?.filter((s) => s.correct / s.totalQuestions >= 0.7)
                .map((s) => s.title)}
              trigger={
                <Button variant="outline" className="gap-2 border-teal-200 text-teal-600 hover:bg-teal-50">
                  <Calendar className="h-4 w-4" />
                  Generate Study Plan
                </Button>
              }
            />
            <Button
              variant="outline"
              onClick={() => {
                // TODO: Implement share functionality
              }}
              className="gap-2"
            >
              <Share2 className="h-4 w-4" />
              Share Results
            </Button>
            <Button
              variant="outline"
              onClick={() => router.push('/home')}
              className="gap-2"
            >
              <RotateCcw className="h-4 w-4" />
              Take Another Test
            </Button>
          </div>
        </TabsContent>

        {/* Questions Review Tab */}
        <TabsContent value="questions" className="space-y-6">
          {loadingPaper ? (
            <div className="flex flex-col items-center justify-center py-16">
              <Loader2 className="h-8 w-8 animate-spin text-primary mb-4" />
              <p className="text-slate-600">Loading questions...</p>
            </div>
          ) : allQuestions.length === 0 ? (
            <Card>
              <CardContent className="p-12 text-center">
                <AlertCircle className="h-12 w-12 mx-auto mb-4 text-slate-300" />
                <p className="text-slate-600">No questions available for review</p>
              </CardContent>
            </Card>
          ) : (
            <>
              {/* Question Filter & Navigation */}
              <Card>
                <CardContent className="p-4">
                  <div className="flex flex-wrap items-center justify-between gap-4">
                    {/* Filter */}
                    <div className="flex items-center gap-2">
                      <Filter className="h-4 w-4 text-slate-500" />
                      <div className="flex gap-2">
                        {[
                          { value: 'all', label: 'All', count: allQuestions.length },
                          { value: 'correct', label: 'Correct', count: result.correctCount, color: 'text-green-600' },
                          { value: 'incorrect', label: 'Incorrect', count: result.incorrectCount, color: 'text-red-600' },
                          { value: 'skipped', label: 'Skipped', count: result.skippedCount, color: 'text-slate-500' },
                        ].map((filter) => (
                          <Button
                            key={filter.value}
                            variant={questionFilter === filter.value ? 'default' : 'outline'}
                            size="sm"
                            onClick={() => {
                              setQuestionFilter(filter.value as QuestionFilter);
                              setCurrentQuestionIndex(0);
                            }}
                            className="gap-1"
                          >
                            <span className={questionFilter !== filter.value ? filter.color : ''}>
                              {filter.label}
                            </span>
                            <Badge variant="secondary" className="ml-1">
                              {filter.count}
                            </Badge>
                          </Button>
                        ))}
                      </div>
                    </div>

                    {/* Navigation */}
                    <div className="flex items-center gap-2">
                      <span className="text-sm text-slate-500">
                        {currentQuestionIndex + 1} of {filteredQuestions.length}
                      </span>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setCurrentQuestionIndex((prev) => Math.max(0, prev - 1))}
                        disabled={currentQuestionIndex === 0}
                      >
                        <ChevronLeft className="h-4 w-4" />
                      </Button>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setCurrentQuestionIndex((prev) => Math.min(filteredQuestions.length - 1, prev + 1))}
                        disabled={currentQuestionIndex >= filteredQuestions.length - 1}
                      >
                        <ChevronRight className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>

              {/* Question Palette */}
              <Card>
                <CardHeader className="pb-3">
                  <CardTitle className="text-sm font-medium">Question Palette</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="flex flex-wrap gap-2">
                    {filteredQuestions.map((q, idx) => {
                      const status = getQuestionStatus(q.problem);
                      return (
                        <button
                          key={q.id}
                          onClick={() => setCurrentQuestionIndex(idx)}
                          className={`w-10 h-10 rounded-lg text-sm font-medium transition-all ${
                            idx === currentQuestionIndex ? 'ring-2 ring-primary ring-offset-2' : ''
                          } ${
                            status === 'correct'
                              ? 'bg-green-500 text-white'
                              : status === 'incorrect'
                              ? 'bg-red-500 text-white'
                              : 'bg-slate-200 text-slate-700'
                          }`}
                        >
                          {q.index + 1}
                        </button>
                      );
                    })}
                  </div>
                  <div className="flex gap-4 mt-4 text-xs text-slate-500">
                    <div className="flex items-center gap-1">
                      <div className="w-3 h-3 rounded bg-green-500" />
                      Correct
                    </div>
                    <div className="flex items-center gap-1">
                      <div className="w-3 h-3 rounded bg-red-500" />
                      Incorrect
                    </div>
                    <div className="flex items-center gap-1">
                      <div className="w-3 h-3 rounded bg-slate-200" />
                      Skipped
                    </div>
                  </div>
                </CardContent>
              </Card>

              {/* Current Question */}
              {currentQuestion && (
                <Card>
                  <CardHeader>
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <Badge variant="outline">Q{currentQuestion.index + 1}</Badge>
                        <Badge variant="secondary">{currentQuestion.sectionTitle}</Badge>
                        {(() => {
                          const status = getQuestionStatus(currentQuestion.problem);
                          if (status === 'correct') {
                            return (
                              <Badge className="bg-green-100 text-green-700">
                                <CheckCircle2 className="h-3 w-3 mr-1" />
                                Correct
                              </Badge>
                            );
                          } else if (status === 'incorrect') {
                            return (
                              <Badge className="bg-red-100 text-red-700">
                                <XCircle className="h-3 w-3 mr-1" />
                                Incorrect
                              </Badge>
                            );
                          } else {
                            return (
                              <Badge className="bg-slate-100 text-slate-700">
                                <MinusCircle className="h-3 w-3 mr-1" />
                                Skipped
                              </Badge>
                            );
                          }
                        })()}
                      </div>
                    </div>
                  </CardHeader>
                  <CardContent className="space-y-6">
                    {/* Question Text */}
                    <div
                      className="text-lg text-slate-800 leading-relaxed p-4 bg-slate-50 rounded-xl"
                      dangerouslySetInnerHTML={{
                        __html: currentQuestion.problem.question || currentQuestion.problem.value || '',
                      }}
                    />

                    {/* Options */}
                    <div className="grid gap-3">
                      {currentQuestion.problem.options?.map((option) => {
                        const soValue = Array.isArray(currentQuestion.problem.so)
                          ? currentQuestion.problem.so[0]
                          : currentQuestion.problem.so;
                        const coValue = Array.isArray(currentQuestion.problem.co)
                          ? currentQuestion.problem.co[0]
                          : currentQuestion.problem.co;

                        const optionKey = option.prompt || option.key;
                        const isSelected = String(soValue) === String(optionKey);
                        const isCorrect = String(coValue) === String(optionKey);

                        return (
                          <div
                            key={optionKey}
                            className={`rounded-xl border-2 px-4 py-3 ${
                              isCorrect
                                ? 'border-green-500 bg-green-50'
                                : isSelected
                                ? 'border-red-500 bg-red-50'
                                : 'border-slate-200'
                            }`}
                          >
                            <div className="flex items-start gap-3">
                              <span
                                className={`w-8 h-8 rounded-lg flex items-center justify-center font-medium flex-shrink-0 ${
                                  isCorrect
                                    ? 'bg-green-500 text-white'
                                    : isSelected
                                    ? 'bg-red-500 text-white'
                                    : 'bg-slate-100 text-slate-700'
                                }`}
                              >
                                {optionKey}
                              </span>
                              <span
                                className="flex-1 pt-1"
                                dangerouslySetInnerHTML={{
                                  __html: option.text || option.value || '',
                                }}
                              />
                              {isCorrect && (
                                <CheckCircle2 className="h-5 w-5 text-green-600 flex-shrink-0 mt-1" />
                              )}
                              {isSelected && !isCorrect && (
                                <XCircle className="h-5 w-5 text-red-600 flex-shrink-0 mt-1" />
                              )}
                            </div>
                          </div>
                        );
                      })}
                    </div>

                    {/* Answer Summary */}
                    <div className="flex flex-wrap gap-4 p-4 bg-slate-50 rounded-xl">
                      <div className="flex items-center gap-2">
                        <span className="text-sm text-slate-500">Your Answer:</span>
                        <Badge variant={getQuestionStatus(currentQuestion.problem) === 'skipped' ? 'secondary' :
                          getQuestionStatus(currentQuestion.problem) === 'correct' ? 'default' : 'destructive'}>
                          {Array.isArray(currentQuestion.problem.so)
                            ? currentQuestion.problem.so[0] || 'Not Answered'
                            : currentQuestion.problem.so || 'Not Answered'}
                        </Badge>
                      </div>
                      <div className="flex items-center gap-2">
                        <span className="text-sm text-slate-500">Correct Answer:</span>
                        <Badge className="bg-green-100 text-green-700">
                          {Array.isArray(currentQuestion.problem.co)
                            ? currentQuestion.problem.co[0]
                            : currentQuestion.problem.co}
                        </Badge>
                      </div>
                    </div>

                    {/* Explanation/Solution */}
                    {currentQuestion.problem.solutions && currentQuestion.problem.solutions.length > 0 && (
                      <div className="p-4 bg-blue-50 rounded-xl border border-blue-200">
                        <div className="flex items-center gap-2 mb-3">
                          <Lightbulb className="h-5 w-5 text-blue-600" />
                          <span className="font-semibold text-blue-900">Explanation</span>
                        </div>
                        <div
                          className="text-blue-800 prose prose-sm max-w-none"
                          dangerouslySetInnerHTML={{
                            __html: (() => {
                              const sol = currentQuestion.problem.solutions[0];
                              if (typeof sol === 'string') return sol;
                              return sol?.text || sol?.value || '';
                            })(),
                          }}
                        />
                      </div>
                    )}

                    {/* If no solution but has explanation field */}
                    {!currentQuestion.problem.solutions?.length && currentQuestion.problem.explanation && (
                      <div className="p-4 bg-blue-50 rounded-xl border border-blue-200">
                        <div className="flex items-center gap-2 mb-3">
                          <Lightbulb className="h-5 w-5 text-blue-600" />
                          <span className="font-semibold text-blue-900">Explanation</span>
                        </div>
                        <div
                          className="text-blue-800 prose prose-sm max-w-none"
                          dangerouslySetInnerHTML={{
                            __html: currentQuestion.problem.explanation,
                          }}
                        />
                      </div>
                    )}

                    {/* AI Explanation for wrong answers */}
                    {getQuestionStatus(currentQuestion.problem) === 'incorrect' && (
                      <div className="flex items-center gap-3 p-4 bg-gradient-to-r from-teal-50 to-cyan-50 rounded-xl border border-teal-200">
                        <Sparkles className="h-5 w-5 text-teal-600" />
                        <div className="flex-1">
                          <p className="text-sm font-medium text-teal-900">Need help understanding this?</p>
                          <p className="text-xs text-teal-600">Get AI-powered explanation for why your answer was wrong</p>
                        </div>
                        <WrongAnswerExplanationDialog
                          questionId={currentQuestion.id}
                          questionText={currentQuestion.problem.question || currentQuestion.problem.value || ''}
                          options={currentQuestion.problem.options?.map((o) => o.text || o.value || '') || []}
                          userAnswer={(() => {
                            const so = currentQuestion.problem.so;
                            const soValue = Array.isArray(so) ? so[0] : so;
                            return currentQuestion.problem.options?.findIndex((o) =>
                              String(o.prompt || o.key) === String(soValue)
                            ) ?? 0;
                          })()}
                          correctAnswer={(() => {
                            const co = currentQuestion.problem.co;
                            const coValue = Array.isArray(co) ? co[0] : co;
                            return currentQuestion.problem.options?.findIndex((o) =>
                              String(o.prompt || o.key) === String(coValue)
                            ) ?? 0;
                          })()}
                          topic={currentQuestion.sectionTitle}
                          existingSolution={(() => {
                            const sol = currentQuestion.problem.solutions?.[0];
                            if (typeof sol === 'string') return sol;
                            return sol?.text || sol?.value;
                          })()}
                        />
                      </div>
                    )}

                    {/* Navigation */}
                    <div className="flex justify-between pt-4 border-t">
                      <Button
                        variant="outline"
                        onClick={() => setCurrentQuestionIndex((prev) => Math.max(0, prev - 1))}
                        disabled={currentQuestionIndex === 0}
                      >
                        <ChevronLeft className="h-4 w-4 mr-1" />
                        Previous
                      </Button>
                      <Button
                        onClick={() => setCurrentQuestionIndex((prev) => Math.min(filteredQuestions.length - 1, prev + 1))}
                        disabled={currentQuestionIndex >= filteredQuestions.length - 1}
                      >
                        Next
                        <ChevronRight className="h-4 w-4 ml-1" />
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              )}
            </>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}
