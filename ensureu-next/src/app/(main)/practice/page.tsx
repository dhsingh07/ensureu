'use client';

import { Suspense, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { CATEGORIES } from '@/lib/constants/api-urls';
import { BookOpen, Sparkles, ChartLine, Infinity, ArrowRight } from 'lucide-react';

const categoryCards = [
  {
    code: 'SSC_CGL',
    title: 'SSC CGL',
    description: 'Graduate level mock practice for Tier 1 & 2.',
    accent: 'from-emerald-500/15 via-emerald-400/10 to-emerald-300/5',
    glow: 'shadow-emerald-500/20',
  },
  {
    code: 'SSC_CPO',
    title: 'SSC CPO',
    description: 'Master reasoning & awareness with targeted drills.',
    accent: 'from-sky-500/15 via-sky-400/10 to-sky-300/5',
    glow: 'shadow-sky-500/20',
  },
  {
    code: 'SSC_CHSL',
    title: 'SSC CHSL',
    description: 'Refine speed & accuracy with curated CHSL sets.',
    accent: 'from-amber-500/15 via-amber-400/10 to-amber-300/5',
    glow: 'shadow-amber-500/20',
  },
  {
    code: 'BANK_PO',
    title: 'Bank PO',
    description: 'Practice sections mapped to PO prelims & mains.',
    accent: 'from-rose-500/15 via-rose-400/10 to-rose-300/5',
    glow: 'shadow-rose-500/20',
  },
];

// Separate component that uses useSearchParams
function QuickCategoryRedirect() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const quickCategory = searchParams.get('category');

  useEffect(() => {
    if (quickCategory) {
      router.replace(`/practice/${quickCategory}`);
    }
  }, [quickCategory, router]);

  return null;
}

export default function PracticeLandingPage() {
  const router = useRouter();

  return (
    <div className="relative overflow-hidden">
      <Suspense fallback={null}>
        <QuickCategoryRedirect />
      </Suspense>
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_top,_rgba(14,116,144,0.12),_transparent_55%)]" />
      <div className="absolute -top-32 right-10 h-72 w-72 rounded-full bg-emerald-200/40 blur-3xl" />
      <div className="absolute top-40 -left-12 h-64 w-64 rounded-full bg-sky-200/40 blur-3xl" />

      <section className="relative container mx-auto px-4 pt-16 pb-12">
        <div className="mx-auto max-w-3xl text-center space-y-6">
          <span className="inline-flex items-center gap-2 rounded-full bg-slate-900 text-white px-4 py-2 text-sm tracking-wide">
            <Sparkles className="h-4 w-4" />
            Free Practice Mode
          </span>
          <h1 className="text-4xl md:text-5xl font-bold text-slate-900 leading-tight">
            Build exam stamina with focused, free practice.
          </h1>
          <p className="text-base md:text-lg text-slate-600">
            Choose a category, drill section-wise questions, and track accuracy across
            every attempt. All practice sets are free and updated continuously.
          </p>
          <div className="flex flex-wrap items-center justify-center gap-4 text-sm text-slate-600">
            <div className="flex items-center gap-2">
              <BookOpen className="h-4 w-4 text-emerald-600" />
              10,000+ questions
            </div>
            <div className="flex items-center gap-2">
              <ChartLine className="h-4 w-4 text-sky-600" />
              Instant accuracy tracking
            </div>
            <div className="flex items-center gap-2">
              <Infinity className="h-4 w-4 text-amber-600" />
              Unlimited attempts
            </div>
          </div>
        </div>
      </section>

      <section className="relative container mx-auto px-4 pb-16">
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
          {categoryCards.map((card) => (
            <Card
              key={card.code}
              className={`group h-full cursor-pointer border border-slate-200 bg-gradient-to-br ${card.accent} p-5 transition-all hover:-translate-y-1 hover:shadow-xl ${card.glow}`}
              onClick={() => router.push(`/practice/${card.code}`)}
            >
              <div className="flex h-full flex-col gap-4">
                <div className="h-12 w-12 rounded-2xl bg-white/80 flex items-center justify-center text-lg font-semibold">
                  {card.title.split(' ')[0]}
                </div>
                <div className="space-y-2">
                  <h3 className="text-lg font-semibold text-slate-900">{card.title}</h3>
                  <p className="text-sm text-slate-600">{card.description}</p>
                </div>
                <div className="mt-auto flex items-center text-sm text-slate-700 font-medium">
                  Start practice
                  <ArrowRight className="ml-2 h-4 w-4 transition-transform group-hover:translate-x-1" />
                </div>
              </div>
            </Card>
          ))}
        </div>
      </section>

      <section className="relative container mx-auto px-4 pb-20">
        <div className="grid gap-6 md:grid-cols-3">
          {[
            {
              title: 'Expert-crafted questions',
              description: 'Practice with questions aligned to recent exam patterns.',
            },
            {
              title: 'Detailed solutions',
              description: 'Review explanations immediately after each response.',
            },
            {
              title: 'Progress summaries',
              description: 'See section-wise accuracy and pace at a glance.',
            },
          ].map((feature) => (
            <div
              key={feature.title}
              className="rounded-2xl border border-slate-200 bg-white/90 p-6 shadow-sm"
            >
              <h4 className="text-lg font-semibold text-slate-900">{feature.title}</h4>
              <p className="mt-2 text-sm text-slate-600">{feature.description}</p>
            </div>
          ))}
        </div>
        <div className="mt-10 flex justify-center">
          <Button
            size="lg"
            className="rounded-full px-8"
            onClick={() => router.push(`/practice/${CATEGORIES[0].subCategories[0].name}`)}
          >
            Start Practicing Now
          </Button>
        </div>
      </section>
    </div>
  );
}

