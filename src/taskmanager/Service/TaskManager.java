package taskmanager.service;

import taskmanager.model.Status;
import taskmanager.model.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages tasks in memory: add, view, update, delete and set status.
 * All input validation lives here.
 *
 * @author Your Name
 */
public class TaskManager {
    private final Map<Integer, Task> tasks = new LinkedHashMap<>();
    private int nextId = 1;

    /**
     * Adds a new task with status PENDING.
     *
     * @param title       task title (must not be blank)
     * @param description task details (may be empty)
     * @return the created task
     * @throws IllegalArgumentException if the title is null or blank
     */
    public Task add(String title, String description) {
        checkTitle(title);
        Task task = new Task(nextId++, title.trim(), description == null ? "" : description.trim());
        tasks.put(task.getId(), task);
        return task;
    }

    /** @return a copy of all tasks in insertion order (empty list if none) */
    public List<Task> getAll() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Updates an existing task.
     *
     * @param id          id of the task to update
     * @param title       new title (must not be blank)
     * @param description new description
     * @param status      new status
     * @return true if updated, false if no task has this id
     * @throws IllegalArgumentException if the title is null or blank
     */
    public boolean update(int id, String title, String description, Status status) {
        Task task = tasks.get(id);
        if (task == null) return false;
        checkTitle(title);
        task.setTitle(title.trim());
        task.setDescription(description == null ? "" : description.trim());
        task.setStatus(status);
        return true;
    }

    /**
     * Changes only the status of a task.
     *
     * @param id     id of the task
     * @param status new status
     * @return true if updated, false if no task has this id
     */
    public boolean setStatus(int id, Status status) {
        Task task = tasks.get(id);
        if (task == null) return false;
        task.setStatus(status);
        return true;
    }

    /**
     * Deletes a task.
     *
     * @param id id of the task to delete
     * @return true if deleted, false if no task has this id
     */
    public boolean delete(int id) {
        return tasks.remove(id) != null;
    }

    private void checkTitle(String title) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Title cannot be empty.");
    }
}