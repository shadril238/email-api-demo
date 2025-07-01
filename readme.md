# 📧 Email API Demo

The Email API is designed to send both single and bulk emails via REST endpoints, supporting attachments and inline HTML content. It ensures reliable and flexible email delivery by using RabbitMQ to queue outgoing email requests, which are then consumed and sent asynchronously by a dedicated consumer. The system includes support for retry mechanisms, a Dead Letter Queue (DLQ) for failed deliveries, and a scheduling queue for delayed or time-based email dispatch. Additionally, an annotation-driven logging system is implemented using Aspect-Oriented Programming (AOP) with @LogEmail, which intercepts email-sending methods, encrypts metadata using AES/GCM, and logs the details asynchronously to the database in batches. This architecture separates responsibilities cleanly, enabling scalable, fault-tolerant email processing alongside secure, non-blocking activity logging.


---

## ✨ Features
- **RabbitMQ Message Queue**: Guarantees decoupled, fault-tolerant, and scalable mail sending.
- **Annotation-Driven Logging**: Use `@LogEmail` to trigger logging logic automatically.
- **AES Encryption (AES/GCM/NoPadding)**: Ensures sensitive data such as subject and content remain confidential.
- **Batch Database Writes**: Logs are flushed periodically from queue to DB for performance.
- **Custom Spring Events**: Internal decoupling of logging workflow from business logic.
- **Swagger UI Integration**: API documentation for interaction and testing.
- **Basic Authentication Support**: Secures APIs using `@RequireBasicAuth`. (Just for testing purpose)

---

## 🧱 Technologies Used
- **Spring Boot** – Application framework.
- **RabbitMQ** – Asynchronous message broker.
- **Spring Data JPA** – ORM layer for DB interaction.
- **AspectJ (AOP)** – Intercept method calls for logging.
- **Spring AMQP** – Messaging abstraction over RabbitMQ.
- **Spring Events** – Event-based decoupling within the system.
- **Javax Crypto** – AES/GCM encryption library.
- **Springdoc OpenAPI** – Swagger UI integration.






