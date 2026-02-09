'use client';

import { useState } from 'react';
import {
  BarChart3,
  Clock,
  TrendingUp,
  TrendingDown,
  Target,
  AlertTriangle,
  CheckCircle2,
  Calendar,
  Sparkles,
  ChevronRight,
  ArrowUpRight,
  ArrowDownRight,
  Minus,
  Brain,
  BookOpen,
  Loader2,
} from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Separator } from '@/components/ui/separator';
import { useAuthStore } from '@/stores/auth-store';
import {
  useMonthlySummary,
  useAnalysisHistory,
  useWeakAreasAnalysis,
  type AnalysisHistoryItem,
  type MonthlySummary,
  type WeakAreasAnalysis,
} from '@/hooks/use-ai';
import { cn } from '@/lib/utils';
import Link from 'next/link';

const trendIcons = {
  improving: <ArrowUpRight className="h-4 w-4 text-green-500" />,
  declining: <ArrowDownRight className="h-4 w-4 text-red-500" />,
  stable: <Minus className="h-4 w-4 text-gray-500" />,
};

const trendColors = {
  improving: 'text-green-600 bg-green-50',
  declining: 'text-red-600 bg-red-50',
  stable: 'text-gray-600 bg-gray-50',
};

const priorityColors: Record<string, string> = {
  high: 'bg-red-100 text-red-700 border-red-200',
  medium: 'bg-yellow-100 text-yellow-700 border-yellow-200',
  low: 'bg-blue-100 text-blue-700 border-blue-200',
};

