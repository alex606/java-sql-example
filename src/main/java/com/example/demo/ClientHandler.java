package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectMapper objectMapper;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.objectMapper = new ObjectMapper();
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
//                System.out.println("Received message from client: " + inputLine);
                String s = "{\"id\":30,\"name\":\"John Smith\"}";
//                Message m = objectMapper.readValue(inputLine, Message.class);

                System.out.println(inputLine);
                System.out.println(s);
                Message m = objectMapper.readValue(s, Message.class);

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
