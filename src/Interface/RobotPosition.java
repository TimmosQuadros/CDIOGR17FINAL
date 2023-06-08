package Interface;

import org.opencv.core.Point;

import java.util.List;

public interface RobotPosition {
    List<Point> getPos(boolean isBlueCircle);
}