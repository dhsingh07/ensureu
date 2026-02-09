'use client';

import { useEffect, useMemo, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { useAuthStore } from '@/stores/auth-store';
import {
  useBlogCategories,
  useBlogList,
  useBlogByCategory,
  useBlogByTitle,
  useTrendingBlogs,
  useBlogByUser,
} from '@/hooks/use-blog';
import type { BlogCategory, BlogItem } from '@/types/blog';
import {
  Flame,
  Search,
  Calendar,
  Eye,
  Heart,
  ArrowRight,
  BookOpenText,
  Sparkles,
} from 'lucide-react';

const sampleBlogs: BlogItem[] = [
  {
    id: 'sample-1',
    title: '10 Proven Study Tips to Crack SSC CGL in First Attempt',
    body: '<p>Preparing for SSC CGL requires a strategic approach. Start with understanding the exam pattern thoroughly — Tier I covers General Intelligence, General Awareness, Quantitative Aptitude, and English Comprehension.</p>',
    thumbnailUrl: 'assets/img/blog/blog-study-tips.svg',
    category: { id: 'cat-1', name: 'Study Tips' },
    tags: 'SSC CGL, Study Tips, Exam Strategy',
    views: 2456,
    likes: ['user1', 'user2', 'user3'],
    createdDate: new Date('2025-12-15').getTime(),
    author: 'EnsureU Team',
    priority: 1,
  },
  {
    id: 'sample-2',
    title: 'Complete Exam Preparation Roadmap for Bank PO 2025',
    body: '<p>Bank PO examination demands a balanced preparation strategy across Reasoning, Quantitative Aptitude, English Language, General Awareness, and Computer Knowledge.</p>',
    thumbnailUrl: 'assets/img/blog/blog-exam-prep.svg',
    category: { id: 'cat-2', name: 'Exam Preparation' },
    tags: 'Bank PO, Preparation, Roadmap',
    views: 1893,
    likes: ['user1', 'user3', 'user5'],
    createdDate: new Date('2025-11-28').getTime(),
    author: 'EnsureU Team',
    priority: 2,
  },
  {
    id: 'sample-3',
    title: 'SSC Exam Pattern 2025: Everything You Need to Know',
    body: '<p>The Staff Selection Commission conducts multiple examinations throughout the year — CGL, CHSL, CPO, and more. Each exam has its unique pattern and syllabus.</p>',
    thumbnailUrl: 'assets/img/blog/blog-ssc-guide.svg',
    category: { id: 'cat-3', name: 'SSC Guide' },
    tags: 'SSC, Exam Pattern, Syllabus',
    views: 3241,
    likes: ['user1', 'user2', 'user4'],
    createdDate: new Date('2025-12-02').getTime(),
    author: 'EnsureU Team',
    priority: 3,
  },
];

const fallbackCategories: BlogCategory[] = [
  { id: 'cat-1', name: 'Study Tips' },
  { id: 'cat-2', name: 'Exam Preparation' },
  { id: 'cat-3', name: 'SSC Guide' },
  { id: 'cat-4', name: 'Success Stories' },
  { id: 'cat-5', name: 'Subject Guide' },
];

const getReadingTime = (body?: string) => {
  if (!body) return 1;
  const text = body.replace(/<[^>]*>/g, '');
  const words = text.trim().split(/\s+/).length;
  return Math.max(1, Math.ceil(words / 200));
};

const getExcerpt = (body?: string, maxLength = 140) => {
  if (!body) return '';
  const text = body.replace(/<[^>]*>/g, '');
  return text.length > maxLength ? `${text.slice(0, maxLength)}...` : text;
};

export default function BlogPage() {
  const router = useRouter();
  const user = useAuthStore((state) => state.user);
  const isAdmin = user?.roles?.some((role) =>
    ['ADMIN', 'SUPERADMIN', 'TEACHER'].includes(role.roleType)
  );

  const [page, setPage] = useState(0);
  const [blogList, setBlogList] = useState<BlogItem[]>([]);
  const [searchFilter, setSearchFilter] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [loadMore, setLoadMore] = useState(true);
  const [usingSample, setUsingSample] = useState(false);

  const mode = searchFilter ? 'search' : selectedCategory ? 'category' : 'all';

  const { data: categoriesData } = useBlogCategories();
  const categories = categoriesData?.length ? categoriesData : fallbackCategories;

  const { data: trendingData } = useTrendingBlogs();
  const trendingList = trendingData?.length ? trendingData : sampleBlogs;

  const { data: listData, isLoading: loadingList } = useBlogList(
    page,
    mode === 'all'
  );
  const { data: categoryData, isLoading: loadingCategory } = useBlogByCategory(
    selectedCategory,
    page,
    mode === 'category'
  );
  const { data: searchData, isLoading: loadingSearch } = useBlogByTitle(
    searchFilter,
    mode === 'search'
  );

  const { data: userBlogs } = useBlogByUser(
    user?.userName || '',
    0,
    !!isAdmin
  );

  const activeData = useMemo(() => {
    if (mode === 'search') return searchData || [];
    if (mode === 'category') return categoryData || [];
    return listData || [];
  }, [mode, listData, categoryData, searchData]);

  useEffect(() => {
    if (!activeData) return;

    if (mode === 'search') {
      setBlogList(activeData);
      setLoadMore(false);
      return;
    }

    if (page === 0) {
      if (activeData.length === 0 && mode === 'all') {
        setBlogList(sampleBlogs);
        setUsingSample(true);
        setLoadMore(false);
      } else {
        setBlogList(activeData);
        setUsingSample(false);
        setLoadMore(activeData.length > 0);
      }
    } else {
      setBlogList((prev) => [...prev, ...activeData]);
      if (activeData.length === 0) {
        setLoadMore(false);
      }
    }
  }, [activeData, mode, page]);

  useEffect(() => {
    setPage(0);
    setLoadMore(true);
  }, [searchFilter, selectedCategory]);

  const featuredBlog = blogList[0];

  const isLoading = loadingList || loadingCategory || loadingSearch;

  return (
    <div className="relative overflow-hidden">
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_top,_rgba(15,118,110,0.12),_transparent_55%)]" />
      <div className="absolute -top-24 right-10 h-64 w-64 rounded-full bg-teal-200/40 blur-3xl" />
      <div className="absolute top-40 -left-10 h-72 w-72 rounded-full bg-amber-200/40 blur-3xl" />

      <section className="relative container mx-auto px-4 pt-16 pb-10">
        <div className="mx-auto max-w-3xl text-center space-y-4">
          <span className="inline-flex items-center gap-2 rounded-full bg-slate-900 text-white px-4 py-2 text-xs uppercase tracking-widest">
            <Sparkles className="h-4 w-4" />
            Knowledge Hub
          </span>
          <h1 className="text-4xl md:text-5xl font-bold text-slate-900">
            Latest Articles & Insights
          </h1>
          <p className="text-base md:text-lg text-slate-600">
            Stay updated with exam tips, study strategies, and preparation guidance.
          </p>
          <div className="flex flex-wrap items-center justify-center gap-6 text-sm text-slate-500">
            <div>{blogList.length} articles</div>
            <div>{categories.length} categories</div>
            <div>{trendingList.length} trending</div>
          </div>
        </div>
      </section>

      {featuredBlog && (
        <section className="relative container mx-auto px-4 pb-12">
          <Card className="overflow-hidden border border-slate-200 shadow-lg">
            <div className="grid gap-0 lg:grid-cols-[1.1fr_1fr]">
              <div className="relative h-64 lg:h-full bg-slate-100">
                {featuredBlog.thumbnailUrl && (
                  <img
                    src={featuredBlog.thumbnailUrl}
                    alt={featuredBlog.title}
                    className="h-full w-full object-cover"
                  />
                )}
                <div className="absolute left-6 top-6 rounded-full bg-white/90 px-3 py-1 text-xs font-semibold text-slate-700">
                  Featured
                </div>
              </div>
              <CardContent className="p-6 lg:p-8 space-y-4">
                <div className="flex items-center gap-3 text-xs text-slate-500">
                  <Badge variant="secondary">{featuredBlog.category?.name}</Badge>
                  <span className="flex items-center gap-1">
                    <BookOpenText className="h-3 w-3" />
                    {getReadingTime(featuredBlog.body)} min read
                  </span>
                </div>
                <h2 className="text-2xl font-semibold text-slate-900">
                  {featuredBlog.title}
                </h2>
                <p className="text-sm text-slate-600">
                  {getExcerpt(featuredBlog.body, 200)}
                </p>
                <div className="flex flex-wrap items-center justify-between gap-4 text-sm text-slate-500">
                  <div>
                    {featuredBlog.author || 'EnsureU Team'} •{' '}
                    {featuredBlog.createdDate
                      ? new Date(featuredBlog.createdDate).toLocaleDateString()
                      : 'Latest'}
                  </div>
                  <Button
                    className="gap-2"
                    onClick={() =>
                      router.push(`/blog/detail?blogId=${featuredBlog.id}`)
                    }
                  >
                    Read article
                    <ArrowRight className="h-4 w-4" />
                  </Button>
                </div>
              </CardContent>
            </div>
          </Card>
        </section>
      )}

      <section className="relative container mx-auto px-4 pb-16">
        <div className="grid gap-8 lg:grid-cols-[1fr_320px]">
          <div className="space-y-6">
            <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
              <div className="flex items-center gap-2">
                <h2 className="text-2xl font-semibold text-slate-900">All Articles</h2>
                <span className="text-sm text-slate-500">
                  {blogList.length} articles
                </span>
              </div>
              <div className="relative w-full md:w-72">
                <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
                <Input
                  className="pl-10"
                  placeholder="Search articles..."
                  value={searchFilter}
                  onChange={(event) => setSearchFilter(event.target.value)}
                />
              </div>
            </div>

            {isLoading && (
              <Card className="border border-slate-200">
                <CardContent className="p-6 text-slate-500">
                  Loading articles...
                </CardContent>
              </Card>
            )}

            {!isLoading && blogList.length === 0 && (
              <Card className="border border-slate-200">
                <CardContent className="p-6 text-center text-slate-500">
                  No articles found. Try resetting filters.
                </CardContent>
              </Card>
            )}

            <div className="grid gap-6 md:grid-cols-2">
              {blogList.map((blog) => (
                <Card key={blog.id} className="border border-slate-200 overflow-hidden">
                  <div className="relative h-40 bg-slate-100">
                    {blog.thumbnailUrl && (
                      <img
                        src={blog.thumbnailUrl}
                        alt={blog.title}
                        className="h-full w-full object-cover"
                      />
                    )}
                    <div className="absolute bottom-3 left-3 flex gap-2 text-xs text-white">
                      <span className="rounded-full bg-slate-900/80 px-2 py-1 flex items-center gap-1">
                        <Eye className="h-3 w-3" /> {blog.views || 0}
                      </span>
                      <span className="rounded-full bg-slate-900/80 px-2 py-1 flex items-center gap-1">
                        <Heart className="h-3 w-3" /> {blog.likes?.length || 0}
                      </span>
                    </div>
                  </div>
                  <CardContent className="p-5 space-y-3">
                    <div className="flex flex-wrap items-center gap-2 text-xs text-slate-500">
                      <Badge variant="secondary">{blog.category?.name || 'General'}</Badge>
                      <span className="flex items-center gap-1">
                        <Calendar className="h-3 w-3" />
                        {blog.createdDate
                          ? new Date(blog.createdDate).toLocaleDateString()
                          : 'Latest'}
                      </span>
                    </div>
                    <h3 className="text-lg font-semibold text-slate-900">
                      {blog.title}
                    </h3>
                    <p className="text-sm text-slate-600">{getExcerpt(blog.body)}</p>
                    <Button
                      variant="ghost"
                      className="gap-2 px-0"
                      onClick={() =>
                        router.push(`/blog/detail?blogId=${blog.id}`)
                      }
                    >
                      Read more <ArrowRight className="h-4 w-4" />
                    </Button>
                  </CardContent>
                </Card>
              ))}
            </div>

            {loadMore && mode !== 'search' && !usingSample && (
              <div className="flex justify-center">
                <Button
                  variant="outline"
                  onClick={() => setPage((prev) => prev + 1)}
                  disabled={isLoading}
                >
                  Load more
                </Button>
              </div>
            )}
          </div>

          <aside className="space-y-6">
            <Card className="border border-slate-200">
              <CardContent className="p-5 space-y-4">
                <div className="flex items-center gap-2 text-slate-800 font-semibold">
                  <Flame className="h-4 w-4 text-rose-500" />
                  Trending
                </div>
                <div className="space-y-3">
                  {trendingList.map((trend, index) => (
                    <button
                      key={trend.id}
                      onClick={() => router.push(`/blog/detail?blogId=${trend.id}`)}
                      className="flex w-full items-start gap-3 text-left"
                    >
                      <span className="text-sm font-semibold text-slate-400">
                        {index + 1}
                      </span>
                      <div>
                        <div className="text-sm font-medium text-slate-800">
                          {trend.title}
                        </div>
                        <div className="text-xs text-slate-500 flex items-center gap-2">
                          <Eye className="h-3 w-3" /> {trend.views || 0}
                          <span>•</span>
                          {getReadingTime(trend.body)} min
                        </div>
                      </div>
                    </button>
                  ))}
                </div>
              </CardContent>
            </Card>

            <Card className="border border-slate-200">
              <CardContent className="p-5 space-y-4">
                <div className="flex items-center justify-between">
                  <div className="text-slate-800 font-semibold">Categories</div>
                  {(selectedCategory || searchFilter) && (
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => {
                        setSelectedCategory('');
                        setSearchFilter('');
                      }}
                    >
                      Reset
                    </Button>
                  )}
                </div>
                <div className="space-y-2">
                  {categories.map((category) => (
                    <button
                      key={category.id}
                      onClick={() => setSelectedCategory(category.id)}
                      className={`w-full rounded-lg border px-3 py-2 text-left text-sm transition ${
                        selectedCategory === category.id
                          ? 'border-slate-900 bg-slate-900 text-white'
                          : 'border-slate-200 hover:border-slate-300'
                      }`}
                    >
                      {category.name}
                    </button>
                  ))}
                </div>
              </CardContent>
            </Card>

            <Card className="border border-slate-200">
              <CardContent className="p-5 space-y-4 text-sm text-slate-600">
                <h4 className="font-semibold text-slate-800">Stay Updated</h4>
                <p>Get the latest exam strategies delivered to your inbox.</p>
                <div className="flex gap-2">
                  <Input placeholder="Email address" />
                  <Button size="sm">Subscribe</Button>
                </div>
              </CardContent>
            </Card>

            {isAdmin && userBlogs && userBlogs.length > 0 && (
              <Card className="border border-slate-200">
                <CardContent className="p-5 space-y-4">
                  <h4 className="font-semibold text-slate-800">Your Articles</h4>
                  <div className="space-y-2">
                    {userBlogs.map((blog) => (
                      <Link
                        key={blog.id}
                        href={`/blog/detail?blogId=${blog.id}`}
                        className="block text-sm text-slate-600 hover:text-slate-900"
                      >
                        {blog.title}
                      </Link>
                    ))}
                  </div>
                </CardContent>
              </Card>
            )}
          </aside>
        </div>
      </section>
    </div>
  );
}

