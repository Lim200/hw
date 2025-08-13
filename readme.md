Project Engagement Tracker
📦 Overview
This is a RESTful application designed to track employee engagement in project activities. Built with Spring Boot, it follows a layered architecture pattern and leverages modern tools and frameworks such as Docker for containerization, Swagger for API documentation, Flyway for database migrations, and includes comprehensive automated tests with code coverage reports via JaCoCo.

The application allows managing departments, participations, users, and retrieving statistics about workloads and engagement on specific dates.

🚀 Running the Application
To build and run the application along with its dependencies (e.g., database), use Docker Compose:
<pre>
docker-compose up --build
</pre>
The application will start and be accessible at <pre>http://localhost:8080</pre>

🛠 API Usage Examples
Here are some example REST API requests you can test (replace dates and names accordingly):

Get overall statistics for a specific date:
<pre>
http://localho:8080/api/statistics?date=2025-08-09
</pre>
Get user-specific statistics by date and full name:
<pre>
GET http://localhost:8080/api/user-statistics?date=2025-08-09&fullName=Иванов Алексей Сергеевич
</pre>
Get employee workload for a specific date:
<pre>
GET http://localhost:8080/api/employee-load?date=2025-08-09
</pre>
Get department workload by date and department name:
<pre>
GET http://localhost:8080/api/department-load?date=2025-08-09&department=Отдел разработки
</pre>
📖 API Documentation with Swagger
Interactive API documentation is available via Swagger UI. You can explore all endpoints, their parameters, request/response models, and even execute API calls directly from the browser.

Access Swagger UI at:
<pre>
http://localhost:8080/swagger-ui/index.html
</pre>
✅ Running Tests and Viewing Coverage
The project contains unit and integration tests to ensure reliability and correctness.

To run the tests locally:
<pre>
./mvnw test
</pre>
The project uses JaCoCo to generate code coverage reports. After tests finish, open the coverage report:
target/site/jacoco/index.html

Open this file in your browser to see detailed insights into which parts of your code are covered by tests.


Saving the Docker Image for Transfer or Backup
To save the Docker image as a .tar file — for transferring it to another machine or keeping it as a backup — use the following command:
<pre>
docker save -o project-engagement-app.tar project-engagement-app:latest
</pre>
This will create a file named project-engagement-app.tar containing the image project-engagement-app:latest, which you can then copy or share as needed.

📦 Running the Application from a Prebuilt Docker Image
If you're setting up the application on another machine and already have the Docker image (project-engagement-app.tar), follow these steps:

1. Load the image
docker load -i project-engagement-app.tar
2. Prepare the environment
To run the application using docker-compose, make sure the following conditions are met:

Place the docker-compose.yml file in the same directory as the .tar image file

In the docker-compose.yml, remove the build: section

Use the image: directive to reference the prebuilt image

Here is the recommended docker-compose.yml configuration:

<pre>
yaml
services:
  postgres:
    image: postgres:15
    container_name: postgres
    environment:
      POSTGRES_DB: projectdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: root
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  app:
    image: project-engagement-app:latest  # ✅ Using the prebuilt image
    container_name: project-app
    depends_on:
      - postgres
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/projectdb
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: root

volumes:
  pgdata:
</pre>
3. Run the application
Once the docker-compose.yml file is in place, start the application:
docker-compose up
This will launch both the PostgreSQL database and the application container using the prebuilt image.