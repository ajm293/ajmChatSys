/**
 * An interface defining the core methods of a server.
 */
public interface Server {

    /**
     * Defines a server being able to accept connections.
     */
    void acceptConnectionsFromClients();

    /**
     * Defines a server being able to listen to its own terminal.
     */
    void terminalListen();

    /**
     * Defines a server being able to close itself.
     */
    void closeServer();

}
