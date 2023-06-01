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

    //load the opencv library into the JVM at runtime



    public static void main(String[] args) {
        List<Point> corners;
        List<Point> goals;
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture videoCapture = VideoCaptureSingleton.getInstance().getVideoCapture();

        FindAreaOfInterestSubject areaOfInterestSubject = new FindAreaOfInterestSubject();
        corners = areaOfInterestSubject.getCorners();
        goals = areaOfInterestSubject.getGoalPos();

        QRCodeDetectorSubject qrCodeDetectorSubject = new QRCodeDetectorSubject(VideoCaptureSingleton.getInstance());

        Point point = qrCodeDetectorSubject.getPos();
        System.out.println(point.x+","+point.y);
        point = qrCodeDetectorSubject.getPos();
        System.out.println(point.x+","+point.y);
        point = qrCodeDetectorSubject.getPos();
        System.out.println(point.x+","+point.y);
    }
}
