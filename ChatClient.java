import java.io.IOException;

/**
 * A chat client that takes input from the user and reads output from a
 * chat server simultaneously through multithreading.
 * A functional extension of ClientBase.
 */
public class ChatClient extends ClientBase implements Client {

    /**
     * ChatClient constructor. Simply a call to the ClientBase constructor.
     * Creates a socket to the specified host and port, and establishes server input
     * and output streams as well as the System.in client input stream.
     *
     * @param host : The name of the host to connect to.
     * @param port : The port number to bind to.
     */
    public ChatClient(String host, int port) {
        super(host, port);
    }

    /**
     * Establishes a thread that handles the input of messages from System.in and then sends those
     * messages to the server the client is connected to.
     */
    public void sendToServer() {
        // Continuously poll for user input and send to the server.
        // Prevents blocking on shutdown.
        // Close the client connection if the user enters 'EXIT'
        Thread inputThread = new Thread() {
            public void run() {
                try {
                    // Continuously poll for user input and send to the server.
                    while (running) {
                        // Prevents blocking on shutdown.
                        if (!clientInput.ready()) {
                            // Sleep if not ready
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                        String clientInputString = clientText.readLine();
                        // Close the client connection if the user enters 'EXIT'
                        if (clientInputString.equals("EXIT")) {
                            closeClient();
                        } else {
                            serverOutput.println(clientInputString);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        inputThread.start();
    }

    /**
     * Establishes a thread that handles getting messages from the server the client is connected
     * to and sends them to the user through System.out.
     */
    public void getFromServer() {
        Thread outputThread = new Thread() {
            public void run() {
                try {
                    // Continuously poll the server for messages and print to user.
                    while (running) {
                        String serverResponse = serverInput.readLine();
                        if (serverResponse != null) {
                            System.out.println(serverResponse);
                        } else {
                            closeClient();
                        }
                    }
                    // The client has failed to use the socket, normal when closing.
                } catch (IOException e) {
                    System.out.println("Socket is closed...");
                    if (running) {
                        closeClient();
                    }
                }
            }
        };
        outputThread.start();
    }

    /**
     * Runs the chat client. This takes two optional arguments denoted by two flags:
     * <p>
     * -csp [int Port number]: Specifies the port the client should bind to.
     * <p>
     * -cca [String Host name]: Specifies the host the client should connect to.
     *
     * @param args : Command line arguments as detailed above.
     */
    public static void main(String[] args) {
        // Set default arguments.
        int portNum = 14001;
        String hostName = "localhost";
        boolean bot = false;
        // Parse command line arguments.
        try {
            for (int i = 0; i < args.length; i++) {
                // If -csp found, try to make next argument the port number.
                if (args[i].equalsIgnoreCase("-ccp")) {
                    portNum = Integer.parseInt(args[i + 1]);
                }
                // If -cca found, try to make next argument the host name.
                if (args[i].equalsIgnoreCase("-cca")) {
                    hostName = args[i + 1];
                }
                if (args[i].equalsIgnoreCase("-bot")) {
                    bot = true;
                }
            }
            // Catch errors and allow defaults through.
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid arguments. Using defaults instead.");
        } catch (IndexOutOfBoundsException ioe) {
            System.out.println("Incorrect argument format. Using defaults instead.");
        }
        // Create connection to server and start input/output handlers.
        if (bot) {
            ChatBot chatBot = new ChatBot(hostName, portNum);
            chatBot.getFromServer();
        } else {
            ChatClient chatClient = new ChatClient(hostName, portNum);
            chatClient.sendToServer();
            chatClient.getFromServer();
        }
    }
}
