'use client';

import { useState, useEffect, use } from 'react';
import { useRouter } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
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
import { Skeleton } from '@/components/ui/skeleton';
import { Separator } from '@/components/ui/separator';
import {
  useAdminPaperById,
  useCreatePaper,
  useUpdatePaper,
} from '@/hooks/use-admin';
import { useUIStore } from '@/stores/ui-store';
import {
  ArrowLeft,
  Save,
  Loader2,
  FileText,
  Clock,
  Trophy,
  HelpCircle,
} from 'lucide-react';
import type { PaperCategory, PaperStateStatus } from '@/types/paper';

// Admin paper test type (excludes MISSED which is user-facing only)
type AdminTestType = 'FREE' | 'PAID';

interface PaperEditorParams {
  params: Promise<{
    slug: string[];
  }>;
}

// Root paper types (SSC, BANK)
type PaperType = 'SSC' | 'BANK';

// Paper categories (specific exams)
const paperCategories: { value: PaperCategory; label: string; paperType: PaperType }[] = [
  { value: 'SSC_CGL', label: 'SSC CGL', paperType: 'SSC' },
  { value: 'SSC_CHSL', label: 'SSC CHSL', paperType: 'SSC' },
  { value: 'SSC_CPO', label: 'SSC CPO', paperType: 'SSC' },
  { value: 'BANK_PO', label: 'Bank PO', paperType: 'BANK' },
];

// Sub-categories (tiers/levels)
const subCategories: Record<string, { value: string; label: string }[]> = {
  SSC_CGL: [
    { value: 'SSC_CGL_TIER1', label: 'Tier 1' },
    { value: 'SSC_CGL_TIER2', label: 'Tier 2' },
  ],
  SSC_CHSL: [
    { value: 'SSC_CHSL_TIER1', label: 'Tier 1' },
  ],
  SSC_CPO: [
    { value: 'SSC_CPO_TIER1', label: 'Tier 1' },
  ],
  BANK_PO: [
    { value: 'BANK_PO_PRELIMS', label: 'Prelims' },
    { value: 'BANK_PO_MAINS', label: 'Mains' },
  ],
};

// Helper to get paperType from paperCategory
function getPaperTypeFromCategory(category: PaperCategory): PaperType {
  if (category.startsWith('SSC')) return 'SSC';
  if (category.startsWith('BANK')) return 'BANK';
  return 'SSC'; // default
}

interface PaperFormData {
  paperName: string;
  paperCategory: PaperCategory; // SSC_CGL, SSC_CHSL, BANK_PO
  paperSubCategory: string;     // SSC_CGL_TIER1, etc.
  testType: AdminTestType;
  totalTime: number; // in seconds
  totalScore: number;
  negativeMarks: number;
  perQuestionScore: number;
  description: string;
  instructions: string;
  status: PaperStateStatus;
}

const defaultFormData: PaperFormData = {
  paperName: '',
  paperCategory: 'SSC_CGL',
  paperSubCategory: 'SSC_CGL_TIER1',
  testType: 'FREE',
  totalTime: 3600, // 60 minutes
  totalScore: 200,
  negativeMarks: 0.5,
  perQuestionScore: 2,
  description: '',
  instructions: '',
  status: 'DRAFT',
};

