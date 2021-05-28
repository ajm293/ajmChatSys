import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * An abstract class that can be extended to create a functional client that
 * connects to an instance of ChatServer. This has no functionality of itself
 * other than being able to open and close its connections, and is therefore
 * an abstract class.
 */
public abstract class ClientBase implements Client {

    protected Socket chatServerSocket;
    protected BufferedReader serverInput;
    protected PrintWriter serverOutput;
    protected InputStreamReader clientInput;
    protected BufferedReader clientText;
    protected boolean running;

    /**
     * Client constructor. Creates a socket to the specified host and port, and establishes server input
     * and output streams as well as the System.in client input stream.
     *
     * @param host : The name of the host to connect to.
     * @param port : The port number to bind to.
     */
    public ClientBase(String host, int port) {
        try {
            // Open up IO.
            System.out.println("Connecting to server...");
            this.chatServerSocket = new Socket(host, port);
            this.serverInput = new BufferedReader(new InputStreamReader((chatServerSocket.getInputStream())));
            this.serverOutput = new PrintWriter(chatServerSocket.getOutputStream(), true);
            this.clientInput = new InputStreamReader(System.in);
            this.clientText = new BufferedReader(this.clientInput);
            this.running = true;
            System.out.println("Connected to server!");
        } catch (IOException e) {
            // Close if IO fails to open.
            System.out.println("Could not connect to the server. Server closed?");
            closeClient();
        }
    }

    /**
     * Abstract method that defines getting data from a server. Also enforced through implementation of Client.
     */
    public abstract void getFromServer();

    /**
     * Shuts down the client by terminating running threads using the running flag, and by closing the
     * socket the client uses to connect to the server if necessary.
     */
    public synchronized void closeClient() {
        System.out.println("Shutting down client...");
        running = false;
        try {
            // Only try and close the server connection if the connection doesn't exist.
            if (chatServerSocket != null) {
                chatServerSocket.close();
            }
            if (clientText != null) {
                clientText.close();
                clientInput.close();
            }
            if (serverInput != null) {
                serverInput.close();
            }
            if (serverOutput != null) {
                serverOutput.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
