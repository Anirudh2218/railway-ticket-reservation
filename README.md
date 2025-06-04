# 🚆 Railway Ticket Reservation System

A full-stack **Java Spring Boot** application with a web frontend (Thymeleaf) and backend powered by an **H2 in-memory database**. This project simulates a basic railway ticket reservation system.

---

## 📌 Features

- ✅ Book railway tickets
- ✅ View all reservations
- ✅ Cancel tickets
- ✅ Responsive frontend using Thymeleaf
- ✅ H2 in-memory DB (auto-reset on restart)
- ✅ Hibernate + JPA for ORM
- ✅ Logging of all SQL queries

---

## 🛠️ Tech Stack

| Layer      | Technology             |
|------------|------------------------|
| Backend    | Spring Boot (Java)     |
| Frontend   | Thymeleaf + HTML/CSS   |
| Database   | H2 (in-memory)         |
| Build Tool | Gradle (Groovy DSL)    |
| ORM        | Hibernate / JPA        |

---

## 🗂️ Project Structure

```
ticket-reservation/
├── src/
│ ├── main/
│ │ ├── java/ → config, controllers, dto, event, exception, model, repository, service
│ │ ├── resources/
│ │ │ ├── templates/ → HTML files (Thymeleaf)
│ │ │ └── application.properties
│ ├── test/
│ │ ├── java/ → controller, service
├── build.gradle
├── .gitignore
├── README.md
```

# ⚙️ How to Run the Project

## ▶️ Prerequisites
- Java 17+  
- Gradle (or use `./gradlew`)  
- IDE like IntelliJ or VS Code  

___


## ▶️ Steps

### **Step 1:** Clone the repo

**git clone https://github.com/Anirudh2218/railway-ticket-reservation.git
cd railway-ticket-reservation**

### Step 2: Run the app

**./gradlew bootRun**

___

## **▶️ Access**

#### Web App: http://localhost:8080

#### H2 Console: http://localhost:8080/h2-console

#### JDBC URL: jdbc:h2:mem:railwaydb

#### Username: sa

#### Password: (leave blank)

___

## 👨‍💻 Developed By

### Anirudh
### Sourabh
### Divya
### Vaishnavi

___

## 2nd Year CSE - Java Project
___