# UI Search App

A React application for searching with a backend API.

## Getting Started

This project uses Docker to manage development and production environments. You can run the application in either environment using the provided `docker-compose` files.

### Prerequisites

- [Docker](https://www.docker.com/get-started) installed on your machine.
- [Docker Compose](https://docs.docker.com/compose/install/) (usually included with Docker Desktop).

### Project Structure

- `Dockerfile`: Defines the build process for the application.
- `docker-compose.development.yml`: Configuration for the development environment.
- `docker-compose.production.yml`: Configuration for the production environment.
- `.env.development`: Contains environment variables for the development environment.
- `build/`: The output directory for the production build of the React app.

### Environment Variables

- **Development:**
  - Create a `.env.development` file with the following variable:
    ```plaintext
    REACT_APP_API_URL=http://search-service:8083
    ```

### Running the Application

#### Development Environment

To start the development environment, use the following command:

```bash
docker-compose -f docker-compose.development.yml up --build
This will:

Build the application.
Start the app and Nginx servers.
Watch for file changes to allow live editing.
You can access the application at http://localhost:3000.

Production Environment
To start the production environment, use the following command:

bash

docker-compose -f docker-compose.production.yml up --build
This will:

Build the application for production.
Serve the built application using Nginx.
You can access the production application at http://localhost:3000.

Project Structure
.
├── Dockerfile
├── docker-compose.development.yml
├── docker-compose.production.yml
├── .env.development
├── build/
└── src/
