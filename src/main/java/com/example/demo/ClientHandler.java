package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Objects;


class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectMapper objectMapper;
    private PrintWriter out;
    private MongoDbPersistenceService mongoDbPersistenceService;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.objectMapper = new ObjectMapper();
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.mongoDbPersistenceService = new MongoDbPersistenceService();
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            Message message;
            List<String> items;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received message from client: " + inputLine);
                message = objectMapper.readValue(inputLine, Message.class);

                if (Objects.equals(message.getType(), "BROWSE")) {
                    items = mongoDbPersistenceService.getAllItems();
                    message.setCollection(items);
                    out.println(objectMapper.writeValueAsString(message));
                } else if (Objects.equals(message.getType(), "CHECKOUT")) {
                    items = mongoDbPersistenceService.checkOutItem(new Document("id", message.getId()), message.getData());
                    message.setCollection(items);
                    out.println(objectMapper.writeValueAsString(message));
                } else if (Objects.equals(message.getType(), "CHECKIN")) {
                    items = mongoDbPersistenceService.checkInItem(new Document("id", message.getId()), message.getData());
                    message.setCollection(items);
                    out.println(objectMapper.writeValueAsString(message));
                } else {
                    message.setData("Unknown Command was sent in: " + message.getType());
                    out.println(objectMapper.writeValueAsString(message));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
