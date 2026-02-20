'use client';

import { useState, useEffect, use } from 'react';
import { useRouter } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { Separator } from '@/components/ui/separator';
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
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from '@/components/ui/collapsible';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { useAdminPaperById, useUpdatePaper } from '@/hooks/use-admin';
import { useUIStore } from '@/stores/ui-store';
import {
  ArrowLeft,
  Plus,
  Edit,
  Trash2,
  Save,
  Loader2,
  FileText,
  BookOpen,
  MoreVertical,
  GripVertical,
  CheckCircle2,
  ChevronDown,
} from 'lucide-react';
import type { PaperCategory, Section, SubSection, Question, Problem, Option } from '@/types/paper';

interface PaperQuestionsParams {
  params: Promise<{
    slug: string[];
  }>;
}

// Question form data
interface QuestionFormData {
  question: string;
  questionHindi?: string;
  options: { text: string; correct: boolean }[];
  solution?: string;
  complexityLevel: string;
}

const defaultQuestionForm: QuestionFormData = {
  question: '',
  questionHindi: '',
  options: [
    { text: '', correct: true },
    { text: '', correct: false },
    { text: '', correct: false },
    { text: '', correct: false },
  ],
  solution: '',
  complexityLevel: 'MEDIUM',
};

// Section form data
interface SectionFormData {
  title: string;
  sectionType: string;
}

const defaultSectionForm: SectionFormData = {
  title: '',
  sectionType: 'GeneralIntelligence',
};

