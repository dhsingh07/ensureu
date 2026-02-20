'use client';

import { useState, useMemo } from 'react';
import { useRouter } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Textarea } from '@/components/ui/textarea';
import { MathEditor } from '@/components/math';
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
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useAdminPaperList, useUpdatePaperStatus, useCreatePaper, useUpdatePaper, useDeletePaper, useUploadCsv } from '@/hooks/use-admin';
import { useUIStore } from '@/stores/ui-store';
import { useAuthStore } from '@/stores/auth-store';
import {
  Plus,
  Search,
  Edit,
  Trash2,
  FileText,
  MoreVertical,
  Check,
  Play,
  AlertCircle,
  Loader2,
  ListOrdered,
  Eye,
  CheckCircle2,
  Clock,
  Trophy,
  Upload,
  FileSpreadsheet,
} from 'lucide-react';
import type { PaperCategory, TestType, PaperStateStatus, PaperType } from '@/types/paper';

const categories: { value: PaperCategory; label: string }[] = [
  { value: 'SSC_CGL', label: 'SSC CGL' },
  { value: 'SSC_CHSL', label: 'SSC CHSL' },
  { value: 'SSC_CPO', label: 'SSC CPO' },
  { value: 'BANK_PO', label: 'Bank PO' },
];

const testTypes: { value: TestType | 'ALL'; label: string }[] = [
  { value: 'ALL', label: 'All Papers' },
  { value: 'FREE', label: 'Free' },
  { value: 'PAID', label: 'Paid' },
];

function getStatusBadge(status?: PaperStateStatus) {
  switch (status) {
    case 'ACTIVE':
      return <Badge className="bg-green-100 text-green-700">Active</Badge>;
    case 'APPROVED':
      return <Badge className="bg-blue-100 text-blue-700">Approved</Badge>;
    case 'DRAFT':
      return <Badge className="bg-slate-100 text-slate-700">Draft</Badge>;
    default:
      return <Badge variant="outline">{status || 'Unknown'}</Badge>;
  }
}

function formatDate(timestamp?: number): string {
  if (!timestamp) return '-';
  return new Date(timestamp).toLocaleDateString('en-IN', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  });
}

function formatTime(seconds: number): string {
  const mins = Math.floor(seconds / 60);
  if (mins < 60) return `${mins} min`;
  const hrs = Math.floor(mins / 60);
  const remainMins = mins % 60;
  return `${hrs}h ${remainMins}m`;
}

function isCurrentlyValid(paper: Paper): boolean {
  if (!paper.validityRangeStartDateTime || !paper.validityRangeEndDateTime) return false;
  const now = Date.now();
  return now >= paper.validityRangeStartDateTime && now <= paper.validityRangeEndDateTime;
}

function isExpired(paper: Paper): boolean {
  if (!paper.validityRangeEndDateTime) return false;
  return Date.now() > paper.validityRangeEndDateTime;
}

interface Paper {
  paperId?: string;
  id?: string;
  paperName: string;
  totalQuestions?: number;
  totalTime: number;
  totalScore: number;
  status?: PaperStateStatus;
  validityRangeStartDateTime?: number;
  validityRangeEndDateTime?: number;
  testType?: 'FREE' | 'PAID';
  paperCategory?: string;
  negativeMarks?: number;
  perQuestionScore?: number;
}

// Get paper ID (handles both id and paperId fields)
function getPaperId(paper: Paper): string {
  return paper.paperId || paper.id || '';
}

// Question form interface
interface PaperFormQuestion {
  id: string;
  question: string;
  questionHindi?: string;
  options: string[];
  correctAnswer: number;
  solution: string;
}

// Create Paper Form Component
interface CreatePaperFormProps {
  onSuccess: () => void;
  onCancel: () => void;
  initialTestType?: 'FREE' | 'PAID';
}

