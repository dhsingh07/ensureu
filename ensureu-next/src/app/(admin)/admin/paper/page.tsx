'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
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
import { useAdminPaperList, useUpdatePaperStatus } from '@/hooks/use-admin';
import {
  Plus,
  Search,
  Edit,
  Trash2,
  CheckCircle,
  Clock,
  FileText,
} from 'lucide-react';
import type { PaperCategory, TestType, PaperStateStatus } from '@/types/paper';

const categories: { value: PaperCategory; label: string }[] = [
  { value: 'SSC_CGL', label: 'SSC CGL' },
  { value: 'SSC_CHSL', label: 'SSC CHSL' },
  { value: 'SSC_CPO', label: 'SSC CPO' },
  { value: 'BANK_PO', label: 'Bank PO' },
];

const testTypes: { value: TestType; label: string }[] = [
  { value: 'FREE', label: 'Free' },
  { value: 'PAID', label: 'Paid' },
];

function getStatusBadge(status: PaperStateStatus) {
  switch (status) {
    case 'ACTIVE':
      return <Badge className="bg-green-500">Active</Badge>;
    case 'APPROVED':
      return <Badge className="bg-blue-500">Approved</Badge>;
    case 'DRAFT':
      return <Badge variant="secondary">Draft</Badge>;
    default:
      return <Badge variant="outline">{status}</Badge>;
  }
}

export default function PaperManagementPage() {
  const router = useRouter();
  const [category, setCategory] = useState<PaperCategory>('SSC_CGL');
  const [testType, setTestType] = useState<TestType>('FREE');
  const [searchQuery, setSearchQuery] = useState('');

  const { data: papers, isLoading } = useAdminPaperList(category, testType);
  const updateStatusMutation = useUpdatePaperStatus();

  const filteredPapers = papers?.filter((paper) =>
    paper.paperName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleEditPaper = (paperId: string) => {
    router.push(`/admin/paper-editor/${category}/${testType}/${paperId}`);
  };

  const handleCreatePaper = () => {
    router.push(`/admin/paper-editor/${category}/${testType}/new`);
  };

  const handleStatusChange = (paperId: string, newStatus: PaperStateStatus) => {
    updateStatusMutation.mutate({ paperId, status: newStatus });
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Paper Management</h1>
          <p className="text-slate-600">Create and manage test papers</p>
        </div>
        <Button onClick={handleCreatePaper} className="gap-2">
          <Plus className="h-4 w-4" />
          Create Paper
        </Button>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="pt-6">
          <div className="flex flex-wrap gap-4">
            <div className="flex-1 min-w-[200px]">
              <Select
                value={category}
                onValueChange={(value) => setCategory(value as PaperCategory)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select category" />
                </SelectTrigger>
                <SelectContent>
                  {categories.map((cat) => (
                    <SelectItem key={cat.value} value={cat.value}>
                      {cat.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="flex-1 min-w-[150px]">
              <Select
                value={testType}
                onValueChange={(value) => setTestType(value as TestType)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Test type" />
                </SelectTrigger>
                <SelectContent>
                  {testTypes.map((type) => (
                    <SelectItem key={type.value} value={type.value}>
                      {type.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="flex-1 min-w-[300px]">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
                <Input
                  placeholder="Search papers..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Papers Table */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <FileText className="h-5 w-5" />
            Papers ({filteredPapers?.length || 0})
          </CardTitle>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="space-y-4">
              {[1, 2, 3, 4, 5].map((i) => (
                <Skeleton key={i} className="h-12 w-full" />
              ))}
            </div>
          ) : filteredPapers && filteredPapers.length > 0 ? (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Paper Name</TableHead>
                  <TableHead>Questions</TableHead>
                  <TableHead>Duration</TableHead>
                  <TableHead>Score</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredPapers.map((paper) => (
                  <TableRow key={paper.paperId}>
                    <TableCell className="font-medium">
                      {paper.paperName}
                    </TableCell>
                    <TableCell>{paper.totalQuestions || '-'}</TableCell>
                    <TableCell>{Math.floor(paper.totalTime / 60)} min</TableCell>
                    <TableCell>{paper.totalScore}</TableCell>
                    <TableCell>{getStatusBadge(paper.status)}</TableCell>
                    <TableCell className="text-right">
                      <div className="flex items-center justify-end gap-2">
                        {paper.status === 'DRAFT' && (
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() =>
                              handleStatusChange(paper.paperId, 'ACTIVE')
                            }
                            title="Activate"
                          >
                            <CheckCircle className="h-4 w-4 text-green-600" />
                          </Button>
                        )}
                        {paper.status === 'ACTIVE' && (
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() =>
                              handleStatusChange(paper.paperId, 'DRAFT')
                            }
                            title="Deactivate"
                          >
                            <Clock className="h-4 w-4 text-orange-600" />
                          </Button>
                        )}
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleEditPaper(paper.paperId)}
                        >
                          <Edit className="h-4 w-4" />
                        </Button>
                        <Button variant="ghost" size="sm">
                          <Trash2 className="h-4 w-4 text-red-600" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          ) : (
            <div className="text-center py-12 text-slate-500">
              <FileText className="h-12 w-12 mx-auto mb-4 text-slate-300" />
              <p>No papers found</p>
              <p className="text-sm">
                Create a new paper to get started
              </p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
