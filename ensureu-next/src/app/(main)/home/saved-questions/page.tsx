'use client';

import { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Bookmark, ArrowLeft } from 'lucide-react';
import Link from 'next/link';
import { useAuthStore } from '@/stores/auth-store';

interface SavedQuestion {
  id: string;
  questionText: string;
  paperName: string;
  savedAt: string;
}

export default function SavedQuestionsPage() {
  const [savedQuestions, setSavedQuestions] = useState<SavedQuestion[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const { isAuthenticated } = useAuthStore();

  useEffect(() => {
    // TODO: Fetch saved questions from API
    // For now, show empty state
    setIsLoading(false);
    setSavedQuestions([]);
  }, []);

  if (!isAuthenticated) {
    return (
      <div className="container mx-auto px-4 py-8">
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-16">
            <Bookmark className="h-16 w-16 text-slate-300 mb-4" />
            <h2 className="text-xl font-semibold text-slate-700 mb-2">
              Login Required
            </h2>
            <p className="text-slate-500 mb-6 text-center">
              Please login to view your saved questions.
            </p>
            <Link href="/login">
              <Button>Login</Button>
            </Link>
          </CardContent>
        </Card>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="animate-pulse space-y-4">
          <div className="h-8 bg-slate-200 rounded w-1/4"></div>
          <div className="h-32 bg-slate-200 rounded"></div>
          <div className="h-32 bg-slate-200 rounded"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Header */}
      <div className="flex items-center gap-4 mb-6">
        <Link href="/home">
          <Button variant="ghost" size="icon">
            <ArrowLeft className="h-5 w-5" />
          </Button>
        </Link>
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Saved Questions</h1>
          <p className="text-slate-500">
            Questions you&apos;ve bookmarked for later review
          </p>
        </div>
      </div>

      {/* Content */}
      {savedQuestions.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-16">
            <Bookmark className="h-16 w-16 text-slate-300 mb-4" />
            <h2 className="text-xl font-semibold text-slate-700 mb-2">
              No Saved Questions
            </h2>
            <p className="text-slate-500 mb-6 text-center max-w-md">
              You haven&apos;t saved any questions yet. While taking a test, click
              the bookmark icon to save questions for later review.
            </p>
            <Link href="/home">
              <Button>Start Practicing</Button>
            </Link>
          </CardContent>
        </Card>
      ) : (
        <div className="space-y-4">
          {savedQuestions.map((question) => (
            <Card key={question.id}>
              <CardHeader>
                <CardTitle className="text-lg">{question.paperName}</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-slate-700">{question.questionText}</p>
                <p className="text-sm text-slate-400 mt-2">
                  Saved on {new Date(question.savedAt).toLocaleDateString()}
                </p>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
