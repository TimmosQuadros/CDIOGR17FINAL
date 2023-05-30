package Observer;

import Interface.FindAreaOfInterest;
import org.opencv.core.Point;

import java.util.List;

public class FindAreaOfInterestSubject extends Subject implements FindAreaOfInterest {


    @Override
    public List<Point> getGoalPos() {
        //TODO Emil Implement this function
        return null;
    }

    @Override
    public List<Point> getCorners() {
        //TODO Emil Implement this function
        return null;
    }
}
