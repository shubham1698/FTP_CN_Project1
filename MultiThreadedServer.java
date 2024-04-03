
import java.net.*;
import java.io.*;

public class MultiThreadedServer {

    public static void main(String[] args) throws Exception {
        System.out.println("The server is running.");

        String portNum = "";
        if (args.length == 0) {
            System.out.println("Please provide a port number for server to run on.");
        } else {
            portNum = args[0].trim();
            ServerSocket listener = new ServerSocket(Integer.parseInt(portNum));

            int clientNum = 1;
            try {
                while (true) {
                    new ClientHandler(listener.accept(), clientNum).start();
                    System.out.println("Client " + clientNum + " isconnected!");
                    clientNum++;
                }
            } finally {
                listener.close();
            }
        }
    }

    /**
     * A handler thread class. Handlers are spawned from the listening
     * loop and are responsible for dealing with a single client's requests.
     */
    private static class ClientHandler extends Thread {
        Socket connection = null; // socket for the connection with the client
        ObjectOutputStream out; // stream write to the socket
        ObjectInputStream in; // stream read from the socket
        private int clientNo; // The index number of the client

        public ClientHandler(Socket connection, int clientNo) {
            this.connection = connection;
            this.clientNo = clientNo;
        }

        // void run(int portNumber)
        @Override
        public void run() {
            try {
                System.out.println("Connection received from " + connection.getInetAddress().getHostName()
                        + "Client Id :Client" + clientNo);

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
                            System.out.println("Client"+clientNo+" disconnected");
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

    }
}