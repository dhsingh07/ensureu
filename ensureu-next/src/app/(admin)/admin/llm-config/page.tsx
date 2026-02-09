'use client';

import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { Slider } from '@/components/ui/slider';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  useLLMProviders,
  useLLMConfig,
  useUpdateLLMConfig,
  useTestLLMProvider,
} from '@/hooks/use-ai';
import {
  Sparkles,
  Bot,
  Server,
  CheckCircle2,
  XCircle,
  AlertCircle,
  Loader2,
  Save,
  TestTube,
  Settings2,
  Thermometer,
  Hash,
} from 'lucide-react';
import { cn } from '@/lib/utils';
import type { LLMProviderInfo, LLMConfigUpdate } from '@/types/ai';

// Provider icons
const providerIcons: Record<string, typeof Bot> = {
  claude: Sparkles,
  openai: Bot,
  ollama: Server,
};

// Provider colors
const providerColors: Record<string, string> = {
  claude: 'from-orange-500 to-amber-600',
  openai: 'from-emerald-500 to-green-600',
  ollama: 'from-blue-500 to-indigo-600',
};

export default function LLMConfigPage() {
  const { data: providers, isLoading: loadingProviders } = useLLMProviders();
  const { data: currentConfig, isLoading: loadingConfig } = useLLMConfig();
  const updateConfig = useUpdateLLMConfig();
  const testProvider = useTestLLMProvider();

  const [selectedProvider, setSelectedProvider] = useState<string>('');
  const [selectedModel, setSelectedModel] = useState<string>('');
  const [temperature, setTemperature] = useState<number>(0.7);
  const [maxTokens, setMaxTokens] = useState<number>(4096);

  // Initialize form when config loads
  if (currentConfig && !selectedProvider) {
    setSelectedProvider(currentConfig.provider);
    setSelectedModel(currentConfig.model);
    setTemperature(currentConfig.temperature || 0.7);
    setMaxTokens(currentConfig.maxTokens || 4096);
  }

  const selectedProviderInfo = providers?.find((p) => p.id === selectedProvider);

  const handleSave = () => {
    if (!selectedProvider || !selectedModel) return;

    const config: LLMConfigUpdate = {
      provider: selectedProvider,
      model: selectedModel,
      temperature,
      maxTokens,
    };

    updateConfig.mutate(config);
  };

  const handleTest = () => {
    if (!selectedProvider) return;
    testProvider.mutate({ provider: selectedProvider, model: selectedModel || undefined });
  };

  const isChanged =
    currentConfig &&
    (selectedProvider !== currentConfig.provider ||
      selectedModel !== currentConfig.model ||
      temperature !== (currentConfig.temperature || 0.7) ||
      maxTokens !== (currentConfig.maxTokens || 4096));

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
          <Sparkles className="h-6 w-6 text-teal-600" />
          LLM Configuration
        </h1>
        <p className="text-slate-600">
          Configure the AI language model provider and settings for the platform
        </p>
      </div>

      {/* Current Status */}
      <Card>
        <CardHeader className="pb-3">
          <CardTitle className="text-sm flex items-center gap-2">
            <Settings2 className="h-4 w-4" />
            Current Configuration
          </CardTitle>
        </CardHeader>
        <CardContent>
          {loadingConfig ? (
            <div className="flex items-center gap-4">
              <Skeleton className="h-12 w-12 rounded-lg" />
              <div className="space-y-2">
                <Skeleton className="h-4 w-32" />
                <Skeleton className="h-3 w-48" />
              </div>
            </div>
          ) : currentConfig ? (
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-4">
                <div
                  className={cn(
                    'w-12 h-12 rounded-lg flex items-center justify-center bg-gradient-to-br',
                    providerColors[currentConfig.provider] || 'from-gray-500 to-gray-600'
                  )}
                >
                  {(() => {
                    const Icon = providerIcons[currentConfig.provider] || Bot;
                    return <Icon className="h-6 w-6 text-white" />;
                  })()}
                </div>
                <div>
                  <p className="font-semibold text-slate-900">
                    {providers?.find((p) => p.id === currentConfig.provider)?.name ||
                      currentConfig.provider}
                  </p>
                  <p className="text-sm text-slate-500">
                    Model: <span className="font-mono">{currentConfig.model}</span>
                  </p>
                </div>
              </div>
              <div className="text-right text-sm text-slate-500">
                {currentConfig.updatedAt && (
                  <p>
                    Updated: {new Date(currentConfig.updatedAt).toLocaleDateString()}
                  </p>
                )}
                {currentConfig.updatedBy && <p>By: {currentConfig.updatedBy}</p>}
              </div>
            </div>
          ) : (
            <p className="text-slate-500">No configuration found. Using defaults.</p>
          )}
        </CardContent>
      </Card>

      <div className="space-y-6">
        {/* Provider Selection */}
        <div className="space-y-6">
          {/* Providers Grid */}
          <Card>
            <CardHeader>
              <CardTitle>Select Provider</CardTitle>
              <CardDescription>Choose the LLM provider for AI features</CardDescription>
            </CardHeader>
            <CardContent>
              {loadingProviders ? (
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                  {[1, 2, 3].map((i) => (
                    <Skeleton key={i} className="h-32" />
                  ))}
                </div>
              ) : (
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                  {providers?.map((provider) => (
                    <ProviderCard
                      key={provider.id}
                      provider={provider}
                      isSelected={selectedProvider === provider.id}
                      onSelect={() => {
                        setSelectedProvider(provider.id);
                        setSelectedModel(provider.models[0] || '');
                      }}
                    />
                  ))}
                </div>
              )}
            </CardContent>
          </Card>

          {/* Model & Settings */}
          {selectedProvider && selectedProviderInfo && (
            <Card>
              <CardHeader>
                <CardTitle>Model Settings</CardTitle>
                <CardDescription>
                  Configure model and generation parameters for {selectedProviderInfo.name}
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                {/* Provider Description */}
                {selectedProviderInfo.description && (
                  <div className="rounded-lg bg-gradient-to-r from-slate-50 to-slate-100 p-4 border border-slate-200">
                    <p className="text-sm text-slate-700">{selectedProviderInfo.description}</p>
                    {selectedProviderInfo.baseUrl && (
                      <p className="text-xs text-slate-500 mt-2">
                        API Endpoint: <code className="bg-slate-200 px-1 rounded">{selectedProviderInfo.baseUrl}</code>
                      </p>
                    )}
                  </div>
                )}
                {/* Available Models Overview */}
                {selectedProviderInfo.modelDetails && selectedProviderInfo.modelDetails.length > 0 && (
                  <div className="space-y-3">
                    <Label>Available Models</Label>
                    <div className="grid gap-2">
                      {selectedProviderInfo.modelDetails.map((model) => (
                        <div
                          key={model.id}
                          onClick={() => setSelectedModel(model.id)}
                          className={cn(
                            'p-3 rounded-lg border cursor-pointer transition-all',
                            selectedModel === model.id
                              ? 'border-teal-500 bg-teal-50 ring-1 ring-teal-500'
                              : 'border-slate-200 hover:border-slate-300 hover:bg-slate-50'
                          )}
                        >
                          <div className="flex items-center justify-between">
                            <div className="flex items-center gap-2">
                              <span className="font-medium text-slate-900">{model.name}</span>
                              <Badge variant="outline" className={cn(
                                'text-xs',
                                model.tier === 'fast' && 'bg-blue-50 text-blue-700 border-blue-200',
                                model.tier === 'balanced' && 'bg-green-50 text-green-700 border-green-200',
                                model.tier === 'powerful' && 'bg-purple-50 text-purple-700 border-purple-200'
                              )}>
                                {model.tier === 'fast' && '⚡ Fast'}
                                {model.tier === 'balanced' && '⚖️ Balanced'}
                                {model.tier === 'powerful' && '🚀 Powerful'}
                              </Badge>
                            </div>
                            <span className="text-xs text-slate-500">
                              {(model.contextWindow / 1000).toFixed(0)}K ctx
                            </span>
                          </div>
                          <p className="text-xs text-slate-500 mt-1">{model.description}</p>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* Model Selection Dropdown (fallback if no modelDetails) */}
                {(!selectedProviderInfo.modelDetails || selectedProviderInfo.modelDetails.length === 0) && (
                  <div className="space-y-2">
                    <Label htmlFor="model">Model</Label>
                    <Select value={selectedModel} onValueChange={setSelectedModel}>
                      <SelectTrigger>
                        <SelectValue placeholder="Select a model" />
                      </SelectTrigger>
                      <SelectContent>
                        {selectedProviderInfo.models.map((model) => (
                          <SelectItem key={model} value={model}>
                            {model}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                )}

                {/* Temperature */}
                <div className="space-y-3">
                  <div className="flex items-center justify-between">
                    <Label className="flex items-center gap-2">
                      <Thermometer className="h-4 w-4" />
                      Temperature
                    </Label>
                    <span className="text-sm font-mono text-slate-600">{temperature}</span>
                  </div>
                  <Slider
                    value={[temperature]}
                    onValueChange={(v) => setTemperature(v[0])}
                    min={0}
                    max={2}
                    step={0.1}
                    className="w-full"
                  />
                  <p className="text-xs text-slate-500">
                    Lower values produce more focused output, higher values more creative
                  </p>
                </div>

                {/* Max Tokens */}
                <div className="space-y-2">
                  <Label htmlFor="maxTokens" className="flex items-center gap-2">
                    <Hash className="h-4 w-4" />
                    Max Tokens
                  </Label>
                  <Input
                    id="maxTokens"
                    type="number"
                    value={maxTokens}
                    onChange={(e) => setMaxTokens(parseInt(e.target.value) || 4096)}
                    min={100}
                    max={128000}
                  />
                  <p className="text-xs text-slate-500">
                    Maximum number of tokens in the response (100 - 128,000)
                  </p>
                </div>

                {/* Capabilities */}
                <div className="space-y-2">
                  <Label>Capabilities</Label>
                  <div className="flex flex-wrap gap-2">
                    <Badge
                      variant={selectedProviderInfo.supportsEmbeddings ? 'default' : 'secondary'}
                      className={cn(
                        selectedProviderInfo.supportsEmbeddings
                          ? 'bg-green-100 text-green-700'
                          : 'bg-gray-100 text-gray-500'
                      )}
                    >
                      {selectedProviderInfo.supportsEmbeddings ? (
                        <CheckCircle2 className="h-3 w-3 mr-1" />
                      ) : (
                        <XCircle className="h-3 w-3 mr-1" />
                      )}
                      Embeddings
                    </Badge>
                    <Badge
                      variant={selectedProviderInfo.supportsJsonMode ? 'default' : 'secondary'}
                      className={cn(
                        selectedProviderInfo.supportsJsonMode
                          ? 'bg-green-100 text-green-700'
                          : 'bg-gray-100 text-gray-500'
                      )}
                    >
                      {selectedProviderInfo.supportsJsonMode ? (
                        <CheckCircle2 className="h-3 w-3 mr-1" />
                      ) : (
                        <XCircle className="h-3 w-3 mr-1" />
                      )}
                      JSON Mode
                    </Badge>
                  </div>
                </div>

                {/* Actions */}
                <div className="flex gap-3 pt-4 border-t">
                  <Button
                    variant="outline"
                    onClick={handleTest}
                    disabled={testProvider.isPending}
                  >
                    {testProvider.isPending ? (
                      <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                    ) : (
                      <TestTube className="h-4 w-4 mr-2" />
                    )}
                    Test Connection
                  </Button>
                  <Button
                    onClick={handleSave}
                    disabled={updateConfig.isPending || !isChanged}
                    className="bg-gradient-to-r from-teal-500 to-cyan-600 hover:from-teal-600 hover:to-cyan-700"
                  >
                    {updateConfig.isPending ? (
                      <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                    ) : (
                      <Save className="h-4 w-4 mr-2" />
                    )}
                    Save Configuration
                  </Button>
                </div>
              </CardContent>
            </Card>
          )}
        </div>

      </div>
    </div>
  );
}

// Provider Card Component
function ProviderCard({
  provider,
  isSelected,
  onSelect,
}: {
  provider: LLMProviderInfo;
  isSelected: boolean;
  onSelect: () => void;
}) {
  const Icon = providerIcons[provider.id] || Bot;
  const gradient = providerColors[provider.id] || 'from-gray-500 to-gray-600';

  return (
    <button
      onClick={onSelect}
      className={cn(
        'p-4 rounded-xl border-2 text-left transition-all hover:shadow-md',
        isSelected
          ? 'border-teal-500 bg-teal-50 shadow-md'
          : 'border-slate-200 hover:border-slate-300'
      )}
    >
      <div className="flex items-start justify-between mb-3">
        <div
          className={cn(
            'w-10 h-10 rounded-lg flex items-center justify-center bg-gradient-to-br',
            gradient
          )}
        >
          <Icon className="h-5 w-5 text-white" />
        </div>
        {provider.isLocal && (
          <Badge variant="outline" className="text-xs bg-blue-50 text-blue-700 border-blue-200">
            Local
          </Badge>
        )}
      </div>
      <h3 className="font-semibold text-slate-900">{provider.name}</h3>
      {provider.description && (
        <p className="text-xs text-slate-500 mt-1 line-clamp-2">
          {provider.description}
        </p>
      )}
      <p className="text-xs text-slate-400 mt-2">
        {provider.models.length} model{provider.models.length !== 1 ? 's' : ''} available
      </p>
      <div className="flex flex-wrap items-center gap-1 mt-2">
        {provider.configured ? (
          <Badge variant="outline" className="text-xs bg-green-50 text-green-700 border-green-200">
            <CheckCircle2 className="h-3 w-3 mr-1" />
            Ready
          </Badge>
        ) : (
          <Badge variant="outline" className="text-xs bg-amber-50 text-amber-700 border-amber-200">
            <AlertCircle className="h-3 w-3 mr-1" />
            {provider.apiKeyEnvVar ? `Set ${provider.apiKeyEnvVar}` : 'Not configured'}
          </Badge>
        )}
        {provider.supportsJsonMode && (
          <Badge variant="outline" className="text-xs bg-slate-50 text-slate-600 border-slate-200">
            JSON
          </Badge>
        )}
        {provider.supportsEmbeddings && (
          <Badge variant="outline" className="text-xs bg-slate-50 text-slate-600 border-slate-200">
            Embed
          </Badge>
        )}
      </div>
    </button>
  );
}
