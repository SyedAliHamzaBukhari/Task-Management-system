package taskmanager.ui;

import taskmanager.model.Status;
import taskmanager.model.Task;
import taskmanager.service.TaskManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Main window of the Task Management System (Swing GUI).
 *
 * @author Your Name
 */
public class MainFrame extends JFrame {
    private final TaskManager manager = new TaskManager();
    private final DefaultTableModel model =
            new DefaultTableModel(new String[]{"ID", "Title", "Description", "Status"}, 0) {
                @Override public boolean isCellEditable(int row, int col) { return false; }
            };
    private final JTable table = new JTable(model);
    private final JTextField titleField = new JTextField(15);
    private final JTextField descField = new JTextField(20);
    private final JComboBox<Status> statusBox = new JComboBox<>(Status.values());
    private final JLabel message = new JLabel(" ");

    /** Builds the window, wires the buttons and shows the empty-list message. */
    public MainFrame() {
        super("Task Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 450);
        setLocationRelativeTo(null);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelected();
        });

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Title:"));       form.add(titleField);
        form.add(new JLabel("Description:")); form.add(descField);
        form.add(new JLabel("Status:"));      form.add(statusBox);
        form.add(button("Add", this::add));
        form.add(button("Update", this::update));
        form.add(button("Delete", this::delete));

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(message, BorderLayout.SOUTH);
        refresh(" ");
    }

    /** Creates a button whose action reports any validation error in a dialog. */
    private JButton button(String text, Runnable action) {
        JButton b = new JButton(text);
        b.addActionListener(e -> {
            try {
                action.run();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        return b;
    }

    private void add() {
        Task t = manager.add(titleField.getText(), descField.getText());
        manager.setStatus(t.getId(), (Status) statusBox.getSelectedItem());
        clearForm();
        refresh("Task added.");
    }

    private void update() {
        boolean ok = manager.update(selectedId(), titleField.getText(), descField.getText(),
                (Status) statusBox.getSelectedItem());
        clearForm();
        refresh(ok ? "Task updated." : "Task not found.");
    }

    private void delete() {
        int id = selectedId();
        if (JOptionPane.showConfirmDialog(this, "Delete task " + id + "?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        clearForm();
        refresh(manager.delete(id) ? "Task deleted." : "Task not found.");
    }

    /** @return id of the selected row; throws if nothing is selected */
    private int selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) throw new IllegalArgumentException("Select a task from the table first.");
        return (int) model.getValueAt(row, 0);
    }

    /** Copies the selected row into the input fields. */
    private void loadSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        titleField.setText(model.getValueAt(row, 1).toString());
        descField.setText(model.getValueAt(row, 2).toString());
        statusBox.setSelectedItem(model.getValueAt(row, 3));
    }

    private void clearForm() {
        titleField.setText("");
        descField.setText("");
        statusBox.setSelectedIndex(0);
        table.clearSelection();
    }

    /** Reloads the table from the service and updates the message line. */
    private void refresh(String msg) {
        List<Task> tasks = manager.getAll();
        model.setRowCount(0);
        for (Task t : tasks)
            model.addRow(new Object[]{t.getId(), t.getTitle(), t.getDescription(), t.getStatus()});
        message.setText(tasks.isEmpty() ? "No tasks available. Add one above." : msg);
    }
}