package taskmanager.service;

import taskmanager.model.Status;
import taskmanager.model.Task;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages tasks: add, view, update, delete and set status.
 * All input validation lives here. If a CSV file is given, every change is saved to it.
 *
 * @author Your Name
 */
public class TaskManager {
    private final Map<Integer, Task> tasks = new LinkedHashMap<>();
    private final Path file;
    private int nextId = 1;

    /** Creates an in-memory manager with no file storage (used by the unit tests). */
    public TaskManager() { this(null); }

    /**
     * Creates a manager backed by a CSV file, loading existing tasks if the file exists.
     *
     * @param file the CSV file to load from and save to (null for no storage)
     * @throws UncheckedIOException if the file exists but cannot be read
     */
    public TaskManager(Path file) {
        this.file = file;
        if (file != null && Files.exists(file)) load();
    }

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
        save();
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
        save();
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
        save();
        return true;
    }

    /**
     * Deletes a task.
     *
     * @param id id of the task to delete
     * @return true if deleted, false if no task has this id
     */
    public boolean delete(int id) {
        boolean removed = tasks.remove(id) != null;
        if (removed) save();
        return removed;
    }

    private void checkTitle(String title) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Title cannot be empty.");
    }

    // ---------- CSV storage ----------

    /** Writes all tasks to the CSV file (does nothing when no file is set). */
    private void save() {
        if (file == null) return;
        List<String> lines = new ArrayList<>();
        lines.add("id,title,description,status");
        for (Task t : tasks.values())
            lines.add(t.getId() + "," + quote(t.getTitle()) + "," + quote(t.getDescription())
                    + "," + t.getStatus().name());
        try {
            Files.write(file, lines);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save tasks: " + e.getMessage(), e);
        }
    }

    /** Loads tasks from the CSV file, skipping the header and any malformed line. */
    private void load() {
        try {
            for (String line : Files.readAllLines(file)) {
                try {
                    List<String> f = parse(line);
                    if (f.get(0).equals("id")) continue;
                    Task t = new Task(Integer.parseInt(f.get(0)), f.get(1), f.get(2));
                    t.setStatus(Status.valueOf(f.get(3)));
                    tasks.put(t.getId(), t);
                    nextId = Math.max(nextId, t.getId() + 1);
                } catch (RuntimeException skip) { /* malformed line: ignore */ }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read tasks: " + e.getMessage(), e);
        }
    }

    private static String quote(String s) {
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    /** Splits one CSV line, honoring quoted fields and doubled quotes. */
    private static List<String> parse(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') { sb.append('"'); i++; }
                else quoted = !quoted;
            } else if (c == ',' && !quoted) {
                out.add(sb.toString());
                sb.setLength(0);
            } else sb.append(c);
        }
        out.add(sb.toString());
        return out;
    }
}