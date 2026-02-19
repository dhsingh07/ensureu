import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import './globals.css';
import { QueryProvider } from '@/providers/query-provider';
import { AuthProvider } from '@/providers/auth-provider';
import { ToastProvider } from '@/providers/toast-provider';
import { GoogleProvider } from '@/providers/google-provider';
import {
  GoogleAnalytics,
  GoogleTagManager,
  GoogleTagManagerNoScript,
} from '@/components/analytics/google-analytics';
import {
  OrganizationStructuredData,
  WebsiteStructuredData,
  EducationalOrganizationStructuredData,
} from '@/components/analytics/structured-data';

const inter = Inter({
  subsets: ['latin'],
  variable: '--font-inter',
});

const siteUrl = process.env.NEXT_PUBLIC_SITE_URL || 'https://ensureu.com';

export const metadata: Metadata = {
  metadataBase: new URL(siteUrl),
  title: {
    default: 'EnsureU - Assessment Platform for SSC & Bank Exams',
    template: '%s | EnsureU',
  },
  description:
    'Practice and prepare for SSC CGL, SSC CPO, SSC CHSL, Bank PO and other competitive exams with mock tests, previous year papers, and detailed analytics.',
  keywords: [
    'SSC CGL',
    'SSC CPO',
    'SSC CHSL',
    'Bank PO',
    'exam preparation',
    'mock tests',
    'online tests',
    'previous year papers',
    'competitive exams',
    'government exams',
    'free mock tests',
  ],
  authors: [{ name: 'GrayscaleLabs AI Pvt Ltd' }],
  creator: 'GrayscaleLabs AI Pvt Ltd',
  publisher: 'GrayscaleLabs AI Pvt Ltd',
  formatDetection: {
    email: false,
    address: false,
    telephone: false,
  },
  openGraph: {
    type: 'website',
    locale: 'en_IN',
    url: siteUrl,
    siteName: 'EnsureU',
    title: 'EnsureU - Assessment Platform for SSC & Bank Exams',
    description:
      'Practice and prepare for SSC CGL, SSC CPO, SSC CHSL, Bank PO and other competitive exams with mock tests and detailed analytics.',
  },
  twitter: {
    card: 'summary_large_image',
    title: 'EnsureU - Assessment Platform for SSC & Bank Exams',
    description:
      'Practice and prepare for SSC CGL, SSC CPO, SSC CHSL, Bank PO and other competitive exams.',
  },
  robots: {
    index: true,
    follow: true,
    googleBot: {
      index: true,
      follow: true,
      'max-video-preview': -1,
      'max-image-preview': 'large',
      'max-snippet': -1,
    },
  },
  verification: {
    google: process.env.NEXT_PUBLIC_GOOGLE_SITE_VERIFICATION,
  },
  alternates: {
    canonical: siteUrl,
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <head>
        <OrganizationStructuredData />
        <WebsiteStructuredData />
        <EducationalOrganizationStructuredData />
      </head>
      <body className={`${inter.variable} font-sans antialiased`}>
        <GoogleTagManagerNoScript />
        <GoogleTagManager />
        <GoogleAnalytics />
        <QueryProvider>
          <GoogleProvider>
            <AuthProvider>
              {children}
              <ToastProvider />
            </AuthProvider>
          </GoogleProvider>
        </QueryProvider>
      </body>
    </html>
  );
}
