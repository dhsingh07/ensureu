'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { cn } from '@/lib/utils';
import { useAuthStore } from '@/stores/auth-store';
import { useCategoryStore } from '@/stores/category-store';
import { CATEGORIES } from '@/lib/constants/api-urls';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from '@/components/ui/collapsible';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import {
  Home,
  BookOpen,
  Crown,
  FileText,
  Target,
  ScrollText,
  BarChart3,
  Trophy,
  Bookmark,
  CreditCard,
  ShoppingBag,
  User,
  Settings,
  LogOut,
  ChevronDown,
  ChevronRight,
  ChevronLeft,
  LayoutDashboard,
  Zap,
  Bell,
  Users,
  Wrench,
  GraduationCap,
  Menu,
  X,
  Timer,
  Library,
  Sparkles,
  PanelLeftClose,
  PanelLeft,
} from 'lucide-react';
import type { LucideIcon } from 'lucide-react';

type RoleType = 'SUPERADMIN' | 'ADMIN' | 'TEACHER' | 'USER';

interface NavItem {
  href: string;
  label: string;
  icon: LucideIcon;
  badge?: string | number;
  roles?: RoleType[];
}

interface NavGroup {
  id: string;
  label: string;
  icon?: LucideIcon;
  items: NavItem[];
  roles?: RoleType[];
  defaultOpen?: boolean;
}

// User navigation groups
const userNavGroups: NavGroup[] = [
  {
    id: 'main',
    label: '',
    items: [
      { href: '/home', label: 'Dashboard', icon: Home },
    ],
    defaultOpen: true,
  },
  {
    id: 'practice',
    label: 'Practice',
    icon: BookOpen,
    items: [
      { href: '/home?tab=free', label: 'Free Tests', icon: FileText },
      { href: '/home?tab=paid', label: 'Premium Tests', icon: Crown },
      { href: '/quiz', label: 'Daily Quiz', icon: Timer },
      { href: '/previous-papers', label: 'Previous Papers', icon: ScrollText },
      { href: '/practice', label: 'Quick Practice', icon: Target },
    ],
    defaultOpen: true,
  },
  {
    id: 'progress',
    label: 'Progress',
    icon: BarChart3,
    items: [
      { href: '/home?tab=completed', label: 'My Results', icon: Trophy },
      { href: '/progress', label: 'Analytics', icon: BarChart3 },
      { href: '/home/saved-questions', label: 'Saved Questions', icon: Bookmark },
    ],
    defaultOpen: false,
  },
  {
    id: 'subscriptions',
    label: 'Subscriptions',
    icon: CreditCard,
    items: [
      { href: '/home/subscription?tab=subscribed', label: 'My Plans', icon: CreditCard },
      { href: '/home/subscription?tab=available', label: 'Browse Plans', icon: ShoppingBag },
    ],
    defaultOpen: false,
  },
];

// Admin navigation groups (only shown to admin users)
const adminNavGroups: NavGroup[] = [
  {
    id: 'admin-main',
    label: '',
    items: [
      { href: '/admin', label: 'Admin Dashboard', icon: LayoutDashboard },
    ],
    roles: ['SUPERADMIN', 'ADMIN', 'TEACHER'],
    defaultOpen: true,
  },
  {
    id: 'content',
    label: 'Content',
    icon: FileText,
    items: [
      { href: '/admin/paper', label: 'Paper Management', icon: FileText },
      { href: '/admin/question-bank', label: 'Question Bank', icon: Library },
      { href: '/admin/quiz', label: 'Quiz Management', icon: Timer },
      { href: '/admin/quick-paper', label: 'Quick Paper', icon: Zap },
    ],
    roles: ['SUPERADMIN', 'ADMIN', 'TEACHER'],
    defaultOpen: false,
  },
  {
    id: 'users',
    label: 'Users',
    icon: Users,
    items: [
      { href: '/admin/users', label: 'User Management', icon: Users },
    ],
    roles: ['SUPERADMIN'],
    defaultOpen: false,
  },
  {
    id: 'admin-subscriptions',
    label: 'Subscriptions',
    icon: CreditCard,
    items: [
      { href: '/admin/subscriptions', label: 'Manage Subscriptions', icon: CreditCard },
    ],
    roles: ['SUPERADMIN'],
    defaultOpen: false,
  },
  {
    id: 'settings',
    label: 'Settings',
    icon: Wrench,
    items: [
      { href: '/admin/llm-config', label: 'AI/LLM Config', icon: Sparkles },
      { href: '/admin/config', label: 'Feature Config', icon: Settings },
      { href: '/admin/notification', label: 'Notifications', icon: Bell },
    ],
    roles: ['SUPERADMIN'],
    defaultOpen: false,
  },
];

