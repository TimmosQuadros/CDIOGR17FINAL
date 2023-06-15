package Navigation;

import Math1.LineCreation;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class BallTracker {

    public Point checkIfBallIsOnLine(double[] line, List<Point> balls, double threshold) {
        if (balls.size() > 0) {
            double slope = line[0];
            double begin = line[1];
            for (Point p : balls) {
                if (Math.abs((p.x * slope + begin) - p.y) < threshold) {
                    return p;
                }
            }
        }
        return null;
    }

    }

