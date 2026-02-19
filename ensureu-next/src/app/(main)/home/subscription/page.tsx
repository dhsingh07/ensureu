'use client';

import { useState, useMemo } from 'react';
import { useRouter } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Skeleton } from '@/components/ui/skeleton';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  usePaidSubscriptions,
  useAllUserSubscriptions,
  useAllSubscriptionHistory,
  useAllPasses,
  usePurchaseSubscription,
  formatPrice,
  getRemainingDays,
  isSubscriptionExpired,
  calculateValidity,
} from '@/hooks/use-subscription';
import { useCategoryStore } from '@/stores/category-store';
import type { SubscriptionItem, SubscriptionType, UserSubscription } from '@/types/subscription';
import type { PaperCategory, PaperSubCategory } from '@/types/paper';
import {
  Crown,
  Clock,
  CheckCircle,
  XCircle,
  Calendar,
  FileText,
  CreditCard,
  Sparkles,
  ArrowRight,
  Loader2,
  AlertCircle,
  RefreshCcw,
  ShoppingBag,
} from 'lucide-react';

const SUBSCRIPTION_LABELS: Record<SubscriptionType, string> = {
  DAY: 'Daily',
  MONTHLY: 'Monthly',
  QUATERLY: 'Quarterly',
  HALFYEARLY: 'Half Yearly',
  YEARLY: 'Yearly',
};

// Helper to format dates safely (handles null, 0, and invalid timestamps)
const formatDate = (timestamp: number | undefined | null): string => {
  if (!timestamp || timestamp <= 0) return '--';
  // Check if timestamp is in seconds (< year 2000 in ms) vs milliseconds
  const adjustedTimestamp = timestamp < 946684800000 ? timestamp * 1000 : timestamp;
  const date = new Date(adjustedTimestamp);
  // Validate date
  if (isNaN(date.getTime()) || date.getFullYear() < 2000) return '--';
  return date.toLocaleDateString();
};

// Helper to convert display name to enum format (e.g., "SSC CGL" -> "SSC_CGL")
const toEnumFormat = (displayName: string): string => {
  return displayName
    .replace(/\s+/g, '_')
    .replace(/-/g, '')
    .toUpperCase();
};

// Helper to derive category from subscription
const getCategoryFromSubscription = (sub: SubscriptionItem): { root: PaperCategory; child: PaperSubCategory } => {
  const subCat = toEnumFormat(sub.paperSubCategory);
  if (subCat.startsWith('SSC_CGL')) {
    return { root: 'SSC_CGL' as PaperCategory, child: subCat as PaperSubCategory };
  }
  if (subCat.startsWith('SSC_CPO')) {
    return { root: 'SSC_CPO' as PaperCategory, child: subCat as PaperSubCategory };
  }
  if (subCat.startsWith('SSC_CHSL')) {
    return { root: 'SSC_CHSL' as PaperCategory, child: subCat as PaperSubCategory };
  }
  if (subCat.startsWith('BANK_PO')) {
    return { root: 'BANK_PO' as PaperCategory, child: subCat as PaperSubCategory };
  }
  return { root: 'SSC_CGL' as PaperCategory, child: 'SSC_CGL_TIER1' as PaperSubCategory };
};

interface CombinedPlan {
  id: string | number;
  paperCategory: string;
  paperSubCategory: string;
  paperType?: string;
  description?: string;
  testCount?: number;
  status: 'active' | 'expired' | 'available';
  subscriptionType?: SubscriptionType;
  remainingDays?: number;
  validity?: number;
  // For available plans
  subscriptionItem?: SubscriptionItem;
  startingPrice?: number;
  // For subscribed plans
  userSubscription?: UserSubscription;
}

