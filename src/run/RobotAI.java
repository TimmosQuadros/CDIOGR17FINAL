package run;

import Observer.FindAreaOfInterestSubject;
import Observer.QRCodeDetectorSubject;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

public class RobotAI {

    private List<Point> corners;
    private List<Point> goals;


    public RobotAI(){
        FindAreaOfInterestSubject areaOfInterestSubject = new FindAreaOfInterestSubject();
        corners = areaOfInterestSubject.getCorners();
        goals = areaOfInterestSubject.getGoalPos();
    }

    public void run() {
        QRCodeDetectorSubject qrCodeDetectorSubject = new QRCodeDetectorSubject(VideoCaptureSingleton.getInstance());

        Point point = qrCodeDetectorSubject.getPos();
        System.out.println(point.x+","+point.y);
        point = qrCodeDetectorSubject.getPos();
        System.out.println(point.x+","+point.y);
        point = qrCodeDetectorSubject.getPos();
        System.out.println(point.x+","+point.y);
    }
}
