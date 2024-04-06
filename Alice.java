import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Alice {

    static final int ALICE_PORT_NUMBER = 8000;
    static Socket requestSocket = null;
    static ObjectInputStream in; // stream read from the socket
    static ObjectOutputStream out; // stream write to the socket
    static String message; // message send to the server
    static String MESSAGE; // capitalized message read from the server

    public static void main(String[] args) throws Exception {
        try {
            Scanner scanner = new Scanner(System.in);
            ServerSocket listener = new ServerSocket(ALICE_PORT_NUMBER);

            System.out.println("Alice is running...");
            System.out.println("Alice's server port number is " + ALICE_PORT_NUMBER);

            System.out.print("Hello, please input a target port number for Bob :");
            int bobPortNum = Integer.parseInt(scanner.nextLine()); // Reads a line of input
            requestSocket = new Socket("localhost", bobPortNum);

            new Thread(() -> {
                try {
                    in = new ObjectInputStream(requestSocket.getInputStream());
                    while (true) {
                        MESSAGE = (String) in.readObject();
                        if (MESSAGE.contains("transfer filename")) {
                            System.out.println("File Transfer to Bob initiated");
                            out.reset();
                            out.writeObject("FILE");
                            out.flush();
                            transferFile(MESSAGE.trim().split(" ")[2]);
                        } else {
                            System.out.println("Bob: " + MESSAGE);
                        }
                    }
                } catch (Exception e) {
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
            System.out.println("Exception" + e.getMessage());
        }

    }

    static void transferFile(String fileName) {
        FileInputStream reader = null;
        try {
            reader = new FileInputStream(fileName);
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = reader.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
            out.writeObject("FileTransferComplete");
            System.out.println("File Transfer to Bob Completed");
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Error closing file: " + e.getMessage());
                }
            }
        }
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

                // initialize Input and Output streams
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
                }

            }
        }

    }
}