export default function SubscriptionPage() {
  const router = useRouter();
  const setCategories = useCategoryStore((state) => state.setCategories);
  const [selectedSubscription, setSelectedSubscription] = useState<SubscriptionItem | null>(null);
  const [selectedPeriod, setSelectedPeriod] = useState<SubscriptionType>('MONTHLY');

  // Fetch subscription data - fetch all paper types
  const { data: paidSubscriptions, isLoading: loadingPaid } = usePaidSubscriptions();
  const { data: userSubscriptions, isLoading: loadingUserSubs } = useAllUserSubscriptions();
  const { data: subscriptionHistory, isLoading: loadingHistory } = useAllSubscriptionHistory();
  const { data: passes, isLoading: loadingPasses } = useAllPasses();

  const purchaseMutation = usePurchaseSubscription();

  // Build a map of user's subscriptions by category key
  const userSubscriptionMap = useMemo(() => {
    const map = new Map<string, UserSubscription>();
    (userSubscriptions || []).forEach((sub) => {
      const key = `${sub.paperCategory}::${sub.paperSubCategory}`;
      map.set(key, sub);
    });
    return map;
  }, [userSubscriptions]);

  // Combine subscribed and available plans into one list
  const combinedPlans = useMemo(() => {
    const plans: CombinedPlan[] = [];
    const processedKeys = new Set<string>();

    // First, add all user subscriptions (active or expired)
    (userSubscriptions || []).forEach((sub) => {
      const key = `${sub.paperCategory}::${sub.paperSubCategory}`;
      processedKeys.add(key);
      // Check if subscription has valid validity (not 0 or undefined)
      const hasValidValidity = sub.validity && sub.validity > 0;
      const expired = hasValidValidity ? isSubscriptionExpired(sub.validity) : true;
      const testCount = sub.paperCount || sub.listOfPaperInfo?.length || sub.paperIds?.length || 0;
      plans.push({
        id: sub.id,
        paperCategory: String(sub.paperCategory).replace(/_/g, ' '),
        paperSubCategory: String(sub.paperSubCategory || '').replace(/_/g, ' '),
        paperType: sub.paperType,
        description: sub.description,
        status: expired ? 'expired' : 'active',
        subscriptionType: sub.subscriptionType,
        remainingDays: hasValidValidity ? getRemainingDays(sub.validity) : 0,
        validity: sub.validity,
        testCount,
        userSubscription: sub,
      });
    });

    // Then, add available plans that user hasn't subscribed to
    (paidSubscriptions || []).forEach((sub) => {
      const categoryKey = toEnumFormat(sub.paperCategory);
      const subCategoryKey = toEnumFormat(sub.paperSubCategory);
      const key = `${categoryKey}::${subCategoryKey}`;

      if (!processedKeys.has(key)) {
        processedKeys.add(key);
        const prices = sub.mapOfSubTypeVsPrice;
        const firstPrice = prices ? Object.values(prices)[0] : null;
        const displayPrice = typeof firstPrice === 'object'
          ? (firstPrice?.discountedPrice || firstPrice?.price || 0)
          : (firstPrice || 0);

        plans.push({
          id: sub.id,
          paperCategory: sub.paperCategory,
          paperSubCategory: sub.paperSubCategory?.replace(/_/g, ' '),
          paperType: sub.paperType,
          description: sub.description,
          status: 'available',
          testCount: sub.paperInfoList?.length || 0,
          subscriptionItem: sub,
          startingPrice: displayPrice,
        });
      }
    });

    return plans;
  }, [userSubscriptions, paidSubscriptions]);

  // Separate plans by status for tabs
  const activePlans = combinedPlans.filter((p) => p.status === 'active');
  const expiredPlans = combinedPlans.filter((p) => p.status === 'expired');
  const availablePlans = combinedPlans.filter((p) => p.status === 'available');

  const isLoading = loadingPaid || loadingUserSubs;

  // Handle purchase
  const handlePurchase = async () => {
    if (!selectedSubscription) return;

    const priceInfo = selectedSubscription.mapOfSubTypeVsPrice?.[selectedPeriod];
    const actualPrice = typeof priceInfo === 'object'
      ? (priceInfo?.discountedPrice || priceInfo?.price || 0)
      : (priceInfo || 0);

    await purchaseMutation.mutateAsync({
      listOfSubscriptionIds: selectedSubscription.listOfSubscriptionIds || [selectedSubscription.id],
      subscriptionType: selectedPeriod,
      actualPrice: actualPrice,
      validity: calculateValidity(selectedPeriod),
      paperType: selectedSubscription.paperType,
      paperCategory: toEnumFormat(selectedSubscription.paperCategory),
      paperSubCategory: toEnumFormat(selectedSubscription.paperSubCategory),
      testType: 'PAID',
    });

    const { root, child } = getCategoryFromSubscription(selectedSubscription);
    setCategories(root, child, true);

    setSelectedSubscription(null);
    router.push('/home');
  };

  const handleRenew = (plan: CombinedPlan) => {
    // Find the corresponding paid subscription for renewal
    const paidSub = paidSubscriptions?.find((sub) => {
      const catKey = toEnumFormat(sub.paperCategory);
      const subCatKey = toEnumFormat(sub.paperSubCategory);
      return catKey === plan.userSubscription?.paperCategory &&
             subCatKey === plan.userSubscription?.paperSubCategory;
    });

    if (paidSub) {
      setSelectedSubscription(paidSub);
      const firstType = paidSub.mapOfSubTypeVsPrice
        ? (Object.keys(paidSub.mapOfSubTypeVsPrice)[0] as SubscriptionType)
        : 'MONTHLY';
      setSelectedPeriod(firstType);
    }
  };

  const handleStartTests = (plan: CombinedPlan) => {
    if (plan.userSubscription) {
      const catKey = plan.userSubscription.paperCategory;
      const subCatKey = plan.userSubscription.paperSubCategory;
      // Navigate to home with this category selected
      if (catKey.startsWith('SSC_CGL')) {
        setCategories('SSC_CGL', subCatKey as PaperSubCategory, true);
      } else if (catKey.startsWith('SSC_CPO')) {
        setCategories('SSC_CPO', subCatKey as PaperSubCategory, true);
      } else if (catKey.startsWith('SSC_CHSL')) {
        setCategories('SSC_CHSL', subCatKey as PaperSubCategory, true);
      } else if (catKey.startsWith('BANK_PO')) {
        setCategories('BANK_PO', subCatKey as PaperSubCategory, true);
      }
      router.push('/home');
    }
  };

  const renderPlanCard = (plan: CombinedPlan) => {
    if (plan.status === 'active') {
      return (
        <Card key={plan.id} className="border-green-200 bg-gradient-to-br from-white to-green-50/50">
          <CardContent className="p-6">
            <div className="flex items-start justify-between mb-3">
              <Badge className="bg-green-500">
                <CheckCircle className="h-3 w-3 mr-1" />
                Active
              </Badge>
              <Badge variant="outline">{plan.subscriptionType}</Badge>
            </div>
            <h3 className="font-bold text-xl text-slate-900">{plan.paperCategory}</h3>
            <p className="text-sm text-slate-500 mb-3">{plan.paperSubCategory}</p>
            <div className="flex flex-wrap items-center gap-4 text-sm mb-4">
              <div className="flex items-center gap-1 text-slate-500">
                <FileText className="h-4 w-4" />
                {plan.testCount || '--'} tests
              </div>
              <div className="flex items-center gap-1 text-green-600">
                <Clock className="h-4 w-4" />
                {plan.remainingDays} days left
              </div>
              <div className="flex items-center gap-1 text-slate-500">
                <Calendar className="h-4 w-4" />
                Expires: {formatDate(plan.validity)}
              </div>
            </div>
            <Button className="w-full" onClick={() => handleStartTests(plan)}>
              Start Tests
              <ArrowRight className="h-4 w-4 ml-2" />
            </Button>
          </CardContent>
        </Card>
      );
    }

    if (plan.status === 'expired') {
      return (
        <Card key={plan.id} className="border-red-200 bg-gradient-to-br from-white to-red-50/50">
          <CardContent className="p-6">
            <div className="flex items-start justify-between mb-3">
              <Badge variant="destructive">
                <XCircle className="h-3 w-3 mr-1" />
                Expired
              </Badge>
              <Badge variant="outline">{plan.subscriptionType}</Badge>
            </div>
            <h3 className="font-bold text-xl text-slate-900">{plan.paperCategory}</h3>
            <p className="text-sm text-slate-500 mb-3">{plan.paperSubCategory}</p>
            <div className="flex items-center gap-4 text-sm mb-4">
              <div className="flex items-center gap-1 text-slate-500">
                <FileText className="h-4 w-4" />
                {plan.testCount || '--'} tests
              </div>
              <div className="flex items-center gap-1 text-red-600">
                <Calendar className="h-4 w-4" />
                Expired on {formatDate(plan.validity)}
              </div>
            </div>
            <Button
              className="w-full bg-red-600 hover:bg-red-700"
              onClick={() => handleRenew(plan)}
            >
              <RefreshCcw className="h-4 w-4 mr-2" />
              Renew Subscription
            </Button>
          </CardContent>
        </Card>
      );
    }

    // Available plan
    return (
      <Card key={plan.id} className="border-orange-200 bg-gradient-to-br from-white to-orange-50/50 hover:shadow-lg transition-shadow">
        <CardContent className="p-6">
          <div className="flex items-start justify-between mb-3">
            <Badge className="bg-orange-500">
              <ShoppingBag className="h-3 w-3 mr-1" />
              Available
            </Badge>
          </div>
          <h3 className="font-bold text-xl text-slate-900">{plan.paperCategory}</h3>
          <p className="text-sm text-slate-500 mb-2">{plan.paperSubCategory}</p>
          <p className="text-sm text-slate-600 mb-3">
            {plan.description || `${plan.testCount} tests included`}
          </p>
          <div className="flex items-center gap-4 text-sm mb-4">
            <div className="flex items-center gap-1 text-slate-500">
              <FileText className="h-4 w-4" />
              {plan.testCount} tests
            </div>
          </div>
          <div className="flex items-baseline gap-1 mb-4">
            <span className="text-2xl font-bold text-orange-600">
              {formatPrice(plan.startingPrice || 0)}
            </span>
            <span className="text-slate-500 text-sm">starting</span>
          </div>
          <Button
            className="w-full bg-orange-600 hover:bg-orange-700"
            onClick={() => {
              if (plan.subscriptionItem) {
                setSelectedSubscription(plan.subscriptionItem);
                const prices = plan.subscriptionItem.mapOfSubTypeVsPrice;
                const firstType = prices ? (Object.keys(prices)[0] as SubscriptionType) : 'MONTHLY';
                setSelectedPeriod(firstType);
              }
            }}
          >
            <Crown className="h-4 w-4 mr-2" />
            Subscribe Now
          </Button>
        </CardContent>
      </Card>
    );
  };

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Page Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-slate-900 flex items-center gap-2">
          <Crown className="h-8 w-8 text-orange-500" />
          Subscriptions
        </h1>
        <p className="text-slate-600 mt-1">
          Manage your subscriptions and unlock premium content
        </p>
      </div>

      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <div className="p-2 bg-green-100 rounded-lg">
              <CheckCircle className="h-5 w-5 text-green-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-slate-900">{activePlans.length}</p>
              <p className="text-sm text-slate-500">Active Plans</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <div className="p-2 bg-red-100 rounded-lg">
              <XCircle className="h-5 w-5 text-red-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-slate-900">{expiredPlans.length}</p>
              <p className="text-sm text-slate-500">Expired</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <div className="p-2 bg-orange-100 rounded-lg">
              <ShoppingBag className="h-5 w-5 text-orange-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-slate-900">{availablePlans.length}</p>
              <p className="text-sm text-slate-500">Available</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 flex items-center gap-3">
            <div className="p-2 bg-purple-100 rounded-lg">
              <Sparkles className="h-5 w-5 text-purple-600" />
            </div>
            <div>
              <p className="text-2xl font-bold text-slate-900">{passes?.length || 0}</p>
              <p className="text-sm text-slate-500">Passes</p>
            </div>
          </CardContent>
        </Card>
      </div>

      <Tabs id="subscription-tabs" defaultValue="all" className="space-y-6">
        <TabsList className="grid w-full grid-cols-4 lg:w-[500px]">
          <TabsTrigger value="all">All Plans ({combinedPlans.length})</TabsTrigger>
          <TabsTrigger value="subscribed">Subscribed ({activePlans.length + expiredPlans.length})</TabsTrigger>
          <TabsTrigger value="available">Available ({availablePlans.length})</TabsTrigger>
          <TabsTrigger value="history">History</TabsTrigger>
        </TabsList>

        {/* All Plans */}
        <TabsContent value="all" className="space-y-6">
          {isLoading ? (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[1, 2, 3, 4, 5, 6].map((i) => (
                <Card key={i}>
                  <CardContent className="p-6">
                    <Skeleton className="h-6 w-20 mb-3" />
                    <Skeleton className="h-6 w-32 mb-2" />
                    <Skeleton className="h-4 w-48 mb-4" />
                    <Skeleton className="h-10 w-full" />
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : combinedPlans.length > 0 ? (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {combinedPlans.map(renderPlanCard)}
            </div>
          ) : (
            <Card>
              <CardContent className="py-12 text-center">
                <Crown className="h-12 w-12 mx-auto mb-4 text-slate-300" />
                <p className="text-slate-500">No subscription plans available</p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        {/* Subscribed Plans (Active + Expired) */}
        <TabsContent value="subscribed" className="space-y-6">
          {loadingUserSubs ? (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[1, 2, 3].map((i) => (
                <Card key={i}>
                  <CardContent className="p-6">
                    <Skeleton className="h-6 w-20 mb-3" />
                    <Skeleton className="h-6 w-32 mb-2" />
                    <Skeleton className="h-4 w-48 mb-4" />
                    <Skeleton className="h-10 w-full" />
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : activePlans.length > 0 || expiredPlans.length > 0 ? (
            <div className="space-y-6">
              {activePlans.length > 0 && (
                <>
                  <h3 className="text-lg font-semibold text-slate-900 flex items-center gap-2">
                    <CheckCircle className="h-5 w-5 text-green-500" />
                    Active Subscriptions
                  </h3>
                  <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {activePlans.map(renderPlanCard)}
                  </div>
                </>
              )}
              {expiredPlans.length > 0 && (
                <>
                  <h3 className="text-lg font-semibold text-slate-900 flex items-center gap-2 mt-8">
                    <XCircle className="h-5 w-5 text-red-500" />
                    Expired Subscriptions
                  </h3>
                  <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {expiredPlans.map(renderPlanCard)}
                  </div>
                </>
              )}
            </div>
          ) : (
            <Card>
              <CardContent className="py-12 text-center">
                <Crown className="h-12 w-12 mx-auto mb-4 text-slate-300" />
                <p className="text-slate-500 mb-4">No subscriptions yet</p>
                <Button onClick={() => {
                  const tabTrigger = document.querySelector('[data-state="inactive"][value="available"]') as HTMLButtonElement;
                  tabTrigger?.click();
                }}>
                  Browse Available Plans
                </Button>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        {/* Available Plans */}
        <TabsContent value="available" className="space-y-6">
          {loadingPaid ? (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[1, 2, 3].map((i) => (
                <Card key={i}>
                  <CardContent className="p-6">
                    <Skeleton className="h-6 w-20 mb-3" />
                    <Skeleton className="h-6 w-32 mb-2" />
                    <Skeleton className="h-4 w-48 mb-4" />
                    <Skeleton className="h-10 w-full" />
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : availablePlans.length > 0 ? (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {availablePlans.map(renderPlanCard)}
            </div>
          ) : (
            <Card>
              <CardContent className="py-12 text-center">
                <CheckCircle className="h-12 w-12 mx-auto mb-4 text-green-300" />
                <p className="text-slate-500">You've subscribed to all available plans!</p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        {/* Purchase History */}
        <TabsContent value="history" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg flex items-center gap-2">
                <CreditCard className="h-5 w-5" />
                Purchase History
              </CardTitle>
              <CardDescription>
                Your subscription purchase history
              </CardDescription>
            </CardHeader>
            <CardContent>
              {loadingHistory ? (
                <div className="space-y-4">
                  {[1, 2, 3].map((i) => (
                    <div key={i} className="flex items-center justify-between p-4 border rounded-lg">
                      <div className="flex-1">
                        <Skeleton className="h-5 w-32 mb-2" />
                        <Skeleton className="h-4 w-48" />
                      </div>
                      <Skeleton className="h-6 w-20" />
                    </div>
                  ))}
                </div>
              ) : subscriptionHistory && subscriptionHistory.length > 0 ? (
                <div className="space-y-4">
                  {subscriptionHistory.map((record) => (
                    <div
                      key={record.id}
                      className="flex items-center justify-between p-4 border rounded-lg hover:bg-slate-50"
                    >
                      <div className="flex-1">
                        <div className="flex items-center gap-2">
                          <h4 className="font-medium text-slate-900">
                            {SUBSCRIPTION_LABELS[record.subscriptionType] || record.subscriptionType} Subscription
                          </h4>
                          <Badge
                            variant={record.purchaseStatus === 'COMPLETED' ? 'default' : 'secondary'}
                          >
                            {record.purchaseStatus}
                          </Badge>
                        </div>
                        <div className="flex items-center gap-4 mt-1 text-sm text-slate-500">
                          <span className="flex items-center gap-1">
                            <Calendar className="h-3 w-3" />
                            {formatDate(record.createdDate)}
                          </span>
                          <span className="flex items-center gap-1">
                            <Clock className="h-3 w-3" />
                            Valid until {formatDate(record.validity)}
                          </span>
                        </div>
                      </div>
                      <div className="text-right">
                        <span className="font-bold text-lg text-slate-900">
                          {formatPrice(record.actualPrice)}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-12">
                  <CreditCard className="h-12 w-12 mx-auto mb-4 text-slate-300" />
                  <p className="text-slate-500">No purchase history</p>
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      {/* Purchase Dialog */}
      <Dialog open={!!selectedSubscription} onOpenChange={() => setSelectedSubscription(null)}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>Choose Your Plan</DialogTitle>
            <DialogDescription>
              {selectedSubscription?.paperCategory} - {selectedSubscription?.paperSubCategory?.replace(/_/g, ' ')}
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-4">
            {/* Duration Options */}
            <div className="grid grid-cols-2 gap-3">
              {selectedSubscription?.mapOfSubTypeVsPrice &&
                Object.entries(selectedSubscription.mapOfSubTypeVsPrice).map(([period, priceData]) => {
                  const priceInfo = typeof priceData === 'object' ? priceData : null;
                  const displayPrice = priceInfo?.discountedPrice || priceInfo?.price || 0;

                  return (
                    <button
                      key={period}
                      onClick={() => setSelectedPeriod(period as SubscriptionType)}
                      className={`p-4 rounded-lg border-2 text-left transition-all ${
                        selectedPeriod === period
                          ? 'border-primary bg-primary/5'
                          : 'border-slate-200 hover:border-slate-300'
                      }`}
                    >
                      <p className="font-semibold text-slate-900">
                        {SUBSCRIPTION_LABELS[period as SubscriptionType] || period}
                      </p>
                      <div className="flex items-baseline gap-1">
                        <span className="text-xl font-bold text-primary">{formatPrice(displayPrice)}</span>
                        {priceInfo?.discountedPrice && priceInfo.discountedPrice < priceInfo.price && (
                          <span className="text-sm text-slate-400 line-through">{formatPrice(priceInfo.price)}</span>
                        )}
                      </div>
                      {priceInfo?.discountPercentage && priceInfo.discountPercentage > 0 && (
                        <Badge variant="secondary" className="mt-1">{priceInfo.discountPercentage}% off</Badge>
                      )}
                    </button>
                  );
                })}
            </div>

            {/* Summary */}
            {(() => {
              const selectedPriceData = selectedSubscription?.mapOfSubTypeVsPrice?.[selectedPeriod];
              const priceInfo = typeof selectedPriceData === 'object' ? selectedPriceData : null;
              const totalPrice = priceInfo?.discountedPrice || priceInfo?.price || 0;

              return (
                <div className="p-4 bg-slate-50 rounded-lg">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-slate-600">Selected Plan:</span>
                    <span className="font-semibold">
                      {SUBSCRIPTION_LABELS[selectedPeriod] || selectedPeriod}
                    </span>
                  </div>
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-slate-600">Tests Included:</span>
                    <span className="font-semibold">
                      {selectedSubscription?.paperInfoList?.length || 0}
                    </span>
                  </div>
                  {priceInfo?.discountPercentage && priceInfo.discountPercentage > 0 && (
                    <div className="flex items-center justify-between mb-2 text-green-600">
                      <span>Discount:</span>
                      <span className="font-semibold">{priceInfo.discountPercentage}%</span>
                    </div>
                  )}
                  <div className="flex items-center justify-between pt-2 border-t">
                    <span className="text-slate-900 font-semibold">Total:</span>
                    <span className="text-2xl font-bold text-primary">
                      {formatPrice(totalPrice)}
                    </span>
                  </div>
                </div>
              );
            })()}

            <Button
              className="w-full"
              size="lg"
              onClick={handlePurchase}
              disabled={purchaseMutation.isPending}
            >
              {purchaseMutation.isPending ? (
                <>
                  <Loader2 className="h-4 w-4 animate-spin mr-2" />
                  Processing...
                </>
              ) : (
                <>
                  <Crown className="h-4 w-4 mr-2" />
                  Subscribe Now
                </>
              )}
            </Button>

            <p className="text-xs text-center text-slate-500">
              By subscribing, you agree to our Terms of Service and Privacy Policy
            </p>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
