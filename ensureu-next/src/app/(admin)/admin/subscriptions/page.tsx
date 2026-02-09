'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import {
  Plus,
  Search,
  Filter,
  MoreVertical,
  Eye,
  Edit,
  Trash,
  Clock,
  CheckCircle,
  AlertCircle,
  Package,
  Users,
  DollarSign,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {
  useSubscriptionList,
  useSubscriptionStats,
  useDeleteSubscription,
  useActivateSubscription,
  useDeactivateSubscription,
} from '@/hooks/use-subscription-admin';
import {
  SubscriptionListParams,
  SubscriptionState,
  TestType,
  formatValidity,
} from '@/types/subscription-admin';
import { PaperType, PaperCategory } from '@/types/paper';

const PAPER_TYPES: PaperType[] = ['SSC', 'BANK'];

const PAPER_CATEGORIES: Record<PaperType, { value: PaperCategory; label: string }[]> = {
  SSC: [
    { value: 'SSC_CGL', label: 'SSC CGL' },
    { value: 'SSC_CPO', label: 'SSC CPO' },
    { value: 'SSC_CHSL', label: 'SSC CHSL' },
  ],
  BANK: [
    { value: 'BANK_PO', label: 'Bank PO' },
  ],
};

const TEST_TYPES: { value: TestType; label: string }[] = [
  { value: 'FREE', label: 'Free' },
  { value: 'PAID', label: 'Paid' },
];

const STATES: { value: SubscriptionState; label: string }[] = [
  { value: 'DRAFT', label: 'Draft' },
  { value: 'ACTIVE', label: 'Active' },
];

