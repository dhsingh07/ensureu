'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { useAuthStore } from '@/stores/auth-store';
import { useDashboardStats } from '@/hooks/use-admin';
import {
  FileText,
  Users,
  CreditCard,
  TrendingUp,
  BookOpen,
  CheckCircle,
  Clock,
  AlertCircle,
} from 'lucide-react';

// Default/fallback statistics when API returns empty
const defaultStats = {
  totalPapers: 0,
  totalFreePapers: 0,
  totalPaidPapers: 0,
  totalUsers: 0,
  activeSubscriptions: 0,
  freePapersByCategory: {
    SSC_CGL: 0,
    SSC_CHSL: 0,
    SSC_CPO: 0,
    BANK_PO: 0,
  },
  paidPapersByCategory: {
    SSC_CGL: 0,
    SSC_CHSL: 0,
    SSC_CPO: 0,
    BANK_PO: 0,
  },
  freePapersByState: {
    DRAFT: 0,
    ACTIVE: 0,
    APPROVED: 0,
  },
  paidPapersByState: {
    DRAFT: 0,
    ACTIVE: 0,
    APPROVED: 0,
  },
};

export default function AdminDashboardPage() {
  const user = useAuthStore((state) => state.user);
  const isSuperAdmin = useAuthStore((state) => state.isSuperAdmin);
  const isAdmin = useAuthStore((state) => state.isAdmin);

  const showStats = isSuperAdmin() || isAdmin();

  // Fetch dashboard statistics from API
  const { data: apiStats, isLoading, error } = useDashboardStats();

  // Use API data or fallback to defaults
  const stats = apiStats || defaultStats;

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-slate-900">Admin Dashboard</h1>
        <p className="text-slate-600">
          Welcome back, {user?.firstName || 'Admin'}
        </p>
      </div>

      {/* Error State */}
      {error && (
        <Card className="border-red-200 bg-red-50">
          <CardContent className="flex items-center gap-4 p-6">
            <AlertCircle className="h-6 w-6 text-red-600" />
            <div>
              <p className="font-medium text-red-900">Failed to load statistics</p>
              <p className="text-sm text-red-600">Please try refreshing the page</p>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Stats Grid - Only for SUPERADMIN and ADMIN */}
      {showStats && (
        <>
          {/* Overview Stats */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <Card>
              <CardContent className="flex items-center gap-4 p-6">
                <div className="p-3 bg-blue-100 rounded-lg">
                  <FileText className="h-6 w-6 text-blue-600" />
                </div>
                <div>
                  {isLoading ? (
                    <Skeleton className="h-8 w-16" />
                  ) : (
                    <p className="text-2xl font-bold text-slate-900">
                      {stats.totalPapers}
                    </p>
                  )}
                  <p className="text-sm text-slate-500">Total Papers</p>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardContent className="flex items-center gap-4 p-6">
                <div className="p-3 bg-green-100 rounded-lg">
                  <BookOpen className="h-6 w-6 text-green-600" />
                </div>
                <div>
                  {isLoading ? (
                    <Skeleton className="h-8 w-16" />
                  ) : (
                    <p className="text-2xl font-bold text-slate-900">
                      {stats.totalFreePapers}
                    </p>
                  )}
                  <p className="text-sm text-slate-500">Free Papers</p>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardContent className="flex items-center gap-4 p-6">
                <div className="p-3 bg-orange-100 rounded-lg">
                  <CreditCard className="h-6 w-6 text-orange-600" />
                </div>
                <div>
                  {isLoading ? (
                    <Skeleton className="h-8 w-16" />
                  ) : (
                    <p className="text-2xl font-bold text-slate-900">
                      {stats.totalPaidPapers}
                    </p>
                  )}
                  <p className="text-sm text-slate-500">Paid Papers</p>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardContent className="flex items-center gap-4 p-6">
                <div className="p-3 bg-purple-100 rounded-lg">
                  <Users className="h-6 w-6 text-purple-600" />
                </div>
                <div>
                  {isLoading ? (
                    <Skeleton className="h-8 w-16" />
                  ) : (
                    <p className="text-2xl font-bold text-slate-900">
                      {stats.totalUsers}
                    </p>
                  )}
                  <p className="text-sm text-slate-500">Total Users</p>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Category Breakdown */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Papers by Category</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {Object.entries(stats.freePapersByCategory).map(
                    ([category, count]) => (
                      <div
                        key={category}
                        className="flex items-center justify-between"
                      >
                        <span className="text-sm font-medium text-slate-700">
                          {category.replace('_', ' ')}
                        </span>
                        <div className="flex items-center gap-4">
                          <span className="text-sm text-green-600">
                            {count} Free
                          </span>
                          <span className="text-sm text-orange-600">
                            {stats.paidPapersByCategory[
                              category as keyof typeof stats.paidPapersByCategory
                            ] || 0}{' '}
                            Paid
                          </span>
                        </div>
                      </div>
                    )
                  )}
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Papers by State</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div className="flex items-center justify-between p-3 bg-slate-50 rounded-lg">
                    <div className="flex items-center gap-2">
                      <Clock className="h-5 w-5 text-yellow-600" />
                      <span className="text-sm font-medium text-slate-700">
                        Draft
                      </span>
                    </div>
                    <div className="flex items-center gap-4">
                      <span className="text-sm text-green-600">
                        {stats.freePapersByState.DRAFT} Free
                      </span>
                      <span className="text-sm text-orange-600">
                        {stats.paidPapersByState.DRAFT} Paid
                      </span>
                    </div>
                  </div>
                  <div className="flex items-center justify-between p-3 bg-slate-50 rounded-lg">
                    <div className="flex items-center gap-2">
                      <CheckCircle className="h-5 w-5 text-green-600" />
                      <span className="text-sm font-medium text-slate-700">
                        Active
                      </span>
                    </div>
                    <div className="flex items-center gap-4">
                      <span className="text-sm text-green-600">
                        {stats.freePapersByState.ACTIVE} Free
                      </span>
                      <span className="text-sm text-orange-600">
                        {stats.paidPapersByState.ACTIVE} Paid
                      </span>
                    </div>
                  </div>
                  <div className="flex items-center justify-between p-3 bg-slate-50 rounded-lg">
                    <div className="flex items-center gap-2">
                      <TrendingUp className="h-5 w-5 text-blue-600" />
                      <span className="text-sm font-medium text-slate-700">
                        Approved
                      </span>
                    </div>
                    <div className="flex items-center gap-4">
                      <span className="text-sm text-green-600">
                        {stats.freePapersByState.APPROVED} Free
                      </span>
                      <span className="text-sm text-orange-600">
                        {stats.paidPapersByState.APPROVED} Paid
                      </span>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </>
      )}

      {/* Quick Actions for Teachers */}
      {!showStats && (
        <Card>
          <CardHeader>
            <CardTitle>Quick Actions</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-slate-600 mb-4">
              As a Teacher, you can manage papers and create new test content.
            </p>
            <div className="flex gap-4">
              <a
                href="/admin/paper"
                className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary/90"
              >
                Manage Papers
              </a>
              <a
                href="/admin/quick-paper"
                className="px-4 py-2 bg-slate-100 text-slate-700 rounded-lg hover:bg-slate-200"
              >
                Quick Paper Creator
              </a>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
