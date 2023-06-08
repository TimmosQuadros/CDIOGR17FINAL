package run;

import IncodeMessage.Incoder;
import LineCreation.AlignRobot;
import LineCreation.LineSegment;
import Observer.FindAreaOfInterestSubject;
import Observer.HoughCircleDetectorSubject;
import Observer.QRCodeDetectorSubject;
import Observer.RobotPositionSubject;
import Server.Server;
import Singleton.VideoCaptureSingleton;
import Vectors.VectorCalculations;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RobotAI {

    //Holds the corner in clock direction from top left to bottom left.
    private List<Point> corners;
    private List<Point> goals;
    private AlignRobot alignRobot;
    private Incoder incoder = new Incoder();
    private Server server;


    public RobotAI(Server server){
        //FindAreaOfInterestSubject areaOfInterestSubject = new FindAreaOfInterestSubject();
        //corners = areaOfInterestSubject.getCorners();
        //goals = areaOfInterestSubject.getGoalPos();
        //areaOfInterestSubject.detectRobot();
        //alignRobot = new AlignRobot(corners);
        this.server = server;

    }

    public void run() {
        while(true){
        RobotPositionSubject robotPositionSubject = new RobotPositionSubject();
        HoughCircleDetectorSubject houghCircleDetectorSubject = new HoughCircleDetectorSubject();

        List<Point> ballPositions = houghCircleDetectorSubject.getBalls();
        List<Point> robotPos = robotPositionSubject.getPos();

        VectorCalculations vectorCalculations = new VectorCalculations(new LineSegment(robotPos.get(0),robotPos.get(1)),ballPositions.get(0));

        String balls = incoder.ballPositions(ballPositions);
        String robotPos1 = incoder.starPos(robotPos.get(0));
        String startAngle = incoder.startAngle(vectorCalculations.getAngle());
        String scaleFactor = incoder.scaleFactor(6.8);

        //server.writeMessage(balls);
        //server.writeMessage(robotPos1);
        //server.writeMessage(startAngle);
        //server.writeMessage(scaleFactor);

        //System.out.println(ballPositions.toString());
        server.writeMessage("Abe");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



    }
}
