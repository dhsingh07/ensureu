'use client';

import { useState, useEffect } from 'react';
import { usePathname, useRouter } from 'next/navigation';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { useAuthStore } from '@/stores/auth-store';
import { useCategoryStore } from '@/stores/category-store';
import { MobileSidebar, SidebarToggle } from './sidebar';
import { Bell, Search, User, Settings, LogOut, LogIn } from 'lucide-react';
import { Input } from '@/components/ui/input';

export function SidebarHeader() {
  const router = useRouter();
  const [mounted, setMounted] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const pathname = usePathname();

  const user = useAuthStore((state) => state.user);
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const logout = useAuthStore((state) => state.logout);
  const rootCategory = useCategoryStore((state) => state.rootCategory);

  useEffect(() => {
    setMounted(true);
  }, []);

  // Get page title based on pathname
  const getPageTitle = () => {
    if (pathname === '/home') return 'Dashboard';
    if (pathname === '/admin') return 'Admin Dashboard';
    if (pathname.startsWith('/admin/paper')) return 'Paper Management';
    if (pathname.startsWith('/admin/quick-paper')) return 'Quick Paper Creator';
    if (pathname.startsWith('/admin/quiz')) return 'Quiz Management';
    if (pathname.startsWith('/admin/users')) return 'User Management';
    if (pathname.startsWith('/admin/config')) return 'Feature Config';
    if (pathname.startsWith('/admin/notification')) return 'Notifications';
    if (pathname.startsWith('/home/subscription')) return 'Subscriptions';
    if (pathname.startsWith('/home/results-analysis')) return 'Results Analysis';
    if (pathname.startsWith('/home/saved-questions')) return 'Saved Questions';
    if (pathname.startsWith('/exam')) return 'Exam';
    if (pathname.startsWith('/quiz')) return 'Daily Quiz';
    if (pathname.startsWith('/practice')) return 'Practice';
    if (pathname.startsWith('/progress')) return 'Progress';
    if (pathname.startsWith('/previous-papers')) return 'Previous Papers';
    if (pathname.startsWith('/profile')) return 'Profile';
    if (pathname.startsWith('/blog')) return 'Blog';
    return 'EnsureU';
  };

  const handleLogout = () => {
    logout();
    router.push('/login');
  };

  const userInitials = user
    ? `${user.firstName?.charAt(0) || ''}${user.lastName?.charAt(0) || user.userName?.charAt(0) || ''}`
    : '';

  return (
    <>
      <header className="sticky top-0 z-40 w-full border-b bg-white/95 backdrop-blur supports-[backdrop-filter]:bg-white/60">
        <div className="flex h-14 items-center gap-4 px-4">
          {/* Mobile menu toggle */}
          <SidebarToggle onClick={() => setMobileMenuOpen(true)} />

          {/* Page Title */}
          <div className="flex-1">
            <h1 className="text-lg font-semibold text-slate-900">
              {getPageTitle()}
            </h1>
            {pathname === '/home' && (
              <p className="text-xs text-slate-500 hidden sm:block">
                {rootCategory.replace(/_/g, ' ')}
              </p>
            )}
          </div>

          {/* Search (desktop only) */}
          <div className="hidden md:flex items-center max-w-sm flex-1">
            <div className="relative w-full">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
              <Input
                type="search"
                placeholder="Search tests..."
                className="pl-10 h-9"
              />
            </div>
          </div>

          {/* Actions */}
          <div className="flex items-center gap-2">
            {/* Notification Bell */}
            <Button variant="ghost" size="icon" className="relative">
              <Bell className="h-5 w-5" />
              <span className="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full" />
            </Button>

            {/* User Menu / Login */}
            {mounted && isAuthenticated ? (
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button
                    variant="ghost"
                    className="relative h-9 w-9 rounded-full"
                  >
                    <Avatar className="h-9 w-9">
                      <AvatarFallback className="bg-primary text-white text-sm">
                        {userInitials}
                      </AvatarFallback>
                    </Avatar>
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-56">
                  <DropdownMenuLabel>
                    <div className="flex flex-col space-y-1">
                      <p className="text-sm font-medium">
                        {user?.firstName} {user?.lastName}
                      </p>
                      <p className="text-xs text-slate-500">
                        {user?.emailId || user?.mobileNumber}
                      </p>
                    </div>
                  </DropdownMenuLabel>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem asChild>
                    <Link href="/profile" className="cursor-pointer">
                      <User className="mr-2 h-4 w-4" />
                      Profile
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link href="/home/subscription" className="cursor-pointer">
                      <Settings className="mr-2 h-4 w-4" />
                      Subscriptions
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
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
              <Link href="/login">
                <Button variant="ghost" size="icon" title="Login">
                  <LogIn className="h-5 w-5" />
                </Button>
              </Link>
            )}
          </div>
        </div>
      </header>

      {/* Mobile Sidebar */}
      <MobileSidebar open={mobileMenuOpen} onOpenChange={setMobileMenuOpen} />
    </>
  );
}
