package Interface;

import org.opencv.core.Point;

import java.util.List;

/*
 * Author Timm Daniel Rasmussen.
 */

public interface RobotPosition {
    List<Point> getPos(boolean isBlueCircle);
}