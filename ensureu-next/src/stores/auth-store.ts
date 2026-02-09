// Auth Store - migrated from Angular role.helper.ts and login.service.ts

import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import type { User, AuthTokens, RoleType } from '@/types/auth';

// Helper to set auth cookie for middleware
function setAuthCookie(user: User | null, isAuthenticated: boolean) {
  if (typeof document === 'undefined') return;

  if (isAuthenticated && user) {
    const cookieData = {
      state: {
        isAuthenticated: true,
        user: {
          roles: user.roles,
        },
      },
    };
    // Set cookie with 1 day expiry
    document.cookie = `auth-storage=${encodeURIComponent(JSON.stringify(cookieData))}; path=/; max-age=86400; SameSite=Lax`;
  } else {
    // Clear cookie
    document.cookie = 'auth-storage=; path=/; max-age=0';
  }
}

interface AuthState {
  user: User | null;
  tokens: AuthTokens | null;
  isAuthenticated: boolean;
  isLoading: boolean;

  // Actions
  setUser: (user: User | null) => void;
  setTokens: (tokens: AuthTokens | null) => void;
  login: (user: User, tokens: AuthTokens) => void;
  logout: () => void;
  updateToken: (token: string) => void;
  setLoading: (loading: boolean) => void;

  // Role helpers - migrated from RoleHelper
  getUserRoles: () => RoleType[];
  hasRole: (role: RoleType) => boolean;
  hasAnyRole: (roles: RoleType[]) => boolean;
  isSuperAdmin: () => boolean;
  isAdmin: () => boolean;
  isTeacher: () => boolean;
  hasAdminPanelAccess: () => boolean;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      tokens: null,
      isAuthenticated: false,
      isLoading: true,

      setUser: (user) =>
        set({
          user,
          isAuthenticated: !!user,
        }),

      setTokens: (tokens) => set({ tokens }),

      login: (user, tokens) => {
        setAuthCookie(user, true);
        set({
          user,
          tokens,
          isAuthenticated: true,
          isLoading: false,
        });
      },

      logout: () => {
        setAuthCookie(null, false);
        set({
          user: null,
          tokens: null,
          isAuthenticated: false,
        });
      },

      updateToken: (token) =>
        set((state) => ({
          tokens: state.tokens
            ? { ...state.tokens, token }
            : null,
        })),

      setLoading: (loading) => set({ isLoading: loading }),

      // Role helpers
      getUserRoles: () => {
        const { user } = get();
        if (!user?.roles) return [];
        return user.roles.map((r) => r.roleType);
      },

      hasRole: (role) => {
        const { user } = get();
        if (!user?.roles) return false;
        return user.roles.some((r) => r.roleType === role);
      },

      hasAnyRole: (roles) => {
        const { user } = get();
        if (!user?.roles) return false;
        return user.roles.some((r) => roles.includes(r.roleType));
      },

      isSuperAdmin: () => get().hasRole('SUPERADMIN'),

      isAdmin: () => get().hasRole('ADMIN'),

      isTeacher: () => get().hasRole('TEACHER'),

      hasAdminPanelAccess: () =>
        get().hasAnyRole(['SUPERADMIN', 'ADMIN', 'TEACHER']),
    }),
    {
      name: 'auth-storage',
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        user: state.user,
        tokens: state.tokens,
        isAuthenticated: state.isAuthenticated,
      }),
      onRehydrateStorage: () => (state) => {
        if (state) {
          state.setLoading(false);
          // Sync cookie on rehydration
          if (state.isAuthenticated && state.user) {
            setAuthCookie(state.user, true);
          }
        }
      },
    }
  )
);

// Selector hooks for convenience
export const useUser = () => useAuthStore((state) => state.user);
export const useIsAuthenticated = () => useAuthStore((state) => state.isAuthenticated);
export const useIsSuperAdmin = () => useAuthStore((state) => state.isSuperAdmin());
export const useIsAdmin = () => useAuthStore((state) => state.isAdmin());
export const useHasAdminAccess = () => useAuthStore((state) => state.hasAdminPanelAccess());
