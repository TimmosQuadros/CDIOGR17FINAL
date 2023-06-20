package Interface;

import LineCreation.Circle;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.util.List;

public interface FindAreaOfInterest {

    public List<Point> getGoalPos();
    public Point[] getCorners();
    public Circle getCross();
    public Circle getScaleAdjustedCross();
    public double getScaleFactor();

}
