'use client';

import { cn } from '@/lib/utils';
import { useExamStore } from '@/stores/exam-store';

export function SectionTabs() {
  const paperData = useExamStore((state) => state.paperData);
  const currentSectionIndex = useExamStore((state) => state.currentSectionIndex);
  const setCurrentSection = useExamStore((state) => state.setCurrentSection);

  if (!paperData) return null;

  const sections = paperData.paper.pattern.sections;

  return (
    <div className="flex gap-1 bg-slate-100 p-1 rounded-lg overflow-x-auto">
      {sections.map((section, index) => (
        <button
          key={section.id || `section-${index}`}
          onClick={() => setCurrentSection(index)}
          className={cn(
            'px-4 py-2 rounded-md text-sm font-medium whitespace-nowrap transition-colors',
            currentSectionIndex === index
              ? 'bg-white text-primary shadow-sm'
              : 'text-slate-600 hover:text-slate-900 hover:bg-white/50'
          )}
        >
          {section.title}
        </button>
      ))}
    </div>
  );
}
