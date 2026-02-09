// Auth types for EnsureU

export type RoleType = 'USER' | 'ADMIN' | 'SUPERADMIN' | 'TEACHER';

export interface Role {
  id: string;
  roleType: RoleType;
}

export interface User {
  id: string;
  userName: string;
  firstName: string;
  lastName: string;
  emailId: string;
  mobileNumber: string;
  mobileNumberVerified: boolean;
  roles: Role[];
  loginType: 'SIGNUP' | 'GOOGLE' | 'FACEBOOK';
  gender?: string;
  dob?: string;
  address?: Address;
  createdAt?: string;
  updatedAt?: string;
}

export interface Address {
  street?: string;
  city?: string;
  state?: string;
  pincode?: string;
  country?: string;
}

export interface AuthTokens {
  token: string;
  refreshToken?: string;
  expiresAt: number;
}

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  name: string;
  verification: boolean;
  roles: { id: string; roleType: string }[];
}

export interface RegisterData {
  userName: string;
  firstName: string;
  lastName: string;
  emailId: string;
  password: string;
  mobileNumber: string;
  userLoginType: 'SIGNUP';
  roles: Role[];
}

export interface OTPRequest {
  userName: string;
}

export interface OTPVerifyRequest {
  userName: string;
  otp: string;
  password?: string;
  confirmPassword?: string;
}

export interface ProviderTokenRequest {
  accessToken?: string;
  accessTokenExpiration?: string;
  idToken?: string;  // Google credential token
  name: string;
  email: string;
  loginId?: string;
  picture?: string;
  providerId: 'GOOGLE' | 'FACEBOOK';
}
