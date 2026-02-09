import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import './globals.css';
import { QueryProvider } from '@/providers/query-provider';
import { AuthProvider } from '@/providers/auth-provider';
import { ToastProvider } from '@/providers/toast-provider';
import { GoogleProvider } from '@/providers/google-provider';

const inter = Inter({
  subsets: ['latin'],
  variable: '--font-inter',
});

export const metadata: Metadata = {
  title: 'EnsureU - Assessment Platform',
  description: 'Practice and prepare for SSC, Bank, and other competitive exams',
  keywords: ['SSC', 'Bank PO', 'exam preparation', 'mock tests', 'online tests'],
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className={`${inter.variable} font-sans antialiased`}>
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
