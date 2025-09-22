# Hiring SQL Challenge - Spring Boot Solution

## 🚀 Project Overview
This project solves the Hiring SQL Challenge using Spring Boot.  
It performs:
1. Calls `generateWebhook` API to get webhook URL + JWT accessToken.  
2. Submits the SQL query (based on regNo) back to the webhook.  

## 📂 Project Structure
- `src/main/java/com/example/hiring/HiringApplication.java` → main logic
- `pom.xml` → dependencies & build config
- `release/hiring-app-0.0.1-SNAPSHOT.jar` → built JAR for submission

## 🛠️ Build & Run
### Build:
```bash
mvn clean package
