import Link from 'next/link';
import { Sparkles } from 'lucide-react';

export function Footer() {
  return (
    <footer className="border-t bg-slate-50">
      <div className="container mx-auto px-4 py-8">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Brand - Icon goes to landing, Text goes to dashboard */}
          <div className="space-y-4">
            <div className="flex items-center gap-2">
              <Link href="/">
                <div className="w-8 h-8 bg-gradient-to-br from-teal-500 to-cyan-600 rounded-lg flex items-center justify-center hover:opacity-90 transition-opacity cursor-pointer">
                  <Sparkles className="h-5 w-5 text-white" />
                </div>
              </Link>
              <Link href="/home">
                <span className="text-xl font-bold text-slate-900 hover:text-teal-600 transition-colors cursor-pointer">EnsureU</span>
              </Link>
            </div>
            <p className="text-sm text-slate-600">
              AI-powered exam preparation platform. Practice smart, learn faster,
              and succeed with confidence.
            </p>
          </div>

          {/* Quick Links */}
          <div>
            <h3 className="font-semibold text-slate-900 mb-4">Quick Links</h3>
            <ul className="space-y-2 text-sm">
              <li>
                <Link
                  href="/home"
                  className="text-slate-600 hover:text-primary transition-colors"
                >
                  Home
                </Link>
              </li>
              <li>
                <Link
                  href="/practice"
                  className="text-slate-600 hover:text-primary transition-colors"
                >
                  Practice Tests
                </Link>
              </li>
              <li>
                <Link
                  href="/previous-papers"
                  className="text-slate-600 hover:text-primary transition-colors"
                >
                  Previous Papers
                </Link>
              </li>
              <li>
                <Link
                  href="/blog"
                  className="text-slate-600 hover:text-primary transition-colors"
                >
                  Blog
                </Link>
              </li>
            </ul>
          </div>

          {/* Exam Categories */}
          <div>
            <h3 className="font-semibold text-slate-900 mb-4">
              Exam Categories
            </h3>
            <ul className="space-y-2 text-sm">
              <li>
                <Link
                  href="/practice?category=SSC_CGL"
                  className="text-slate-600 hover:text-primary transition-colors"
                >
                  SSC CGL
                </Link>
              </li>
              <li>
                <Link
                  href="/practice?category=SSC_CHSL"
                  className="text-slate-600 hover:text-primary transition-colors"
                >
                  SSC CHSL
                </Link>
              </li>
              <li>
                <Link
                  href="/practice?category=SSC_CPO"
                  className="text-slate-600 hover:text-primary transition-colors"
                >
                  SSC CPO
                </Link>
              </li>
              <li>
                <Link
                  href="/practice?category=BANK_PO"
                  className="text-slate-600 hover:text-primary transition-colors"
                >
                  Bank PO
                </Link>
              </li>
            </ul>
          </div>

          {/* Support */}
          <div>
            <h3 className="font-semibold text-slate-900 mb-4">Support</h3>
            <ul className="space-y-2 text-sm">
              <li>
                <Link
                  href="/contact"
                  className="text-slate-600 hover:text-primary transition-colors"
                >
                  Contact Us
                </Link>
              </li>
              <li>
                <Link
                  href="/instruction"
                  className="text-slate-600 hover:text-primary transition-colors"
                >
                  Instructions
                </Link>
              </li>
              <li>
                <Link
                  href="/links"
                  className="text-slate-600 hover:text-primary transition-colors"
                >
                  Important Links
                </Link>
              </li>
            </ul>
          </div>
        </div>

        <div className="border-t mt-8 pt-8 text-center text-sm text-slate-500">
          <p>&copy; {new Date().getFullYear()} EnsureU. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
}
