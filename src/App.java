import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.net.*;

class App
{
    JFrame frame;
    JTextField nameField;
    JTextField quantityField;
    JTextField costField;

    App()
    {
        frame = new JFrame("Pharmacy Management System");
        frame.setSize(700, 600);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display Login/Register frame
        Login login = new Login();
        login.setLoginCallback(() -> frame.setVisible(true));

        //Setting up GUI
        window();

        //Background image
        setBackground();
    }


    /* Main window */
    void window()
    {
        //Window Icon
        try {
            String url = "https://cdn-icons-png.flaticon.com/512/4599/4599153.png";
            URI imageUrl = new URI(url);
            Image icon = ImageIO.read(imageUrl.toURL());
            frame.setIconImage(icon);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Medicine Name
        createLabel("Medicine Name :", 140, 32, 180, 30);
        nameField = createTextField(330, 30, 200, 40, false);

        //Quantity
        createLabel("Quantity :", 140, 82, 110, 30);
        quantityField = createTextField(330, 80, 200, 40, true);

        //Cost
        createLabel("Cost :", 140, 132, 60, 30);
        costField = createTextField(330, 130, 200, 40, true);

        //Buttons
        createButton("Add Medicine", 180, 190, 145, 40, (new Color(60, 179, 113)), (e -> addMedicine()) );
        createButton("Delete Medicine", 340, 190, 165, 40, Color.RED, (e -> deleteMedicine()) );
        createButton("View Inventory", 260, 490, 170, 40, Color.BLUE, (e -> viewInventory()) );

        //Medicine List
        displayMedicines();
    }


    /* Function to set background image */
    void setBackground()
    {
        try
        {
            String url = "https://img.freepik.com/free-photo/grunge-style-background-cracked_1048-14268.jpg";
            URI imageUrl = new URI(url);
            Image backgroundImage = ImageIO.read(imageUrl.toURL());
            Image scaledImage = backgroundImage.getScaledInstance(700, 600, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            JLabel backgroundLabel = new JLabel(scaledIcon);
            backgroundLabel.setBounds(0, 0, 700, 600);
            frame.getLayeredPane().add(backgroundLabel, Integer.valueOf(Integer.MIN_VALUE));
            JPanel contentPane = (JPanel) frame.getContentPane();
            contentPane.setOpaque(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /* Function to create Labels */
    void createLabel(String labelText, int x, int y, int width, int height)
    {
        JLabel label = new JLabel(labelText);
        label.setBounds(x, y, width, height);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Ebrima", Font.BOLD, 22));
        frame.add(label);
    }


    /* Function to create TextFields */
    JTextField createTextField(int x, int y, int width, int height, boolean numericOnly)
    {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, width, height);
        textField.setFont(new Font("Arial", Font.PLAIN, 20));
        Border paddingBorder = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        textField.setBorder(BorderFactory.createCompoundBorder(textField.getBorder(), paddingBorder));

        //Key listener to filter out non-numeric characters if numericOnly is true
        if (numericOnly) {
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!(Character.isDigit(c) || (c == '.') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                        e.consume();
                    }
                    // Allow only one period
                    if (c == '.' && textField.getText().contains(".")) {
                        e.consume();
                    }
                }
            });
        }

        frame.add(textField);
        return textField;
    }


    /* Function to create Buttons */
    void createButton(String text, int x, int y, int width, int height, Color bgColor, ActionListener actionListener)
    {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.addActionListener(actionListener);
        frame.add(button);
    }


    /* Add Medicine Button */
    void addMedicine()
    {
        String name = nameField.getText();
        String quantityText = quantityField.getText();
        String costText = costField.getText();

        //If any field is empty, display error message
        if (name.isEmpty() || quantityText.isEmpty() || costText.isEmpty())
        {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please enter medicine name, quantity, and cost.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        //If all fields are inputted, add the medicine to database
        int quantity = Integer.parseInt(quantityText);
        double cost = Double.parseDouble(costText);

        try (Connection connection = PharmacyManagementSystem.getConnection())
        {
            PharmacyManagementSystem.createTable(connection);
            PharmacyManagementSystem.insertData(connection, name, quantity, cost);
            JOptionPane.showMessageDialog(
                    frame,
                    "Medicine added successfully!"
            );

            //Clear all text fields
            nameField.setText("");
            quantityField.setText("");
            costField.setText("");
        }
        catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(
                    frame,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    /* Delete Medicine Button */
    void deleteMedicine()
    {
        String name = nameField.getText();

        try (Connection connection = PharmacyManagementSystem.getConnection())
        {
            //Query
            String deleteSQL = "DELETE FROM medicines WHERE name=?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL))
            {
                preparedStatement.setString(1, name);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Medicine deleted successfully!"
                    );
                }
                else {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Medicine not found!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
        catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(
                    frame,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    /* Table of first 5 medicines */
    void displayMedicines()
    {
        try (Connection connection = PharmacyManagementSystem.getConnection())
        {
            //Query
            String selectSQL = "SELECT name, quantity, cost FROM medicines";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL))
            {
                ResultSet resultSet = preparedStatement.executeQuery();

                //Create table model with column names
                String[] columnNames = {"Medicine", "Quantity", "Price"};
                DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

                //Populate table model with data
                int rowCount = 0;
                while (resultSet.next() && rowCount < 5)
                {
                    String name = resultSet.getString("name");
                    int quantity = resultSet.getInt("quantity");
                    double cost = resultSet.getDouble("cost");
                    String formattedCost = String.format("$%.2f", cost);
                    Object[] rowData = {name, quantity, formattedCost};
                    tableModel.addRow(rowData);
                    rowCount++;
                }

                //Create JTable with table model
                JTable table = new JTable(tableModel)
                {
                    @Override
                    public TableCellRenderer getCellRenderer(int row, int column)
                    {
                        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
                        renderer.setHorizontalAlignment(SwingConstants.CENTER);
                        return renderer;
                    }
                };

                //Column header styling
                JTableHeader header = table.getTableHeader();
                header.setFont(new Font("Arial", Font.BOLD, 22));
                DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
                headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                for (int i = 0; i < table.getColumnCount(); i++)
                {
                    TableColumn column = table.getColumnModel().getColumn(i);
                    column.setHeaderValue(column.getHeaderValue().toString().toUpperCase());
                }

                //Row styling
                table.setFont(new Font("Arial", Font.PLAIN, 20));

                //Row height
                table.setRowHeight(35);

                //Add table to scroll pane and add scroll pane to frame
                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setBounds(50, 260, 600, 208);
                frame.add(scrollPane);
            }
        }
        catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(
                    frame,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    /* View Inventory Button */
    void viewInventory()
    {
        try (Connection connection = PharmacyManagementSystem.getConnection())
        {
            //Query
            String selectSQL = "SELECT name, quantity, cost FROM medicines";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL))
            {
                ResultSet resultSet = preparedStatement.executeQuery();
                JFrame inventoryFrame = new JFrame("Inventory");
                inventoryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                //Window Icon
                try {
                    String url = "https://cdn-icons-png.flaticon.com/512/4599/4599153.png";
                    URI imageUrl = new URI(url);
                    Image icon = ImageIO.read(imageUrl.toURL());
                    inventoryFrame.setIconImage(icon);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                //Create table model with column names
                String[] columnNames = {"Medicine", "Quantity", "Price"};
                DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

                //Populate table model with data
                while (resultSet.next())
                {
                    String name = resultSet.getString("name");
                    int quantity = resultSet.getInt("quantity");
                    double cost = resultSet.getDouble("cost");
                    String formattedCost = String.format("$%.2f", cost);
                    Object[] rowData = {name, quantity, formattedCost};
                    tableModel.addRow(rowData);
                }

                //Create JTable with table model
                JTable table = new JTable(tableModel)
                {
                    @Override
                    public TableCellRenderer getCellRenderer(int row, int column)
                    {
                        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
                        renderer.setHorizontalAlignment(SwingConstants.CENTER);
                        return renderer;
                    }
                };

                //Column header styling
                JTableHeader header = table.getTableHeader();
                header.setFont(new Font("Arial", Font.BOLD, 22));
                DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
                headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                for (int i = 0; i < table.getColumnCount(); i++)
                {
                    TableColumn column = table.getColumnModel().getColumn(i);
                    column.setHeaderValue(column.getHeaderValue().toString().toUpperCase());
                }

                //Row styling
                table.setFont(new Font("Arial", Font.PLAIN, 20));

                //Row height
                table.setRowHeight(40);

                //Add table to scroll pane and add scroll pane to frame
                JScrollPane scrollPane = new JScrollPane(table);
                inventoryFrame.add(scrollPane);

                //Size of window
                inventoryFrame.setSize(700, 500);
                inventoryFrame.setLocationRelativeTo(null);
                inventoryFrame.setVisible(true);
            }
        }
        catch (SQLException ex)
        {
            JOptionPane.showMessageDialog(
                    frame,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new App());
    }
}