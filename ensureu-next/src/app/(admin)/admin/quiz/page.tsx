'use client';

import { useState, useMemo } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Skeleton } from '@/components/ui/skeleton';
import { useQuizCollectionList, useCreateQuiz } from '@/hooks/use-quiz';
import { CATEGORIES } from '@/lib/constants/api-urls';
import type { Quiz, QuizStateStatus, QuizCreatePayload, QuizQuestion, QuizSection } from '@/types/quiz';
import type { PaperCategory, PaperType } from '@/types/paper';
import {
  Plus,
  Search,
  Clock,
  FileQuestion,
  Trophy,
  Zap,
  Edit,
  Eye,
  Loader2,
  CheckCircle2,
  XCircle,
  AlertCircle,
} from 'lucide-react';

function formatTime(seconds: number): string {
  const mins = Math.floor(seconds / 60);
  if (mins < 60) return `${mins} min`;
  const hrs = Math.floor(mins / 60);
  const remainMins = mins % 60;
  return `${hrs}h ${remainMins}m`;
}

function getStatusBadge(status: QuizStateStatus) {
  switch (status) {
    case 'ACTIVE':
      return <Badge className="bg-green-100 text-green-700">Active</Badge>;
    case 'APPROVED':
      return <Badge className="bg-blue-100 text-blue-700">Approved</Badge>;
    case 'DRAFT':
      return <Badge className="bg-slate-100 text-slate-700">Draft</Badge>;
    default:
      return <Badge variant="outline">{status}</Badge>;
  }
}

// Sample questions for demo (in real app, these would come from question bank)
const sampleQuestions: QuizQuestion[] = [
  {
    _id: 'q1',
    problem: {
      value: 'What is the capital of India?',
      options: [
        { prompt: 'A', value: 'Mumbai' },
        { prompt: 'B', value: 'New Delhi' },
        { prompt: 'C', value: 'Kolkata' },
        { prompt: 'D', value: 'Chennai' },
      ],
      co: 'B',
      solutions: [{ value: 'New Delhi is the capital of India.' }],
    },
  },
  // Add more sample questions as needed
];

interface CreateQuizFormProps {
  onSuccess: () => void;
  onCancel: () => void;
}

