'use client';

import { useState } from 'react';
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
import { useUIStore } from '@/stores/ui-store';
import { useCreatePaper } from '@/hooks/use-admin';
import { Zap, Plus, Trash2, Save, Loader2 } from 'lucide-react';
import type { PaperCategory, PaperSubCategory, TestType } from '@/types/paper';

interface QuickQuestion {
  id: string;
  question: string;
  options: string[];
  correctAnswer: number;
}

const categories: { value: PaperCategory; label: string }[] = [
  { value: 'SSC_CGL', label: 'SSC CGL' },
  { value: 'SSC_CHSL', label: 'SSC CHSL' },
  { value: 'SSC_CPO', label: 'SSC CPO' },
  { value: 'BANK_PO', label: 'Bank PO' },
];

const subCategories: Record<PaperCategory, { value: PaperSubCategory; label: string }[]> = {
  SSC_CGL: [
    { value: 'SSC_CGL_TIER1', label: 'Tier 1' },
    { value: 'SSC_CGL_TIER2', label: 'Tier 2' },
  ],
  SSC_CHSL: [
    { value: 'SSC_CHSL_TIER1', label: 'Tier 1' },
    { value: 'SSC_CHSL_TIER2', label: 'Tier 2' },
  ],
  SSC_CPO: [
    { value: 'SSC_CPO_TIER1', label: 'Tier 1' },
    { value: 'SSC_CPO_TIER2', label: 'Tier 2' },
  ],
  BANK_PO: [
    { value: 'BANK_PO_PRE', label: 'Prelims' },
    { value: 'BANK_PO_MAIN', label: 'Mains' },
  ],
};

