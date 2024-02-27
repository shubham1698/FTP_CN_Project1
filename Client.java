import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Client {

	String message; // message send to the server
	String MESSAGE; // capitalized message read from the server

	public void Client() {
	}

	void uploadFile(
			Socket requestSocket,
			ObjectOutputStream out,
			ObjectInputStream in,
			String fileName) {
		try {
			// get Input from standard input
			FileInputStream reader = new FileInputStream(fileName);
			out.writeObject("2 " + fileName);
			byte[] buffer = new byte[1024];
			int bytesRead;

			while ((bytesRead = reader.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				out.flush();
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	void downloadFile(
			Socket requestSocket,
			ObjectOutputStream out,
			ObjectInputStream in,
			String fileName) {
		try {
			// get Input from standard input
			FileOutputStream writer = new FileOutputStream("new" + fileName, false);

			out.writeObject("1 " + fileName);

			byte[] buffer = new byte[1024];
			int bytesRead;

			while ((bytesRead = in.read(buffer)) != -1) {
				if (new String(buffer, 0, bytesRead).equals("FileTransferComplete")) {
					// File transfer complete, break out of the loop
					break;
				}
				writer.write(buffer, 0, bytesRead);
			}
			System.out.println("reached");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// main method
	public static void main(String args[]) {
		Socket requestSocket=null; // socket connect to the server
			ObjectOutputStream out=null; // stream write to the socket
			ObjectInputStream in=null; // stream read from the socket
		try {
			
			String portNum = "";
			if (args.length == 0) {
				System.out.println("Please provide a port number for client.");
			} else {
				portNum = args[0].trim();
				Scanner scanner = new Scanner(System.in);
				boolean exit = false;
				requestSocket = new Socket("localhost", Integer.parseInt(portNum));
				System.out.println("Connected to localhost in port 8000");
				// initialize inputStream and outputStream
				out = new ObjectOutputStream(requestSocket.getOutputStream());
				in = new ObjectInputStream(requestSocket.getInputStream());
				Client client = new Client();

				while (!exit) {
					System.out.println("Select an option:");
					System.out.println("1. Download File(get <File-name>)");
					System.out.println("2. Upload File(upload <File-name>)");
					System.out.println("3. Exit");

					int choice = scanner.nextInt();
					scanner.nextLine();
					switch (choice) {
						case 1: {
							System.out.println("Enter the download command");
							String downloadCommand = scanner.nextLine();
							String fileName = downloadCommand.trim().split("\\s+")[1];
							client.downloadFile(requestSocket, out, in, fileName);
						}
							break;
						case 2: {
							System.out.println("Enter the upload command");
							String uploadCommand = scanner.nextLine();
							String fileName = uploadCommand.trim().split("\\s+")[1];
							client.uploadFile(requestSocket, out, in, fileName);
						}
							break;
						case 3: {
							System.out.println("Exiting...");
							exit = true;
							out.writeObject("3 Exit");
						}
							break;
						default:
							System.out.println("Invalid choice. Please select again.");
					}
				}
				out.close();
				in.close();
				requestSocket.close();
				scanner.close();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}finally {
			try {
				if (out != null) out.close(); // Close streams in the finally block to ensure they are always closed
				if (in != null) in.close();
				if (requestSocket != null) requestSocket.close(); // Close socket in the finally block to ensure it's always closed
			} catch (IOException e) {
				System.out.println("Error closing resources: " + e.getMessage());
			}
		}
	}

}
