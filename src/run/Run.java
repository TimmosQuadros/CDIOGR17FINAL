package run;

import Server.Server;
import org.opencv.core.Core;

import java.io.IOException;


public class Run {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        try {
            Server server = new Server();
            Thread task1 = new Thread(server);
            task1.start();
            while (server.connected == null) {
                Thread.sleep(100);
            }
            RobotAI robotAI = new RobotAI(server);
            robotAI.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}