'use client';

import { useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import Link from 'next/link';
import {
  ArrowLeft,
  Edit,
  Clock,
  CheckCircle,
  AlertCircle,
  Package,
  Calendar,
  Users,
  DollarSign,
  FileText,
  Loader2,
  CalendarPlus,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { Separator } from '@/components/ui/separator';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import {
  useSubscription,
  useSubscriptionPapers,
  useActivateSubscription,
  useDeactivateSubscription,
  useExtendValidity,
} from '@/hooks/use-subscription-admin';
import {
  formatValidity,
  formatPrice,
  msToMinutes,
  SUBSCRIPTION_TYPE_LABELS,
  SubscriptionType,
} from '@/types/subscription-admin';

export default function SubscriptionDetailPage() {
  const params = useParams();
  const router = useRouter();
  const id = params.id as string;

  const { data: subscription, isLoading } = useSubscription(id);
  const { data: papers, isLoading: papersLoading } = useSubscriptionPapers(id);
  const activateSubscription = useActivateSubscription();
  const deactivateSubscription = useDeactivateSubscription();
  const extendValidity = useExtendValidity();

  const [extendDialogOpen, setExtendDialogOpen] = useState(false);
  const [extendDays, setExtendDays] = useState('30');
  const [extendReason, setExtendReason] = useState('');

  const handleActivate = async () => {
    if (!confirm('Activate this subscription? Papers will be marked as taken.')) return;
    try {
      await activateSubscription.mutateAsync(id);
    } catch (error) {
      console.error('Failed to activate:', error);
    }
  };

  const handleDeactivate = async () => {
    if (!confirm('Deactivate this subscription? Papers will be released.')) return;
    try {
      await deactivateSubscription.mutateAsync({ id, force: false });
    } catch (error) {
      console.error('Failed to deactivate:', error);
    }
  };

  const handleExtend = async () => {
    const days = parseInt(extendDays);
    if (isNaN(days) || days <= 0) return;

    try {
      await extendValidity.mutateAsync({
        id,
        dto: { extendDays: days, reason: extendReason || undefined },
      });
      setExtendDialogOpen(false);
      setExtendDays('30');
      setExtendReason('');
    } catch (error) {
      console.error('Failed to extend validity:', error);
    }
  };

  if (isLoading) {
    return (
      <div className="container mx-auto py-6 px-4">
        <Skeleton className="h-8 w-64 mb-6" />
        <div className="grid gap-6 md:grid-cols-2">
          <Skeleton className="h-48" />
          <Skeleton className="h-48" />
        </div>
      </div>
    );
  }

  if (!subscription) {
    return (
      <div className="container mx-auto py-6 px-4">
        <div className="text-center py-12">
          <Package className="h-12 w-12 mx-auto text-gray-400 mb-4" />
          <p className="text-gray-600">Subscription not found</p>
          <Link href="/admin/subscriptions">
            <Button className="mt-4">Back to List</Button>
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-6 px-4">
      {/* Header */}
      <div className="flex justify-between items-start mb-6">
        <div className="flex items-center gap-4">
          <Link href="/admin/subscriptions">
            <Button variant="ghost" size="icon">
              <ArrowLeft className="h-5 w-5" />
            </Button>
          </Link>
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-2xl font-bold">
                {subscription.name || `Subscription #${subscription.subscriptionId}`}
              </h1>
              <Badge
                variant="outline"
                className={
                  subscription.state === 'ACTIVE'
                    ? 'bg-green-50 text-green-700 border-green-200'
                    : 'bg-gray-50 text-gray-700'
                }
              >
                {subscription.state}
              </Badge>
            </div>
            <p className="text-gray-600 mt-1">
              {subscription.paperCategory?.replace(/_/g, ' ')} •{' '}
              {subscription.paperSubCategory?.replace(/_/g, ' ')}
            </p>
          </div>
        </div>

        <div className="flex gap-2">
          {subscription.state === 'DRAFT' ? (
            <Button onClick={handleActivate} disabled={activateSubscription.isPending}>
              {activateSubscription.isPending ? (
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              ) : (
                <CheckCircle className="mr-2 h-4 w-4" />
              )}
              Activate
            </Button>
          ) : (
            <Button
              variant="outline"
              onClick={handleDeactivate}
              disabled={deactivateSubscription.isPending}
            >
              {deactivateSubscription.isPending ? (
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              ) : (
                <Clock className="mr-2 h-4 w-4" />
              )}
              Deactivate
            </Button>
          )}
          <Button variant="outline" onClick={() => setExtendDialogOpen(true)}>
            <CalendarPlus className="mr-2 h-4 w-4" />
            Extend Validity
          </Button>
          <Link href={`/admin/subscriptions/${id}/edit`}>
            <Button variant="outline">
              <Edit className="mr-2 h-4 w-4" />
              Edit
            </Button>
          </Link>
        </div>
      </div>

      {/* Info Cards */}
      <div className="grid gap-6 md:grid-cols-3 mb-6">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium flex items-center gap-2">
              <Package className="h-4 w-4" />
              Papers
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{subscription.paperCount}</div>
            <p className="text-sm text-gray-500">
              {subscription.testType === 'PAID' ? 'Paid' : 'Free'} papers
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium flex items-center gap-2">
              <Calendar className="h-4 w-4" />
              Validity
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {subscription.isExpired ? (
                <span className="text-red-600">Expired</span>
              ) : (
                <span className={subscription.validityDays < 7 ? 'text-orange-600' : ''}>
                  {subscription.validityDays} days
                </span>
              )}
            </div>
            <p className="text-sm text-gray-500">
              {subscription.validity
                ? new Date(subscription.validity).toLocaleDateString()
                : 'No expiry set'}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium flex items-center gap-2">
              <Users className="h-4 w-4" />
              Subscribers
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{subscription.subscriberCount || 0}</div>
            <p className="text-sm text-gray-500">
              {subscription.activeSubscribers || 0} active
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Description */}
      {subscription.description && (
        <Card className="mb-6">
          <CardHeader>
            <CardTitle className="text-sm font-medium flex items-center gap-2">
              <FileText className="h-4 w-4" />
              Description
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-gray-700">{subscription.description}</p>
          </CardContent>
        </Card>
      )}

      {/* Pricing */}
      {subscription.pricing && Object.keys(subscription.pricing).length > 0 && (
        <Card className="mb-6">
          <CardHeader>
            <CardTitle className="text-sm font-medium flex items-center gap-2">
              <DollarSign className="h-4 w-4" />
              Pricing
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid gap-4 md:grid-cols-3 lg:grid-cols-5">
              {(Object.keys(SUBSCRIPTION_TYPE_LABELS) as SubscriptionType[]).map((type) => {
                const price = subscription.pricing?.[type];
                if (!price) return null;
                return (
                  <div
                    key={type}
                    className={`p-4 rounded-lg border ${
                      price.isActive ? 'border-green-200 bg-green-50' : 'border-gray-200'
                    }`}
                  >
                    <div className="text-sm font-medium mb-2">
                      {SUBSCRIPTION_TYPE_LABELS[type]}
                    </div>
                    {price.discountedPrice < price.originalPrice ? (
                      <>
                        <div className="text-lg font-bold text-green-600">
                          {formatPrice(price.discountedPrice)}
                        </div>
                        <div className="text-sm text-gray-500 line-through">
                          {formatPrice(price.originalPrice)}
                        </div>
                        {price.discountPercentage && (
                          <Badge variant="secondary" className="mt-1">
                            {price.discountPercentage}% off
                          </Badge>
                        )}
                      </>
                    ) : (
                      <div className="text-lg font-bold">
                        {formatPrice(price.originalPrice)}
                      </div>
                    )}
                    {!price.isActive && (
                      <span className="text-xs text-gray-500">Inactive</span>
                    )}
                  </div>
                );
              })}
            </div>
          </CardContent>
        </Card>
      )}

      {/* Papers List */}
      <Card>
        <CardHeader>
          <CardTitle>Papers in Subscription</CardTitle>
          <CardDescription>
            {subscription.paperCount} papers assigned to this subscription
          </CardDescription>
        </CardHeader>
        <CardContent className="p-0">
          {papersLoading ? (
            <div className="p-6 space-y-4">
              {[...Array(3)].map((_, i) => (
                <Skeleton key={i} className="h-12 w-full" />
              ))}
            </div>
          ) : !papers?.length ? (
            <div className="p-12 text-center">
              <FileText className="h-12 w-12 mx-auto text-gray-400 mb-4" />
              <p className="text-gray-600">No papers assigned</p>
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Paper Name</TableHead>
                  <TableHead>Category</TableHead>
                  <TableHead>Questions</TableHead>
                  <TableHead>Score</TableHead>
                  <TableHead>Duration</TableHead>
                  <TableHead>Status</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {papers.map((paper) => (
                  <TableRow key={paper.id}>
                    <TableCell className="font-medium">{paper.paperName}</TableCell>
                    <TableCell>
                      <div className="text-sm">
                        <div>{paper.paperCategory?.replace(/_/g, ' ')}</div>
                        <div className="text-gray-500">
                          {paper.paperSubCategory?.replace(/_/g, ' ')}
                        </div>
                      </div>
                    </TableCell>
                    <TableCell>{paper.totalQuestionCount}</TableCell>
                    <TableCell>{paper.totalScore}</TableCell>
                    <TableCell>{paper.totalTimeMinutes} min</TableCell>
                    <TableCell>
                      <Badge
                        variant="outline"
                        className={
                          paper.paperStateStatus === 'ACTIVE'
                            ? 'bg-green-50 text-green-700'
                            : 'bg-gray-50 text-gray-700'
                        }
                      >
                        {paper.paperStateStatus}
                      </Badge>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      {/* Audit Info */}
      <Card className="mt-6">
        <CardHeader>
          <CardTitle className="text-sm font-medium">Audit Information</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 md:grid-cols-2 text-sm">
            <div>
              <span className="text-gray-500">Created by:</span>{' '}
              {subscription.createdByName || subscription.createdBy || 'N/A'}
            </div>
            <div>
              <span className="text-gray-500">Created at:</span>{' '}
              {subscription.createdAt
                ? new Date(subscription.createdAt).toLocaleString()
                : 'N/A'}
            </div>
            <div>
              <span className="text-gray-500">Last updated by:</span>{' '}
              {subscription.updatedBy || 'N/A'}
            </div>
            <div>
              <span className="text-gray-500">Last updated at:</span>{' '}
              {subscription.updatedAt
                ? new Date(subscription.updatedAt).toLocaleString()
                : 'N/A'}
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Extend Validity Dialog */}
      <Dialog open={extendDialogOpen} onOpenChange={setExtendDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Extend Subscription Validity</DialogTitle>
            <DialogDescription>
              Add more days to the subscription validity period.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="extendDays">Days to Add</Label>
              <Input
                id="extendDays"
                type="number"
                min="1"
                value={extendDays}
                onChange={(e) => setExtendDays(e.target.value)}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="reason">Reason (optional)</Label>
              <Textarea
                id="reason"
                value={extendReason}
                onChange={(e) => setExtendReason(e.target.value)}
                placeholder="Enter reason for extension..."
              />
            </div>
            <div className="text-sm text-gray-500">
              Current expiry:{' '}
              {subscription.validity
                ? new Date(subscription.validity).toLocaleDateString()
                : 'Not set'}
              <br />
              New expiry:{' '}
              {subscription.validity
                ? new Date(
                    subscription.validity + parseInt(extendDays || '0') * 24 * 60 * 60 * 1000
                  ).toLocaleDateString()
                : 'N/A'}
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setExtendDialogOpen(false)}>
              Cancel
            </Button>
            <Button onClick={handleExtend} disabled={extendValidity.isPending}>
              {extendValidity.isPending ? (
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              ) : null}
              Extend Validity
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
