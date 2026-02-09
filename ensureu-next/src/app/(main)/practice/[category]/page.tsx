'use client';

import { useEffect, useMemo, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import { usePracticeSections, usePracticeQuestions } from '@/hooks/use-practice';
import { decrypt, encrypt } from '@/lib/utils/encryption';
import { CATEGORIES } from '@/lib/constants/api-urls';
import type { PaperCategory } from '@/types/paper';
import type { PracticeQuestion, PracticeStorage, PracticeCategoryData, PracticeStorageEntry } from '@/types/practice';

// Helper to safely get category data from PracticeStorage
function getCategoryData(storage: PracticeStorage, category: string): PracticeCategoryData | undefined {
  const data = storage[category];
  if (typeof data === 'object' && data !== null && !('editTime' in data && Object.keys(data).length === 1)) {
    return data as PracticeCategoryData;
  }
  return undefined;
}

// Helper to get saved entry for a question
function getSavedEntry(
  storage: PracticeStorage,
  category: string,
  section: string,
  subSection: string,
  questionId: string
): PracticeStorageEntry | undefined {
  const categoryData = getCategoryData(storage, category);
  return categoryData?.[section]?.[subSection]?.[questionId];
}
import {
  ArrowLeft,
  BookOpen,
  ChevronRight,
  Play,
  CheckCircle2,
  XCircle,
  Timer,
} from 'lucide-react';

const PRACTICE_KEY = 'practiceInfo';

export default function PracticeCategoryPage() {
  const params = useParams();
  const router = useRouter();
  const category = params.category as PaperCategory;

  const categoryMeta = useMemo(() => {
    for (const group of CATEGORIES) {
      const found = group.subCategories.find((sub) => sub.name === category);
      if (found) {
        return { label: found.label, rootLabel: group.label };
      }
    }
    return null;
  }, [category]);

  const { data: sectionData, isLoading: loadingSections } = usePracticeSections(category);

  const [activeSection, setActiveSection] = useState<string | null>(null);
  const [activeSubSection, setActiveSubSection] = useState<string | null>(null);
  const [savedData, setSavedData] = useState<PracticeStorage>({});
  const [questionData, setQuestionData] = useState<PracticeQuestion[]>([]);
  const [startPaper, setStartPaper] = useState(false);
  const [questionIndex, setQuestionIndex] = useState(0);
  const [questionSeconds, setQuestionSeconds] = useState(0);

  useEffect(() => {
    if (typeof window === 'undefined') return;
    const stored = localStorage.getItem(PRACTICE_KEY);
    if (stored) {
      try {
        // Use silent mode - practice data is non-critical and can be regenerated
        setSavedData(decrypt<PracticeStorage>(stored, true, true));
      } catch {
        // Clear corrupted/incompatible data silently
        localStorage.removeItem(PRACTICE_KEY);
        setSavedData({});
      }
    }
  }, []);

  const groupedSections = useMemo(() => {
    const groups: Record<string, typeof sectionData> = {};
    if (!sectionData) return [];
    sectionData.forEach((item) => {
      if (!groups[item.sectionTitle]) {
        groups[item.sectionTitle] = [];
      }
      groups[item.sectionTitle]?.push(item);
    });
    return Object.entries(groups).map(([title, items]) => ({
      title,
      items: items || [],
    }));
  }, [sectionData]);

  useEffect(() => {
    if (!groupedSections.length) return;
    if (!activeSection || !activeSubSection) {
      const firstSection = groupedSections[0];
      setActiveSection(firstSection.title);
      setActiveSubSection(firstSection.items[0]?.subSectionTitle || null);
    }
  }, [groupedSections, activeSection, activeSubSection]);

  const { data: practiceQuestions, isLoading: loadingQuestions } = usePracticeQuestions(
    category,
    activeSection || undefined,
    activeSubSection || undefined,
    !!activeSection && !!activeSubSection
  );

  // Track previous section/subsection to detect actual navigation changes
  const [prevSection, setPrevSection] = useState<string | null>(null);
  const [prevSubSection, setPrevSubSection] = useState<string | null>(null);

  useEffect(() => {
    if (!practiceQuestions || practiceQuestions.length === 0) {
      setQuestionData([]);
      return;
    }

    const questions = practiceQuestions[0].questions.map((q) => {
      const saved = getSavedEntry(savedData, category, activeSection || '', activeSubSection || '', q._id);
      return {
        ...q,
        problem: {
          ...q.problem,
          so: saved?.so || q.problem.so,
        },
      };
    });

    setQuestionData(questions);

    // Only reset practice state when section/subsection actually changes
    // (not when savedData updates from answering a question)
    const sectionChanged = activeSection !== prevSection || activeSubSection !== prevSubSection;
    if (sectionChanged) {
      setStartPaper(false);
      setQuestionIndex(0);
      setPrevSection(activeSection);
      setPrevSubSection(activeSubSection);
    }
  }, [practiceQuestions, savedData, category, activeSection, activeSubSection, prevSection, prevSubSection]);

  useEffect(() => {
    if (!startPaper) return;
    const timer = setInterval(() => {
      setQuestionSeconds((prev) => prev + 1);
    }, 1000);
    return () => clearInterval(timer);
  }, [startPaper, questionIndex]);

  const savePracticeData = (
    questionId: string,
    selectedOption: string,
    correctOption: string
  ) => {
    const categoryData = getCategoryData(savedData, category) || {};
    const sectionData = categoryData[activeSection || ''] || {};
    const subSectionData = sectionData[activeSubSection || ''] || {};

    const updated: PracticeStorage = {
      ...savedData,
      editTime: Date.now(),
      [category]: {
        ...categoryData,
        [activeSection || '']: {
          ...sectionData,
          [activeSubSection || '']: {
            ...subSectionData,
            [questionId]: {
              co: correctOption,
              so: selectedOption,
              quesTime: questionSeconds,
            },
          },
        },
      },
    };

    setSavedData(updated);
    if (typeof window !== 'undefined') {
      localStorage.setItem(PRACTICE_KEY, encrypt(updated, true));
    }
  };

  const summary = useMemo(() => {
    return groupedSections.map((section) => {
      let total = 0;
      let attempted = 0;
      let correct = 0;
      let incorrect = 0;
      let correctTime = 0;
      let incorrectTime = 0;

      section.items.forEach((sub) => {
        total += sub.questionCount;
        const categoryData = getCategoryData(savedData, category);
        const entries = categoryData?.[section.title]?.[sub.subSectionTitle] || {};
        const values = Object.values(entries);
        attempted += values.length;
        values.forEach((entry) => {
          if (entry.co === entry.so) {
            correct += 1;
            correctTime += entry.quesTime;
          } else {
            incorrect += 1;
            incorrectTime += entry.quesTime;
          }
        });
      });

      return {
        title: section.title,
        total,
        attempted,
        correct,
        incorrect,
        correctTime,
        incorrectTime,
        accuracy: attempted ? Math.round((correct / attempted) * 100) : 0,
      };
    });
  }, [groupedSections, savedData, category]);

  if (!categoryMeta) {
    return (
      <div className="container mx-auto px-4 py-16 text-center">
        <h2 className="text-2xl font-semibold text-slate-900">Category not found</h2>
        <p className="mt-2 text-slate-600">Choose a valid practice category.</p>
        <Button className="mt-6" onClick={() => router.push('/practice')}>
          Go to Practice
        </Button>
      </div>
    );
  }

  const currentQuestion = questionData[questionIndex];

  const handleOption = (option: string) => {
    if (!currentQuestion || currentQuestion.problem.so) return;
    const updated = questionData.map((q, idx) =>
      idx === questionIndex
        ? { ...q, problem: { ...q.problem, so: option } }
        : q
    );
    setQuestionData(updated);
    savePracticeData(currentQuestion._id, option, currentQuestion.problem.co);
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex items-center gap-3 text-sm text-slate-500 mb-6">
        <button
          className="inline-flex items-center gap-2 text-slate-700 hover:text-slate-900"
          onClick={() => router.push('/practice')}
        >
          <ArrowLeft className="h-4 w-4" /> Practice
        </button>
        <ChevronRight className="h-4 w-4" />
        <span>{categoryMeta.label} Practice</span>
      </div>

      <div className="grid gap-6 lg:grid-cols-[280px_1fr]">
        <aside className="space-y-4">
          <Card className="border border-slate-200">
            <CardContent className="p-4">
              <div className="flex items-center gap-2 text-slate-900 font-semibold">
                <BookOpen className="h-4 w-4" />
                {categoryMeta.label}
              </div>
              <p className="text-xs text-slate-500 mt-1">Section wise practice</p>
            </CardContent>
          </Card>

          <div className="space-y-3">
            {groupedSections.map((section) => {
              const stats = summary.find((s) => s.title === section.title);
              return (
                <Card key={section.title} className="border border-slate-200">
                  <CardContent className="p-4 space-y-3">
                    <button
                      className="flex w-full items-center justify-between text-left font-medium text-slate-800"
                      onClick={() =>
                        setActiveSection((prev) =>
                          prev === section.title ? null : section.title
                        )
                      }
                    >
                      {section.title}
                      <ChevronRight
                        className={`h-4 w-4 transition-transform ${
                          activeSection === section.title ? 'rotate-90' : ''
                        }`}
                      />
                    </button>
                    {stats && stats.attempted > 0 && (
                      <div className="space-y-2">
                        <Progress value={(stats.attempted / stats.total) * 100} />
                        <div className="text-xs text-slate-500">
                          {stats.attempted}/{stats.total} attempted • {stats.accuracy}% accuracy
                        </div>
                      </div>
                    )}
                    {activeSection === section.title && (
                      <div className="space-y-2">
                        {section.items.map((sub) => {
                          const categoryData = getCategoryData(savedData, category);
                          const attemptedCount = Object.keys(
                            categoryData?.[section.title]?.[sub.subSectionTitle] || {}
                          ).length;
                          return (
                            <button
                              key={sub.subSectionTitle}
                              onClick={() => {
                                setActiveSubSection(sub.subSectionTitle);
                                setStartPaper(false);
                                setQuestionIndex(0);
                                setQuestionSeconds(0);
                              }}
                              className={`w-full rounded-lg border px-3 py-2 text-left text-sm transition ${
                                activeSubSection === sub.subSectionTitle
                                  ? 'border-slate-900 bg-slate-900 text-white'
                                  : 'border-slate-200 hover:border-slate-300'
                              }`}
                            >
                              <div className="font-medium">{sub.subSectionTitle}</div>
                              <div className="text-xs opacity-80">
                                {attemptedCount} / {sub.questionCount} questions
                              </div>
                            </button>
                          );
                        })}
                      </div>
                    )}
                  </CardContent>
                </Card>
              );
            })}
          </div>
        </aside>

        <section className="space-y-6">
          <Card className="border border-slate-200">
            <CardContent className="p-6">
              <div className="flex flex-wrap items-center justify-between gap-4">
                <div>
                  <h2 className="text-2xl font-semibold text-slate-900">
                    {activeSubSection || 'Select a section'}
                  </h2>
                  <p className="text-sm text-slate-500">
                    {activeSection} • {categoryMeta.label}
                  </p>
                </div>
                <Button
                  onClick={() => {
                    setStartPaper(true);
                    setQuestionSeconds(0);
                  }}
                  disabled={!questionData.length}
                  className="gap-2"
                >
                  <Play className="h-4 w-4" />
                  Start Practice
                </Button>
              </div>
            </CardContent>
          </Card>

          {loadingSections || loadingQuestions ? (
            <Card className="border border-slate-200">
              <CardContent className="p-6 text-slate-500">
                Loading practice questions...
              </CardContent>
            </Card>
          ) : !activeSubSection ? (
            <Card className="border border-slate-200">
              <CardContent className="p-6 text-slate-500">
                Select a sub-section to start practicing.
              </CardContent>
            </Card>
          ) : (
            <div className="space-y-6">
              {!startPaper && (
                <Card className="border border-slate-200">
                  <CardContent className="p-6 space-y-4">
                    <h3 className="text-lg font-semibold text-slate-900">
                      Section Overview
                    </h3>
                    <div className="grid gap-4 md:grid-cols-2">
                      {summary
                        .filter((s) => s.title === activeSection)
                        .map((section) => (
                          <div
                            key={section.title}
                            className="rounded-xl border border-slate-200 bg-slate-50 p-4"
                          >
                            <div className="text-sm font-medium text-slate-700">
                              {section.title}
                            </div>
                            <div className="mt-2 text-sm text-slate-600">
                              {section.attempted} of {section.total} attempted
                            </div>
                            <div className="mt-3">
                              <Progress value={section.accuracy} />
                              <div className="mt-1 text-xs text-slate-500">
                                {section.accuracy}% accuracy
                              </div>
                            </div>
                          </div>
                        ))}
                    </div>
                    <div className="text-sm text-slate-500">
                      Click “Start Practice” to attempt the questions in this sub-section.
                    </div>
                  </CardContent>
                </Card>
              )}

              {startPaper && currentQuestion && (
                <Card className="border border-slate-200">
                  <CardContent className="p-6 space-y-6">
                    <div className="flex flex-wrap items-center justify-between gap-3">
                      <div className="text-sm text-slate-500">
                        Question {questionIndex + 1} of {questionData.length}
                      </div>
                      <div className="flex items-center gap-2 text-sm text-slate-500">
                        <Timer className="h-4 w-4" />
                        {Math.floor(questionSeconds / 60)}m {questionSeconds % 60}s
                      </div>
                    </div>

                    <div
                      className="rounded-xl bg-slate-50 p-4 text-slate-800"
                      dangerouslySetInnerHTML={{ __html: currentQuestion.problem.value }}
                    />

                    <div className="grid gap-3">
                      {currentQuestion.problem.options.map((option) => {
                        const isSelected = currentQuestion.problem.so === option.prompt;
                        const isCorrect = currentQuestion.problem.co === option.prompt;
                        const showResult = !!currentQuestion.problem.so;

                        return (
                          <button
                            key={option.prompt}
                            onClick={() => handleOption(option.prompt)}
                            className={`rounded-xl border px-4 py-3 text-left transition ${
                              showResult
                                ? isCorrect
                                  ? 'border-emerald-500 bg-emerald-50'
                                  : isSelected
                                    ? 'border-rose-500 bg-rose-50'
                                    : 'border-slate-200'
                                : isSelected
                                  ? 'border-slate-900 bg-slate-900 text-white'
                                  : 'border-slate-200 hover:border-slate-300'
                            }`}
                          >
                            <div className="flex items-start gap-3">
                              <span className="mt-1 font-semibold">{option.prompt}.</span>
                              <span dangerouslySetInnerHTML={{ __html: option.value }} />
                            </div>
                          </button>
                        );
                      })}
                    </div>

                    {currentQuestion.problem.so && (
                      <div className="rounded-xl border border-slate-200 bg-white p-4">
                        <div className="flex items-center gap-2 text-sm font-medium">
                          {currentQuestion.problem.so === currentQuestion.problem.co ? (
                            <>
                              <CheckCircle2 className="h-4 w-4 text-emerald-600" />
                              <span className="text-emerald-700">Correct answer</span>
                            </>
                          ) : (
                            <>
                              <XCircle className="h-4 w-4 text-rose-600" />
                              <span className="text-rose-700">Incorrect answer</span>
                            </>
                          )}
                        </div>
                        <div className="mt-2 text-sm text-slate-600">
                          Correct option: {currentQuestion.problem.co}
                        </div>
                        {currentQuestion.problem.solutions?.[0]?.value && (
                          <div
                            className="mt-3 text-sm text-slate-600"
                            dangerouslySetInnerHTML={{
                              __html: currentQuestion.problem.solutions[0].value,
                            }}
                          />
                        )}
                      </div>
                    )}

                    <div className="flex flex-wrap items-center justify-between gap-3">
                      <Button
                        variant="outline"
                        onClick={() =>
                          setQuestionIndex((prev) => Math.max(prev - 1, 0))
                        }
                        disabled={questionIndex === 0}
                      >
                        Previous
                      </Button>
                      <Button
                        onClick={() =>
                          setQuestionIndex((prev) =>
                            Math.min(prev + 1, questionData.length - 1)
                          )
                        }
                        disabled={questionIndex === questionData.length - 1}
                      >
                        Next
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              )}
            </div>
          )}
        </section>
      </div>
    </div>
  );
}

