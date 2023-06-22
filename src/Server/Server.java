package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

/*
 * Author Timm Daniel Rasmussen.
 */

public class Server implements Runnable{

    public String getConnected() {
        return connected;
    }

    private String connected = null;

    private PrintWriter writer;
    private Scanner input;
    private static Server server;
    private Socket socket;

    private Server() {

    }

    public static Server getServer(){
        if(server==null){
            server = new Server();
        }
        return server;
    }

    public void writeMessage(String message1){
        Thread thread = new Thread(() -> {
            writer.println(message1);
        });
        thread.start();
    }

    public String receiveMessage(){
        try {
            input = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] mes = {""};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Make sure that there is something to receive otherwise busy wait
                while(!input.hasNextLine()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(input.hasNextLine())
                    mes[0] = input.nextLine();
            }
        });
        //Start the thread
        thread.start();
        //Wait for the thread to finish
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mes[0];
    }

    @Override
    public void run() {
        int portNumber = 4445;
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            socket = serverSocket.accept();
            System.out.println("connected");
            connected = "";
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
