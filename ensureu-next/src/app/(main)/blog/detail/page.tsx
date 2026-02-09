'use client';

import { Suspense, useEffect, useMemo, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Textarea } from '@/components/ui/textarea';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { useAuthStore } from '@/stores/auth-store';
import { useAddBlogComment, useBlogComments, useBlogDetail, useToggleBlogLike } from '@/hooks/use-blog';
import type { BlogComment, BlogItem, BlogCommentEntry } from '@/types/blog';
import {
  ArrowLeft,
  Calendar,
  Clock,
  Eye,
  Heart,
  MessageSquare,
  Share2,
  User,
  BookOpen,
  Send,
} from 'lucide-react';

const sampleBlogs: BlogItem[] = [
  {
    id: 'sample-1',
    title: '10 Proven Study Tips to Crack SSC CGL in First Attempt',
    body: `<p>Preparing for SSC CGL requires a strategic approach. Start with understanding the exam pattern thoroughly — Tier I covers General Intelligence, General Awareness, Quantitative Aptitude, and English Comprehension.</p>
    <h2>1. Understand the Exam Pattern</h2>
    <p>The SSC CGL exam is conducted in multiple tiers. Tier I is the preliminary test with objective questions, while Tier II involves more detailed testing. Understanding this structure helps you allocate your preparation time effectively.</p>
    <h2>2. Create a Study Schedule</h2>
    <p>Divide your day into dedicated study blocks. Allocate more time to subjects you find challenging while maintaining regular practice in your stronger areas.</p>
    <h2>3. Practice Previous Year Papers</h2>
    <p>Nothing beats practicing with actual exam papers. It helps you understand the question patterns and time management required during the actual exam.</p>
    <h2>4. Focus on Current Affairs</h2>
    <p>General Awareness is a scoring section if you stay updated with current events. Read newspapers daily and make notes of important events.</p>
    <h2>5. Take Regular Mock Tests</h2>
    <p>Mock tests simulate the actual exam environment and help you identify your weaknesses before the final exam.</p>`,
    thumbnailUrl: '/assets/img/blog/blog-study-tips.svg',
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
    thumbnailUrl: '/assets/img/blog/blog-exam-prep.svg',
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
    thumbnailUrl: '/assets/img/blog/blog-ssc-guide.svg',
    category: { id: 'cat-3', name: 'SSC Guide' },
    tags: 'SSC, Exam Pattern, Syllabus',
    views: 3241,
    likes: ['user1', 'user2', 'user4'],
    createdDate: new Date('2025-12-02').getTime(),
    author: 'EnsureU Team',
    priority: 3,
  },
];

const sampleComments: BlogComment[] = [
  {
    id: 'comment-1',
    blogId: '',
    comments: [
      {
        body: 'Great article! Very helpful for my SSC preparation. The study tips are practical and easy to follow.',
        createdBy: 'Rahul Kumar',
        createdDate: new Date('2025-12-16').getTime(),
      },
    ],
  },
  {
    id: 'comment-2',
    blogId: '',
    comments: [
      {
        body: 'Thanks for sharing this detailed roadmap. It gave me a clear direction for my preparation journey.',
        createdBy: 'Priya Singh',
        createdDate: new Date('2025-12-17').getTime(),
      },
    ],
  },
];

const getTagList = (tags?: string | { name?: string }[]) => {
  if (!tags) return [];
  if (Array.isArray(tags)) {
    return tags.map((tag) => tag.name || '').filter(Boolean);
  }
  return tags.split(',').map((tag) => tag.trim());
};

const getReadingTime = (body?: string) => {
  if (!body) return 1;
  const text = body.replace(/<[^>]*>/g, '');
  const words = text.trim().split(/\s+/).length;
  return Math.max(1, Math.ceil(words / 200));
};

const getInitials = (name?: string) => {
  if (!name) return 'U';
  return name
    .split(' ')
    .map((n) => n[0])
    .join('')
    .toUpperCase()
    .slice(0, 2);
};

