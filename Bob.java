import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Bob {
    static final int BOB_PORT_NUMBER = 8001;
    static Socket requestSocket = null;
    static ObjectInputStream in; // stream read from the socket
    static ObjectOutputStream out; // stream write to the socket
    static String message; // message send to the server
    static String MESSAGE; // capitalized message read from the server

    public static void main(String[] args) throws Exception {
        try {
            Scanner scanner = new Scanner(System.in);
            ServerSocket listener = new ServerSocket(BOB_PORT_NUMBER);

            System.out.println("Bob is running...");
            System.out.println("Bob's server port number is " + BOB_PORT_NUMBER);

            System.out.print("Hello, please input a target port number for Alice :");
            int alicePortNum = Integer.parseInt(scanner.nextLine()); // Reads a line of input
            requestSocket = new Socket("localhost", alicePortNum);
            in = new ObjectInputStream(requestSocket.getInputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        MESSAGE = (String) in.readObject();
                        if (!MESSAGE.equals("FILE")) {
                            System.out.println("Alice: " + MESSAGE);
                        } else {
                            System.out.println("Preparing to Receive File Transfer from Alice");
                            receiveFile();
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }).start();
            try {
                while (true) {
                    new ClientHandler(listener.accept()).start();
                }
            } finally {
                listener.close();
                scanner.close();
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Exception" + e.getMessage());
        }

    }

    private static void receiveFile() {
        FileOutputStream writer = null;
        try {
            // get Input from standard input
            writer = new FileOutputStream("TransferFromAlice.pptx", false);
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                if (new String(buffer, 0, bytesRead).equals("FileTransferComplete")) {
                    // File transfer complete, break out of the loop
                    break;
                }
                writer.write(buffer, 0, bytesRead);
            }
            System.out.println("File Received from Alice");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.out.println("Error closing file: " + e.getMessage());
                }
            }
        }
        return;
    }

    /**
     * A handler thread class. Handlers are spawned from the listening
     * loop and are responsible for dealing with a single client's requests.
     */
    private static class ClientHandler extends Thread {
        Socket connection = null; // socket for the connection with the client

        public ClientHandler(Socket connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                System.out.println("Connection received from " + connection.getInetAddress().getHostName()
                        + "Client Id :Client");
                out = new ObjectOutputStream(connection.getOutputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    out.writeObject(bufferedReader.readLine());
                    out.flush();
                }

            } catch (IOException ioException) {
                ioException.printStackTrace();

            } finally {
                try {
                    out.close();
                    connection.close();
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }

    }
}