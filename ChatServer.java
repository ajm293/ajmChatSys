import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple chat server with error handling.
 * Implements the Server interface.
 * Handles multiple clients through multithreading.
 */
public class ChatServer implements Server {

    private ServerSocket chatServerSocket;
    public List<ClientThread> clientList = new ArrayList<>();
    private int uid = 0;
    private volatile Boolean running;

    /**
     * Server constructor, establishes the server socket that the server resides on, on
     * the specified port number.
     *
     * @param portNumber : Port number to reside on.
     */
    public ChatServer(int portNumber) {
        running = true;
        try {
            System.out.println("Opening server socket on port " + portNumber + "...");
            chatServerSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println("Could not open server socket.");
            closeServer();
        }
    }

    /**
     * Closes the server by lowering the running flag, closing the server socket,
     * and closing the clients.
     */
    public synchronized void closeServer() {
        running = false;
        System.out.println("Attempting to close server...");
        try {
            chatServerSocket.close();
            // Creating a shutdown list avoids concurrent modification.
            List<ClientThread> clientsToClose = new ArrayList<>(clientList);
            for (ClientThread client : clientsToClose) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a client to the client list, increment the unique ID integer,
     * and start a new thread for the client.
     *
     * @param chatClientSocket : The socket of the client.
     */
    public synchronized void addClient(Socket chatClientSocket) {
        // Create client thread instance.
        ClientThread client = new ClientThread(chatClientSocket, "User" + uid, this);
        // Ensure each new user has a unique username.
        uid++;
        // Create thread from runnable instance and start it.
        Thread clientThread = new Thread(client);
        clientList.add(client);
        clientThread.start();
    }

    /**
     * Removes a client from the client list.
     *
     * @param client : Client to be removed.
     */
    public synchronized void removeClient(ClientThread client) {
        clientList.remove(client);
    }

    /**
     * Starts a thread that continuously accepts connections from connecting clients.
     */
    public void acceptConnectionsFromClients() {
        Thread connectionListener = new Thread() {
            public void run() {
                // Continuously add non-null connections as clients.
                while (running) {
                    Socket chatClientSocket = null;
                    try {
                        chatClientSocket = chatServerSocket.accept();
                        System.out.println("Server accepted connection on: " +
                                chatClientSocket.getInetAddress().toString());
                    } catch (IOException e) {
                        System.out.println("Server socket is closed...");
                    }
                    if (chatClientSocket != null) {
                        addClient(chatClientSocket);
                    }
                }
            }
        };
        System.out.println("Starting connection listener...");
        connectionListener.start();
    }

    /**
     * Starts a thread that continuously listens for admin input on the terminal,
     * handling the exit prompt for the server.
     */
    public void terminalListen() {
        Thread terminalListener = new Thread() {
            public void run() {
                BufferedReader terminalInput = new BufferedReader(new InputStreamReader(System.in));
                while (running) {
                    try {
                        if (terminalInput.readLine().equals("EXIT")) {
                            closeServer();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        System.out.println("Starting terminal listener...");
        terminalListener.start();
    }

    /**
     * Runs the chat server. This takes one optional parameter denoted by one flag:
     * <p>
     * -csp [int Port number]: Specifies the port the server will bind to.
     *
     * @param args : Command line arguments.
     */
    public static void main(String[] args) {
        // Set default port argument.
        int portNum = 14001;
        // Parse command line arguments.
        try {
            for (int i = 0; i < args.length; i++) {
                // If -csp found, try to make next argument port number.
                if (args[i].equalsIgnoreCase("-csp")) {
                    portNum = Integer.parseInt(args[i + 1]);
                }
            }
            // Catch errors and allow defaults through.
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid arguments. Using defaults instead.");
        } catch (IndexOutOfBoundsException ioe) {
            System.out.println("Incorrect argument format. Using defaults instead.");
        }
        // Create the server socket, and start client connection handler and terminal listener threads.
        ChatServer chatServer = new ChatServer(portNum);
        chatServer.acceptConnectionsFromClients();
        chatServer.terminalListen();
        System.out.println("The server is running!");
    }
}