const formatDate = (timestamp?: number) => {
  if (!timestamp) return 'Recently';
  return new Date(timestamp).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
};

// Flatten all comments from a BlogComment (which may have multiple entries)
const getAllCommentEntries = (commentsData: BlogComment[] | BlogComment | null | undefined): BlogCommentEntry[] => {
  if (!commentsData) return [];

  // If it's a single BlogComment object (not an array)
  if (!Array.isArray(commentsData)) {
    return commentsData.comments || [];
  }

  // If it's an array of BlogComment objects, flatten all comments
  return commentsData.flatMap((comment) => comment.comments || []);
};

function BlogDetailContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const blogId = searchParams.get('blogId');
  const user = useAuthStore((state) => state.user);
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  const { data: blogData, isLoading: blogLoading } = useBlogDetail(blogId || undefined);
  const { data: commentsData, isLoading: commentsLoading } = useBlogComments(blogId || undefined);
  const addCommentMutation = useAddBlogComment();
  const toggleLikeMutation = useToggleBlogLike();

  const [localComments, setLocalComments] = useState<BlogCommentEntry[]>([]);
  const [commentBody, setCommentBody] = useState('');

  const isSample = blogId?.startsWith('sample-');

  const blog = useMemo(() => {
    if (blogData) return blogData;
    if (isSample && blogId) {
      return sampleBlogs.find((item) => item.id === blogId) || null;
    }
    return null;
  }, [blogData, blogId, isSample]);

  useEffect(() => {
    if (!blogId) {
      router.replace('/blog');
    }
  }, [blogId, router]);

  useEffect(() => {
    if (isSample && blogId) {
      const entries = sampleComments.flatMap((c) => c.comments || []);
      setLocalComments(entries);
    }
  }, [isSample, blogId]);

  // Get flattened comment entries
  const commentEntries = isSample ? localComments : getAllCommentEntries(commentsData);

  const handleSubmitComment = () => {
    if (!blogId || !commentBody.trim()) return;

    const newEntry: BlogCommentEntry = {
      body: commentBody,
      createdBy: user?.firstName ? `${user.firstName} ${user.lastName || ''}`.trim() : user?.userName || 'Anonymous',
      createdDate: Date.now(),
    };

    if (isSample) {
      setLocalComments((prev) => [...prev, newEntry]);
      setCommentBody('');
      return;
    }

    addCommentMutation.mutate({
      id: '',
      blogId,
      comments: [newEntry],
    });
    setCommentBody('');
  };

  const userLiked = blog?.likes?.includes(user?.userName || '');

  if (blogLoading) {
    return (
      <div className="min-h-screen bg-gradient-to-b from-slate-50 to-white">
        <div className="container mx-auto px-4 py-16">
          <div className="animate-pulse space-y-6 max-w-4xl mx-auto">
            <div className="h-8 bg-slate-200 rounded w-24" />
            <div className="h-12 bg-slate-200 rounded w-3/4" />
            <div className="h-64 bg-slate-200 rounded-2xl" />
            <div className="space-y-3">
              <div className="h-4 bg-slate-200 rounded w-full" />
              <div className="h-4 bg-slate-200 rounded w-5/6" />
              <div className="h-4 bg-slate-200 rounded w-4/6" />
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (!blog) {
    return (
      <div className="min-h-screen bg-gradient-to-b from-slate-50 to-white flex items-center justify-center">
        <div className="text-center space-y-4">
          <BookOpen className="h-16 w-16 text-slate-300 mx-auto" />
          <h2 className="text-xl font-semibold text-slate-700">Article not found</h2>
          <p className="text-slate-500">The article you're looking for doesn't exist.</p>
          <Button onClick={() => router.push('/blog')}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Articles
          </Button>
        </div>
      </div>
    );
  }

  const tagList = getTagList(blog.tags);
  const readingTime = getReadingTime(blog.body);

  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-50 to-white">
      {/* Hero Section */}
      <div className="relative bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 text-white overflow-hidden">
        <div className="absolute inset-0 bg-[url('/assets/img/grid-pattern.svg')] opacity-5" />
        <div className="absolute top-0 right-0 w-96 h-96 bg-teal-500/10 rounded-full blur-3xl" />
        <div className="absolute bottom-0 left-0 w-72 h-72 bg-blue-500/10 rounded-full blur-3xl" />

        <div className="relative container mx-auto px-4 py-12 md:py-16">
          <button
            className="inline-flex items-center gap-2 text-sm text-slate-300 hover:text-white transition-colors mb-8"
            onClick={() => router.push('/blog')}
          >
            <ArrowLeft className="h-4 w-4" />
            Back to Articles
          </button>

          <div className="max-w-4xl">
            <div className="flex flex-wrap items-center gap-3 mb-4">
              <Badge className="bg-teal-500/20 text-teal-300 border-teal-500/30 hover:bg-teal-500/30">
                {blog.category?.name || 'General'}
              </Badge>
              <span className="flex items-center gap-1 text-sm text-slate-400">
                <Clock className="h-3.5 w-3.5" />
                {readingTime} min read
              </span>
              <span className="flex items-center gap-1 text-sm text-slate-400">
                <Calendar className="h-3.5 w-3.5" />
                {formatDate(blog.createdDate)}
              </span>
            </div>

            <h1 className="text-3xl md:text-4xl lg:text-5xl font-bold leading-tight mb-6">
              {blog.title}
            </h1>

            <div className="flex flex-wrap items-center gap-6">
              <div className="flex items-center gap-3">
                <Avatar className="h-10 w-10 border-2 border-white/20">
                  <AvatarFallback className="bg-teal-600 text-white text-sm">
                    {getInitials(blog.author)}
                  </AvatarFallback>
                </Avatar>
                <div>
                  <div className="font-medium">{blog.author || 'EnsureU Team'}</div>
                  <div className="text-sm text-slate-400">Author</div>
                </div>
              </div>

              <div className="flex items-center gap-4 text-sm text-slate-400">
                <span className="flex items-center gap-1">
                  <Eye className="h-4 w-4" />
                  {blog.views || 0} views
                </span>
                <span className="flex items-center gap-1">
                  <Heart className={`h-4 w-4 ${userLiked ? 'fill-rose-400 text-rose-400' : ''}`} />
                  {blog.likes?.length || 0} likes
                </span>
                <span className="flex items-center gap-1">
                  <MessageSquare className="h-4 w-4" />
                  {commentEntries.length} comments
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="container mx-auto px-4 py-10">
        <div className="grid gap-8 lg:grid-cols-[1fr_320px]">
          {/* Article Content */}
          <div className="space-y-8">
            {/* Thumbnail */}
            {blog.thumbnailUrl && (
              <div className="overflow-hidden rounded-2xl bg-slate-100 shadow-lg -mt-20 relative z-10">
                <img
                  src={blog.thumbnailUrl}
                  alt={blog.title}
                  className="w-full h-auto max-h-[400px] object-cover"
                />
              </div>
            )}

            {/* Article Body */}
            <Card className="border-0 shadow-lg">
              <CardContent className="p-6 md:p-10">
                <article
                  className="prose prose-slate prose-lg max-w-none
                    prose-headings:text-slate-900 prose-headings:font-bold
                    prose-h2:text-2xl prose-h2:mt-8 prose-h2:mb-4
                    prose-h3:text-xl prose-h3:mt-6 prose-h3:mb-3
                    prose-p:text-slate-600 prose-p:leading-relaxed
                    prose-a:text-teal-600 prose-a:no-underline hover:prose-a:underline
                    prose-strong:text-slate-800
                    prose-ul:text-slate-600 prose-ol:text-slate-600
                    prose-li:marker:text-teal-500"
                  dangerouslySetInnerHTML={{ __html: blog.body }}
                />

                {/* Tags */}
                {tagList.length > 0 && (
                  <div className="mt-10 pt-6 border-t">
                    <div className="flex flex-wrap gap-2">
                      {tagList.map((tag) => (
                        <Badge
                          key={tag}
                          variant="secondary"
                          className="bg-slate-100 text-slate-600 hover:bg-slate-200"
                        >
                          #{tag}
                        </Badge>
                      ))}
                    </div>
                  </div>
                )}

                {/* Actions */}
                <div className="mt-8 pt-6 border-t flex flex-wrap items-center justify-between gap-4">
                  <div className="flex items-center gap-3">
                    <Button
                      variant={userLiked ? 'default' : 'outline'}
                      size="sm"
                      className={userLiked ? 'bg-rose-500 hover:bg-rose-600' : ''}
                      disabled={!isAuthenticated || toggleLikeMutation.isPending}
                      onClick={() => {
                        if (!blogId || !user) return;
                        toggleLikeMutation.mutate({
                          blogId,
                          isDislike: userLiked || false,
                          userId: user.userName,
                        });
                      }}
                    >
                      <Heart className={`h-4 w-4 mr-2 ${userLiked ? 'fill-current' : ''}`} />
                      {userLiked ? 'Liked' : 'Like'} ({blog.likes?.length || 0})
                    </Button>
                    {!isAuthenticated && (
                      <span className="text-xs text-slate-500">
                        <Link href="/login" className="text-teal-600 hover:underline">Login</Link> to like
                      </span>
                    )}
                  </div>
                  <Button variant="ghost" size="sm">
                    <Share2 className="h-4 w-4 mr-2" />
                    Share
                  </Button>
                </div>
              </CardContent>
            </Card>

            {/* Comments Section */}
            <Card className="border-0 shadow-lg">
              <CardContent className="p-6 md:p-8">
                <div className="flex items-center gap-3 mb-6">
                  <MessageSquare className="h-5 w-5 text-teal-600" />
                  <h2 className="text-xl font-bold text-slate-900">
                    Comments ({commentEntries.length})
                  </h2>
                </div>

                {/* Comment Form */}
                <div className="mb-8 p-4 bg-slate-50 rounded-xl">
                  {isAuthenticated ? (
                    <div className="space-y-4">
                      <div className="flex items-start gap-3">
                        <Avatar className="h-10 w-10">
                          <AvatarFallback className="bg-teal-600 text-white text-sm">
                            {getInitials(user?.firstName || user?.userName)}
                          </AvatarFallback>
                        </Avatar>
                        <div className="flex-1">
                          <Textarea
                            placeholder="Share your thoughts on this article..."
                            value={commentBody}
                            onChange={(e) => setCommentBody(e.target.value)}
                            className="min-h-[100px] bg-white resize-none"
                          />
                        </div>
                      </div>
                      <div className="flex justify-end">
                        <Button
                          onClick={handleSubmitComment}
                          disabled={!commentBody.trim() || addCommentMutation.isPending}
                        >
                          <Send className="h-4 w-4 mr-2" />
                          {addCommentMutation.isPending ? 'Posting...' : 'Post Comment'}
                        </Button>
                      </div>
                    </div>
                  ) : (
                    <div className="text-center py-4">
                      <User className="h-10 w-10 text-slate-300 mx-auto mb-3" />
                      <p className="text-slate-600 mb-3">Join the discussion</p>
                      <Link href="/login">
                        <Button size="sm">Login to Comment</Button>
                      </Link>
                    </div>
                  )}
                </div>

                {/* Comments List */}
                {commentsLoading ? (
                  <div className="space-y-4">
                    {[1, 2].map((i) => (
                      <div key={i} className="animate-pulse flex gap-3">
                        <div className="h-10 w-10 bg-slate-200 rounded-full" />
                        <div className="flex-1 space-y-2">
                          <div className="h-4 bg-slate-200 rounded w-24" />
                          <div className="h-3 bg-slate-200 rounded w-full" />
                          <div className="h-3 bg-slate-200 rounded w-3/4" />
                        </div>
                      </div>
                    ))}
                  </div>
                ) : commentEntries.length === 0 ? (
                  <div className="text-center py-8">
                    <MessageSquare className="h-12 w-12 text-slate-200 mx-auto mb-3" />
                    <p className="text-slate-500">No comments yet. Be the first to share your thoughts!</p>
                  </div>
                ) : (
                  <div className="space-y-6">
                    {commentEntries.map((entry, index) => (
                      <div key={index} className="flex gap-4">
                        <Avatar className="h-10 w-10 flex-shrink-0">
                          <AvatarFallback className="bg-slate-200 text-slate-600 text-sm">
                            {getInitials(entry.createdBy)}
                          </AvatarFallback>
                        </Avatar>
                        <div className="flex-1 min-w-0">
                          <div className="flex items-center gap-2 mb-1">
                            <span className="font-medium text-slate-900">
                              {entry.createdBy || 'Anonymous'}
                            </span>
                            <span className="text-xs text-slate-400">
                              {formatDate(entry.createdDate)}
                            </span>
                          </div>
                          <p className="text-slate-600 leading-relaxed">{entry.body}</p>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </div>

          {/* Sidebar */}
          <aside className="space-y-6">
            {/* Author Card */}
            <Card className="border-0 shadow-lg sticky top-6">
              <CardContent className="p-6">
                <h3 className="font-semibold text-slate-900 mb-4">About the Author</h3>
                <div className="flex items-center gap-3 mb-4">
                  <Avatar className="h-14 w-14">
                    <AvatarFallback className="bg-teal-600 text-white text-lg">
                      {getInitials(blog.author)}
                    </AvatarFallback>
                  </Avatar>
                  <div>
                    <div className="font-medium text-slate-900">{blog.author || 'EnsureU Team'}</div>
                    <div className="text-sm text-slate-500">Content Creator</div>
                  </div>
                </div>
                <p className="text-sm text-slate-600 mb-4">
                  Dedicated to helping students achieve their exam goals with expert tips and strategies.
                </p>
                <Separator className="my-4" />
                <div className="grid grid-cols-2 gap-4 text-center">
                  <div>
                    <div className="text-2xl font-bold text-slate-900">{blog.views || 0}</div>
                    <div className="text-xs text-slate-500">Views</div>
                  </div>
                  <div>
                    <div className="text-2xl font-bold text-slate-900">{blog.likes?.length || 0}</div>
                    <div className="text-xs text-slate-500">Likes</div>
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* Quick Stats */}
            <Card className="border-0 shadow-lg">
              <CardContent className="p-6">
                <h3 className="font-semibold text-slate-900 mb-4">Article Info</h3>
                <div className="space-y-3 text-sm">
                  <div className="flex justify-between">
                    <span className="text-slate-500">Reading Time</span>
                    <span className="font-medium text-slate-900">{readingTime} min</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-500">Category</span>
                    <span className="font-medium text-slate-900">{blog.category?.name || 'General'}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-500">Published</span>
                    <span className="font-medium text-slate-900">
                      {blog.createdDate ? new Date(blog.createdDate).toLocaleDateString() : 'Recently'}
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-500">Comments</span>
                    <span className="font-medium text-slate-900">{commentEntries.length}</span>
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* CTA */}
            <Card className="border-0 shadow-lg bg-gradient-to-br from-teal-600 to-teal-700 text-white">
              <CardContent className="p-6 text-center">
                <h3 className="font-semibold mb-2">Ready to Start Learning?</h3>
                <p className="text-sm text-teal-100 mb-4">
                  Join thousands of students preparing for competitive exams.
                </p>
                <Link href="/home">
                  <Button variant="secondary" className="w-full">
                    Start Practice Tests
                  </Button>
                </Link>
              </CardContent>
            </Card>
          </aside>
        </div>
      </div>
    </div>
  );
}

export default function BlogDetailPage() {
  return (
    <Suspense
      fallback={
        <div className="min-h-screen bg-gradient-to-b from-slate-50 to-white flex items-center justify-center">
          <div className="animate-pulse text-slate-500">Loading article...</div>
        </div>
      }
    >
      <BlogDetailContent />
    </Suspense>
  );
}
