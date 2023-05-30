package Interface;

import org.opencv.core.Point;

import java.util.List;

public interface FindAreaOfInterest {

    public List<Point> getGoalPos();
    public List<Point> getCorners();
}
