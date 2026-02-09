import { ForgotPasswordForm } from '@/components/auth/forgot-password-form';
import type { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Forgot Password - EnsureU',
  description: 'Reset your EnsureU account password',
};

export default function ForgotPasswordPage() {
  return <ForgotPasswordForm />;
}
