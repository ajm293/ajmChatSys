import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A chat bot that reads server output, detects strings denoting commands,
 * and sends back a predefined string corresponding to that command to the server.
 * A functional extension of ClientBase.
 */
public class ChatBot extends ClientBase implements Client {

    private final Map<String, String> promptsToReplies;

    /**
     * Default constructor. Above what is defined in ChatClient
     * it also instantiates the reply map and populates it.
     *
     * @param host : Host name of server to connect to.
     * @param port : Port number of host to connect to.
     */
    public ChatBot(String host, int port) {
        super(host, port);
        promptsToReplies = new HashMap<>();
        populateReplies();
    }

    /**
     * Populates the reply map with command-reply pairs.
     * Add .put calls in the format:
     * .put("Command keyword", "Reply")
     * to add commands to the bot.
     */
    public void populateReplies() {
        promptsToReplies.put("!smiley", ":-)");
        promptsToReplies.put("!square", "#####\n#...#\n#...#\n#...#\n#####");
        promptsToReplies.put("!huey", "You like Huey Lewis and the News?");
        promptsToReplies.put("!hello", "Hello there!");
    }

    /**
     * Sends a message to the server the client is connected to.
     *
     * @param reply : The bot reply to send to the server.
     */
    public void sendToServer(String reply) {
        serverOutput.println(reply);
    }

    /**
     * Continuously poll the server for messages, interpret them as possible commands,
     * and then send back the appropriate reply if a message is a command.
     */
    public void getFromServer() {
        try {
            // Continuously poll the server for messages.
            while (running) {
                String serverResponse = serverInput.readLine();
                if (serverResponse != null) {
                    // Split response on a colon and space, so that a typical message,
                    // "User0: foo", is read as "User0: " and "foo".
                    String[] serverResponses = serverResponse.split("[:]\\s");
                    // If there is a command to interpret
                    if (serverResponses.length > 1) {
                        String examinedKey = serverResponses[1];
                        if (promptsToReplies.containsKey(examinedKey)) {
                            // Send reply to server if message is command
                            sendToServer(promptsToReplies.get(examinedKey));
                        } else if (examinedKey.equals("!EXIT")) {
                            sendToServer("Shutting down bot...");
                            closeClient();
                        }
                    }
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

}
