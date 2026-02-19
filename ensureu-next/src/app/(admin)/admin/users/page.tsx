'use client';

import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { useUserList, useUserSearch, useAssignRole } from '@/hooks/use-admin';
import { Users, Search, ChevronLeft, ChevronRight, Shield } from 'lucide-react';

const roles = [
  { value: 'ROLE_USER', label: 'User' },
  { value: 'ROLE_TEACHER', label: 'Teacher' },
  { value: 'ROLE_ADMIN', label: 'Admin' },
  { value: 'ROLE_SUPERADMIN', label: 'Super Admin' },
];

function getRoleBadge(role: string) {
  switch (role) {
    case 'ROLE_SUPERADMIN':
      return <Badge className="bg-purple-500">Super Admin</Badge>;
    case 'ROLE_ADMIN':
      return <Badge className="bg-blue-500">Admin</Badge>;
    case 'ROLE_TEACHER':
      return <Badge className="bg-green-500">Teacher</Badge>;
    default:
      return <Badge variant="secondary">User</Badge>;
  }
}

export default function UserManagementPage() {
  const [page, setPage] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [isSearching, setIsSearching] = useState(false);

  const { data: usersData, isLoading: loadingUsers } = useUserList(page, 20);
  const { data: searchResults, isLoading: loadingSearch } = useUserSearch(
    searchQuery,
    isSearching && searchQuery.length >= 2
  );
  const assignRoleMutation = useAssignRole();

  const users = isSearching && searchQuery.length >= 2 ? searchResults : usersData?.content;
  const isLoading = isSearching ? loadingSearch : loadingUsers;

  const handleSearch = (query: string) => {
    setSearchQuery(query);
    setIsSearching(query.length >= 2);
  };

  const handleRoleChange = (userId: string, newRole: string) => {
    assignRoleMutation.mutate({ userId, role: newRole });
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
          <Users className="h-6 w-6" />
          User Management
        </h1>
        <p className="text-slate-600">Manage users and assign roles</p>
      </div>

      {/* Search */}
      <Card>
        <CardContent className="pt-6">
          <div className="relative max-w-md">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
            <Input
              placeholder="Search users by name, email, or phone..."
              value={searchQuery}
              onChange={(e) => handleSearch(e.target.value)}
              className="pl-10"
            />
          </div>
        </CardContent>
      </Card>

      {/* Users Table */}
      <Card>
        <CardHeader>
          <CardTitle>Users</CardTitle>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="space-y-4">
              {[1, 2, 3, 4, 5].map((i) => (
                <Skeleton key={i} className="h-12 w-full" />
              ))}
            </div>
          ) : users && users.length > 0 ? (
            <>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Name</TableHead>
                    <TableHead>Email</TableHead>
                    <TableHead>Phone</TableHead>
                    <TableHead>Current Role</TableHead>
                    <TableHead>Assign Role</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {users.map((user, index) => (
                    <TableRow key={user.userId || user.id || user.email || `user-${index}`}>
                      <TableCell className="font-medium">
                        {user.firstName} {user.lastName}
                      </TableCell>
                      <TableCell>{user.email || '-'}</TableCell>
                      <TableCell>{user.phone || '-'}</TableCell>
                      <TableCell>
                        {getRoleBadge(user.roles?.[0] || 'ROLE_USER')}
                      </TableCell>
                      <TableCell>
                        <Select
                          value={user.roles?.[0] || 'ROLE_USER'}
                          onValueChange={(value) =>
                            handleRoleChange(user.userId || String(user.id), value)
                          }
                          disabled={assignRoleMutation.isPending}
                        >
                          <SelectTrigger className="w-[140px]">
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            {roles.map((role) => (
                              <SelectItem key={role.value} value={role.value}>
                                <div className="flex items-center gap-2">
                                  <Shield className="h-3 w-3" />
                                  {role.label}
                                </div>
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>

              {/* Pagination */}
              {!isSearching && usersData && (
                <div className="flex items-center justify-between mt-4 pt-4 border-t">
                  <p className="text-sm text-slate-500">
                    Page {page + 1} of {usersData.totalPages}
                    {' '}({usersData.totalElements} total users)
                  </p>
                  <div className="flex items-center gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setPage(Math.max(0, page - 1))}
                      disabled={page === 0}
                    >
                      <ChevronLeft className="h-4 w-4" />
                      Previous
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() =>
                        setPage(Math.min(usersData.totalPages - 1, page + 1))
                      }
                      disabled={page >= usersData.totalPages - 1}
                    >
                      Next
                      <ChevronRight className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              )}
            </>
          ) : (
            <div className="text-center py-12 text-slate-500">
              <Users className="h-12 w-12 mx-auto mb-4 text-slate-300" />
              <p>No users found</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
