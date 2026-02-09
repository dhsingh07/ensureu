'use client';

import { useEffect, useMemo, useState } from 'react';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Checkbox } from '@/components/ui/checkbox';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { getPaperInfo, clearPaperInfo } from '@/hooks/use-papers';
import { CATEGORIES } from '@/lib/constants/api-urls';
import {
  Clock,
  FileText,
  Trophy,
  AlertCircle,
  CheckCircle,
  XCircle,
  MinusCircle,
  ArrowLeft,
  Play,
  BookOpen,
  Languages,
  Save,
  Timer,
} from 'lucide-react';

const formatTime = (seconds?: number) => {
  if (!seconds) return '--';
  const hrs = Math.floor(seconds / 3600);
  const mins = Math.floor((seconds % 3600) / 60);
  if (hrs > 0) return `${hrs}h ${mins}m`;
  return `${mins} min`;
};

export default function InstructionPage() {
  const router = useRouter();
  const [accepted, setAccepted] = useState(false);
  const [paperInfo, setPaperInfo] = useState(getPaperInfo());

  useEffect(() => {
    const info = getPaperInfo();
    if (!info) {
      router.replace('/home');
      return;
    }
    if (info.testType === 'MISSED') {
      info.testType = 'PAID';
    }
    setPaperInfo(info);
  }, [router]);

  const categoryLabel = useMemo(() => {
    if (!paperInfo) return '';
    for (const group of CATEGORIES) {
      const found = group.subCategories.find((sub) => sub.name === paperInfo.paperCategory);
      if (found) return found.label;
    }
    return paperInfo.paperCategory?.replace(/_/g, ' ') || '';
  }, [paperInfo]);

  if (!paperInfo) return null;

  // Calculate estimated questions (if not available, show placeholder)
  const totalQuestions = paperInfo.totalScore ? Math.floor(paperInfo.totalScore / 2) : '--';
  const marksPerQuestion = 2;
  const negativeMarks = 0.5;

  return (
    <div className="min-h-screen bg-slate-50 py-8">
      <div className="container mx-auto px-4 max-w-4xl">
        {/* Header */}
        <div className="mb-6">
          <Button
            variant="ghost"
            className="mb-4 gap-2"
            onClick={() => {
              clearPaperInfo();
              router.back();
            }}
          >
            <ArrowLeft className="h-4 w-4" />
            Back to Tests
          </Button>

          <div className="flex flex-wrap items-start justify-between gap-4">
            <div>
              <Badge className="mb-2" variant={paperInfo.testType === 'FREE' ? 'secondary' : 'default'}>
                {paperInfo.testType}
              </Badge>
              <h1 className="text-2xl md:text-3xl font-bold text-slate-900">
                {paperInfo.paperName || `${categoryLabel} Mock Test`}
              </h1>
              <p className="text-slate-600 mt-1">{categoryLabel}</p>
            </div>
          </div>
        </div>

        {/* Test Overview Cards */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
          <Card>
            <CardContent className="p-4 flex items-center gap-3">
              <div className="p-2 bg-blue-100 rounded-lg">
                <Clock className="h-5 w-5 text-blue-600" />
              </div>
              <div>
                <p className="text-lg font-bold text-slate-900">{formatTime(paperInfo.totalTime)}</p>
                <p className="text-xs text-slate-500">Duration</p>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4 flex items-center gap-3">
              <div className="p-2 bg-purple-100 rounded-lg">
                <FileText className="h-5 w-5 text-purple-600" />
              </div>
              <div>
                <p className="text-lg font-bold text-slate-900">{totalQuestions}</p>
                <p className="text-xs text-slate-500">Questions</p>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4 flex items-center gap-3">
              <div className="p-2 bg-green-100 rounded-lg">
                <Trophy className="h-5 w-5 text-green-600" />
              </div>
              <div>
                <p className="text-lg font-bold text-slate-900">{paperInfo.totalScore ?? '--'}</p>
                <p className="text-xs text-slate-500">Max Marks</p>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-4 flex items-center gap-3">
              <div className="p-2 bg-orange-100 rounded-lg">
                <Languages className="h-5 w-5 text-orange-600" />
              </div>
              <div>
                <p className="text-lg font-bold text-slate-900">EN/HI</p>
                <p className="text-xs text-slate-500">Languages</p>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Marking Scheme */}
        <Card className="mb-6">
          <CardHeader className="pb-3">
            <CardTitle className="text-lg flex items-center gap-2">
              <AlertCircle className="h-5 w-5 text-orange-500" />
              Marking Scheme
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="flex items-center gap-3 p-3 bg-green-50 rounded-lg">
                <CheckCircle className="h-5 w-5 text-green-600" />
                <div>
                  <p className="font-semibold text-green-700">Correct Answer</p>
                  <p className="text-sm text-green-600">+{marksPerQuestion} marks</p>
                </div>
              </div>
              <div className="flex items-center gap-3 p-3 bg-red-50 rounded-lg">
                <XCircle className="h-5 w-5 text-red-600" />
                <div>
                  <p className="font-semibold text-red-700">Wrong Answer</p>
                  <p className="text-sm text-red-600">-{negativeMarks} marks</p>
                </div>
              </div>
              <div className="flex items-center gap-3 p-3 bg-slate-100 rounded-lg">
                <MinusCircle className="h-5 w-5 text-slate-600" />
                <div>
                  <p className="font-semibold text-slate-700">Unattempted</p>
                  <p className="text-sm text-slate-600">0 marks</p>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Instructions */}
        <Card className="mb-6">
          <CardHeader className="pb-3">
            <CardTitle className="text-lg flex items-center gap-2">
              <BookOpen className="h-5 w-5 text-blue-500" />
              Test Instructions
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-3">
              <div className="flex gap-3">
                <div className="flex-shrink-0 w-6 h-6 bg-primary text-white rounded-full flex items-center justify-center text-sm font-medium">1</div>
                <p className="text-slate-600">
                  The test contains <strong>multiple-choice questions (MCQs)</strong>. Each question has 4 options with one or more correct answers.
                </p>
              </div>
              <div className="flex gap-3">
                <div className="flex-shrink-0 w-6 h-6 bg-primary text-white rounded-full flex items-center justify-center text-sm font-medium">2</div>
                <p className="text-slate-600">
                  You can <strong>switch between English and Hindi</strong> language using the language toggle button during the test.
                </p>
              </div>
              <div className="flex gap-3">
                <div className="flex-shrink-0 w-6 h-6 bg-primary text-white rounded-full flex items-center justify-center text-sm font-medium">3</div>
                <p className="text-slate-600">
                  The <strong>countdown timer</strong> is displayed at the top. Keep track of remaining time while attempting questions.
                </p>
              </div>
              <div className="flex gap-3">
                <div className="flex-shrink-0 w-6 h-6 bg-primary text-white rounded-full flex items-center justify-center text-sm font-medium">4</div>
                <p className="text-slate-600">
                  Navigate between questions using <strong>Next/Previous buttons</strong> or click directly on the question palette.
                </p>
              </div>
              <div className="flex gap-3">
                <div className="flex-shrink-0 w-6 h-6 bg-primary text-white rounded-full flex items-center justify-center text-sm font-medium">5</div>
                <p className="text-slate-600">
                  Your answers are <strong>auto-saved every 5 minutes</strong>. You can also save manually using the Save button.
                </p>
              </div>
              <div className="flex gap-3">
                <div className="flex-shrink-0 w-6 h-6 bg-primary text-white rounded-full flex items-center justify-center text-sm font-medium">6</div>
                <p className="text-slate-600">
                  Use <strong>Mark for Review</strong> feature to flag questions you want to revisit before submitting.
                </p>
              </div>
              <div className="flex gap-3">
                <div className="flex-shrink-0 w-6 h-6 bg-primary text-white rounded-full flex items-center justify-center text-sm font-medium">7</div>
                <p className="text-slate-600">
                  The test will be <strong>auto-submitted</strong> when the time expires. Make sure to review your answers before time runs out.
                </p>
              </div>
            </div>

            <Separator />

            {/* Question Palette Legend */}
            <div>
              <p className="font-semibold text-slate-900 mb-3">Question Palette Legend:</p>
              <div className="flex flex-wrap gap-4">
                <div className="flex items-center gap-2">
                  <div className="w-6 h-6 bg-green-500 rounded"></div>
                  <span className="text-sm text-slate-600">Answered</span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="w-6 h-6 bg-red-500 rounded"></div>
                  <span className="text-sm text-slate-600">Not Answered</span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="w-6 h-6 bg-purple-500 rounded"></div>
                  <span className="text-sm text-slate-600">Marked for Review</span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="w-6 h-6 bg-slate-300 rounded"></div>
                  <span className="text-sm text-slate-600">Not Visited</span>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Declaration & Start */}
        <Card>
          <CardContent className="p-6">
            <div className="flex items-start gap-3 mb-6">
              <Checkbox
                id="confirm"
                checked={accepted}
                onCheckedChange={(value) => setAccepted(Boolean(value))}
                className="mt-1"
              />
              <label htmlFor="confirm" className="text-sm text-slate-700 cursor-pointer">
                I have read and understood all the instructions. I agree to abide by the rules of this test.
                I understand that any violation of rules may result in disqualification.
              </label>
            </div>

            <div className="flex flex-col sm:flex-row gap-3 justify-center">
              <Button
                variant="outline"
                size="lg"
                className="gap-2"
                onClick={() => {
                  clearPaperInfo();
                  router.back();
                }}
              >
                <ArrowLeft className="h-4 w-4" />
                Go Back
              </Button>
              <Button
                size="lg"
                className="gap-2 bg-green-600 hover:bg-green-700"
                onClick={() => router.push(`/exam/${paperInfo.paperId}`)}
                disabled={!accepted}
              >
                <Play className="h-4 w-4" />
                Start Test
              </Button>
            </div>

            {!accepted && (
              <p className="text-center text-sm text-orange-600 mt-4">
                Please accept the declaration to start the test
              </p>
            )}
          </CardContent>
        </Card>

        {/* Footer Note */}
        <p className="text-center text-sm text-slate-500 mt-6">
          All the best for your exam!
        </p>
      </div>
    </div>
  );
}
