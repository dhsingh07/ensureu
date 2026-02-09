'use client';

import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { ExternalLink } from 'lucide-react';

const linkList = [
  {
    type: 'SSC',
    links: [
      { label: 'SSC Admit Card', url: 'https://ssc.nic.in/Portal/AdmitCard' },
      { label: 'Apply', url: 'https://ssc.nic.in/Portal/Apply' },
      { label: 'SSC Exam Results', url: 'https://ssc.nic.in/Portal/Results' },
      { label: 'SSC Notices', url: 'https://ssc.nic.in/Portal/Notices' },
      { label: 'SSC Registration', url: 'https://ssc.nic.in/Registration/Home' },
    ],
  },
];

export default function LinksPage() {
  return (
    <div className="container mx-auto px-4 py-10 space-y-6">
      <div className="text-center space-y-2">
        <h1 className="text-3xl font-bold text-slate-900">Important Links</h1>
        <p className="text-slate-600">Official resources for exam updates.</p>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        {linkList.map((group) => (
          <Card key={group.type} className="border border-slate-200">
            <CardContent className="p-6 space-y-4">
              <div className="flex items-center justify-between">
                <h2 className="text-lg font-semibold text-slate-900">
                  {group.type} Links
                </h2>
                <Badge variant="secondary">{group.links.length} links</Badge>
              </div>
              <div className="space-y-3">
                {group.links.map((link) => (
                  <a
                    key={link.url}
                    href={link.url}
                    target="_blank"
                    rel="noreferrer"
                    className="flex items-center justify-between rounded-lg border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700 transition hover:border-slate-300"
                  >
                    <span>{link.label}</span>
                    <ExternalLink className="h-4 w-4 text-slate-400" />
                  </a>
                ))}
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}

