import { useQuery } from '@tanstack/react-query';
import { get } from '@/lib/api/client';
import { API_URLS } from '@/lib/constants/api-urls';
import type { ApiResponse } from '@/types/api';
import type { PastPaperListItem } from '@/types/past-paper';

export function usePastPapers(
  paperType: string,
  paperSubCategory: string,
  enabled = true
) {
  return useQuery({
    queryKey: ['past-papers', paperType, paperSubCategory],
    queryFn: async () => {
      const response = await get<ApiResponse<PastPaperListItem[]>>(
        `${API_URLS.PAST_PAPER.LIST}/${paperType}?paperCategory=${paperSubCategory}`
      );
      if (Array.isArray(response)) {
        return response as PastPaperListItem[];
      }
      return response.body || [];
    },
    enabled: enabled && !!paperType && !!paperSubCategory,
    staleTime: 5 * 60 * 1000,
  });
}

