# 🏥 Smart Health Consulting App

> A comprehensive Java desktop application for managing healthcare operations, connecting patients with doctors, and tracking vital health metrics — built with Java Swing, Maven, and MySQL.

![Version](https://img.shields.io/badge/version-1.0-blue)
![Java](https://img.shields.io/badge/Java-Swing-orange)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue)
![Maven](https://img.shields.io/badge/Build-Maven-purple)

---

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [System Architecture](#-system-architecture)
- [Tech Stack](#-tech-stack)
- [Core Modules](#-core-modules)
- [Database Design](#-database-design)
- [Setup & Installation](#-setup--installation)

---

## 📌 Project Overview

The **Smart Health Consulting App** is a centralized GUI application that bridges the gap between healthcare providers and patients. It replaces fragmented paper-based records with a unified, role-driven platform offering appointment scheduling, virtual consultation tracking, automated invoicing, and visual health analytics.

### Key Features
- 🔐 **Role-Based Access Control** (RBAC) separating Patient and Doctor workflows
- 📅 **Dynamic Appointment Scheduling** with double-booking prevention
- 📈 **Visual Health Analytics** using real-time charts (JFreeChart) for vitals and sleep tracking
- 📄 **Automated PDF Generation** for prescriptions and invoices (iTextPDF)
- 💊 **Medication Tracking** with automated reminders and frequency logging
- 💻 **Video Consultation Mockups** for virtual patient-doctor sessions

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────┐
│                    CLIENT GUI                       │
│        Java Swing (JFrame, JPanel, JTable)          │
└────────────────────────┬────────────────────────────┘
                         │ Event Listeners
┌────────────────────────▼────────────────────────────┐
│                  CONTROLLER LAYER                   │
│   Event Dispatch Thread (PatientModule, MainApp)    │
└────────────────────────┬────────────────────────────┘
                         │ 
┌────────────────────────▼────────────────────────────┐
│                   SERVICE LAYER                     │
│    PDFGenerator | ChartFactory | Authentication     │
└────────────────────────┬────────────────────────────┘
                         │ JDBC / Config Properties
┌────────────────────────▼────────────────────────────┐
│                  DATABASE LAYER                     │
│          MySQL 8.0 (smarthealth database)           │
└─────────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Frontend GUI | Java Swing, AWT |
| Backend Core | Java 21 |
| Database | MySQL 8.0 |
| Dependency Management | Maven |
| Document Generation | iTextPDF |
| Data Visualization | JFreeChart |
| Configuration | Properties Files (ignored in Git) |

---

## 📦 Core Modules

### 1. 👤 Authentication & Role Management
- Secure user registration and login functionality.
- Dynamically routes users to either the **Patient Dashboard** or **Doctor Dashboard** based on their designated role.

### 2. 👨‍⚕️ Patient Module
- **Health Tracker:** Input daily vitals (Blood Pressure, Sugar Level, SpO2) and visualize trends via auto-generated charts.
- **Medication Tracker:** Keep an active log of prescribed medicines, dosages, and duration.
- **Appointments:** Book, view, and manage upcoming physical or virtual appointments with registered doctors.

### 3. 🩺 Doctor Module
- **Consultation Management:** View assigned patients and manage upcoming video consultation links.
- **Patient History:** Review patient medical history, allergies, and tracked vitals prior to consultation.

### 4. 📄 Automated Document Engine
- Uses `iTextPDF` to instantly generate branded PDFs for Invoices and Prescriptions.
- Automatically organizes output into a local `documents/` directory.

---

## 🗄️ Database Design

The relational database is optimized into **9 active tables** to ensure zero data redundancy:
- `signup` / `patient` / `doctor` - Core user identities and profiles
- `book_appointment` / `consultation` / `payment` - Operational tracking
- `health_tracker` / `medication_tracker` / `sleeptracker` - Health data warehousing

---

## 🚀 Setup & Installation

### Prerequisites
- **Java Development Kit (JDK) 17+** (Project target is Java 21)
- **MySQL Server 8.0+**
- (Optional) Maven if not using the included wrapper.

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Maxyn01/Smart-Health-Consulting.git
   cd Smart-Health-Consulting
   ```

2. **Database Setup**
   - Create a local MySQL database named `smarthealth`.
   - Import your SQL schema into the database.

3. **Configure Database Credentials**
   - Navigate to `src/main/resources/`
   - Copy `config.properties.example` and rename it to `config.properties`.
   - Update it with your actual MySQL credentials:
     ```properties
     db.url=jdbc:mysql://localhost:3306/smarthealth
     db.user=root
     db.password=YOUR_PASSWORD_HERE
     ```

4. **Run the Application**
   Use the included Maven wrapper to seamlessly compile and run the application without installing Maven globally:
   ```bash
   # Windows
   .\mvnw.cmd clean compile exec:java
   
   # Mac/Linux
   ./mvnw clean compile exec:java
   ```
