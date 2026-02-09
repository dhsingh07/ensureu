import { LoginForm } from '@/components/auth/login-form';
import type { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Login - EnsureU',
  description: 'Sign in to your EnsureU account',
};

export default function LoginPage() {
  return <LoginForm />;
}
