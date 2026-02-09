// MongoDB Initialization Script for EnsureU
// This script runs when the MongoDB container starts for the first time

// Switch to ensureu database
db = db.getSiblingDB('ensureu');

// Create application user
db.createUser({
  user: 'ensureu',
  pwd: 'Ensureu@india123',
  roles: [
    { role: 'readWrite', db: 'ensureu' }
  ]
});

// Create indexes for better query performance

// Users collection
db.users.createIndex({ "userName": 1 }, { unique: true, sparse: true });
db.users.createIndex({ "email": 1 }, { sparse: true });
db.users.createIndex({ "mobileNumber": 1 }, { sparse: true });
db.users.createIndex({ "createdDate": -1 });

// Papers collection
db.papers.createIndex({ "paperCategory": 1, "paperStatus": 1 });
db.papers.createIndex({ "paperType": 1 });
db.papers.createIndex({ "createdDate": -1 });
db.papers.createIndex({ "paperSubCategory": 1 });

// User Progress/Enrollments
db.userenrollments.createIndex({ "userId": 1, "paperId": 1 });
db.userenrollments.createIndex({ "userId": 1, "status": 1 });

// Subscriptions
db.subscriptions.createIndex({ "userId": 1, "state": 1 });
db.subscriptions.createIndex({ "expiryDate": 1 });

// AI Analysis collection (new)
db.user_exam_analyses.createIndex({ "user_id": 1, "analyzed_at": -1 });
db.user_exam_analyses.createIndex({ "user_id": 1, "exam_id": 1 });
db.user_exam_analyses.createIndex({ "analysis_month": 1 });

// App configuration
db.app_config.createIndex({ "type": 1 }, { unique: true });

// Blog
db.blogs.createIndex({ "createdDate": -1 });
db.blogs.createIndex({ "category": 1 });

print('✅ MongoDB initialized successfully');
print('✅ User "ensureu" created');
print('✅ Indexes created');
