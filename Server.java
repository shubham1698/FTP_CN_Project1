import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {
	int sPort = 8000; // The server will be listening on this port number
	ServerSocket sSocket; // serversocket used to lisen on port number 8000
	Socket connection = null; // socket for the connection with the client
	String message; // message received from the client
	String MESSAGE; // uppercase message send to the client
	ObjectOutputStream out; // stream write to the socket
	ObjectInputStream in; // stream read from the socket
	FileOutputStream writer;
	public void Server() {
	}

	void run(int portNumber) {
		try {
			writer = new FileOutputStream("disassembly.pptx");
			// create a serversocket
			sSocket = new ServerSocket(portNumber, 10);
			// Wait for connection
			System.out.println("Waiting for connection");
			// accept a connection from the client
			connection = sSocket.accept();
			System.out.println("Connection received from " +
					connection.getInetAddress().getHostName());
			// initialize Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			try {

				byte[] buffer = new byte[1024];
				int bytesRead;
	
				while ((bytesRead = in.read(buffer)) != -1) {
					
					writer.write(buffer, 0, bytesRead);
				}
				// while (true) {
				// 	// receive the message sent from the client
				// 	message = (String) in.readObject();
				// 	// show the message to the user
				// 	writer.write(message);
				// 	System.out.println("Receive message: " + message);
				// 	// Capitalize all letters in the message
				// 	MESSAGE = message.toUpperCase();
				// 	// send MESSAGE back to the client
				// 	sendMessage(MESSAGE);
				// }
			} catch (Exception classnot) {
				System.err.println("Data received in unknownformat");
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// Close connections
			try {
				in.close();
				out.close();
				sSocket.close();
				writer.close();;
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	// send a message to the output stream
	void sendMessage(String msg) {
		try {
			out.writeObject(msg);
			out.flush();
			System.out.println("Send message: " + msg);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public static void main(String args[]) {

		String portNum = "";
		if (args.length == 0) {
			System.out.println("Please provide a port number for server to run on.");
		} else {
			portNum=args[0].trim();
			Server server = new Server();
			server.run(Integer.parseInt(portNum));			
		}

	}
}
