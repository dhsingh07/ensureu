// Category Store - for managing selected exam category

import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import type { PaperCategory, PaperSubCategory } from '@/types/paper';

interface CategoryState {
  // Selected category
  rootCategory: PaperCategory;
  childCategory: PaperSubCategory | '';
  hasChildSelector: boolean;

  // Actions
  setRootCategory: (category: PaperCategory) => void;
  setChildCategory: (category: PaperSubCategory | '') => void;
  setCategories: (
    root: PaperCategory,
    child: PaperSubCategory | '',
    hasChild: boolean
  ) => void;
  reset: () => void;
}

const initialState = {
  rootCategory: 'SSC_CGL' as PaperCategory,
  childCategory: 'SSC_CGL_TIER1' as PaperSubCategory | '',
  hasChildSelector: true,
};

export const useCategoryStore = create<CategoryState>()(
  persist(
    (set) => ({
      ...initialState,

      setRootCategory: (category) =>
        set({
          rootCategory: category,
          childCategory: '',
          hasChildSelector: false,
        }),

      setChildCategory: (category) =>
        set({
          childCategory: category,
          hasChildSelector: !!category,
        }),

      setCategories: (root, child, hasChild) =>
        set({
          rootCategory: root,
          childCategory: child,
          hasChildSelector: hasChild,
        }),

      reset: () => set(initialState),
    }),
    {
      name: 'category-storage',
      storage: createJSONStorage(() => localStorage),
    }
  )
);

// Convenience hooks
export const useRootCategory = () => useCategoryStore((state) => state.rootCategory);
export const useChildCategory = () => useCategoryStore((state) => state.childCategory);
