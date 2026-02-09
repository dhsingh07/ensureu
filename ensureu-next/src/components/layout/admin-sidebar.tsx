'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { cn } from '@/lib/utils';
import { useAuthStore } from '@/stores/auth-store';
import {
  LayoutDashboard,
  FileText,
  Users,
  Settings,
  Bell,
  Zap,
} from 'lucide-react';

interface NavItem {
  href: string;
  label: string;
  icon: React.ComponentType<{ className?: string }>;
  roles?: ('SUPERADMIN' | 'ADMIN' | 'TEACHER' | 'USER')[];
}

const navItems: NavItem[] = [
  {
    href: '/admin',
    label: 'Dashboard',
    icon: LayoutDashboard,
  },
  {
    href: '/admin/paper',
    label: 'Paper Management',
    icon: FileText,
  },
  {
    href: '/admin/quick-paper',
    label: 'Quick Paper',
    icon: Zap,
  },
  {
    href: '/admin/notification',
    label: 'Notifications',
    icon: Bell,
    roles: ['SUPERADMIN', 'ADMIN'],
  },
  {
    href: '/admin/users',
    label: 'User Management',
    icon: Users,
    roles: ['SUPERADMIN'],
  },
  {
    href: '/admin/config',
    label: 'Feature Config',
    icon: Settings,
    roles: ['SUPERADMIN'],
  },
];

export function AdminSidebar() {
  const [mounted, setMounted] = useState(false);
  const pathname = usePathname();
  const user = useAuthStore((state) => state.user);
  const isSuperAdmin = useAuthStore((state) => state.isSuperAdmin);
  const isAdmin = useAuthStore((state) => state.isAdmin);
  const isTeacher = useAuthStore((state) => state.isTeacher);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return null;
  }

  const userRoles: ('SUPERADMIN' | 'ADMIN' | 'TEACHER' | 'USER')[] = [];
  if (isSuperAdmin()) userRoles.push('SUPERADMIN');
  if (isAdmin()) userRoles.push('ADMIN');
  if (isTeacher()) userRoles.push('TEACHER');

  const filteredNavItems = navItems.filter((item) => {
    if (!item.roles) return true;
    return item.roles.some((role) => userRoles.includes(role));
  });

  return (
    <aside className="w-64 border-r bg-white min-h-[calc(100vh-4rem)]">
      {/* User Profile Card */}
      <div className="p-4 border-b">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-primary rounded-full flex items-center justify-center">
            <span className="text-white font-semibold">
              {user?.firstName?.charAt(0) || user?.userName?.charAt(0) || 'A'}
            </span>
          </div>
          <div>
            <p className="font-medium text-slate-900">
              {user?.firstName} {user?.lastName}
            </p>
            <p className="text-xs text-slate-500">
              {userRoles[0] || 'Admin'}
            </p>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="p-2">
        <ul className="space-y-1">
          {filteredNavItems.map((item) => {
            const isActive =
              pathname === item.href ||
              (item.href !== '/admin' && pathname.startsWith(item.href));

            return (
              <li key={item.href}>
                <Link
                  href={item.href}
                  className={cn(
                    'flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors',
                    isActive
                      ? 'bg-primary text-white'
                      : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                  )}
                >
                  <item.icon className="h-5 w-5" />
                  {item.label}
                </Link>
              </li>
            );
          })}
        </ul>
      </nav>
    </aside>
  );
}
