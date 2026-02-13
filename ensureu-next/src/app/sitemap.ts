import { MetadataRoute } from 'next';

export default function sitemap(): MetadataRoute.Sitemap {
  const baseUrl = process.env.NEXT_PUBLIC_SITE_URL || 'https://ensureu.com';

  // Static pages
  const staticPages = [
    '',
    '/login',
    '/register',
    '/forgot-password',
    '/home',
    '/practice',
    '/quiz',
    '/previous-papers',
    '/blog',
    '/contact',
    '/information',
    '/instruction',
  ];

  const staticRoutes = staticPages.map((route) => ({
    url: `${baseUrl}${route}`,
    lastModified: new Date(),
    changeFrequency: (route === '' ? 'daily' : 'weekly') as 'daily' | 'weekly',
    priority: route === '' ? 1 : route === '/home' ? 0.9 : 0.8,
  }));

  // Practice category pages
  const practiceCategories = ['SSC_CGL', 'SSC_CPO', 'SSC_CHSL', 'BANK_PO'];
  const practiceRoutes = practiceCategories.map((category) => ({
    url: `${baseUrl}/practice/${category}`,
    lastModified: new Date(),
    changeFrequency: 'weekly' as const,
    priority: 0.7,
  }));

  return [...staticRoutes, ...practiceRoutes];
}
