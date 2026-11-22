package taskmanager.model;

/**
 * A single to-do item with a title, description and status.
 *
 * @author Your Name
 */
public class Task {
    private final int id;
    private String title;
    private String description;
    private Status status = Status.PENDING;

    /**
     * Creates a task with status PENDING.
     *
     * @param id          unique task id
     * @param title       task title
     * @param description task details
     */
    public Task(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    /** @return the unique id */
    public int getId() { return id; }

    /** @return the title */
    public String getTitle() { return title; }

    /** @param title the new title */
    public void setTitle(String title) { this.title = title; }

    /** @return the description */
    public String getDescription() { return description; }

    /** @param description the new description */
    public void setDescription(String description) { this.description = description; }

    /** @return the current status */
    public Status getStatus() { return status; }

    /** @param status the new status */
    public void setStatus(Status status) { this.status = status; }
}