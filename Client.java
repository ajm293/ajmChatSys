/**
 * An interface defining the core methods of a client.
 */
interface Client {

    /**
     * Defines getting data from a server.
     */
    void getFromServer();

    /**
     * Defines a client being able to close itself.
     */
    void closeClient();

}