function CreatePaperForm({ onSuccess, onCancel, initialTestType = 'FREE' }: CreatePaperFormProps) {
  const createPaper = useCreatePaper();
  const showAlert = useUIStore((state) => state.showAlert);
  const [step, setStep] = useState<'details' | 'questions'>('details');

  const [formData, setFormData] = useState({
    paperName: '',
    paperType: 'SSC' as PaperType,
    paperCategory: 'SSC_CGL' as PaperCategory,
    testType: initialTestType as 'FREE' | 'PAID',
    totalTime: 3600, // 60 minutes
    negativeMarks: 0.5,
    perQuestionScore: 2,
    paperStateStatus: 'DRAFT' as PaperStateStatus,
  });

  const [questions, setQuestions] = useState<PaperFormQuestion[]>([
    { id: '1', question: '', questionHindi: '', options: ['', '', '', ''], correctAnswer: 0, solution: '' },
  ]);

  const addQuestion = () => {
    setQuestions([
      ...questions,
      {
        id: Date.now().toString(),
        question: '',
        questionHindi: '',
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

  const updateQuestion = (id: string, field: keyof PaperFormQuestion, value: string | number | string[]) => {
    setQuestions(questions.map((q) => (q.id === id ? { ...q, [field]: value } : q)));
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
    if (!formData.paperName.trim()) {
      showAlert('error', 'Paper name is required');
      return;
    }
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

    // Build paper structure
    const paperQuestions = validQuestions.map((q, idx) => ({
      id: `q-${idx + 1}`,
      qNo: idx + 1,
      problem: {
        question: q.question,
        options: q.options.map((opt, i) => ({
          prompt: String.fromCharCode(65 + i),
          text: opt,
          selected: false,
        })),
        so: [],
        co: [String.fromCharCode(65 + q.correctAnswer)],
        solution: q.solution,
      },
      problemHindi: q.questionHindi ? {
        question: q.questionHindi,
        options: q.options.map((opt, i) => ({
          prompt: String.fromCharCode(65 + i),
          text: opt,
          selected: false,
        })),
        so: [],
        co: [String.fromCharCode(65 + q.correctAnswer)],
      } : undefined,
      type: 'MCQ',
      questionType: 'RADIOBUTTON',
      complexityLevel: 'MEDIUM',
      complexityScore: 2,
      timeTakenInSecond: 0,
    }));

    const payload = {
      ...formData,
      paperSubCategory: `${formData.paperCategory}_TIER1`,
      totalQuestionCount: validQuestions.length,
      totalScore: validQuestions.length * formData.perQuestionScore,
      pattern: {
        sections: [
          {
            id: 'section-1',
            title: 'General',
            sectionType: 'GeneralIntelligence',
            subSections: [],
            questionData: {
              questions: paperQuestions,
            },
            timeTakenSecond: 0,
          },
        ],
      },
    };

    try {
      await createPaper.mutateAsync(payload as any);
      showAlert('success', 'Paper created successfully');
      onSuccess();
    } catch (error) {
      console.error('Failed to create paper:', error);
    }
  };

  // Step 1: Paper Details
  if (step === 'details') {
    return (
      <form onSubmit={handleDetailsNext} className="space-y-4">
        <div className="grid gap-4 md:grid-cols-2">
          <div className="space-y-2">
            <Label htmlFor="paperName">Paper Name *</Label>
            <Input
              id="paperName"
              value={formData.paperName}
              onChange={(e) => setFormData({ ...formData, paperName: e.target.value })}
              placeholder="SSC CGL 2024 Practice Set 1"
              required
            />
          </div>
          <div className="space-y-2">
            <Label>Test Type</Label>
            <Select
              value={formData.testType}
              onValueChange={(v) => setFormData({ ...formData, testType: v as 'FREE' | 'PAID' })}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="FREE">Free</SelectItem>
                <SelectItem value="PAID">Paid</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        <div className="grid gap-4 md:grid-cols-2">
          <div className="space-y-2">
            <Label>Paper Type</Label>
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
          <div className="space-y-2">
            <Label>Category</Label>
            <Select
              value={formData.paperCategory}
              onValueChange={(v) => setFormData({ ...formData, paperCategory: v as PaperCategory })}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {categories.map((cat) => (
                  <SelectItem key={cat.value} value={cat.value}>
                    {cat.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div>

        <div className="grid gap-4 md:grid-cols-3">
          <div className="space-y-2">
            <Label>Duration (minutes)</Label>
            <Input
              type="number"
              min={1}
              value={Math.floor(formData.totalTime / 60)}
              onChange={(e) => setFormData({ ...formData, totalTime: (parseInt(e.target.value) || 60) * 60 })}
            />
          </div>
          <div className="space-y-2">
            <Label>Marks per Question</Label>
            <Input
              type="number"
              min={0.5}
              step={0.5}
              value={formData.perQuestionScore}
              onChange={(e) => setFormData({ ...formData, perQuestionScore: parseFloat(e.target.value) || 2 })}
            />
          </div>
          <div className="space-y-2">
            <Label>Negative Marks</Label>
            <Input
              type="number"
              min={0}
              step={0.25}
              value={formData.negativeMarks}
              onChange={(e) => setFormData({ ...formData, negativeMarks: parseFloat(e.target.value) || 0 })}
            />
          </div>
        </div>

        <div className="flex justify-end gap-3 pt-4">
          <Button type="button" variant="outline" onClick={onCancel}>Cancel</Button>
          <Button type="submit">Next: Add Questions</Button>
        </div>
      </form>
    );
  }

  // Step 2: Add Questions
  return (
    <div className="space-y-4">
      <div className="p-3 bg-slate-50 rounded-lg flex flex-wrap gap-4 text-sm">
        <span><strong>Paper:</strong> {formData.paperName}</span>
        <span><strong>Type:</strong> {formData.testType}</span>
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
                    <Button variant="ghost" size="sm" onClick={() => removeQuestion(question.id)} className="h-8 w-8 p-0">
                      <Trash2 className="h-4 w-4 text-red-600" />
                    </Button>
                  )}
                </div>
              </CardHeader>
              <CardContent className="px-4 pb-4 space-y-3">
                <div className="space-y-1">
                  <Label className="text-xs">Question Text (English)</Label>
                  <MathEditor
                    value={question.question}
                    onChange={(value) => updateQuestion(question.id, 'question', value)}
                    placeholder="Enter question..."
                    rows={2}
                  />
                </div>
                <div className="space-y-1">
                  <Label className="text-xs text-slate-500">Question Text (Hindi - optional)</Label>
                  <Input
                    value={question.questionHindi || ''}
                    onChange={(e) => updateQuestion(question.id, 'questionHindi', e.target.value)}
                    placeholder="Hindi translation..."
                    className="h-8 text-sm"
                  />
                </div>
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
                          {question.correctAnswer === optIndex && <span className="text-green-600 ml-1">(Correct)</span>}
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
        <Button type="button" variant="outline" onClick={() => setStep('details')}>Back to Details</Button>
        <div className="flex gap-3">
          <Button type="button" variant="outline" onClick={onCancel}>Cancel</Button>
          <Button
            onClick={handleSubmit}
            disabled={createPaper.isPending || questions.filter(q => q.question.trim()).length === 0}
          >
            {createPaper.isPending ? (
              <><Loader2 className="h-4 w-4 mr-2 animate-spin" />Creating...</>
            ) : (
              <><Plus className="h-4 w-4 mr-2" />Create Paper ({questions.filter(q => q.question.trim()).length} questions)</>
            )}
          </Button>
        </div>
      </div>
    </div>
  );
}

export default function PaperManagementPage() {
  const router = useRouter();
  const [category, setCategory] = useState<PaperCategory>('SSC_CGL');
  const [testType, setTestType] = useState<TestType | 'ALL'>('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<PaperStateStatus | 'ALL'>('ALL');
  const [showCreateDialog, setShowCreateDialog] = useState(false);
  const [createTestType, setCreateTestType] = useState<'FREE' | 'PAID'>('FREE');
  const [showCsvUploadDialog, setShowCsvUploadDialog] = useState(false);
  const [csvFile, setCsvFile] = useState<File | null>(null);

  // Activation dialog state
  const [activatingPaper, setActivatingPaper] = useState<Paper | null>(null);
  const [validityStartDate, setValidityStartDate] = useState('');
  const [validityEndDate, setValidityEndDate] = useState('');

  const showAlert = useUIStore((state) => state.showAlert);
  const isSuperAdmin = useAuthStore((state) => state.isSuperAdmin());

  // Fetch FREE papers
  const { data: freePapers, isLoading: loadingFree, refetch: refetchFree } = useAdminPaperList(
    category, 'FREE', 0, 50, testType === 'ALL' || testType === 'FREE'
  );

  // Fetch PAID papers
  const { data: paidPapers, isLoading: loadingPaid, refetch: refetchPaid } = useAdminPaperList(
    category, 'PAID', 0, 50, testType === 'ALL' || testType === 'PAID'
  );

  const updateStatusMutation = useUpdatePaperStatus();
  const deletePaperMutation = useDeletePaper();
  const uploadCsvMutation = useUploadCsv();

  // Combine papers
  const papers = useMemo(() => {
    if (testType === 'ALL') {
      const free = (freePapers || []).map((p: Paper) => ({ ...p, testType: 'FREE' as const }));
      const paid = (paidPapers || []).map((p: Paper) => ({ ...p, testType: 'PAID' as const }));
      return [...free, ...paid];
    }
    if (testType === 'FREE') return (freePapers || []).map((p: Paper) => ({ ...p, testType: 'FREE' as const }));
    return (paidPapers || []).map((p: Paper) => ({ ...p, testType: 'PAID' as const }));
  }, [freePapers, paidPapers, testType]);

  const isLoading = loadingFree || loadingPaid;

  const refetch = () => {
    refetchFree();
    refetchPaid();
  };

  const filteredPapers = useMemo(() => {
    return papers.filter((paper: Paper) => {
      const matchesSearch = paper.paperName.toLowerCase().includes(searchQuery.toLowerCase());
      const matchesStatus = statusFilter === 'ALL' || paper.status === statusFilter;
      return matchesSearch && matchesStatus;
    });
  }, [papers, searchQuery, statusFilter]);

  // Stats
  const stats = useMemo(() => {
    return {
      total: papers.length,
      free: papers.filter((p: Paper) => p.testType === 'FREE').length,
      paid: papers.filter((p: Paper) => p.testType === 'PAID').length,
      active: papers.filter((p: Paper) => p.status === 'ACTIVE').length,
      draft: papers.filter((p: Paper) => p.status === 'DRAFT').length,
    };
  }, [papers]);

  const isApprovedOrActive = (paper: Paper) => paper.status === 'APPROVED' || paper.status === 'ACTIVE';
  const canEditPaper = (paper: Paper) => paper.status === 'DRAFT' || isSuperAdmin;
  // Only SUPERADMIN can delete papers
  const canDeletePaper = () => isSuperAdmin;

  const handleEditPaper = (paper: Paper) => {
    router.push(`/admin/paper-editor/${category}/${paper.testType || 'FREE'}/${getPaperId(paper)}`);
  };

  const handleManageQuestions = (paper: Paper) => {
    router.push(`/admin/paper-questions/${category}/${paper.testType || 'FREE'}/${getPaperId(paper)}`);
  };

  const openActivationDialog = (paper: Paper) => {
    if (isApprovedOrActive(paper) && !isSuperAdmin) {
      showAlert('error', 'Only SUPERADMIN can change status of APPROVED/ACTIVE papers');
      return;
    }
    const today = new Date();
    const nextMonth = new Date();
    nextMonth.setMonth(nextMonth.getMonth() + 1);
    setValidityStartDate(today.toISOString().split('T')[0]);
    setValidityEndDate(nextMonth.toISOString().split('T')[0]);
    setActivatingPaper(paper);
  };

  const handleActivate = () => {
    if (!activatingPaper) return;
    if (!validityEndDate) {
      showAlert('error', 'Please select validity end date');
      return;
    }
    const startTimestamp = validityStartDate ? new Date(validityStartDate).getTime() : Date.now();
    const endTimestamp = new Date(validityEndDate).getTime();
    if (endTimestamp <= startTimestamp) {
      showAlert('error', 'End date must be after start date');
      return;
    }

    updateStatusMutation.mutate(
      {
        paperId: getPaperId(activatingPaper),
        status: 'ACTIVE',
        testType: activatingPaper.testType || 'FREE',
        validityStartDate: startTimestamp,
        validityEndDate: endTimestamp,
      },
      {
        onSuccess: () => {
          showAlert('success', 'Paper activated successfully');
          setActivatingPaper(null);
          refetch();
        },
        onError: (error: any) => {
          showAlert('error', error?.response?.data || 'Failed to activate paper');
        },
      }
    );
  };

  const handleStatusChange = (paper: Paper, newStatus: PaperStateStatus) => {
    if (newStatus === 'ACTIVE') {
      openActivationDialog(paper);
      return;
    }
    if (isApprovedOrActive(paper) && !isSuperAdmin) {
      showAlert('error', 'Only SUPERADMIN can change status of APPROVED/ACTIVE papers');
      return;
    }
    const statusLabels: Record<string, string> = { DRAFT: 'Draft', APPROVED: 'Approved', ACTIVE: 'Active' };
    if (confirm(`Change paper status to ${statusLabels[newStatus]}?`)) {
      updateStatusMutation.mutate(
        { paperId: getPaperId(paper), status: newStatus, testType: paper.testType || 'FREE' },
        {
          onSuccess: () => {
            showAlert('success', `Paper status changed to ${statusLabels[newStatus]}`);
            refetch();
          },
          onError: (error: any) => {
            showAlert('error', error?.response?.data || 'Failed to update status');
          },
        }
      );
    }
  };

  const handleDeletePaper = (paper: Paper) => {
    if (!canDeletePaper()) {
      showAlert('error', 'Only SUPERADMIN can delete papers');
      return;
    }
    if (isApprovedOrActive(paper)) {
      showAlert('error', 'Cannot delete APPROVED or ACTIVE papers. Set status to DRAFT first.');
      return;
    }
    if (confirm(`Are you sure you want to delete "${paper.paperName}"? This action cannot be undone.`)) {
      deletePaperMutation.mutate(
        {
          paperId: getPaperId(paper),
          testType: paper.testType || 'FREE',
        },
        {
          onSuccess: () => {
            refetch();
          },
        }
      );
    }
  };

  const openCreateDialog = (type: 'FREE' | 'PAID') => {
    setCreateTestType(type);
    setShowCreateDialog(true);
  };

  const handleCsvUpload = async () => {
    if (!csvFile) {
      showAlert('error', 'Please select a CSV file');
      return;
    }
    try {
      await uploadCsvMutation.mutateAsync(csvFile);
      setShowCsvUploadDialog(false);
      setCsvFile(null);
      refetch();
    } catch (error) {
      // Error is handled by the mutation
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      if (!file.name.toLowerCase().endsWith('.csv')) {
        showAlert('error', 'Please select a valid CSV file');
        return;
      }
      setCsvFile(file);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Paper Management</h1>
          <p className="text-slate-600">Create and manage test papers (Free & Paid)</p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" className="gap-2" onClick={() => setShowCsvUploadDialog(true)}>
            <Upload className="h-4 w-4" />
            Upload CSV
          </Button>
          <Dialog open={showCreateDialog} onOpenChange={setShowCreateDialog}>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button className="gap-2">
                  <Plus className="h-4 w-4" />
                  Create Paper
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent>
                <DropdownMenuItem onClick={() => openCreateDialog('FREE')}>
                  <Badge variant="secondary" className="mr-2">FREE</Badge>
                  Create Free Paper
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => openCreateDialog('PAID')}>
                  <Badge variant="default" className="mr-2">PAID</Badge>
                  Create Paid Paper
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          <DialogContent className="max-w-3xl max-h-[90vh] overflow-hidden">
            <DialogHeader>
              <DialogTitle>Create {createTestType} Paper</DialogTitle>
              <DialogDescription>Create a new test paper with questions</DialogDescription>
            </DialogHeader>
            <CreatePaperForm
              initialTestType={createTestType}
              onSuccess={() => {
                setShowCreateDialog(false);
                refetch();
              }}
              onCancel={() => setShowCreateDialog(false)}
            />
          </DialogContent>
        </Dialog>
        </div>
      </div>

      {/* CSV Upload Dialog */}
      <Dialog open={showCsvUploadDialog} onOpenChange={(open) => {
        setShowCsvUploadDialog(open);
        if (!open) setCsvFile(null);
      }}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <FileSpreadsheet className="h-5 w-5" />
              Upload Paper from CSV
            </DialogTitle>
            <DialogDescription>
              Upload a CSV file containing questions to create a new paper. The file should follow the SSC paper format.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="csvFile">CSV File</Label>
              <Input
                id="csvFile"
                type="file"
                accept=".csv"
                onChange={handleFileChange}
                className="cursor-pointer"
              />
              {csvFile && (
                <p className="text-sm text-slate-500">
                  Selected: {csvFile.name} ({(csvFile.size / 1024).toFixed(1)} KB)
                </p>
              )}
            </div>
            <div className="p-3 bg-slate-50 rounded-lg text-sm space-y-2">
              <p className="font-medium text-slate-700">CSV Format Requirements:</p>
              <ul className="list-disc list-inside text-slate-600 space-y-1">
                <li>First row: Column headers</li>
                <li>Required: paperType, paperCategory, paperName, sectionName, question, options</li>
                <li>correctOption values: 1, 2, 3, or 4</li>
                <li>testType: FREE (default) or PAID</li>
              </ul>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setShowCsvUploadDialog(false)}>
              Cancel
            </Button>
            <Button
              onClick={handleCsvUpload}
              disabled={!csvFile || uploadCsvMutation.isPending}
              className="gap-2"
            >
              {uploadCsvMutation.isPending ? (
                <>
                  <Loader2 className="h-4 w-4 animate-spin" />
                  Uploading...
                </>
              ) : (
                <>
                  <Upload className="h-4 w-4" />
                  Upload Paper
                </>
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <div className="p-2 bg-blue-100 rounded-lg">
              <FileText className="h-5 w-5 text-blue-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-slate-900">{stats.total}</p>
              <p className="text-sm text-slate-500">Total Papers</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <div className="p-2 bg-slate-100 rounded-lg">
              <FileText className="h-5 w-5 text-slate-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-slate-900">{stats.free}</p>
              <p className="text-sm text-slate-500">Free Papers</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <div className="p-2 bg-purple-100 rounded-lg">
              <Trophy className="h-5 w-5 text-purple-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-slate-900">{stats.paid}</p>
              <p className="text-sm text-slate-500">Paid Papers</p>
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
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="pt-6">
          <div className="flex flex-wrap gap-4">
            <div className="flex-1 min-w-[200px]">
              <Select value={category} onValueChange={(value) => setCategory(value as PaperCategory)}>
                <SelectTrigger><SelectValue placeholder="Select category" /></SelectTrigger>
                <SelectContent>
                  {categories.map((cat) => (
                    <SelectItem key={cat.value} value={cat.value}>{cat.label}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="flex-1 min-w-[150px]">
              <Select value={testType} onValueChange={(value) => setTestType(value as TestType | 'ALL')}>
                <SelectTrigger><SelectValue placeholder="Test type" /></SelectTrigger>
                <SelectContent>
                  {testTypes.map((type) => (
                    <SelectItem key={type.value} value={type.value}>{type.label}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="flex-1 min-w-[150px]">
              <Select value={statusFilter} onValueChange={(value) => setStatusFilter(value as PaperStateStatus | 'ALL')}>
                <SelectTrigger><SelectValue placeholder="Status" /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="ALL">All Statuses</SelectItem>
                  <SelectItem value="DRAFT">Draft</SelectItem>
                  <SelectItem value="APPROVED">Approved</SelectItem>
                  <SelectItem value="ACTIVE">Active</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="flex-1 min-w-[300px]">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
                <Input
                  placeholder="Search papers..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Papers Table */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <FileText className="h-5 w-5" />
            Papers ({filteredPapers.length})
          </CardTitle>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="space-y-4">
              {[1, 2, 3, 4, 5].map((i) => (<Skeleton key={i} className="h-12 w-full" />))}
            </div>
          ) : filteredPapers.length > 0 ? (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Paper Name</TableHead>
                  <TableHead>Type</TableHead>
                  <TableHead>Questions</TableHead>
                  <TableHead>Duration</TableHead>
                  <TableHead>Score</TableHead>
                  <TableHead>Validity Range</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredPapers.map((paper: Paper) => (
                  <TableRow key={`${paper.testType || 'FREE'}-${getPaperId(paper)}`}>
                    <TableCell className="font-medium">{paper.paperName}</TableCell>
                    <TableCell>
                      <Badge variant={paper.testType === 'PAID' ? 'default' : 'secondary'}>
                        {paper.testType || 'FREE'}
                      </Badge>
                    </TableCell>
                    <TableCell>{paper.totalQuestions || '-'}</TableCell>
                    <TableCell>{formatTime(paper.totalTime)}</TableCell>
                    <TableCell>{paper.totalScore}</TableCell>
                    <TableCell>
                      <div className="text-xs space-y-1">
                        {paper.validityRangeStartDateTime || paper.validityRangeEndDateTime ? (
                          <>
                            <div>{formatDate(paper.validityRangeStartDateTime)} - {formatDate(paper.validityRangeEndDateTime)}</div>
                            {paper.status === 'ACTIVE' && (
                              isCurrentlyValid(paper) ? (
                                <Badge variant="outline" className="bg-green-50 text-green-700 text-[10px]">Valid</Badge>
                              ) : isExpired(paper) ? (
                                <Badge variant="outline" className="bg-red-50 text-red-700 text-[10px]">Expired</Badge>
                              ) : (
                                <Badge variant="outline" className="bg-yellow-50 text-yellow-700 text-[10px]">Upcoming</Badge>
                              )
                            )}
                          </>
                        ) : <span className="text-slate-400">Not set</span>}
                      </div>
                    </TableCell>
                    <TableCell>{getStatusBadge(paper.status)}</TableCell>
                    <TableCell className="text-right">
                      <div className="flex items-center justify-end gap-1">
                        <Button variant="ghost" size="sm" onClick={() => handleEditPaper(paper)} title="Edit Paper"
                          className={!canEditPaper(paper) ? 'opacity-50' : ''}>
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button variant="ghost" size="sm" onClick={() => handleManageQuestions(paper)} title="Manage Questions">
                          <ListOrdered className="h-4 w-4" />
                        </Button>
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button variant="ghost" size="sm"><MoreVertical className="h-4 w-4" /></Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end">
                            {paper.status !== 'DRAFT' && (
                              <DropdownMenuItem onClick={() => handleStatusChange(paper, 'DRAFT')} disabled={isApprovedOrActive(paper) && !isSuperAdmin}>
                                <AlertCircle className="h-4 w-4 mr-2 text-slate-500" />Set as Draft
                              </DropdownMenuItem>
                            )}
                            {paper.status !== 'APPROVED' && (
                              <DropdownMenuItem onClick={() => handleStatusChange(paper, 'APPROVED')} disabled={isApprovedOrActive(paper) && !isSuperAdmin}>
                                <Check className="h-4 w-4 mr-2 text-blue-500" />Approve
                              </DropdownMenuItem>
                            )}
                            {paper.status !== 'ACTIVE' && (
                              <DropdownMenuItem onClick={() => handleStatusChange(paper, 'ACTIVE')} disabled={isApprovedOrActive(paper) && !isSuperAdmin}>
                                <Play className="h-4 w-4 mr-2 text-green-500" />Activate
                              </DropdownMenuItem>
                            )}
                            {isSuperAdmin && (
                              <>
                                <DropdownMenuSeparator />
                                <DropdownMenuItem onClick={() => handleDeletePaper(paper)} className="text-red-600">
                                  <Trash2 className="h-4 w-4 mr-2" />Delete
                                </DropdownMenuItem>
                              </>
                            )}
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          ) : (
            <div className="text-center py-12 text-slate-500">
              <FileText className="h-12 w-12 mx-auto mb-4 text-slate-300" />
              <p>No papers found</p>
              <p className="text-sm">Create a new paper to get started</p>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Activation Dialog */}
      <Dialog open={!!activatingPaper} onOpenChange={(open) => !open && setActivatingPaper(null)}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Activate Paper</DialogTitle>
            <DialogDescription>Set the validity period for this paper.</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label>Paper Name</Label>
              <p className="text-sm font-medium text-slate-700">{activatingPaper?.paperName}</p>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="paperValidityStartDate">Start Date</Label>
                <Input id="paperValidityStartDate" type="date" value={validityStartDate} onChange={(e) => setValidityStartDate(e.target.value)} />
              </div>
              <div className="space-y-2">
                <Label htmlFor="paperValidityEndDate">End Date *</Label>
                <Input id="paperValidityEndDate" type="date" value={validityEndDate} onChange={(e) => setValidityEndDate(e.target.value)} min={validityStartDate} />
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setActivatingPaper(null)}>Cancel</Button>
            <Button onClick={handleActivate} disabled={updateStatusMutation.isPending} className="gap-2">
              {updateStatusMutation.isPending ? <Loader2 className="h-4 w-4 animate-spin" /> : <Play className="h-4 w-4" />}
              Activate Paper
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
