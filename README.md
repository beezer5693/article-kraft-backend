# Article Kraft

**Article Kraft** is a cutting-edge application designed to help writers enhance their content's SEO performance. By
leveraging OpenAI's GPT-4, the app provides intelligent suggestions to make articles more SEO-friendly.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)

## Features

- **SEO Improvement Suggestions**: Uses GPT-4 to analyze articles and suggest SEO enhancements.
- **User Authentication**: Secure user authentication via OAuth2 and JWT.
- **PDF Storage**: Stores PDF articles using PostgreSQL with pgvector for efficient retrieval.
- **Real-time Updates**: Frontend built with Next.js and TypeScript for fast and responsive user experience.

## Technology Stack

### Frontend

- **Framework**: Next.js
- **Language**: TypeScript

### Backend

- **Framework**: Spring Framework
- **Data Persistence**: Spring Data JPA, PostgreSQL
- **AI Integration**: Spring AI
- **PDF Storage**: pgvector
- **Authentication**: OAuth2, JWT

## Installation

### Prerequisites

- Node.js (v14.x or later)
- Java (JDK 11 or later)
- PostgreSQL
- Maven

### Frontend Setup

1. Clone the repository:
    ```bash
    git clone https://github.com/beezer5693/article-kraft.git
    cd article-kraft/frontend
    ```

2. Install dependencies:
    ```bash
    npm install
    ```

3. Start the development server:
    ```bash
    npm run dev
    ```
   The frontend will be running on `localhost:3000`.

### Backend Setup

1. Clone the repository (if not already done):
    ```bash
    git clone https://github.com/beezer5693/article-kraft-backend.git
    cd article-kraft/backend
    ```

2. Set up PostgreSQL using Docker:
    ```bash
    docker run -itd --name article-kraft-postgres -e POSTGRES_USER=yourusername -e POSTGRES_PASSWORD=yourpassword -e POSTGRES_DB=article_kraft -p 5432:5432 -d postgres
    ```

3. Configure the database in `application.properties`:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/article_kraft
    spring.datasource.username=yourusername
    spring.datasource.password=yourpassword
    ```

4. Build and run the backend:
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
   The backend will be running on `localhost:8080`.

## Usage

1. Visit `http://localhost:3000` in your web browser.
2. Sign up or log in using the authentication system.
3. Upload your article or enter your text directly into the editor.
4. Receive AI-generated suggestions to improve your article's SEO performance.
5. Save and manage your articles in PDF format.

## API Endpoints

- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/register` - User registration
- `GET /api/v1/articles` - Fetch all articles
- `POST /api/v1/articles` - Create a new article
- `GET /api/v1/articles/{id}` - Fetch a specific article
- `PUT /api/v1/articles/{id}` - Update an article
- `DELETE /api/v1/articles/{id}` - Delete an article
- `POST /api/v1/seo/suggestions` - Get SEO improvement suggestions

## Authentication

Authentication is handled via OAuth2 and JWT. Ensure you have the correct OAuth2 provider configurations in
your `application.properties`:

```properties
spring.security.oauth2.client.registration.google.client-id=your-client-id
spring.security.oauth2.client.registration.google.client-secret=your-client-secret
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/google
```