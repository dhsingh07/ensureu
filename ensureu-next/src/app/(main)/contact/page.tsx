'use client';

import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import {
  MapPin,
  Phone,
  Mail,
  CheckCircle2,
  Sparkles,
  ArrowRight,
} from 'lucide-react';

export default function ContactPage() {
  return (
    <div className="relative overflow-hidden">
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_top,_rgba(14,116,144,0.12),_transparent_55%)]" />
      <div className="absolute -top-24 right-10 h-64 w-64 rounded-full bg-emerald-200/40 blur-3xl" />
      <div className="absolute top-40 -left-10 h-72 w-72 rounded-full bg-sky-200/40 blur-3xl" />

      <section className="relative container mx-auto px-4 pt-16 pb-12">
        <div className="mx-auto max-w-3xl text-center space-y-4">
          <span className="inline-flex items-center gap-2 rounded-full bg-slate-900 text-white px-4 py-2 text-xs uppercase tracking-widest">
            <Sparkles className="h-4 w-4" />
            Get in Touch
          </span>
          <h1 className="text-4xl md:text-5xl font-bold text-slate-900">
            We&apos;d love to hear from you
          </h1>
          <p className="text-base md:text-lg text-slate-600">
            Have questions about preparation, subscriptions, or technical support? We&apos;re
            here to help.
          </p>
        </div>
      </section>

      <section className="relative container mx-auto px-4 pb-16">
        <div className="grid gap-6 md:grid-cols-3">
          {[
            {
              icon: MapPin,
              title: 'Visit our office',
              detail:
                '450, Delhi - Jaipur Expy, Phase V, Udyog Vihar, Sector 19, Gurugram, Haryana 122022',
            },
            {
              icon: Phone,
              title: 'Call us',
              detail: '+91-9430566698 (Mon - Sat, 9:00 AM - 6:00 PM)',
            },
            {
              icon: Mail,
              title: 'Email us',
              detail: 'support@ensureu.com (Replies within 24 hours)',
            },
          ].map((item) => (
            <Card key={item.title} className="border border-slate-200">
              <CardContent className="p-6 space-y-3">
                <div className="h-12 w-12 rounded-2xl bg-slate-900 text-white flex items-center justify-center">
                  <item.icon className="h-5 w-5" />
                </div>
                <h3 className="text-lg font-semibold text-slate-900">{item.title}</h3>
                <p className="text-sm text-slate-600">{item.detail}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      </section>

      <section className="relative container mx-auto px-4 pb-20">
        <div className="grid gap-8 lg:grid-cols-[1fr_1.1fr]">
          <Card className="border border-slate-200">
            <CardContent className="p-6 md:p-8 space-y-5">
              <h2 className="text-2xl font-semibold text-slate-900">
                Get started with your learning journey
              </h2>
              <p className="text-sm text-slate-600">
                Whether you&apos;re looking for course details or need technical support,
                we&apos;re ready to assist.
              </p>
              <div className="space-y-4">
                {[
                  {
                    title: 'Quick response',
                    desc: 'We reply within 24 hours to all inquiries.',
                  },
                  {
                    title: 'Expert support',
                    desc: 'Reach our education team for exam guidance.',
                  },
                  {
                    title: 'Personalized help',
                    desc: 'Get tailored recommendations for your goals.',
                  },
                ].map((item) => (
                  <div key={item.title} className="flex items-start gap-3">
                    <CheckCircle2 className="h-5 w-5 text-emerald-600 mt-1" />
                    <div>
                      <h4 className="text-sm font-semibold text-slate-900">
                        {item.title}
                      </h4>
                      <p className="text-sm text-slate-600">{item.desc}</p>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          <Card className="border border-slate-200">
            <CardContent className="p-6 md:p-8 space-y-5">
              <h3 className="text-xl font-semibold text-slate-900">Send us a message</h3>
              <div className="grid gap-4 md:grid-cols-2">
                <Input placeholder="First name" />
                <Input placeholder="Last name" />
              </div>
              <div className="grid gap-4 md:grid-cols-2">
                <Input placeholder="Email address" type="email" />
                <Input placeholder="Phone number" />
              </div>
              <Input placeholder="Subject" />
              <Textarea rows={4} placeholder="Tell us how we can help..." />
              <Button className="gap-2">
                Send message <ArrowRight className="h-4 w-4" />
              </Button>
            </CardContent>
          </Card>
        </div>
      </section>
    </div>
  );
}

