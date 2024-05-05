// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
//Aleksejs Moscanovs
//220018173
//aleksejs.moscanovs@city.ac.uk

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// DO NOT EDIT starts
interface TemporaryNodeInterface {
    public boolean start(String startingNodeName, String startingNodeAddress);
    public boolean store(String key, String value);
    public String get(String key);
}
// DO NOT EDIT ends


public class TemporaryNode implements TemporaryNodeInterface {
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;

    public boolean start(String startingNodeName, String startingNodeAddress) {
        try {
            String[] addressParts = startingNodeAddress.split(":");
            if (addressParts.length != 2) {
                System.out.println("Invalid address format.");
                return false;
            }
            socket = new Socket(addressParts[0], Integer.parseInt(addressParts[1]));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            writer.write("START 1 " + startingNodeName + "\n");
            writer.flush();
            System.out.println("Response from server: " + reader.readLine());
            return true;
        } catch (IOException e) {
            System.out.println("Failed to connect: " + e.getMessage());
            return false;
        }
    }

    public boolean store(String key, String value) {
        try {
            String message = "PUT? " + key.split("\n").length + " " + value.split("\n").length + "\n" + key + "\n" + value + "\n";
            writer.write(message);
            writer.flush();
            return "SUCCESS".equals(reader.readLine());
        } catch (IOException e) {
            System.out.println("Error during storage operation: " + e.getMessage());
            return false;
        }
    }

    public String get(String key) {
        try {
            writer.write("GET? " + key.split("\n").length + "\n" + key + "\n");
            writer.flush();
            String response = reader.readLine();
            if (response != null && response.startsWith("VALUE")) {
                int numLines = Integer.parseInt(response.split(" ")[1]);
                StringBuilder value = new StringBuilder();
                for (int i = 0; i < numLines; i++) {
                    value.append(reader.readLine()).append("\n");
                }
                return value.toString().trim();
            }
            return null;
        } catch (IOException e) {
            System.out.println("Error retrieving value: " + e.getMessage());
            return null;
        }
    }

    public void findClosestNode(String hashID) {
        try {
            writer.write("NEAREST? " + hashID + "\n");
            writer.flush();
            String response;
            while ((response = reader.readLine()) != null) {
                if (response.startsWith("NODES")) {
                    int numNodes = Integer.parseInt(response.split(" ")[1]);
                    for (int i = 0; i < numNodes; i++) {
                        String nodeInfo = reader.readLine();
                        System.out.println("Closest node: " + nodeInfo);
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error finding closest node: " + e.getMessage());
        }

    }
}
