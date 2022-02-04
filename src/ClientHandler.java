import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Statement;

public class ClientHandler implements Runnable {
    private Socket socket;
    private DBUtility utility;
    private Statement database;
    private BufferedReader in;
    private PrintWriter out;
    Client client;

    public ClientHandler(Socket socket) {
        try {
            utility = new DBUtility();
            Connection connection = utility.getConnection();
            database = connection.createStatement();
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            client = utility.login(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String message;
        while (socket.isConnected()) {
            try {
                message = in.readLine();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
