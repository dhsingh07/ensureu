// Route protection middleware - migrated from Angular guards

import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

// Routes that require authentication
const protectedRoutes = [
  '/home',
  '/exam',
  '/test-paper',
  '/testPaper',
  '/practice',
  '/progress',
  '/profile',
  '/previous-papers',
  '/previousPapers',
  '/instruction',
];

// Routes that require admin access (ADMIN, SUPERADMIN, or TEACHER)
const adminRoutes = ['/admin'];

// Routes that require super admin access only
const superAdminRoutes = ['/admin/users', '/admin/config'];

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  // Skip middleware for static files and API routes
  if (
    pathname.startsWith('/_next') ||
    pathname.startsWith('/api') ||
    pathname.includes('.') ||
    pathname === '/favicon.ico'
  ) {
    return NextResponse.next();
  }

  // Get auth data from cookies
  const authStorage = request.cookies.get('auth-storage')?.value;

  let authData: {
    state?: {
      user?: {
        roles?: { roleType: string }[];
      };
      isAuthenticated?: boolean;
    };
  } | null = null;

  try {
    if (authStorage) {
      authData = JSON.parse(authStorage);
    }
  } catch {
    authData = null;
  }

  const isAuthenticated = authData?.state?.isAuthenticated;
  const userRoles = authData?.state?.user?.roles?.map((r) => r.roleType) || [];

  // Check if route requires authentication
  const isProtectedRoute = protectedRoutes.some((route) =>
    pathname.startsWith(route)
  );
  const isAdminRoute = adminRoutes.some((route) =>
    pathname.startsWith(route)
  );
  const isSuperAdminRoute = superAdminRoutes.some((route) =>
    pathname.startsWith(route)
  );

  // Redirect unauthenticated users to login
  if ((isProtectedRoute || isAdminRoute) && !isAuthenticated) {
    const url = new URL('/login', request.url);
    url.searchParams.set('callbackUrl', pathname);
    return NextResponse.redirect(url);
  }

  // Check admin access
  if (isAdminRoute && isAuthenticated) {
    const hasAdminAccess = userRoles.some((role) =>
      ['SUPERADMIN', 'ADMIN', 'TEACHER'].includes(role)
    );

    if (!hasAdminAccess) {
      return NextResponse.redirect(new URL('/', request.url));
    }
  }

  // Check super admin access
  if (isSuperAdminRoute && isAuthenticated) {
    const isSuperAdmin = userRoles.includes('SUPERADMIN');

    if (!isSuperAdmin) {
      return NextResponse.redirect(new URL('/admin', request.url));
    }
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    /*
     * Match all request paths except:
     * - api routes
     * - static files
     * - image files
     */
    '/((?!api|_next/static|_next/image|favicon.ico|.*\\..*).*)',
  ],
};
