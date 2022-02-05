import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

public class DBUtility {

    private String path = "res/database.properties";
    private Connection connection;
    private Statement database;

    // INITIALIZER BLOCK
    {
        connection = getConnection();
        try {
            database = connection.createStatement();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public boolean testDB() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.closeOnCompletion();
            return connection.isValid(5);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        }
    }

    public Connection getConnection() {
        if (connection == null) getConnection(1);
        return connection;
    }

    private void getConnection(int dummyVal) {
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get(path))){
            properties.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String drivers = properties.getProperty("jdbc.drivers");
        if (drivers != null) System.setProperty("jdbc.drivers", drivers);
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String pw = properties.getProperty("jdbc.password");

        try {
            this.connection = DriverManager.getConnection(url, username, pw);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Client login(Socket socket) {
        Client client = null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM account WHERE username =? AND password =?")) {

            out.println("Username: ");
            String username = in.readLine();
            out.println("Password: ");
            String password = in.readLine();

            username.replace("\\b", "");
            password.replace("\\b", "");

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet account = statement.executeQuery();

            if (account.next()) client =
                    new Client(account.getInt(1),
                            account.getString(2),
                            account.getString(3));

            else throw new NoSuchUserException("User " + client.getUsername() + " not found.");
            connection.close();
            return client;
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (NoSuchUserException noSuchUser) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SQLException sqlException) {
        }
        return client;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Statement getDatabase() {
        return database;
    }

}
