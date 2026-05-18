import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class CalculatorClient {
    private static final String HOST = "localhost";
    private static final int PORT = 1099;
    private static final String SERVICE = "Calculator";

    private static final Color BG_TOP = new Color(248, 249, 251);
    private static final Color BG_BOTTOM = new Color(230, 237, 245);
    private static final Color CARD_BG = new Color(255, 255, 255);
    private static final Color TEXT_DARK = new Color(28, 30, 33);
    private static final Color MUTED = new Color(110, 117, 128);
    private static final Color ACCENT = new Color(0, 135, 158);
    private static final Color BTN_ORANGE = new Color(233, 119, 48);
    private static final Color BTN_BLUE = new Color(46, 124, 190);

    private Calculator calculator;
    private final JLabel statusLabel = new JLabel("Status: DISCONNECTED");
    private final JLabel resultLabel = new JLabel("0");
    private final DefaultListModel<String> historyModel = new DefaultListModel<>();

    private void createAndShowUI() {
        JFrame frame = new JFrame("RMI Calculator Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(820, 460);
        frame.setMinimumSize(new Dimension(760, 420));
        frame.setLocationRelativeTo(null);

        JPanel root = new GradientPanel();
        root.setLayout(new BorderLayout(20, 20));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("RMI Calculator");
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);

        JLabel subtitle = new JLabel("Remote Operations Console");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(MUTED);

        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        JPanel titleBox = new JPanel(new BorderLayout());
        titleBox.setOpaque(false);
        titleBox.add(title, BorderLayout.NORTH);
        titleBox.add(subtitle, BorderLayout.SOUTH);

        header.add(title, BorderLayout.WEST);
        header.add(titleBox, BorderLayout.WEST);
        header.add(wrapStatusLabel(), BorderLayout.EAST);

        JTextField aField = new JTextField(12);
        JTextField bField = new JTextField(12);
        styleField(aField);
        styleField(bField);

        JPanel inputGrid = new JPanel(new GridBagLayout());
        inputGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel aLabel = new JLabel("Operand A");
        aLabel.setForeground(MUTED);
        inputGrid.add(aLabel, gbc);
        gbc.gridx = 1;
        inputGrid.add(aField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel bLabel = new JLabel("Operand B");
        bLabel.setForeground(MUTED);
        inputGrid.add(bLabel, gbc);
        gbc.gridx = 1;
        inputGrid.add(bField, gbc);

        JButton multiplyBtn = createActionButton("x Multiply", ACCENT);
        JButton divideBtn = createActionButton("/ Divide", BTN_ORANGE);
        JButton powerBtn = createActionButton("^ Power", BTN_BLUE);

        multiplyBtn.addActionListener(e -> performOperation("multiply", aField, bField));
        divideBtn.addActionListener(e -> performOperation("divide", aField, bField));
        powerBtn.addActionListener(e -> performOperation("power", aField, bField));

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(multiplyBtn);
        buttons.add(divideBtn);
        buttons.add(powerBtn);

        resultLabel.setFont(new Font("Serif", Font.BOLD, 32));
        resultLabel.setForeground(TEXT_DARK);
        resultLabel.setOpaque(true);
        resultLabel.setBackground(new Color(245, 248, 251));
        resultLabel.setBorder(new RoundedBorder(18, new Color(220, 227, 236)));

        JPanel centerBox = new RoundedPanel(CARD_BG, 24, true);
        centerBox.setLayout(new GridBagLayout());
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 0;
        centerGbc.anchor = GridBagConstraints.CENTER;
        centerGbc.insets = new Insets(8, 8, 8, 8);

        centerGbc.gridy = 0;
        centerBox.add(inputGrid, centerGbc);
        centerGbc.gridy = 1;
        centerBox.add(buttons, centerGbc);
        centerGbc.gridy = 2;
        centerBox.add(resultLabel, centerGbc);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(1, 8));
        centerGbc.gridy = 3;
        centerBox.add(spacer, centerGbc);

        JList<String> historyList = new JList<>(historyModel);
        historyList.setBackground(new Color(250, 252, 255));
        historyList.setForeground(TEXT_DARK);
        historyList.setFixedCellHeight(24);

        JPanel historyBox = new RoundedPanel(CARD_BG, 24, true);
        historyBox.setLayout(new BorderLayout(8, 8));
        JLabel historyTitle = new JLabel("History");
        historyTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        historyTitle.setForeground(MUTED);
        historyBox.add(historyTitle, BorderLayout.NORTH);
        historyBox.add(new JScrollPane(historyList), BorderLayout.CENTER);
        historyBox.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));

        root.add(header, BorderLayout.NORTH);
        root.add(centerBox, BorderLayout.CENTER);
        root.add(historyBox, BorderLayout.EAST);

        frame.setContentPane(root);
        frame.setVisible(true);

        connectToServer();
    }

    private void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            calculator = (Calculator) registry.lookup(SERVICE);
            updateStatus(true);
        } catch (RemoteException | NotBoundException e) {
            updateStatus(false);
            showError("Connection error", "Unable to connect to server", e.getMessage());
        }
    }

    private void performOperation(String op, JTextField aField, JTextField bField) {
        if (calculator == null) {
            connectToServer();
            if (calculator == null) {
                return;
            }
        }

        double a;
        double b;
        try {
            a = Double.parseDouble(aField.getText().trim());
            b = Double.parseDouble(bField.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Input error", "Invalid numbers", "Please enter valid numeric values.");
            return;
        }

        try {
            double result;
            String symbol;
            switch (op) {
                case "multiply":
                    result = calculator.multiply(a, b);
                    symbol = "x";
                    break;
                case "divide":
                    result = calculator.divide(a, b);
                    symbol = "/";
                    break;
                case "power":
                    result = calculator.power(a, b);
                    symbol = "^";
                    break;
                default:
                    return;
            }

            resultLabel.setText(String.valueOf(result));
            historyModel.addElement(a + " " + symbol + " " + b + " = " + result);
        } catch (ArithmeticException ex) {
            showError("Operation error", "Invalid operation", ex.getMessage());
        } catch (RemoteException ex) {
            updateStatus(false);
            showError("Remote error", "Operation failed", ex.getMessage());
        }
    }

    private JPanel wrapStatusLabel() {
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        statusLabel.setOpaque(true);
        updateStatus(false);

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.add(statusLabel);
        return wrapper;
    }

    private void updateStatus(boolean connected) {
        if (connected) {
            statusLabel.setText("CONNECTED");
            statusLabel.setForeground(new Color(8, 90, 62));
            statusLabel.setBackground(new Color(202, 240, 224));
        } else {
            statusLabel.setText("DISCONNECTED");
            statusLabel.setForeground(new Color(124, 24, 24));
            statusLabel.setBackground(new Color(245, 210, 210));
        }
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(new RoundedBorder(12, new Color(210, 218, 228)));
        field.setBackground(Color.WHITE);
    }

    private JButton createActionButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setBorder(new RoundedBorder(16, baseColor.darker()));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorderPainted(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });

        return button;
    }

    private void showError(String title, String header, String content) {
        JOptionPane.showMessageDialog(null, content, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        UIManager.put("ScrollBar.width", 10);
        SwingUtilities.invokeLater(() -> new CalculatorClient().createAndShowUI());
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint paint = new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BOTTOM);
            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final Color background;
        private final int radius;
        private final boolean shadow;

        RoundedPanel(Color background, int radius, boolean shadow) {
            this.background = background;
            this.radius = radius;
            this.shadow = shadow;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();

            if (shadow) {
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fillRoundRect(6, 6, width - 12, height - 12, radius, radius);
            }

            g2.setColor(background);
            g2.fillRoundRect(0, 0, width - 6, height - 6, radius, radius);
            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class RoundedBorder implements Border {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public Insets getBorderInsets(java.awt.Component c) {
            return new Insets(8, 12, 8, 12);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}
