'use client';

import { useState, useMemo } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { MathEditor } from '@/components/math';
import { ScrollArea } from '@/components/ui/scroll-area';
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
import { useQuizCollectionList, useCreateQuiz, useUpdateQuiz, useQuizById, useDeleteQuiz, useUpdateQuizStatus } from '@/hooks/use-quiz';
import { CATEGORIES } from '@/lib/constants/api-urls';
import { useUIStore } from '@/stores/ui-store';
import { useAuthStore } from '@/stores/auth-store';
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
  Trash2,
  Check,
  Play,
  MoreVertical,
} from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';

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

// Interface for questions being created in the form
interface QuizFormQuestion {
  id: string;
  question: string;
  options: string[];
  correctAnswer: number; // 0-3 for A-D
  solution: string;
}

interface CreateQuizFormProps {
  onSuccess: () => void;
  onCancel: () => void;
}

function CreateQuizForm({ onSuccess, onCancel }: CreateQuizFormProps) {
  const createQuiz = useCreateQuiz();
  const [step, setStep] = useState<'details' | 'questions'>('details');
  const [formData, setFormData] = useState({
    paperName: '',
    paperType: 'SSC' as PaperType,
    paperCategory: 'SSC_CGL' as PaperCategory,
    totalTime: 600, // 10 minutes in seconds
    negativeMarks: 0,
    perQuestionScore: 1,
    paperStateStatus: 'DRAFT' as QuizStateStatus,
    priority: 1,
  });

  // Initialize with one empty question
  const [questions, setQuestions] = useState<QuizFormQuestion[]>([
    { id: '1', question: '', options: ['', '', '', ''], correctAnswer: 0, solution: '' },
  ]);

  const addQuestion = () => {
    setQuestions([
      ...questions,
      {
        id: Date.now().toString(),
        question: '',
        options: ['', '', '', ''],
        correctAnswer: 0,
        solution: '',
      },
    ]);
  };

  const removeQuestion = (id: string) => {
    if (questions.length > 1) {
      setQuestions(questions.filter((q) => q.id !== id));
    }
  };

  const updateQuestion = (id: string, field: keyof QuizFormQuestion, value: string | number | string[]) => {
    setQuestions(
      questions.map((q) => (q.id === id ? { ...q, [field]: value } : q))
    );
  };

  const updateOption = (questionId: string, optionIndex: number, value: string) => {
    setQuestions(
      questions.map((q) => {
        if (q.id === questionId) {
          const newOptions = [...q.options];
          newOptions[optionIndex] = value;
          return { ...q, options: newOptions };
        }
        return q;
      })
    );
  };

  const handleDetailsNext = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.paperName.trim()) return;
    setStep('questions');
  };

  const handleSubmit = async () => {
    // Filter valid questions (have question text and all options filled)
    const validQuestions = questions.filter(
      (q) => q.question.trim() && q.options.every((opt) => opt.trim())
    );

    if (validQuestions.length === 0) {
      return;
    }

    // Convert form questions to API format
    // Note: Backend expects co/so as integers (0=A, 1=B, 2=C, 3=D)
    const quizQuestions: QuizQuestion[] = validQuestions.map((q, idx) => ({
      _id: `q${idx + 1}`,
      problem: {
        value: q.question,
        options: q.options.map((opt, i) => ({
          prompt: String.fromCharCode(65 + i), // A, B, C, D
          value: opt,
        })),
        co: q.correctAnswer, // Integer: 0=A, 1=B, 2=C, 3=D
        solutions: q.solution ? [{ value: q.solution }] : [],
      },
    }));

    const pattern = {
      sections: [
        {
          title: 'General',
          subSections: [
            {
              title: 'Questions',
              questionData: {
                questions: quizQuestions,
              },
            },
          ],
        },
      ],
    };

    const payload: QuizCreatePayload = {
      ...formData,
      totalQuestionCount: validQuestions.length,
      totalScore: validQuestions.length * formData.perQuestionScore,
      pattern,
    };

    try {
      await createQuiz.mutateAsync(payload);
      onSuccess();
    } catch (error) {
      console.error('Failed to create quiz:', error);
    }
  };

  // Step 1: Quiz Details
  if (step === 'details') {
    return (
      <form onSubmit={handleDetailsNext} className="space-y-4">
        <div className="grid gap-4 md:grid-cols-2">
          <div className="space-y-2">
            <Label htmlFor="paperName">Quiz Name *</Label>
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

        <div className="grid gap-4 md:grid-cols-3">
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
          <Button type="submit">
            Next: Add Questions
          </Button>
        </div>
      </form>
    );
  }

  // Step 2: Add Questions
  return (
    <div className="space-y-4">
      {/* Quiz summary */}
      <div className="p-3 bg-slate-50 rounded-lg flex flex-wrap gap-4 text-sm">
        <span><strong>Quiz:</strong> {formData.paperName}</span>
        <span><strong>Time:</strong> {Math.floor(formData.totalTime / 60)} min</span>
        <span><strong>Questions:</strong> {questions.length}</span>
      </div>

      {/* Questions List */}
      <ScrollArea className="h-[400px] pr-4">
        <div className="space-y-4">
          {questions.map((question, qIndex) => (
            <Card key={question.id} className="border-slate-200">
              <CardHeader className="pb-2 pt-3 px-4">
                <div className="flex items-center justify-between">
                  <CardTitle className="text-sm font-medium">Question {qIndex + 1}</CardTitle>
                  {questions.length > 1 && (
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => removeQuestion(question.id)}
                      className="h-8 w-8 p-0"
                    >
                      <Trash2 className="h-4 w-4 text-red-600" />
                    </Button>
                  )}
                </div>
              </CardHeader>
              <CardContent className="px-4 pb-4 space-y-3">
                {/* Question Text */}
                <div className="space-y-1">
                  <Label className="text-xs">Question Text</Label>
                  <MathEditor
                    value={question.question}
                    onChange={(value) => updateQuestion(question.id, 'question', value)}
                    placeholder="Enter question with math symbols..."
                    rows={2}
                  />
                </div>

                {/* Options Grid */}
                <div className="grid grid-cols-2 gap-3">
                  {question.options.map((option, optIndex) => (
                    <div key={optIndex} className="space-y-1">
                      <div className="flex items-center gap-2">
                        <input
                          type="radio"
                          name={`correct-${question.id}`}
                          checked={question.correctAnswer === optIndex}
                          onChange={() => updateQuestion(question.id, 'correctAnswer', optIndex)}
                          className="w-3 h-3"
                        />
                        <Label className="text-xs">
                          Option {String.fromCharCode(65 + optIndex)}
                          {question.correctAnswer === optIndex && (
                            <span className="text-green-600 ml-1">(Correct)</span>
                          )}
                        </Label>
                      </div>
                      <Input
                        value={option}
                        onChange={(e) => updateOption(question.id, optIndex, e.target.value)}
                        placeholder={`Option ${String.fromCharCode(65 + optIndex)}`}
                        className="h-8 text-sm"
                      />
                    </div>
                  ))}
                </div>

                {/* Solution (optional) */}
                <div className="space-y-1">
                  <Label className="text-xs text-slate-500">Solution (optional)</Label>
                  <Input
                    value={question.solution}
                    onChange={(e) => updateQuestion(question.id, 'solution', e.target.value)}
                    placeholder="Explain the answer..."
                    className="h-8 text-sm"
                  />
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </ScrollArea>

      {/* Add Question Button */}
      <Button onClick={addQuestion} variant="outline" className="w-full gap-2">
        <Plus className="h-4 w-4" />
        Add Question ({questions.length})
      </Button>

      {/* Actions */}
      <div className="flex justify-between pt-4 border-t">
        <Button type="button" variant="outline" onClick={() => setStep('details')}>
          Back to Details
        </Button>
        <div className="flex gap-3">
          <Button type="button" variant="outline" onClick={onCancel}>
            Cancel
          </Button>
          <Button
            onClick={handleSubmit}
            disabled={createQuiz.isPending || questions.filter(q => q.question.trim()).length === 0}
          >
            {createQuiz.isPending ? (
              <>
                <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                Creating...
              </>
            ) : (
              <>
                <Plus className="h-4 w-4 mr-2" />
                Create Quiz ({questions.filter(q => q.question.trim()).length} questions)
              </>
            )}
          </Button>
        </div>
      </div>
    </div>
  );
}

// Edit Quiz Form Component
interface EditQuizFormProps {
  quiz: Quiz;
  onSuccess: () => void;
  onCancel: () => void;
}

function EditQuizForm({ quiz, onSuccess, onCancel }: EditQuizFormProps) {
  const updateQuiz = useUpdateQuiz();
  const showAlert = useUIStore((state) => state.showAlert);
  const [step, setStep] = useState<'details' | 'questions'>('details');
  const [formData, setFormData] = useState({
    paperName: quiz.paperName || '',
    paperType: (quiz.paperType || 'SSC') as PaperType,
    paperCategory: (quiz.paperCategory || 'SSC_CGL') as PaperCategory,
    totalTime: quiz.totalTime || 600,
    negativeMarks: quiz.negativeMarks || 0,
    perQuestionScore: quiz.perQuestionScore || 1,
    paperStateStatus: (quiz.paperStateStatus || 'DRAFT') as QuizStateStatus,
    priority: quiz.priority || 1,
  });

  // Convert existing quiz questions to form format
  const convertQuizQuestionsToForm = (): QuizFormQuestion[] => {
    const formQuestions: QuizFormQuestion[] = [];
    if (quiz.pattern?.sections) {
      quiz.pattern.sections.forEach((section) => {
        section.subSections?.forEach((subSection) => {
          // Support both old format (questions directly) and new format (questionData.questions)
          const questions = subSection.questionData?.questions || subSection.questions || [];
          questions.forEach((q: any, idx: number) => {
            // co is stored as integer (0=A, 1=B, 2=C, 3=D)
            // Handle both integer and legacy string format
            let correctAnswerIndex = 0;
            const co = q.problem?.co;
            if (typeof co === 'number') {
              correctAnswerIndex = co;
            } else if (typeof co === 'string') {
              // Legacy: convert letter to index
              correctAnswerIndex = co.charCodeAt(0) - 65;
            }
            formQuestions.push({
              id: q._id || `q-${idx}`,
              question: q.problem?.value || '',
              options: q.problem?.options?.map((opt: any) => opt.value) || ['', '', '', ''],
              correctAnswer: Math.max(0, Math.min(3, correctAnswerIndex)),
              solution: q.problem?.solutions?.[0]?.value || '',
            });
          });
        });
      });
    }
    return formQuestions.length > 0 ? formQuestions : [
      { id: '1', question: '', options: ['', '', '', ''], correctAnswer: 0, solution: '' },
    ];
  };

  const [questions, setQuestions] = useState<QuizFormQuestion[]>(convertQuizQuestionsToForm);

  const addQuestion = () => {
    setQuestions([
      ...questions,
      {
        id: Date.now().toString(),
        question: '',
        options: ['', '', '', ''],
        correctAnswer: 0,
        solution: '',
      },
    ]);
  };

  const removeQuestion = (id: string) => {
    if (questions.length > 1) {
      setQuestions(questions.filter((q) => q.id !== id));
    }
  };

  const updateQuestion = (id: string, field: keyof QuizFormQuestion, value: string | number | string[]) => {
    setQuestions(
      questions.map((q) => (q.id === id ? { ...q, [field]: value } : q))
    );
  };

  const updateOption = (questionId: string, optionIndex: number, value: string) => {
    setQuestions(
      questions.map((q) => {
        if (q.id === questionId) {
          const newOptions = [...q.options];
          newOptions[optionIndex] = value;
          return { ...q, options: newOptions };
        }
        return q;
      })
    );
  };

  const handleDetailsNext = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.paperName.trim()) return;
    setStep('questions');
  };

  const handleSubmit = async () => {
    const validQuestions = questions.filter(
      (q) => q.question.trim() && q.options.every((opt) => opt.trim())
    );

    if (validQuestions.length === 0) {
      showAlert('error', 'Please add at least one complete question');
      return;
    }

    // Note: Backend expects co/so as integers (0=A, 1=B, 2=C, 3=D)
    const quizQuestions: QuizQuestion[] = validQuestions.map((q, idx) => ({
      _id: q.id.startsWith('q-') ? q.id : `q${idx + 1}`,
      problem: {
        value: q.question,
        options: q.options.map((opt, i) => ({
          prompt: String.fromCharCode(65 + i),
          value: opt,
        })),
        co: q.correctAnswer, // Integer: 0=A, 1=B, 2=C, 3=D
        solutions: q.solution ? [{ value: q.solution }] : [],
      },
    }));

    const pattern = {
      sections: [
        {
          title: 'General',
          subSections: [
            {
              title: 'Questions',
              questionData: {
                questions: quizQuestions,
              },
            },
          ],
        },
      ],
    };

    const payload = {
      id: quiz.id,
      ...formData,
      totalQuestionCount: validQuestions.length,
      totalScore: validQuestions.length * formData.perQuestionScore,
      pattern,
    };

    try {
      await updateQuiz.mutateAsync(payload);
      showAlert('success', 'Quiz updated successfully');
      onSuccess();
    } catch (error) {
      console.error('Failed to update quiz:', error);
    }
  };

  // Step 1: Quiz Details
  if (step === 'details') {
    return (
      <form onSubmit={handleDetailsNext} className="space-y-4">
        <div className="grid gap-4 md:grid-cols-2">
          <div className="space-y-2">
            <Label htmlFor="editPaperName">Quiz Name *</Label>
            <Input
              id="editPaperName"
              value={formData.paperName}
              onChange={(e) => setFormData({ ...formData, paperName: e.target.value })}
              placeholder="SSC CGL Daily Quiz - Day 1"
              required
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="editPaperType">Paper Type</Label>
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
            <Label htmlFor="editPaperCategory">Category</Label>
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
            <Label htmlFor="editStatus">Status</Label>
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

        <div className="grid gap-4 md:grid-cols-3">
          <div className="space-y-2">
            <Label htmlFor="editTotalTime">Time (minutes)</Label>
            <Input
              id="editTotalTime"
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
            <Label htmlFor="editPerQuestionScore">Marks/Question</Label>
            <Input
              id="editPerQuestionScore"
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
            <Label htmlFor="editNegativeMarks">Negative Marks</Label>
            <Input
              id="editNegativeMarks"
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
          <Button type="submit">
            Next: Edit Questions
          </Button>
        </div>
      </form>
    );
  }

  // Step 2: Edit Questions
  return (
    <div className="space-y-4">
      <div className="p-3 bg-slate-50 rounded-lg flex flex-wrap gap-4 text-sm">
        <span><strong>Quiz:</strong> {formData.paperName}</span>
        <span><strong>Time:</strong> {Math.floor(formData.totalTime / 60)} min</span>
        <span><strong>Questions:</strong> {questions.length}</span>
      </div>

      <ScrollArea className="h-[400px] pr-4">
        <div className="space-y-4">
          {questions.map((question, qIndex) => (
            <Card key={question.id} className="border-slate-200">
              <CardHeader className="pb-2 pt-3 px-4">
                <div className="flex items-center justify-between">
                  <CardTitle className="text-sm font-medium">Question {qIndex + 1}</CardTitle>
                  {questions.length > 1 && (
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => removeQuestion(question.id)}
                      className="h-8 w-8 p-0"
                    >
                      <Trash2 className="h-4 w-4 text-red-600" />
                    </Button>
                  )}
                </div>
              </CardHeader>
              <CardContent className="px-4 pb-4 space-y-3">
                <div className="space-y-1">
                  <Label className="text-xs">Question Text</Label>
                  <MathEditor
                    value={question.question}
                    onChange={(value) => updateQuestion(question.id, 'question', value)}
                    placeholder="Enter question with math symbols..."
                    rows={2}
                  />
                </div>

                <div className="grid grid-cols-2 gap-3">
                  {question.options.map((option, optIndex) => (
                    <div key={optIndex} className="space-y-1">
                      <div className="flex items-center gap-2">
                        <input
                          type="radio"
                          name={`edit-correct-${question.id}`}
                          checked={question.correctAnswer === optIndex}
                          onChange={() => updateQuestion(question.id, 'correctAnswer', optIndex)}
                          className="w-3 h-3"
                        />
                        <Label className="text-xs">
                          Option {String.fromCharCode(65 + optIndex)}
                          {question.correctAnswer === optIndex && (
                            <span className="text-green-600 ml-1">(Correct)</span>
                          )}
                        </Label>
                      </div>
                      <Input
                        value={option}
                        onChange={(e) => updateOption(question.id, optIndex, e.target.value)}
                        placeholder={`Option ${String.fromCharCode(65 + optIndex)}`}
                        className="h-8 text-sm"
                      />
                    </div>
                  ))}
                </div>

                <div className="space-y-1">
                  <Label className="text-xs text-slate-500">Solution (optional)</Label>
                  <Input
                    value={question.solution}
                    onChange={(e) => updateQuestion(question.id, 'solution', e.target.value)}
                    placeholder="Explain the answer..."
                    className="h-8 text-sm"
                  />
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </ScrollArea>

      <Button onClick={addQuestion} variant="outline" className="w-full gap-2">
        <Plus className="h-4 w-4" />
        Add Question ({questions.length})
      </Button>

      <div className="flex justify-between pt-4 border-t">
        <Button type="button" variant="outline" onClick={() => setStep('details')}>
          Back to Details
        </Button>
        <div className="flex gap-3">
          <Button type="button" variant="outline" onClick={onCancel}>
            Cancel
          </Button>
          <Button
            onClick={handleSubmit}
            disabled={updateQuiz.isPending || questions.filter(q => q.question.trim()).length === 0}
          >
            {updateQuiz.isPending ? (
              <>
                <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                Updating...
              </>
            ) : (
              <>
                <Edit className="h-4 w-4 mr-2" />
                Update Quiz ({questions.filter(q => q.question.trim()).length} questions)
              </>
            )}
          </Button>
        </div>
      </div>
    </div>
  );
}

export default function AdminQuizPage() {
  const [page, setPage] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [showCreateDialog, setShowCreateDialog] = useState(false);
  const [editingQuiz, setEditingQuiz] = useState<Quiz | null>(null);
  const [viewingQuiz, setViewingQuiz] = useState<Quiz | null>(null);
  // Activation dialog state
  const [activatingQuiz, setActivatingQuiz] = useState<Quiz | null>(null);
  const [validityStartDate, setValidityStartDate] = useState('');
  const [validityEndDate, setValidityEndDate] = useState('');

  const showAlert = useUIStore((state) => state.showAlert);
  const isSuperAdmin = useAuthStore((state) => state.isSuperAdmin());
  const deleteQuiz = useDeleteQuiz();
  const updateQuizStatus = useUpdateQuizStatus();

  const { data: quizData, isLoading, refetch } = useQuizCollectionList(page, 20);

  // Open activation dialog
  const openActivationDialog = (quiz: Quiz) => {
    // Check permissions
    if (isApprovedOrActive(quiz) && !isSuperAdmin) {
      showAlert('error', 'Only SUPERADMIN can change status of APPROVED/ACTIVE quizzes');
      return;
    }
    // Set default dates
    const today = new Date();
    const nextMonth = new Date();
    nextMonth.setMonth(nextMonth.getMonth() + 1);
    setValidityStartDate(today.toISOString().split('T')[0]);
    setValidityEndDate(nextMonth.toISOString().split('T')[0]);
    setActivatingQuiz(quiz);
  };

  // Handle activation with validity dates
  const handleActivate = () => {
    if (!activatingQuiz) return;

    if (!validityEndDate) {
      showAlert('error', 'Please select validity end date');
      return;
    }

    const startTimestamp = validityStartDate
      ? new Date(validityStartDate).getTime()
      : Date.now();
    const endTimestamp = new Date(validityEndDate).getTime();

    if (endTimestamp <= startTimestamp) {
      showAlert('error', 'End date must be after start date');
      return;
    }

    updateQuizStatus.mutate(
      {
        quizId: activatingQuiz.id,
        status: 'ACTIVE',
        validityStartDate: startTimestamp,
        validityEndDate: endTimestamp,
      },
      {
        onSuccess: () => {
          showAlert('success', 'Quiz activated successfully');
          setActivatingQuiz(null);
          refetch();
        },
        onError: (error: any) => {
          showAlert('error', error?.response?.data || 'Failed to activate quiz');
        },
      }
    );
  };

  // Handle status change (for non-ACTIVE statuses)
  const handleStatusChange = (quiz: Quiz, newStatus: 'DRAFT' | 'APPROVED' | 'ACTIVE') => {
    // For ACTIVE status, show the activation dialog
    if (newStatus === 'ACTIVE') {
      openActivationDialog(quiz);
      return;
    }

    // Check permissions for changing from APPROVED/ACTIVE status
    if (isApprovedOrActive(quiz) && !isSuperAdmin) {
      showAlert('error', 'Only SUPERADMIN can change status of APPROVED/ACTIVE quizzes');
      return;
    }

    const statusLabels: Record<string, string> = {
      DRAFT: 'Draft',
      APPROVED: 'Approved',
      ACTIVE: 'Active',
    };

    if (confirm(`Change quiz status to ${statusLabels[newStatus]}?`)) {
      updateQuizStatus.mutate(
        { quizId: quiz.id, status: newStatus },
        {
          onSuccess: () => {
            showAlert('success', `Quiz status changed to ${statusLabels[newStatus]}`);
            refetch();
          },
          onError: (error: any) => {
            showAlert('error', error?.response?.data || 'Failed to update status');
          },
        }
      );
    }
  };

  // Check if quiz is APPROVED or ACTIVE
  const isApprovedOrActive = (quiz: Quiz) => {
    return quiz.paperStateStatus === 'APPROVED' || quiz.paperStateStatus === 'ACTIVE';
  };

  // Check if user can edit a quiz based on its status
  const canEditQuiz = (quiz: Quiz) => {
    // DRAFT quizzes can be edited by any admin
    if (quiz.paperStateStatus === 'DRAFT') return true;
    // APPROVED and ACTIVE quizzes can only be edited by SUPERADMIN
    return isSuperAdmin;
  };

  // Check if user can delete a quiz based on its status
  const canDeleteQuiz = (quiz: Quiz) => {
    // Only SUPERADMIN can delete APPROVED/ACTIVE quizzes
    if (quiz.paperStateStatus === 'APPROVED' || quiz.paperStateStatus === 'ACTIVE') {
      return isSuperAdmin;
    }
    return true;
  };

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
          <DialogContent className="max-w-3xl max-h-[90vh] overflow-hidden">
            <DialogHeader>
              <DialogTitle>Create New Quiz</DialogTitle>
              <DialogDescription>
                Create a new quiz with multiple choice questions
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
                      <div className="flex justify-end gap-1">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => setViewingQuiz(quiz)}
                          title="View Quiz"
                        >
                          <Eye className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => {
                            if (!canEditQuiz(quiz)) {
                              showAlert('error', 'Only SUPERADMIN can edit APPROVED/ACTIVE quizzes');
                              return;
                            }
                            setEditingQuiz(quiz);
                          }}
                          title={canEditQuiz(quiz) ? "Edit Quiz" : "Only SUPERADMIN can edit"}
                          className={!canEditQuiz(quiz) ? 'opacity-50' : ''}
                        >
                          <Edit className="h-4 w-4" />
                        </Button>

                        {/* Status & More Actions Dropdown */}
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button variant="ghost" size="sm">
                              <MoreVertical className="h-4 w-4" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end">
                            {/* Status Change Actions */}
                            {quiz.paperStateStatus !== 'DRAFT' && (
                              <DropdownMenuItem
                                onClick={() => handleStatusChange(quiz, 'DRAFT')}
                                disabled={isApprovedOrActive(quiz) && !isSuperAdmin}
                              >
                                <AlertCircle className="h-4 w-4 mr-2 text-slate-500" />
                                Set as Draft
                                {isApprovedOrActive(quiz) && !isSuperAdmin && (
                                  <span className="ml-2 text-xs text-slate-400">(SUPERADMIN)</span>
                                )}
                              </DropdownMenuItem>
                            )}
                            {quiz.paperStateStatus !== 'APPROVED' && (
                              <DropdownMenuItem
                                onClick={() => handleStatusChange(quiz, 'APPROVED')}
                                disabled={isApprovedOrActive(quiz) && !isSuperAdmin}
                              >
                                <Check className="h-4 w-4 mr-2 text-blue-500" />
                                Approve
                                {isApprovedOrActive(quiz) && !isSuperAdmin && (
                                  <span className="ml-2 text-xs text-slate-400">(SUPERADMIN)</span>
                                )}
                              </DropdownMenuItem>
                            )}
                            {quiz.paperStateStatus !== 'ACTIVE' && (
                              <DropdownMenuItem
                                onClick={() => handleStatusChange(quiz, 'ACTIVE')}
                                disabled={isApprovedOrActive(quiz) && !isSuperAdmin}
                              >
                                <Play className="h-4 w-4 mr-2 text-green-500" />
                                Activate
                                {isApprovedOrActive(quiz) && !isSuperAdmin && (
                                  <span className="ml-2 text-xs text-slate-400">(SUPERADMIN)</span>
                                )}
                              </DropdownMenuItem>
                            )}

                            <DropdownMenuSeparator />

                            {/* Delete Action */}
                            <DropdownMenuItem
                              onClick={() => {
                                if (!canDeleteQuiz(quiz)) {
                                  showAlert('error', 'Only SUPERADMIN can delete APPROVED/ACTIVE quizzes');
                                  return;
                                }
                                if (confirm('Are you sure you want to delete this quiz?')) {
                                  deleteQuiz.mutate(quiz.id, {
                                    onSuccess: () => {
                                      showAlert('success', 'Quiz deleted successfully');
                                      refetch();
                                    },
                                  });
                                }
                              }}
                              className="text-red-600"
                              disabled={!canDeleteQuiz(quiz)}
                            >
                              <Trash2 className="h-4 w-4 mr-2" />
                              Delete
                              {!canDeleteQuiz(quiz) && (
                                <span className="ml-2 text-xs text-slate-400">(SUPERADMIN)</span>
                              )}
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
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

      {/* Edit Quiz Dialog */}
      <Dialog open={!!editingQuiz} onOpenChange={(open) => !open && setEditingQuiz(null)}>
        <DialogContent className="max-w-3xl max-h-[90vh] overflow-hidden">
          <DialogHeader>
            <DialogTitle>Edit Quiz</DialogTitle>
            <DialogDescription>
              Update quiz details and questions
            </DialogDescription>
          </DialogHeader>
          {editingQuiz && (
            <EditQuizForm
              quiz={editingQuiz}
              onSuccess={() => {
                setEditingQuiz(null);
                refetch();
              }}
              onCancel={() => setEditingQuiz(null)}
            />
          )}
        </DialogContent>
      </Dialog>

      {/* View Quiz Dialog */}
      <Dialog open={!!viewingQuiz} onOpenChange={(open) => !open && setViewingQuiz(null)}>
        <DialogContent className="max-w-3xl max-h-[90vh] overflow-auto">
          <DialogHeader>
            <DialogTitle>{viewingQuiz?.paperName}</DialogTitle>
            <DialogDescription>
              Quiz details and questions preview
            </DialogDescription>
          </DialogHeader>
          {viewingQuiz && (
            <div className="space-y-4">
              {/* Quiz Info */}
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div className="p-3 bg-slate-50 rounded-lg">
                  <p className="text-xs text-slate-500">Category</p>
                  <p className="font-medium">{viewingQuiz.paperCategory?.replace(/_/g, ' ')}</p>
                </div>
                <div className="p-3 bg-slate-50 rounded-lg">
                  <p className="text-xs text-slate-500">Questions</p>
                  <p className="font-medium">{viewingQuiz.totalQuestionCount}</p>
                </div>
                <div className="p-3 bg-slate-50 rounded-lg">
                  <p className="text-xs text-slate-500">Duration</p>
                  <p className="font-medium">{formatTime(viewingQuiz.totalTime)}</p>
                </div>
                <div className="p-3 bg-slate-50 rounded-lg">
                  <p className="text-xs text-slate-500">Status</p>
                  <p className="font-medium">{viewingQuiz.paperStateStatus}</p>
                </div>
              </div>

              {/* Questions Preview */}
              <ScrollArea className="h-[400px]">
                <div className="space-y-4">
                  {viewingQuiz.pattern?.sections?.map((section, sIdx) =>
                    section.subSections?.map((subSection: any, ssIdx) =>
                      (subSection.questionData?.questions || subSection.questions || []).map((q: any, qIdx: number) => (
                        <Card key={`${sIdx}-${ssIdx}-${qIdx}`} className="border-slate-200">
                          <CardContent className="p-4">
                            <p className="font-medium mb-3">
                              Q{qIdx + 1}. {q.problem?.value}
                            </p>
                            <div className="grid grid-cols-2 gap-2">
                              {q.problem?.options?.map((opt: any, optIdx: number) => (
                                <div
                                  key={optIdx}
                                  className={`p-2 rounded text-sm ${
                                    opt.prompt === q.problem?.co
                                      ? 'bg-green-100 text-green-700 font-medium'
                                      : 'bg-slate-50'
                                  }`}
                                >
                                  {opt.prompt}. {opt.value}
                                </div>
                              ))}
                            </div>
                            {q.problem?.solutions?.[0]?.value && (
                              <p className="mt-2 text-sm text-slate-600">
                                <strong>Solution:</strong> {q.problem.solutions[0].value}
                              </p>
                            )}
                          </CardContent>
                        </Card>
                      ))
                    )
                  )}
                </div>
              </ScrollArea>

              <div className="flex justify-end gap-3 pt-4 border-t">
                <Button variant="outline" onClick={() => setViewingQuiz(null)}>
                  Close
                </Button>
                {canEditQuiz(viewingQuiz) ? (
                  <Button onClick={() => {
                    setViewingQuiz(null);
                    setEditingQuiz(viewingQuiz);
                  }}>
                    <Edit className="h-4 w-4 mr-2" />
                    Edit Quiz
                  </Button>
                ) : (
                  <Button
                    variant="outline"
                    disabled
                    title="Only SUPERADMIN can edit APPROVED/ACTIVE quizzes"
                  >
                    <Edit className="h-4 w-4 mr-2" />
                    Edit (SUPERADMIN only)
                  </Button>
                )}
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* Activation Dialog with Validity Dates */}
      <Dialog open={!!activatingQuiz} onOpenChange={(open) => !open && setActivatingQuiz(null)}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Activate Quiz</DialogTitle>
            <DialogDescription>
              Set the validity period for this quiz. Users can only attempt the quiz within this period.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label>Quiz Name</Label>
              <p className="text-sm font-medium text-slate-700">{activatingQuiz?.paperName}</p>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="validityStartDate">Start Date</Label>
                <Input
                  id="validityStartDate"
                  type="date"
                  value={validityStartDate}
                  onChange={(e) => setValidityStartDate(e.target.value)}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="validityEndDate">End Date *</Label>
                <Input
                  id="validityEndDate"
                  type="date"
                  value={validityEndDate}
                  onChange={(e) => setValidityEndDate(e.target.value)}
                  min={validityStartDate}
                />
              </div>
            </div>
            <p className="text-xs text-slate-500">
              The quiz will be available to users from the start date until the end date.
            </p>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setActivatingQuiz(null)}>
              Cancel
            </Button>
            <Button
              onClick={handleActivate}
              disabled={updateQuizStatus.isPending}
              className="gap-2"
            >
              {updateQuizStatus.isPending ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Play className="h-4 w-4" />
              )}
              Activate Quiz
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
