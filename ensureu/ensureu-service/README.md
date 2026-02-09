## Microservice setup and run.

1. setup mongo db in application.properties file
start mongodb server local then you need to add 127.0.0.1:27027 in properties file
start mongo server : mongod --dbpath ${dbpath} --auth

start mvn boot

2. Click the README.md link from the list of files.
3. Click the **Edit** button.
4. Delete the following text: *Delete this line to make a change to the README from Bitbucket.*
5. After making your change, click **Commit** and then **Commit** again in the dialog. The commit page will open and you’ll see the change you just made.
6. Go back to the **Source** page.


MongoDB
1. Enable Authentication in MongoDB Configuration
   If you're running a local MongoDB server, you must enable authentication in the config file (mongod.conf):

Edit mongod.conf (usually in /etc/mongod.conf on Linux or /usr/local/etc/mongod.conf on macOS):
yaml
Copy
Edit
security:
authorization: enabled
Then restart MongoDB:

bash
Copy
Edit
# macOS / Linux:
sudo systemctl restart mongod

# Or if you're using brew on macOS:
brew services restart mongodb-community
👤 2. Create Admin User (if not done yet)
Connect to MongoDB without authentication (only works before enabling auth):

bash
Copy
Edit
mongosh
Then switch to the admin DB and create the user:

javascript
Copy
Edit
use admin
db.createUser({
user: "adminUser",
pwd: "securePassword123",
roles: [{ role: "userAdminAnyDatabase", db: "admin" }]
})
You can now enable authorization in mongod.conf and restart the server.

📦 3. Create Application User for Your DB
Now log in with the admin user:

bash
Copy
Edit
mongosh -u adminUser -p securePassword123 --authenticationDatabase admin
Then create a user for your app DB (e.g., assessu):

javascript
Copy
Edit
use assessu
db.createUser({
user: "appUser",
pwd: "appPassword123",
roles: [{ role: "readWrite", db: "assessu" }]
})
🔗 4. Connect with Spring Boot
Update your Spring Boot configuration (e.g., in application.properties):

properties
Copy
Edit
spring.data.mongodb.uri=mongodb://appUser:appPassword123@localhost:27017/assessu
Or if using YAML:

yaml
Copy
Edit
spring:
data:
mongodb:
uri: mongodb://appUser:appPassword123@localhost:27017/assessu
---

✅ Start MongoDB

brew services start mongodb-community

This starts MongoDB as a background service, and it will auto-start on boot.

🛑 Stop MongoDB

brew services stop mongodb-community

This stops the running MongoDB background service.
🔁 Restart MongoDB

brew services restart mongodb-community

# Subscription and Entitlement
EntitlementType (Access Type)

SUBSCRIPTION - Regular paper subscriptions
TEST_SERIES  - Test series packages
USER_PASS    - Simple pass-based access

  ---
# 📊 Example Flow

# 1. Login and get JWT token
TOKEN=$(curl -X POST "${BASE_URL}/auth/login" \
-H "Content-Type: application/json" \
-d '{"username":"user@example.com","password":"password123"}' \
| jq -r '.body.token')

# 2. Get available passes
curl -X GET "${BASE_URL}/pass/list" \
-H "Authorization: Bearer ${TOKEN}"

# 3. Subscribe to Monthly Pass
curl -X POST "${BASE_URL}/pass/subscribe" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer ${TOKEN}" \
-d '{
"id": 5001,
"subscriptionType": "MONTHLY",
"price": 299.00,
"discountPercentage": 10.0
}'

# 4. Subscribe to a specific subscription package
curl -X POST "${BASE_URL}/subscription/subscribe" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer ${TOKEN}" \
-d '{
"id": 101,
"listOfSubscriptionIds": [101],
"subscriptionType": "MONTHLY",
"paperType": "SSC",
"testType": "PAID",
"paperCategory": "SSC_CGL",
"paperSubCategory": "SSC_CGL_TIER1"
}'

# 5. Get user's active entitlements
curl -X POST "${BASE_URL}/entitlement/getUserEntitle/user123?active=true" \
-H "Authorization: Bearer ${TOKEN}"

# 6. Subscribe to test series
curl -X POST "${BASE_URL}/series/subscribe" \
-H "Authorization: Bearer ${TOKEN}" \
-H "series: TS_2024_SSC_CGL_001"