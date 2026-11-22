package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.model.Status;
import taskmanager.service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests mirroring the OEL test-case table.
 *
 * @author Your Name
 */
class TaskManagerTest {
    private TaskManager manager;

    @BeforeEach
    void setUp() { manager = new TaskManager(); }

    /** Add a valid task -> task is added. */
    @Test
    void addValidTask() {
        var task = manager.add("Buy milk", "2 litres");
        assertEquals(1, manager.getAll().size());
        assertEquals("Buy milk", task.getTitle());
        assertEquals(Status.PENDING, task.getStatus());
    }

    /** Add a task with an empty title -> validation error. */
    @Test
    void addEmptyTitleRejected() {
        assertThrows(IllegalArgumentException.class, () -> manager.add("", "x"));
        assertThrows(IllegalArgumentException.class, () -> manager.add("   ", "x"));
        assertThrows(IllegalArgumentException.class, () -> manager.add(null, "x"));
        assertTrue(manager.getAll().isEmpty());
    }

    /** View all tasks -> all returned. */
    @Test
    void viewAllTasks() {
        manager.add("A", "");
        manager.add("B", "");
        assertEquals(2, manager.getAll().size());
    }

    /** View an empty list -> empty result (GUI shows the message). */
    @Test
    void viewEmptyList() {
        assertTrue(manager.getAll().isEmpty());
    }

    /** Update an existing task -> details updated. */
    @Test
    void updateExistingTask() {
        int id = manager.add("Old", "old desc").getId();
        assertTrue(manager.update(id, "New", "new desc", Status.IN_PROGRESS));
        var task = manager.getAll().get(0);
        assertEquals("New", task.getTitle());
        assertEquals("new desc", task.getDescription());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    /** Update a non-existing task -> handled gracefully. */
    @Test
    void updateNonExistingTask() {
        assertFalse(manager.update(99, "X", "Y", Status.COMPLETED));
    }

    /** Delete an existing task -> task removed. */
    @Test
    void deleteExistingTask() {
        int id = manager.add("Temp", "").getId();
        assertTrue(manager.delete(id));
        assertTrue(manager.getAll().isEmpty());
        assertFalse(manager.delete(id));
    }

    /** Set status through all three values. */
    @Test
    void setStatus() {
        int id = manager.add("Task", "").getId();
        for (Status s : Status.values()) {
            assertTrue(manager.setStatus(id, s));
            assertEquals(s, manager.getAll().get(0).getStatus());
        }
    }
}