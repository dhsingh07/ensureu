'use client';

import { useEffect, type ReactNode } from 'react';
import { useAuthStore } from '@/stores/auth-store';
import { useUIStore } from '@/stores/ui-store';
import { initializeApiClient } from '@/lib/api/client';

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const logout = useAuthStore((state) => state.logout);
  const showAlert = useUIStore((state) => state.showAlert);

  useEffect(() => {
    // Initialize API client with store callbacks
    // Use getState() to always get the latest token value
    initializeApiClient({
      getToken: () => useAuthStore.getState().tokens?.token || null,
      onUnauthorized: () => useAuthStore.getState().logout(),
      showAlert: (type, message) => useUIStore.getState().showAlert(type, message),
    });
  }, []); // Run once on mount - callbacks read from store directly

  return <>{children}</>;
}
