// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// Aleksejs Moscanovs
// 220018173
// aleksejs.moscanovs@city.ac.uk


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

// DO NOT EDIT starts
interface FullNodeInterface {
    public boolean listen(String ipAddress, int portNumber);
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress);
}
// DO NOT EDIT ends


public class FullNode implements FullNodeInterface {

    public boolean listen(String ipAddress, int portNumber) {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("FullNode listening on " + ipAddress + ":" + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Incoming connection from: " + clientSocket.getInetAddress());

                // Handle the incoming connection
                handleConnection(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Error while listening: " + e.getMessage());
            return false;
        }
    }
    
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress) {
	// Implement this!
	return;
    }

    private void handleConnection(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

            // Implement request handling here
            // Example:
            // Read requests from the reader, process them, and send responses via the writer
        } catch (IOException e) {
            System.out.println("Error handling connection: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
