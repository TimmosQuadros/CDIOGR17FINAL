package run;

import Observer.FindAreaOfInterestSubject;
import Observer.QRCodeDetectorSubject;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.Core;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class RobotAI {

    //load the opencv library into the JVM at runtime
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        List<Point> corners;
        List<Point> goals;

        VideoCaptureSingleton videoCaptureSingleton = VideoCaptureSingleton.getInstance();

        /*FindAreaOfInterestSubject areaOfInterestSubject = new FindAreaOfInterestSubject();
        corners = areaOfInterestSubject.getCorners(videoCaptureSingleton);
        goals = areaOfInterestSubject.getGoalPos(videoCaptureSingleton);*/

        QRCodeDetectorSubject qrCodeDetectorSubject = new QRCodeDetectorSubject(videoCaptureSingleton);

        Point point = qrCodeDetectorSubject.getPos();
        System.out.println(point.x+","+point.y);
        point = qrCodeDetectorSubject.getPos();
        System.out.println(point.x+","+point.y);
        point = qrCodeDetectorSubject.getPos();
        System.out.println(point.x+","+point.y);
    }
}
