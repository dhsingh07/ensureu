'use client';

import { useState, useEffect, useMemo } from 'react';
import { useParams, useRouter } from 'next/navigation';
import Link from 'next/link';
import {
  ArrowLeft,
  Save,
  Search,
  Plus,
  Minus,
  Clock,
  FileText,
  Loader2,
  AlertCircle,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { Badge } from '@/components/ui/badge';
import { Switch } from '@/components/ui/switch';
import { Checkbox } from '@/components/ui/checkbox';
import { Alert, AlertDescription } from '@/components/ui/alert';
import {
  useSubscription,
  useSubscriptionPapers,
  useAvailablePapers,
  useUpdateSubscription,
} from '@/hooks/use-subscription-admin';
import {
  PaperSelectionDto,
  SubscriptionUpdateDto,
  PriceMetadataDto,
  SubscriptionType,
  SUBSCRIPTION_TYPE_LABELS,
  SUBSCRIPTION_TYPE_DAYS,
  msToMinutes,
} from '@/types/subscription-admin';

export default function EditSubscriptionPage() {
  const params = useParams();
  const router = useRouter();
  const id = params.id as string;

  const { data: subscription, isLoading: subLoading } = useSubscription(id);
  const { data: currentPapers } = useSubscriptionPapers(id);
  const updateSubscription = useUpdateSubscription();

  // Form state
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [validityDays, setValidityDays] = useState(30);
  const [selectedPaperIds, setSelectedPaperIds] = useState<Set<string>>(new Set());
  const [pricing, setPricing] = useState<Record<SubscriptionType, PriceMetadataDto>>({
    DAY: { originalPrice: 0, discountedPrice: 0, isActive: false },
    MONTHLY: { originalPrice: 0, discountedPrice: 0, isActive: false },
    QUATERLY: { originalPrice: 0, discountedPrice: 0, isActive: false },
    HALFYEARLY: { originalPrice: 0, discountedPrice: 0, isActive: false },
    YEARLY: { originalPrice: 0, discountedPrice: 0, isActive: false },
  });
  const [error, setError] = useState<string | null>(null);

  // Paper search
  const [paperSearch, setPaperSearch] = useState('');

  // Fetch available papers (including current ones for this subscription)
  const { data: availablePapersResponse, isLoading: papersLoading } = useAvailablePapers(
    subscription
      ? {
          testType: subscription.testType,
          paperSubCategory: subscription.paperSubCategory,
          excludeSubscriptionId: id,
          search: paperSearch || undefined,
          page: 0,
          size: 100,
        }
      : null
  );

  // Initialize form with subscription data
  useEffect(() => {
    if (subscription) {
      setName(subscription.name || '');
      setDescription(subscription.description || '');
      if (subscription.pricing) {
        setPricing({
          DAY: subscription.pricing.DAY || { originalPrice: 0, discountedPrice: 0, isActive: false },
          MONTHLY: subscription.pricing.MONTHLY || { originalPrice: 0, discountedPrice: 0, isActive: false },
          QUATERLY: subscription.pricing.QUATERLY || { originalPrice: 0, discountedPrice: 0, isActive: false },
          HALFYEARLY: subscription.pricing.HALFYEARLY || { originalPrice: 0, discountedPrice: 0, isActive: false },
          YEARLY: subscription.pricing.YEARLY || { originalPrice: 0, discountedPrice: 0, isActive: false },
        });
      }
      // Calculate validity days from validity timestamp
      if (subscription.validity && subscription.createdDate) {
        const days = Math.ceil(
          (subscription.validity - subscription.createdDate) / (24 * 60 * 60 * 1000)
        );
        setValidityDays(days > 0 ? days : 30);
      }
    }
  }, [subscription]);

  // Initialize selected papers from current subscription papers
  useEffect(() => {
    if (currentPapers) {
      setSelectedPaperIds(new Set(currentPapers.map((p) => p.id)));
    }
  }, [currentPapers]);

  // Combine available papers with current papers for display
  const allPapers = useMemo(() => {
    const paperMap = new Map<string, PaperSelectionDto>();

    // Add current subscription papers
    currentPapers?.forEach((p) => {
      paperMap.set(p.id, { ...p, isSelected: true });
    });

    // Add available papers (they should not include papers from other subscriptions)
    availablePapersResponse?.content?.forEach((p) => {
      if (!paperMap.has(p.id)) {
        paperMap.set(p.id, { ...p, isSelected: false });
      }
    });

    return Array.from(paperMap.values());
  }, [currentPapers, availablePapersResponse]);

  // Filter papers based on search
  const filteredPapers = useMemo(() => {
    if (!paperSearch) return allPapers;
    const search = paperSearch.toLowerCase();
    return allPapers.filter(
      (p) =>
        p.paperName.toLowerCase().includes(search) ||
        p.paperSubCategoryName?.toLowerCase().includes(search)
    );
  }, [allPapers, paperSearch]);

  // Selected and available papers lists
  const selectedPapers = useMemo(
    () => filteredPapers.filter((p) => selectedPaperIds.has(p.id)),
    [filteredPapers, selectedPaperIds]
  );

  const availablePapers = useMemo(
    () => filteredPapers.filter((p) => !selectedPaperIds.has(p.id)),
    [filteredPapers, selectedPaperIds]
  );

  const handlePaperSelect = (paperId: string) => {
    setSelectedPaperIds((prev) => {
      const next = new Set(prev);
      next.add(paperId);
      return next;
    });
  };

  const handlePaperDeselect = (paperId: string) => {
    setSelectedPaperIds((prev) => {
      const next = new Set(prev);
      next.delete(paperId);
      return next;
    });
  };

  const handlePriceChange = (
    type: SubscriptionType,
    field: 'originalPrice' | 'discountedPrice',
    value: number
  ) => {
    setPricing((prev) => ({
      ...prev,
      [type]: {
        ...prev[type],
        [field]: value,
        discountPercentage:
          field === 'discountedPrice' && prev[type].originalPrice > 0
            ? Math.round(((prev[type].originalPrice - value) / prev[type].originalPrice) * 100)
            : prev[type].discountPercentage,
      },
    }));
  };

  const handlePriceActiveToggle = (type: SubscriptionType) => {
    setPricing((prev) => ({
      ...prev,
      [type]: { ...prev[type], isActive: !prev[type].isActive },
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!name.trim()) {
      setError('Name is required');
      return;
    }

    if (selectedPaperIds.size === 0) {
      setError('Please select at least one paper');
      return;
    }

    // Calculate new validity timestamp
    const createdDate = subscription?.createdDate || Date.now();
    const validity = createdDate + validityDays * 24 * 60 * 60 * 1000;

    const dto: SubscriptionUpdateDto = {
      name: name.trim(),
      description: description.trim() || undefined,
      paperIds: Array.from(selectedPaperIds),
      createdDate,
      validity,
      pricing: subscription?.testType === 'PAID' ? pricing : undefined,
    };

    try {
      await updateSubscription.mutateAsync({ id, dto });
      router.push(`/admin/subscriptions/${id}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to update subscription');
    }
  };

  if (subLoading) {
    return (
      <div className="container mx-auto py-6 px-4">
        <Skeleton className="h-8 w-64 mb-6" />
        <Skeleton className="h-96" />
      </div>
    );
  }

  if (!subscription) {
    return (
      <div className="container mx-auto py-6 px-4 text-center py-12">
        <FileText className="h-12 w-12 mx-auto text-gray-400 mb-4" />
        <p className="text-gray-600">Subscription not found</p>
        <Link href="/admin/subscriptions">
          <Button className="mt-4">Back to List</Button>
        </Link>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-6 px-4">
      <form onSubmit={handleSubmit}>
        {/* Header */}
        <div className="flex justify-between items-center mb-6">
          <div className="flex items-center gap-4">
            <Link href={`/admin/subscriptions/${id}`}>
              <Button type="button" variant="ghost" size="icon">
                <ArrowLeft className="h-5 w-5" />
              </Button>
            </Link>
            <div>
              <h1 className="text-2xl font-bold">Edit Subscription</h1>
              <p className="text-gray-600">
                {subscription.paperCategory?.replace(/_/g, ' ')} •{' '}
                {subscription.paperSubCategory?.replace(/_/g, ' ')} •{' '}
                {subscription.testType}
              </p>
            </div>
          </div>
          <div className="flex gap-2">
            <Link href={`/admin/subscriptions/${id}`}>
              <Button type="button" variant="outline">
                Cancel
              </Button>
            </Link>
            <Button type="submit" disabled={updateSubscription.isPending}>
              {updateSubscription.isPending ? (
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              ) : (
                <Save className="mr-2 h-4 w-4" />
              )}
              Save Changes
            </Button>
          </div>
        </div>

        {error && (
          <Alert variant="destructive" className="mb-6">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}

        <div className="grid gap-6 lg:grid-cols-3">
          {/* Left Column - Details */}
          <div className="lg:col-span-1 space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Basic Details</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div>
                  <Label htmlFor="name">Name *</Label>
                  <Input
                    id="name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="e.g., SSC CGL Premium Pack"
                    required
                  />
                </div>
                <div>
                  <Label htmlFor="description">Description</Label>
                  <Textarea
                    id="description"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    placeholder="Describe this subscription package..."
                    rows={3}
                  />
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Clock className="h-4 w-4" />
                  Validity
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div>
                  <Label htmlFor="validityDays">Validity Period (Days)</Label>
                  <Input
                    id="validityDays"
                    type="number"
                    min="1"
                    value={validityDays}
                    onChange={(e) => setValidityDays(parseInt(e.target.value) || 30)}
                  />
                  <p className="text-sm text-gray-500 mt-1">
                    Expires on:{' '}
                    {new Date(
                      (subscription.createdDate || Date.now()) +
                        validityDays * 24 * 60 * 60 * 1000
                    ).toLocaleDateString()}
                  </p>
                </div>
              </CardContent>
            </Card>

            {/* Pricing for PAID subscriptions */}
            {subscription.testType === 'PAID' && (
              <Card>
                <CardHeader>
                  <CardTitle>Pricing</CardTitle>
                  <CardDescription>Set prices for each subscription duration</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  {(Object.keys(SUBSCRIPTION_TYPE_LABELS) as SubscriptionType[]).map((type) => (
                    <div key={type} className="border rounded-lg p-3">
                      <div className="flex items-center justify-between mb-2">
                        <span className="font-medium">{SUBSCRIPTION_TYPE_LABELS[type]}</span>
                        <div className="flex items-center gap-2">
                          <span className="text-sm text-gray-500">Active</span>
                          <Switch
                            checked={pricing[type].isActive}
                            onCheckedChange={() => handlePriceActiveToggle(type)}
                          />
                        </div>
                      </div>
                      <div className="grid grid-cols-2 gap-2">
                        <div>
                          <Label className="text-xs">Original (₹)</Label>
                          <Input
                            type="number"
                            min="0"
                            value={pricing[type].originalPrice}
                            onChange={(e) =>
                              handlePriceChange(type, 'originalPrice', parseFloat(e.target.value) || 0)
                            }
                          />
                        </div>
                        <div>
                          <Label className="text-xs">Discounted (₹)</Label>
                          <Input
                            type="number"
                            min="0"
                            value={pricing[type].discountedPrice}
                            onChange={(e) =>
                              handlePriceChange(type, 'discountedPrice', parseFloat(e.target.value) || 0)
                            }
                          />
                        </div>
                      </div>
                    </div>
                  ))}
                </CardContent>
              </Card>
            )}
          </div>

          {/* Right Column - Paper Selection */}
          <div className="lg:col-span-2">
            <Card>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <div>
                    <CardTitle>Paper Selection</CardTitle>
                    <CardDescription>
                      {selectedPaperIds.size} papers selected
                    </CardDescription>
                  </div>
                  <div className="relative w-64">
                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                    <Input
                      placeholder="Search papers..."
                      value={paperSearch}
                      onChange={(e) => setPaperSearch(e.target.value)}
                      className="pl-9"
                    />
                  </div>
                </div>
              </CardHeader>
              <CardContent>
                <div className="grid md:grid-cols-2 gap-4">
                  {/* Available Papers */}
                  <div>
                    <h3 className="font-medium mb-3 flex items-center gap-2">
                      Available Papers
                      <Badge variant="secondary">{availablePapers.length}</Badge>
                    </h3>
                    <div className="border rounded-lg max-h-96 overflow-y-auto">
                      {papersLoading ? (
                        <div className="p-4 space-y-2">
                          {[...Array(3)].map((_, i) => (
                            <Skeleton key={i} className="h-16" />
                          ))}
                        </div>
                      ) : availablePapers.length === 0 ? (
                        <div className="p-8 text-center text-gray-500">
                          No available papers
                        </div>
                      ) : (
                        availablePapers.map((paper) => (
                          <div
                            key={paper.id}
                            className="p-3 border-b last:border-b-0 hover:bg-gray-50 flex items-center justify-between"
                          >
                            <div className="flex-1 min-w-0">
                              <div className="font-medium text-sm truncate">
                                {paper.paperName}
                              </div>
                              <div className="text-xs text-gray-500">
                                {paper.totalQuestionCount} Q • {paper.totalTimeMinutes} min
                              </div>
                            </div>
                            <Button
                              type="button"
                              variant="ghost"
                              size="sm"
                              onClick={() => handlePaperSelect(paper.id)}
                            >
                              <Plus className="h-4 w-4" />
                            </Button>
                          </div>
                        ))
                      )}
                    </div>
                  </div>

                  {/* Selected Papers */}
                  <div>
                    <h3 className="font-medium mb-3 flex items-center gap-2">
                      Selected Papers
                      <Badge variant="default">{selectedPapers.length}</Badge>
                    </h3>
                    <div className="border rounded-lg max-h-96 overflow-y-auto">
                      {selectedPapers.length === 0 ? (
                        <div className="p-8 text-center text-gray-500">
                          No papers selected
                        </div>
                      ) : (
                        selectedPapers.map((paper) => (
                          <div
                            key={paper.id}
                            className="p-3 border-b last:border-b-0 bg-green-50 flex items-center justify-between"
                          >
                            <div className="flex-1 min-w-0">
                              <div className="font-medium text-sm truncate">
                                {paper.paperName}
                              </div>
                              <div className="text-xs text-gray-500">
                                {paper.totalQuestionCount} Q • {paper.totalTimeMinutes} min
                              </div>
                            </div>
                            <Button
                              type="button"
                              variant="ghost"
                              size="sm"
                              onClick={() => handlePaperDeselect(paper.id)}
                            >
                              <Minus className="h-4 w-4 text-red-500" />
                            </Button>
                          </div>
                        ))
                      )}
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </form>
    </div>
  );
}
