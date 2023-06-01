package Observer;

import Interface.FindAreaOfInterest;
import ObjectDetection.RedRectangleDetection;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.util.List;

public class FindAreaOfInterestSubject extends Subject implements FindAreaOfInterest {

    private RedRectangleDetection fieldDetection;

    public FindAreaOfInterestSubject(){
        fieldDetection = new RedRectangleDetection();

    }

    @Override
    public List<Point> getGoalPos(VideoCaptureSingleton videoCapture) {
        return fieldDetection.detectField(videoCapture);
    }


    @Override
    public List<Point> getCorners(VideoCaptureSingleton videoCapture) {
        return fieldDetection.determineGoalCenters();
    }
}
