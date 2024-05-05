// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// Aleksejs Moscanovs
// 220018173
// aleksejs.moscanovs@city.ac.uk


import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

// DO NOT EDIT starts
interface FullNodeInterface {
    public boolean listen(String ipAddress, int portNumber);
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress);
}
// DO NOT EDIT ends


public class FullNode implements FullNodeInterface {
    ServerSocket serverSocket;

    public boolean listen(String ipAddress, int portNumber) {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ipAddress, portNumber));
            System.out.println("FullNode listening on " + ipAddress + ":" + portNumber);


            return true;
        } catch (IOException e) {
            System.out.println("Error binding server socket: " + e.getMessage());
            return false;
        }
    }

    
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress) {
        Thread acceptThread = new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Incoming connection from: " + clientSocket.getInetAddress());
                    // Handle incoming connection in a separate thread
                    handleIncomingRequests(clientSocket);
                } catch (IOException e) {
                    System.out.println("Error accepting connection: " + e.getMessage());
                }
            }
        });
        acceptThread.start();

        Thread outgoingThread = new Thread(() -> {
            while (true) {
                try {
                    handleOutgoingRequests(startingNodeName, startingNodeAddress);
                }
                catch (Exception e) {
                    System.out.println("Error processing outgoing requests: " + e.getMessage());
                }
            }
        });
        outgoingThread.start();
    }

    private void handleOutgoingRequests(String startingNodeName, String startingNodeAddress) {
        String[] addressParts = startingNodeAddress.split(":");
        String nodeIP = addressParts[0];
        int nodePort = Integer.parseInt(addressParts[1]);

        try (Socket socket = new Socket(nodeIP, nodePort);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            // Send a message to the node
            writer.write("HELLO\n");
            writer.flush();

            String response;
            while ((response = reader.readLine()) != null) {
                switch (response) {
                    case "NOTIFIED":
                        System.out.println("Received NOTIFIED response from node.");
                        break;
                    case "END":
                        String endReason = reader.readLine();
                        System.out.println("Received END message. Reason: " + endReason);
                        return; // Terminate communication
                    case "NOPE":
                        System.out.println("Received NOPE response from node.");
                        break;
                    case "VALUE":
                        int numLines = Integer.parseInt(reader.readLine());
                        StringBuilder value = new StringBuilder();
                        for (int i = 0; i < numLines; i++) {
                            value.append(reader.readLine()).append("\n");
                        }
                        System.out.println("Received VALUE response from node: " + value);
                        break;
                    case "NODES":
                        int numNodes = Integer.parseInt(reader.readLine());
                        for (int i = 0; i < numNodes; i++) {
                            String nodeNameAddress = reader.readLine();
                            System.out.println("Received NODES response from node: " + nodeNameAddress);
                        }
                        break;
                    default:
                        System.out.println("Unexpected response: " + response);
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error handling outgoing request: " + e.getMessage());
        }

    }

    private void handleIncomingRequests(Socket socket) {
        try (
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            writer.write("YOUR_MESSAGE_HERE\n");
            writer.flush();

            String response;
            while ((response = reader.readLine()) != null) {
                String requestType = reader.readLine();
                switch (requestType) {
                    case "START":
                        String[] startParts = reader.readLine().split(" ");
                        int protocolVersion = Integer.parseInt(startParts[1]);
                        String nodeName = startParts[2];
                        System.out.println("Received START message from node: " + nodeName);
                        break;
                    case "END":
                        String endReason = reader.readLine();
                        System.out.println("Received END message. Reason: " + endReason);
                        break;
                    case "ECHO?":
                        writer.write("OHCE\n");
                        writer.flush();
                        System.out.println("Responded to ECHO? request");
                        break;
                    case "PUT?":
                        int keyLines = Integer.parseInt(reader.readLine());
                        int valueLines = Integer.parseInt(reader.readLine());
                        String key = readLines(reader, keyLines);
                        String value = readLines(reader, valueLines);
                        boolean stored = true; // Placeholder for actual logic
                        if (stored) {
                            writer.write("SUCCESS\n");
                        } else {
                            writer.write("FAILED\n");
                        }
                        writer.flush();
                        System.out.println("Responded to PUT? request");
                        break;
                    case "GET?":
                        int keyLinesGet = Integer.parseInt(reader.readLine());
                        String keyGet = readLines(reader, keyLinesGet);
                        String retrievedValue = "Hello\nWorld!\n"; // Placeholder for actual logic
                        if (retrievedValue != null) {
                            writer.write("VALUE 2\n" + retrievedValue);
                        }
                        else{
                                writer.write("NOPE\n");
                            }
                        writer.flush();
                        System.out.println("Responded to GET? request");
                        break;
                    case "NOTIFY?":
                        String fullNodeName = reader.readLine();
                        String fullNodeAddress = reader.readLine();
                        System.out.println("Received NOTIFY? request from node: " + fullNodeName);
                        writer.write("NOTIFIED\n");
                        writer.flush();
                        System.out.println("Responded to NOTIFY? request");
                        break;
                    case "NEAREST?":
                        String hashID = reader.readLine();
                        String nearestNodes = "NODES 3\n" +
                                "martin.brain@city.ac.uk:MyCoolImplementation,1.41,test-node-1\n" +
                                "10.0.0.4:2244\n" +
                                "martin.brain@city.ac.uk:MyCoolImplementation,1.67,test-node-7\n" +
                                "10.0.0.23:2400\n" +
                                "martin.brain@city.ac.uk:MyCoolImplementation,1.67,test-node-9\n" +
                                "10.0.0.96:35035";
                        writer.write(nearestNodes);
                        writer.flush();
                        System.out.println("Responded to NEAREST? request");
                        break;
                    default:
                        System.out.println("Unknown request type: " + requestType);
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error handling outgoing request: " + e.getMessage());
        }
    }

    private String readLines(BufferedReader reader, int numLines) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < numLines; i++) {
            stringBuilder.append(reader.readLine()).append("\n");
        }
        return stringBuilder.toString();
    }

    private void handleConnection(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

            // Implement request handling here
            // Example:
            // Read requests from the reader, process them, and send responses via the writer

            String request;
            while ((request = reader.readLine()) != null) {
                // Parse the request and determine its type
                String[] parts = request.split(" ", 2);
                String requestType = parts[0];
                String requestData = parts.length > 1 ? parts[1] : null;

                // Implement logic to handle different types of requests
                switch (requestType) {
                    case "ECHO?":
                        writer.write("OHCE\n");
                        writer.flush();
                        System.out.println("Responded to ECHO? request");
                        break;
                    case "PUT?":
                        String[] putData = requestData.split("\n", 2);
                        String key = putData[0];
                        String value = putData[1];
                        boolean putSuccess = true;
                        if (putSuccess) {
                            writer.write("SUCCESS\n");
                        } else {
                            writer.write("FAILED\n");
                        }
                        writer.flush();
                        System.out.println("Responded to PUT? request");
                        break;
                    case "GET?":
                        String getKey = requestData;
                        String retrievedValue = "Hello\nWorld!\n"; // Placeholder for actual logic
                        if (retrievedValue != null) {
                            writer.write("VALUE 2\n" + retrievedValue);
                        } else {
                            writer.write("NOPE\n");
                        }
                        writer.flush();
                        System.out.println("Responded to GET? request");
                        break;
                    case "NOTIFY?":
                        String[] notifyData = requestData.split("\n", 2);
                        String nodeName = notifyData[0];
                        String nodeAddress = notifyData[1];
                        boolean notifySuccess = true;
                        if (notifySuccess) {
                            writer.write("NOTIFIED\n");
                        } else {
                            writer.write("FAILED\n"); // Or any appropriate failure response
                        }
                        writer.flush();
                        System.out.println("Responded to NOTIFY? request");
                        break;
                    case "NEAREST?":
                        String hashID = requestData;
                        String nearestNodes = "NODES 3\n" +
                                "node1\naddress1\n" +
                                "node2\naddress2\n" +
                                "node3\naddress3\n";
                        writer.write(nearestNodes);
                        writer.flush();
                        System.out.println("Responded to NEAREST? request");
                        break;
                    default:
                        System.out.println("Unknown request type: " + requestType);
                        break;
                }
            }
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