export default function ProgressPage() {
  const user = useAuthStore((state) => state.user);
  const userId = user?.id || user?.userName || '';

  const { data: monthlySummary, isLoading: loadingMonthly } = useMonthlySummary(userId, 6, !!userId);
  const { data: analysisHistory, isLoading: loadingHistory } = useAnalysisHistory(userId, 10, 0, !!userId);
  const { data: weakAreas, isLoading: loadingWeakAreas } = useWeakAreasAnalysis(userId, !!userId);

  const isLoading = loadingMonthly || loadingHistory || loadingWeakAreas;

  if (!userId) {
    return (
      <div className="container mx-auto py-12 px-4">
        <Card className="max-w-lg mx-auto">
          <CardContent className="pt-12 pb-12 text-center">
            <Brain className="h-12 w-12 text-gray-400 mx-auto mb-4" />
            <h2 className="text-xl font-semibold mb-2">Login Required</h2>
            <p className="text-muted-foreground mb-4">
              Please login to view your AI-powered progress analytics.
            </p>
            <Link href="/login">
              <Button>Login</Button>
            </Link>
          </CardContent>
        </Card>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="container mx-auto py-12 px-4">
        <div className="flex flex-col items-center justify-center py-20">
          <Loader2 className="h-10 w-10 animate-spin text-primary mb-4" />
          <p className="text-muted-foreground">Loading your AI analytics...</p>
        </div>
      </div>
    );
  }

  const hasData = monthlySummary?.total_analyses && monthlySummary.total_analyses > 0;

  if (!hasData) {
    return (
      <div className="container mx-auto py-12 px-4">
        <Card className="max-w-lg mx-auto">
          <CardContent className="pt-12 pb-12 text-center">
            <div className="w-20 h-20 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-6">
              <Sparkles className="h-10 w-10 text-primary" />
            </div>
            <h1 className="text-2xl font-bold text-slate-900 mb-2">
              No Analysis Data Yet
            </h1>
            <p className="text-slate-600 mb-6 max-w-sm mx-auto">
              Complete a test and click "Analyze My Performance" to get AI-powered insights and track your progress over time.
            </p>
            <Link href="/home">
              <Button className="gap-2">
                <BookOpen className="h-4 w-4" />
                Take a Test
              </Button>
            </Link>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-6 px-4 space-y-6">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold flex items-center gap-2">
            <Brain className="h-6 w-6 text-primary" />
            AI Progress Analytics
          </h1>
          <p className="text-muted-foreground">
            Track your performance trends and AI-powered recommendations
          </p>
        </div>
      </div>

      {/* Overview Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <OverviewCard
          title="Total Analyses"
          value={monthlySummary?.total_analyses || 0}
          icon={<BarChart3 className="h-5 w-5" />}
          subtitle="AI analyses performed"
        />
        <OverviewCard
          title="Score Trend"
          value={monthlySummary?.score_trend || 'stable'}
          icon={trendIcons[monthlySummary?.score_trend || 'stable']}
          subtitle="Overall performance"
          valueClassName={trendColors[monthlySummary?.score_trend || 'stable']}
          isText
        />
        <OverviewCard
          title="Weak Areas"
          value={weakAreas?.persistent_weak_areas?.length || 0}
          icon={<AlertTriangle className="h-5 w-5 text-orange-500" />}
          subtitle="Areas needing focus"
        />
        <OverviewCard
          title="Last Analysis"
          value={monthlySummary?.last_analysis ? formatDate(monthlySummary.last_analysis) : 'N/A'}
          icon={<Calendar className="h-5 w-5" />}
          subtitle="Most recent"
          isText
        />
      </div>

      {/* Main Content Tabs */}
      <Tabs defaultValue="monthly" className="space-y-4">
        <TabsList>
          <TabsTrigger value="monthly" className="gap-2">
            <Calendar className="h-4 w-4" />
            Monthly Progress
          </TabsTrigger>
          <TabsTrigger value="weakareas" className="gap-2">
            <Target className="h-4 w-4" />
            Weak Areas
          </TabsTrigger>
          <TabsTrigger value="history" className="gap-2">
            <Clock className="h-4 w-4" />
            Analysis History
          </TabsTrigger>
        </TabsList>

        {/* Monthly Progress Tab */}
        <TabsContent value="monthly" className="space-y-4">
          <MonthlyProgressSection summary={monthlySummary} />
        </TabsContent>

        {/* Weak Areas Tab */}
        <TabsContent value="weakareas" className="space-y-4">
          <WeakAreasSection weakAreas={weakAreas} />
        </TabsContent>

        {/* History Tab */}
        <TabsContent value="history" className="space-y-4">
          <HistorySection history={analysisHistory || []} />
        </TabsContent>
      </Tabs>
    </div>
  );
}

// Overview Card Component
function OverviewCard({
  title,
  value,
  icon,
  subtitle,
  valueClassName,
  isText = false,
}: {
  title: string;
  value: number | string;
  icon: React.ReactNode;
  subtitle: string;
  valueClassName?: string;
  isText?: boolean;
}) {
  return (
    <Card>
      <CardContent className="pt-6">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-muted-foreground">{title}</p>
            <p className={cn(
              'text-2xl font-bold mt-1',
              isText && 'text-lg capitalize',
              valueClassName
            )}>
              {value}
            </p>
            <p className="text-xs text-muted-foreground mt-1">{subtitle}</p>
          </div>
          <div className="p-3 bg-muted rounded-full">
            {icon}
          </div>
        </div>
      </CardContent>
    </Card>
  );
}

// Monthly Progress Section
function MonthlyProgressSection({ summary }: { summary?: MonthlySummary }) {
  if (!summary?.monthly_summaries?.length) {
    return (
      <Card>
        <CardContent className="py-12 text-center">
          <Calendar className="h-12 w-12 text-gray-400 mx-auto mb-4" />
          <p className="text-muted-foreground">No monthly data available yet.</p>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
      {/* Monthly Score Chart (Visual) */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg flex items-center gap-2">
            <TrendingUp className="h-5 w-5 text-primary" />
            Score Progression
          </CardTitle>
          <CardDescription>
            Average percentage by month
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {summary.monthly_summaries.map((month) => (
              <div key={month.month} className="space-y-2">
                <div className="flex items-center justify-between text-sm">
                  <span className="font-medium">{formatMonth(month.month)}</span>
                  <span className="text-muted-foreground">
                    {month.avg_percentage.toFixed(1)}% ({month.total_exams} tests)
                  </span>
                </div>
                <Progress value={month.avg_percentage} className="h-3" />
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* Monthly Details */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg flex items-center gap-2">
            <Calendar className="h-5 w-5 text-primary" />
            Monthly Breakdown
          </CardTitle>
          <CardDescription>
            Tests analyzed per month
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {summary.monthly_summaries.map((month) => (
              <div
                key={month.month}
                className="p-3 bg-muted/50 rounded-lg"
              >
                <div className="flex items-center justify-between mb-2">
                  <span className="font-semibold">{formatMonth(month.month)}</span>
                  <Badge variant="outline">
                    {month.total_exams} {month.total_exams === 1 ? 'test' : 'tests'}
                  </Badge>
                </div>
                <div className="space-y-1">
                  {month.exams.slice(0, 3).map((exam, idx) => (
                    <div key={idx} className="flex items-center justify-between text-sm">
                      <span className="text-muted-foreground truncate max-w-[200px]">
                        {exam.exam_name || exam.exam_id}
                      </span>
                      <Badge
                        variant="secondary"
                        className={cn(
                          exam.percentage >= 70 ? 'bg-green-100 text-green-700' :
                          exam.percentage >= 50 ? 'bg-yellow-100 text-yellow-700' :
                          'bg-red-100 text-red-700'
                        )}
                      >
                        {exam.percentage.toFixed(1)}%
                      </Badge>
                    </div>
                  ))}
                  {month.exams.length > 3 && (
                    <p className="text-xs text-muted-foreground">
                      +{month.exams.length - 3} more
                    </p>
                  )}
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

// Weak Areas Section
function WeakAreasSection({ weakAreas }: { weakAreas?: WeakAreasAnalysis }) {
  if (!weakAreas?.persistent_weak_areas?.length) {
    return (
      <Card>
        <CardContent className="py-12 text-center">
          <CheckCircle2 className="h-12 w-12 text-green-500 mx-auto mb-4" />
          <h3 className="font-semibold mb-2">No Persistent Weak Areas</h3>
          <p className="text-muted-foreground">
            Great job! No consistent problem areas identified across your tests.
          </p>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      <Card className="border-orange-200 bg-orange-50/30">
        <CardHeader>
          <CardTitle className="text-lg flex items-center gap-2">
            <AlertTriangle className="h-5 w-5 text-orange-500" />
            Focus Areas
          </CardTitle>
          <CardDescription>
            Areas that appear frequently across your analyses - prioritize these for improvement
          </CardDescription>
        </CardHeader>
      </Card>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {weakAreas.persistent_weak_areas.map((area, index) => (
          <Card key={area.area} className="relative overflow-hidden">
            <div className={cn(
              'absolute top-0 left-0 w-1 h-full',
              index === 0 ? 'bg-red-500' :
              index === 1 ? 'bg-orange-500' :
              'bg-yellow-500'
            )} />
            <CardContent className="pt-6 pl-6">
              <div className="flex items-start justify-between">
                <div className="space-y-2">
                  <h3 className="font-semibold text-lg">{area.area}</h3>
                  <div className="flex items-center gap-4 text-sm text-muted-foreground">
                    <span className="flex items-center gap-1">
                      <BarChart3 className="h-4 w-4" />
                      Appeared {area.frequency}x
                    </span>
                    <Badge
                      variant="outline"
                      className={
                        area.priority_score >= 2.5 ? 'border-red-300 text-red-700' :
                        area.priority_score >= 1.5 ? 'border-yellow-300 text-yellow-700' :
                        'border-blue-300 text-blue-700'
                      }
                    >
                      Priority: {area.priority_score >= 2.5 ? 'High' : area.priority_score >= 1.5 ? 'Medium' : 'Low'}
                    </Badge>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-3xl font-bold text-muted-foreground">
                    #{index + 1}
                  </div>
                </div>
              </div>

              {area.patterns.length > 0 && (
                <div className="mt-4">
                  <p className="text-sm font-medium mb-2">Common Patterns:</p>
                  <ul className="space-y-1">
                    {area.patterns.map((pattern, pIdx) => (
                      <li key={pIdx} className="text-sm text-muted-foreground flex items-start gap-2">
                        <ChevronRight className="h-4 w-4 mt-0.5 flex-shrink-0" />
                        <span>{pattern}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Recommendations based on weak areas */}
      <Card className="border-primary/20 bg-primary/5">
        <CardHeader>
          <CardTitle className="text-lg flex items-center gap-2">
            <Sparkles className="h-5 w-5 text-primary" />
            AI Recommendations
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {weakAreas.persistent_weak_areas.slice(0, 3).map((area, index) => (
              <div
                key={area.area}
                className={cn(
                  'p-3 rounded-lg border',
                  priorityColors[index === 0 ? 'high' : index === 1 ? 'medium' : 'low']
                )}
              >
                <div className="flex items-center gap-2 mb-1">
                  <Target className="h-4 w-4" />
                  <span className="font-medium">{area.area}</span>
                </div>
                <p className="text-sm">
                  Focus on practicing {area.area.toLowerCase()} questions.
                  This topic has appeared in {area.frequency} of your analyses as a concern.
                  Consider reviewing fundamentals and attempting topic-specific practice tests.
                </p>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

// History Section
function HistorySection({ history }: { history: AnalysisHistoryItem[] }) {
  if (!history.length) {
    return (
      <Card>
        <CardContent className="py-12 text-center">
          <Clock className="h-12 w-12 text-gray-400 mx-auto mb-4" />
          <p className="text-muted-foreground">No analysis history found.</p>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      {history.map((item) => (
        <Card key={item._id} className="hover:shadow-md transition-shadow">
          <CardContent className="pt-6">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <h3 className="font-semibold">
                    {item.exam_name || `Exam ${item.exam_id}`}
                  </h3>
                  <Badge
                    variant="secondary"
                    className={cn(
                      item.percentage >= 70 ? 'bg-green-100 text-green-700' :
                      item.percentage >= 50 ? 'bg-yellow-100 text-yellow-700' :
                      'bg-red-100 text-red-700'
                    )}
                  >
                    {item.percentage.toFixed(1)}%
                  </Badge>
                </div>
                <p className="text-sm text-muted-foreground">
                  Score: {item.score}/{item.total_marks} | Analyzed: {formatDateTime(item.analyzed_at)}
                </p>
              </div>

              <div className="flex items-center gap-4">
                {/* Performance Rating */}
                <div className="text-center">
                  <p className="text-xs text-muted-foreground">Rating</p>
                  <Badge variant="outline" className="capitalize">
                    {String(item.overall_assessment?.performance_rating || 'N/A').replace(/_/g, ' ')}
                  </Badge>
                </div>

                {/* Concerns Count */}
                <div className="text-center">
                  <p className="text-xs text-muted-foreground">Concerns</p>
                  <p className="font-semibold">{item.areas_of_concern?.length || 0}</p>
                </div>

                {/* Actions Count */}
                <div className="text-center">
                  <p className="text-xs text-muted-foreground">Actions</p>
                  <p className="font-semibold">{item.action_items?.length || 0}</p>
                </div>
              </div>
            </div>

            {/* What went well */}
            {item.what_went_well?.length > 0 && (
              <div className="mt-4 p-3 bg-green-50 rounded-lg">
                <p className="text-sm font-medium text-green-700 mb-1 flex items-center gap-1">
                  <CheckCircle2 className="h-4 w-4" />
                  What Went Well
                </p>
                <p className="text-sm text-green-600">
                  {item.what_went_well[0]}
                </p>
              </div>
            )}

            {/* Top Action Item */}
            {item.action_items?.length > 0 && (
              <div className="mt-3 p-3 bg-primary/5 rounded-lg">
                <p className="text-sm font-medium text-primary mb-1 flex items-center gap-1">
                  <Target className="h-4 w-4" />
                  Top Priority
                </p>
                <p className="text-sm">
                  <span className="font-medium">{item.action_items[0].topic}:</span>{' '}
                  {item.action_items[0].action}
                </p>
              </div>
            )}
          </CardContent>
        </Card>
      ))}
    </div>
  );
}

// Helper functions
function formatMonth(monthStr: string): string {
  try {
    const [year, month] = monthStr.split('-');
    const date = new Date(parseInt(year), parseInt(month) - 1);
    return date.toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
  } catch {
    return monthStr;
  }
}

function formatDate(dateStr: string): string {
  try {
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  } catch {
    return dateStr;
  }
}

function formatDateTime(dateStr: string): string {
  try {
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch {
    return dateStr;
  }
}
