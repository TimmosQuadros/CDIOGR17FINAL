package Interface;

import Singleton.VideoCaptureSingleton;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.util.List;

public interface FindAreaOfInterest {

    public List<Point> getGoalPos();
    public List<Point> getCorners();
    public List<Point> getCross();
    public double getScaleFactor();

}
