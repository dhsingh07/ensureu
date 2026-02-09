'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function PreviousPapersAliasPage() {
  const router = useRouter();

  useEffect(() => {
    router.replace('/previous-papers');
  }, [router]);

  return null;
}

