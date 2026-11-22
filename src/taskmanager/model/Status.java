package taskmanager.model;

/**
 * Possible states of a task.
 *
 * @author Your Name
 */
public enum Status {
    PENDING("Pending"), IN_PROGRESS("In Progress"), COMPLETED("Completed");

    private final String label;

    Status(String label) { this.label = label; }

    /** @return the human-readable label, also shown in the GUI combo box */
    @Override
    public String toString() { return label; }
}