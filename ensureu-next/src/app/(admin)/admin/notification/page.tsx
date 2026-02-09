'use client';

import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Checkbox } from '@/components/ui/checkbox';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useUIStore } from '@/stores/ui-store';
import { Bell, Send, Loader2, Users, Mail, Smartphone } from 'lucide-react';
import type { PaperCategory } from '@/types/paper';

const categories: { value: PaperCategory | 'ALL'; label: string }[] = [
  { value: 'ALL', label: 'All Categories' },
  { value: 'SSC_CGL', label: 'SSC CGL' },
  { value: 'SSC_CHSL', label: 'SSC CHSL' },
  { value: 'SSC_CPO', label: 'SSC CPO' },
  { value: 'BANK_PO', label: 'Bank PO' },
];

const notificationTypes = [
  { id: 'push', label: 'Push Notification', icon: Bell },
  { id: 'email', label: 'Email', icon: Mail },
  { id: 'sms', label: 'SMS', icon: Smartphone },
];

export default function NotificationPage() {
  const showAlert = useUIStore((state) => state.showAlert);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [title, setTitle] = useState('');
  const [message, setMessage] = useState('');
  const [targetCategory, setTargetCategory] = useState<PaperCategory | 'ALL'>('ALL');
  const [selectedTypes, setSelectedTypes] = useState<string[]>(['push']);

  const handleTypeToggle = (typeId: string) => {
    setSelectedTypes((prev) =>
      prev.includes(typeId)
        ? prev.filter((t) => t !== typeId)
        : [...prev, typeId]
    );
  };

  const handleSubmit = async () => {
    if (!title.trim()) {
      showAlert('error', 'Please enter a title');
      return;
    }
    if (!message.trim()) {
      showAlert('error', 'Please enter a message');
      return;
    }
    if (selectedTypes.length === 0) {
      showAlert('error', 'Please select at least one notification type');
      return;
    }

    setIsSubmitting(true);

    // Simulate API call
    setTimeout(() => {
      showAlert('success', 'Notification sent successfully');
      setTitle('');
      setMessage('');
      setIsSubmitting(false);
    }, 1500);
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
          <Bell className="h-6 w-6" />
          Send Notification
        </h1>
        <p className="text-slate-600">
          Send notifications to users based on category
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Notification Form */}
        <div className="lg:col-span-2">
          <Card>
            <CardHeader>
              <CardTitle>Compose Notification</CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="space-y-2">
                <Label htmlFor="title">Title</Label>
                <Input
                  id="title"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="Notification title"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="message">Message</Label>
                <Textarea
                  id="message"
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  placeholder="Enter your notification message..."
                  rows={5}
                />
              </div>

              <div className="space-y-2">
                <Label>Target Audience</Label>
                <Select
                  value={targetCategory}
                  onValueChange={(value) =>
                    setTargetCategory(value as PaperCategory | 'ALL')
                  }
                >
                  <SelectTrigger>
                    <SelectValue />
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

              <div className="space-y-3">
                <Label>Notification Channels</Label>
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                  {notificationTypes.map((type) => (
                    <div
                      key={type.id}
                      className={`flex items-center gap-3 p-4 rounded-lg border-2 cursor-pointer transition-colors ${
                        selectedTypes.includes(type.id)
                          ? 'border-primary bg-primary/5'
                          : 'border-slate-200 hover:border-slate-300'
                      }`}
                      onClick={() => handleTypeToggle(type.id)}
                    >
                      <Checkbox
                        checked={selectedTypes.includes(type.id)}
                        onCheckedChange={() => handleTypeToggle(type.id)}
                      />
                      <type.icon className="h-5 w-5 text-slate-600" />
                      <span className="text-sm font-medium">{type.label}</span>
                    </div>
                  ))}
                </div>
              </div>

              <Button
                onClick={handleSubmit}
                disabled={isSubmitting}
                className="w-full gap-2"
              >
                {isSubmitting ? (
                  <Loader2 className="h-4 w-4 animate-spin" />
                ) : (
                  <Send className="h-4 w-4" />
                )}
                Send Notification
              </Button>
            </CardContent>
          </Card>
        </div>

        {/* Preview */}
        <div>
          <Card>
            <CardHeader>
              <CardTitle>Preview</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="bg-slate-100 rounded-lg p-4 space-y-2">
                <div className="flex items-start gap-3">
                  <div className="p-2 bg-primary rounded-lg">
                    <Bell className="h-4 w-4 text-white" />
                  </div>
                  <div className="flex-1">
                    <p className="font-semibold text-slate-900">
                      {title || 'Notification Title'}
                    </p>
                    <p className="text-sm text-slate-600 mt-1">
                      {message || 'Your notification message will appear here...'}
                    </p>
                    <p className="text-xs text-slate-400 mt-2">Just now</p>
                  </div>
                </div>
              </div>

              <div className="mt-6 pt-4 border-t">
                <h4 className="text-sm font-medium text-slate-700 mb-3">
                  Delivery Summary
                </h4>
                <div className="space-y-2 text-sm">
                  <div className="flex items-center justify-between">
                    <span className="text-slate-500">Target:</span>
                    <span className="font-medium">
                      {categories.find((c) => c.value === targetCategory)?.label}
                    </span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-slate-500">Channels:</span>
                    <span className="font-medium">
                      {selectedTypes.length > 0
                        ? selectedTypes
                            .map(
                              (t) =>
                                notificationTypes.find((nt) => nt.id === t)?.label
                            )
                            .join(', ')
                        : 'None selected'}
                    </span>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-slate-500">Est. Recipients:</span>
                    <span className="font-medium flex items-center gap-1">
                      <Users className="h-3 w-3" />
                      {targetCategory === 'ALL' ? '5,420' : '1,250'}
                    </span>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
