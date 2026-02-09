'use client';

import { useParams, useRouter } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import { Loader2, ArrowLeft, AlertCircle } from 'lucide-react';
import { usePaperAnalysis, useUserAnalytics } from '@/hooks/use-papers';
import type { PaperCategory } from '@/types/paper';

function formatTime(seconds: number) {
  const hrs = Math.floor(seconds / 3600);
  const mins = Math.floor((seconds % 3600) / 60);
  const secs = seconds % 60;
  if (hrs > 0) {
    return `${hrs}h ${mins}m ${secs}s`;
  }
  return `${mins}m ${secs}s`;
}

export default function ProgressPage() {
  const params = useParams();
  const router = useRouter();
  const paperId = params.paperId as string;
  const paperCategory = params.paperCategory as PaperCategory;

  const { data: analysis, isLoading: loadingAnalysis } = usePaperAnalysis(paperId);
  const { data: analytics, isLoading: loadingAnalytics } = useUserAnalytics(paperCategory, paperId);

  if (loadingAnalysis) {
    return (
      <div className="container mx-auto px-4 py-8">
        <Button variant="ghost" onClick={() => router.push('/home')} className="mb-6 gap-2">
          <ArrowLeft className="h-4 w-4" />
          Back to Home
        </Button>
        <div className="flex flex-col items-center justify-center py-16">
          <Loader2 className="h-12 w-12 animate-spin text-primary mb-4" />
          <p className="text-slate-600">Loading progress...</p>
        </div>
      </div>
    );
  }

  if (!analysis) {
    return (
      <div className="container mx-auto px-4 py-8">
        <Button variant="ghost" onClick={() => router.push('/home')} className="mb-6 gap-2">
          <ArrowLeft className="h-4 w-4" />
          Back to Home
        </Button>
        <Card className="max-w-md mx-auto">
          <CardContent className="pt-6 text-center">
            <AlertCircle className="h-12 w-12 text-red-500 mx-auto mb-4" />
            <h2 className="text-xl font-semibold mb-2">Progress not available</h2>
            <p className="text-slate-600 mb-4">We couldn&apos;t load progress for this paper.</p>
            <Button onClick={() => router.push('/home')}>Return to Home</Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  const attempted = analysis.correctCount + analysis.incorrectCount;
  const accuracy = attempted > 0 ? Math.round((analysis.correctCount / attempted) * 100) : 0;
  const scorePercent = analysis.totalScore > 0 ? Math.round((analysis.obtainedScore / analysis.totalScore) * 100) : 0;

  return (
    <div className="container mx-auto px-4 py-8">
      <Button variant="ghost" onClick={() => router.push('/home')} className="mb-6 gap-2">
        <ArrowLeft className="h-4 w-4" />
        Back to Home
      </Button>

      <div className="mb-8">
        <h1 className="text-2xl font-bold text-slate-900 mb-2">Progress</h1>
        <p className="text-slate-600">{analysis.paperName}</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
        <Card>
          <CardContent className="p-6">
            <p className="text-sm text-slate-500">Score</p>
            <p className="text-3xl font-bold text-slate-900">
              {analysis.obtainedScore}/{analysis.totalScore}
            </p>
            <Progress value={scorePercent} className="h-2 mt-3" />
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-6">
            <p className="text-sm text-slate-500">Accuracy</p>
            <p className="text-3xl font-bold text-slate-900">{accuracy}%</p>
            <p className="text-xs text-slate-500 mt-2">
              {analysis.correctCount} / {attempted} attempted
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-6">
            <p className="text-sm text-slate-500">Time Taken</p>
            <p className="text-3xl font-bold text-slate-900">{formatTime(analysis.timeTaken)}</p>
            <p className="text-xs text-slate-500 mt-2">
              Avg {analysis.totalQuestions > 0 ? Math.round(analysis.timeTaken / analysis.totalQuestions) : 0}s / question
            </p>
          </CardContent>
        </Card>
      </div>

      {analysis.sections.length > 0 && (
        <Card className="mb-8">
          <CardHeader>
            <CardTitle>Section Summary</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <table className="min-w-full text-sm">
                <thead className="text-slate-500">
                  <tr>
                    <th className="text-left py-2">Section</th>
                    <th className="text-right py-2">Total</th>
                    <th className="text-right py-2">Correct</th>
                    <th className="text-right py-2">Incorrect</th>
                    <th className="text-right py-2">Skipped</th>
                    <th className="text-right py-2">Score</th>
                    <th className="text-right py-2">Time</th>
                  </tr>
                </thead>
                <tbody className="text-slate-700">
                  {analysis.sections.map((section, index) => (
                    <tr key={index} className="border-t">
                      <td className="py-2 pr-4">{section.title}</td>
                      <td className="py-2 text-right">{section.totalQuestions}</td>
                      <td className="py-2 text-right">{section.correct}</td>
                      <td className="py-2 text-right">{section.incorrect}</td>
                      <td className="py-2 text-right">{section.skipped}</td>
                      <td className="py-2 text-right">{section.score}</td>
                      <td className="py-2 text-right">{formatTime(section.timeTaken)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </CardContent>
        </Card>
      )}

      {loadingAnalytics ? (
        <div className="flex justify-center py-6">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : analytics ? (
        <div className="space-y-8">
          {analytics.userScoreDto && (
            <Card>
              <CardHeader>
                <CardTitle>Ranking</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div>
                    <p className="text-sm text-slate-500">Rank</p>
                    <p className="text-2xl font-bold">{analytics.userScoreDto.rank ?? '-'}</p>
                  </div>
                  <div>
                    <p className="text-sm text-slate-500">Score</p>
                    <p className="text-2xl font-bold">{analytics.userScoreDto.score ?? '-'}</p>
                  </div>
                  <div>
                    <p className="text-sm text-slate-500">Max Score</p>
                    <p className="text-2xl font-bold">{analytics.userScoreDto.maxPossibleScore ?? '-'}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {analytics.percentileList && analytics.percentileList.length > 0 && (
            <Card>
              <CardHeader>
                <CardTitle>Percentile Distribution</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="overflow-x-auto">
                  <table className="min-w-full text-sm">
                    <thead className="text-slate-500">
                      <tr>
                        <th className="text-left py-2">Rank</th>
                        <th className="text-right py-2">Marks</th>
                        <th className="text-right py-2">Percentile</th>
                      </tr>
                    </thead>
                    <tbody className="text-slate-700">
                      {analytics.percentileList.map((item, index) => (
                        <tr key={index} className="border-t">
                          <td className="py-2 pr-4">Rank {item.rank}</td>
                          <td className="py-2 text-right">{item.marks}</td>
                          <td className="py-2 text-right">{item.percentile}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      ) : (
        <Card>
          <CardContent className="p-6 text-sm text-slate-500">
            Advanced analytics are not available for this paper yet.
          </CardContent>
        </Card>
      )}
    </div>
  );
}
