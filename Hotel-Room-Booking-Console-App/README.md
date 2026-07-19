# 🏨 StayEase PMS - Premium Hotel Room Booking Dashboard

[![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/HTML)
[![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/CSS)
[![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)](https://developer.mozilla.org/en-US/docs/Web/JavaScript)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](https://opensource.org/licenses/MIT)

A lightweight, serverless Property Management System (PMS) client designed to streamline hotel room booking, guest registration, billing, and cancellations. Built as a single-page dashboard with a premium slate-dark glassmorphism theme, this client-side web application features interactive SVG metrics, robust date-conflict checks, and persistent browser storage.

---

## 📖 Table of Contents
* [Introduction](#-introduction)
* [Features](#-features)
* [Technology Stack](#-technology-stack)
* [File & Directory Structure](#-file--directory-structure)
* [Booking Flow & Storage Layout](#-booking-flow--storage-layout)
* [Running the Application](#-running-the-application)
* [Learning Outcomes](#-learning-outcomes)
* [Author](#-author)

---

## 🚀 Introduction

Managing hotel rooms, reservations, and customer billing via server-dependent backends can sometimes be over-engineered for boutique setups. **StayEase PMS Web Dashboard** resolves this by providing a zero-install, serverless front-end manager that runs fully inside any modern web browser. 

Designed with modern slate-dark styling and smooth micro-animations, it allows users to search for available rooms, book single/double/deluxe/suite inventories, check in/out guests, generate detailed invoices, and monitor operational occupancy stats dynamically.

---

## 🌟 Features

* **Slate-Dark Glassmorphism Interface**: Designed with custom CSS variables, fine border blurs, and responsive grid layouts.
* **Interactive SVG Analytics**: Visual widgets calculating total revenue, occupancy rate, checkout counters, and inventory ratios in real-time.
* **Overlapping Collision Validation**: A client-side overlap checker that blocks duplicate bookings on identical room numbers for overlapping date periods:
  $$\text{Overlap} = (\text{NewCheckIn} < \text{ExistingCheckOut}) \land (\text{NewCheckOut} > \text{ExistingCheckIn})$$
* **Persistent LocalStorage State**: Full data persistence across browser restarts using local JSON serialization.
* **Calculated Billing & Invoices**: Auto-calculates room charges based on night multipliers, includes a standard 10% tax line, and renders printable invoices.
* **Full Room Inventory Management**: Pre-configured with single, double, deluxe, and suite rooms distributed across multiple floors.

---

## 🛠️ Technology Stack

* **Frontend UI Core**: HTML5 Semantic markup.
* **Styling (CSS)**: Custom CSS3 utilizing HSL color tokens, Flexbox/Grid systems, media queries, and backdrop filter blurs.
* **State Logic Engine**: Vanilla JavaScript (ES6) for DOM manipulation, booking validation algorithms, and local storage ledger mapping.

---

## 📁 File & Directory Structure

```directory
Hotel-Room-Booking-Console-App/
├── web/                     # Web Dashboard Application Source
│   ├── index.html           # Main dashboard markup structure
│   ├── style.css            # HSL typography tokens & glassmorphic layout
│   └── app.js               # State engine, validation logic, & LocalStorage handler
└── README.md                # Project documentation and guide
```

---

## 📊 Booking Flow & Storage Layout

### Application Logic Flow
```text
  [User Enters Room Category & Dates]
                   │
                   ▼
  [Verify Overlaps Against Current Ledger] ──(Collision)──> [Show Error Alert]
                   │
              (No Conflict)
                   ▼
     [Select Room & Guest Details]
                   │
                   ▼
    [Calculate Nights & Add 10% Tax]
                   │
                   ▼
 [Write to LocalStorage & Render Invoice] ──> [Refresh Interactive SVG Charts]
```

### Storage Schema
The application uses the browser's `LocalStorage` database to maintain transactional records:

| Key | Type | Description | Sample Structure |
| :--- | :--- | :--- | :--- |
| `grand_horizon_bookings` | `JSON String` | Array containing reservations, billing totals, dates, and check-in statuses. | `[{"bookingId": "BK-10001", "guestName": "Jane Doe", "roomNumber": "101", ...}]` |

---

## 💻 Running the Application

### 1. Launching the Web Dashboard
No compilation, node installations, or servers are needed:
1. Navigate to the `/web` folder in the project workspace.
2. Double-click the [index.html](file:///c:/Users/PC/OneDrive/Desktop/Online%20Complaint%20Management%20System%20using%20Java/Hotel-Room-Booking-Console-App/web/index.html) file to open it in your browser of choice.
3. You can also drag-and-drop the file into a running tab on Chrome, Edge, Firefox, or Safari.

---

## 🎓 Learning Outcomes

* **State Persistence Design**: Mastered browser-based serialization methods for maintaining persistent databases offline.
* **Collision Detection Algorithms**: Implemented strict range validations to control concurrency conflicts in reservation states.
* **Responsive Visual Frameworks**: Designed multi-resolution responsive grid structures from scratch without relying on visual frameworks like Tailwind.

---

## 👤 Author

* **Harshal Navale** - [@HarshalNavale45](https://github.com/HarshalNavale45)
