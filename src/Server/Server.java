package Server;

import Observer.Subject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server implements Runnable{

    public String connected = null;

    PrintWriter writer;

    public Server() {

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

    @Override
    public void run() {

        int portNumber = 4445;
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket socket = serverSocket.accept();
            System.out.println("connected");
            connected = "";
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("hello");
            writer.println();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
