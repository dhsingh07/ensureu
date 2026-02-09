'use client';

import { use } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { ArrowLeft, Edit, Send, CheckCircle, XCircle, Clock, Trash } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { Separator } from '@/components/ui/separator';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { useState } from 'react';
import {
  useQuestionBankItem,
  useSubmitForReview,
  useApproveQuestion,
  useRejectQuestion,
  useDeleteQuestion,
} from '@/hooks/use-question-bank';
import { useAuthStore } from '@/stores/auth-store';
import { QuestionBankStatus, DifficultyLevel } from '@/types/question-bank';

const statusConfig: Record<QuestionBankStatus, { label: string; color: string; icon: React.ReactNode }> = {
  DRAFT: { label: 'Draft', color: 'bg-gray-100 text-gray-800', icon: <Clock className="w-4 h-4" /> },
  PENDING_REVIEW: { label: 'Pending Review', color: 'bg-yellow-100 text-yellow-800', icon: <Clock className="w-4 h-4" /> },
  APPROVED: { label: 'Approved', color: 'bg-green-100 text-green-800', icon: <CheckCircle className="w-4 h-4" /> },
  REJECTED: { label: 'Rejected', color: 'bg-red-100 text-red-800', icon: <XCircle className="w-4 h-4" /> },
  ARCHIVED: { label: 'Archived', color: 'bg-gray-200 text-gray-600', icon: <Clock className="w-4 h-4" /> },
};

const difficultyConfig: Record<DifficultyLevel, { label: string; color: string }> = {
  EASY: { label: 'Easy', color: 'bg-green-100 text-green-800' },
  MEDIUM: { label: 'Medium', color: 'bg-yellow-100 text-yellow-800' },
  HARD: { label: 'Hard', color: 'bg-red-100 text-red-800' },
};

