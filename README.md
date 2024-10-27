# Project Overview

This project is a microservices-based application that utilizes Spring Boot, RabbitMQ, and Kafka for managing projects and scenarios. It includes various services for creating, updating, and deleting projects and scenarios, as well as handling events related to these entities.

## Technologies Used

- **Spring Boot**: Framework for building the application.
- **RabbitMQ**: Message broker for handling asynchronous communication.
- **Kafka**: Stream processing platform for handling real-time data feeds.
- **Redis**: In-memory data structure store, used as a database, cache, and message broker.
- **JUnit**: Testing framework for unit and integration tests.
- **Jackson**: Library for processing JSON data.

## Project Structure

- **src/main/java/com/geolite/projects**: Contains the main application code for the project service.
- **src/main/java/com/geolite/scenarios**: Contains the main application code for the scenario service.
- **src/main/java/com/geolite/drilling**: Contains the main application code for the drilling service.
- **src/test/java/com/geolite/projects**: Contains integration tests for the project service.
- **src/test/java/com/geolite/scenarios**: Contains integration tests for the scenario service.

## Configuration

### RabbitMQ Configuration

The application configures several queues for handling project and scenario events:

- `create-project-queue`
- `update-project-queue`
- `delete-project-queue`
- `create-scenario-queue`
- `update-scenario-queue`
- `delete-scenario-queue`

### Kafka Configuration

The application uses Kafka for event streaming. It listens to the following topics:

- `project-created`
- `project-updated`
- `project-deleted`

### Redis Configuration

Redis is used for caching and storing temporary data.

## Running the Application

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd <repository-directory>
   ```

2. **Build the project**:
   ```bash
   ./mvnw clean install
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Start RabbitMQ and Kafka**: Ensure that RabbitMQ and Kafka are running. You can use Docker to run these services easily.

## Testing

The project includes integration tests that can be run using:

 ```bash
   ./mvnw test
   ```

### Example Tests

- **Creating a Project**: Tests the creation of a project through the REST API.
- **Updating a Project**: Tests updating an existing project.
- **Deleting a Project**: Tests deleting a project.
- **Creating a Scenario**: Tests the creation of a scenario linked to a project.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

## License

This project is licensed under the MIT License. See the LICENSE file for more details.