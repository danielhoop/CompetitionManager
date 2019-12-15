package ch.ffhs.pa.competitionmanager.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * WebServer Object.
 *
 * WebServer represents a server that serves up web content through its
 * ServerSocket. Listens indefinitely for new client connections and creates
 * a new thread to handle client requests.
 *
 *
 */
public class WebServer {

    /**
     * Creates the ServerSocket and listens for client connections, creates a
     * separate thread to handle each client request.
     *
     * @param args an array of arguments to be used in the
     *
     *
     */
    public static void startWebserver(int port) throws IOException {

        // Create ServerSocket on LocalHost, port 80
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Listening for connections on port " + port + "...");

        // Listen for new client connections
        while(true) {
            // Accept new client connection
            // Hint: accept() is a blocking method. This loop will repeat only if a socket was accepted.
            Socket connectionSocket = serverSocket.accept();

            // Create new thread to handle client request
            Thread connectionThread = new Thread(new Connection(connectionSocket));

            // Start the connection thread
            connectionThread.start();
            //System.out.println("New connection on port 80...");
        }
    }
}
