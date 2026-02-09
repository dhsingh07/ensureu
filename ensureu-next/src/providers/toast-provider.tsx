'use client';

import { useEffect } from 'react';
import { Toaster, toast } from 'sonner';
import { useUIStore } from '@/stores/ui-store';

export function ToastProvider() {
  const alerts = useUIStore((state) => state.alerts);
  const dismissAlert = useUIStore((state) => state.dismissAlert);

  useEffect(() => {
    // Show alerts as toasts
    alerts.forEach((alert) => {
      const toastFn = {
        success: toast.success,
        error: toast.error,
        warning: toast.warning,
        info: toast.info,
      }[alert.type];

      toastFn(alert.message, {
        id: alert.id,
        description: alert.title,
        onDismiss: () => dismissAlert(alert.id),
        onAutoClose: () => dismissAlert(alert.id),
      });
    });
  }, [alerts, dismissAlert]);

  return (
    <Toaster
      position="top-right"
      expand={false}
      richColors
      closeButton
      duration={5000}
    />
  );
}
