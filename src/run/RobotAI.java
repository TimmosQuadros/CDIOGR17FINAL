package run;

import LineCreation.AlignRobot;
import Observer.FindAreaOfInterestSubject;
import Observer.QRCodeDetectorSubject;
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
        alignRobot = new AlignRobot(corners);
    }

    public void run() {
        QRCodeDetectorSubject qrCodeDetectorSubject = new QRCodeDetectorSubject(VideoCaptureSingleton.getInstance());

        /*Point point = qrCodeDetectorSubject.getPos();
        System.out.println(point.x+","+point.y);
        point = qrCodeDetectorSubject.getPos();
        System.out.println(point.x+","+point.y);
        point = qrCodeDetectorSubject.getPos();
        System.out.println(point.x+","+point.y);*/
    }
}
