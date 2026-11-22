# Task Management System

A Java Swing to-do application with a dark UI. Each task has a title, description and status (Pending / In Progress / Completed). Tasks are saved to a CSV file.

Built for the Open-Ended Lab (OEL): Task Management System using modern software construction tools (JDK, IDE, Git, GitHub, Javadoc).

## Features
- Add, view, update and delete tasks
- Set status: Pending / In Progress / Completed
- Input validation (no empty titles) with error dialogs
- Empty-list message and graceful handling of missing tasks
- Automatic CSV storage (`tasks.csv`), loaded on startup
- JUnit 5 unit tests

## Tech Stack
Java 25 (LTS), Swing, JUnit 5, Git/GitHub, Javadoc, IntelliJ IDEA

## Project Structure
```
src/taskmanager/
├── Main.java            entry point
├── model/               Task, Status
├── service/             TaskManager (CRUD, validation, CSV storage)
└── ui/                  MainFrame (Swing GUI)
test/taskmanager/        TaskManagerTest
docs/                    generated Javadoc (open docs/index.html)
screenshots/             application and Git screenshots
report/                  OEL report (PDF)
```

## How to Run
1. Clone the repo: `git clone https://github.com/<username>/task-management-system.git`
2. Open the folder in IntelliJ IDEA (JDK 17+).
3. Run `src/taskmanager/Main.java`.

