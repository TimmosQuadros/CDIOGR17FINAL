package ServerLogic;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;

/*
 * Author Emil Iversen
 */

public class Server implements KeyListener{
	PrintWriter writer;
	
public Server() {
	// Create a JFrame for the server GUI
	JFrame frame = new JFrame();
    frame.setSize(300, 300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.addKeyListener(this);
	try {
		int portNumber = 4445;
		ServerSocket serverSocket = new ServerSocket(portNumber);
		while(true) {

			// Wait for a client connection
			Socket socket = serverSocket.accept();
			System.out.println("connected");

			// Create a PrintWriter to send messages to the client
			writer = new PrintWriter(socket.getOutputStream(), true);

			// Send a greeting message to the client
			writer.println("hello");
			writer.println();

			// Check if the socket is closed and break the loop if it is
			if(socket.isClosed()) {
				System.out.println("connection closed: "+socket.getRemoteSocketAddress());
				break;
			}
		}
		
		
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//serverSocket.bind(new InetSocketAddress(ip, portNumber));
	
	//System.out.println("connected to client: " + socket.getRemoteSocketAddress().toString());
	//PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
}
	
	public String message = "";
	
    public static void main(String[] args) throws IOException {
        Server server = new Server();

       /* try {
        	while(true) {
        		
    			
    			writer.println("hello!");
    			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    			while (true) {
    				while(!socket.isClosed()) {
    					String msg=reader.readLine();
    					writer.println(msg);
        				System.out.println(msg);
    				}
    				if(socket.isClosed()) {
    					System.out.println("connection closed: "+socket.getRemoteSocketAddress());
    					break;
    				}
    			}
        	}
        	
		} catch (IOException e) {
			// TODO: handle exception
		}*/
    }

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		// Send commands based on the pressed key
		if(e.getKeyChar()=='w') {
			writer.println("MoveForward");
		}else if(e.getKeyChar()=='s') {
			writer.println("MoveBackward");
		}else if(e.getKeyChar()=='a') {
			writer.println("MoveLeft");
		}else if(e.getKeyChar()=='d') {
			writer.println("MoveRight");
		}else if(e.getKeyChar()=='p') {
			writer.println("StartMotor");
		}else if(e.getKeyChar()=='o') {
			writer.println("StopMotor");
		}else if(e.getKeyChar()=='å') {
			writer.println("ReverseMotor");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		// Send stop commands based on the released key
		if(e.getKeyChar()=='w') {
			writer.println("StopMoveForward");
		}else if(e.getKeyChar()=='s') {
			writer.println("StopMoveBackward");
		}else if(e.getKeyChar()=='a') {
			writer.println("StopMoveLeft");
		}else if(e.getKeyChar()=='d') {
			writer.println("StopMoveRight");
		}
	}
}

