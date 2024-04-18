import java.net.*;
import java.util.Scanner;
import java.io.*;

public class PeerChat {

    static final int ALICE_PORT_NUMBER = 8000;
    static Socket remoteRequestSocket = null;
    static String message; // message send to the server
    static String MESSAGE; // capitalized message read from the server

    public static void main(String[] args) throws Exception {
        try {
            Scanner scanner = new Scanner(System.in);
            ServerSocket listener = new ServerSocket(Integer.parseInt(args[0].trim()));
            String otherUserName = args[1].trim();
            String yourName = (otherUserName.equals("Bob")) ? "Alice" : "Bob";
            System.out.println(yourName + " is running...");
            System.out.println(yourName + "'s server port number is " + args[0].trim());
            System.out.print("Hello, please input a target port number for " + otherUserName + " :");
            int bobPortNum = Integer.parseInt(scanner.nextLine()); // Reads a line of input

            remoteRequestSocket = new Socket("localhost", bobPortNum);

            System.out.println("Connected to " + otherUserName);

            try {
                while (true) {

                    new StartReaderThread(listener.accept(), args[1].trim()).start();

                    new StartWriterThread(remoteRequestSocket).start();
                    
                }
            } finally {
                listener.close();
                remoteRequestSocket.close();
                scanner.close();
            }

        } catch (Exception e) {
            System.out.println("Exception" + e.getMessage());
        }

    }

    /**
     * A handler thread class. Handlers are spawned from the listening
     * loop and are responsible for dealing with a single client's requests.
     */
    private static class StartReaderThread extends Thread {
        static Socket connection = null; // socket for the connection with the client
        static ObjectInputStream in; // stream read from the socket
        static String otherUserName = "";

        public StartReaderThread(Socket connection, String otheUserName) {
            this.connection = connection;
            this.otherUserName = otheUserName;
        }

        @Override
        public void run() {
            try {
                in = new ObjectInputStream(connection.getInputStream());
                while (true) {
                    MESSAGE = (String) in.readObject();
                    // System.out.println(otherUserName + ": " + MESSAGE);

                    if (!MESSAGE.startsWith("transfer ")) {
                        if (!MESSAGE.equals("FileTransferComplete")) {
                            System.out.println(otherUserName + ": " + MESSAGE);
                        }
                    } else {
                        System.out.println("Preparing to Receive File Transfer from Alice");
                        receiveFile();
                    }
                }
            } catch (Exception e) {

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
                System.out.println("File Received from " + otherUserName);

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

    }

    private static class StartWriterThread extends Thread {
        Socket connection = null; // socket for the connection with the client
        static ObjectOutputStream out; // stream read from the socket

        public StartWriterThread(Socket connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(connection.getOutputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String commString = bufferedReader.readLine();
                    out.writeObject(commString);
                    out.flush();
                    if (commString.startsWith("transfer")) {
                        transferFile(commString.trim().split(" ")[1]);
                    }
                }
            } catch (Exception e) {
                System.out.println("Write Exception" + e.getMessage());

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

    }

}