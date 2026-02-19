'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useQueryClient } from '@tanstack/react-query';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { useCategoryStore } from '@/stores/category-store';
import {
  useTestList,
  useMissedTests,
  useCompletedTests,
  savePaperInfo,
  paperKeys,
} from '@/hooks/use-papers';
import { BookOpen, Clock, Trophy, Play, Loader2, Crown } from 'lucide-react';
import type { PaperListItem, PaperStatus, TestType, PaperSubCategory } from '@/types/paper';

interface TestCardProps {
  test: PaperListItem;
  onStart: (test: PaperListItem) => void;
}

function TestCard({ test, onStart }: TestCardProps) {
  return (
    <Card className="hover:shadow-md transition-shadow">
      <CardHeader className="pb-2">
        <CardTitle className="text-lg">{test.paperName}</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="flex items-center gap-4 text-sm text-slate-500 mb-4">
          <div className="flex items-center gap-1">
            <Clock className="h-4 w-4" />
            {Math.floor(test.totalTime / 60)} min
          </div>
          <div className="flex items-center gap-1">
            <Trophy className="h-4 w-4" />
            {test.totalScore} marks
          </div>
        </div>
        <div className="flex items-center justify-between">
          <span
            className={`text-xs px-2 py-1 rounded-full ${
              test.testType === 'FREE'
                ? 'bg-green-100 text-green-700'
                : 'bg-orange-100 text-orange-700'
            }`}
          >
            {test.testType}
          </span>
          <Button size="sm" className="gap-1" onClick={() => onStart(test)}>
            <Play className="h-4 w-4" />
            {test.paperStatus === 'INPROGRESS' ? 'Resume' : 'Start'}
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}

function TestCardSkeleton() {
  return (
    <Card>
      <CardHeader className="pb-2">
        <Skeleton className="h-6 w-3/4" />
      </CardHeader>
      <CardContent>
        <div className="flex items-center gap-4 mb-4">
          <Skeleton className="h-4 w-16" />
          <Skeleton className="h-4 w-20" />
        </div>
        <div className="flex items-center justify-between">
          <Skeleton className="h-6 w-12" />
          <Skeleton className="h-8 w-20" />
        </div>
      </CardContent>
    </Card>
  );
}

interface EmptyStateProps {
  icon: React.ElementType;
  message: string;
  subMessage: string;
  action?: {
    label: string;
    onClick: () => void;
  };
}

function EmptyState({ icon: Icon, message, subMessage, action }: EmptyStateProps) {
  return (
    <div className="text-center py-12 text-slate-500">
      <Icon className="h-12 w-12 mx-auto mb-4 text-slate-300" />
      <p>{message}</p>
      <p className="text-sm">{subMessage}</p>
      {action && (
        <Button className="mt-4" onClick={action.onClick}>
          {action.label}
        </Button>
      )}
    </div>
  );
}

export default function HomePage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const rootCategory = useCategoryStore((state) => state.rootCategory);
  const childCategory = useCategoryStore((state) => state.childCategory);

  // Prevent hydration mismatch with Radix Tabs
  const [mounted, setMounted] = useState(false);
  useEffect(() => {
    setMounted(true);
  }, []);

  // Use default subcategory if not set
  const subCategory = childCategory || `${rootCategory}_TIER1` as PaperSubCategory;

  // Fetch all tests for category - backend returns both FREE and PAID
  const { data: allTests, isLoading: loadingTests } = useTestList(rootCategory, subCategory);
  const { data: completedTests, isLoading: loadingCompleted } = useCompletedTests(rootCategory, subCategory);
  const { data: missedTests, isLoading: loadingMissed } = useMissedTests(rootCategory, subCategory);

  // Filter tests by testType (client-side) - matches Angular behavior
  const freeTests = allTests?.filter((t) => t.testType === 'FREE') || [];
  const paidTests = allTests?.filter((t) => t.testType === 'PAID') || [];
  const loadingFree = loadingTests;
  const loadingPaid = loadingTests;

  const handleStartTest = (test: PaperListItem) => {
    // Determine paper status for API call
    const paperStatus: PaperStatus =
      test.paperStatus === 'INPROGRESS' ? 'INPROGRESS' : 'START';

    // Clear any cached paper data to ensure fresh fetch
    queryClient.removeQueries({ queryKey: paperKeys.detail(test.paperId) });
    console.log('[HomePage] Cleared cache for paper:', test.paperId);

    // Save paper info to session storage
    savePaperInfo({
      paperId: test.paperId,
      testType: test.testType,
      paperStatus,
      paperCategory: test.paperType,
      paperSubCategory: test.paperSubCategory,
      totalTime: test.totalTime,
      totalScore: test.totalScore,
      paperName: test.paperName,
    });

    // For in-progress tests, go directly to exam; for new tests, show instructions first
    if (test.paperStatus === 'INPROGRESS') {
      router.push(`/exam/${test.paperId}`);
    } else {
      router.push('/instruction');
    }
  };

  const handleViewCompleted = (test: PaperListItem) => {
    savePaperInfo({
      paperId: test.paperId,
      testType: test.testType,
      paperStatus: 'DONE',
      paperCategory: test.paperType,
      paperSubCategory: test.paperSubCategory,
      totalTime: test.totalTime,
      totalScore: test.totalScore,
      paperName: test.paperName,
      readOnly: true,
    });

    router.push(`/exam/${test.paperId}`);
  };

  const handleViewResults = (test: PaperListItem) => {
    router.push(`/home/results-analysis/${test.paperId}`);
  };

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Page Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-slate-900 mb-2">
          Welcome to EnsureU
        </h1>
        <p className="text-slate-600">
          Start practicing for {rootCategory.replace('_', ' ')} exams
        </p>
      </div>

      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
        {[
          {
            label: 'Tests Available',
            value: `${(freeTests?.length || 0) + (paidTests?.length || 0)}`,
            icon: BookOpen,
          },
          {
            label: 'Tests Completed',
            value: `${completedTests?.length || 0}`,
            icon: Trophy,
          },
          { label: 'Average Score', value: '78%', icon: Trophy },
          { label: 'Study Hours', value: '24h', icon: Clock },
        ].map((stat) => (
          <Card key={stat.label}>
            <CardContent className="flex items-center gap-4 p-4">
              <div className="p-3 bg-primary/10 rounded-lg">
                <stat.icon className="h-6 w-6 text-primary" />
              </div>
              <div>
                <p className="text-2xl font-bold text-slate-900">{stat.value}</p>
                <p className="text-sm text-slate-500">{stat.label}</p>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Test Tabs - render only after mount to prevent hydration mismatch */}
      {!mounted ? (
        <div className="space-y-6">
          <Skeleton className="h-10 w-96" />
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {[1, 2, 3].map((i) => (
              <Skeleton key={i} className="h-32" />
            ))}
          </div>
        </div>
      ) : (
      <Tabs id="home-test-tabs" defaultValue="free" className="space-y-6">
        <TabsList>
          <TabsTrigger value="free">
            Free Tests {freeTests?.length ? `(${freeTests.length})` : ''}
          </TabsTrigger>
          <TabsTrigger value="paid">
            Paid Tests {paidTests?.length ? `(${paidTests.length})` : ''}
          </TabsTrigger>
          <TabsTrigger value="completed">
            Completed {completedTests?.length ? `(${completedTests.length})` : ''}
          </TabsTrigger>
          <TabsTrigger value="missed">
            Missed {missedTests?.length ? `(${missedTests.length})` : ''}
          </TabsTrigger>
        </TabsList>

        <TabsContent value="free" className="space-y-4">
          {loadingFree ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {[1, 2, 3].map((i) => (
                <TestCardSkeleton key={i} />
              ))}
            </div>
          ) : freeTests && freeTests.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {freeTests.map((test) => (
                <TestCard key={test.paperId} test={test} onStart={handleStartTest} />
              ))}
            </div>
          ) : (
            <EmptyState
              icon={BookOpen}
              message="No free tests available"
              subMessage="Check back later for new tests"
            />
          )}
        </TabsContent>

        <TabsContent value="paid" className="space-y-4">
          {loadingPaid ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {[1, 2, 3].map((i) => (
                <TestCardSkeleton key={i} />
              ))}
            </div>
          ) : paidTests && paidTests.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {paidTests.map((test) => (
                <TestCard key={test.paperId} test={test} onStart={handleStartTest} />
              ))}
            </div>
          ) : (
            <EmptyState
              icon={Crown}
              message="No paid tests available"
              subMessage="Subscribe to access premium tests"
              action={{
                label: 'Browse Subscriptions',
                onClick: () => router.push('/home/subscription'),
              }}
            />
          )}
        </TabsContent>

        <TabsContent value="completed">
          {loadingCompleted ? (
            <div className="flex justify-center py-12">
              <Loader2 className="h-8 w-8 animate-spin text-primary" />
            </div>
          ) : completedTests && completedTests.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {completedTests.map((test) => (
                <Card key={test.paperId} className="hover:shadow-md transition-shadow">
                  <CardHeader className="pb-2">
                    <CardTitle className="text-lg">{test.paperName}</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="flex items-center gap-4 text-sm text-slate-500 mb-4">
                      <div className="flex items-center gap-1">
                        <Trophy className="h-4 w-4" />
                        {test.totalGetScore || 0}/{test.totalScore} marks
                      </div>
                    </div>
                    <Button
                      size="sm"
                      variant="outline"
                      className="w-full"
                      onClick={() => handleViewResults(test)}
                    >
                      View Results
                    </Button>
                    <Button
                      size="sm"
                      variant="secondary"
                      className="w-full mt-2"
                      onClick={() => handleViewCompleted(test)}
                    >
                      View Answers
                    </Button>
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : (
            <EmptyState
              icon={Trophy}
              message="No completed tests yet"
              subMessage="Start a test to see your results here"
            />
          )}
        </TabsContent>

        <TabsContent value="missed">
          {loadingMissed ? (
            <div className="flex justify-center py-12">
              <Loader2 className="h-8 w-8 animate-spin text-primary" />
            </div>
          ) : missedTests && missedTests.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {missedTests.map((test) => (
                <TestCard key={test.paperId} test={test} onStart={handleStartTest} />
              ))}
            </div>
          ) : (
            <EmptyState
              icon={Clock}
              message="No missed tests"
              subMessage="Great job staying on track!"
            />
          )}
        </TabsContent>
      </Tabs>
      )}
    </div>
  );
}
