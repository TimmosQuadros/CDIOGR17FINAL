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

    public Point trackBalls(List<Point> balls, double givenAngleDistance) {
        double threshold = 0.1;
        int[] degreesToTurn = {90, 60, 30, 15, 10, 5, 2, 1};
        int currentDegreeIndex = 0;
        int maxDegreeIndex = degreesToTurn.length - 1;
        int maxAttempts = 3;
        int currentAttempts = 0;

        while (currentDegreeIndex <= maxDegreeIndex) {
            int degrees = degreesToTurn[currentDegreeIndex];
            System.out.println("Turning " + degrees + " degrees...");

            // Calculate the actual angle to turn based on givenAngleDistance
            double actualAngle = degrees * givenAngleDistance;

            // TODO: Code to turn the robot by 'actualAngle' here
/*
            Point ball = checkIfBallIsOnLine(line, balls, threshold);
            if (ball != null) {
                System.out.println("Ball found at " + ball.x + ", " + ball.y);
                return ball;
            }

            currentAttempts++;
            if (currentAttempts >= maxAttempts) {
                currentAttempts = 0;
                currentDegreeIndex++;
            }
        }

        System.out.println("No balls found. Moving to a new position...");
        // TODO: Code to move the robot to a new position here

        return null;
    }
}


 */
        }
        return null; //Den skal fjernes, den er der så der ikke er fejl i koden efter udkommenteringen.
    }
}