import Link from 'next/link';
import { Sparkles } from 'lucide-react';
import type { ReactNode } from 'react';

export default function AuthLayout({ children }: { children: ReactNode }) {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50/30 to-purple-50/20 flex flex-col">
      {/* Header */}
      <header className="py-4">
        <div className="container mx-auto px-4">
          <div className="flex items-center gap-2 w-fit">
            <Link href="/">
              <div className="w-8 h-8 bg-gradient-to-br from-teal-500 to-cyan-600 rounded-lg flex items-center justify-center hover:opacity-90 transition-opacity cursor-pointer">
                <Sparkles className="h-5 w-5 text-white" />
              </div>
            </Link>
            <Link href="/home">
              <span className="text-xl font-bold text-slate-900 hover:text-teal-600 transition-colors cursor-pointer">EnsureU</span>
            </Link>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 flex items-center justify-center p-4">
        {children}
      </main>

      {/* Footer */}
      <footer className="py-4 text-center text-sm text-slate-500">
        <p>&copy; 2026 GrayscaleLabs AI Pvt Ltd. All rights reserved.</p>
      </footer>
    </div>
  );
}
