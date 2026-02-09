// Auth hooks - migrated from Angular login.service.ts

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';
import { post, get } from '@/lib/api/client';
import { API_URLS, APP_CONFIG } from '@/lib/constants/api-urls';
import { useAuthStore } from '@/stores/auth-store';
import { useUIStore } from '@/stores/ui-store';
import type {
  User,
  LoginCredentials,
  LoginResponse,
  RegisterData,
  OTPRequest,
  OTPVerifyRequest,
  ProviderTokenRequest,
} from '@/types/auth';
import type { ApiResponse } from '@/types/api';

// Demo mode check - set to true to bypass API calls for testing
const DEMO_MODE = process.env.NEXT_PUBLIC_DEMO_MODE === 'true';

// Helper to normalize role type (strip ROLE_ prefix if present)
function normalizeRoleType(roleType: string): string {
  return roleType.replace(/^ROLE_/, '');
}

// Login mutation
export function useLogin() {
  const login = useAuthStore((state) => state.login);
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (credentials: LoginCredentials) => {
      // Demo mode - simulate successful login
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 500)); // Simulate network delay
        // Use the username as the name for demo
        const displayName = credentials.username.includes('@')
          ? credentials.username.split('@')[0]
          : credentials.username;
        return {
          token: 'demo-token-' + Date.now(),
          name: displayName,
          verification: true,
          roles: [{ id: 'ROLE_USER', roleType: 'USER' }],
        } as LoginResponse;
      }
      return post<LoginResponse>(API_URLS.AUTH.TOKEN, credentials);
    },
    onSuccess: (response, variables) => {
      if (response.verification) {
        // Parse name for display
        const nameParts = (response.name || variables.username).split(' ');
        const firstName = nameParts[0] || variables.username;
        const lastName = nameParts.slice(1).join(' ') || '';

        const user: User = {
          id: 'user-' + Date.now(),
          userName: variables.username,
          firstName,
          lastName,
          emailId: variables.username.includes('@') ? variables.username : '',
          mobileNumber: variables.username.includes('@') ? '' : variables.username,
          mobileNumberVerified: true,
          roles: (response.roles && response.roles.length > 0)
            ? response.roles.map((r) => ({
                id: r.id || '',
                roleType: normalizeRoleType(r.roleType) as User['roles'][0]['roleType'],
              }))
            : [{ id: '', roleType: 'USER' as const }],
          loginType: 'SIGNUP',
        };

        login(user, {
          token: response.token,
          expiresAt: Date.now() + 60 * 60 * 1000, // 1 hour
        });

        showAlert('success', 'Login successful!');

        // Use window.location for reliable redirect
        window.location.href = '/home';
      } else {
        showAlert('warning', 'Please verify your mobile number with OTP');
      }
    },
    onError: (error: any) => {
      const message = error?.response?.data?.message || error?.message || 'Login failed. Please check your credentials.';
      showAlert('error', message);
    },
  });
}

// Register mutation
export function useRegister() {
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: (data: RegisterData) =>
      post<ApiResponse<User>>(API_URLS.USER.CREATE, data),
    onSuccess: () => {
      showAlert('success', 'Registration successful! Please verify your OTP.');
    },
  });
}

// OAuth provider token mutation
export function useProviderLogin() {
  const login = useAuthStore((state) => state.login);
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: async (data: ProviderTokenRequest) => {
      // Demo mode - simulate successful login
      if (DEMO_MODE) {
        await new Promise((resolve) => setTimeout(resolve, 500));
        return {
          token: 'demo-token-' + Date.now(),
          name: data.name,
          verification: true,
          roles: [{ id: 'ROLE_USER', roleType: 'USER' }],
        } as LoginResponse;
      }
      // Transform to backend expected format
      const backendPayload = {
        token: data.idToken || data.accessToken, // Google uses idToken (credential)
        username: data.email,
        name: data.name, // User's display name from provider
        loginType: data.providerId, // GOOGLE or FACEBOOK
      };
      return post<LoginResponse>(API_URLS.AUTH.PROVIDER_TOKEN, backendPayload);
    },
    onSuccess: (response, variables) => {
      const user: User = {
        id: 'user-' + Date.now(),
        userName: variables.email,
        firstName: variables.name.split(' ')[0],
        lastName: variables.name.split(' ').slice(1).join(' '),
        emailId: variables.email,
        mobileNumber: '',
        mobileNumberVerified: false,
        roles: (response.roles && response.roles.length > 0)
          ? response.roles.map((r) => ({
              id: r.id || '',
              roleType: normalizeRoleType(r.roleType) as User['roles'][0]['roleType'],
            }))
          : [{ id: '', roleType: 'USER' as const }],
        loginType: variables.providerId === 'GOOGLE' ? 'GOOGLE' : 'FACEBOOK',
      };

      login(user, {
        token: response.token,
        expiresAt: Date.now() + 60 * 60 * 1000,
      });

      showAlert('success', 'Login successful!');

      // Use window.location for reliable redirect
      window.location.href = '/home';
    },
    onError: (error: any) => {
      const message = error?.response?.data?.message || error?.message || 'Login failed. Please try again.';
      showAlert('error', message);
    },
  });
}

// Generate OTP mutation
export function useGenerateOTP() {
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: (data: OTPRequest) =>
      post<ApiResponse>(API_URLS.OTP.GENERATE, data),
    onSuccess: () => {
      showAlert('success', 'OTP sent to your mobile number');
    },
  });
}

// Verify OTP mutation
export function useVerifyOTP() {
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: (data: OTPVerifyRequest) =>
      post<ApiResponse<boolean>>(API_URLS.OTP.VALIDATE, data),
    onSuccess: () => {
      showAlert('success', 'OTP verified successfully');
    },
  });
}

// Refresh token query
export function useRefreshToken() {
  const updateToken = useAuthStore((state) => state.updateToken);
  const logout = useAuthStore((state) => state.logout);
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  return useQuery({
    queryKey: ['refreshToken'],
    queryFn: () => get<{ token: string }>(API_URLS.AUTH.REFRESH),
    refetchInterval: APP_CONFIG.REFRESH_TOKEN_INTERVAL,
    enabled: isAuthenticated,
    retry: false,
    select: (data) => {
      updateToken(data.token);
      return data;
    },
  });
}

// Get user profile query
export function useProfile() {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const setUser = useAuthStore((state) => state.setUser);

  return useQuery({
    queryKey: ['profile'],
    queryFn: () => get<ApiResponse<User>>(API_URLS.USER.GET_BY_USERNAME),
    enabled: isAuthenticated,
    staleTime: 10 * 60 * 1000, // 10 minutes
    select: (data) => {
      if (data.body) {
        setUser(data.body);
      }
      return data.body;
    },
  });
}

// Update profile mutation
export function useUpdateProfile() {
  const queryClient = useQueryClient();
  const setUser = useAuthStore((state) => state.setUser);
  const showAlert = useUIStore((state) => state.showAlert);

  return useMutation({
    mutationFn: (data: Partial<User>) =>
      post<ApiResponse<User>>(API_URLS.USER.PROFILE_UPDATE, data),
    onSuccess: (response) => {
      if (response.body) {
        setUser(response.body);
      }
      queryClient.invalidateQueries({ queryKey: ['profile'] });
      showAlert('success', 'Profile updated successfully');
    },
  });
}

// Logout hook
export function useLogout() {
  const router = useRouter();
  const logout = useAuthStore((state) => state.logout);
  const queryClient = useQueryClient();

  return () => {
    logout();
    queryClient.clear();
    router.push('/');
  };
}
