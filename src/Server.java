import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Server {
    private ServerSocket serverSocket;
    private Connection connection;
    private Statement database;
    private DBUtility utility;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        /*
        utility = new DBUtility();
        connection = utility.getConnection();
        try {
            database = connection.createStatement();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

         */
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established.");
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8189)){
            Server server = new Server(serverSocket);
            server.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DBUtility utility = new DBUtility();
        // System.out.println(utility.testDB());
    }

}
