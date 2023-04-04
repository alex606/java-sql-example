package com.example.demo;


import org.bson.Document;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;

public class Server {


	public static void main(String[] args) throws IOException, SQLException {

		int port = 12345;
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("Server listening on port " + port);

		MongoDbPersistenceService mongoDbPersistenceService = new MongoDbPersistenceService();

		Document filter1 = new Document("id", 1);
		Document searchDocument1 = mongoDbPersistenceService.findDocument(filter1);
		Document filter2 = new Document("pages", 0);
		Document searchDocument2 = mongoDbPersistenceService.findDocument(filter2);

		mongoDbPersistenceService.checkOutItem(filter1, "Mary");
		mongoDbPersistenceService.checkOutItem(filter2, "John");

		mongoDbPersistenceService.checkInItem(filter1, "Mary");
		mongoDbPersistenceService.checkInItem(filter2, "John");

		mongoDbPersistenceService.closeConnection();

//		while (true) {
//			Socket clientSocket = serverSocket.accept();
//			System.out.println("Client connected: " + clientSocket.getInetAddress().getHostName());
//
//			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
//			out.println("Hello, client!");
//
//			Thread clientThread = new Thread(new ClientHandler(clientSocket));
//			clientThread.start();
//		}
	}
}