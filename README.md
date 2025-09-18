# 🏕️ OutScout - Smart Picnic & Camping Planner

OutScout is a comprehensive camping and outdoor activity planner that helps you find the perfect campsite and plan your trip with real-time weather insights. Built with modern technologies and designed for scalability.

## ✨ Features

- **🔍 Smart Search**: Find campsites by location, amenities, and preferences
- **🌤️ Weather Integration**: Real-time weather forecasts and planning recommendations
- **📱 Modern UI**: Responsive React frontend with TailwindCSS
- **⚡ Real-time Updates**: Server-Sent Events for live trip planning
- **🚨 Park Alerts**: Stay informed about closures and safety information
- **🏗️ Future-Ready**: Clean architecture ready for cloud deployment

## 🏗️ Architecture

### Backend (Spring Boot 3 + Java 17)
- RESTful API with Server-Sent Events
- PostgreSQL database with Flyway migrations
- Optional Redis caching
- Open-Meteo weather API integration
- Comprehensive test coverage

### Frontend (React + TypeScript + Vite)
- Modern React with TypeScript
- TailwindCSS for styling
- Recharts for data visualization
- Real-time SSE integration

## 🚀 Quick Start

### Prerequisites

- **Java 17+**
- **Node.js 18+**
- **PostgreSQL 14+**
- **Maven 3.6+**
- **Redis** (optional, for caching)

### 1. Database Setup

Create the database and user:

```sql
CREATE DATABASE outscout;
CREATE USER outscout WITH PASSWORD 'outscout';
GRANT ALL PRIVILEGES ON DATABASE outscout TO outscout;
```

### 2. Environment Configuration

Copy the environment template:

```bash
cp env.template .env.local
```

Edit `.env.local` with your database credentials:

```bash
# PostgreSQL connection
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=outscout
POSTGRES_USER=outscout
POSTGRES_PASSWORD=outscout

# Optional Redis cache
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_ENABLED=false

# Frontend origin for CORS
FRONTEND_ORIGIN=http://localhost:5173

# Weather API base (Open-Meteo is free)
OPEN_METEO_BASE=https://api.open-meteo.com/v1/forecast
```

### 3. Backend Setup

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend will:
- Run Flyway migrations automatically
- Load seed data on first startup (dev profile)
- Start on `http://localhost:8080`

### 4. Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

The frontend will start on `http://localhost:5173`

### 5. Access the Application

Open your browser and navigate to `http://localhost:5173`

## 📁 Project Structure

```
OutScout/
├── backend/                    # Spring Boot backend
│   ├── src/main/java/com/outscout/api/
│   │   ├── controller/         # REST controllers
│   │   ├── service/           # Business logic
│   │   ├── repo/              # Data repositories
│   │   ├── model/             # Entities and DTOs
│   │   └── config/            # Configuration
│   ├── src/main/resources/
│   │   ├── application.yml    # Main configuration
│   │   ├── application-dev.yml # Development config
│   │   └── db/migration/      # Flyway migrations
│   └── pom.xml               # Maven dependencies
├── frontend/                  # React frontend
│   ├── src/
│   │   ├── pages/            # Main pages
│   │   ├── components/       # Reusable components
│   │   ├── lib/              # API client and utilities
│   │   └── main.tsx          # App entry point
│   ├── package.json          # NPM dependencies
│   └── vite.config.ts        # Vite configuration
├── seed/                     # Sample data
│   ├── parks.csv            # Park information
│   ├── campsites.csv        # Campsite data
│   └── alerts.json          # Park alerts
├── .github/workflows/        # CI/CD pipelines
└── README.md                # This file
```

## 🌐 API Endpoints

### Spots (Campsites)
- `GET /api/spots/search` - Search campsites with filters
- `GET /api/spots/{id}` - Get campsite details

### Planning
- `POST /api/plan` - Create trip plan
- `GET /api/plan/{requestId}` - Get plan result
- `GET /api/plan/stream/{requestId}` - Stream plan updates (SSE)

### Weather
- `GET /api/forecast` - Get weather forecast for location

### Alerts
- `GET /api/alerts` - Get active park alerts
- `GET /api/alerts/park/{parkId}` - Get alerts for specific park

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm run lint
npm run build
```

## 🚀 Deployment

### Development
The application is configured for local development. Both backend and frontend can run independently.

### Production Considerations
- Configure production database
- Set up Redis for caching
- Configure proper CORS origins
- Set up monitoring and logging
- Configure SSL/TLS

## 🔧 Configuration

### Backend Configuration
Key configuration options in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${POSTGRES_HOST:localhost}:${POSTGRES_PORT:5432}/${POSTGRES_DB:outscout}
    username: ${POSTGRES_USER:outscout}
    password: ${POSTGRES_PASSWORD:outscout}

app:
  cors:
    allowed-origins: ${FRONTEND_ORIGIN:http://localhost:5173}
  weather:
    api-base: ${OPEN_METEO_BASE:https://api.open-meteo.com/v1/forecast}
    cache-ttl: 7200
  redis:
    enabled: ${REDIS_ENABLED:false}
```

### Frontend Configuration
Environment variables in `.env.local`:

```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

## 🏗️ Future Enhancements

The architecture is designed to support future enhancements:

- **Docker Containerization**: Ready for container deployment
- **Message Queues**: Kafka integration for async processing
- **Search Engine**: Elasticsearch for advanced search capabilities
- **Cloud Deployment**: AWS/Azure/GCP ready
- **Microservices**: Service boundaries clearly defined
- **Monitoring**: Spring Actuator endpoints for health checks

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Check the documentation
- Review the API endpoints
- Check the GitHub issues
- Contact the development team

---

**OutScout** - Making outdoor adventures smarter and safer! 🏕️✨