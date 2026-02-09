'use client';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Switch } from '@/components/ui/switch';
import { Skeleton } from '@/components/ui/skeleton';
import { useFeatureConfig, useUpdateFeatureConfig } from '@/hooks/use-admin';
import { Settings, Save, Loader2, AlertCircle } from 'lucide-react';
import type { FeatureConfig } from '@/types/admin';

const featureDescriptions: Record<string, string> = {
  practiceMode: 'Enable practice mode for users',
  quizMode: 'Enable quiz mode for quick tests',
  mockTests: 'Enable full-length mock tests',
  previousPapers: 'Show previous year papers',
  notifications: 'Enable push/email notifications',
  subscriptions: 'Enable subscription/payment features',
  analytics: 'Show user analytics and progress',
  blogSection: 'Enable blog section',
  googleLogin: 'Allow users to sign in with Google',
  facebookLogin: 'Allow users to sign in with Facebook',
  otpLogin: 'Enable OTP-based login',
};

// Convert camelCase to Title Case for display
function formatFeatureName(name: string): string {
  return name
    .replace(/([A-Z])/g, ' $1')
    .replace(/^./, (str) => str.toUpperCase())
    .trim();
}

export default function FeatureConfigPage() {
  const { data: configs, isLoading, error } = useFeatureConfig();
  const updateConfigMutation = useUpdateFeatureConfig();

  const handleToggle = (config: FeatureConfig) => {
    updateConfigMutation.mutate({
      ...config,
      enabled: !config.enabled,
    });
  };

  if (error) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <Card className="max-w-md">
          <CardContent className="pt-6 text-center">
            <AlertCircle className="h-12 w-12 text-red-500 mx-auto mb-4" />
            <h2 className="text-xl font-semibold mb-2">Failed to Load Config</h2>
            <p className="text-slate-600">
              We couldn&apos;t load the feature configuration. Please try again.
            </p>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
          <Settings className="h-6 w-6" />
          Feature Configuration
        </h1>
        <p className="text-slate-600">
          Enable or disable features across the platform
        </p>
      </div>

      {/* Feature Toggles */}
      <Card>
        <CardHeader>
          <CardTitle>Feature Toggles</CardTitle>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="space-y-4">
              {[1, 2, 3, 4, 5].map((i) => (
                <div key={i} className="flex items-center justify-between p-4 border rounded-lg">
                  <div className="space-y-2">
                    <Skeleton className="h-4 w-32" />
                    <Skeleton className="h-3 w-64" />
                  </div>
                  <Skeleton className="h-6 w-10" />
                </div>
              ))}
            </div>
          ) : configs && configs.length > 0 ? (
            <div className="space-y-4">
              {configs.map((config) => (
                <div
                  key={config.id}
                  className="flex items-center justify-between p-4 border rounded-lg hover:bg-slate-50 transition-colors"
                >
                  <div className="space-y-1">
                    <h4 className="font-medium text-slate-900">
                      {formatFeatureName(config.featureName)}
                    </h4>
                    <p className="text-sm text-slate-500">
                      {featureDescriptions[config.featureName] ||
                        `Control the ${formatFeatureName(config.featureName).toLowerCase()} feature`}
                    </p>
                  </div>
                  <Switch
                    checked={config.enabled}
                    onCheckedChange={() => handleToggle(config)}
                    disabled={updateConfigMutation.isPending}
                  />
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12 text-slate-500">
              <Settings className="h-12 w-12 mx-auto mb-4 text-slate-300" />
              <p>No feature configurations found</p>
              <p className="text-sm">Contact support to add feature toggles</p>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Quick Actions */}
      <Card>
        <CardHeader>
          <CardTitle>Quick Actions</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <Button
              variant="outline"
              className="h-auto py-4 flex flex-col items-center gap-2"
              onClick={() => {
                configs?.forEach((config) => {
                  if (!config.enabled) {
                    updateConfigMutation.mutate({ ...config, enabled: true });
                  }
                });
              }}
              disabled={updateConfigMutation.isPending}
            >
              {updateConfigMutation.isPending ? (
                <Loader2 className="h-5 w-5 animate-spin" />
              ) : (
                <Save className="h-5 w-5" />
              )}
              <span>Enable All</span>
            </Button>
            <Button
              variant="outline"
              className="h-auto py-4 flex flex-col items-center gap-2"
              onClick={() => {
                configs?.forEach((config) => {
                  if (config.enabled) {
                    updateConfigMutation.mutate({ ...config, enabled: false });
                  }
                });
              }}
              disabled={updateConfigMutation.isPending}
            >
              <Settings className="h-5 w-5" />
              <span>Disable All</span>
            </Button>
            <Button
              variant="outline"
              className="h-auto py-4 flex flex-col items-center gap-2"
            >
              <Settings className="h-5 w-5" />
              <span>Export Config</span>
            </Button>
            <Button
              variant="outline"
              className="h-auto py-4 flex flex-col items-center gap-2"
            >
              <Settings className="h-5 w-5" />
              <span>Import Config</span>
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
