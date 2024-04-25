import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.imageio.*;
import java.sql.*;
import java.net.*;

class Login
{
    Connection connection;
    Runnable loginCallback;

    Login()
    {
        JFrame frame = new JFrame("Login / Register");
        frame.setSize(500, 300);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        //Background color
        frame.getContentPane().setBackground(new Color(230, 230, 230));

        //Email Label
        JLabel emailLabel = new JLabel("Email :");
        emailLabel.setBounds(80, 53, 70, 30);
        emailLabel.setFont(new Font("Candara", Font.BOLD, 22));
        frame.add(emailLabel);

        //Email Field
        JTextField emailField = new JTextField();
        emailField.setBounds(200, 50, 200, 35);
        emailField.setFont(new Font("Arial", Font.PLAIN, 20));
        emailField.setBorder(null);
        Border paddingBorder = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        emailField.setBorder(BorderFactory.createCompoundBorder(emailField.getBorder(), paddingBorder));
        frame.add(emailField);

        //Password Label
        JLabel passwordLabel = new JLabel("Password :");
        passwordLabel.setBounds(80, 108, 110, 30);
        passwordLabel.setFont(new Font("Candara", Font.BOLD, 22));
        frame.add(passwordLabel);

        //Password Field
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(200, 105, 200, 35);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordField.setBorder(null);
        passwordField.setBorder(BorderFactory.createCompoundBorder(passwordField.getBorder(), paddingBorder));
        frame.add(passwordField);

        //Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(110, 180, 120, 32);
        loginButton.setFont(new Font("Arial", Font.PLAIN, 18));
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        frame.add(loginButton);

        //Register Button
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(250, 180, 120, 32);
        registerButton.setFont(new Font("Arial", Font.PLAIN, 18));
        registerButton.setBackground(new Color(204, 0, 0));
        registerButton.setForeground(Color.WHITE);
        frame.add(registerButton);


        /* Action listener for Login button */
        loginButton.addActionListener((e) -> {
            String email = emailField.getText();
            String password = String.valueOf(passwordField.getPassword());

            if (loginUser(email, password))
            {
                JOptionPane.showMessageDialog(frame, "Login successful!");
                // Trigger login callback
                if (loginCallback != null) {
                    loginCallback.run();
                }
            }
            else {
                JOptionPane.showMessageDialog(frame, "Invalid email or password!");
            }

            //Clear fields
            emailField.setText("");
            passwordField.setText("");
        });


        /* Action listener for Register button */
        registerButton.addActionListener((e) -> {
            String email = emailField.getText();
            String password = String.valueOf(passwordField.getPassword());

            //Check if email or password fields are empty
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both email and password.");
                return; //Stop further execution
            }

            //Proceed with user registration
            if (registerUser(email, password)) {
                JOptionPane.showMessageDialog(frame, "Registration successful! You can now login.");
            }
            else {
                JOptionPane.showMessageDialog(frame, "Registration failed!");
            }

            //Clear fields
            emailField.setText("");
            passwordField.setText("");
        });


        //Make the frame visible
        frame.setVisible(true);

        //Connect to the database
        connectToDatabase();
    }


    /* Method to connect to the database */
    void connectToDatabase()
    {
        String url = "jdbc:mysql://localhost:3306/PharmacyManagementSystem";
        String username = "root";
        String password = "My Sql12";

        try {
            connection = DriverManager.getConnection(url, username, password);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /* Method to register a new user */
    boolean registerUser(String email, String password)
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Users (email, password) VALUES (?, ?)");
            statement.setString(1, email);
            statement.setString(2, password);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }


    /* Method to check if a user exists during login */
    boolean loginUser(String email, String password)
    {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Users WHERE email = ? AND password = ?");
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /* Method to set login callback */
    void setLoginCallback(Runnable callback)
    {
        this.loginCallback = callback;
    }
}