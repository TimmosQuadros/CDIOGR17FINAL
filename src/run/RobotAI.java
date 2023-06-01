package run;

import Observer.FindAreaOfInterestSubject;
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

        FindAreaOfInterestSubject areaOfInterestSubject = new FindAreaOfInterestSubject();
        corners = areaOfInterestSubject.getCorners(videoCaptureSingleton);
        goals = areaOfInterestSubject.getGoalPos(videoCaptureSingleton);
    }
}
