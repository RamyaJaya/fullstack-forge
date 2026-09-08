# FullStack Forge 🛠️

Welcome to **FullStack Forge**, a centralized monorepo showcasing a collection of production-grade, full-stack applications. This repository serves as an engineering playground and portfolio, focusing on robust backend architectures integrated with modern, high-performance user interfaces.

The primary objective of this repository is to demonstrate clean code principles, scalable system design patterns, and fluid user experiences across various real-world domains.

---

## 🏗️ The Tech Stack

Every application within this forge bridges enterprise-grade backend stability with modern frontend performance:

*   **Backend:** Java (Spring Boot) — Leveraging strong typing, multi-threading, structured APIs, and secure relational database mapping.
*   **Frontend:** React & Next.js — Utilizing server-side rendering (SSR), dynamic routing, optimized assets, and responsive UI components.
*   **AI Pair Programming:** Accelerated and optimized using **Cursor**.

---

## 🚀 Active Projects

### 1. TaskOrchestrator 📋
*A dynamic project management board featuring automated background timers and task tracking.*
*   **Backend Features:** Multi-user authentication, PostgreSQL database mapping, and background cron jobs for deadline alerts.
*   **Frontend Features:** Drag-and-drop task boards, dynamic project workspaces, and real-time status updates.
*   **Status:** 🟡 *In Development*

### 2. PulseFin 📈 *(Planned)*
*A high-frequency personal finance and market tracking dashboard.*
*   **Backend Features:** External API aggregation, data caching, and optimized JSON data streams.
*   **Frontend Features:** Interactive charts (Recharts/Chart.js), responsive financial ledgers, and dark-mode optimization.
*   **Status:** ⚪ *Planned*

---

## 📁 Repository Structure

```text
fullstack-forge/
├── README.md                 <-- You are here
├── .gitignore                <-- Global ignores (node_modules, target, .env)
├── [project-name]/
│   ├── backend/             <-- Java / Spring Boot application
│   └── frontend/            <-- React / Next.js application
```

---

## 🛠️ How to Run Projects Locally

Each project contains its own localized `README.md` inside its respective folder detailing specific database migrations and environment variables. However, the generic workflow is:

1. **Clone the repository:**
   ```bash
   git clone https://github.com
   cd fullstack-forge
   ```
2. **Spin up the Backend:** Navigate to the backend directory, configure your database in `application.properties`, and run the Java application.
3. **Spin up the Frontend:** Navigate to the frontend directory, install dependencies (`npm install`), and start the development server (`npm run dev`).
