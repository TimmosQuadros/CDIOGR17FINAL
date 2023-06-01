package Interface;

import Singleton.VideoCaptureSingleton;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.util.List;

public interface FindAreaOfInterest {

    public List<Point> getGoalPos(VideoCaptureSingleton videoCapture);
    public List<Point> getCorners(VideoCaptureSingleton videoCapture);
}