export default function PaperQuestionsPage({ params }: PaperQuestionsParams) {
  const router = useRouter();
  const showAlert = useUIStore((state) => state.showAlert);

  // Unwrap params Promise (Next.js 14+)
  const resolvedParams = use(params);
  const [category, testType, paperId] = resolvedParams.slug || [];

  // State
  const [questionDialogOpen, setQuestionDialogOpen] = useState(false);
  const [sectionDialogOpen, setSectionDialogOpen] = useState(false);
  const [editingQuestion, setEditingQuestion] = useState<{
    sectionId: string;
    subSectionId?: string;
    questionIndex: number;
  } | null>(null);
  const [targetSection, setTargetSection] = useState<{
    sectionId: string;
    subSectionId?: string;
  } | null>(null);
  const [questionForm, setQuestionForm] = useState<QuestionFormData>(defaultQuestionForm);
  const [sectionForm, setSectionForm] = useState<SectionFormData>(defaultSectionForm);

  // Local paper state for editing
  const [localPaper, setLocalPaper] = useState<any>(null);

  // Queries and mutations
  const { data: paper, isLoading } = useAdminPaperById(paperId, testType || 'FREE', !!paperId);
  const updateMutation = useUpdatePaper();

  // Initialize local paper state
  useEffect(() => {
    if (paper && !localPaper) {
      setLocalPaper({
        ...paper,
        pattern: paper.pattern || { sections: [] },
      });
    }
  }, [paper, localPaper]);

  const sections: Section[] = localPaper?.pattern?.sections || [];

  // Get all questions from a section (including subsections)
  const getQuestionsFromSection = (section: Section): Question[] => {
    const questions: Question[] = [];
    if (section.questionData?.questions) {
      questions.push(...section.questionData.questions);
    }
    if (section.subSections) {
      section.subSections.forEach((sub) => {
        if (sub.questionData?.questions) {
          questions.push(...sub.questionData.questions);
        }
      });
    }
    return questions;
  };

  // Count total questions
  const totalQuestions = sections.reduce(
    (acc, section) => acc + getQuestionsFromSection(section).length,
    0
  );

  // Open dialog to add new question
  const handleAddQuestion = (sectionId: string, subSectionId?: string) => {
    setTargetSection({ sectionId, subSectionId });
    setEditingQuestion(null);
    setQuestionForm(defaultQuestionForm);
    setQuestionDialogOpen(true);
  };

  // Open dialog to edit question
  const handleEditQuestion = (
    sectionId: string,
    subSectionId: string | undefined,
    questionIndex: number,
    question: Question
  ) => {
    setTargetSection({ sectionId, subSectionId });
    setEditingQuestion({ sectionId, subSectionId, questionIndex });

    // Get correct option index
    const correctOptionIndex = question.problem.options.findIndex(
      (opt) => {
        const correctAnswer = Array.isArray(question.problem.co)
          ? question.problem.co[0]
          : question.problem.co;
        return opt.prompt === String(correctAnswer) || opt.prompt === correctAnswer;
      }
    );

    setQuestionForm({
      question: question.problem.question || question.problem.value || '',
      questionHindi: question.problemHindi?.question || question.problemHindi?.value || '',
      options: question.problem.options.map((opt, idx) => ({
        text: opt.text || opt.value || '',
        correct: idx === (correctOptionIndex >= 0 ? correctOptionIndex : 0),
      })),
      solution: question.problem.solution || '',
      complexityLevel: question.complexityLevel || 'MEDIUM',
    });
    setQuestionDialogOpen(true);
  };

  // Save question (add or edit)
  const handleSaveQuestion = () => {
    if (!targetSection || !localPaper) return;

    // Validate
    if (!questionForm.question.trim()) {
      showAlert('error', 'Question text is required');
      return;
    }

    const filledOptions = questionForm.options.filter((opt) => opt.text.trim());
    if (filledOptions.length < 2) {
      showAlert('error', 'At least 2 options are required');
      return;
    }

    const correctOption = questionForm.options.find((opt) => opt.correct);
    if (!correctOption) {
      showAlert('error', 'Please select a correct answer');
      return;
    }

    // Build question object
    const correctIndex = questionForm.options.findIndex((opt) => opt.correct);
    const newQuestion: Question = {
      id: editingQuestion ? `q-${Date.now()}` : `q-${Date.now()}`,
      qNo: 0, // Will be renumbered
      problem: {
        question: questionForm.question,
        options: questionForm.options.map((opt, idx) => ({
          prompt: String.fromCharCode(65 + idx), // A, B, C, D
          text: opt.text,
          selected: false,
          correct: opt.correct,
        })),
        so: [],
        co: [String.fromCharCode(65 + correctIndex)],
        solution: questionForm.solution,
      },
      problemHindi: questionForm.questionHindi
        ? {
            question: questionForm.questionHindi,
            options: questionForm.options.map((opt, idx) => ({
              prompt: String.fromCharCode(65 + idx),
              text: opt.text,
              selected: false,
              correct: opt.correct,
            })),
            so: [],
            co: [String.fromCharCode(65 + correctIndex)],
          }
        : undefined,
      type: 'MCQ',
      questionType: 'RADIOBUTTON',
      complexityLevel: questionForm.complexityLevel,
      complexityScore: 2,
      timeTakenInSecond: 0,
    };

    // Update local paper
    const updatedSections = localPaper.pattern.sections.map((section: Section) => {
      if (section.id !== targetSection.sectionId) return section;

      if (targetSection.subSectionId) {
        // Update in subsection
        return {
          ...section,
          subSections: section.subSections?.map((sub) => {
            if (sub.id !== targetSection.subSectionId) return sub;

            const questions = [...(sub.questionData?.questions || [])];
            if (editingQuestion) {
              questions[editingQuestion.questionIndex] = newQuestion;
            } else {
              questions.push(newQuestion);
            }

            return {
              ...sub,
              questionData: { questions },
            };
          }),
        };
      } else {
        // Update in section directly
        const questions = [...(section.questionData?.questions || [])];
        if (editingQuestion) {
          questions[editingQuestion.questionIndex] = newQuestion;
        } else {
          questions.push(newQuestion);
        }

        return {
          ...section,
          questionData: { questions },
        };
      }
    });

    setLocalPaper({
      ...localPaper,
      pattern: { sections: updatedSections },
    });

    setQuestionDialogOpen(false);
    setEditingQuestion(null);
    setTargetSection(null);
    showAlert('success', editingQuestion ? 'Question updated' : 'Question added');
  };

  // Delete question
  const handleDeleteQuestion = (
    sectionId: string,
    subSectionId: string | undefined,
    questionIndex: number
  ) => {
    if (!localPaper) return;
    if (!confirm('Are you sure you want to delete this question?')) return;

    const updatedSections = localPaper.pattern.sections.map((section: Section) => {
      if (section.id !== sectionId) return section;

      if (subSectionId) {
        return {
          ...section,
          subSections: section.subSections?.map((sub) => {
            if (sub.id !== subSectionId) return sub;
            const questions = [...(sub.questionData?.questions || [])];
            questions.splice(questionIndex, 1);
            return {
              ...sub,
              questionData: { questions },
            };
          }),
        };
      } else {
        const questions = [...(section.questionData?.questions || [])];
        questions.splice(questionIndex, 1);
        return {
          ...section,
          questionData: { questions },
        };
      }
    });

    setLocalPaper({
      ...localPaper,
      pattern: { sections: updatedSections },
    });
    showAlert('success', 'Question deleted');
  };

  // Add new section
  const handleAddSection = () => {
    setSectionForm(defaultSectionForm);
    setSectionDialogOpen(true);
  };

  const handleSaveSection = () => {
    if (!sectionForm.title.trim()) {
      showAlert('error', 'Section title is required');
      return;
    }

    const newSection: Section = {
      id: `section-${Date.now()}`,
      title: sectionForm.title,
      sectionType: sectionForm.sectionType,
      subSections: [],
      questionData: { questions: [] },
      timeTakenSecond: 0,
    };

    setLocalPaper({
      ...localPaper,
      pattern: {
        sections: [...(localPaper?.pattern?.sections || []), newSection],
      },
    });

    setSectionDialogOpen(false);
    showAlert('success', 'Section added');
  };

  // Delete section
  const handleDeleteSection = (sectionId: string) => {
    if (!localPaper) return;
    const section = sections.find((s) => s.id === sectionId);
    const questionCount = section ? getQuestionsFromSection(section).length : 0;

    if (questionCount > 0) {
      if (!confirm(`This section has ${questionCount} questions. Delete anyway?`)) return;
    }

    setLocalPaper({
      ...localPaper,
      pattern: {
        sections: localPaper.pattern.sections.filter((s: Section) => s.id !== sectionId),
      },
    });
    showAlert('success', 'Section deleted');
  };

  // Save all changes to backend
  const handleSaveAll = async () => {
    if (!localPaper) return;

    // Renumber all questions
    let qNo = 1;
    const renumberedSections = localPaper.pattern.sections.map((section: Section) => {
      const sectionQuestions = section.questionData?.questions?.map((q: Question) => ({
        ...q,
        qNo: qNo++,
      })) || [];

      const subSections = section.subSections?.map((sub) => {
        const subQuestions = sub.questionData?.questions?.map((q: Question) => ({
          ...q,
          qNo: qNo++,
        })) || [];
        return {
          ...sub,
          questionData: { questions: subQuestions },
        };
      }) || [];

      return {
        ...section,
        questionData: { questions: sectionQuestions },
        subSections,
      };
    });

    // Get paper type from category
    const getPaperType = (cat: string) => {
      if (cat.startsWith('SSC')) return 'SSC';
      if (cat.startsWith('BANK')) return 'BANK';
      return 'SSC';
    };

    const paperData = {
      id: paperId,
      paperName: localPaper.paperName,
      paperType: getPaperType(category),
      paperCategory: category,
      paperSubCategory: localPaper.paperSubCategory,
      testType: testType,
      totalTime: localPaper.totalTime,
      totalScore: localPaper.totalScore,
      negativeMarks: localPaper.negativeMarks,
      perQuestionScore: localPaper.perQuestionScore,
      paperStateStatus: localPaper.status || localPaper.paperStateStatus,
      pattern: { sections: renumberedSections },
    };

    try {
      await updateMutation.mutateAsync(paperData as any);
      router.push('/admin/paper');
    } catch (error) {
      // Error handled by mutation
    }
  };

  if (isLoading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-10 w-64" />
        <Card>
          <CardContent className="pt-6 space-y-4">
            <Skeleton className="h-10 w-full" />
            <Skeleton className="h-32 w-full" />
            <Skeleton className="h-32 w-full" />
          </CardContent>
        </Card>
      </div>
    );
  }

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
            <h1 className="text-2xl font-bold text-slate-900">Manage Questions</h1>
            <p className="text-slate-600">
              {localPaper?.paperName || paperId} - {totalQuestions} questions
            </p>
          </div>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" onClick={handleAddSection}>
            <Plus className="h-4 w-4 mr-2" />
            Add Section
          </Button>
          <Button
            onClick={handleSaveAll}
            disabled={updateMutation.isPending}
            className="gap-2"
          >
            {updateMutation.isPending ? (
              <Loader2 className="h-4 w-4 animate-spin" />
            ) : (
              <Save className="h-4 w-4" />
            )}
            Save All Changes
          </Button>
        </div>
      </div>

      {/* Paper Info */}
      <Card>
        <CardHeader className="pb-2">
          <CardTitle className="flex items-center gap-2 text-base">
            <FileText className="h-5 w-5" />
            Paper Details
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
            <div>
              <span className="text-muted-foreground">Category:</span>
              <p className="font-medium">{category}</p>
            </div>
            <div>
              <span className="text-muted-foreground">Type:</span>
              <p className="font-medium">{testType}</p>
            </div>
            <div>
              <span className="text-muted-foreground">Total Score:</span>
              <p className="font-medium">{localPaper?.totalScore}</p>
            </div>
            <div>
              <span className="text-muted-foreground">Duration:</span>
              <p className="font-medium">{Math.floor((localPaper?.totalTime || 0) / 60)} min</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Sections and Questions */}
      {sections.length === 0 ? (
        <Card>
          <CardContent className="py-12 text-center">
            <BookOpen className="h-12 w-12 mx-auto mb-4 text-slate-300" />
            <p className="text-slate-500">No sections yet</p>
            <p className="text-sm text-slate-400">Add a section to start adding questions</p>
            <Button onClick={handleAddSection} className="mt-4 gap-2">
              <Plus className="h-4 w-4" />
              Add First Section
            </Button>
          </CardContent>
        </Card>
      ) : (
        <div className="space-y-4">
          {sections.map((section, sectionIndex) => {
            const sectionQuestions = getQuestionsFromSection(section);
            return (
              <Collapsible
                key={section.id || `section-${sectionIndex}`}
                defaultOpen={true}
                className="border rounded-lg"
              >
                <div className="flex items-center px-4 py-3">
                  <CollapsibleTrigger className="flex items-center gap-3 flex-1 text-left hover:bg-slate-50 rounded-md py-1 -ml-1 pl-1 transition-colors [&[data-state=open]>svg]:rotate-180">
                    <ChevronDown className="h-4 w-4 text-muted-foreground transition-transform duration-200" />
                    <Badge variant="outline" className="font-mono">
                      {sectionIndex + 1}
                    </Badge>
                    <span className="font-medium">{section.title}</span>
                    <Badge variant="secondary" className="ml-2">
                      {sectionQuestions.length} questions
                    </Badge>
                  </CollapsibleTrigger>
                  <div className="flex gap-1 ml-2">
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleAddQuestion(section.id)}
                      title="Add Question"
                    >
                      <Plus className="h-4 w-4" />
                    </Button>
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="ghost" size="sm">
                          <MoreVertical className="h-4 w-4" />
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent>
                        <DropdownMenuItem
                          onClick={() => handleDeleteSection(section.id)}
                          className="text-red-600"
                        >
                          <Trash2 className="h-4 w-4 mr-2" />
                          Delete Section
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </div>
                </div>
                <CollapsibleContent className="px-4 pb-4">
                  <div className="space-y-3">
                    {/* Direct section questions */}
                    {section.questionData?.questions?.map((question, qIndex) => (
                      <QuestionCard
                        key={question.id || `sq-${sectionIndex}-${qIndex}`}
                        question={question}
                        index={qIndex}
                        onEdit={() => handleEditQuestion(section.id, undefined, qIndex, question)}
                        onDelete={() => handleDeleteQuestion(section.id, undefined, qIndex)}
                      />
                    ))}

                    {/* Subsection questions */}
                    {section.subSections?.map((sub, subIndex) => (
                      <div key={sub.id || `${section.id}-sub-${subIndex}`} className="pl-4 border-l-2 border-slate-200">
                        <p className="text-sm font-medium text-slate-600 mb-2">{sub.title}</p>
                        {sub.questionData?.questions?.map((question, qIndex) => (
                          <QuestionCard
                            key={question.id || `q-${subIndex}-${qIndex}`}
                            question={question}
                            index={qIndex}
                            onEdit={() =>
                              handleEditQuestion(section.id, sub.id, qIndex, question)
                            }
                            onDelete={() =>
                              handleDeleteQuestion(section.id, sub.id, qIndex)
                            }
                          />
                        ))}
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleAddQuestion(section.id, sub.id)}
                          className="mt-2"
                        >
                          <Plus className="h-4 w-4 mr-1" />
                          Add Question to {sub.title}
                        </Button>
                      </div>
                    ))}

                    {sectionQuestions.length === 0 && (
                      <p className="text-sm text-muted-foreground text-center py-4">
                        No questions in this section yet
                      </p>
                    )}
                  </div>
                </CollapsibleContent>
              </Collapsible>
            );
          })}
        </div>
      )}

      {/* Question Dialog */}
      <Dialog open={questionDialogOpen} onOpenChange={setQuestionDialogOpen}>
        <DialogContent className="sm:max-w-2xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>
              {editingQuestion ? 'Edit Question' : 'Add Question'}
            </DialogTitle>
            <DialogDescription>
              Fill in the question details and select the correct answer
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label>Question Text (English) *</Label>
              <Textarea
                value={questionForm.question}
                onChange={(e) =>
                  setQuestionForm({ ...questionForm, question: e.target.value })
                }
                placeholder="Enter the question..."
                rows={3}
              />
            </div>

            <div className="space-y-2">
              <Label>Question Text (Hindi)</Label>
              <Textarea
                value={questionForm.questionHindi}
                onChange={(e) =>
                  setQuestionForm({ ...questionForm, questionHindi: e.target.value })
                }
                placeholder="Enter the question in Hindi (optional)..."
                rows={2}
              />
            </div>

            <Separator />

            <div className="space-y-3">
              <Label>Options (select correct answer)</Label>
              {questionForm.options.map((option, idx) => (
                <div key={idx} className="flex items-center gap-3">
                  <input
                    type="radio"
                    name="correctOption"
                    checked={option.correct}
                    onChange={() => {
                      setQuestionForm({
                        ...questionForm,
                        options: questionForm.options.map((opt, i) => ({
                          ...opt,
                          correct: i === idx,
                        })),
                      });
                    }}
                    className="w-4 h-4"
                  />
                  <Badge variant="outline" className="w-6 h-6 flex items-center justify-center p-0">
                    {String.fromCharCode(65 + idx)}
                  </Badge>
                  <Input
                    value={option.text}
                    onChange={(e) => {
                      const newOptions = [...questionForm.options];
                      newOptions[idx] = { ...newOptions[idx], text: e.target.value };
                      setQuestionForm({ ...questionForm, options: newOptions });
                    }}
                    placeholder={`Option ${String.fromCharCode(65 + idx)}`}
                    className="flex-1"
                  />
                  {option.correct && (
                    <CheckCircle2 className="h-5 w-5 text-green-500" />
                  )}
                </div>
              ))}
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Complexity Level</Label>
                <Select
                  value={questionForm.complexityLevel}
                  onValueChange={(value) =>
                    setQuestionForm({ ...questionForm, complexityLevel: value })
                  }
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
            </div>

            <div className="space-y-2">
              <Label>Solution/Explanation</Label>
              <Textarea
                value={questionForm.solution}
                onChange={(e) =>
                  setQuestionForm({ ...questionForm, solution: e.target.value })
                }
                placeholder="Enter the solution or explanation (optional)..."
                rows={3}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setQuestionDialogOpen(false)}>
              Cancel
            </Button>
            <Button onClick={handleSaveQuestion}>
              {editingQuestion ? 'Update Question' : 'Add Question'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Section Dialog */}
      <Dialog open={sectionDialogOpen} onOpenChange={setSectionDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add Section</DialogTitle>
            <DialogDescription>
              Create a new section to organize questions
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label>Section Title *</Label>
              <Input
                value={sectionForm.title}
                onChange={(e) =>
                  setSectionForm({ ...sectionForm, title: e.target.value })
                }
                placeholder="e.g., General Intelligence, Quantitative Aptitude"
              />
            </div>
            <div className="space-y-2">
              <Label>Section Type</Label>
              <Select
                value={sectionForm.sectionType}
                onValueChange={(value) =>
                  setSectionForm({ ...sectionForm, sectionType: value })
                }
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="GeneralIntelligence">General Intelligence & Reasoning</SelectItem>
                  <SelectItem value="QuantitativeAptitude">Quantitative Aptitude</SelectItem>
                  <SelectItem value="EnglishLanguage">English Language</SelectItem>
                  <SelectItem value="GeneralAwareness">General Awareness</SelectItem>
                  <SelectItem value="Statistics">Statistics</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setSectionDialogOpen(false)}>
              Cancel
            </Button>
            <Button onClick={handleSaveSection}>Add Section</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}

