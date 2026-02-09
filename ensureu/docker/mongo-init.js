// MongoDB initialization script for EnsureU database
// This script creates the application database and user with appropriate permissions

// Switch to the assessu database
db = db.getSiblingDB('assessu');

// Get the app password from environment variable (set in docker-compose.yml)
const appPassword = process.env.MONGO_APP_PASSWORD || 'AppPassword123';

// Create application user with read/write permissions
db.createUser({
  user: 'appUser',
  pwd: appPassword,
  roles: [
    {
      role: 'readWrite',
      db: 'assessu'
    }
  ]
});

// Create initial collections (optional - MongoDB creates them automatically on first insert)
db.createCollection('users');
db.createCollection('papers');
db.createCollection('questions');
db.createCollection('subscriptions');

// Create indexes for better performance
db.users.createIndex({ "email": 1 }, { unique: true });
db.users.createIndex({ "phone": 1 }, { sparse: true });
db.papers.createIndex({ "category": 1 });
db.papers.createIndex({ "createdAt": -1 });

print('MongoDB initialization completed successfully for assessu database');
