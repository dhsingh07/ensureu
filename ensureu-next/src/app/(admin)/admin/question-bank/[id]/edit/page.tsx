'use client';

import { useState, useEffect, use } from 'react';
import { useRouter } from 'next/navigation';
import { ArrowLeft, Save, Send, Eye } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { MathEditor, MathRenderer } from '@/components/math';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
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
} from '@/components/ui/dialog';
import { useQuestionBankItem, useUpdateQuestion } from '@/hooks/use-question-bank';
import { ImageUpload } from '@/components/shared/image-upload';
import {
  QuestionBankCreatePayload,
  QuestionBankOption,
  SSC_CGL_TIER1_SUBJECTS,
  getTopicsForSubject,
  DifficultyLevel,
  QuestionTypeEnum,
} from '@/types/question-bank';
import { PaperType, PaperCategory, PaperSubCategory } from '@/types/paper';
import Link from 'next/link';

const PAPER_TYPES: PaperType[] = ['SSC', 'BANK'];

const PAPER_CATEGORIES: Record<PaperType, { value: PaperCategory; label: string }[]> = {
  SSC: [
    { value: 'SSC_CGL', label: 'SSC CGL' },
    { value: 'SSC_CPO', label: 'SSC CPO' },
    { value: 'SSC_CHSL', label: 'SSC CHSL' },
  ],
  BANK: [
    { value: 'BANK_PO', label: 'Bank PO' },
  ],
};

const PAPER_SUBCATEGORIES: Record<PaperCategory, { value: PaperSubCategory; label: string }[]> = {
  SSC_CGL: [
    { value: 'SSC_CGL_TIER1', label: 'Tier 1' },
    { value: 'SSC_CGL_TIER2', label: 'Tier 2' },
  ],
  SSC_CPO: [
    { value: 'SSC_CPO_TIER1', label: 'Tier 1' },
    { value: 'SSC_CPO_TIER2', label: 'Tier 2' },
  ],
  SSC_CHSL: [
    { value: 'SSC_CHSL_TIER1', label: 'Tier 1' },
    { value: 'SSC_CHSL_TIER2', label: 'Tier 2' },
  ],
  BANK_PO: [
    { value: 'BANK_PO_PRE', label: 'Prelims' },
    { value: 'BANK_PO_MAIN', label: 'Mains' },
  ],
};

interface FormData {
  paperType: PaperType;
  paperCategory: PaperCategory | '';
  paperSubCategory: PaperSubCategory | '';
  subject: string;
  topic: string;
  subTopic: string;
  question: string;
  questionHindi: string;
  options: QuestionBankOption[];
  correctOption: string;
  solution: string;
  solutionHindi: string;
  questionType: QuestionTypeEnum;
  difficultyLevel: DifficultyLevel;
  marks: number;
  negativeMarks: number;
  averageTime: number;
  tags: string;
  year: string;
  source: string;
  imageUrl: string;
  imagePosition: 'above' | 'below' | 'inline';
}

const initialFormData: FormData = {
  paperType: 'SSC',
  paperCategory: '',
  paperSubCategory: '',
  subject: '',
  topic: '',
  subTopic: '',
  question: '',
  questionHindi: '',
  options: [
    { key: 'A', value: '', valueHindi: '' },
    { key: 'B', value: '', valueHindi: '' },
    { key: 'C', value: '', valueHindi: '' },
    { key: 'D', value: '', valueHindi: '' },
  ],
  correctOption: '',
  solution: '',
  solutionHindi: '',
  questionType: 'SINGLE',
  difficultyLevel: 'MEDIUM',
  marks: 2,
  negativeMarks: 0.5,
  averageTime: 60,
  tags: '',
  year: '',
  source: '',
  imageUrl: '',
  imagePosition: 'above',
};

