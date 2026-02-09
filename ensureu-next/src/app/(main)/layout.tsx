'use client';

import { useState, useEffect } from 'react';
import { Sidebar } from '@/components/layout/sidebar';
import { SidebarHeader } from '@/components/layout/sidebar-header';
import { Footer } from '@/components/layout/footer';
import type { ReactNode } from 'react';

const SIDEBAR_COLLAPSED_KEY = 'ensureu-sidebar-collapsed';

export default function MainLayout({ children }: { children: ReactNode }) {
  const [isCollapsed, setIsCollapsed] = useState(false);
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
    const stored = localStorage.getItem(SIDEBAR_COLLAPSED_KEY);
    if (stored === 'true') {
      setIsCollapsed(true);
    }
  }, []);

  const handleToggleCollapse = () => {
    setIsCollapsed((prev) => {
      const newValue = !prev;
      localStorage.setItem(SIDEBAR_COLLAPSED_KEY, String(newValue));
      return newValue;
    });
  };

  // Sidebar widths
  const sidebarWidth = isCollapsed ? 'lg:w-[70px]' : 'lg:w-72';
  const contentPadding = isCollapsed ? 'lg:pl-[70px]' : 'lg:pl-72';

  return (
    <div className="min-h-screen flex">
      {/* Desktop Sidebar - fixed on left */}
      <div className={`hidden lg:flex ${sidebarWidth} lg:flex-col lg:fixed lg:inset-y-0 transition-all duration-300`}>
        <Sidebar
          isCollapsed={mounted ? isCollapsed : false}
          onToggleCollapse={handleToggleCollapse}
        />
      </div>

      {/* Main content area */}
      <div className={`flex-1 flex flex-col ${contentPadding} transition-all duration-300`}>
        <SidebarHeader />
        <main className="flex-1 bg-slate-50">{children}</main>
        <Footer />
      </div>
    </div>
  );
}
