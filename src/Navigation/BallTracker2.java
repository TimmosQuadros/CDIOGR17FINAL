package Navigation;

import org.opencv.core.Point;

import java.util.List;

/*
 * Author Mohammed Irout
 */

public class BallTracker2 {

    public Point checkIfBallIsOnLine(double slope, double begin, List<Point> balls, double threshold) {
        if (balls.size() > 0) {
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

            // Calculate the line equation based on the new robot's angle
            double slope = Math.tan(Math.toRadians(actualAngle));
            double begin = 0; // Adjust as needed

            Point ball = checkIfBallIsOnLine(slope, begin, balls, threshold);
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
