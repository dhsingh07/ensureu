'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useAuthStore } from '@/stores/auth-store';
import { useCategoryStore } from '@/stores/category-store';
import {
  useFreeSubscriptions,
  usePaidSubscriptions,
  useUserSubscriptions,
  useSubscribe,
  usePurchaseSubscription,
  formatPrice,
  calculateValidity,
  isSubscriptionExpired,
} from '@/hooks/use-subscription';
import type { SubscriptionItem, SubscriptionType, PriceMetadata } from '@/types/subscription';
import { SUBSCRIPTION_LABELS, SUBSCRIPTION_VALIDITY } from '@/types/subscription';
import type { PaperCategory, PaperSubCategory } from '@/types/paper';
import {
  BookOpen,
  Crown,
  Check,
  ChevronRight,
  FileText,
  Sparkles,
  TrendingUp,
  Clock,
  Loader2,
  Star,
  Percent,
  Brain,
  Target,
  Lightbulb,
  BarChart3,
  Zap,
  MessageSquare,
  GraduationCap,
  Route,
  Trophy,
  ArrowRight,
} from 'lucide-react';

export default function LandingPage() {
  const router = useRouter();
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const user = useAuthStore((state) => state.user);

  // Subscription state
  const [selectedSubscription, setSelectedSubscription] = useState<SubscriptionItem | null>(null);
  const [selectedPeriod, setSelectedPeriod] = useState<SubscriptionType>('MONTHLY');
  const [showPaperList, setShowPaperList] = useState<SubscriptionItem | null>(null);
  const [cardDurations, setCardDurations] = useState<Record<number, SubscriptionType>>({});

  const setCategories = useCategoryStore((state) => state.setCategories);

  const { data: freeSubscriptions, isLoading: loadingFree } = useFreeSubscriptions();
  const { data: paidSubscriptions, isLoading: loadingPaid } = usePaidSubscriptions();
  const { data: userSubscriptionsSSC } = useUserSubscriptions('SSC', isAuthenticated);
  const { data: userSubscriptionsBANK } = useUserSubscriptions('BANK', isAuthenticated);

  const subscribeMutation = useSubscribe();
  const purchaseMutation = usePurchaseSubscription();

  const toEnumFormat = (displayName: string): string => {
    return displayName
      .replace(/\s+/g, '_')
      .replace(/-/g, '')
      .toUpperCase();
  };

  const getCategoryFromSubscription = (sub: SubscriptionItem): { root: PaperCategory; child: PaperSubCategory } => {
    const subCat = toEnumFormat(sub.paperSubCategory);
    if (subCat.startsWith('SSC_CGL')) return { root: 'SSC_CGL' as PaperCategory, child: subCat as PaperSubCategory };
    if (subCat.startsWith('SSC_CPO')) return { root: 'SSC_CPO' as PaperCategory, child: subCat as PaperSubCategory };
    if (subCat.startsWith('SSC_CHSL')) return { root: 'SSC_CHSL' as PaperCategory, child: subCat as PaperSubCategory };
    if (subCat.startsWith('BANK_PO')) return { root: 'BANK_PO' as PaperCategory, child: subCat as PaperSubCategory };
    return { root: 'SSC_CGL' as PaperCategory, child: 'SSC_CGL_TIER1' as PaperSubCategory };
  };

  const getCardDuration = (sub: SubscriptionItem): SubscriptionType => {
    if (cardDurations[sub.id]) return cardDurations[sub.id];
    const prices = sub.mapOfSubTypeVsPrice;
    if (prices) {
      const availableTypes = Object.keys(prices) as SubscriptionType[];
      return availableTypes[0] || 'MONTHLY';
    }
    return 'MONTHLY';
  };

  const getPriceInfo = (sub: SubscriptionItem, duration: SubscriptionType): PriceMetadata | null => {
    return sub.mapOfSubTypeVsPrice?.[duration] || null;
  };

  const activeSubscriptionKeys = new Set(
    [...(userSubscriptionsSSC || []), ...(userSubscriptionsBANK || [])]
      .filter((sub) => sub.active && !isSubscriptionExpired(sub.validity))
      .map((sub) => `${sub.paperCategory}::${sub.paperSubCategory}`)
  );

  const filteredFreeSubscriptions = (freeSubscriptions || []).filter((sub) => {
    if (!isAuthenticated) return true;
    const categoryKey = toEnumFormat(sub.paperCategory);
    const subCategoryKey = toEnumFormat(sub.paperSubCategory);
    return !activeSubscriptionKeys.has(`${categoryKey}::${subCategoryKey}`);
  });

  const filteredPaidSubscriptions = (paidSubscriptions || []).filter((sub) => {
    if (!isAuthenticated) return true;
    const categoryKey = toEnumFormat(sub.paperCategory);
    const subCategoryKey = toEnumFormat(sub.paperSubCategory);
    return !activeSubscriptionKeys.has(`${categoryKey}::${subCategoryKey}`);
  });

  const handleFreeSubscribe = async (sub: SubscriptionItem) => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }
    await subscribeMutation.mutateAsync({
      id: sub.id,
      paperType: sub.paperType,
      paperCategory: toEnumFormat(sub.paperCategory),
      paperSubCategory: toEnumFormat(sub.paperSubCategory),
      testType: 'FREE',
      listOfSubscriptionIds: [sub.id],
    });
    const { root, child } = getCategoryFromSubscription(sub);
    setCategories(root, child, true);
    router.push('/home');
  };

  const handlePaidSelect = (sub: SubscriptionItem) => {
    if (!isAuthenticated) {
      router.push('/register');
      return;
    }
    setSelectedSubscription(sub);
    setSelectedPeriod(getCardDuration(sub));
  };

  const handlePurchase = async () => {
    if (!selectedSubscription) return;
    const priceInfo = getPriceInfo(selectedSubscription, selectedPeriod);
    const actualPrice = priceInfo?.discountedPrice || priceInfo?.price || 0;
    await purchaseMutation.mutateAsync({
      listOfSubscriptionIds: selectedSubscription.listOfSubscriptionIds || [selectedSubscription.id],
      subscriptionType: selectedPeriod,
      actualPrice: actualPrice,
      validity: SUBSCRIPTION_VALIDITY[selectedPeriod] || calculateValidity(selectedPeriod),
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

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50/30 to-purple-50/20">
      {/* Header */}
      <header className="border-b bg-white/80 backdrop-blur-sm sticky top-0 z-50">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Link href="/">
              <div className="w-8 h-8 bg-gradient-to-br from-teal-500 to-cyan-600 rounded-lg flex items-center justify-center hover:opacity-90 transition-opacity cursor-pointer">
                <Sparkles className="h-5 w-5 text-white" />
              </div>
            </Link>
            <Link href="/home">
              <span className="text-xl font-bold text-slate-900 hover:text-teal-600 transition-colors cursor-pointer">EnsureU</span>
            </Link>
            <Badge variant="secondary" className="ml-2 bg-gradient-to-r from-teal-100 to-cyan-100 text-teal-700 border-teal-200">
              <Zap className="h-3 w-3 mr-1" />
              AI Powered
            </Badge>
          </div>
          <nav className="flex items-center gap-4">
            {isAuthenticated ? (
              <>
                <span className="text-sm text-slate-600">
                  Welcome, {user?.firstName || user?.userName}
                </span>
                <Link href="/home">
                  <Button>Go to Dashboard</Button>
                </Link>
              </>
            ) : (
              <>
                <Link href="/login">
                  <Button variant="ghost">Login</Button>
                </Link>
                <Link href="/register">
                  <Button className="bg-gradient-to-r from-teal-500 to-cyan-600 hover:from-teal-600 hover:to-cyan-700">
                    Get Started Free
                  </Button>
                </Link>
              </>
            )}
          </nav>
        </div>
      </header>

      {/* Hero Section */}
      <main className="container mx-auto px-4 py-16">
        <div className="text-center max-w-4xl mx-auto mb-20">
          <div className="flex items-center justify-center gap-2 mb-6">
            <Badge className="px-4 py-1.5 bg-gradient-to-r from-teal-500 to-cyan-500 text-white border-0">
              <Sparkles className="h-3.5 w-3.5 mr-1.5" />
              India's First AI-Native Exam Platform
            </Badge>
          </div>
          <h1 className="text-4xl md:text-6xl font-bold text-slate-900 mb-6 leading-tight">
            Your Personal{' '}
            <span className="bg-gradient-to-r from-teal-500 via-cyan-500 to-blue-500 bg-clip-text text-transparent">
              AI Tutor
            </span>{' '}
            for Competitive Exams
          </h1>
          <p className="text-xl text-slate-600 mb-8 max-w-2xl mx-auto">
            AI understands your weaknesses, creates personalized study plans, explains every wrong answer, and predicts your exam score. Not just practice - intelligent preparation.
          </p>
          <div className="flex items-center justify-center gap-4 flex-wrap">
            <Link href={isAuthenticated ? "/home" : "/register"}>
              <Button size="lg" className="px-8 bg-gradient-to-r from-teal-500 to-cyan-600 hover:from-teal-600 hover:to-cyan-700">
                <Sparkles className="mr-2 h-5 w-5" />
                {isAuthenticated ? "Go to Dashboard" : "Start AI-Powered Learning"}
              </Button>
            </Link>
            <Link href="/practice">
              <Button size="lg" variant="outline" className="px-8">
                Explore Features
                <ArrowRight className="ml-2 h-4 w-4" />
              </Button>
            </Link>
          </div>

          {/* Trust Badges */}
          <div className="flex items-center justify-center gap-6 mt-8 text-sm text-slate-500">
            <div className="flex items-center gap-1">
              <Check className="h-4 w-4 text-green-500" />
              <span>No Credit Card Required</span>
            </div>
            <div className="flex items-center gap-1">
              <Check className="h-4 w-4 text-green-500" />
              <span>SSC, Bank, Railway Exams</span>
            </div>
            <div className="flex items-center gap-1">
              <Check className="h-4 w-4 text-green-500" />
              <span>10,000+ Students</span>
            </div>
          </div>
        </div>

        {/* AI Features Section */}
        <div className="mb-20">
          <div className="text-center mb-12">
            <Badge variant="outline" className="mb-4 border-teal-300 text-teal-700">
              <Zap className="h-3 w-3 mr-1" />
              AI-Powered Features
            </Badge>
            <h2 className="text-3xl md:text-4xl font-bold text-slate-900 mb-4">
              How AI Transforms Your Preparation
            </h2>
            <p className="text-lg text-slate-600 max-w-2xl mx-auto">
              Traditional coaching gives you content. EnsureU AI gives you personalized intelligence.
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {/* AI Feature Cards */}
            <Card className="border-teal-200 bg-gradient-to-br from-white to-teal-50/50 hover:shadow-lg transition-all">
              <CardContent className="pt-6">
                <div className="w-12 h-12 bg-teal-100 rounded-xl flex items-center justify-center mb-4">
                  <MessageSquare className="h-6 w-6 text-teal-600" />
                </div>
                <h3 className="text-xl font-semibold text-slate-900 mb-2">
                  Instant Wrong Answer Explanation
                </h3>
                <p className="text-slate-600 mb-4">
                  Made a mistake? AI explains exactly why your answer was wrong, identifies your misconception, and gives you a memory tip to never repeat it.
                </p>
                <div className="flex items-center text-teal-600 text-sm font-medium">
                  <Lightbulb className="h-4 w-4 mr-1" />
                  Personalized to your mistake patterns
                </div>
              </CardContent>
            </Card>

            <Card className="border-blue-200 bg-gradient-to-br from-white to-blue-50/50 hover:shadow-lg transition-all">
              <CardContent className="pt-6">
                <div className="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center mb-4">
                  <Target className="h-6 w-6 text-blue-600" />
                </div>
                <h3 className="text-xl font-semibold text-slate-900 mb-2">
                  Weakness Detection & Focus
                </h3>
                <p className="text-slate-600 mb-4">
                  AI analyzes your performance to identify weak topics, subtopics, and even specific concept gaps. Know exactly what to study next.
                </p>
                <div className="flex items-center text-blue-600 text-sm font-medium">
                  <BarChart3 className="h-4 w-4 mr-1" />
                  Topic → Subtopic → Concept level
                </div>
              </CardContent>
            </Card>

            <Card className="border-green-200 bg-gradient-to-br from-white to-green-50/50 hover:shadow-lg transition-all">
              <CardContent className="pt-6">
                <div className="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center mb-4">
                  <Route className="h-6 w-6 text-green-600" />
                </div>
                <h3 className="text-xl font-semibold text-slate-900 mb-2">
                  AI Study Plan Generator
                </h3>
                <p className="text-slate-600 mb-4">
                  Get a personalized week-by-week study plan based on your exam date, available hours, and current level. AI adjusts as you progress.
                </p>
                <div className="flex items-center text-green-600 text-sm font-medium">
                  <Clock className="h-4 w-4 mr-1" />
                  Adapts to your schedule & progress
                </div>
              </CardContent>
            </Card>

            <Card className="border-orange-200 bg-gradient-to-br from-white to-orange-50/50 hover:shadow-lg transition-all">
              <CardContent className="pt-6">
                <div className="w-12 h-12 bg-orange-100 rounded-xl flex items-center justify-center mb-4">
                  <Trophy className="h-6 w-6 text-orange-600" />
                </div>
                <h3 className="text-xl font-semibold text-slate-900 mb-2">
                  Score Prediction
                </h3>
                <p className="text-slate-600 mb-4">
                  AI predicts your actual exam score based on mock performance, historical data, and comparison with successful candidates.
                </p>
                <div className="flex items-center text-orange-600 text-sm font-medium">
                  <TrendingUp className="h-4 w-4 mr-1" />
                  95% accuracy in score prediction
                </div>
              </CardContent>
            </Card>

            <Card className="border-indigo-200 bg-gradient-to-br from-white to-indigo-50/50 hover:shadow-lg transition-all">
              <CardContent className="pt-6">
                <div className="w-12 h-12 bg-indigo-100 rounded-xl flex items-center justify-center mb-4">
                  <Sparkles className="h-6 w-6 text-indigo-600" />
                </div>
                <h3 className="text-xl font-semibold text-slate-900 mb-2">
                  AI Question Generation
                </h3>
                <p className="text-slate-600 mb-4">
                  Papers created by AI that match real exam patterns. Fresh questions daily - never run out of practice material.
                </p>
                <div className="flex items-center text-indigo-600 text-sm font-medium">
                  <FileText className="h-4 w-4 mr-1" />
                  Matches SSC/Bank exam patterns exactly
                </div>
              </CardContent>
            </Card>

            <Card className="border-cyan-200 bg-gradient-to-br from-white to-cyan-50/50 hover:shadow-lg transition-all">
              <CardContent className="pt-6">
                <div className="w-12 h-12 bg-cyan-100 rounded-xl flex items-center justify-center mb-4">
                  <GraduationCap className="h-6 w-6 text-cyan-600" />
                </div>
                <h3 className="text-xl font-semibold text-slate-900 mb-2">
                  Progressive Hints System
                </h3>
                <p className="text-slate-600 mb-4">
                  Stuck on a question? Get 3 levels of hints - from subtle nudges to step-by-step guidance. Learn to solve, not just memorize.
                </p>
                <div className="flex items-center text-cyan-600 text-sm font-medium">
                  <Zap className="h-4 w-4 mr-1" />
                  Builds problem-solving skills
                </div>
              </CardContent>
            </Card>
          </div>
        </div>

        {/* How It Works */}
        <div className="mb-20 bg-gradient-to-r from-slate-900 to-slate-800 rounded-3xl p-8 md:p-12 text-white">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold mb-4">
              How AI Learning Works
            </h2>
            <p className="text-slate-300 max-w-2xl mx-auto">
              A continuous cycle of learning, analysis, and improvement
            </p>
          </div>

          <div className="grid md:grid-cols-4 gap-8">
            {[
              {
                step: '01',
                title: 'Take a Test',
                description: 'Attempt mock tests or practice questions at your own pace',
                icon: FileText,
              },
              {
                step: '02',
                title: 'AI Analyzes',
                description: 'AI identifies patterns in your mistakes and weak areas',
                icon: Sparkles,
              },
              {
                step: '03',
                title: 'Get Insights',
                description: 'Receive detailed analysis with actionable recommendations',
                icon: Lightbulb,
              },
              {
                step: '04',
                title: 'Improve',
                description: 'Follow personalized study plan and track your growth',
                icon: TrendingUp,
              },
            ].map((item, idx) => (
              <div key={item.step} className="text-center relative">
                {idx < 3 && (
                  <div className="hidden md:block absolute top-8 left-[60%] w-[80%] h-0.5 bg-gradient-to-r from-teal-500 to-transparent" />
                )}
                <div className="w-16 h-16 bg-gradient-to-br from-teal-500 to-cyan-500 rounded-2xl flex items-center justify-center mx-auto mb-4">
                  <item.icon className="h-8 w-8 text-white" />
                </div>
                <div className="text-teal-400 text-sm font-bold mb-2">{item.step}</div>
                <h3 className="text-xl font-semibold mb-2">{item.title}</h3>
                <p className="text-slate-400 text-sm">{item.description}</p>
              </div>
            ))}
          </div>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-16">
          {[
            { label: 'AI-Generated Questions', value: '100,000+', icon: Sparkles, color: 'teal' },
            { label: 'Explanations Given', value: '500,000+', icon: MessageSquare, color: 'cyan' },
            { label: 'Study Plans Created', value: '25,000+', icon: Route, color: 'green' },
            { label: 'Score Predictions', value: '95%', subtext: 'Accuracy', icon: Target, color: 'orange' },
          ].map((stat) => (
            <Card key={stat.label} className="text-center hover:shadow-md transition-all">
              <CardContent className="pt-6">
                <stat.icon className={`h-8 w-8 mx-auto mb-2 text-${stat.color}-500`} />
                <p className="text-2xl font-bold text-slate-900">{stat.value}</p>
                <p className="text-sm text-slate-500">{stat.label}</p>
                {stat.subtext && <p className="text-xs text-slate-400">{stat.subtext}</p>}
              </CardContent>
            </Card>
          ))}
        </div>

        {/* Free Subscriptions Section */}
        <div className="mb-16">
          <div className="flex items-center gap-2 mb-6">
            <div className="p-2 bg-green-100 rounded-lg">
              <BookOpen className="h-5 w-5 text-green-600" />
            </div>
            <div>
              <h2 className="text-2xl font-bold text-slate-900">Free AI-Powered Test Packages</h2>
              <p className="text-slate-500">Start with AI learning - no payment required</p>
            </div>
          </div>

          {loadingFree ? (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[1, 2, 3].map((i) => (
                <Card key={i}>
                  <CardContent className="p-6">
                    <Skeleton className="h-6 w-3/4 mb-2" />
                    <Skeleton className="h-4 w-full mb-4" />
                    <Skeleton className="h-20 w-full mb-4" />
                    <Skeleton className="h-10 w-full" />
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : filteredFreeSubscriptions.length > 0 ? (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {filteredFreeSubscriptions.map((sub) => (
                <Card key={sub.id} className="hover:shadow-lg transition-all border-green-200 bg-gradient-to-br from-white to-green-50/30">
                  <CardHeader className="pb-2">
                    <div className="flex items-center justify-between">
                      <Badge className="bg-green-500">FREE + AI</Badge>
                      <span className="text-sm text-slate-500">
                        {sub.paperInfoList?.length || 0} tests
                      </span>
                    </div>
                    <CardTitle className="text-xl">{sub.paperCategory}</CardTitle>
                    <CardDescription>{sub.paperSubCategory.replace(/_/g, ' ')}</CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <p className="text-sm text-slate-600">
                      {sub.description || 'AI-powered mock tests with instant explanations and progress tracking.'}
                    </p>
                    <div className="flex flex-wrap gap-2">
                      <Badge variant="outline" className="text-xs">
                        <Sparkles className="h-3 w-3 mr-1" />
                        AI Explanations
                      </Badge>
                      <Badge variant="outline" className="text-xs">
                        <BarChart3 className="h-3 w-3 mr-1" />
                        Analytics
                      </Badge>
                    </div>
                    <Button
                      className="w-full bg-green-600 hover:bg-green-700"
                      onClick={() => handleFreeSubscribe(sub)}
                      disabled={subscribeMutation.isPending}
                    >
                      {subscribeMutation.isPending ? (
                        <Loader2 className="h-4 w-4 animate-spin mr-2" />
                      ) : (
                        <Sparkles className="h-4 w-4 mr-2" />
                      )}
                      {isAuthenticated ? 'Start AI Learning' : 'Login to Start'}
                    </Button>
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : (
            <Card className="border-dashed">
              <CardContent className="py-12 text-center text-slate-500">
                <BookOpen className="h-12 w-12 mx-auto mb-4 text-slate-300" />
                <p>No free test packages available at the moment</p>
              </CardContent>
            </Card>
          )}
        </div>

        {/* Premium Subscriptions Section */}
        {(loadingPaid || !isAuthenticated || filteredPaidSubscriptions.length > 0) && (
        <div className="mb-16">
          <div className="flex items-center gap-2 mb-6">
            <div className="p-2 bg-gradient-to-br from-orange-100 to-teal-100 rounded-lg">
              <Crown className="h-5 w-5 text-orange-600" />
            </div>
            <div>
              <h2 className="text-2xl font-bold text-slate-900">Premium AI Packages</h2>
              <p className="text-slate-500">Full AI power with personalized study plans & score prediction</p>
            </div>
          </div>

          {loadingPaid ? (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {[1, 2, 3].map((i) => (
                <Card key={i}>
                  <CardContent className="p-6">
                    <Skeleton className="h-6 w-3/4 mb-2" />
                    <Skeleton className="h-4 w-full mb-4" />
                    <Skeleton className="h-20 w-full mb-4" />
                    <Skeleton className="h-10 w-full" />
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : filteredPaidSubscriptions.length > 0 ? (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {filteredPaidSubscriptions.map((sub) => {
                const prices = sub.mapOfSubTypeVsPrice;
                const availableTypes = prices ? (Object.keys(prices) as SubscriptionType[]) : [];
                const selectedDuration = getCardDuration(sub);
                const priceInfo = getPriceInfo(sub, selectedDuration);

                return (
                  <Card key={sub.id} className="hover:shadow-lg transition-all border-orange-200 bg-gradient-to-br from-white to-orange-50/30 relative overflow-hidden">
                    <div className="absolute top-0 right-0 w-20 h-20 bg-gradient-to-br from-teal-500/10 to-orange-500/10 rounded-bl-full" />
                    <CardHeader className="pb-2">
                      <div className="flex items-center justify-between">
                        <Badge className="bg-gradient-to-r from-orange-500 to-teal-500">
                          <Sparkles className="h-3 w-3 mr-1" />
                          PREMIUM AI
                        </Badge>
                        <span className="text-sm text-slate-500">
                          {sub.paperInfoList?.length || 0} tests
                        </span>
                      </div>
                      <CardTitle className="text-xl">{sub.paperCategory}</CardTitle>
                      <CardDescription>{sub.paperSubCategory.replace(/_/g, ' ')}</CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <p className="text-sm text-slate-600">
                        {sub.description || 'Complete AI preparation with study plans, score prediction & unlimited explanations.'}
                      </p>

                      <div className="flex flex-wrap gap-2">
                        <Badge variant="outline" className="text-xs border-teal-200 text-teal-700">
                          <Route className="h-3 w-3 mr-1" />
                          AI Study Plan
                        </Badge>
                        <Badge variant="outline" className="text-xs border-orange-200 text-orange-700">
                          <Target className="h-3 w-3 mr-1" />
                          Score Prediction
                        </Badge>
                      </div>

                      {availableTypes.length > 0 && (
                        <Select
                          value={selectedDuration}
                          onValueChange={(value) =>
                            setCardDurations(prev => ({ ...prev, [sub.id]: value as SubscriptionType }))
                          }
                        >
                          <SelectTrigger className="w-full">
                            <SelectValue placeholder="Select duration" />
                          </SelectTrigger>
                          <SelectContent>
                            {availableTypes.map((type) => (
                              <SelectItem key={type} value={type}>
                                {SUBSCRIPTION_LABELS[type] || type}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      )}

                      {priceInfo && (
                        <div className="space-y-1">
                          <div className="flex items-baseline gap-2">
                            <span className="text-3xl font-bold text-orange-600">
                              {formatPrice(priceInfo.discountedPrice || priceInfo.price)}
                            </span>
                            {priceInfo.discountedPrice && priceInfo.discountedPrice < priceInfo.price && (
                              <span className="text-lg text-slate-400 line-through">
                                {formatPrice(priceInfo.price)}
                              </span>
                            )}
                          </div>
                          {priceInfo.discountPercentage > 0 && (
                            <div className="flex items-center gap-1 text-green-600 text-sm">
                              <Percent className="h-3 w-3" />
                              <span>{priceInfo.discountPercentage}% off</span>
                            </div>
                          )}
                        </div>
                      )}

                      <Button
                        className="w-full bg-gradient-to-r from-orange-500 to-teal-500 hover:from-orange-600 hover:to-teal-600"
                        onClick={() => handlePaidSelect(sub)}
                      >
                        <Sparkles className="h-4 w-4 mr-2" />
                        {isAuthenticated ? 'Unlock AI Features' : 'Sign Up for AI Learning'}
                      </Button>
                    </CardContent>
                  </Card>
                );
              })}
            </div>
          ) : null}
        </div>
        )}

        {/* CTA Section */}
        <div className="text-center mb-16 bg-gradient-to-r from-teal-500 via-cyan-500 to-blue-500 rounded-3xl p-12 text-white">
          <Sparkles className="h-16 w-16 mx-auto mb-6 opacity-90" />
          <h2 className="text-3xl md:text-4xl font-bold mb-4">
            Ready to Learn Smarter, Not Harder?
          </h2>
          <p className="text-xl text-white/80 mb-8 max-w-2xl mx-auto">
            Join thousands of students who are using AI to crack competitive exams. Your personalized AI tutor is waiting.
          </p>
          <Link href={isAuthenticated ? "/home" : "/register"}>
            <Button size="lg" variant="secondary" className="px-8 text-teal-600 font-semibold">
              <Zap className="mr-2 h-5 w-5" />
              {isAuthenticated ? "Go to Dashboard" : "Start Free AI Trial"}
            </Button>
          </Link>
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t bg-white py-8">
        <div className="container mx-auto px-4">
          <div className="flex flex-col md:flex-row items-center justify-between gap-4">
            <div className="flex items-center gap-2">
              <Link href="/">
                <div className="w-8 h-8 bg-gradient-to-br from-teal-500 to-cyan-600 rounded-lg flex items-center justify-center hover:opacity-90 transition-opacity cursor-pointer">
                  <Sparkles className="h-5 w-5 text-white" />
                </div>
              </Link>
              <Link href="/home">
                <span className="font-bold text-slate-900 hover:text-teal-600 transition-colors cursor-pointer">EnsureU</span>
              </Link>
              <span className="text-slate-500">- AI-Powered Exam Preparation</span>
            </div>
            <p className="text-slate-500 text-sm">&copy; 2026 EnsureU. All rights reserved.</p>
          </div>
        </div>
      </footer>

      {/* Paper List Dialog */}
      <Dialog open={!!showPaperList} onOpenChange={() => setShowPaperList(null)}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>{showPaperList?.paperCategory} - Tests Included</DialogTitle>
            <DialogDescription>
              {showPaperList?.paperInfoList?.length || 0} AI-enhanced tests in this package
            </DialogDescription>
          </DialogHeader>
          <div className="max-h-80 overflow-y-auto space-y-2">
            {showPaperList?.paperInfoList?.map((paper, idx) => (
              <div
                key={paper.id}
                className="flex items-center gap-3 p-2 rounded-lg hover:bg-slate-50"
              >
                <span className="text-sm text-slate-400 w-6">{idx + 1}.</span>
                <Check className="h-4 w-4 text-green-500" />
                <span className="text-sm text-slate-700">{paper.paperName}</span>
              </div>
            ))}
          </div>
        </DialogContent>
      </Dialog>

      {/* Purchase Dialog */}
      <Dialog open={!!selectedSubscription} onOpenChange={() => setSelectedSubscription(null)}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <Sparkles className="h-5 w-5 text-teal-600" />
              Unlock AI Features
            </DialogTitle>
            <DialogDescription>
              {selectedSubscription?.paperCategory} - {selectedSubscription?.paperSubCategory.replace(/_/g, ' ')}
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-3">
              {selectedSubscription?.mapOfSubTypeVsPrice &&
                Object.entries(selectedSubscription.mapOfSubTypeVsPrice).map(([period, priceData]) => {
                  const priceInfo = priceData as PriceMetadata;
                  const isHighestDiscount = Object.values(selectedSubscription.mapOfSubTypeVsPrice || {})
                    .every((p) => (p as PriceMetadata).discountPercentage <= priceInfo.discountPercentage);

                  return (
                    <button
                      key={period}
                      onClick={() => setSelectedPeriod(period as SubscriptionType)}
                      className={`p-4 rounded-lg border-2 text-left transition-all ${
                        selectedPeriod === period
                          ? 'border-teal-500 bg-teal-50'
                          : 'border-slate-200 hover:border-slate-300'
                      }`}
                    >
                      <p className="font-semibold text-slate-900">
                        {SUBSCRIPTION_LABELS[period as SubscriptionType] || period}
                      </p>
                      <div className="flex items-baseline gap-1">
                        <span className="text-xl font-bold text-teal-600">
                          {formatPrice(priceInfo.discountedPrice || priceInfo.price)}
                        </span>
                        {priceInfo.discountedPrice && priceInfo.discountedPrice < priceInfo.price && (
                          <span className="text-sm text-slate-400 line-through">
                            {formatPrice(priceInfo.price)}
                          </span>
                        )}
                      </div>
                      {priceInfo.discountPercentage > 0 && (
                        <div className="flex items-center gap-1 mt-1">
                          <Badge variant={isHighestDiscount ? 'default' : 'secondary'} className="text-xs">
                            {priceInfo.discountPercentage}% off
                          </Badge>
                        </div>
                      )}
                    </button>
                  );
                })}
            </div>

            {/* AI Features included */}
            <div className="p-4 bg-gradient-to-r from-teal-50 to-cyan-50 rounded-lg">
              <p className="font-medium text-slate-900 mb-2">AI Features Included:</p>
              <div className="grid grid-cols-2 gap-2 text-sm">
                <div className="flex items-center gap-1 text-slate-600">
                  <Check className="h-4 w-4 text-green-500" />
                  Wrong Answer AI Tutor
                </div>
                <div className="flex items-center gap-1 text-slate-600">
                  <Check className="h-4 w-4 text-green-500" />
                  Personalized Study Plan
                </div>
                <div className="flex items-center gap-1 text-slate-600">
                  <Check className="h-4 w-4 text-green-500" />
                  Score Prediction
                </div>
                <div className="flex items-center gap-1 text-slate-600">
                  <Check className="h-4 w-4 text-green-500" />
                  Weakness Analysis
                </div>
              </div>
            </div>

            {(() => {
              const selectedPriceInfo = selectedSubscription?.mapOfSubTypeVsPrice?.[selectedPeriod] as PriceMetadata | undefined;
              return (
                <div className="p-4 bg-slate-50 rounded-lg">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-slate-600">Tests Included:</span>
                    <span className="font-semibold">
                      {selectedSubscription?.paperInfoList?.length || 0}
                    </span>
                  </div>
                  <div className="flex items-center justify-between pt-2 border-t">
                    <span className="text-slate-900 font-semibold">Total:</span>
                    <span className="text-2xl font-bold text-teal-600">
                      {formatPrice(selectedPriceInfo?.discountedPrice || selectedPriceInfo?.price || 0)}
                    </span>
                  </div>
                </div>
              );
            })()}

            <Button
              className="w-full bg-gradient-to-r from-teal-500 to-cyan-600 hover:from-teal-600 hover:to-cyan-700"
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
                  <Sparkles className="h-4 w-4 mr-2" />
                  Unlock AI Learning
                </>
              )}
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