export default function PaperEditorPage({ params }: PaperEditorParams) {
  const router = useRouter();
  const showAlert = useUIStore((state) => state.showAlert);

  // Unwrap params Promise (Next.js 14+)
  const resolvedParams = use(params);

  // Parse slug: [category, testType, paperId]
  const [category, testType, paperId] = resolvedParams.slug || [];
  const isNewPaper = paperId === 'new';

  // Form state
  const [formData, setFormData] = useState<PaperFormData>({
    ...defaultFormData,
    paperCategory: (category as PaperCategory) || 'SSC_CGL',
    testType: (testType as AdminTestType) || 'FREE',
    paperSubCategory: `${category || 'SSC_CGL'}_TIER1`,
  });

  // Queries and mutations
  const { data: existingPaper, isLoading: loadingPaper } = useAdminPaperById(
    paperId,
    testType || 'FREE',
    !isNewPaper
  );
  const createMutation = useCreatePaper();
  const updateMutation = useUpdatePaper();

  const isLoading = loadingPaper;
  const isSaving = createMutation.isPending || updateMutation.isPending;

  // Populate form with existing paper data
  useEffect(() => {
    if (existingPaper && !isNewPaper) {
      // Backend returns paperCategory, we need to map it to our form
      const existingCategory = (existingPaper.paperType as unknown as PaperCategory) ||
                               (category as PaperCategory) || 'SSC_CGL';
      setFormData({
        paperName: existingPaper.paperName || '',
        paperCategory: existingCategory,
        paperSubCategory: existingPaper.paperSubCategory || `${existingCategory}_TIER1`,
        testType: existingPaper.testType || testType || 'FREE',
        totalTime: existingPaper.totalTime || 3600,
        totalScore: existingPaper.totalScore || 200,
        negativeMarks: existingPaper.negativeMarks || 0.5,
        perQuestionScore: existingPaper.perQuestionScore || 2,
        description: existingPaper.description || '',
        instructions: existingPaper.instructions || '',
        status: existingPaper.status || 'DRAFT',
      });
    }
  }, [existingPaper, isNewPaper, category, testType]);

  const handleInputChange = (
    field: keyof PaperFormData,
    value: string | number
  ) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleCategoryChange = (newCategory: PaperCategory) => {
    const subCats = subCategories[newCategory] || [];
    const firstSubCat = subCats[0]?.value || `${newCategory}_TIER1`;
    setFormData((prev) => ({
      ...prev,
      paperCategory: newCategory,
      paperSubCategory: firstSubCat,
    }));
  };

  const handleSubmit = async () => {
    // Validation
    if (!formData.paperName.trim()) {
      showAlert('error', 'Paper name is required');
      return;
    }

    if (formData.totalTime <= 0) {
      showAlert('error', 'Total time must be greater than 0');
      return;
    }

    if (formData.totalScore <= 0) {
      showAlert('error', 'Total score must be greater than 0');
      return;
    }

    // Build paper data matching backend PaperCollectionDto
    const paperData = {
      id: isNewPaper ? undefined : paperId,
      paperName: formData.paperName,
      paperType: getPaperTypeFromCategory(formData.paperCategory), // SSC or BANK
      paperCategory: formData.paperCategory, // SSC_CGL, SSC_CHSL, etc.
      paperSubCategory: formData.paperSubCategory, // SSC_CGL_TIER1, etc.
      testType: formData.testType, // FREE or PAID
      totalTime: formData.totalTime,
      totalScore: formData.totalScore,
      negativeMarks: formData.negativeMarks,
      perQuestionScore: formData.perQuestionScore,
      paperStateStatus: formData.status, // DRAFT, APPROVED, ACTIVE
    };

    try {
      if (isNewPaper) {
        await createMutation.mutateAsync(paperData as any);
        router.push('/admin/paper');
      } else {
        await updateMutation.mutateAsync(paperData as any);
        router.push('/admin/paper');
      }
    } catch (error) {
      // Error is handled by the mutation
    }
  };

  if (isLoading && !isNewPaper) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-10 w-64" />
        <Card>
          <CardContent className="pt-6 space-y-4">
            <Skeleton className="h-10 w-full" />
            <Skeleton className="h-10 w-full" />
            <Skeleton className="h-10 w-full" />
            <Skeleton className="h-32 w-full" />
          </CardContent>
        </Card>
      </div>
    );
  }

  const availableSubCategories = subCategories[formData.paperCategory] || [];

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => router.push('/admin/paper')}
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back
          </Button>
          <div>
            <h1 className="text-2xl font-bold text-slate-900">
              {isNewPaper ? 'Create New Paper' : 'Edit Paper'}
            </h1>
            <p className="text-slate-600">
              {isNewPaper
                ? 'Fill in the details to create a new test paper'
                : `Editing: ${existingPaper?.paperName || paperId}`}
            </p>
          </div>
        </div>
        <Button onClick={handleSubmit} disabled={isSaving} className="gap-2">
          {isSaving ? (
            <Loader2 className="h-4 w-4 animate-spin" />
          ) : (
            <Save className="h-4 w-4" />
          )}
          {isNewPaper ? 'Create Paper' : 'Save Changes'}
        </Button>
      </div>

      {/* Form */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Details */}
        <Card className="lg:col-span-2">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <FileText className="h-5 w-5" />
              Paper Details
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            {/* Paper Name */}
            <div className="space-y-2">
              <Label htmlFor="paperName">Paper Name *</Label>
              <Input
                id="paperName"
                placeholder="e.g., SSC CGL 2024 Practice Set 1"
                value={formData.paperName}
                onChange={(e) => handleInputChange('paperName', e.target.value)}
              />
            </div>

            {/* Category and Sub-category */}
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Category *</Label>
                <Select
                  value={formData.paperCategory}
                  onValueChange={(value) =>
                    handleCategoryChange(value as PaperCategory)
                  }
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {paperCategories.map((cat) => (
                      <SelectItem key={cat.value} value={cat.value}>
                        {cat.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label>Sub-category</Label>
                <Select
                  value={formData.paperSubCategory}
                  onValueChange={(value) =>
                    handleInputChange('paperSubCategory', value)
                  }
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {availableSubCategories.map((sub) => (
                      <SelectItem key={sub.value} value={sub.value}>
                        {sub.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>

            {/* Test Type */}
            <div className="space-y-2">
              <Label>Test Type *</Label>
              <Select
                value={formData.testType}
                onValueChange={(value) =>
                  handleInputChange('testType', value as AdminTestType)
                }
              >
                <SelectTrigger className="w-[200px]">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="FREE">Free</SelectItem>
                  <SelectItem value="PAID">Paid</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <Separator />

            {/* Description */}
            <div className="space-y-2">
              <Label htmlFor="description">Description</Label>
              <Textarea
                id="description"
                placeholder="Brief description of the paper..."
                value={formData.description}
                onChange={(e) =>
                  handleInputChange('description', e.target.value)
                }
                rows={3}
              />
            </div>

            {/* Instructions */}
            <div className="space-y-2">
              <Label htmlFor="instructions">Instructions for Students</Label>
              <Textarea
                id="instructions"
                placeholder="Instructions to be shown before starting the test..."
                value={formData.instructions}
                onChange={(e) =>
                  handleInputChange('instructions', e.target.value)
                }
                rows={4}
              />
            </div>
          </CardContent>
        </Card>

        {/* Scoring & Time */}
        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Clock className="h-5 w-5" />
                Time Settings
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="totalTime">Total Time (minutes) *</Label>
                <Input
                  id="totalTime"
                  type="number"
                  min={1}
                  value={Math.floor(formData.totalTime / 60)}
                  onChange={(e) =>
                    handleInputChange(
                      'totalTime',
                      parseInt(e.target.value || '0') * 60
                    )
                  }
                />
                <p className="text-xs text-muted-foreground">
                  {formData.totalTime} seconds total
                </p>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Trophy className="h-5 w-5" />
                Scoring Settings
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="totalScore">Total Score *</Label>
                <Input
                  id="totalScore"
                  type="number"
                  min={1}
                  value={formData.totalScore}
                  onChange={(e) =>
                    handleInputChange(
                      'totalScore',
                      parseInt(e.target.value || '0')
                    )
                  }
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="perQuestionScore">Marks per Question</Label>
                <Input
                  id="perQuestionScore"
                  type="number"
                  min={0}
                  step={0.5}
                  value={formData.perQuestionScore}
                  onChange={(e) =>
                    handleInputChange(
                      'perQuestionScore',
                      parseFloat(e.target.value || '0')
                    )
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
                    handleInputChange(
                      'negativeMarks',
                      parseFloat(e.target.value || '0')
                    )
                  }
                />
                <p className="text-xs text-muted-foreground">
                  Marks deducted for wrong answers
                </p>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <HelpCircle className="h-5 w-5" />
                Help
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-sm text-muted-foreground space-y-2">
                <p>
                  <strong>Free:</strong> Available to all users
                </p>
                <p>
                  <strong>Paid:</strong> Requires subscription
                </p>
                <Separator className="my-3" />
                <p>
                  After creating the paper, you can add questions using the
                  Quick Paper feature or upload from a file.
                </p>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
