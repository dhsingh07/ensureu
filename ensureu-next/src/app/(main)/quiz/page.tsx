'use client';

import { useMemo, useState } from 'react';
import { useRouter } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Skeleton } from '@/components/ui/skeleton';
import { useCategoryStore } from '@/stores/category-store';
import { useQuizList, useCompletedQuizzes, saveQuizInfo } from '@/hooks/use-quiz';
import { CATEGORIES } from '@/lib/constants/api-urls';
import type { QuizListItem } from '@/types/quiz';
import type { PaperCategory } from '@/types/paper';
import {
  Clock,
  Trophy,
  Play,
  CheckCircle2,
  Target,
  Zap,
  Calendar,
  Award,
  FileQuestion,
  Loader2,
} from 'lucide-react';

function formatTime(seconds: number): string {
  const mins = Math.floor(seconds / 60);
  if (mins < 60) return `${mins} min`;
  const hrs = Math.floor(mins / 60);
  const remainMins = mins % 60;
  return `${hrs}h ${remainMins}m`;
}

interface QuizCardProps {
  quiz: QuizListItem;
  onStart: (quiz: QuizListItem) => void;
  isCompleted?: boolean;
}

function QuizCard({ quiz, onStart, isCompleted }: QuizCardProps) {
  return (
    <Card className="hover:shadow-md transition-shadow border-l-4 border-l-primary">
      <CardContent className="p-5">
        <div className="flex items-start justify-between gap-3">
          <div className="flex-1">
            <h3 className="font-semibold text-slate-900 mb-2">{quiz.paperName}</h3>
            <div className="flex flex-wrap items-center gap-3 text-sm text-slate-500 mb-3">
              <div className="flex items-center gap-1">
                <FileQuestion className="h-4 w-4" />
                {quiz.totalQuestionCount} Qs
              </div>
              <div className="flex items-center gap-1">
                <Clock className="h-4 w-4" />
                {formatTime(quiz.totalTime)}
              </div>
              <div className="flex items-center gap-1">
                <Trophy className="h-4 w-4" />
                {quiz.totalScore} marks
              </div>
            </div>
            {isCompleted && quiz.totalGetScore !== undefined && (
              <div className="flex items-center gap-2">
                <Badge variant="outline" className="bg-green-50 text-green-700 border-green-200">
                  Score: {quiz.totalGetScore}/{quiz.totalScore}
                </Badge>
                {quiz.totalCorrectCount !== undefined && (
                  <Badge variant="outline" className="bg-blue-50 text-blue-700 border-blue-200">
                    {quiz.totalCorrectCount} correct
                  </Badge>
                )}
              </div>
            )}
          </div>
          <div className="flex flex-col items-end gap-2">
            {quiz.negativeMarks > 0 && (
              <Badge variant="outline" className="text-xs">
                -{quiz.negativeMarks} negative
              </Badge>
            )}
            <Button
              size="sm"
              className="gap-1"
              variant={isCompleted ? 'outline' : 'default'}
              onClick={() => onStart(quiz)}
            >
              {isCompleted ? (
                <>
                  <CheckCircle2 className="h-4 w-4" />
                  View
                </>
              ) : quiz.paperStatus === 'INPROGRESS' ? (
                <>
                  <Play className="h-4 w-4" />
                  Resume
                </>
              ) : (
                <>
                  <Play className="h-4 w-4" />
                  Start
                </>
              )}
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}

function QuizCardSkeleton() {
  return (
    <Card>
      <CardContent className="p-5">
        <Skeleton className="h-5 w-3/4 mb-3" />
        <div className="flex gap-3 mb-3">
          <Skeleton className="h-4 w-16" />
          <Skeleton className="h-4 w-16" />
          <Skeleton className="h-4 w-20" />
        </div>
        <div className="flex justify-end">
          <Skeleton className="h-8 w-20" />
        </div>
      </CardContent>
    </Card>
  );
}

export default function QuizPage() {
  const router = useRouter();
  const rootCategory = useCategoryStore((state) => state.rootCategory);
  const [activeTab, setActiveTab] = useState<'available' | 'completed'>('available');

  // Get category label
  const categoryLabel = useMemo(() => {
    for (const group of CATEGORIES) {
      const found = group.subCategories.find((sub) => sub.name === rootCategory);
      if (found) return found.label;
    }
    return rootCategory.replace(/_/g, ' ');
  }, [rootCategory]);

  // Get paper type from category
  const paperType = rootCategory.startsWith('BANK') ? 'BANK' : 'SSC';

  // Fetch quizzes
  const { data: availableQuizzes, isLoading: loadingAvailable } = useQuizList(
    paperType as 'SSC' | 'BANK',
    rootCategory as PaperCategory
  );
  const { data: completedQuizzes, isLoading: loadingCompleted } = useCompletedQuizzes(
    paperType as 'SSC' | 'BANK',
    rootCategory as PaperCategory
  );

  const handleStartQuiz = (quiz: QuizListItem) => {
    // Use paperId (quiz collection ID) for navigation, not id (user attempt ID)
    const quizCollectionId = quiz.paperId || quiz.id || '';

    // Save quiz info to session
    saveQuizInfo({
      quizId: quizCollectionId,
      paperName: quiz.paperName,
      paperCategory: quiz.paperCategory,
      totalTime: quiz.totalTime,
      totalScore: quiz.totalScore,
      totalQuestionCount: quiz.totalQuestionCount,
      negativeMarks: quiz.negativeMarks,
      perQuestionScore: quiz.perQuestionScore,
    });

    // Navigate to quiz using the collection ID
    router.push(`/quiz/${quizCollectionId}`);
  };

  const handleViewCompleted = (quiz: QuizListItem) => {
    // Use paperId (quiz collection ID) for navigation
    const quizCollectionId = quiz.paperId || quiz.id || '';

    saveQuizInfo({
      quizId: quizCollectionId,
      paperName: quiz.paperName,
      paperCategory: quiz.paperCategory,
      totalTime: quiz.totalTime,
      totalScore: quiz.totalScore,
      totalQuestionCount: quiz.totalQuestionCount,
      negativeMarks: quiz.negativeMarks,
      perQuestionScore: quiz.perQuestionScore,
    });

    router.push(`/quiz/${quizCollectionId}?view=result`);
  };

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Header */}
      <div className="mb-8">
        <div className="flex items-center gap-3 mb-2">
          <div className="p-2 bg-primary/10 rounded-lg">
            <Zap className="h-6 w-6 text-primary" />
          </div>
          <h1 className="text-2xl md:text-3xl font-bold text-slate-900">
            Daily Quiz
          </h1>
        </div>
        <p className="text-slate-600">
          Quick 10-minute quizzes to test your {categoryLabel} preparation
        </p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <div className="p-2 bg-blue-100 rounded-lg">
              <Target className="h-5 w-5 text-blue-600" />
            </div>
            <div>
              <p className="text-lg font-bold text-slate-900">
                {availableQuizzes?.length || 0}
              </p>
              <p className="text-xs text-slate-500">Available</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <div className="p-2 bg-green-100 rounded-lg">
              <CheckCircle2 className="h-5 w-5 text-green-600" />
            </div>
            <div>
              <p className="text-lg font-bold text-slate-900">
                {completedQuizzes?.length || 0}
              </p>
              <p className="text-xs text-slate-500">Completed</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <div className="p-2 bg-orange-100 rounded-lg">
              <Clock className="h-5 w-5 text-orange-600" />
            </div>
            <div>
              <p className="text-lg font-bold text-slate-900">10</p>
              <p className="text-xs text-slate-500">Min/Quiz</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <div className="p-2 bg-purple-100 rounded-lg">
              <Award className="h-5 w-5 text-purple-600" />
            </div>
            <div>
              <p className="text-lg font-bold text-slate-900">10</p>
              <p className="text-xs text-slate-500">Marks/Quiz</p>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Quiz Tabs */}
      <Tabs id="quiz-tabs" value={activeTab} onValueChange={(v) => setActiveTab(v as typeof activeTab)}>
        <TabsList className="mb-6">
          <TabsTrigger value="available" className="gap-2">
            <Target className="h-4 w-4" />
            Available Quizzes
            {availableQuizzes?.length ? ` (${availableQuizzes.length})` : ''}
          </TabsTrigger>
          <TabsTrigger value="completed" className="gap-2">
            <CheckCircle2 className="h-4 w-4" />
            Completed
            {completedQuizzes?.length ? ` (${completedQuizzes.length})` : ''}
          </TabsTrigger>
        </TabsList>

        <TabsContent value="available">
          {loadingAvailable ? (
            <div className="grid gap-4 md:grid-cols-2">
              {[1, 2, 3, 4].map((i) => (
                <QuizCardSkeleton key={i} />
              ))}
            </div>
          ) : availableQuizzes && availableQuizzes.length > 0 ? (
            <div className="grid gap-4 md:grid-cols-2">
              {availableQuizzes.map((quiz) => (
                <QuizCard
                  key={quiz.id || quiz.paperId}
                  quiz={quiz}
                  onStart={handleStartQuiz}
                />
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="p-12 text-center">
                <Zap className="h-12 w-12 mx-auto mb-4 text-slate-300" />
                <p className="text-slate-600 mb-2">No quizzes available</p>
                <p className="text-sm text-slate-500">
                  Check back later for new {categoryLabel} quizzes
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="completed">
          {loadingCompleted ? (
            <div className="flex justify-center py-12">
              <Loader2 className="h-8 w-8 animate-spin text-primary" />
            </div>
          ) : completedQuizzes && completedQuizzes.length > 0 ? (
            <div className="grid gap-4 md:grid-cols-2">
              {completedQuizzes.map((quiz) => (
                <QuizCard
                  key={quiz.id || quiz.paperId}
                  quiz={quiz}
                  onStart={handleViewCompleted}
                  isCompleted
                />
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="p-12 text-center">
                <Trophy className="h-12 w-12 mx-auto mb-4 text-slate-300" />
                <p className="text-slate-600 mb-2">No completed quizzes yet</p>
                <p className="text-sm text-slate-500">
                  Start a quiz to see your results here
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}
