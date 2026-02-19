export function OrganizationStructuredData() {
  const siteUrl = process.env.NEXT_PUBLIC_SITE_URL || 'https://ensureu.com';

  const structuredData = {
    '@context': 'https://schema.org',
    '@type': 'Organization',
    name: 'GrayscaleLabs AI Pvt Ltd',
    legalName: 'GrayscaleLabs AI Private Limited',
    url: siteUrl,
    logo: `${siteUrl}/logo.png`,
    description:
      'GrayscaleLabs AI Pvt Ltd - EnsureU is an online assessment platform for SSC, Bank PO, and other competitive exam preparation.',
    brand: {
      '@type': 'Brand',
      name: 'EnsureU',
    },
    sameAs: [],
  };

  return (
    <script
      type="application/ld+json"
      dangerouslySetInnerHTML={{ __html: JSON.stringify(structuredData) }}
    />
  );
}

export function WebsiteStructuredData() {
  const siteUrl = process.env.NEXT_PUBLIC_SITE_URL || 'https://ensureu.com';

  const structuredData = {
    '@context': 'https://schema.org',
    '@type': 'WebSite',
    name: 'EnsureU',
    url: siteUrl,
    potentialAction: {
      '@type': 'SearchAction',
      target: {
        '@type': 'EntryPoint',
        urlTemplate: `${siteUrl}/search?q={search_term_string}`,
      },
      'query-input': 'required name=search_term_string',
    },
  };

  return (
    <script
      type="application/ld+json"
      dangerouslySetInnerHTML={{ __html: JSON.stringify(structuredData) }}
    />
  );
}

export function EducationalOrganizationStructuredData() {
  const siteUrl = process.env.NEXT_PUBLIC_SITE_URL || 'https://ensureu.com';

  const structuredData = {
    '@context': 'https://schema.org',
    '@type': 'EducationalOrganization',
    name: 'EnsureU by GrayscaleLabs AI',
    url: siteUrl,
    parentOrganization: {
      '@type': 'Organization',
      name: 'GrayscaleLabs AI Pvt Ltd',
    },
    description:
      'EnsureU by GrayscaleLabs AI - Online assessment and exam preparation platform for SSC CGL, SSC CPO, SSC CHSL, Bank PO and other competitive exams in India.',
    areaServed: {
      '@type': 'Country',
      name: 'India',
    },
    hasOfferCatalog: {
      '@type': 'OfferCatalog',
      name: 'Exam Preparation Courses',
      itemListElement: [
        {
          '@type': 'Course',
          name: 'SSC CGL Preparation',
          description: 'Mock tests and practice papers for SSC CGL exam',
        },
        {
          '@type': 'Course',
          name: 'SSC CPO Preparation',
          description: 'Mock tests and practice papers for SSC CPO exam',
        },
        {
          '@type': 'Course',
          name: 'SSC CHSL Preparation',
          description: 'Mock tests and practice papers for SSC CHSL exam',
        },
        {
          '@type': 'Course',
          name: 'Bank PO Preparation',
          description: 'Mock tests and practice papers for Bank PO exams',
        },
      ],
    },
  };

  return (
    <script
      type="application/ld+json"
      dangerouslySetInnerHTML={{ __html: JSON.stringify(structuredData) }}
    />
  );
}
