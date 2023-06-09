import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class ExpenseTrackerGUI extends JFrame {
    private JTextField expenseField;
    private JTextField amountField;
    private JTextArea expenseListArea;
    private JButton addButton;
    private JButton showButton;

    private static final String URL = "jdbc:mysql://localhost:3306/expense_tracker";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "welcome";

    public ExpenseTrackerGUI() {
        setTitle("Expense Tracker");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel expensePanel = new JPanel();
        expensePanel.setLayout(new GridLayout(3, 2));

        JLabel expenseLabel = new JLabel("Expense:");
        expenseField = new JTextField();

        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField();

        addButton = new JButton("Add Expense");
        addButton.addActionListener(new AddButtonListener());

        showButton = new JButton("Show Expenses");
        showButton.addActionListener(new ShowButtonListener());

        expenseListArea = new JTextArea();
        expenseListArea.setEditable(false);

        expensePanel.add(expenseLabel);
        expensePanel.add(expenseField);
        expensePanel.add(amountLabel);
        expensePanel.add(amountField);
        expensePanel.add(addButton);
        expensePanel.add(showButton);

        setLayout(new BorderLayout());
        add(expensePanel, BorderLayout.NORTH);
        add(new JScrollPane(expenseListArea), BorderLayout.CENTER);
    }

    private void addExpense(String description, double amount) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String insertQuery = "INSERT INTO expenses (description, amount) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                statement.setString(1, description);
                statement.setDouble(2, amount);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showExpenses() {
        expenseListArea.setText("");

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String fetchQuery = "SELECT * FROM expenses";
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(fetchQuery)) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String description = resultSet.getString("description");
                    double amount = resultSet.getDouble("amount");
                    expenseListArea.append(id + ". " + description + " - $" + amount + "\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class AddButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String expense = expenseField.getText();
            double amount = Double.parseDouble(amountField.getText());

            addExpense(expense, amount);

            expenseField.setText("");
            amountField.setText("");
        }
    }

    private class ShowButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            showExpenses();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ExpenseTrackerGUI expenseTracker = new ExpenseTrackerGUI();
                expenseTracker.setVisible(true);
            }
        });
    }
}
