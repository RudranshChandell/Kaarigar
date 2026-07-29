# 🛠️ Kaarigar (कारिगर) - Service Provider Marketplace

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen.svg?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.1-blue.svg?style=flat-square&logo=react)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-7.1-purple.svg?style=flat-square&logo=vite)](https://vite.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-4.0-38B2AC.svg?style=flat-square&logo=tailwindcss)](https://tailwindcss.com/)
[![Database](https://img.shields.io/badge/Database-MySQL-blue.svg?style=flat-square&logo=mysql)](https://www.mysql.com/)

**Kaarigar** (Hindi/Urdu for *Artisan* or *Skilled Craftsman*) is a comprehensive, full-stack service marketplace platform that bridges the gap between clients needing local services and verified service providers (plumbers, electricians, painters, carpenters, tutors, developers, etc.). 

The platform offers robust role-based functionality, interactive maps, voice search capability, multi-language localization, real-time messaging using WebSockets, admin verification of documents, and an analytical dashboard.

---

## 🚀 Core Features

### 👥 Customer Capabilities
*   **Search & Filters:** Search for services by location, category, subcategory, and price range.
*   **Voice Search:** Hands-free accessibility using speech recognition (`react-speech-recognition`) to search services using voice commands.
*   **Interactive Maps:** Visual location mapping using Leaflet and Google Maps API. Providers are displayed on the map with clustering for easy local lookup.
*   **Provider Profile & Detail View:** Deep-dive into provider profiles, view credentials, pricing, average ratings, and previous user feedback.
*   **Booking Lifecycle:** Book a service provider, monitor status, and mark bookings as complete. Generate PDF invoices using JSPdf.
*   **Feedback & Reviews:** Rate service providers (1-5 stars) and write comprehensive reviews.
*   **Real-time Chat:** Direct messaging with providers using WebSockets (STOMP) to discuss job details.

### 💼 Service Provider Capabilities
*   **Quick Registration:** Seamless onboarding with initial service details creation (category, hourly rate, location).
*   **Document Verification (KYC):** Upload ID proofs or certifications for admin verification. Access is limited until documents are approved.
*   **Catalog Management:** Add, edit, or delete provided services, rates, availability windows, and service locations.
*   **Booking Management:** Live tracking of customer bookings with options to change status (Accept, Decline, Complete).
*   **Review Replies:** Respond directly to client reviews on service details.

### 🛡️ Admin Capabilities
*   **Analytical Dashboard:** Interactive charts (using Recharts) representing monthly bookings, top-performing service categories, highly-rated providers, and regional trends.
*   **Document Verification Portal:** Review, approve, or reject provider-submitted verification documents with custom rejection feedback.
*   **User Management:** Audit bookings, reviews, reports, and logs to ensure marketplace safety.

---

## 🛠️ Technical Stack

### Backend
*   **Core:** [Java 21](https://openjdk.org/), [Spring Boot 3.5.x](https://spring.io/projects/spring-boot)
*   **Security:** Spring Security with stateless JWT (JSON Web Tokens) Authentication
*   **Data Access:** Spring Data JPA, Hibernate ORM
*   **Database:** MySQL (relational storage for users, service records, chat messages, and reports)
*   **Real-time Communication:** Spring WebSockets, STOMP messaging protocol, SockJS client compatibility

### Frontend
*   **Core:** React 19, Vite (Fast build tool & dev server), React Router Dom (v7)
*   **Styling:** Tailwind CSS (v4) with Custom CSS animations and Framer Motion for micro-interactions
*   **Maps & Geolocation:** React Leaflet, Leaflet Marker Cluster, and Google Maps integration
*   **Data Visualization:** Recharts (responsive analytics charts)
*   **Localization (i18n):** `i18next` for seamless language switches (supports English, Hindi, and more)
*   **Voice Capability:** Speech recognition for voice search
*   **PDF Generation:** `jspdf` & `jspdf-autotable` for downloadable booking invoice creation

---

## 📂 Project Architecture

The codebase is organized into two primary micro-services/folders:

*   [Backend Codebase](file:///d:/Github/Kaarigar/backend) - Spring Boot Java Application
*   [Frontend Codebase](file:///d:/Github/Kaarigar/frontend) - React SPA (Vite)

### Directory Structure

```
Kaarigar/
├── backend/                  # Spring Boot Java Application
│   ├── src/main/java/        # Java Source files
│   │   └── kaarigar/backend/
│   │       ├── config/       # Spring Configuration (Security, WebSockets, CORS)
│   │       ├── controller/   # REST Controllers (Auth, Bookings, Messages, etc.)
│   │       ├── dto/          # Data Transfer Objects for API Requests/Responses
│   │       ├── enums/        # System Enums (Roles, BookingStatus)
│   │       ├── model/        # JPA Entities (User, Booking, Message, Document, Review)
│   │       ├── repository/   # Spring Data JPA Repository Interfaces
│   │       └── service/      # Business Logic Implementations
│   └── src/main/resources/   # Application properties & Seed configuration
└── frontend/                 # React SPA (Vite)
    ├── src/
    │   ├── components/       # Reusable components (Service Cards, Chat Widgets, UI Elements)
    │   ├── context/          # Auth Context for managing global logged-in states
    │   ├── hooks/            # Custom React Hooks
    │   ├── locales/          # Translation JSON files (en/hi) for i18n
    │   ├── pages/            # View Pages (Auth, Booking Details, Dashboards, Home)
    │   ├── services/         # Axios API HTTP and WebSocket Clients
    │   └── utils/            # Helper functions
    └── package.json          # Frontend dependencies & npm scripts
```

---

## ⚙️ Setup & Installation

### Prerequisites
*   **Java Development Kit (JDK) 21** or higher
*   **Node.js** (v18+) & **npm**
*   **MySQL Server** running locally or on a cloud instance

---

### 1. Backend Configuration & Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Create a `.env` file based on `.env.example`:
   ```bash
   cp .env.example .env
   ```

3. Open `.env` and configure your database credentials and secret keys:
   ```properties
   DB_URL=jdbc:mysql://localhost:3306/FixItNow?createDatabaseIfNotExist=true
   DB_USERNAME=your_mysql_username
   DB_PASSWORD=your_mysql_password
   JWT_SECRET=your_32_character_jwt_secret_key
   ADMIN_PASSWORD=your_admin_dashboard_login_password
   SAMPLE_DATA_PASSWORD=your_default_seeded_accounts_password
   ```

4. Run the Spring Boot application using Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
   *The backend server will start running on port `8081` by default.*

---

### 2. Frontend Configuration & Setup

1. Navigate to the frontend directory:
   ```bash
   cd ../frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the Vite local development server:
   ```bash
   npm run dev
   ```
   *Open [http://localhost:5173](http://localhost:5173) in your web browser to access the application.*

---

## 🗺️ API Endpoints Reference

### 🔐 Authentication (`/api/auth`)
*   `POST /api/auth/register` - Register a Customer or Service Provider.
*   `POST /api/auth/login` - Authenticate users and return JWT.
*   `POST /api/auth/upload-documents/{providerId}` - Upload verification documents.

### 📅 Booking Management (`/api/bookings`)
*   `POST /api/bookings/create` - Book a service provider (Customers only).
*   `GET /api/bookings/customer/{customerId}` - Get booking history for a customer.
*   `GET /api/bookings/provider/{providerId}` - Get assigned bookings for a provider.
*   `PUT /api/bookings/updateStatus/{bookingId}` - Update status (Accept/Decline/Cancel).
*   `POST /api/bookings/{bookingId}/markComplete` - Request completion status (Providers only).
*   `POST /api/bookings/{bookingId}/verify` - Confirm job completion (Customers only).
*   `GET /api/bookings/{bookingId}` - Fetch single booking details.

### 💬 Chat & Messages (`/api/messages`)
*   `POST /api/messages` - Send a REST message.
*   `GET /api/messages/between/{userId}` - Retrieve historical messages with a specific contact.
*   `GET /api/messages/contacts` - Fetch unique active chat list/contacts.

#### 🔌 WebSocket Protocol
*   **WS Endpoint URL:** `ws://localhost:8081/ws`
*   **Destination Subscriptions:** `/topic/messages` (Global notification channels)
*   **App Prefix Sending:** `/app/chat` (Broker redirects messages to designated users in real-time)

### 🛠️ Services catalog (`/api/services`)
*   `POST /api/services` - Create a service listing.
*   `GET /api/services` - Fetch all active service listings.
*   `GET /api/services/provider/{providerId}` - Fetch specific services provided by an artisan.
*   `PUT /api/services/{id}` - Modify details/rates of a service.
*   `DELETE /api/services/{id}` - Remove a service from the marketplace.

### 📂 Provider Documents Verification (`/api/documents`)
*   `POST /api/documents/upload/{providerId}` - Upload file attachments.
*   `GET /api/documents/provider/{providerId}` - List uploaded documents of a provider.
*   `GET /api/documents/all` - List all uploaded documents across the platform (Admin/Provider).
*   `PUT /api/documents/approve/{id}` - Approve provider credentials (Admin only).
*   `PUT /api/documents/reject/{id}` - Reject credentials with comments (Admin only).

### ⭐ Reviews & Ratings (`/api/reviews`)
*   `POST /api/reviews/add` - Review and rate a booking (Customers only).
*   `GET /api/reviews/provider/{providerId}` - View reviews of a provider.
*   `GET /api/reviews/provider/{providerId}/average` - Fetch numerical average rating.
*   `PUT /api/reviews/reply/{reviewId}` - Add response comments to feedback (Providers only).

### 📊 Admin Analytics (`/api/admin/analytics`)
*   `GET /api/admin/analytics/summary` - General platform metadata (counts of users, bookings, total platform earnings).
*   `GET /api/admin/analytics/bookings/monthly` - Monthly volume analytics for line charts.
*   `GET /api/admin/analytics/top-providers` - Leaderboard of best providers.
*   `GET /api/admin/analytics/top-services` - Leaderboard of popular categories.
*   `GET /api/admin/analytics/locations` - Distribution maps statistics.

---

## 🤝 Contribution Guidelines
Contributions make the open-source community an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project.
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the Branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

## 📄 License
Distributed under the MIT License. See `LICENSE` for more information.
