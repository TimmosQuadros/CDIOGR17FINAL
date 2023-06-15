package run;

import Server.Server;
import org.opencv.core.Core;

import java.io.IOException;


public class Run {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        try {
            Server server = Server.getServer();
            RobotAI robotAI = new RobotAI(server);
            Thread task1 = new Thread(server);
            task1.start();
            //busy wait until robot is connected
            while (server.getConnected() == null) {
                Thread.sleep(100);
            }
            //TODO Make a serverHandler
            //robotAI.run();

            while(true){
                String mes = server.receiveMessage();
                if(mes!=null){

                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

    }
}