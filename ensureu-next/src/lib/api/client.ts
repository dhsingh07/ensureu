// API Client with JWT interceptor - migrated from Angular http.interceptor.ts

import axios, { AxiosError, AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios';
import { API_BASE_URL, AI_SERVICE_URL, MESSAGES } from '@/lib/constants/api-urls';

// Create axios instance for main backend
export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 120000, // 120 seconds for AI calls that go through Java backend
  headers: {
    'Content-Type': 'application/json',
  },
});

// Create axios instance for AI service (direct connection)
export const aiServiceClient = axios.create({
  baseURL: AI_SERVICE_URL,
  timeout: 60000, // AI requests may take longer
  headers: {
    'Content-Type': 'application/json',
  },
});

// Token management (will be connected to Zustand store)
let getToken: (() => string | null) | null = null;
let onUnauthorized: (() => void) | null = null;
let showAlert: ((type: 'error' | 'success' | 'warning', message: string) => void) | null = null;

// Initialize interceptors with store callbacks
export function initializeApiClient(config: {
  getToken: () => string | null;
  onUnauthorized: () => void;
  showAlert: (type: 'error' | 'success' | 'warning', message: string) => void;
}) {
  getToken = config.getToken;
  onUnauthorized = config.onUnauthorized;
  showAlert = config.showAlert;
}

// Request interceptor - add JWT token
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken?.();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - handle errors
apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      showAlert?.('error', MESSAGES.UNAUTHORIZED);
      onUnauthorized?.();
    } else if (!error.response) {
      showAlert?.('error', MESSAGES.CONNECTION_ERROR);
    } else {
      const message = (error.response?.data as { message?: string })?.message || error.message;
      showAlert?.('error', message);
    }
    return Promise.reject(error);
  }
);

// AI Service interceptors (same pattern)
aiServiceClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken?.();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

aiServiceClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      showAlert?.('error', MESSAGES.UNAUTHORIZED);
      onUnauthorized?.();
    } else if (error.response?.status === 403) {
      showAlert?.('error', 'Access denied. You need SUPERADMIN privileges.');
    } else if (!error.response) {
      showAlert?.('error', 'AI Service unavailable. Please try again later.');
    } else {
      const message = (error.response?.data as { detail?: string })?.detail ||
                      (error.response?.data as { message?: string })?.message ||
                      error.message;
      showAlert?.('error', message);
    }
    return Promise.reject(error);
  }
);

// Generic fetch function with type safety
export async function fetchApi<T>(
  endpoint: string,
  options?: AxiosRequestConfig
): Promise<T> {
  const response = await apiClient.request<T>({
    url: endpoint,
    ...options,
  });
  return response.data;
}

// GET request helper
export async function get<T>(endpoint: string, params?: Record<string, unknown>): Promise<T> {
  return fetchApi<T>(endpoint, { method: 'GET', params });
}

// POST request helper
export async function post<T>(endpoint: string, data?: unknown): Promise<T> {
  return fetchApi<T>(endpoint, { method: 'POST', data });
}

// PUT request helper
export async function put<T>(endpoint: string, data?: unknown): Promise<T> {
  return fetchApi<T>(endpoint, { method: 'PUT', data });
}

// DELETE request helper
export async function del<T>(endpoint: string): Promise<T> {
  return fetchApi<T>(endpoint, { method: 'DELETE' });
}

// PATCH request helper
export async function patch<T>(endpoint: string, data?: unknown): Promise<T> {
  return fetchApi<T>(endpoint, { method: 'PATCH', data });
}

// Upload file helper
export async function uploadFile<T>(
  endpoint: string,
  file: File,
  fieldName = 'file'
): Promise<T> {
  const formData = new FormData();
  formData.append(fieldName, file);

  return fetchApi<T>(endpoint, {
    method: 'POST',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

// =============================================================================
// AI Service API Helpers (direct to Python service)
// =============================================================================

// Generic fetch function for AI service
export async function fetchAiApi<T>(
  endpoint: string,
  options?: AxiosRequestConfig
): Promise<T> {
  const response = await aiServiceClient.request<T>({
    url: endpoint,
    ...options,
  });
  return response.data;
}

// AI Service GET request helper
export async function aiGet<T>(endpoint: string, params?: Record<string, unknown>): Promise<T> {
  return fetchAiApi<T>(endpoint, { method: 'GET', params });
}

// AI Service POST request helper
export async function aiPost<T>(endpoint: string, data?: unknown): Promise<T> {
  return fetchAiApi<T>(endpoint, { method: 'POST', data });
}

// AI Service PUT request helper
export async function aiPut<T>(endpoint: string, data?: unknown): Promise<T> {
  return fetchAiApi<T>(endpoint, { method: 'PUT', data });
}