// Question Card Component
function QuestionCard({
  question,
  index,
  onEdit,
  onDelete,
}: {
  question: Question;
  index: number;
  onEdit: () => void;
  onDelete: () => void;
}) {
  const correctAnswer = Array.isArray(question.problem.co)
    ? question.problem.co[0]
    : question.problem.co;

  return (
    <Card className="bg-slate-50">
      <CardContent className="py-3 px-4">
        <div className="flex items-start gap-3">
          <div className="flex items-center gap-2 flex-shrink-0">
            <GripVertical className="h-4 w-4 text-slate-400" />
            <Badge variant="outline" className="font-mono text-xs">
              Q{question.qNo || index + 1}
            </Badge>
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm line-clamp-2">
              {question.problem.question || question.problem.value}
            </p>
            <div className="flex flex-wrap gap-1 mt-2">
              {question.problem.options.map((opt, idx) => (
                <Badge
                  key={idx}
                  variant={opt.prompt === String(correctAnswer) ? 'default' : 'outline'}
                  className="text-xs"
                >
                  {opt.prompt}: {(opt.text || opt.value || '').substring(0, 20)}
                  {(opt.text || opt.value || '').length > 20 ? '...' : ''}
                </Badge>
              ))}
            </div>
            <div className="flex gap-2 mt-2">
              <Badge variant="secondary" className="text-xs">
                {question.complexityLevel || 'MEDIUM'}
              </Badge>
            </div>
          </div>
          <div className="flex gap-1 flex-shrink-0">
            <Button variant="ghost" size="sm" onClick={onEdit}>
              <Edit className="h-4 w-4" />
            </Button>
            <Button variant="ghost" size="sm" onClick={onDelete}>
              <Trash2 className="h-4 w-4 text-red-500" />
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
