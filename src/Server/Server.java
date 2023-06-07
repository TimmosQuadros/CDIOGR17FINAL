package Server;

import Observer.Subject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server extends Subject {


    PrintWriter writer;

    public Server() throws IOException {
        int portNumber = 4445;
        ServerSocket serverSocket = new ServerSocket(portNumber);
        while(true) {
            Socket socket = serverSocket.accept();
            System.out.println("connected");
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("hello");
            writer.println();
            if(socket.isClosed()) {
                System.out.println("connection closed: "+socket.getRemoteSocketAddress());
                break;
            }
        }

        serverSocket.close();
    }

    public void writeMessage(String message){
        writer.println(message);
    }

    /**
     * Blocking call!!!
     * @param socket
     * @return
     * @throws IOException
     */
    public String receiveMessage(Socket socket) throws IOException {
        Scanner input = new Scanner(socket.getInputStream());
        //Waits for input
        String message = input.nextLine();
        return message;
    }
}
