package taskmanager.ui;

import taskmanager.model.Status;
import taskmanager.model.Task;
import taskmanager.service.TaskManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

import java.nio.file.Path;

/**
 * Main window of the Task Management System (Swing GUI, Notion-style dark theme).
 *
 * @author Your Name
 */
public class MainFrame extends JFrame {
    private static final Color BG = new Color(0x191919), SURFACE = new Color(0x202020),
            FIELD = new Color(0x2B2B2B), LINE = new Color(0x373737), ROW_LINE = new Color(0x2A2A2A),
            TEXT = new Color(0xE6E6E6), MUTED = new Color(0x9B9B9B),
            ACCENT = new Color(0x2383E2), SELECT = new Color(0x26354A);
    private static final String FONT = "Segoe UI";

    static {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) { }
        UIManager.put("OptionPane.background", SURFACE);
        UIManager.put("Panel.background", SURFACE);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Button.background", FIELD);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("Button.select", LINE);
    }

    private final TaskManager manager = new TaskManager(Path.of("tasks.csv"));
    private final DefaultTableModel model =
            new DefaultTableModel(new String[]{"ID", "Title", "Description", "Status"}, 0) {
                @Override public boolean isCellEditable(int row, int col) { return false; }
            };
    private final JTable table = new JTable(model);
    private final JTextField titleField = new JTextField();
    private final JTextField descField = new JTextField();
    private final JComboBox<Status> statusBox = new JComboBox<>(Status.values());
    private final JLabel message = new JLabel(" ");
    private final JLabel countLabel = new JLabel(" ");

    /** Builds the window, wires the buttons and shows the empty-list message. */
    public MainFrame() {
        super("Task Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 560);
        setMinimumSize(new Dimension(760, 420));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(28, 36, 16, 36));
        setContentPane(root);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        top.add(header());
        top.add(Box.createVerticalStrut(18));
        top.add(formCard());

        message.setForeground(MUTED);
        message.setFont(new Font(FONT, Font.PLAIN, 12));

        root.add(top, BorderLayout.NORTH);
        root.add(tablePane(), BorderLayout.CENTER);
        root.add(message, BorderLayout.SOUTH);
        refresh(" ");
    }

    // ---------- UI building ----------

    private JPanel header() {
        JLabel title = new JLabel("Tasks");
        title.setFont(new Font(FONT, Font.BOLD, 30));
        title.setForeground(TEXT);
        countLabel.setFont(new Font(FONT, Font.PLAIN, 12));
        countLabel.setForeground(MUTED);
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(title, BorderLayout.NORTH);
        p.add(countLabel, BorderLayout.SOUTH);
        return p;
    }

    private JPanel formCard() {
        style(titleField);
        style(descField);
        styleCombo();

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(SURFACE);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setBorder(new CompoundBorder(new LineBorder(LINE, 1, true), new EmptyBorder(14, 16, 14, 16)));

        String[] names = {"TITLE", "DESCRIPTION", "STATUS"};
        JComponent[] inputs = {titleField, descField, statusBox};
        double[] weights = {0.35, 0.65, 0};
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < 3; i++) {
            c.gridx = i;
            c.weightx = weights[i];
            c.gridy = 0;
            c.insets = new Insets(0, 0, 4, 10);
            p.add(label(names[i]), c);
            c.gridy = 1;
            c.insets = new Insets(0, 0, 0, 10);
            p.add(inputs[i], c);
        }
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(button("Add", ACCENT, Color.WHITE, this::add));
        buttons.add(button("Update", FIELD, TEXT, this::update));
        buttons.add(button("Delete", new Color(0x4A2B2B), new Color(0xFF8A80), this::delete));
        c.gridx = 3;
        c.gridy = 1;
        c.weightx = 0;
        c.insets = new Insets(0, 0, 0, 0);
        p.add(buttons, c);
        return p;
    }

    private JScrollPane tablePane() {
        table.setBackground(BG);
        table.setForeground(TEXT);
        table.setFont(new Font(FONT, Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(SELECT);
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelected();
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, false, r, c);
                setBackground(sel ? SELECT : BG);
                setForeground(c == 0 ? MUTED : TEXT);
                setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, ROW_LINE),
                        new EmptyBorder(0, 12, 0, 12)));
                return this;
            }
        });
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(380);
        table.getColumnModel().getColumn(3).setMaxWidth(170);
        table.getColumnModel().getColumn(3).setMinWidth(150);

        JTableHeader h = table.getTableHeader();
        h.setReorderingAllowed(false);
        h.setBackground(BG);
        h.setPreferredSize(new Dimension(0, 34));
        h.setDefaultRenderer((t, v, s, f, r, c) -> {
            JLabel l = new JLabel(String.valueOf(v).toUpperCase());
            l.setOpaque(true);
            l.setBackground(BG);
            l.setForeground(MUTED);
            l.setFont(new Font(FONT, Font.BOLD, 11));
            l.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, LINE), new EmptyBorder(0, 12, 0, 12)));
            return l;
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setBackground(BG);
        sp.getViewport().setBackground(BG);
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        sp.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                super.configureScrollBarColors();
                thumbColor = LINE;
                trackColor = BG;
            }
            @Override protected JButton createDecreaseButton(int o) { return zeroButton(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroButton(); }
        });
        return sp;
    }

    // ---------- Styling helpers ----------

    private static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(FONT, Font.BOLD, 11));
        l.setForeground(MUTED);
        return l;
    }

    private static Border fieldBorder(Color c) {
        return new CompoundBorder(new LineBorder(c, 1, true), new EmptyBorder(7, 10, 7, 10));
    }

    private static void style(JTextField f) {
        f.setBackground(FIELD);
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setFont(new Font(FONT, Font.PLAIN, 14));
        Border idle = fieldBorder(LINE), focus = fieldBorder(ACCENT);
        f.setBorder(idle);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.setBorder(focus); }
            @Override public void focusLost(FocusEvent e) { f.setBorder(idle); }
        });
    }

    private void styleCombo() {
        statusBox.setUI(new BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                JButton b = new JButton("\u25BE");
                b.setBackground(FIELD);
                b.setForeground(MUTED);
                b.setBorder(new EmptyBorder(0, 8, 0, 10));
                b.setFocusPainted(false);
                b.setContentAreaFilled(false);
                return b;
            }
        });
        statusBox.setBackground(FIELD);
        statusBox.setForeground(TEXT);
        statusBox.setFont(new Font(FONT, Font.PLAIN, 14));
        statusBox.setBorder(new LineBorder(LINE, 1, true));
        statusBox.setPreferredSize(new Dimension(160, 36));
        statusBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i,
                                                          boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, v, i, sel, foc);
                setBackground(sel ? SELECT : FIELD);
                setForeground(TEXT);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
    }



    private static Color lighten(Color c) {
        return new Color(Math.min(255, c.getRed() + 22), Math.min(255, c.getGreen() + 22),
                Math.min(255, c.getBlue() + 22));
    }

    /** Creates a flat rounded button whose action reports any validation error in a dialog. */
    private JButton button(String text, Color bg, Color fg, Runnable action) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker()
                        : getModel().isRollover() ? lighten(bg) : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setRolloverEnabled(true);
        b.setForeground(fg);
        b.setFont(new Font(FONT, Font.BOLD, 13));
        b.setBorder(new EmptyBorder(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> {
            try {
                action.run();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        return b;
    }

    /** Draws the status as a colored pill, like Notion tags. */
    private static class StatusRenderer extends DefaultTableCellRenderer {
        private Color pillBg = LINE, pillFg = TEXT;

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                       boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, false, r, c);
            setBackground(sel ? SELECT : BG);
            setFont(new Font(FONT, Font.BOLD, 12));
            switch ((Status) v) {
                case IN_PROGRESS -> { pillBg = new Color(0x2B4C6F); pillFg = new Color(0x7FB7F0); }
                case COMPLETED -> { pillBg = new Color(0x2B4A3A); pillFg = new Color(0x7FD1A0); }
                default -> { pillBg = LINE; pillFg = new Color(0xD3D3D3); }
            }
            return this;
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(ROW_LINE);
            g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            FontMetrics fm = g2.getFontMetrics(getFont());
            int pw = fm.stringWidth(getText()) + 22, ph = 24, x = 12, y = (getHeight() - ph) / 2;
            g2.setColor(pillBg);
            g2.fillRoundRect(x, y, pw, ph, ph, ph);
            g2.setColor(pillFg);
            g2.setFont(getFont());
            g2.drawString(getText(), x + 11, y + (ph + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }

    // ---------- Actions (unchanged logic) ----------

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

    /** Reloads the table from the service and updates the count and message lines. */
    private void refresh(String msg) {
        List<Task> tasks = manager.getAll();
        model.setRowCount(0);
        for (Task t : tasks)
            model.addRow(new Object[]{t.getId(), t.getTitle(), t.getDescription(), t.getStatus()});
        countLabel.setText(tasks.size() + (tasks.size() == 1 ? " task" : " tasks"));
        message.setText(tasks.isEmpty() ? "No tasks available. Add one above." : msg);
    }
}