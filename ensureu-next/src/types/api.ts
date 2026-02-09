// API Response types for EnsureU

export interface ApiResponse<T = unknown> {
  status: number;
  message: string;
  body?: T;
}

export interface EncryptedResponse {
  body: string;
}

export interface ErrorResponse {
  status: number;
  message: string;
  error?: string;
}
