# FTP_CN_Project1
Project on FTP

Instructions for compiling and running the client-server application written in Java:
---

# Prerequisites:
- Java Development Kit (JDK) installed on your system. This application was tested with JDK version 11, but it should be compatible with other versions as well.

# Compiling the Code:

# 1. Open Terminal/Command Prompt:
   - Open a terminal or command prompt on your system.

# 2. **Navigate to Code Directory:**
   - Navigate to the directory containing the `Server.java` and `Client.java` files.

# 3. **Compile Server Code:**
   - Run the following commands to compile the Server code:
     
     javac Server.java
     

# 4. **Compile Client Code:**
   - Run the following commands to compile the Client code:
     
     javac Client.java
     

# **Running the Application:**

# 1. **Start the Server:**
   - After compiling the Server code, run the Server application with the desired port number:
     
     java Server <port_number>
     

# 2. **Start the Client:**
   - After compiling the Client code, run the Client application with the desired port number:
     
     java Client <port_number>
     

# 3. **Interact with the Server:**
   - Follow the on-screen prompts to interact with the server.
   - The client supports commands like "get <filename>", "upload <filename>", and "exit" to terminate the connection.

---