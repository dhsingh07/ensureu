'use client';

import { Card, CardContent } from '@/components/ui/card';

const sections = [
  {
    id: 'ssc-cgl',
    title: 'SSC CGL - Combined Graduate Level Examination',
    intro:
      'Staff Selection Commission - Combined Graduate Level Examination (SSC CGL) recruits staff to Group B and Group C posts in ministries and departments.',
    details: [
      { label: 'Conducting Body', value: 'Staff Selection Commission (SSC)' },
      { label: 'Status', value: 'Active - Conducted annually' },
      {
        label: 'Posts',
        value:
          'Income Tax Inspector, Auditor, Accountant, SI in CBI, Assistant in MEA, Inspector, Tax Assistant, UDC, and more.',
      },
      {
        label: 'Eligibility',
        value:
          'Nationality: Indian citizen. Age: 18-32 years (post-dependent). Education: Bachelor’s degree.',
      },
      {
        label: 'Exam Tiers',
        value: 'Tier 1 and Tier 2 - Computer based objective exams.',
      },
    ],
    pattern: [
      { label: 'General Intelligence and Reasoning', questions: 25, marks: 50 },
      { label: 'General Awareness', questions: 25, marks: 50 },
      { label: 'Quantitative Aptitude', questions: 25, marks: 50 },
      { label: 'English Comprehension', questions: 25, marks: 50 },
    ],
    note: 'Duration: 60 minutes | Negative marking: 0.50 marks per wrong answer',
  },
  {
    id: 'ssc-chsl',
    title: 'SSC CHSL - Combined Higher Secondary Level Examination',
    intro:
      'SSC CHSL recruits Lower Divisional Clerks, Postal Assistants, Sorting Assistants, and Data Entry Operators.',
    details: [
      { label: 'Conducting Body', value: 'Staff Selection Commission (SSC)' },
      { label: 'Status', value: 'Active - Conducted annually' },
      {
        label: 'Posts',
        value: 'LDC/JSA, PA/SA, Data Entry Operator.',
      },
      {
        label: 'Eligibility',
        value:
          'Nationality: Indian citizen. Age: 18-27 years. Education: 12th pass.',
      },
      {
        label: 'Exam Tiers',
        value: 'Tier 1 (Objective) and Tier 2 (Objective + Descriptive).',
      },
    ],
    pattern: [
      { label: 'General Intelligence', questions: 25, marks: 50 },
      { label: 'General Awareness', questions: 25, marks: 50 },
      { label: 'Quantitative Aptitude', questions: 25, marks: 50 },
      { label: 'English Language', questions: 25, marks: 50 },
    ],
    note:
      'Duration: 60 minutes | Negative marking: 0.50 marks per wrong answer',
  },
  {
    id: 'ssc-cpo',
    title: 'SSC CPO - Central Police Organisation Examination',
    intro:
      'SSC CPO recruits Sub-Inspectors in Delhi Police and CAPFs, and Assistant Sub-Inspectors in CISF.',
    details: [
      { label: 'Conducting Body', value: 'Staff Selection Commission (SSC)' },
      { label: 'Status', value: 'Active - Conducted annually' },
      {
        label: 'Posts',
        value:
          'SI in Delhi Police/CAPFs, ASI in CISF.',
      },
      {
        label: 'Eligibility',
        value:
          'Nationality: Indian citizen. Age: 20-25 years. Education: Bachelor’s degree.',
      },
      {
        label: 'Selection Stages',
        value:
          'Tier 1, PST/PET, Tier 2, Medical Examination.',
      },
    ],
    pattern: [
      { label: 'General Intelligence and Reasoning', questions: 50, marks: 50 },
      { label: 'General Knowledge and Awareness', questions: 50, marks: 50 },
      { label: 'Quantitative Aptitude', questions: 50, marks: 50 },
      { label: 'English Comprehension', questions: 50, marks: 50 },
    ],
    note: 'Duration: 2 hours | Negative marking: 0.25 marks per wrong answer',
  },
];

export default function InformationPage() {
  return (
    <div className="container mx-auto px-4 py-10 space-y-10">
      <div className="text-center space-y-3">
        <h1 className="text-3xl md:text-4xl font-bold text-slate-900">
          Exam Information
        </h1>
        <p className="text-slate-600">
          Understand eligibility, exam structure, and patterns for popular exams.
        </p>
      </div>

      {sections.map((section) => (
        <Card key={section.id} className="border border-slate-200">
          <CardContent className="p-6 md:p-8 space-y-6">
            <div>
              <h2 className="text-2xl font-semibold text-slate-900">
                {section.title}
              </h2>
              <p className="mt-2 text-sm text-slate-600">{section.intro}</p>
            </div>

            <div className="grid gap-4 md:grid-cols-2">
              {section.details.map((detail) => (
                <div
                  key={detail.label}
                  className="rounded-xl border border-slate-200 bg-slate-50 p-4"
                >
                  <div className="text-xs uppercase tracking-wide text-slate-500">
                    {detail.label}
                  </div>
                  <div className="mt-1 text-sm text-slate-700">{detail.value}</div>
                </div>
              ))}
            </div>

            <div className="rounded-xl border border-slate-200 overflow-hidden">
              <div className="bg-slate-900 text-white px-4 py-3 text-sm font-semibold">
                Tier 1 - Exam Pattern
              </div>
              <div className="divide-y divide-slate-200">
                {section.pattern.map((row) => (
                  <div
                    key={row.label}
                    className="grid grid-cols-[1fr_100px_80px] gap-2 px-4 py-3 text-sm text-slate-700"
                  >
                    <span>{row.label}</span>
                    <span className="text-right">{row.questions}</span>
                    <span className="text-right">{row.marks}</span>
                  </div>
                ))}
              </div>
            </div>

            <p className="text-xs text-slate-500">{section.note}</p>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}