interface SidebarProps {
  className?: string;
  onNavigate?: () => void;
  isCollapsed?: boolean;
  onToggleCollapse?: () => void;
}

export function Sidebar({ className, onNavigate, isCollapsed = false, onToggleCollapse }: SidebarProps) {
  const [mounted, setMounted] = useState(false);
  const [openGroups, setOpenGroups] = useState<Record<string, boolean>>({});
  const pathname = usePathname();

  const user = useAuthStore((state) => state.user);
  const isSuperAdmin = useAuthStore((state) => state.isSuperAdmin);
  const isAdmin = useAuthStore((state) => state.isAdmin);
  const isTeacher = useAuthStore((state) => state.isTeacher);
  const hasAdminAccess = useAuthStore((state) => state.hasAdminPanelAccess);
  const logout = useAuthStore((state) => state.logout);

  const { rootCategory, setRootCategory, setChildCategory } = useCategoryStore();

  useEffect(() => {
    setMounted(true);
    // Initialize open groups based on defaultOpen
    const initialOpenGroups: Record<string, boolean> = {};
    [...userNavGroups, ...adminNavGroups].forEach((group) => {
      initialOpenGroups[group.id] = group.defaultOpen ?? false;
    });
    setOpenGroups(initialOpenGroups);
  }, []);

  if (!mounted) {
    return null;
  }

  const userRoles: RoleType[] = [];
  if (isSuperAdmin()) userRoles.push('SUPERADMIN');
  if (isAdmin()) userRoles.push('ADMIN');
  if (isTeacher()) userRoles.push('TEACHER');
  userRoles.push('USER');

  const filterByRole = (items: NavItem[] | NavGroup[]) => {
    return items.filter((item) => {
      if (!item.roles) return true;
      return item.roles.some((role) => userRoles.includes(role));
    });
  };

  const toggleGroup = (groupId: string) => {
    setOpenGroups((prev) => ({ ...prev, [groupId]: !prev[groupId] }));
  };

  const isActive = (href: string) => {
    // Handle query params in href
    const [path, query] = href.split('?');
    if (query) {
      return pathname === path && window.location.search.includes(query.split('=')[1]);
    }
    return pathname === path || (path !== '/home' && path !== '/admin' && pathname.startsWith(path));
  };

  const handleLogout = () => {
    logout();
    onNavigate?.();
  };

  const userInitials = user
    ? `${user.firstName?.charAt(0) || ''}${user.lastName?.charAt(0) || user.userName?.charAt(0) || ''}`
    : '';

  const renderNavItem = (item: NavItem) => {
    const linkContent = (
      <Link
        key={item.href}
        href={item.href}
        onClick={onNavigate}
        className={cn(
          'flex items-center rounded-lg text-sm font-medium transition-colors',
          isCollapsed ? 'justify-center p-2' : 'gap-3 px-3 py-2',
          isActive(item.href)
            ? 'bg-primary text-white'
            : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
        )}
      >
        <item.icon className={cn('flex-shrink-0', isCollapsed ? 'h-5 w-5' : 'h-4 w-4')} />
        {!isCollapsed && (
          <>
            <span className="truncate">{item.label}</span>
            {item.badge && (
              <span className="ml-auto bg-primary/10 text-primary text-xs px-2 py-0.5 rounded-full">
                {item.badge}
              </span>
            )}
          </>
        )}
      </Link>
    );

    if (isCollapsed) {
      return (
        <Tooltip key={item.href}>
          <TooltipTrigger asChild>
            {linkContent}
          </TooltipTrigger>
          <TooltipContent side="right">
            <p>{item.label}</p>
          </TooltipContent>
        </Tooltip>
      );
    }

    return linkContent;
  };

  const renderNavGroup = (group: NavGroup) => {
    const filteredItems = filterByRole(group.items) as NavItem[];
    if (filteredItems.length === 0) return null;

    // When collapsed, just render items directly without group headers
    if (isCollapsed) {
      return (
        <div key={group.id} className="space-y-1">
          {filteredItems.map(renderNavItem)}
        </div>
      );
    }

    // If no label, render items directly
    if (!group.label) {
      return (
        <div key={group.id} className="space-y-1">
          {filteredItems.map(renderNavItem)}
        </div>
      );
    }

    return (
      <Collapsible
        key={group.id}
        open={openGroups[group.id]}
        onOpenChange={() => toggleGroup(group.id)}
      >
        <CollapsibleTrigger className="flex items-center justify-between w-full px-3 py-2 text-xs font-semibold text-slate-500 uppercase tracking-wider hover:text-slate-700">
          <div className="flex items-center gap-2">
            {group.icon && <group.icon className="h-4 w-4" />}
            {group.label}
          </div>
          {openGroups[group.id] ? (
            <ChevronDown className="h-4 w-4" />
          ) : (
            <ChevronRight className="h-4 w-4" />
          )}
        </CollapsibleTrigger>
        <CollapsibleContent className="space-y-1 mt-1">
          {filteredItems.map(renderNavItem)}
        </CollapsibleContent>
      </Collapsible>
    );
  };

  return (
    <TooltipProvider delayDuration={0}>
      <aside
        className={cn(
          'flex flex-col bg-white border-r h-full overflow-hidden transition-all duration-300',
          isCollapsed ? 'w-[70px]' : 'w-full',
          className
        )}
      >
        {/* Logo - Fixed */}
        <div className={cn('flex-shrink-0 border-b', isCollapsed ? 'p-3' : 'p-4')}>
          <Link href="/home" className="flex items-center gap-2" onClick={onNavigate}>
            <div className={cn(
              'bg-gradient-to-br from-teal-500 to-cyan-600 rounded-lg flex items-center justify-center flex-shrink-0',
              isCollapsed ? 'w-10 h-10' : 'w-10 h-10'
            )}>
              <Sparkles className="h-6 w-6 text-white" />
            </div>
            {!isCollapsed && <span className="text-xl font-bold text-slate-900">EnsureU</span>}
          </Link>
        </div>

      {/* Category Selector - Fixed */}
      <div className={cn('flex-shrink-0 border-b', isCollapsed ? 'p-2' : 'p-3')}>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            {isCollapsed ? (
              <Tooltip>
                <TooltipTrigger asChild>
                  <Button variant="outline" size="icon" className="w-full h-10">
                    <GraduationCap className="h-4 w-4" />
                  </Button>
                </TooltipTrigger>
                <TooltipContent side="right">
                  <p>{rootCategory.replace(/_/g, ' ')}</p>
                </TooltipContent>
              </Tooltip>
            ) : (
              <Button variant="outline" className="w-full justify-between gap-2">
                <div className="flex items-center gap-2">
                  <GraduationCap className="h-4 w-4" />
                  <span className="truncate">{rootCategory.replace(/_/g, ' ')}</span>
                </div>
                <ChevronDown className="h-4 w-4 flex-shrink-0" />
              </Button>
            )}
          </DropdownMenuTrigger>
          <DropdownMenuContent align="start" className="w-56">
            {CATEGORIES.map((category) => (
              <div key={category.name}>
                <div className="px-2 py-1.5 text-xs font-semibold text-slate-500">
                  {category.label}
                </div>
                {category.subCategories.map((sub) => (
                  <DropdownMenuItem
                    key={sub.name}
                    onClick={() => {
                      setRootCategory(sub.name as typeof rootCategory);
                      setChildCategory('');
                      onNavigate?.();
                    }}
                    className={cn(
                      'cursor-pointer',
                      rootCategory === sub.name && 'bg-primary/10 text-primary'
                    )}
                  >
                    {sub.label}
                  </DropdownMenuItem>
                ))}
              </div>
            ))}
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      {/* Navigation - Scrollable */}
      <div className="flex-1 overflow-y-auto">
        <div className={cn('py-4 space-y-4', isCollapsed ? 'px-2' : 'px-3')}>
          {/* User Navigation */}
          {userNavGroups.map(renderNavGroup)}

          {/* Admin Navigation */}
          {hasAdminAccess() && (
            <>
              <Separator className="my-4" />
              {!isCollapsed && (
                <div className="px-3 py-1">
                  <span className="text-xs font-semibold text-orange-600 uppercase tracking-wider">
                    Admin Panel
                  </span>
                </div>
              )}
              {(filterByRole(adminNavGroups) as NavGroup[]).map(renderNavGroup)}
            </>
          )}
        </div>
      </div>

      {/* Collapse Toggle Button */}
      {onToggleCollapse && (
        <div className={cn('flex-shrink-0 border-t', isCollapsed ? 'p-2' : 'p-3')}>
          <Tooltip>
            <TooltipTrigger asChild>
              <Button
                variant="ghost"
                size={isCollapsed ? 'icon' : 'sm'}
                className={cn('w-full', !isCollapsed && 'justify-start gap-2')}
                onClick={onToggleCollapse}
              >
                {isCollapsed ? (
                  <PanelLeft className="h-4 w-4" />
                ) : (
                  <>
                    <PanelLeftClose className="h-4 w-4" />
                    <span>Collapse</span>
                  </>
                )}
              </Button>
            </TooltipTrigger>
            <TooltipContent side="right">
              <p>{isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}</p>
            </TooltipContent>
          </Tooltip>
        </div>
      )}

      {/* User Profile & Actions - Fixed at bottom */}
      <div className={cn('flex-shrink-0 border-t', isCollapsed ? 'p-2' : 'p-3')}>
        {isCollapsed ? (
          // Collapsed: Show avatar with dropdown
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" className="w-full h-10 p-0">
                <div className="w-10 h-10 bg-primary rounded-full flex items-center justify-center">
                  <span className="text-white font-semibold text-sm">{userInitials}</span>
                </div>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent side="right" align="end" className="w-56">
              <div className="px-2 py-1.5">
                <p className="font-medium text-sm">
                  {user?.firstName} {user?.lastName}
                </p>
                <p className="text-xs text-slate-500">
                  {user?.emailId || user?.mobileNumber}
                </p>
              </div>
              <DropdownMenuItem asChild>
                <Link href="/profile" onClick={onNavigate} className="cursor-pointer">
                  <User className="mr-2 h-4 w-4" />
                  Profile
                </Link>
              </DropdownMenuItem>
              <DropdownMenuItem
                className="text-red-600 cursor-pointer"
                onClick={handleLogout}
              >
                <LogOut className="mr-2 h-4 w-4" />
                Logout
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        ) : (
          // Expanded: Show full profile section
          <>
            <div className="flex items-center gap-3 mb-3 px-2">
              <div className="w-10 h-10 bg-primary rounded-full flex items-center justify-center flex-shrink-0">
                <span className="text-white font-semibold">{userInitials}</span>
              </div>
              <div className="min-w-0 flex-1">
                <p className="font-medium text-slate-900 truncate">
                  {user?.firstName} {user?.lastName}
                </p>
                <p className="text-xs text-slate-500 truncate">
                  {user?.emailId || user?.mobileNumber}
                </p>
              </div>
            </div>
            <div className="space-y-1">
              <Link href="/profile" onClick={onNavigate}>
                <Button variant="ghost" className="w-full justify-start gap-2" size="sm">
                  <User className="h-4 w-4" />
                  Profile
                </Button>
              </Link>
              <Button
                variant="ghost"
                className="w-full justify-start gap-2 text-red-600 hover:text-red-700 hover:bg-red-50"
                size="sm"
                onClick={handleLogout}
              >
                <LogOut className="h-4 w-4" />
                Logout
              </Button>
            </div>
          </>
        )}
      </div>
    </aside>
    </TooltipProvider>
  );
}

// Mobile sidebar wrapper using Sheet
interface MobileSidebarProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function MobileSidebar({ open, onOpenChange }: MobileSidebarProps) {
  return (
    <>
      {/* Backdrop */}
      {open && (
        <div
          className="fixed inset-0 bg-black/50 z-40 lg:hidden"
          onClick={() => onOpenChange(false)}
        />
      )}

      {/* Sidebar */}
      <div
        className={cn(
          'fixed inset-y-0 left-0 z-50 w-72 transform transition-transform duration-300 ease-in-out lg:hidden',
          open ? 'translate-x-0' : '-translate-x-full'
        )}
      >
        <Sidebar onNavigate={() => onOpenChange(false)} />
        <Button
          variant="ghost"
          size="icon"
          className="absolute top-4 right-4"
          onClick={() => onOpenChange(false)}
        >
          <X className="h-5 w-5" />
        </Button>
      </div>
    </>
  );
}

// Sidebar toggle button for header
export function SidebarToggle({ onClick }: { onClick: () => void }) {
  return (
    <Button variant="ghost" size="icon" onClick={onClick} className="lg:hidden">
      <Menu className="h-5 w-5" />
    </Button>
  );
}
