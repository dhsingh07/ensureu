'use client';

import { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { apiClient } from '@/lib/api/client';

interface UploadResponse {
  url: string;
  filename: string;
  contentType?: string;
  size?: string;
  storageType?: string;
}

interface ApiResponse<T> {
  status: number;
  body: T;
  message: string;
}

export type UploadFolder = 'questions' | 'papers' | 'blogs' | 'general';

/**
 * Hook for uploading files
 */
export function useFileUpload() {
  const [uploadProgress, setUploadProgress] = useState<number>(0);

  const uploadMutation = useMutation({
    mutationFn: async ({
      file,
      folder = 'general',
    }: {
      file: File;
      folder?: UploadFolder;
    }) => {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('folder', folder);

      const response = await apiClient.post<ApiResponse<UploadResponse>>(
        '/files/upload',
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
          onUploadProgress: (progressEvent) => {
            if (progressEvent.total) {
              const progress = Math.round(
                (progressEvent.loaded * 100) / progressEvent.total
              );
              setUploadProgress(progress);
            }
          },
        }
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message || 'Upload failed');
      }

      return response.data.body;
    },
    onSettled: () => {
      setUploadProgress(0);
    },
  });

  return {
    upload: uploadMutation.mutateAsync,
    isUploading: uploadMutation.isPending,
    uploadProgress,
    error: uploadMutation.error,
  };
}

/**
 * Hook for uploading images specifically
 */
export function useImageUpload() {
  const [uploadProgress, setUploadProgress] = useState<number>(0);

  const uploadMutation = useMutation({
    mutationFn: async ({
      file,
      folder = 'questions',
    }: {
      file: File;
      folder?: UploadFolder;
    }) => {
      // Validate image type
      const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'image/svg+xml'];
      if (!allowedTypes.includes(file.type)) {
        throw new Error('Invalid image type. Allowed: JPEG, PNG, GIF, WebP, SVG');
      }

      // Validate file size (10MB max)
      const maxSize = 10 * 1024 * 1024;
      if (file.size > maxSize) {
        throw new Error('Image size exceeds 10MB limit');
      }

      const formData = new FormData();
      formData.append('file', file);
      formData.append('folder', folder);

      const response = await apiClient.post<ApiResponse<UploadResponse>>(
        '/files/upload/image',
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
          onUploadProgress: (progressEvent) => {
            if (progressEvent.total) {
              const progress = Math.round(
                (progressEvent.loaded * 100) / progressEvent.total
              );
              setUploadProgress(progress);
            }
          },
        }
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message || 'Image upload failed');
      }

      return response.data.body;
    },
    onSettled: () => {
      setUploadProgress(0);
    },
  });

  return {
    uploadImage: uploadMutation.mutateAsync,
    isUploading: uploadMutation.isPending,
    uploadProgress,
    error: uploadMutation.error,
  };
}

/**
 * Hook for deleting files
 */
export function useFileDelete() {
  return useMutation({
    mutationFn: async (fileUrl: string) => {
      const response = await apiClient.delete<ApiResponse<boolean>>(
        `/files/delete?url=${encodeURIComponent(fileUrl)}`
      );

      if (response.data.status !== 200) {
        throw new Error(response.data.message || 'Delete failed');
      }

      return response.data.body;
    },
  });
}

/**
 * Get storage info
 */
export async function getStorageInfo(): Promise<{ storageType: string; maxFileSize: string }> {
  const response = await apiClient.get<ApiResponse<{ storageType: string; maxFileSize: string }>>(
    '/files/info'
  );
  return response.data.body;
}
