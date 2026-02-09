import { RegisterForm } from '@/components/auth/register-form';
import type { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'Register - EnsureU',
  description: 'Create your EnsureU account',
};

export default function RegisterPage() {
  return <RegisterForm />;
}
