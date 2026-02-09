export interface BlogCategory {
  id: string;
  name: string;
}

export interface BlogTag {
  id?: string;
  name?: string;
}

export interface BlogItem {
  id: string;
  title: string;
  body: string;
  thumbnailUrl?: string;
  category?: BlogCategory;
  tags?: string | BlogTag[];
  views?: number;
  likes?: string[];
  createdDate?: number;
  author?: string;
  priority?: number;
  userLiked?: boolean;
}

export interface BlogCommentEntry {
  body: string;
  createdBy?: string;
  createdDate?: number;
}

export interface BlogComment {
  id: string;
  blogId: string;
  comments?: BlogCommentEntry[];
  body?: string;
  createdBy?: string;
  createdDate?: number;
}

