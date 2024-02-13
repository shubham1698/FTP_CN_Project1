import java.net.*;
import java.io.*;

public class Client {
	Socket requestSocket; // socket connect to the server
	ObjectOutputStream out; // stream write to the socket
	ObjectInputStream in; // stream read from the socket
	String message; // message send to the server
	String MESSAGE; // capitalized message read from the server

	public void Client() {
	}

	void run() {
		try {
			// create a socket to connect to the server
			requestSocket = new Socket("localhost", 8000);
			System.out.println("Connected to localhost in port 8000");
			// initialize inputStream and outputStream
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());

			// get Input from standard input
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

			FileInputStream reader = new FileInputStream("uploadTestFile.pptx");

			byte[] buffer = new byte[1024];
			int bytesRead;

			while ((bytesRead = reader.read(buffer)) != -1) {
				System.out.println(bytesRead);
				out.write(buffer, 0, bytesRead);
				out.flush();
			}

			// while ((line = reader.readLine()) != null) {
			// System.out.print("Hello, please input a sentence: ");
			// // read a sentence from the standard input
			// // message = bufferedReader.readLine();
			// // Send the sentence to the server
			// sendMessage(line);
			// // Receive the upperCase sentence from the server
			// MESSAGE = (String) in.readObject();
			// // show the message to the user
			// System.out.println("Receive message: " + MESSAGE);
			// }
		} catch (ConnectException e) {
			System.err.println("Connection refused. You need to initiate a server first.");
			// } catch (ClassNotFoundException e) {
			// System.err.println("Class not found");
			// }
		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// Close connections
			try {
				in.close();
				out.close();
				requestSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	// send a message to the output stream
	void sendMessage(String msg) {
		try {
			// stream write the message
			out.writeObject(msg);
			out.flush();
			System.out.println("Send message: " + msg);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	// main method
	public static void main(String args[]) {

		// String filename = "";
		// String userAction="";
		// if (args.length == 0) {

		// } else {
		// 	System.out.println(args[0]);
		// 	String userInput=args[0].trim();
		// 	int indexOfWhitespace = userInput.indexOf(' ');
		// 	userAction = userInput.substring(0,indexOfWhitespace);
        //     filename = userInput.substring(indexOfWhitespace + 1);
		// 	System.out.println(userAction+" "+userAction);
		// }


		Client client = new Client();
		client.run();
	}

}