export default function ViewQuestionPage({ params }: { params: Promise<{ id: string }> }) {
  const resolvedParams = use(params);
  const router = useRouter();
  const { data: question, isLoading } = useQuestionBankItem(resolvedParams.id);
  const { hasRole } = useAuthStore();
  const user = useAuthStore((state) => state.user);

  const [rejectDialogOpen, setRejectDialogOpen] = useState(false);
  const [rejectReason, setRejectReason] = useState('');

  const submitForReview = useSubmitForReview();
  const approveQuestion = useApproveQuestion();
  const rejectQuestion = useRejectQuestion();
  const deleteQuestion = useDeleteQuestion();

  const isAdmin = hasRole('ADMIN') || hasRole('SUPERADMIN');
  const isOwner = question?.createdBy === user?.userName;
  const canEdit = isOwner || isAdmin;
  const canSubmitForReview = isOwner && (question?.status === 'DRAFT' || question?.status === 'REJECTED');
  const canApprove = isAdmin && question?.status === 'PENDING_REVIEW';
  const canDelete = (isOwner && question?.status === 'DRAFT') || isAdmin;

  const handleSubmitForReview = async () => {
    if (!question) return;
    try {
      await submitForReview.mutateAsync(question.id);
    } catch (error) {
      console.error('Failed to submit for review:', error);
    }
  };

  const handleApprove = async () => {
    if (!question) return;
    try {
      await approveQuestion.mutateAsync(question.id);
    } catch (error) {
      console.error('Failed to approve:', error);
    }
  };

  const handleReject = async () => {
    if (!question || !rejectReason) return;
    try {
      await rejectQuestion.mutateAsync({ id: question.id, reason: rejectReason });
      setRejectDialogOpen(false);
      setRejectReason('');
    } catch (error) {
      console.error('Failed to reject:', error);
    }
  };

  const handleDelete = async () => {
    if (!question) return;
    if (!confirm('Are you sure you want to delete this question?')) return;
    try {
      await deleteQuestion.mutateAsync(question.id);
      router.push('/admin/question-bank');
    } catch (error) {
      console.error('Failed to delete:', error);
    }
  };

  const formatDate = (timestamp: number) => {
    return new Date(timestamp).toLocaleString();
  };

  if (isLoading) {
    return (
      <div className="container mx-auto py-6 px-4 max-w-4xl">
        <Skeleton className="h-8 w-64 mb-6" />
        <Skeleton className="h-64 w-full" />
      </div>
    );
  }

  if (!question) {
    return (
      <div className="container mx-auto py-6 px-4">
        <p>Question not found</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-6 px-4 max-w-4xl">
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-4">
          <Link href="/admin/question-bank">
            <Button variant="ghost" size="sm">
              <ArrowLeft className="mr-2 h-4 w-4" />
              Back
            </Button>
          </Link>
          <div>
            <h1 className="text-xl font-bold font-mono">{question.questionId}</h1>
            <p className="text-gray-600 text-sm">Created by {question.createdByName}</p>
          </div>
        </div>
        <div className="flex gap-2">
          {canEdit && (
            <Link href={`/admin/question-bank/${question.id}/edit`}>
              <Button variant="outline" size="sm">
                <Edit className="mr-2 h-4 w-4" />
                Edit
              </Button>
            </Link>
          )}
          {canSubmitForReview && (
            <Button
              size="sm"
              onClick={handleSubmitForReview}
              disabled={submitForReview.isPending}
            >
              <Send className="mr-2 h-4 w-4" />
              Submit for Review
            </Button>
          )}
          {canApprove && (
            <>
              <Button
                size="sm"
                className="bg-green-600 hover:bg-green-700"
                onClick={handleApprove}
                disabled={approveQuestion.isPending}
              >
                <CheckCircle className="mr-2 h-4 w-4" />
                Approve
              </Button>
              <Button
                variant="destructive"
                size="sm"
                onClick={() => setRejectDialogOpen(true)}
              >
                <XCircle className="mr-2 h-4 w-4" />
                Reject
              </Button>
            </>
          )}
          {canDelete && (
            <Button
              variant="outline"
              size="sm"
              className="text-red-600 hover:bg-red-50"
              onClick={handleDelete}
              disabled={deleteQuestion.isPending}
            >
              <Trash className="mr-2 h-4 w-4" />
              Delete
            </Button>
          )}
        </div>
      </div>

      <div className="space-y-6">
        {/* Status & Meta */}
        <Card>
          <CardContent className="pt-6">
            <div className="flex flex-wrap gap-3">
              <Badge className={`${statusConfig[question.status]?.color} flex items-center gap-1`}>
                {statusConfig[question.status]?.icon}
                {statusConfig[question.status]?.label}
              </Badge>
              <Badge className={difficultyConfig[question.difficultyLevel]?.color}>
                {difficultyConfig[question.difficultyLevel]?.label}
              </Badge>
              <Badge variant="outline">{question.paperCategory?.replace(/_/g, ' ')}</Badge>
              <Badge variant="outline">{question.paperSubCategory?.replace(/_/g, ' ')}</Badge>
              <Badge variant="outline">{question.subject}</Badge>
              <Badge variant="outline">{question.topic}</Badge>
            </div>

            {question.status === 'REJECTED' && question.rejectionReason && (
              <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
                <p className="text-sm font-medium text-red-800">Rejection Reason:</p>
                <p className="text-sm text-red-700">{question.rejectionReason}</p>
              </div>
            )}
          </CardContent>
        </Card>

        {/* Question */}
        <Card>
          <CardHeader>
            <CardTitle>Question</CardTitle>
          </CardHeader>
          <CardContent>
            {/* Image above question */}
            {question.imageUrl && question.imagePosition === 'above' && (
              <div className="mb-4">
                <img
                  src={question.imageUrl}
                  alt="Question illustration"
                  className="max-w-full h-auto rounded-lg border"
                />
              </div>
            )}

            <div
              className="text-lg mb-6"
              dangerouslySetInnerHTML={{ __html: question.problem.question }}
            />

            {/* Image below question */}
            {question.imageUrl && question.imagePosition === 'below' && (
              <div className="mb-6">
                <img
                  src={question.imageUrl}
                  alt="Question illustration"
                  className="max-w-full h-auto rounded-lg border"
                />
              </div>
            )}

            {/* Image inline - show alongside question if inline */}
            {question.imageUrl && question.imagePosition === 'inline' && (
              <div className="mb-6 p-4 bg-gray-50 rounded-lg">
                <img
                  src={question.imageUrl}
                  alt="Question illustration"
                  className="max-w-md h-auto rounded-lg border mx-auto"
                />
              </div>
            )}

            {question.problem.questionHindi && (
              <div className="p-3 bg-gray-50 rounded-lg mb-6">
                <p className="text-xs text-gray-500 mb-1">Hindi</p>
                <p dangerouslySetInnerHTML={{ __html: question.problem.questionHindi }} />
              </div>
            )}

            <div className="space-y-3">
              {question.problem.options.map((option) => (
                <div
                  key={option.key}
                  className={`p-4 rounded-lg border ${
                    question.problem.correctOption === option.key
                      ? 'bg-green-50 border-green-500'
                      : 'bg-gray-50 border-gray-200'
                  }`}
                >
                  <div className="flex items-start gap-3">
                    <Badge
                      variant={question.problem.correctOption === option.key ? 'default' : 'outline'}
                      className={question.problem.correctOption === option.key ? 'bg-green-600' : ''}
                    >
                      {option.key}
                    </Badge>
                    <div className="flex-1">
                      <p dangerouslySetInnerHTML={{ __html: option.value }} />
                      {option.valueHindi && (
                        <p className="text-sm text-gray-600 mt-1" dangerouslySetInnerHTML={{ __html: option.valueHindi }} />
                      )}
                    </div>
                    {question.problem.correctOption === option.key && (
                      <CheckCircle className="h-5 w-5 text-green-600" />
                    )}
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Solution */}
        {question.problem.solution && (
          <Card>
            <CardHeader>
              <CardTitle>Solution / Explanation</CardTitle>
            </CardHeader>
            <CardContent>
              <div
                className="prose max-w-none"
                dangerouslySetInnerHTML={{ __html: question.problem.solution }}
              />
              {question.problem.solutionHindi && (
                <div className="mt-4 p-3 bg-gray-50 rounded-lg">
                  <p className="text-xs text-gray-500 mb-1">Hindi</p>
                  <div dangerouslySetInnerHTML={{ __html: question.problem.solutionHindi }} />
                </div>
              )}
            </CardContent>
          </Card>
        )}

        {/* Metadata */}
        <Card>
          <CardHeader>
            <CardTitle>Metadata</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div>
                <p className="text-sm text-gray-500">Marks</p>
                <p className="font-medium">{question.marks}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Negative Marks</p>
                <p className="font-medium">{question.negativeMarks}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Avg. Time</p>
                <p className="font-medium">{question.averageTime} seconds</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Question Type</p>
                <p className="font-medium">{question.questionType}</p>
              </div>
              {question.year && (
                <div>
                  <p className="text-sm text-gray-500">Year</p>
                  <p className="font-medium">{question.year}</p>
                </div>
              )}
              {question.source && (
                <div className="col-span-2">
                  <p className="text-sm text-gray-500">Source</p>
                  <p className="font-medium">{question.source}</p>
                </div>
              )}
              {question.hasImage && (
                <div>
                  <p className="text-sm text-gray-500">Has Image</p>
                  <p className="font-medium">Yes ({question.imagePosition || 'above'})</p>
                </div>
              )}
            </div>

            {question.tags && question.tags.length > 0 && (
              <div className="mt-4">
                <p className="text-sm text-gray-500 mb-2">Tags</p>
                <div className="flex flex-wrap gap-2">
                  {question.tags.map((tag) => (
                    <Badge key={tag} variant="secondary">{tag}</Badge>
                  ))}
                </div>
              </div>
            )}

            <Separator className="my-4" />

            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p className="text-gray-500">Created At</p>
                <p>{formatDate(question.createdAt)}</p>
              </div>
              {question.updatedAt && (
                <div>
                  <p className="text-gray-500">Updated At</p>
                  <p>{formatDate(question.updatedAt)}</p>
                </div>
              )}
              {question.approvedAt && (
                <div>
                  <p className="text-gray-500">Approved At</p>
                  <p>{formatDate(question.approvedAt)}</p>
                </div>
              )}
              {question.approvedBy && (
                <div>
                  <p className="text-gray-500">Approved By</p>
                  <p>{question.approvedBy}</p>
                </div>
              )}
            </div>
          </CardContent>
        </Card>
      </div>

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
