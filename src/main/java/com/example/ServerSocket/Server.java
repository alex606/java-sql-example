package com.example.ServerSocket;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {


	public static void main(String[] args) throws IOException {

		int port = 12345;
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("Server listening on port " + port);


		while (true) {
			Socket clientSocket = serverSocket.accept();
			System.out.println("Received connection request from a client.");

			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			out.println("Connection request accepted by server...");

			Thread clientThread = new Thread(new ClientHandler(clientSocket));
			clientThread.start();
		}
	}
}