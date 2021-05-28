import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * An implementation of Runnable that handles a single client
 * connected to an instance of ChatServer.
 */
public class ClientThread implements Runnable {

    private Socket clientSocket = null;
    private InputStreamReader clientInput;
    private PrintWriter clientOutput;
    private BufferedReader clientText;
    private String username;
    private volatile boolean running;
    private ChatServer parentServer;

    /**
     * Client Thread constructor, establishes the socket the server is using to connect to the client,
     * the input and output streams associated with the socket, the reader associated with taking client
     * input, and the username of the client. Also sets the running flag, to allow the thread to loop in run().
     *
     * @param clientSocket : The socket the server is using to connect to the client.
     * @param username     : The username the server has given the client.
     * @param parentServer : The ChatServer the thread is a child of.
     */
    public ClientThread(Socket clientSocket, String username, ChatServer parentServer) {
        try {
            // Open up I/O.
            this.clientSocket = clientSocket;
            this.clientInput = new InputStreamReader(clientSocket.getInputStream());
            this.clientOutput = new PrintWriter(clientSocket.getOutputStream(), true);
            this.clientText = new BufferedReader(clientInput);
            this.username = username;
            this.parentServer = parentServer;
            running = true;
            // Inform the user of their username.
            send("Your username is: " + this.username);
        } catch (IOException e) {
            System.out.println("Failed to initiate client thread.");
            close();
        }
    }

    /**
     * Sends a String message to the client through the socket's output stream.
     *
     * @param message : The message to send to the client.
     */
    public synchronized void send(String message) {
        clientOutput.println(message);
    }

    /**
     * Sends a String message to every client connected to the server.
     *
     * @param message : The message to send to all clients.
     */
    public synchronized void broadcast(String message) {
        if (message != null) {
            System.out.println("Response taken from " + username);
            // Send message to all clients in the parent server client list.
            for (ClientThread client : parentServer.clientList) {
                System.out.println("Sending to " + client.username);
                client.send(this.username + ": " + message);
            }
        } else if (running) {
            // Close the client thread if the client starts sending nulls.
            close();
        }
    }


    /**
     * Reads the client's input through the socket's input stream.
     *
     * @return : The message the client has inputted.
     */
    public String read() {
        String result = null;
        try {
            result = clientText.readLine();
        } catch (IOException e) {
            System.out.println("Failed to read from " + username);
        }
        return result;
    }

    /**
     * Close the connection and thread associated with the client by closing the streams,
     * closing the socket, lowering the running flag, and removing itself from the list
     * of clients held by the server.
     */
    public synchronized void close() {
        System.out.println(username + " is disconnecting...");
        try {
            // Close I/O.
            clientSocket.shutdownInput();
            clientSocket.shutdownOutput();
            clientSocket.close();
            clientText.close();
            clientInput.close();
            clientOutput.close();
            running = false;
            // The client thread removes itself instead of the server doing so as this is simpler.
            parentServer.removeClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the thread, continuously reading from the client's input and then sending that message
     * to all users if not null whilst the running flag is set. The thread is closed if the input
     * is null, as this corresponds to a disconnected client.
     */
    public void run() {
        while (running) {
            // Continuously broadcast what the client sends to all users.
            String clientInputString = read();
            broadcast(clientInputString);
        }
    }
}
