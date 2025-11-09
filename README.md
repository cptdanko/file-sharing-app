# 📂 File Sharing App — Secure, Fast & Cloud-Native

A modern **File Sharing Application** built with **Java Spring Boot**, **ReactJS**, and **AWS Cloud Services**.  
This project demonstrates how to design and deploy a secure, scalable, and production-ready **document sharing platform** — similar to Dropbox or Google Drive — with a focus on **security, cloud integration, and developer learning**.

---

## 🚀 Overview

The **File Sharing App** enables users to:
- 🔑 Authenticate securely with **Google OAuth2.0** and **JWT tokens**
- 📤 Upload, download, and manage documents stored in **AWS S3**
- 🤝 Share files with other users for collaboration
- ⏱️ Automate tasks with **scheduled jobs** (e.g., email notifications)
- 📊 Visualize architecture with included **system design diagrams**

This repository serves both as:
- A **production-ready template** for building file sharing systems
- A **learning resource** for developers exploring **Spring Boot APIs**, **React frontends**, and **AWS cloud-native services**

---

## 🧩 Technologies Used & Why

| Technology | Purpose | Why It’s Used |
|------------|---------|---------------|
| **Java 17** | Backend language | Stable, enterprise-ready, modern features |
| **Spring Boot** | API framework | Rapid development, built-in support for security & reactive programming |
| **Spring Security + JWT** | Authentication | Secure login and token-based authorization |
| **Spring Data DynamoDB** | Database integration | Simplifies AWS DynamoDB metadata storage |
| **Springdoc OpenAPI** | API documentation | Auto-generates Swagger UI for easy API exploration |
| **Lombok** | Code simplification | Reduces boilerplate with annotations |
| **io.jsonwebtoken** | Token handling | Creates and validates JWTs |
| **ReactJS + MUI** | Frontend framework | Responsive, modern UI with Material Design |
| **React Cookie** | Token management | Handles authentication cookies in browser |
| **@react-oauth/google** | OAuth integration | Enables Google login |
| **AWS S3** | File storage | Secure, scalable cloud storage |
| **AWS DynamoDB** | Metadata storage | Fast, serverless NoSQL database |
| **AWS Route 53** | DNS routing | Reliable domain management |
| **AWS Elastic Beanstalk** | Deployment | Simplifies scaling and hosting |
| **Docker & Docker Compose** | Containerization | Portable, reproducible deployments |
| **GitHub Actions** | CI/CD | Automates builds and tests |

---

## ⚙️ Installation & Setup

### 1. Clone the Repository
```bash
git clone git@github.com:cptdanko/file-sharing-app.git
cd file-sharing-app
```

### 2. Configure AWS Credentials
```yaml
aws:
  region: <your-region>
  key: <your-key>
  secret: <your-secret>
dynamo-db:
  amazonDBEndpoint: <your-dynamodb-endpoint>
```
### 3. Run Backend (Spring Boot)

```bash
mvn clean install package
mvn spring-boot:run
```
### 4. Run with Docker
```bash
docker compose up
```

### 5. Enable Google Login (Optional)
Create .env in frontend root:

```bash
REACT_APP_GOOGLE_CLIENT_ID=<your-client-id>
```

### 6. Monolithic Setup (Optional)
To run both API + React UI together, edit pom.xml and remove the <pluginsManagement> tag.

## 📚 References to Author’s Blog
This repository is supported by tutorials on My Day To-Do Blog:

  - [Upload to AWS S3 bucket from Java Spring Boot app]

  - [How to add JWT security to Java Spring Boot API]

  - [How to build a Spring Boot API with ReactJS frontend]

  - [How to Catch ExpiredJwtException in Spring OncePerRequestFilter]

  - [Cron expression must consist of 6 fields instead of 5]

Additional tutorials include:

  - [Building a blog engine with Spring Boot + React]

  - [Calling REST APIs with Spring WebClient]

  - [AWS DynamoDB queries with NodeJS/Express & Typescript]

## 🤝 Contributing
This project uses a Git feature-based workflow:

Create an issue
1. create an issue
2. Branch off:

```bash
git branch feature/your-awesome-feature
git checkout feature/your-awesome-feature
```
3. Commit and squash changes
4. Push and open a PR

## ✨ Author
Bhuman Soni

Full Stack & AI Engineer | Blogger | Open Source Contributor

Tutorials & insights: MyDayToDo Blog

GitHub: @cptdanko

[Cron expression must consist of 6 fields instead of 5]: https://mydaytodo.com/cron-expression-must-consist-of-6-fields-found-5-in-45/
[How to add JWT security to Java Spring Boot API]: https://mydaytodo.com/jwt-authentication-java-spring-boot/
[How to Catch ExpiredJwtException in Spring OncePerRequestFilter]: https://mydaytodo.com/catch-expiredjwtexception-spring-onceperrequestfilter/
[How to build a Spring Boot API with ReactJS frontend]: https://mydaytodo.com/spring-boot-api-with-reactjs/
[Upload to AWS S3 bucket from Java Spring Boot app]: https://mydaytodo.com/upload-to-aws-s3-java-spring-boot/
[Building a blog engine with Spring Boot + React]: https://mydaytodo.com/build-blog-engine-with-react-spring-boot-1/
[Calling REST APIs with Spring WebClient]: https://mydaytodo.com/spring-reactive-api-mongodb/
[AWS DynamoDB queries with NodeJS/Express & Typescript]: https://mydaytodo.com/aws-dynamodb-typescript-guide/



