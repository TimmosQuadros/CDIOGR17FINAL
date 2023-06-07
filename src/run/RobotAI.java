package run;

import LineCreation.AlignRobot;
import Observer.FindAreaOfInterestSubject;
import Observer.HoughCircleDetectorSubject;
import Observer.QRCodeDetectorSubject;
import Observer.RobotPositionSubject;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

public class RobotAI {

    //Holds the corner in clock direction from top left to bottom left.
    private List<Point> corners;
    private List<Point> goals;
    private AlignRobot alignRobot;


    public RobotAI(){
        FindAreaOfInterestSubject areaOfInterestSubject = new FindAreaOfInterestSubject();
        corners = areaOfInterestSubject.getCorners();
        goals = areaOfInterestSubject.getGoalPos();
        //areaOfInterestSubject.detectRobot();
        //alignRobot = new AlignRobot(corners);
    }

    public void run() {
        RobotPositionSubject robotPositionSubject = new RobotPositionSubject();
        HoughCircleDetectorSubject houghCircleDetectorSubject = new HoughCircleDetectorSubject();

        List<Point> points = houghCircleDetectorSubject.getBalls();

        System.out.println(points.toString());


        Point point = robotPositionSubject.getPos().get(0);
        System.out.println(point.x+","+point.y);


    }
}
