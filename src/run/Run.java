package run;

import IncodeMessage.MessageStrings;
import Observer.RobotPositionSubject;
import Server.Server;
import org.opencv.core.Core;
import org.opencv.core.Point;

import java.io.IOException;
import java.util.List;
import java.util.Timer;

/*
 * Author Timm Daniel Rasmussen.
 */

public class Run {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        try {
            Server server = Server.getServer();
            RobotAI robotAI = new RobotAI(server);
            Thread task1 = new Thread(server);
            task1.start();
            //busy wait until robot is connected
            //robotAI.run();
            while (server.getConnected() == null) {
                Thread.sleep(100);
            }
            //run the robot
            robotAI.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}