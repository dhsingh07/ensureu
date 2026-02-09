// UI Store - migrated from Angular data.service.ts and loader.service.ts

import { create } from 'zustand';

export type AlertType = 'success' | 'error' | 'warning' | 'info';

export interface Alert {
  id: string;
  type: AlertType;
  message: string;
  title?: string;
}

interface UIState {
  // Loading state
  isLoading: boolean;
  loadingMessage: string;

  // Alert state
  alerts: Alert[];

  // Sidebar state
  isSidebarOpen: boolean;
  isMobileMenuOpen: boolean;

  // Modal state
  activeModal: string | null;
  modalData: unknown;

  // Actions
  setLoading: (loading: boolean, message?: string) => void;
  showAlert: (type: AlertType, message: string, title?: string) => void;
  dismissAlert: (id: string) => void;
  clearAlerts: () => void;
  toggleSidebar: () => void;
  setSidebarOpen: (open: boolean) => void;
  toggleMobileMenu: () => void;
  setMobileMenuOpen: (open: boolean) => void;
  openModal: (modalId: string, data?: unknown) => void;
  closeModal: () => void;
}

export const useUIStore = create<UIState>((set) => ({
  // Initial state
  isLoading: false,
  loadingMessage: '',
  alerts: [],
  isSidebarOpen: true,
  isMobileMenuOpen: false,
  activeModal: null,
  modalData: null,

  // Actions
  setLoading: (loading, message = '') =>
    set({
      isLoading: loading,
      loadingMessage: message,
    }),

  showAlert: (type, message, title) =>
    set((state) => ({
      alerts: [
        ...state.alerts,
        {
          id: crypto.randomUUID(),
          type,
          message,
          title,
        },
      ],
    })),

  dismissAlert: (id) =>
    set((state) => ({
      alerts: state.alerts.filter((alert) => alert.id !== id),
    })),

  clearAlerts: () => set({ alerts: [] }),

  toggleSidebar: () =>
    set((state) => ({ isSidebarOpen: !state.isSidebarOpen })),

  setSidebarOpen: (open) => set({ isSidebarOpen: open }),

  toggleMobileMenu: () =>
    set((state) => ({ isMobileMenuOpen: !state.isMobileMenuOpen })),

  setMobileMenuOpen: (open) => set({ isMobileMenuOpen: open }),

  openModal: (modalId, data) =>
    set({
      activeModal: modalId,
      modalData: data,
    }),

  closeModal: () =>
    set({
      activeModal: null,
      modalData: null,
    }),
}));

// Convenience hooks
export const useIsLoading = () => useUIStore((state) => state.isLoading);
export const useAlerts = () => useUIStore((state) => state.alerts);
export const useShowAlert = () => useUIStore((state) => state.showAlert);
