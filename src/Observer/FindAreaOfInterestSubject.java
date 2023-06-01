package Observer;

import Interface.FindAreaOfInterest;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.util.List;

public class FindAreaOfInterestSubject extends Subject implements FindAreaOfInterest {


    @Override
    public List<Point> getGoalPos(VideoCaptureSingleton videoCapture) {
        return null;
    }

    @Override
    public List<Point> getCorners(VideoCaptureSingleton videoCapture) {
        return null;
    }
}