export default function QuickPaperPage() {
  const showAlert = useUIStore((state) => state.showAlert);
  const createPaperMutation = useCreatePaper();

  const [paperName, setPaperName] = useState('');
  const [category, setCategory] = useState<PaperCategory>('SSC_CGL');
  const [subCategory, setSubCategory] = useState<PaperSubCategory>('SSC_CGL_TIER1');
  const [testType, setTestType] = useState<TestType>('FREE');
  const [totalTime, setTotalTime] = useState(60);
  const [questions, setQuestions] = useState<QuickQuestion[]>([
    { id: '1', question: '', options: ['', '', '', ''], correctAnswer: 0 },
  ]);

  const addQuestion = () => {
    setQuestions([
      ...questions,
      {
        id: Date.now().toString(),
        question: '',
        options: ['', '', '', ''],
        correctAnswer: 0,
      },
    ]);
  };

  const removeQuestion = (id: string) => {
    if (questions.length > 1) {
      setQuestions(questions.filter((q) => q.id !== id));
    }
  };

  const updateQuestion = (id: string, field: keyof QuickQuestion, value: string | number | string[]) => {
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

  const handleSubmit = async () => {
    // Validation
    if (!paperName.trim()) {
      showAlert('error', 'Please enter a paper name');
      return;
    }

    const validQuestions = questions.filter(
      (q) => q.question.trim() && q.options.every((opt) => opt.trim())
    );

    if (validQuestions.length === 0) {
      showAlert('error', 'Please add at least one complete question');
      return;
    }

    // Create paper data structure
    // Backend expects totalTime in seconds, user enters in minutes
    const paperData = {
      paperName,
      paperType: category,
      paperSubCategory: subCategory,
      testType,
      totalTime: totalTime * 60,
      totalScore: validQuestions.length * 2,
      perQuestionScore: 2,
      negativeMarks: 0.5,
      pattern: {
        sections: [
          {
            id: 'section-1',
            title: 'General',
            sectionType: 'DEFAULT',
            subSections: [],
            questionData: {
              questions: validQuestions.map((q, idx) => ({
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
                  co: [String(q.correctAnswer)],
                },
                type: 'MCQ',
                questionType: 'RADIOBUTTON',
                complexityLevel: 'MEDIUM',
                complexityScore: 2,
                timeTakenInSecond: 0,
              })),
            },
            timeTakenSecond: 0,
          },
        ],
      },
    };

    createPaperMutation.mutate(paperData as any, {
      onSuccess: () => {
        // Reset form
        setPaperName('');
        setQuestions([
          { id: '1', question: '', options: ['', '', '', ''], correctAnswer: 0 },
        ]);
      },
    });
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
          <Zap className="h-6 w-6 text-primary" />
          Quick Paper Creator
        </h1>
        <p className="text-slate-600">
          Quickly create simple test papers with multiple choice questions
        </p>
      </div>

      {/* Paper Details */}
      <Card>
        <CardHeader>
          <CardTitle>Paper Details</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="paperName">Paper Name</Label>
              <Input
                id="paperName"
                value={paperName}
                onChange={(e) => setPaperName(e.target.value)}
                placeholder="Enter paper name"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="totalTime">Duration (minutes)</Label>
              <Input
                id="totalTime"
                type="number"
                value={totalTime}
                onChange={(e) => setTotalTime(Number(e.target.value))}
                min={1}
              />
            </div>
            <div className="space-y-2">
              <Label>Category</Label>
              <Select
                value={category}
                onValueChange={(value) => {
                  setCategory(value as PaperCategory);
                  setSubCategory(subCategories[value as PaperCategory][0].value);
                }}
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
            <div className="space-y-2">
              <Label>Sub Category</Label>
              <Select
                value={subCategory}
                onValueChange={(value) => setSubCategory(value as PaperSubCategory)}
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {subCategories[category].map((sub) => (
                    <SelectItem key={sub.value} value={sub.value}>
                      {sub.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label>Test Type</Label>
              <Select
                value={testType}
                onValueChange={(value) => setTestType(value as TestType)}
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
        </CardContent>
      </Card>

      {/* Questions */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold">Questions ({questions.length})</h2>
          <Button onClick={addQuestion} variant="outline" className="gap-2">
            <Plus className="h-4 w-4" />
            Add Question
          </Button>
        </div>

        {questions.map((question, qIndex) => (
          <Card key={question.id}>
            <CardHeader className="pb-2">
              <div className="flex items-center justify-between">
                <CardTitle className="text-base">Question {qIndex + 1}</CardTitle>
                {questions.length > 1 && (
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => removeQuestion(question.id)}
                  >
                    <Trash2 className="h-4 w-4 text-red-600" />
                  </Button>
                )}
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label>Question Text</Label>
                <Textarea
                  value={question.question}
                  onChange={(e) =>
                    updateQuestion(question.id, 'question', e.target.value)
                  }
                  placeholder="Enter the question"
                  rows={2}
                />
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {question.options.map((option, optIndex) => (
                  <div key={optIndex} className="space-y-2">
                    <div className="flex items-center gap-2">
                      <input
                        type="radio"
                        name={`correct-${question.id}`}
                        checked={question.correctAnswer === optIndex}
                        onChange={() =>
                          updateQuestion(question.id, 'correctAnswer', optIndex)
                        }
                        className="w-4 h-4"
                      />
                      <Label>Option {String.fromCharCode(65 + optIndex)}</Label>
                      {question.correctAnswer === optIndex && (
                        <span className="text-xs text-green-600">(Correct)</span>
                      )}
                    </div>
                    <Input
                      value={option}
                      onChange={(e) =>
                        updateOption(question.id, optIndex, e.target.value)
                      }
                      placeholder={`Option ${String.fromCharCode(65 + optIndex)}`}
                    />
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Submit */}
      <div className="flex justify-end">
        <Button
          onClick={handleSubmit}
          disabled={createPaperMutation.isPending}
          className="gap-2"
        >
          {createPaperMutation.isPending ? (
            <Loader2 className="h-4 w-4 animate-spin" />
          ) : (
            <Save className="h-4 w-4" />
          )}
          Create Paper
        </Button>
      </div>
    </div>
  );
}
