'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { ArrowLeft, Save, Send, Search, ChevronRight, ChevronLeft, X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Checkbox } from '@/components/ui/checkbox';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useCreateSubscription, useAvailablePapers } from '@/hooks/use-subscription-admin';
import {
  SubscriptionCreateDto,
  SubscriptionState,
  TestType,
  SubscriptionType,
  PriceMetadataDto,
  PaperSelectionDto,
  SUBSCRIPTION_TYPE_LABELS,
} from '@/types/subscription-admin';
import { PaperType, PaperCategory, PaperSubCategory } from '@/types/paper';

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

const PAPER_SUBCATEGORIES: Record<PaperCategory, { value: PaperSubCategory; label: string }[]> = {
  SSC_CGL: [
    { value: 'SSC_CGL_TIER1', label: 'Tier 1' },
    { value: 'SSC_CGL_TIER2', label: 'Tier 2' },
  ],
  SSC_CPO: [
    { value: 'SSC_CPO_TIER1', label: 'Tier 1' },
    { value: 'SSC_CPO_TIER2', label: 'Tier 2' },
  ],
  SSC_CHSL: [
    { value: 'SSC_CHSL_TIER1', label: 'Tier 1' },
    { value: 'SSC_CHSL_TIER2', label: 'Tier 2' },
  ],
  BANK_PO: [
    { value: 'BANK_PO_PRE', label: 'Prelims' },
    { value: 'BANK_PO_MAIN', label: 'Mains' },
  ],
};

const SUBSCRIPTION_TYPES: SubscriptionType[] = ['DAY', 'MONTHLY', 'QUATERLY', 'HALFYEARLY', 'YEARLY'];

interface FormData {
  paperType: PaperType;
  paperCategory: PaperCategory | '';
  paperSubCategory: PaperSubCategory | '';
  testType: TestType;
  name: string;
  description: string;
  createdDate: string;
  validity: string;
  pricing: Record<SubscriptionType, { originalPrice: string; discountedPrice: string; isActive: boolean }>;
}

const initialPricing = SUBSCRIPTION_TYPES.reduce((acc, type) => {
  acc[type] = { originalPrice: '', discountedPrice: '', isActive: false };
  return acc;
}, {} as FormData['pricing']);

