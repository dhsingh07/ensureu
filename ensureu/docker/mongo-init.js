// MongoDB initialization script for EnsureU database
// This script creates the application database and user with appropriate permissions

// Switch to the ensureu database
db = db.getSiblingDB('ensureu');

// Get credentials from environment variables (set in docker-compose.yml)
const appUser = process.env.MONGO_APP_USER || 'ensureu';
const appPassword = process.env.MONGO_APP_PASSWORD || 'Ensureu@india123';

// Create application user with read/write permissions
db.createUser({
  user: appUser,
  pwd: appPassword,
  roles: [
    {
      role: 'readWrite',
      db: 'ensureu'
    }
  ]
});

// Create initial collections (optional - MongoDB creates them automatically on first insert)
db.createCollection('user');
db.createCollection('paper');
db.createCollection('question');
db.createCollection('subscription');
db.createCollection('role');

// Seed default roles
db.role.insertMany([
  { _id: 'ROLE_USER', roleType: 'USER', _class: 'com.book.ensureu.model.Role' },
  { _id: 'ROLE_ADMIN', roleType: 'ADMIN', _class: 'com.book.ensureu.model.Role' },
  { _id: 'ROLE_SUPERADMIN', roleType: 'SUPERADMIN', _class: 'com.book.ensureu.model.Role' },
  { _id: 'ROLE_TEACHER', roleType: 'TEACHER', _class: 'com.book.ensureu.model.Role' }
]);

// Create indexes for better performance
db.user.createIndex({ "userName": 1 }, { unique: true });
db.user.createIndex({ "emailId": 1 }, { sparse: true });
db.paper.createIndex({ "paperCategory": 1 });
db.paper.createIndex({ "createdDate": -1 });

print('MongoDB initialization completed successfully for ensureu database');