export default function EditQuestionPage({ params }: { params: Promise<{ id: string }> }) {
  const resolvedParams = use(params);
  const router = useRouter();
  const { data: question, isLoading } = useQuestionBankItem(resolvedParams.id);
  const updateQuestion = useUpdateQuestion();
  const [formData, setFormData] = useState<FormData>(initialFormData);
  const [showPreview, setShowPreview] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [initialized, setInitialized] = useState(false);

  // Initialize form with existing question data
  useEffect(() => {
    if (question && !initialized) {
      // Ensure we have 4 options
      const existingOptions = question.problem.options || [];
      const options: QuestionBankOption[] = ['A', 'B', 'C', 'D'].map((key, index) => {
        const existing = existingOptions[index];
        return {
          key,
          value: existing?.value || '',
          valueHindi: existing?.valueHindi || '',
        };
      });

      setFormData({
        paperType: question.paperType || 'SSC',
        paperCategory: question.paperCategory || '',
        paperSubCategory: question.paperSubCategory || '',
        subject: question.subject || '',
        topic: question.topic || '',
        subTopic: question.subTopic || '',
        question: question.problem.question || '',
        questionHindi: question.problem.questionHindi || '',
        options,
        correctOption: question.problem.correctOption || '',
        solution: question.problem.solution || '',
        solutionHindi: question.problem.solutionHindi || '',
        questionType: question.questionType || 'SINGLE',
        difficultyLevel: question.difficultyLevel || 'MEDIUM',
        marks: question.marks || 2,
        negativeMarks: question.negativeMarks || 0.5,
        averageTime: question.averageTime || 60,
        tags: question.tags?.join(', ') || '',
        year: question.year?.toString() || '',
        source: question.source || '',
        imageUrl: question.imageUrl || '',
        imagePosition: (question.imagePosition as 'above' | 'below' | 'inline') || 'above',
      });
      setInitialized(true);
    }
  }, [question, initialized]);

  const availableTopics = formData.subject ? getTopicsForSubject(formData.subject) : [];

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.paperCategory) newErrors.paperCategory = 'Category is required';
    if (!formData.paperSubCategory) newErrors.paperSubCategory = 'Sub-category is required';
    if (!formData.subject) newErrors.subject = 'Subject is required';
    if (!formData.topic) newErrors.topic = 'Topic is required';
    if (!formData.question.trim()) newErrors.question = 'Question is required';
    if (!formData.correctOption) newErrors.correctOption = 'Correct option is required';

    const filledOptions = formData.options.filter((o) => o.value.trim());
    if (filledOptions.length < 2) {
      newErrors.options = 'At least 2 options are required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const buildPayload = (submitForReview: boolean): QuestionBankCreatePayload => {
    return {
      paperType: formData.paperType,
      paperCategory: formData.paperCategory as PaperCategory,
      paperSubCategory: formData.paperSubCategory as PaperSubCategory,
      subject: formData.subject,
      topic: formData.topic,
      subTopic: formData.subTopic || undefined,
      problem: {
        question: formData.question,
        questionHindi: formData.questionHindi || undefined,
        options: formData.options.filter((o) => o.value.trim()),
        correctOption: formData.correctOption,
        solution: formData.solution || undefined,
        solutionHindi: formData.solutionHindi || undefined,
      },
      questionType: formData.questionType,
      difficultyLevel: formData.difficultyLevel,
      marks: formData.marks,
      negativeMarks: formData.negativeMarks,
      averageTime: formData.averageTime,
      hasImage: !!formData.imageUrl,
      imageUrl: formData.imageUrl || undefined,
      imagePosition: formData.imageUrl ? formData.imagePosition : undefined,
      tags: formData.tags ? formData.tags.split(',').map((t) => t.trim()) : undefined,
      year: formData.year ? parseInt(formData.year) : undefined,
      source: formData.source || undefined,
      submitForReview,
    };
  };

  const handleSave = async () => {
    if (!validateForm()) return;

    try {
      await updateQuestion.mutateAsync({
        id: resolvedParams.id,
        payload: buildPayload(false),
      });
      router.push(`/admin/question-bank/${resolvedParams.id}`);
    } catch (error) {
      console.error('Failed to save:', error);
    }
  };

  const handleSaveAndSubmitForReview = async () => {
    if (!validateForm()) return;

    try {
      await updateQuestion.mutateAsync({
        id: resolvedParams.id,
        payload: buildPayload(true),
      });
      router.push(`/admin/question-bank/${resolvedParams.id}`);
    } catch (error) {
      console.error('Failed to submit for review:', error);
    }
  };

  const updateOption = (index: number, field: 'value' | 'valueHindi', value: string) => {
    const newOptions = [...formData.options];
    newOptions[index] = { ...newOptions[index], [field]: value };
    setFormData({ ...formData, options: newOptions });
  };

  if (isLoading) {
    return (
      <div className="container mx-auto py-6 px-4 max-w-4xl">
        <Skeleton className="h-8 w-64 mb-6" />
        <div className="space-y-6">
          <Skeleton className="h-48 w-full" />
          <Skeleton className="h-64 w-full" />
          <Skeleton className="h-48 w-full" />
        </div>
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

  const canSubmitForReview = question.status === 'DRAFT' || question.status === 'REJECTED';

  return (
    <div className="container mx-auto py-6 px-4 max-w-4xl">
      <div className="flex items-center gap-4 mb-6">
        <Link href={`/admin/question-bank/${resolvedParams.id}`}>
          <Button variant="ghost" size="sm">
            <ArrowLeft className="mr-2 h-4 w-4" />
            Back
          </Button>
        </Link>
        <div>
          <h1 className="text-2xl font-bold">Edit Question</h1>
          <p className="text-gray-600">
            <span className="font-mono">{question.questionId}</span>
            <Badge className="ml-2" variant="outline">
              {question.status}
            </Badge>
          </p>
        </div>
      </div>

      <div className="space-y-6">
        {/* Classification */}
        <Card>
          <CardHeader>
            <CardTitle>Classification</CardTitle>
            <CardDescription>Categorize the question for easy organization</CardDescription>
          </CardHeader>
          <CardContent className="grid gap-4 md:grid-cols-2">
            <div className="space-y-2">
              <Label>Paper Type</Label>
              <Select
                value={formData.paperType}
                onValueChange={(v: PaperType) =>
                  setFormData({ ...formData, paperType: v, paperCategory: '', paperSubCategory: '' })
                }
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {PAPER_TYPES.map((t) => (
                    <SelectItem key={t} value={t}>{t}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label>Category *</Label>
              <Select
                value={formData.paperCategory}
                onValueChange={(v: PaperCategory) =>
                  setFormData({ ...formData, paperCategory: v, paperSubCategory: '' })
                }
              >
                <SelectTrigger className={errors.paperCategory ? 'border-red-500' : ''}>
                  <SelectValue placeholder="Select category" />
                </SelectTrigger>
                <SelectContent>
                  {PAPER_CATEGORIES[formData.paperType]?.map((c) => (
                    <SelectItem key={c.value} value={c.value}>{c.label}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {errors.paperCategory && <p className="text-sm text-red-500">{errors.paperCategory}</p>}
            </div>

            <div className="space-y-2">
              <Label>Sub-Category *</Label>
              <Select
                value={formData.paperSubCategory}
                onValueChange={(v: PaperSubCategory) =>
                  setFormData({ ...formData, paperSubCategory: v })
                }
                disabled={!formData.paperCategory}
              >
                <SelectTrigger className={errors.paperSubCategory ? 'border-red-500' : ''}>
                  <SelectValue placeholder="Select sub-category" />
                </SelectTrigger>
                <SelectContent>
                  {formData.paperCategory &&
                    PAPER_SUBCATEGORIES[formData.paperCategory]?.map((c) => (
                      <SelectItem key={c.value} value={c.value}>{c.label}</SelectItem>
                    ))}
                </SelectContent>
              </Select>
              {errors.paperSubCategory && <p className="text-sm text-red-500">{errors.paperSubCategory}</p>}
            </div>

            <div className="space-y-2">
              <Label>Subject *</Label>
              <Select
                value={formData.subject}
                onValueChange={(v) => setFormData({ ...formData, subject: v, topic: '' })}
              >
                <SelectTrigger className={errors.subject ? 'border-red-500' : ''}>
                  <SelectValue placeholder="Select subject" />
                </SelectTrigger>
                <SelectContent>
                  {SSC_CGL_TIER1_SUBJECTS.map((s) => (
                    <SelectItem key={s.name} value={s.name}>{s.name}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {errors.subject && <p className="text-sm text-red-500">{errors.subject}</p>}
            </div>

            <div className="space-y-2">
              <Label>Topic *</Label>
              <Select
                value={formData.topic}
                onValueChange={(v) => setFormData({ ...formData, topic: v })}
                disabled={!formData.subject}
              >
                <SelectTrigger className={errors.topic ? 'border-red-500' : ''}>
                  <SelectValue placeholder="Select topic" />
                </SelectTrigger>
                <SelectContent>
                  {availableTopics.map((t) => (
                    <SelectItem key={t} value={t}>{t}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {errors.topic && <p className="text-sm text-red-500">{errors.topic}</p>}
            </div>

            <div className="space-y-2">
              <Label>Difficulty Level</Label>
              <Select
                value={formData.difficultyLevel}
                onValueChange={(v: DifficultyLevel) => setFormData({ ...formData, difficultyLevel: v })}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="EASY">Easy</SelectItem>
                  <SelectItem value="MEDIUM">Medium</SelectItem>
                  <SelectItem value="HARD">Hard</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </CardContent>
        </Card>

        {/* Question Content */}
        <Card>
          <CardHeader>
            <CardTitle>Question</CardTitle>
            <CardDescription>Enter the question text with math symbols using the toolbar</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label>Question Text (English) *</Label>
              <MathEditor
                value={formData.question}
                onChange={(value) => setFormData({ ...formData, question: value })}
                placeholder="Enter question text... Use toolbar for math symbols"
                rows={4}
                className={errors.question ? '[&_textarea]:border-red-500' : ''}
              />
              {errors.question && <p className="text-sm text-red-500">{errors.question}</p>}
            </div>

            <div className="space-y-2">
              <Label>Question Text (Hindi) - Optional</Label>
              <MathEditor
                value={formData.questionHindi}
                onChange={(value) => setFormData({ ...formData, questionHindi: value })}
                placeholder="Enter Hindi translation with math symbols..."
                rows={3}
              />
            </div>

            {/* Question Image */}
            <div className="space-y-2">
              <Label>Question Image (Optional)</Label>
              <div className="flex gap-4 items-start">
                <div className="flex-1">
                  <ImageUpload
                    value={formData.imageUrl || undefined}
                    onChange={(url) => setFormData({ ...formData, imageUrl: url || '' })}
                    folder="questions"
                    placeholder="Click or drag to upload question image"
                  />
                </div>
                {formData.imageUrl && (
                  <div className="space-y-2">
                    <Label>Position</Label>
                    <Select
                      value={formData.imagePosition}
                      onValueChange={(v: 'above' | 'below' | 'inline') =>
                        setFormData({ ...formData, imagePosition: v })
                      }
                    >
                      <SelectTrigger className="w-32">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="above">Above</SelectItem>
                        <SelectItem value="below">Below</SelectItem>
                        <SelectItem value="inline">Inline</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                )}
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Options */}
        <Card>
          <CardHeader>
            <CardTitle>Options</CardTitle>
            <CardDescription>Enter answer options and select the correct one</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            {errors.options && <p className="text-sm text-red-500">{errors.options}</p>}
            {formData.options.map((option, index) => (
              <div key={option.key} className="flex items-start gap-4">
                <div className="flex items-center gap-2 pt-2">
                  <input
                    type="radio"
                    name="correctOption"
                    checked={formData.correctOption === option.key}
                    onChange={() => setFormData({ ...formData, correctOption: option.key })}
                    className="h-4 w-4"
                  />
                  <Badge variant={formData.correctOption === option.key ? 'default' : 'outline'}>
                    {option.key}
                  </Badge>
                </div>
                <div className="flex-1 space-y-2">
                  <Input
                    placeholder={`Option ${option.key} (English)`}
                    value={option.value}
                    onChange={(e) => updateOption(index, 'value', e.target.value)}
                  />
                  <Input
                    placeholder={`Option ${option.key} (Hindi) - Optional`}
                    value={option.valueHindi || ''}
                    onChange={(e) => updateOption(index, 'valueHindi', e.target.value)}
                  />
                </div>
              </div>
            ))}
            {errors.correctOption && <p className="text-sm text-red-500">{errors.correctOption}</p>}
          </CardContent>
        </Card>

        {/* Solution */}
        <Card>
          <CardHeader>
            <CardTitle>Solution / Explanation</CardTitle>
            <CardDescription>Provide detailed explanation with math formulas if needed</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label>Solution (English)</Label>
              <MathEditor
                value={formData.solution}
                onChange={(value) => setFormData({ ...formData, solution: value })}
                placeholder="Enter solution/explanation with math..."
                rows={4}
              />
            </div>

            <div className="space-y-2">
              <Label>Solution (Hindi) - Optional</Label>
              <MathEditor
                value={formData.solutionHindi}
                onChange={(value) => setFormData({ ...formData, solutionHindi: value })}
                placeholder="Enter Hindi translation..."
                rows={3}
              />
            </div>
          </CardContent>
        </Card>

        {/* Metadata */}
        <Card>
          <CardHeader>
            <CardTitle>Metadata</CardTitle>
            <CardDescription>Additional information about the question</CardDescription>
          </CardHeader>
          <CardContent className="grid gap-4 md:grid-cols-3">
            <div className="space-y-2">
              <Label>Marks</Label>
              <Input
                type="number"
                value={formData.marks}
                onChange={(e) => setFormData({ ...formData, marks: parseFloat(e.target.value) || 0 })}
              />
            </div>

            <div className="space-y-2">
              <Label>Negative Marks</Label>
              <Input
                type="number"
                step="0.25"
                value={formData.negativeMarks}
                onChange={(e) => setFormData({ ...formData, negativeMarks: parseFloat(e.target.value) || 0 })}
              />
            </div>

            <div className="space-y-2">
              <Label>Avg. Time (seconds)</Label>
              <Input
                type="number"
                value={formData.averageTime}
                onChange={(e) => setFormData({ ...formData, averageTime: parseInt(e.target.value) || 0 })}
              />
            </div>

            <div className="space-y-2">
              <Label>Tags (comma-separated)</Label>
              <Input
                placeholder="algebra, percentage, tier-1"
                value={formData.tags}
                onChange={(e) => setFormData({ ...formData, tags: e.target.value })}
              />
            </div>

            <div className="space-y-2">
              <Label>Year (if from past paper)</Label>
              <Input
                type="number"
                placeholder="2023"
                value={formData.year}
                onChange={(e) => setFormData({ ...formData, year: e.target.value })}
              />
            </div>

            <div className="space-y-2">
              <Label>Source</Label>
              <Input
                placeholder="SSC CGL 2023 Shift 1"
                value={formData.source}
                onChange={(e) => setFormData({ ...formData, source: e.target.value })}
              />
            </div>
          </CardContent>
        </Card>

        {/* Actions */}
        <div className="flex justify-end gap-4">
          <Button variant="outline" onClick={() => setShowPreview(true)}>
            <Eye className="mr-2 h-4 w-4" />
            Preview
          </Button>
          <Button variant="outline" onClick={handleSave} disabled={updateQuestion.isPending}>
            <Save className="mr-2 h-4 w-4" />
            Save Changes
          </Button>
          {canSubmitForReview && (
            <Button onClick={handleSaveAndSubmitForReview} disabled={updateQuestion.isPending}>
              <Send className="mr-2 h-4 w-4" />
              Save & Submit for Review
            </Button>
          )}
        </div>
      </div>

      {/* Preview Dialog */}
      <Dialog open={showPreview} onOpenChange={setShowPreview}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Question Preview</DialogTitle>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="flex gap-2">
              <Badge>{formData.subject}</Badge>
              <Badge variant="outline">{formData.topic}</Badge>
              <Badge
                className={
                  formData.difficultyLevel === 'EASY'
                    ? 'bg-green-100 text-green-800'
                    : formData.difficultyLevel === 'MEDIUM'
                    ? 'bg-yellow-100 text-yellow-800'
                    : 'bg-red-100 text-red-800'
                }
              >
                {formData.difficultyLevel}
              </Badge>
            </div>

            <div className="border rounded-lg p-4">
              {formData.imageUrl && formData.imagePosition === 'above' && (
                <div className="mb-4">
                  <img src={formData.imageUrl} alt="Question" className="max-w-full h-auto rounded" />
                </div>
              )}
              <div className="font-medium mb-4">
                <MathRenderer content={formData.question} />
              </div>
              {formData.imageUrl && formData.imagePosition === 'below' && (
                <div className="mb-4">
                  <img src={formData.imageUrl} alt="Question" className="max-w-full h-auto rounded" />
                </div>
              )}

              <div className="space-y-2">
                {formData.options
                  .filter((o) => o.value.trim())
                  .map((option) => (
                    <div
                      key={option.key}
                      className={`p-3 rounded-lg border ${
                        formData.correctOption === option.key
                          ? 'bg-green-50 border-green-500'
                          : 'bg-gray-50'
                      }`}
                    >
                      <span className="font-medium mr-2">{option.key}.</span>
                      <MathRenderer content={option.value} className="inline" />
                      {formData.correctOption === option.key && (
                        <Badge className="ml-2 bg-green-600">Correct</Badge>
                      )}
                    </div>
                  ))}
              </div>

              {formData.solution && (
                <div className="mt-4 p-4 bg-blue-50 rounded-lg">
                  <p className="font-medium text-blue-800 mb-2">Solution:</p>
                  <MathRenderer content={formData.solution} />
                </div>
              )}
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
