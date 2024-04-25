import java.sql.*;

class PharmacyManagementSystem
{
    static String JDBC_URL = "jdbc:mysql://localhost:3306/PharmacyManagementSystem";
    static String USERNAME = "root";
    static String PASSWORD = "My Sql12";


    /* Method to establish database connection */
    static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }


    /* Method to create the Medicines table */
    static void createTable(Connection connection) throws SQLException
    {
        //Query
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Medicines (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "quantity INT NOT NULL," +
                "cost DOUBLE NOT NULL" +
                ")";

        try (Statement statement = connection.createStatement())
        {
            statement.executeUpdate(createTableSQL);
        }
    }


    /* Method to insert data into the table */
    static void insertData(Connection connection, String name, int quantity, double cost) throws SQLException
    {
        //Query
        String insertSQL = "INSERT INTO medicines (name, quantity, cost) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL))
        {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, quantity);
            preparedStatement.setDouble(3, cost);
            preparedStatement.executeUpdate();
        }
    }
}