function CreateQuizForm({ onSuccess, onCancel }: CreateQuizFormProps) {
  const createQuiz = useCreateQuiz();
  const [formData, setFormData] = useState({
    paperName: '',
    paperType: 'SSC' as PaperType,
    paperCategory: 'SSC_CGL' as PaperCategory,
    totalQuestionCount: 10,
    totalScore: 10,
    totalTime: 600, // 10 minutes in seconds
    negativeMarks: 0,
    perQuestionScore: 1,
    paperStateStatus: 'DRAFT' as QuizStateStatus,
    priority: 1,
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Create pattern with empty sections (questions would be added separately)
    const pattern = {
      sections: [
        {
          sectionTitle: 'General',
          subSections: [
            {
              subSectionTitle: 'Questions',
              questions: sampleQuestions.slice(0, formData.totalQuestionCount),
            },
          ],
        },
      ],
    };

    const payload: QuizCreatePayload = {
      ...formData,
      pattern,
    };

    try {
      await createQuiz.mutateAsync(payload);
      onSuccess();
    } catch (error) {
      console.error('Failed to create quiz:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="grid gap-4 md:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="paperName">Quiz Name</Label>
          <Input
            id="paperName"
            value={formData.paperName}
            onChange={(e) => setFormData({ ...formData, paperName: e.target.value })}
            placeholder="SSC CGL Daily Quiz - Day 1"
            required
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="paperType">Paper Type</Label>
          <Select
            value={formData.paperType}
            onValueChange={(v) => setFormData({ ...formData, paperType: v as PaperType })}
          >
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="SSC">SSC</SelectItem>
              <SelectItem value="BANK">BANK</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="paperCategory">Category</Label>
          <Select
            value={formData.paperCategory}
            onValueChange={(v) => setFormData({ ...formData, paperCategory: v as PaperCategory })}
          >
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              {CATEGORIES.flatMap((group) =>
                group.subCategories.map((sub) => (
                  <SelectItem key={sub.name} value={sub.name}>
                    {sub.label}
                  </SelectItem>
                ))
              )}
            </SelectContent>
          </Select>
        </div>
        <div className="space-y-2">
          <Label htmlFor="status">Status</Label>
          <Select
            value={formData.paperStateStatus}
            onValueChange={(v) => setFormData({ ...formData, paperStateStatus: v as QuizStateStatus })}
          >
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="DRAFT">Draft</SelectItem>
              <SelectItem value="APPROVED">Approved</SelectItem>
              <SelectItem value="ACTIVE">Active</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      <div className="grid gap-4 md:grid-cols-4">
        <div className="space-y-2">
          <Label htmlFor="totalQuestionCount">Questions</Label>
          <Input
            id="totalQuestionCount"
            type="number"
            min={1}
            max={50}
            value={formData.totalQuestionCount}
            onChange={(e) =>
              setFormData({ ...formData, totalQuestionCount: parseInt(e.target.value) || 10 })
            }
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="totalTime">Time (minutes)</Label>
          <Input
            id="totalTime"
            type="number"
            min={1}
            max={120}
            value={Math.floor(formData.totalTime / 60)}
            onChange={(e) =>
              setFormData({ ...formData, totalTime: (parseInt(e.target.value) || 10) * 60 })
            }
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="perQuestionScore">Marks/Question</Label>
          <Input
            id="perQuestionScore"
            type="number"
            min={0.5}
            step={0.5}
            value={formData.perQuestionScore}
            onChange={(e) =>
              setFormData({ ...formData, perQuestionScore: parseFloat(e.target.value) || 1 })
            }
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="negativeMarks">Negative Marks</Label>
          <Input
            id="negativeMarks"
            type="number"
            min={0}
            step={0.25}
            value={formData.negativeMarks}
            onChange={(e) =>
              setFormData({ ...formData, negativeMarks: parseFloat(e.target.value) || 0 })
            }
          />
        </div>
      </div>

      <div className="flex justify-end gap-3 pt-4">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancel
        </Button>
        <Button type="submit" disabled={createQuiz.isPending}>
          {createQuiz.isPending ? (
            <>
              <Loader2 className="h-4 w-4 mr-2 animate-spin" />
              Creating...
            </>
          ) : (
            <>
              <Plus className="h-4 w-4 mr-2" />
              Create Quiz
            </>
          )}
        </Button>
      </div>
    </form>
  );
}

export default function AdminQuizPage() {
  const [page, setPage] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [showCreateDialog, setShowCreateDialog] = useState(false);

  const { data: quizData, isLoading, refetch } = useQuizCollectionList(page, 20);

  const filteredQuizzes = useMemo(() => {
    if (!quizData?.content) return [];
    if (!searchQuery) return quizData.content;
    return quizData.content.filter((quiz) =>
      quiz.paperName?.toLowerCase().includes(searchQuery.toLowerCase())
    );
  }, [quizData, searchQuery]);

  // Stats
  const stats = useMemo(() => {
    if (!quizData?.content) return { total: 0, active: 0, draft: 0 };
    const content = quizData.content;
    return {
      total: content.length,
      active: content.filter((q) => q.paperStateStatus === 'ACTIVE').length,
      draft: content.filter((q) => q.paperStateStatus === 'DRAFT').length,
    };
  }, [quizData]);

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-4 mb-8">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Quiz Management</h1>
          <p className="text-slate-600">Create and manage daily quizzes</p>
        </div>
        <Dialog open={showCreateDialog} onOpenChange={setShowCreateDialog}>
          <DialogTrigger asChild>
            <Button className="gap-2">
              <Plus className="h-4 w-4" />
              Create Quiz
            </Button>
          </DialogTrigger>
          <DialogContent className="max-w-2xl">
            <DialogHeader>
              <DialogTitle>Create New Quiz</DialogTitle>
              <DialogDescription>
                Create a new daily quiz for users to attempt
              </DialogDescription>
            </DialogHeader>
            <CreateQuizForm
              onSuccess={() => {
                setShowCreateDialog(false);
                refetch();
              }}
              onCancel={() => setShowCreateDialog(false)}
            />
          </DialogContent>
        </Dialog>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <div className="p-2 bg-blue-100 rounded-lg">
              <Zap className="h-5 w-5 text-blue-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-slate-900">{stats.total}</p>
              <p className="text-sm text-slate-500">Total Quizzes</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <div className="p-2 bg-green-100 rounded-lg">
              <CheckCircle2 className="h-5 w-5 text-green-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-slate-900">{stats.active}</p>
              <p className="text-sm text-slate-500">Active</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <div className="p-2 bg-slate-100 rounded-lg">
              <AlertCircle className="h-5 w-5 text-slate-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-slate-900">{stats.draft}</p>
              <p className="text-sm text-slate-500">Draft</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <div className="p-2 bg-purple-100 rounded-lg">
              <Trophy className="h-5 w-5 text-purple-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-slate-900">--</p>
              <p className="text-sm text-slate-500">Attempts Today</p>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Search and Filter */}
      <Card className="mb-6">
        <CardContent className="p-4">
          <div className="flex flex-wrap gap-4">
            <div className="relative flex-1 min-w-[200px]">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
              <Input
                placeholder="Search quizzes..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Quiz Table */}
      <Card>
        <CardContent className="p-0">
          {isLoading ? (
            <div className="p-6 space-y-4">
              {[1, 2, 3, 4, 5].map((i) => (
                <div key={i} className="flex gap-4">
                  <Skeleton className="h-12 w-full" />
                </div>
              ))}
            </div>
          ) : filteredQuizzes.length === 0 ? (
            <div className="p-12 text-center">
              <Zap className="h-12 w-12 mx-auto mb-4 text-slate-300" />
              <p className="text-slate-600 mb-2">No quizzes found</p>
              <p className="text-sm text-slate-500">
                Create your first quiz to get started
              </p>
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Quiz Name</TableHead>
                  <TableHead>Category</TableHead>
                  <TableHead className="text-center">Questions</TableHead>
                  <TableHead className="text-center">Time</TableHead>
                  <TableHead className="text-center">Marks</TableHead>
                  <TableHead className="text-center">Status</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredQuizzes.map((quiz) => (
                  <TableRow key={quiz.id}>
                    <TableCell className="font-medium">{quiz.paperName}</TableCell>
                    <TableCell>
                      <Badge variant="outline">
                        {quiz.paperCategory?.replace(/_/g, ' ')}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-center">
                      {quiz.totalQuestionCount}
                    </TableCell>
                    <TableCell className="text-center">
                      {formatTime(quiz.totalTime)}
                    </TableCell>
                    <TableCell className="text-center">{quiz.totalScore}</TableCell>
                    <TableCell className="text-center">
                      {getStatusBadge(quiz.paperStateStatus)}
                    </TableCell>
                    <TableCell className="text-right">
                      <div className="flex justify-end gap-2">
                        <Button variant="ghost" size="sm">
                          <Eye className="h-4 w-4" />
                        </Button>
                        <Button variant="ghost" size="sm">
                          <Edit className="h-4 w-4" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      {/* Pagination */}
      {quizData && quizData.totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-6">
          <Button
            variant="outline"
            size="sm"
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={page === 0}
          >
            Previous
          </Button>
          <span className="flex items-center px-4 text-sm text-slate-600">
            Page {page + 1} of {quizData.totalPages}
          </span>
          <Button
            variant="outline"
            size="sm"
            onClick={() => setPage((p) => p + 1)}
            disabled={page >= quizData.totalPages - 1}
          >
            Next
          </Button>
        </div>
      )}
    </div>
  );
}
