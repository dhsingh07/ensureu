'use client';

import { useState } from 'react';
import {
  Calendar,
  Sparkles,
  Loader2,
  BookOpen,
  Target,
  Clock,
  CheckCircle2,
  AlertTriangle,
  ChevronDown,
  ChevronUp,
  Lightbulb,
  Trophy,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogFooter,
} from '@/components/ui/dialog';
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from '@/components/ui/collapsible';
import { useStudyPlan, useCachedStudyPlan } from '@/hooks/use-ai';
import { useAuthStore } from '@/stores/auth-store';
import type { StudyPlanRequest, StudyPlanResponse } from '@/types/ai';
import { cn } from '@/lib/utils';

interface StudyPlanDialogProps {
  examName?: string;
  weakTopics?: string[];
  strongTopics?: string[];
  trigger?: React.ReactNode;
}

const importanceColors: Record<string, string> = {
  critical: 'bg-red-100 text-red-700 border-red-200',
  important: 'bg-yellow-100 text-yellow-700 border-yellow-200',
  'good-to-know': 'bg-blue-100 text-blue-700 border-blue-200',
};

export function StudyPlanDialog({
  examName: defaultExamName,
  weakTopics = [],
  strongTopics = [],
  trigger,
}: StudyPlanDialogProps) {
  const [open, setOpen] = useState(false);
  const [showForm, setShowForm] = useState(true);
  const [formData, setFormData] = useState<StudyPlanRequest>({
    userId: '',
    examName: defaultExamName || '',
    examDate: '',
    currentLevel: 'intermediate',
    availableHoursPerDay: 4,
    weakTopics,
    strongTopics,
  });

  const user = useAuthStore((state) => state.user);
  const { mutate, isPending, data } = useStudyPlan();
  const { data: cachedData } = useCachedStudyPlan(
    user?.id || '',
    formData.examName,
    !!user?.id && !!formData.examName
  );

  const plan = data || cachedData;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!user?.id) return;

    mutate(
      { ...formData, userId: user.id },
      {
        onSuccess: () => {
          setShowForm(false);
        },
      }
    );
  };

  const handleOpen = (isOpen: boolean) => {
    setOpen(isOpen);
    if (!isOpen) {
      // Reset to form if closing
      if (!plan) {
        setShowForm(true);
      }
    }
  };

  return (
    <Dialog open={open} onOpenChange={handleOpen}>
      <DialogTrigger asChild>
        {trigger || (
          <Button
            variant="outline"
            className="gap-2 border-teal-200 text-teal-600 hover:bg-teal-50"
          >
            <Calendar className="h-4 w-4" />
            Generate Study Plan
          </Button>
        )}
      </DialogTrigger>
      <DialogContent className="max-w-3xl max-h-[85vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Sparkles className="h-5 w-5 text-teal-600" />
            AI Study Plan Generator
          </DialogTitle>
        </DialogHeader>

        {showForm && !plan ? (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="examName">Exam Name *</Label>
                <Input
                  id="examName"
                  value={formData.examName}
                  onChange={(e) =>
                    setFormData((prev) => ({ ...prev, examName: e.target.value }))
                  }
                  placeholder="e.g., SSC CGL 2024"
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="examDate">Target Exam Date</Label>
                <Input
                  id="examDate"
                  type="date"
                  value={formData.examDate}
                  onChange={(e) =>
                    setFormData((prev) => ({ ...prev, examDate: e.target.value }))
                  }
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="currentLevel">Current Level</Label>
                <Select
                  value={formData.currentLevel}
                  onValueChange={(value: 'beginner' | 'intermediate' | 'advanced') =>
                    setFormData((prev) => ({ ...prev, currentLevel: value }))
                  }
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="beginner">Beginner</SelectItem>
                    <SelectItem value="intermediate">Intermediate</SelectItem>
                    <SelectItem value="advanced">Advanced</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="hours">Available Hours/Day</Label>
                <Input
                  id="hours"
                  type="number"
                  min={1}
                  max={12}
                  value={formData.availableHoursPerDay}
                  onChange={(e) =>
                    setFormData((prev) => ({
                      ...prev,
                      availableHoursPerDay: parseInt(e.target.value) || 4,
                    }))
                  }
                />
              </div>
            </div>

            {(weakTopics.length > 0 || strongTopics.length > 0) && (
              <div className="space-y-2">
                <Label>Topics Identified from Your Performance</Label>
                <div className="flex flex-wrap gap-2">
                  {weakTopics.map((topic) => (
                    <Badge key={topic} variant="secondary" className="bg-red-100 text-red-700">
                      Weak: {topic}
                    </Badge>
                  ))}
                  {strongTopics.map((topic) => (
                    <Badge
                      key={topic}
                      variant="secondary"
                      className="bg-green-100 text-green-700"
                    >
                      Strong: {topic}
                    </Badge>
                  ))}
                </div>
              </div>
            )}

            <DialogFooter>
              <Button type="submit" disabled={isPending || !formData.examName} className="bg-gradient-to-r from-teal-500 to-cyan-600 hover:from-teal-600 hover:to-cyan-700">
                {isPending ? (
                  <>
                    <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                    Generating Plan...
                  </>
                ) : (
                  <>
                    <Sparkles className="h-4 w-4 mr-2" />
                    Generate My Plan
                  </>
                )}
              </Button>
            </DialogFooter>
          </form>
        ) : isPending ? (
          <div className="flex flex-col items-center justify-center py-16 space-y-4">
            <div className="relative">
              <Loader2 className="h-12 w-12 animate-spin text-teal-600" />
              <Calendar className="h-6 w-6 text-teal-400 absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2" />
            </div>
            <div className="text-center">
              <p className="font-medium">Creating your personalized study plan...</p>
              <p className="text-sm text-muted-foreground">
                Analyzing your goals and optimizing for maximum results
              </p>
            </div>
          </div>
        ) : plan ? (
          <StudyPlanContent plan={plan} onNewPlan={() => setShowForm(true)} />
        ) : (
          <div className="flex flex-col items-center justify-center py-12 text-center">
            <AlertTriangle className="h-12 w-12 text-gray-400 mb-4" />
            <p className="text-muted-foreground">
              Unable to generate study plan. Please try again.
            </p>
            <Button variant="outline" className="mt-4" onClick={() => setShowForm(true)}>
              Try Again
            </Button>
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
}

function StudyPlanContent({
  plan,
  onNewPlan,
}: {
  plan: StudyPlanResponse;
  onNewPlan: () => void;
}) {
  const [expandedWeeks, setExpandedWeeks] = useState<number[]>([1]);

  const toggleWeek = (week: number) => {
    setExpandedWeeks((prev) =>
      prev.includes(week) ? prev.filter((w) => w !== week) : [...prev, week]
    );
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between p-4 bg-gradient-to-br from-teal-50 to-cyan-50 rounded-lg">
        <div>
          <h3 className="text-lg font-semibold">{plan.examName}</h3>
          <p className="text-sm text-muted-foreground">
            Duration: {plan.planDuration}
          </p>
        </div>
        <Button variant="outline" size="sm" onClick={onNewPlan}>
          Regenerate Plan
        </Button>
      </div>

      {/* Milestones */}
      {plan.milestones && plan.milestones.length > 0 && (
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm flex items-center gap-2">
              <Trophy className="h-4 w-4 text-yellow-500" />
              Your Milestones
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center gap-2 overflow-x-auto pb-2">
              {plan.milestones.map((milestone, index) => (
                <div
                  key={index}
                  className="flex-shrink-0 p-3 bg-yellow-50 rounded-lg border border-yellow-200 min-w-[150px]"
                >
                  <div className="text-xs text-yellow-600 font-medium">
                    Week {milestone.week}
                  </div>
                  <div className="text-sm font-medium mt-1">{milestone.milestone}</div>
                  {milestone.targetScore && (
                    <div className="text-xs text-muted-foreground mt-1">
                      Target: {milestone.targetScore}%
                    </div>
                  )}
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      )}

      {/* Priority Topics */}
      <Card>
        <CardHeader className="pb-2">
          <CardTitle className="text-sm flex items-center gap-2">
            <Target className="h-4 w-4 text-teal-600" />
            Priority Topics
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-2">
          {plan.priorityTopics.map((topic, index) => (
            <div
              key={index}
              className={cn(
                'p-3 rounded-lg border flex items-center justify-between',
                importanceColors[topic.importance]
              )}
            >
              <div>
                <span className="font-medium">{topic.topic}</span>
                <span className="text-xs ml-2 opacity-75">({topic.suggestedTime})</span>
              </div>
              <Badge variant="outline" className="capitalize text-xs">
                {topic.importance.replace('-', ' ')}
              </Badge>
            </div>
          ))}
        </CardContent>
      </Card>

      {/* Weekly Schedule */}
      <div className="space-y-3">
        <h3 className="font-semibold flex items-center gap-2">
          <Calendar className="h-5 w-5 text-teal-600" />
          Weekly Schedule
        </h3>

        {plan.weeklySchedule.map((week) => (
          <Collapsible
            key={week.week}
            open={expandedWeeks.includes(week.week)}
            onOpenChange={() => toggleWeek(week.week)}
          >
            <Card>
              <CollapsibleTrigger asChild>
                <CardHeader className="cursor-pointer hover:bg-gray-50 transition-colors">
                  <div className="flex items-center justify-between">
                    <CardTitle className="text-sm flex items-center gap-2">
                      <BookOpen className="h-4 w-4" />
                      Week {week.week}: {week.focus}
                    </CardTitle>
                    {expandedWeeks.includes(week.week) ? (
                      <ChevronUp className="h-4 w-4 text-muted-foreground" />
                    ) : (
                      <ChevronDown className="h-4 w-4 text-muted-foreground" />
                    )}
                  </div>
                  <p className="text-xs text-muted-foreground text-left">
                    Goal: {week.weeklyGoal}
                  </p>
                </CardHeader>
              </CollapsibleTrigger>
              <CollapsibleContent>
                <CardContent className="pt-0 space-y-3">
                  {week.dailyTasks.map((day, dayIndex) => (
                    <div
                      key={dayIndex}
                      className="p-3 bg-gray-50 rounded-lg space-y-2"
                    >
                      <div className="flex items-center justify-between">
                        <span className="font-medium text-sm">{day.day}</span>
                        <span className="text-xs text-muted-foreground flex items-center gap-1">
                          <Clock className="h-3 w-3" />
                          {day.duration}
                        </span>
                      </div>
                      <div className="flex flex-wrap gap-1">
                        {day.topics.map((topic, i) => (
                          <Badge key={i} variant="secondary" className="text-xs">
                            {topic}
                          </Badge>
                        ))}
                      </div>
                      <ul className="text-xs text-muted-foreground space-y-1">
                        {day.activities.map((activity, i) => (
                          <li key={i} className="flex items-start gap-2">
                            <CheckCircle2 className="h-3 w-3 mt-0.5 text-green-500 flex-shrink-0" />
                            {activity}
                          </li>
                        ))}
                      </ul>
                    </div>
                  ))}
                  {week.assessment && (
                    <div className="p-3 bg-teal-50 rounded-lg border border-teal-200">
                      <span className="text-sm text-teal-700 font-medium">
                        Weekly Assessment: {week.assessment}
                      </span>
                    </div>
                  )}
                </CardContent>
              </CollapsibleContent>
            </Card>
          </Collapsible>
        ))}
      </div>

      {/* Tips */}
      {plan.tips && plan.tips.length > 0 && (
        <Card className="bg-amber-50 border-amber-200">
          <CardHeader className="pb-2">
            <CardTitle className="text-sm flex items-center gap-2 text-amber-700">
              <Lightbulb className="h-4 w-4" />
              Study Tips
            </CardTitle>
          </CardHeader>
          <CardContent>
            <ul className="space-y-2">
              {plan.tips.map((tip, index) => (
                <li key={index} className="text-sm text-amber-800 flex items-start gap-2">
                  <CheckCircle2 className="h-4 w-4 mt-0.5 flex-shrink-0" />
                  {tip}
                </li>
              ))}
            </ul>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
