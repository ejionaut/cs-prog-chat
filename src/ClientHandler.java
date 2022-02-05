import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private static List<ClientHandler> clients = new ArrayList<>();
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
            if (client != null) out.println(client.getUsername() + " logged in.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String message;
        while (socket.isConnected()) {
            try {
                out.println(client.getUsername() + ": ");
                message = in.readLine();

            } catch (Exception e) {
                closeEverything(socket, in, out);
            }
        }
    }

    public void broadcastMessage(String message) {
        for (var client : clients) {
            try {
                if (!client.getClient().getUsername().equals(this.client.getUsername())) {
                    client.out.println(message);
                }
            } catch (Exception e) {
                closeEverything(socket, in, out);
            }
        }
    }

    public void removeClientHandler() {
        clients.remove(this);

    }

    public void closeEverything(Socket socket, BufferedReader in, PrintWriter out) {
        removeClientHandler();
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Client getClient() {
        return client;
    }
}
