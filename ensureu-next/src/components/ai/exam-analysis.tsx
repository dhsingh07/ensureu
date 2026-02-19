'use client';

import { useState } from 'react';
import {
  Sparkles,
  TrendingUp,
  TrendingDown,
  Target,
  Clock,
  Award,
  Loader2,
  ChevronRight,
  AlertTriangle,
  CheckCircle2,
  Zap,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { Separator } from '@/components/ui/separator';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { useExamAnalysis, useCachedExamAnalysis } from '@/hooks/use-ai';
import type { ExamAnalysisRequest, ExamAnalysisResponse } from '@/types/ai';
import { cn } from '@/lib/utils';

interface ExamAnalysisProps {
  examData: ExamAnalysisRequest;
  trigger?: React.ReactNode;
  autoLoad?: boolean;
}

const ratingColors: Record<string, string> = {
  excellent: 'bg-green-500',
  good: 'bg-green-400',
  average: 'bg-yellow-500',
  needs_improvement: 'bg-orange-500',
  poor: 'bg-red-500',
};

const ratingLabels: Record<string, string> = {
  excellent: 'A+',
  good: 'A',
  average: 'B',
  needs_improvement: 'C',
  poor: 'D',
};

const priorityColors: Record<string, string> = {
  high: 'bg-red-100 text-red-700 border-red-200',
  medium: 'bg-yellow-100 text-yellow-700 border-yellow-200',
  low: 'bg-blue-100 text-blue-700 border-blue-200',
};

export function ExamAnalysisDialog({ examData, trigger, autoLoad = false }: ExamAnalysisProps) {
  const [open, setOpen] = useState(false);
  const { mutate, isPending, data } = useExamAnalysis();
  const { data: cachedData } = useCachedExamAnalysis(examData.examId, autoLoad);

  const analysis = data || cachedData;

  const handleOpen = (isOpen: boolean) => {
    setOpen(isOpen);
    if (isOpen && !analysis && !isPending) {
      mutate(examData);
    }
  };

  return (
    <Dialog open={open} onOpenChange={handleOpen}>
      <DialogTrigger asChild>
        {trigger || (
          <Button className="gap-2 bg-gradient-to-r from-teal-500 to-cyan-600 hover:from-teal-600 hover:to-cyan-700">
            <Sparkles className="h-4 w-4" />
            Get AI Analysis
          </Button>
        )}
      </DialogTrigger>
      <DialogContent className="sm:max-w-3xl max-h-[90vh] flex flex-col">
        <DialogHeader className="flex-shrink-0">
          <DialogTitle className="flex items-center gap-2">
            <Sparkles className="h-5 w-5 text-teal-600" />
            AI Performance Analysis
          </DialogTitle>
        </DialogHeader>

        <div className="flex-1 overflow-y-auto pr-2">
          {isPending ? (
            <div className="flex flex-col items-center justify-center py-16 space-y-4">
              <div className="relative">
                <Loader2 className="h-12 w-12 animate-spin text-teal-600" />
                <Sparkles className="h-6 w-6 text-teal-400 absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2" />
              </div>
              <div className="text-center">
                <p className="font-medium">Analyzing your performance...</p>
                <p className="text-sm text-muted-foreground">
                  Our AI is evaluating your answers and generating insights
                </p>
              </div>
            </div>
          ) : analysis ? (
            <ExamAnalysisContent analysis={analysis} />
          ) : (
            <div className="flex flex-col items-center justify-center py-12 text-center">
              <AlertTriangle className="h-12 w-12 text-gray-400 mb-4" />
              <p className="text-muted-foreground">
                Unable to generate analysis. Please try again.
              </p>
              <Button variant="outline" className="mt-4" onClick={() => mutate(examData)}>
                Retry Analysis
              </Button>
            </div>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
}

function ExamAnalysisContent({ analysis }: { analysis: ExamAnalysisResponse }) {
  const rating = analysis.overall_assessment?.performance_rating || 'average';
  const gradeLabel = ratingLabels[rating] || 'B';
  const gradeColor = ratingColors[rating] || 'bg-yellow-500';

  // Helper to normalize priority (can be number or string)
  const normalizePriority = (priority: number | string): string => {
    if (typeof priority === 'number') {
      return priority === 1 ? 'high' : priority === 2 ? 'medium' : 'low';
    }
    return String(priority).toLowerCase();
  };

  return (
    <div className="space-y-6">
      {/* Overall Performance */}
      <div className="flex items-center justify-between p-4 bg-gradient-to-br from-teal-50 to-cyan-50 rounded-lg">
        <div className="flex-1">
          <h3 className="text-lg font-semibold">Overall Performance</h3>
          <p className="text-sm text-muted-foreground mt-1 capitalize">
            {rating.replace(/_/g, ' ')}
          </p>
          {analysis.overall_assessment?.key_strength && (
            <p className="text-sm text-green-600 mt-2">
              <CheckCircle2 className="h-4 w-4 inline mr-1" />
              Strength: {analysis.overall_assessment.key_strength}
            </p>
          )}
          {analysis.overall_assessment?.critical_weakness && (
            <p className="text-sm text-orange-600 mt-1">
              <AlertTriangle className="h-4 w-4 inline mr-1" />
              Focus: {analysis.overall_assessment.critical_weakness}
            </p>
          )}
        </div>
        <div className="text-center ml-4">
          <div
            className={cn(
              'w-16 h-16 rounded-full flex items-center justify-center text-2xl font-bold text-white',
              gradeColor
            )}
          >
            {gradeLabel}
          </div>
        </div>
      </div>

      {/* Predicted Improvement */}
      {analysis.predicted_improvement && Object.keys(analysis.predicted_improvement).length > 0 && (
        <Card className="border-teal-200 bg-teal-50/50">
          <CardHeader className="pb-2">
            <CardTitle className="text-sm flex items-center gap-2">
              <Zap className="h-4 w-4 text-teal-600" />
              AI Improvement Prediction
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-2">
            {Object.entries(analysis.predicted_improvement).map(([key, value]) => (
              <div key={key} className="flex items-center justify-between">
                <span className="text-sm capitalize">{key.replace(/_/g, ' ')}:</span>
                <span className="font-semibold">{String(value)}</span>
              </div>
            ))}
          </CardContent>
        </Card>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* What Went Well */}
        <Card className="border-green-200">
          <CardHeader className="pb-2">
            <CardTitle className="text-sm flex items-center gap-2 text-green-700">
              <TrendingUp className="h-4 w-4" />
              What Went Well
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-2">
            {analysis.what_went_well?.length > 0 ? (
              analysis.what_went_well.map((item, index) => (
                <div key={index} className="flex items-start gap-2">
                  <CheckCircle2 className="h-4 w-4 text-green-500 mt-0.5 flex-shrink-0" />
                  <p className="text-sm">{item}</p>
                </div>
              ))
            ) : (
              <p className="text-sm text-muted-foreground">No specific strengths identified</p>
            )}
          </CardContent>
        </Card>

        {/* Areas of Concern */}
        <Card className="border-red-200">
          <CardHeader className="pb-2">
            <CardTitle className="text-sm flex items-center gap-2 text-red-700">
              <TrendingDown className="h-4 w-4" />
              Areas to Improve
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {analysis.areas_of_concern?.length > 0 ? (
              analysis.areas_of_concern.map((concern, index) => (
                <div key={index} className="space-y-1">
                  <div className="flex items-center justify-between">
                    <span className="font-medium text-sm">{concern.area}</span>
                    {concern.score && (
                      <Badge variant="secondary" className="bg-red-100 text-red-700">
                        {concern.score}
                      </Badge>
                    )}
                  </div>
                  <p className="text-xs text-muted-foreground">{concern.pattern}</p>
                  {concern.evidence && (
                    <p className="text-xs text-blue-600 flex items-center gap-1">
                      <ChevronRight className="h-3 w-3" />
                      {concern.evidence}
                    </p>
                  )}
                </div>
              ))
            ) : (
              <p className="text-sm text-muted-foreground">No specific concerns identified</p>
            )}
          </CardContent>
        </Card>
      </div>

      {/* Mistake Analysis */}
      {analysis.mistake_analysis && (
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm flex items-center gap-2">
              <Clock className="h-4 w-4 text-blue-600" />
              Mistake Analysis
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {analysis.mistake_analysis.conceptual_gaps && Object.keys(analysis.mistake_analysis.conceptual_gaps).length > 0 && (
              <div className="p-2 bg-orange-50 rounded">
                <span className="font-medium text-sm text-orange-700">Conceptual Gaps</span>
                <p className="text-xs text-muted-foreground mt-1">
                  {JSON.stringify(analysis.mistake_analysis.conceptual_gaps)}
                </p>
              </div>
            )}
            {analysis.mistake_analysis.careless_errors && Object.keys(analysis.mistake_analysis.careless_errors).length > 0 && (
              <div className="p-2 bg-yellow-50 rounded">
                <span className="font-medium text-sm text-yellow-700">Careless Errors</span>
                <p className="text-xs text-muted-foreground mt-1">
                  {JSON.stringify(analysis.mistake_analysis.careless_errors)}
                </p>
              </div>
            )}
            {analysis.mistake_analysis.time_management && Object.keys(analysis.mistake_analysis.time_management).length > 0 && (
              <div className="p-2 bg-blue-50 rounded">
                <span className="font-medium text-sm text-blue-700">Time Management</span>
                <p className="text-xs text-muted-foreground mt-1">
                  {JSON.stringify(analysis.mistake_analysis.time_management)}
                </p>
              </div>
            )}
          </CardContent>
        </Card>
      )}

      <Separator />

      {/* Action Items */}
      <div>
        <h3 className="font-semibold flex items-center gap-2 mb-4">
          <Target className="h-5 w-5 text-teal-600" />
          Your Action Plan
        </h3>
        <div className="space-y-3">
          {analysis.action_items?.length > 0 ? (
            analysis.action_items.map((item, index) => {
              const priority = normalizePriority(item.priority);
              return (
                <div
                  key={index}
                  className={cn(
                    'p-3 rounded-lg border flex items-start gap-3',
                    priorityColors[priority] || priorityColors.medium
                  )}
                >
                  <div className="flex-shrink-0 mt-0.5">
                    {priority === 'high' ? (
                      <AlertTriangle className="h-4 w-4" />
                    ) : priority === 'medium' ? (
                      <Target className="h-4 w-4" />
                    ) : (
                      <CheckCircle2 className="h-4 w-4" />
                    )}
                  </div>
                  <div className="flex-1">
                    <div className="flex items-center justify-between">
                      <span className="font-medium text-sm">{item.topic}</span>
                      <Badge variant="outline" className="text-xs capitalize">
                        {priority} priority
                      </Badge>
                    </div>
                    <p className="text-sm mt-1">{item.action}</p>
                    {item.expected_impact && (
                      <p className="text-xs mt-1 opacity-75">
                        Expected: {item.expected_impact}
                      </p>
                    )}
                    {item.estimated_time && (
                      <p className="text-xs opacity-75">
                        Time: {item.estimated_time}
                      </p>
                    )}
                  </div>
                </div>
              );
            })
          ) : (
            <p className="text-sm text-muted-foreground">No specific action items provided</p>
          )}
        </div>
      </div>
    </div>
  );
}

// Compact card for results page
export function ExamAnalysisCard({ examData }: { examData: ExamAnalysisRequest }) {
  const { data: analysis } = useCachedExamAnalysis(examData.examId);

  if (!analysis) {
    return (
      <Card className="border-teal-200 bg-gradient-to-br from-teal-50 to-cyan-50">
        <CardContent className="py-6 text-center">
          <Sparkles className="h-10 w-10 text-teal-400 mx-auto mb-3" />
          <h3 className="font-medium mb-2">Unlock AI Insights</h3>
          <p className="text-sm text-muted-foreground mb-4">
            Get personalized analysis of your performance with actionable recommendations
          </p>
          <ExamAnalysisDialog
            examData={examData}
            trigger={
              <Button className="gap-2 bg-gradient-to-r from-teal-500 to-cyan-600 hover:from-teal-600 hover:to-cyan-700">
                <Sparkles className="h-4 w-4" />
                Analyze My Performance
              </Button>
            }
          />
        </CardContent>
      </Card>
    );
  }

  const rating = analysis.overall_assessment?.performance_rating || 'average';
  const gradeLabel = ratingLabels[rating] || 'B';
  const gradeColor = ratingColors[rating] || 'bg-yellow-500';

  // Helper to normalize priority
  const normalizePriority = (priority: number | string): string => {
    if (typeof priority === 'number') {
      return priority === 1 ? 'high' : priority === 2 ? 'medium' : 'low';
    }
    return String(priority).toLowerCase();
  };

  // Filter high priority items
  const highPriorityItems = analysis.action_items?.filter(
    (item) => normalizePriority(item.priority) === 'high'
  ) || [];

  return (
    <Card className="border-teal-200">
      <CardHeader className="pb-2">
        <CardTitle className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Sparkles className="h-5 w-5 text-teal-600" />
            AI Analysis
          </div>
          <div
            className={cn(
              'w-10 h-10 rounded-full flex items-center justify-center text-lg font-bold text-white',
              gradeColor
            )}
          >
            {gradeLabel}
          </div>
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <p className="text-sm text-muted-foreground capitalize">
          {rating.replace(/_/g, ' ')}
          {analysis.overall_assessment?.critical_weakness && (
            <span className="block mt-1 text-orange-600">
              Focus: {analysis.overall_assessment.critical_weakness}
            </span>
          )}
        </p>

        {/* Top action items */}
        {highPriorityItems.length > 0 && (
          <div className="space-y-2">
            <h4 className="text-sm font-medium">Top Priorities</h4>
            {highPriorityItems.slice(0, 2).map((item, index) => (
              <div
                key={index}
                className="text-sm p-2 bg-red-50 rounded border border-red-100"
              >
                <span className="font-medium">{item.topic}:</span> {item.action}
              </div>
            ))}
          </div>
        )}

        <ExamAnalysisDialog
          examData={examData}
          trigger={
            <Button variant="outline" className="w-full gap-2">
              View Full Analysis
              <ChevronRight className="h-4 w-4" />
            </Button>
          }
        />
      </CardContent>
    </Card>
  );
}
