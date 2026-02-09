'use client';

import { useEffect, useState } from 'react';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useProfile, useUpdateProfile } from '@/hooks/use-auth';

interface AddressData {
  addressLine1?: string;
  addressLine2?: string;
  houseNumber?: string;
  city?: string;
  state?: string;
  country?: string;
}

interface ProfileFormData {
  firstName?: string;
  lastName?: string;
  userName?: string;
  dob?: string;
  gender?: string;
  emailId?: string;
  mobileNumber?: string;
  address?: AddressData;
}

export default function ProfilePage() {
  const { data: profile, isLoading } = useProfile();
  const updateProfile = useUpdateProfile();
  const [formData, setFormData] = useState<ProfileFormData>({
    address: {
      addressLine1: '',
      addressLine2: '',
      houseNumber: '',
      city: '',
      state: '',
      country: '',
    },
  });

  useEffect(() => {
    if (!profile) return;
    setFormData({
      firstName: profile.firstName,
      lastName: profile.lastName,
      userName: profile.userName,
      dob: profile.dob,
      gender: profile.gender,
      emailId: profile.emailId,
      mobileNumber: profile.mobileNumber,
      address: {
        addressLine1: (profile.address as any)?.addressLine1 || '',
        addressLine2: (profile.address as any)?.addressLine2 || '',
        houseNumber: (profile.address as any)?.houseNumber || '',
        city: (profile.address as any)?.city || '',
        state: (profile.address as any)?.state || '',
        country: (profile.address as any)?.country || '',
      },
    });
  }, [profile]);

  const handleChange = (field: keyof ProfileFormData, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleAddressChange = (field: keyof AddressData, value: string) => {
    setFormData((prev) => ({
      ...prev,
      address: {
        ...prev.address,
        [field]: value,
      },
    }));
  };

  const handleSubmit = () => {
    updateProfile.mutate(formData);
  };

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-12 text-slate-500">
        Loading profile...
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-10 space-y-6">
      <h1 className="text-3xl font-bold text-slate-900">Personal Information</h1>

      <div className="grid gap-6 lg:grid-cols-2">
        <Card className="border border-slate-200">
          <CardContent className="p-6 space-y-4">
            <h2 className="text-lg font-semibold text-slate-900">Profile</h2>
            <div className="grid gap-4">
              <div>
                <Label>First Name</Label>
                <Input
                  value={formData.firstName || ''}
                  onChange={(event) => handleChange('firstName', event.target.value)}
                />
              </div>
              <div>
                <Label>Last Name</Label>
                <Input
                  value={formData.lastName || ''}
                  onChange={(event) => handleChange('lastName', event.target.value)}
                />
              </div>
              <div>
                <Label>Username</Label>
                <Input value={formData.userName || ''} disabled />
              </div>
              <div>
                <Label>Date of birth</Label>
                <Input
                  type="date"
                  value={formData.dob || ''}
                  onChange={(event) => handleChange('dob', event.target.value)}
                />
              </div>
              <div>
                <Label>Gender</Label>
                <div className="flex gap-4 pt-1">
                  <label className="flex items-center gap-2 text-sm text-slate-600">
                    <input
                      type="radio"
                      name="gender"
                      value="male"
                      checked={formData.gender === 'male'}
                      onChange={() => handleChange('gender', 'male')}
                    />
                    Male
                  </label>
                  <label className="flex items-center gap-2 text-sm text-slate-600">
                    <input
                      type="radio"
                      name="gender"
                      value="female"
                      checked={formData.gender === 'female'}
                      onChange={() => handleChange('gender', 'female')}
                    />
                    Female
                  </label>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="border border-slate-200">
          <CardContent className="p-6 space-y-4">
            <h2 className="text-lg font-semibold text-slate-900">Contact Info</h2>
            <div className="grid gap-4">
              <div>
                <Label>Email</Label>
                <Input
                  type="email"
                  value={formData.emailId || ''}
                  onChange={(event) => handleChange('emailId', event.target.value)}
                />
              </div>
              <div>
                <Label>Mobile Number</Label>
                <Input
                  value={formData.mobileNumber || ''}
                  onChange={(event) => handleChange('mobileNumber', event.target.value)}
                />
              </div>
              <div>
                <Label>Address Line 1</Label>
                <Input
                  value={formData.address?.addressLine1 || ''}
                  onChange={(event) => handleAddressChange('addressLine1', event.target.value)}
                />
              </div>
              <div>
                <Label>Address Line 2</Label>
                <Input
                  value={formData.address?.addressLine2 || ''}
                  onChange={(event) => handleAddressChange('addressLine2', event.target.value)}
                />
              </div>
              <div>
                <Label>City</Label>
                <Input
                  value={formData.address?.city || ''}
                  onChange={(event) => handleAddressChange('city', event.target.value)}
                />
              </div>
              <div>
                <Label>State</Label>
                <Input
                  value={formData.address?.state || ''}
                  onChange={(event) => handleAddressChange('state', event.target.value)}
                />
              </div>
              <div>
                <Label>Country</Label>
                <Input
                  value={formData.address?.country || ''}
                  onChange={(event) => handleAddressChange('country', event.target.value)}
                />
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <div className="flex justify-end">
        <Button onClick={handleSubmit} disabled={updateProfile.isPending}>
          Update Profile
        </Button>
      </div>
    </div>
  );
}

