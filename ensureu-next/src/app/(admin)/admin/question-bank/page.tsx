'use client';

import { useState } from 'react';
import { Plus, Search, Filter, CheckCircle, XCircle, Clock, FileText, AlertCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
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
import { useQuestionBankList, useQuestionBankStats, useApproveQuestion, useRejectQuestion, useDeleteQuestion } from '@/hooks/use-question-bank';
import { useAuthStore } from '@/stores/auth-store';
import { QuestionBankItem, QuestionBankStatus, DifficultyLevel, SSC_CGL_TIER1_SUBJECTS } from '@/types/question-bank';
import { PaperCategory, PaperSubCategory } from '@/types/paper';
import Link from 'next/link';

const statusConfig: Record<QuestionBankStatus, { label: string; color: string; icon: React.ReactNode }> = {
  DRAFT: { label: 'Draft', color: 'bg-gray-100 text-gray-800', icon: <FileText className="w-3 h-3" /> },
  PENDING_REVIEW: { label: 'Pending Review', color: 'bg-yellow-100 text-yellow-800', icon: <Clock className="w-3 h-3" /> },
  APPROVED: { label: 'Approved', color: 'bg-green-100 text-green-800', icon: <CheckCircle className="w-3 h-3" /> },
  REJECTED: { label: 'Rejected', color: 'bg-red-100 text-red-800', icon: <XCircle className="w-3 h-3" /> },
  ARCHIVED: { label: 'Archived', color: 'bg-gray-200 text-gray-600', icon: <AlertCircle className="w-3 h-3" /> },
};

const difficultyConfig: Record<DifficultyLevel, { label: string; color: string }> = {
  EASY: { label: 'Easy', color: 'bg-green-100 text-green-800' },
  MEDIUM: { label: 'Medium', color: 'bg-yellow-100 text-yellow-800' },
  HARD: { label: 'Hard', color: 'bg-red-100 text-red-800' },
};

function StatsCards() {
  const { data: stats, isLoading } = useQuestionBankStats();
  const { hasRole } = useAuthStore();
  const isAdmin = hasRole('ADMIN') || hasRole('SUPERADMIN');

  if (isLoading) {
    return (
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-5 mb-6">
        {[...Array(5)].map((_, i) => (
          <Card key={i}>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <Skeleton className="h-4 w-20" />
            </CardHeader>
            <CardContent>
              <Skeleton className="h-8 w-12" />
            </CardContent>
          </Card>
        ))}
      </div>
    );
  }

  const myStats = [
    { label: 'My Total', value: stats?.myTotalQuestions || 0, icon: FileText, color: 'text-blue-600' },
    { label: 'My Draft', value: stats?.myDraftCount || 0, icon: FileText, color: 'text-gray-600' },
    { label: 'My Pending', value: stats?.myPendingCount || 0, icon: Clock, color: 'text-yellow-600' },
    { label: 'My Approved', value: stats?.myApprovedCount || 0, icon: CheckCircle, color: 'text-green-600' },
    { label: 'My Rejected', value: stats?.myRejectedCount || 0, icon: XCircle, color: 'text-red-600' },
  ];

  const adminStats = [
    { label: 'Total Questions', value: stats?.totalQuestions || 0, icon: FileText, color: 'text-blue-600' },
    { label: 'Draft', value: stats?.draftCount || 0, icon: FileText, color: 'text-gray-600' },
    { label: 'Pending Review', value: stats?.pendingReviewCount || 0, icon: Clock, color: 'text-yellow-600' },
    { label: 'Approved', value: stats?.approvedCount || 0, icon: CheckCircle, color: 'text-green-600' },
    { label: 'Rejected', value: stats?.rejectedCount || 0, icon: XCircle, color: 'text-red-600' },
  ];

  const displayStats = isAdmin ? adminStats : myStats;

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-5 mb-6">
      {displayStats.map((stat) => (
        <Card key={stat.label}>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">{stat.label}</CardTitle>
            <stat.icon className={`h-4 w-4 ${stat.color}`} />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stat.value}</div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}

function QuestionTable({ status }: { status?: QuestionBankStatus }) {
  const [page, setPage] = useState(0);
  const [filters, setFilters] = useState({
    paperCategory: '' as PaperCategory | '',
    paperSubCategory: '' as PaperSubCategory | '',
    subject: '',
    difficultyLevel: '' as DifficultyLevel | '',
  });
  const [searchQuery, setSearchQuery] = useState('');
  const [rejectDialogOpen, setRejectDialogOpen] = useState(false);
  const [rejectReason, setRejectReason] = useState('');
  const [selectedQuestion, setSelectedQuestion] = useState<QuestionBankItem | null>(null);

  const { hasRole } = useAuthStore();
  const isAdmin = hasRole('ADMIN') || hasRole('SUPERADMIN');

  const { data, isLoading } = useQuestionBankList({
    status,
    paperCategory: filters.paperCategory || undefined,
    paperSubCategory: filters.paperSubCategory || undefined,
    subject: filters.subject || undefined,
    difficultyLevel: filters.difficultyLevel || undefined,
    page,
    size: 20,
  });

  const approveQuestion = useApproveQuestion();
  const rejectQuestion = useRejectQuestion();
  const deleteQuestion = useDeleteQuestion();

  const handleApprove = async (id: string) => {
    try {
      await approveQuestion.mutateAsync(id);
    } catch (error) {
      console.error('Failed to approve question:', error);
    }
  };

  const handleReject = async () => {
    if (!selectedQuestion || !rejectReason) return;
    try {
      await rejectQuestion.mutateAsync({ id: selectedQuestion.id, reason: rejectReason });
      setRejectDialogOpen(false);
      setRejectReason('');
      setSelectedQuestion(null);
    } catch (error) {
      console.error('Failed to reject question:', error);
    }
  };

  const handleDelete = async (id: string) => {
    if (!confirm('Are you sure you want to delete this question?')) return;
    try {
      await deleteQuestion.mutateAsync(id);
    } catch (error) {
      console.error('Failed to delete question:', error);
    }
  };

  const truncateText = (text: string, maxLength: number) => {
    const plainText = text.replace(/<[^>]*>/g, '');
    if (plainText.length <= maxLength) return plainText;
    return plainText.substring(0, maxLength) + '...';
  };

  if (isLoading) {
    return (
      <div className="space-y-4">
        {[...Array(5)].map((_, i) => (
          <Skeleton key={i} className="h-16 w-full" />
        ))}
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Filters */}
      <div className="flex flex-wrap gap-4">
        <Select
          value={filters.paperCategory || '__all__'}
          onValueChange={(v) => setFilters({ ...filters, paperCategory: v === '__all__' ? '' : v as PaperCategory })}
        >
          <SelectTrigger className="w-[180px]">
            <SelectValue placeholder="Category" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="__all__">All Categories</SelectItem>
            <SelectItem value="SSC_CGL">SSC CGL</SelectItem>
            <SelectItem value="SSC_CPO">SSC CPO</SelectItem>
            <SelectItem value="SSC_CHSL">SSC CHSL</SelectItem>
            <SelectItem value="BANK_PO">BANK PO</SelectItem>
          </SelectContent>
        </Select>

        <Select
          value={filters.subject || '__all__'}
          onValueChange={(v) => setFilters({ ...filters, subject: v === '__all__' ? '' : v })}
        >
          <SelectTrigger className="w-[250px]">
            <SelectValue placeholder="Subject" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="__all__">All Subjects</SelectItem>
            {SSC_CGL_TIER1_SUBJECTS.map((s) => (
              <SelectItem key={s.name} value={s.name}>{s.name}</SelectItem>
            ))}
          </SelectContent>
        </Select>

        <Select
          value={filters.difficultyLevel || '__all__'}
          onValueChange={(v) => setFilters({ ...filters, difficultyLevel: v === '__all__' ? '' : v as DifficultyLevel })}
        >
          <SelectTrigger className="w-[150px]">
            <SelectValue placeholder="Difficulty" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="__all__">All Levels</SelectItem>
            <SelectItem value="EASY">Easy</SelectItem>
            <SelectItem value="MEDIUM">Medium</SelectItem>
            <SelectItem value="HARD">Hard</SelectItem>
          </SelectContent>
        </Select>

        <div className="relative flex-1 min-w-[200px]">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
          <Input
            placeholder="Search questions..."
            className="pl-9"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      {/* Table */}
      <div className="border rounded-lg">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="w-[100px]">ID</TableHead>
              <TableHead>Question</TableHead>
              <TableHead>Subject</TableHead>
              <TableHead>Topic</TableHead>
              <TableHead>Difficulty</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Created By</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {data?.content?.length === 0 ? (
              <TableRow>
                <TableCell colSpan={8} className="text-center py-8 text-gray-500">
                  No questions found
                </TableCell>
              </TableRow>
            ) : (
              data?.content?.map((question) => (
                <TableRow key={question.id}>
                  <TableCell className="font-mono text-xs">{question.questionId}</TableCell>
                  <TableCell className="max-w-[300px]">
                    {truncateText(question.problem.question, 60)}
                  </TableCell>
                  <TableCell className="text-sm">{question.subject?.split(' ').slice(0, 2).join(' ')}</TableCell>
                  <TableCell className="text-sm">{question.topic}</TableCell>
                  <TableCell>
                    <Badge className={difficultyConfig[question.difficultyLevel]?.color}>
                      {difficultyConfig[question.difficultyLevel]?.label}
                    </Badge>
                  </TableCell>
                  <TableCell>
                    <Badge className={`${statusConfig[question.status]?.color} flex items-center gap-1 w-fit`}>
                      {statusConfig[question.status]?.icon}
                      {statusConfig[question.status]?.label}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-sm">{question.createdByName}</TableCell>
                  <TableCell className="text-right">
                    <div className="flex justify-end gap-2">
                      <Link href={`/admin/question-bank/${question.id}`}>
                        <Button variant="outline" size="sm">View</Button>
                      </Link>
                      <Link href={`/admin/question-bank/${question.id}/edit`}>
                        <Button variant="outline" size="sm">Edit</Button>
                      </Link>
                      {isAdmin && question.status === 'PENDING_REVIEW' && (
                        <>
                          <Button
                            variant="outline"
                            size="sm"
                            className="text-green-600"
                            onClick={() => handleApprove(question.id)}
                            disabled={approveQuestion.isPending}
                          >
                            Approve
                          </Button>
                          <Button
                            variant="outline"
                            size="sm"
                            className="text-red-600"
                            onClick={() => {
                              setSelectedQuestion(question);
                              setRejectDialogOpen(true);
                            }}
                          >
                            Reject
                          </Button>
                        </>
                      )}
                      {question.status === 'DRAFT' && (
                        <Button
                          variant="outline"
                          size="sm"
                          className="text-red-600"
                          onClick={() => handleDelete(question.id)}
                          disabled={deleteQuestion.isPending}
                        >
                          Delete
                        </Button>
                      )}
                    </div>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {/* Pagination */}
      {data && data.totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-4">
          <Button
            variant="outline"
            size="sm"
            onClick={() => setPage(Math.max(0, page - 1))}
            disabled={page === 0}
          >
            Previous
          </Button>
          <span className="flex items-center px-4 text-sm">
            Page {page + 1} of {data.totalPages}
          </span>
          <Button
            variant="outline"
            size="sm"
            onClick={() => setPage(Math.min(data.totalPages - 1, page + 1))}
            disabled={page >= data.totalPages - 1}
          >
            Next
          </Button>
        </div>
      )}

      {/* Reject Dialog */}
      <Dialog open={rejectDialogOpen} onOpenChange={setRejectDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Reject Question</DialogTitle>
            <DialogDescription>
              Please provide a reason for rejecting this question.
            </DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <Input
              placeholder="Rejection reason..."
              value={rejectReason}
              onChange={(e) => setRejectReason(e.target.value)}
            />
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setRejectDialogOpen(false)}>
              Cancel
            </Button>
            <Button
              variant="destructive"
              onClick={handleReject}
              disabled={!rejectReason || rejectQuestion.isPending}
            >
              Reject
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}

export default function QuestionBankPage() {
  const { hasRole } = useAuthStore();
  const isAdmin = hasRole('ADMIN') || hasRole('SUPERADMIN');

  return (
    <div className="container mx-auto py-6 px-4">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold">Question Bank</h1>
          <p className="text-gray-600">Manage and organize questions for assessments</p>
        </div>
        <Link href="/admin/question-bank/create">
          <Button>
            <Plus className="mr-2 h-4 w-4" />
            Create Question
          </Button>
        </Link>
      </div>

      <StatsCards />

      <Tabs defaultValue="all" className="w-full">
        <TabsList>
          <TabsTrigger value="all">All Questions</TabsTrigger>
          <TabsTrigger value="draft">Draft</TabsTrigger>
          <TabsTrigger value="pending">Pending Review</TabsTrigger>
          <TabsTrigger value="approved">Approved</TabsTrigger>
          <TabsTrigger value="rejected">Rejected</TabsTrigger>
        </TabsList>

        <TabsContent value="all" className="mt-4">
          <QuestionTable />
        </TabsContent>
        <TabsContent value="draft" className="mt-4">
          <QuestionTable status="DRAFT" />
        </TabsContent>
        <TabsContent value="pending" className="mt-4">
          <QuestionTable status="PENDING_REVIEW" />
        </TabsContent>
        <TabsContent value="approved" className="mt-4">
          <QuestionTable status="APPROVED" />
        </TabsContent>
        <TabsContent value="rejected" className="mt-4">
          <QuestionTable status="REJECTED" />
        </TabsContent>
      </Tabs>
    </div>
  );
}
