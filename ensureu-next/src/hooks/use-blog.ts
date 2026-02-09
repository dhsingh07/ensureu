import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { get, post, patch, del } from '@/lib/api/client';
import { API_URLS, APP_CONFIG } from '@/lib/constants/api-urls';
import type { ApiResponse } from '@/types/api';
import type { BlogCategory, BlogComment, BlogItem } from '@/types/blog';
import { useUIStore } from '@/stores/ui-store';

export function useBlogCategories() {
  return useQuery({
    queryKey: ['blog', 'categories'],
    queryFn: async () => {
      const response = await get<ApiResponse<BlogCategory[]>>(
        `${API_URLS.BLOG.CATEGORY_FETCH_ALL}?page=0&size=0`
      );
      if (Array.isArray(response)) {
        return response as BlogCategory[];
      }
      return response.body || [];
    },
    staleTime: 10 * 60 * 1000,
  });
}

export function useBlogList(page: number, enabled = true) {
  return useQuery({
    queryKey: ['blog', 'list', page],
    queryFn: async () => {
      const response = await get<ApiResponse<BlogItem[]>>(
        `${API_URLS.BLOG.FETCH_ALL}?page=${page}&size=${APP_CONFIG.BLOG_PAGE_LIMIT}`
      );
      if (Array.isArray(response)) {
        return response as BlogItem[];
      }
      return response.body || [];
    },
    enabled,
    staleTime: 2 * 60 * 1000,
  });
}

export function useTrendingBlogs() {
  return useQuery({
    queryKey: ['blog', 'trending'],
    queryFn: async () => {
      const response = await get<ApiResponse<BlogItem[]>>(
        `${API_URLS.BLOG.FETCH_ALL}?page=0&size=${APP_CONFIG.BLOG_PAGE_LIMIT}&sortBy=priority`
      );
      if (Array.isArray(response)) {
        return response as BlogItem[];
      }
      return response.body || [];
    },
    staleTime: 5 * 60 * 1000,
  });
}

export function useBlogByTitle(phrase: string, enabled = true) {
  return useQuery({
    queryKey: ['blog', 'search', phrase],
    queryFn: async () => {
      const response = await get<ApiResponse<BlogItem[]>>(
        `${API_URLS.BLOG.SEARCH}?phrase=${encodeURIComponent(phrase)}`
      );
      if (Array.isArray(response)) {
        return response as BlogItem[];
      }
      return response.body || [];
    },
    enabled: enabled && !!phrase,
    staleTime: 2 * 60 * 1000,
  });
}

export function useBlogByCategory(categoryId: string, page: number, enabled = true) {
  return useQuery({
    queryKey: ['blog', 'category', categoryId, page],
    queryFn: async () => {
      const response = await get<ApiResponse<BlogItem[]>>(
        `${API_URLS.BLOG.FETCH_BY_CATEGORY}/${categoryId}?page=${page}&size=${APP_CONFIG.BLOG_PAGE_LIMIT}`
      );
      if (Array.isArray(response)) {
        return response as BlogItem[];
      }
      return response.body || [];
    },
    enabled: enabled && !!categoryId,
    staleTime: 2 * 60 * 1000,
  });
}

export function useBlogByUser(userId: string, page: number, enabled = true) {
  return useQuery({
    queryKey: ['blog', 'user', userId, page],
    queryFn: async () => {
      const response = await get<ApiResponse<BlogItem[]>>(
        `${API_URLS.BLOG.FETCH_BY_USER}?page=${page}&size=${APP_CONFIG.BLOG_PAGE_LIMIT}&userId=${encodeURIComponent(
          userId
        )}`
      );
      if (Array.isArray(response)) {
        return response as BlogItem[];
      }
      return response.body || [];
    },
    enabled: enabled && !!userId,
    staleTime: 2 * 60 * 1000,
  });
}

export function useBlogDetail(blogId?: string) {
  return useQuery({
    queryKey: ['blog', 'detail', blogId],
    queryFn: async () => {
      if (!blogId) return null;
      const response = await get<ApiResponse<BlogItem>>(
        `${API_URLS.BLOG.FETCH_BY_ID}/${blogId}`
      );
      if (response && typeof response === 'object' && 'body' in response) {
        return response.body || null;
      }
      // API might return BlogItem directly without wrapper
      if (response && typeof response === 'object' && 'id' in response) {
        return response as unknown as BlogItem;
      }
      return null;
    },
    enabled: !!blogId,
  });
}

export function useBlogComments(blogId?: string) {
  return useQuery({
    queryKey: ['blog', 'comments', blogId],
    queryFn: async () => {
      if (!blogId) return null;
      const response = await get<ApiResponse<BlogComment> | BlogComment>(
        `${API_URLS.BLOG.FETCH_COMMENTS}/${blogId}`
      );
      // Backend returns a single BlogComment object (not an array)
      if (response && typeof response === 'object') {
        if ('body' in response && response.body) {
          return response.body as BlogComment;
        }
        if ('blogId' in response) {
          return response as BlogComment;
        }
      }
      return null;
    },
    enabled: !!blogId,
  });
}

export function useAddBlogComment() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: (payload: BlogComment) =>
      post<ApiResponse>(API_URLS.BLOG.ADD_COMMENT, payload),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['blog', 'comments', variables.blogId] });
      showAlert('success', 'Comment submitted');
    },
  });
}

export function useToggleBlogLike() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: ({ blogId, isDislike, userId }: { blogId: string; isDislike: boolean; userId: string }) =>
      patch<ApiResponse>(
        `${API_URLS.BLOG.PARTIAL_UPDATE}?isDislike=${isDislike ? 'true' : 'false'}`,
        { id: blogId, likes: [userId] }
      ),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['blog', 'detail', variables.blogId] });
      queryClient.invalidateQueries({ queryKey: ['blog', 'list'] });
      showAlert('success', variables.isDislike ? 'Like removed' : 'Article liked!');
    },
  });
}

export function useDeleteBlog() {
  const queryClient = useQueryClient();
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: (blogId: string) =>
      del<ApiResponse>(`${API_URLS.BLOG.DELETE}/${blogId}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['blog', 'list'] });
      showAlert('success', 'Blog deleted');
    },
  });
}