export default function CreateSubscriptionPage() {
  const router = useRouter();
  const createSubscription = useCreateSubscription();

  const [formData, setFormData] = useState<FormData>({
    paperType: 'SSC',
    paperCategory: '',
    paperSubCategory: '',
    testType: 'PAID',
    name: '',
    description: '',
    createdDate: new Date().toISOString().split('T')[0],
    validity: '',
    pricing: { ...initialPricing },
  });

  const [selectedPapers, setSelectedPapers] = useState<PaperSelectionDto[]>([]);
  const [paperSearch, setPaperSearch] = useState('');
  const [errors, setErrors] = useState<Record<string, string>>({});

  // Fetch available papers
  const { data: availablePapersData, isLoading: loadingPapers } = useAvailablePapers(
    formData.paperSubCategory
      ? {
          testType: formData.testType,
          paperSubCategory: formData.paperSubCategory as PaperSubCategory,
          search: paperSearch,
          size: 50,
        }
      : null
  );

  const availablePapers = availablePapersData?.content || [];

  // Filter out already selected papers
  const filteredAvailablePapers = availablePapers.filter(
    (p) => !selectedPapers.some((sp) => sp.id === p.id)
  );

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.name.trim()) newErrors.name = 'Name is required';
    if (!formData.paperCategory) newErrors.paperCategory = 'Category is required';
    if (!formData.paperSubCategory) newErrors.paperSubCategory = 'Sub-category is required';
    if (!formData.validity) newErrors.validity = 'Validity date is required';
    if (selectedPapers.length === 0) newErrors.papers = 'At least one paper is required';

    // Validate pricing for PAID subscriptions
    if (formData.testType === 'PAID') {
      const hasActivePrice = Object.values(formData.pricing).some((p) => p.isActive);
      if (!hasActivePrice) {
        newErrors.pricing = 'At least one pricing tier must be active';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const buildPayload = (state: SubscriptionState): SubscriptionCreateDto => {
    const pricing: Record<SubscriptionType, PriceMetadataDto> = {} as Record<SubscriptionType, PriceMetadataDto>;

    if (formData.testType === 'PAID') {
      Object.entries(formData.pricing).forEach(([key, value]) => {
        if (value.isActive) {
          pricing[key as SubscriptionType] = {
            originalPrice: parseFloat(value.originalPrice) || 0,
            discountedPrice: parseFloat(value.discountedPrice) || 0,
            isActive: true,
          };
        }
      });
    }

    return {
      paperType: formData.paperType,
      paperCategory: formData.paperCategory as PaperCategory,
      paperSubCategory: formData.paperSubCategory as PaperSubCategory,
      testType: formData.testType,
      name: formData.name,
      description: formData.description || undefined,
      paperIds: selectedPapers.map((p) => p.id),
      createdDate: new Date(formData.createdDate).getTime(),
      validity: new Date(formData.validity).getTime(),
      pricing: formData.testType === 'PAID' ? pricing : undefined,
      state,
    };
  };

  const handleSaveDraft = async () => {
    if (!validateForm()) return;
    try {
      await createSubscription.mutateAsync(buildPayload('DRAFT'));
      router.push('/admin/subscriptions');
    } catch (error) {
      console.error('Failed to save draft:', error);
    }
  };

  const handleSaveAndActivate = async () => {
    if (!validateForm()) return;
    try {
      await createSubscription.mutateAsync(buildPayload('ACTIVE'));
      router.push('/admin/subscriptions');
    } catch (error) {
      console.error('Failed to activate:', error);
    }
  };

  const addPaper = (paper: PaperSelectionDto) => {
    setSelectedPapers([...selectedPapers, paper]);
  };

  const removePaper = (paperId: string) => {
    setSelectedPapers(selectedPapers.filter((p) => p.id !== paperId));
  };

  const addAllPapers = () => {
    setSelectedPapers([...selectedPapers, ...filteredAvailablePapers]);
  };

  const removeAllPapers = () => {
    setSelectedPapers([]);
  };

  const updatePricing = (type: SubscriptionType, field: string, value: string | boolean) => {
    setFormData({
      ...formData,
      pricing: {
        ...formData.pricing,
        [type]: {
          ...formData.pricing[type],
          [field]: value,
        },
      },
    });
  };

  return (
    <div className="container mx-auto py-6 px-4 max-w-6xl">
      <div className="flex items-center gap-4 mb-6">
        <Link href="/admin/subscriptions">
          <Button variant="ghost" size="sm">
            <ArrowLeft className="mr-2 h-4 w-4" />
            Back
          </Button>
        </Link>
        <div>
          <h1 className="text-2xl font-bold">Create Subscription</h1>
          <p className="text-gray-600">Create a new subscription package</p>
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        {/* Left Column - Form */}
        <div className="space-y-6">
          {/* Classification */}
          <Card>
            <CardHeader>
              <CardTitle>Classification</CardTitle>
              <CardDescription>Select paper type and category</CardDescription>
            </CardHeader>
            <CardContent className="grid gap-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label>Paper Type</Label>
                  <Select
                    value={formData.paperType}
                    onValueChange={(v: PaperType) =>
                      setFormData({
                        ...formData,
                        paperType: v,
                        paperCategory: '',
                        paperSubCategory: '',
                      })
                    }
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {PAPER_TYPES.map((t) => (
                        <SelectItem key={t} value={t}>{t}</SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label>Test Type</Label>
                  <Select
                    value={formData.testType}
                    onValueChange={(v: TestType) =>
                      setFormData({ ...formData, testType: v })
                    }
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="FREE">Free</SelectItem>
                      <SelectItem value="PAID">Paid</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label>Category *</Label>
                  <Select
                    value={formData.paperCategory}
                    onValueChange={(v: PaperCategory) =>
                      setFormData({ ...formData, paperCategory: v, paperSubCategory: '' })
                    }
                  >
                    <SelectTrigger className={errors.paperCategory ? 'border-red-500' : ''}>
                      <SelectValue placeholder="Select category" />
                    </SelectTrigger>
                    <SelectContent>
                      {PAPER_CATEGORIES[formData.paperType]?.map((c) => (
                        <SelectItem key={c.value} value={c.value}>{c.label}</SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label>Sub-Category *</Label>
                  <Select
                    value={formData.paperSubCategory}
                    onValueChange={(v: PaperSubCategory) =>
                      setFormData({ ...formData, paperSubCategory: v })
                    }
                    disabled={!formData.paperCategory}
                  >
                    <SelectTrigger className={errors.paperSubCategory ? 'border-red-500' : ''}>
                      <SelectValue placeholder="Select sub-category" />
                    </SelectTrigger>
                    <SelectContent>
                      {formData.paperCategory &&
                        PAPER_SUBCATEGORIES[formData.paperCategory]?.map((c) => (
                          <SelectItem key={c.value} value={c.value}>{c.label}</SelectItem>
                        ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Details */}
          <Card>
            <CardHeader>
              <CardTitle>Details</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label>Name *</Label>
                <Input
                  placeholder="e.g., SSC CGL Tier-1 Complete Package"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className={errors.name ? 'border-red-500' : ''}
                />
                {errors.name && <p className="text-sm text-red-500">{errors.name}</p>}
              </div>

              <div className="space-y-2">
                <Label>Description</Label>
                <Textarea
                  placeholder="Subscription description..."
                  rows={3}
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label>Start Date</Label>
                  <Input
                    type="date"
                    value={formData.createdDate}
                    onChange={(e) => setFormData({ ...formData, createdDate: e.target.value })}
                  />
                </div>

                <div className="space-y-2">
                  <Label>Expiry Date *</Label>
                  <Input
                    type="date"
                    value={formData.validity}
                    onChange={(e) => setFormData({ ...formData, validity: e.target.value })}
                    className={errors.validity ? 'border-red-500' : ''}
                  />
                  {errors.validity && <p className="text-sm text-red-500">{errors.validity}</p>}
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Pricing (PAID only) */}
          {formData.testType === 'PAID' && (
            <Card>
              <CardHeader>
                <CardTitle>Pricing</CardTitle>
                <CardDescription>Set prices for each subscription duration</CardDescription>
              </CardHeader>
              <CardContent>
                {errors.pricing && <p className="text-sm text-red-500 mb-4">{errors.pricing}</p>}
                <div className="space-y-4">
                  {SUBSCRIPTION_TYPES.map((type) => (
                    <div key={type} className="flex items-center gap-4">
                      <Checkbox
                        checked={formData.pricing[type].isActive}
                        onCheckedChange={(checked) => updatePricing(type, 'isActive', !!checked)}
                      />
                      <div className="w-24 font-medium">{SUBSCRIPTION_TYPE_LABELS[type]}</div>
                      <Input
                        type="number"
                        placeholder="Original"
                        className="w-28"
                        value={formData.pricing[type].originalPrice}
                        onChange={(e) => updatePricing(type, 'originalPrice', e.target.value)}
                        disabled={!formData.pricing[type].isActive}
                      />
                      <Input
                        type="number"
                        placeholder="Discounted"
                        className="w-28"
                        value={formData.pricing[type].discountedPrice}
                        onChange={(e) => updatePricing(type, 'discountedPrice', e.target.value)}
                        disabled={!formData.pricing[type].isActive}
                      />
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          )}
        </div>

        {/* Right Column - Paper Selection */}
        <div>
          <Card className="h-fit">
            <CardHeader>
              <CardTitle>Select Papers</CardTitle>
              <CardDescription>
                Choose papers from {formData.testType === 'FREE' ? 'freePaperCollection' : 'paidPaperCollection'}
              </CardDescription>
            </CardHeader>
            <CardContent>
              {errors.papers && <p className="text-sm text-red-500 mb-4">{errors.papers}</p>}

              {!formData.paperSubCategory ? (
                <p className="text-gray-500 text-center py-8">
                  Select a sub-category to view available papers
                </p>
              ) : (
                <div className="space-y-4">
                  {/* Search */}
                  <div className="flex gap-2">
                    <Input
                      placeholder="Search papers by name or ID..."
                      value={paperSearch}
                      onChange={(e) => setPaperSearch(e.target.value)}
                    />
                    <Button variant="outline" size="icon">
                      <Search className="h-4 w-4" />
                    </Button>
                  </div>

                  {/* Dual List */}
                  <div className="grid grid-cols-2 gap-4">
                    {/* Available Papers */}
                    <div>
                      <div className="flex justify-between items-center mb-2">
                        <Label>Available ({filteredAvailablePapers.length})</Label>
                        <Button variant="link" size="sm" onClick={addAllPapers}>
                          Add All
                        </Button>
                      </div>
                      <div className="border rounded-lg h-[400px] overflow-y-auto">
                        {loadingPapers ? (
                          <p className="text-center py-8 text-gray-500">Loading...</p>
                        ) : filteredAvailablePapers.length === 0 ? (
                          <p className="text-center py-8 text-gray-500">No papers available</p>
                        ) : (
                          filteredAvailablePapers.map((paper) => (
                            <div
                              key={paper.id}
                              className="p-3 border-b hover:bg-gray-50 cursor-pointer"
                              onClick={() => addPaper(paper)}
                            >
                              <div className="flex justify-between">
                                <div className="flex-1">
                                  <p className="font-mono text-xs text-gray-500">{paper.id}</p>
                                  <p className="font-medium text-sm">{paper.paperName}</p>
                                  <p className="text-xs text-gray-500">
                                    {paper.totalQuestionCount} Q | {paper.totalTimeMinutes} min
                                  </p>
                                </div>
                                <ChevronRight className="h-4 w-4 text-gray-400 mt-2" />
                              </div>
                            </div>
                          ))
                        )}
                      </div>
                    </div>

                    {/* Selected Papers */}
                    <div>
                      <div className="flex justify-between items-center mb-2">
                        <Label>Selected ({selectedPapers.length})</Label>
                        <Button variant="link" size="sm" onClick={removeAllPapers}>
                          Remove All
                        </Button>
                      </div>
                      <div className="border rounded-lg h-[400px] overflow-y-auto">
                        {selectedPapers.length === 0 ? (
                          <p className="text-center py-8 text-gray-500">No papers selected</p>
                        ) : (
                          selectedPapers.map((paper) => (
                            <div
                              key={paper.id}
                              className="p-3 border-b hover:bg-gray-50 group"
                            >
                              <div className="flex justify-between">
                                <div className="flex-1">
                                  <p className="font-mono text-xs text-gray-500">{paper.id}</p>
                                  <p className="font-medium text-sm">{paper.paperName}</p>
                                  <p className="text-xs text-gray-500">
                                    {paper.totalQuestionCount} Q | {paper.totalTimeMinutes} min
                                  </p>
                                </div>
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  className="opacity-0 group-hover:opacity-100"
                                  onClick={() => removePaper(paper.id)}
                                >
                                  <X className="h-4 w-4" />
                                </Button>
                              </div>
                            </div>
                          ))
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </div>

      {/* Actions */}
      <div className="flex justify-end gap-4 mt-6">
        <Button variant="outline" onClick={handleSaveDraft} disabled={createSubscription.isPending}>
          <Save className="mr-2 h-4 w-4" />
          Save as Draft
        </Button>
        <Button onClick={handleSaveAndActivate} disabled={createSubscription.isPending}>
          <Send className="mr-2 h-4 w-4" />
          Save & Activate
        </Button>
      </div>
    </div>
  );
}
