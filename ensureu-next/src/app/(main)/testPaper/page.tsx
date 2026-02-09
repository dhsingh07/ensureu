'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { getPaperInfo } from '@/hooks/use-papers';

export default function TestPaperRedirectPage() {
  const router = useRouter();

  useEffect(() => {
    const info = getPaperInfo();
    if (info?.paperId) {
      router.replace(`/exam/${info.paperId}`);
    } else {
      router.replace('/home');
    }
  }, [router]);

  return null;
}

