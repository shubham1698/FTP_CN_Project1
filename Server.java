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

	public void Server() {
	}

	void run(int portNumber) {
		try {

			// create a serversocket
			sSocket = new ServerSocket(portNumber, 10);
			// Wait for connection
			System.out.println("Waiting for connection");
			// accept a connection from the client
			connection = sSocket.accept();
			System.out.println("Connection received from " + connection.getInetAddress().getHostName());

			// initialize Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			in = new ObjectInputStream(connection.getInputStream());

			try {
				boolean exit = true;
				while (exit) {
					String command = (String) in.readObject();
					
					if (command.trim().split("\\s+")[0].equals("2")) {
						handleUploadFile(command.trim().split("\\s+")[1], in, out);
					} else if (command.trim().split("\\s+")[0].equals("1")) {
						handleDownloadFile(command.trim().split("\\s+")[1], in, out);
					} else {
						System.out.println("Server Shutting down...");
						exit = false;
					}
				}
				in.close();
				out.close();
				connection.close();
			} catch (Exception classnot) {
				System.err.println("Data received in unknownformat" + classnot);
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// Close resources (streams and socket) in the finally block
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				if (connection != null)
					connection.close(); // Close the connection (socket)
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void handleDownloadFile(String filename, ObjectInputStream in, ObjectOutputStream out) {
		try {
			FileInputStream reader = new FileInputStream(filename);
			byte[] buffer = new byte[1024];
			int bytesRead;

			while ((bytesRead = reader.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				out.flush();
			}
			out.writeObject("FileTransferComplete");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
	}

	void handleUploadFile(String filename, ObjectInputStream in, ObjectOutputStream out) {
		try {
			FileOutputStream writer = new FileOutputStream("new" + filename, false);

			byte[] buffer = new byte[1024];
			int bytesRead;

			while ((bytesRead = in.read(buffer)) != -1) {
				writer.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void main(String args[]) {

		String portNum = "";
		if (args.length == 0) {
			System.out.println("Please provide a port number for server to run on.");
		} else {
			portNum = args[0].trim();
			Server server = new Server();
			server.run(Integer.parseInt(portNum));
		}

	}
}
