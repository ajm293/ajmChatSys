CHATSYS - Consisting of ChatServer and ChatClient

IMPLEMENTATION DETAILS
======================

The full application consists of the following .java files:

ChatServer.java - Contains the implementation of the server, consisting of
a connection listener which listens for new connections and dispatches them
to individual ClientThread instances, and a terminal listener which listens
for input from the server's own terminal. The server is multithreaded and
can handle multiple client connections at once. Contains a main() function
to run the server.

ClientThread.java - Contains code for handling an individual client, and is
run as a thread by the server. Is able to send a message to all clients
connected to the server by using the parent server's client list, which is
itself a list of all ClientThread instances.

ClientBase.java - An abstract class which can be extended to create a
functional client that connects to the chat server. It has no functionality
other than opening and closing connections.

ChatClient.java - Contains the implementation of the client as specified,
and is an extension of ClientBase which adds the functionality of being able
to simultaneously read and send user input to the server, and read and send
messages from the server to the user. Contains a main() function to run the
client.

ChatBot.java - Contains the implementation of a chat bot as specified, and
is an extension of ClientBase which adds the functionality of being able to
give predetermined replies to specific user commands. This is run from
ChatClient by using an optional flag in the command line.

Server.java - This is an interface which defines the core methods of an
implementation of a server: Accepting client connections, listening to its
terminal, and closing itself.

Client.java - This is an interface which defines the core methods of an
implementation of a client: Getting data from the server, and closing itself.

USAGE DETAILS
=============

COMPILING:

The system is compiled in its entirety by running:
	javac *.java
in the root directory of the classes.

To compile just the server, only the following files are needed in directory:
	ChatServer.java
	ClientThread.java
	Server.java
	
To compile just the client+bot, only the following files are needed:
	ChatClient.java
	ClientBase.java
	ChatBot.java
	Client.java
	
RUNNING:
	
The server is started with the following command:
	java ChatServer [-csp PortNumber]
The optional parameter -csp PortNumber specifies the port on which the server
will try and open and accept connections on. By default the server will
start on port 14001.

The client is started with the following command:
	java ChatClient [-ccp PortNumber] [-cca HostName] [-bot]
The optional parameter -ccp PortNumber specifies the port on which the client
will try and connect to the server through. By default the client will try
and use port 14001.
The optional parameter -cca HostName specifies the host name on which the
client will try and connect to the server on. By default the client will
try to connect to localhost.
The optional flag -bot specifies that the client should run as a bot,
instead of as a regular user-input client. Without this flag, the client
will run as a normal client.

USAGE:

The ChatServer has a running terminal listener, which listens for an admin
to type in 'EXIT' into the terminal, which then closes the server.

The ChatClient ran allows a user to send messages which will then be seen by
all other clients connected to the server, including the sending client.
Each user is assigned a unique username by the server which is a unique ID
incremented on every connection.

The ChatBot, which is ran by attaching the -bot flag to ChatClient, does not
display any other client messages to an observing user, but processes the
messages itself and sends a reply message to all other connected clients when
it detects a valid command.

EXITING:

On the server, type 'EXIT' to close the server.

On the client, type 'EXIT' to close the client. On terminal, you can use
Ctrl+c as an alternative.

On the chatbot, type '!EXIT' from another client to close the chatbot.
Alternatively, you can use Ctrl+C on the chatbot terminal to close.

CHATBOT COMMANDS:

The current commands are:
	!smiley, !square, !huey, !hello
More commands can be added by adding String, String maps to populateReplies()
in ChatBot.java and then recompiling the client as above. The format chosen
for commands is starting with '!' but any string is valid.