export default function SubscriptionsPage() {
  const router = useRouter();
  const [filters, setFilters] = useState<SubscriptionListParams>({
    page: 0,
    size: 20,
    sortBy: 'createdAt',
    sortDir: 'desc',
  });
  const [searchTerm, setSearchTerm] = useState('');

  const { data: subscriptions, isLoading } = useSubscriptionList(filters);
  const { data: stats } = useSubscriptionStats();
  const deleteSubscription = useDeleteSubscription();
  const activateSubscription = useActivateSubscription();
  const deactivateSubscription = useDeactivateSubscription();

  const handleSearch = () => {
    setFilters({ ...filters, search: searchTerm, page: 0 });
  };

  const handleDelete = async (id: string) => {
    if (!confirm('Are you sure you want to delete this subscription?')) return;
    try {
      await deleteSubscription.mutateAsync(id);
    } catch (error) {
      console.error('Failed to delete:', error);
    }
  };

  const handleActivate = async (id: string) => {
    try {
      await activateSubscription.mutateAsync(id);
    } catch (error) {
      console.error('Failed to activate:', error);
    }
  };

  const handleDeactivate = async (id: string) => {
    try {
      await deactivateSubscription.mutateAsync({ id, force: false });
    } catch (error) {
      console.error('Failed to deactivate:', error);
    }
  };

  return (
    <div className="container mx-auto py-6 px-4">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold">Subscription Management</h1>
          <p className="text-gray-600">Manage subscription packages for users</p>
        </div>
        <Link href="/admin/subscriptions/create">
          <Button>
            <Plus className="mr-2 h-4 w-4" />
            Create Subscription
          </Button>
        </Link>
      </div>

      {/* Stats Cards */}
      <div className="grid gap-4 md:grid-cols-4 mb-6">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total</CardTitle>
            <Package className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stats?.totalSubscriptions || 0}</div>
            <p className="text-xs text-muted-foreground">
              {stats?.activeSubscriptions || 0} active, {stats?.draftSubscriptions || 0} draft
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Expiring Soon</CardTitle>
            <AlertCircle className="h-4 w-4 text-orange-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-orange-600">{stats?.expiringIn7Days || 0}</div>
            <p className="text-xs text-muted-foreground">
              in next 7 days ({stats?.expiringIn30Days || 0} in 30 days)
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Available Papers</CardTitle>
            <CheckCircle className="h-4 w-4 text-green-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">{stats?.availablePapers || 0}</div>
            <p className="text-xs text-muted-foreground">not assigned to any subscription</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">By Type</CardTitle>
            <DollarSign className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-sm">
              <span className="font-medium">Free:</span> {stats?.freeStats?.active || 0} active
            </div>
            <div className="text-sm">
              <span className="font-medium">Paid:</span> {stats?.paidStats?.active || 0} active
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Filters */}
      <Card className="mb-6">
        <CardContent className="pt-6">
          <div className="flex flex-wrap gap-4">
            <div className="flex-1 min-w-[200px]">
              <div className="flex gap-2">
                <Input
                  placeholder="Search by name..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                />
                <Button variant="outline" onClick={handleSearch}>
                  <Search className="h-4 w-4" />
                </Button>
              </div>
            </div>

            <Select
              value={filters.paperType || '__all__'}
              onValueChange={(v) => setFilters({
                ...filters,
                paperType: v === '__all__' ? undefined : v as PaperType,
                paperCategory: undefined,
                page: 0,
              })}
            >
              <SelectTrigger className="w-[140px]">
                <SelectValue placeholder="Paper Type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="__all__">All Types</SelectItem>
                {PAPER_TYPES.map((t) => (
                  <SelectItem key={t} value={t}>{t}</SelectItem>
                ))}
              </SelectContent>
            </Select>

            {filters.paperType && (
              <Select
                value={filters.paperCategory || '__all__'}
                onValueChange={(v) => setFilters({
                  ...filters,
                  paperCategory: v === '__all__' ? undefined : v as PaperCategory,
                  page: 0,
                })}
              >
                <SelectTrigger className="w-[160px]">
                  <SelectValue placeholder="Category" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="__all__">All Categories</SelectItem>
                  {PAPER_CATEGORIES[filters.paperType]?.map((c) => (
                    <SelectItem key={c.value} value={c.value}>{c.label}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            )}

            <Select
              value={filters.testType || '__all__'}
              onValueChange={(v) => setFilters({
                ...filters,
                testType: v === '__all__' ? undefined : v as TestType,
                page: 0,
              })}
            >
              <SelectTrigger className="w-[120px]">
                <SelectValue placeholder="Test Type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="__all__">All</SelectItem>
                {TEST_TYPES.map((t) => (
                  <SelectItem key={t.value} value={t.value}>{t.label}</SelectItem>
                ))}
              </SelectContent>
            </Select>

            <Select
              value={filters.state || '__all__'}
              onValueChange={(v) => setFilters({
                ...filters,
                state: v === '__all__' ? undefined : v as SubscriptionState,
                page: 0,
              })}
            >
              <SelectTrigger className="w-[120px]">
                <SelectValue placeholder="State" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="__all__">All States</SelectItem>
                {STATES.map((s) => (
                  <SelectItem key={s.value} value={s.value}>{s.label}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      {/* Table */}
      <Card>
        <CardContent className="p-0">
          {isLoading ? (
            <div className="p-6 space-y-4">
              {[...Array(5)].map((_, i) => (
                <Skeleton key={i} className="h-16 w-full" />
              ))}
            </div>
          ) : !subscriptions?.content?.length ? (
            <div className="p-12 text-center">
              <Package className="h-12 w-12 mx-auto text-gray-400 mb-4" />
              <p className="text-gray-600">No subscriptions found</p>
              <Link href="/admin/subscriptions/create">
                <Button className="mt-4">
                  <Plus className="mr-2 h-4 w-4" />
                  Create First Subscription
                </Button>
              </Link>
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Name</TableHead>
                  <TableHead>Category</TableHead>
                  <TableHead>Type</TableHead>
                  <TableHead>Papers</TableHead>
                  <TableHead>Validity</TableHead>
                  <TableHead>State</TableHead>
                  <TableHead className="w-[80px]">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {subscriptions.content.map((sub) => (
                  <TableRow key={sub.id}>
                    <TableCell>
                      <div>
                        <Link
                          href={`/admin/subscriptions/${sub.id}`}
                          className="font-medium hover:underline"
                        >
                          {sub.name || `Subscription #${sub.subscriptionId}`}
                        </Link>
                        {sub.description && (
                          <p className="text-sm text-gray-500 truncate max-w-[200px]">
                            {sub.description}
                          </p>
                        )}
                      </div>
                    </TableCell>
                    <TableCell>
                      <div className="text-sm">
                        <div>{sub.paperCategory?.replace(/_/g, ' ')}</div>
                        <div className="text-gray-500">
                          {sub.paperSubCategory?.replace(/_/g, ' ')}
                        </div>
                      </div>
                    </TableCell>
                    <TableCell>
                      <Badge variant={sub.testType === 'PAID' ? 'default' : 'secondary'}>
                        {sub.testType}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      <span className="font-medium">{sub.paperCount}</span>
                    </TableCell>
                    <TableCell>
                      <div className="text-sm">
                        {sub.isExpired ? (
                          <span className="text-red-600">Expired</span>
                        ) : (
                          <span className={sub.validityDays < 7 ? 'text-orange-600' : ''}>
                            {formatValidity(sub.validity)}
                          </span>
                        )}
                      </div>
                    </TableCell>
                    <TableCell>
                      <Badge
                        variant="outline"
                        className={
                          sub.state === 'ACTIVE'
                            ? 'bg-green-50 text-green-700 border-green-200'
                            : 'bg-gray-50 text-gray-700'
                        }
                      >
                        {sub.state}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="sm">
                            <MoreVertical className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem onClick={() => router.push(`/admin/subscriptions/${sub.id}`)}>
                            <Eye className="mr-2 h-4 w-4" />
                            View
                          </DropdownMenuItem>
                          <DropdownMenuItem onClick={() => router.push(`/admin/subscriptions/${sub.id}/edit`)}>
                            <Edit className="mr-2 h-4 w-4" />
                            Edit
                          </DropdownMenuItem>
                          {sub.state === 'DRAFT' ? (
                            <DropdownMenuItem onClick={() => handleActivate(sub.id)}>
                              <CheckCircle className="mr-2 h-4 w-4" />
                              Activate
                            </DropdownMenuItem>
                          ) : (
                            <DropdownMenuItem onClick={() => handleDeactivate(sub.id)}>
                              <Clock className="mr-2 h-4 w-4" />
                              Deactivate
                            </DropdownMenuItem>
                          )}
                          {sub.state === 'DRAFT' && (
                            <DropdownMenuItem
                              className="text-red-600"
                              onClick={() => handleDelete(sub.id)}
                            >
                              <Trash className="mr-2 h-4 w-4" />
                              Delete
                            </DropdownMenuItem>
                          )}
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>

        {/* Pagination */}
        {subscriptions && subscriptions.totalPages > 1 && (
          <div className="flex justify-between items-center p-4 border-t">
            <p className="text-sm text-gray-600">
              Showing {subscriptions.number * subscriptions.size + 1} -{' '}
              {Math.min((subscriptions.number + 1) * subscriptions.size, subscriptions.totalElements)} of{' '}
              {subscriptions.totalElements}
            </p>
            <div className="flex gap-2">
              <Button
                variant="outline"
                size="sm"
                disabled={subscriptions.first}
                onClick={() => setFilters({ ...filters, page: (filters.page || 0) - 1 })}
              >
                Previous
              </Button>
              <Button
                variant="outline"
                size="sm"
                disabled={subscriptions.last}
                onClick={() => setFilters({ ...filters, page: (filters.page || 0) + 1 })}
              >
                Next
              </Button>
            </div>
          </div>
        )}
      </Card>
    </div>
  );
}
