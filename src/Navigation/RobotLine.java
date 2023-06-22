package Navigation;

import org.opencv.core.Point;

public class RobotLine {

    private static final double TURN_THRESHOLD = 5.0; // Threshold for turning angle in degrees

    private static double calculateAngle(Point robotPosition, Point ballLocation) {
        // Calculate the angle between the robot's position and the ball's location
        double deltaX = ballLocation.x - robotPosition.x;
        double deltaY = ballLocation.y - robotPosition.y;
        return Math.toDegrees(Math.atan2(deltaY, deltaX));
    }

    private static double calculateDistance(Point robotPosition, Point ballLocation) {
        // Calculate the distance between the robot's position and the ball's location
        double deltaX = ballLocation.x - robotPosition.x;
        double deltaY = ballLocation.y - robotPosition.y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    private static void turnRobot(double angle) {
        // Determine the direction and amount of turning based on the angle
        // Here, we assume that the robot can turn a certain number of degrees per turn

        // Check if the angle is within the turn threshold
        if (Math.abs(angle) <= TURN_THRESHOLD) {
            // Robot does not need to turn
            return;
        }

        // Determine the direction of turning (left or right)
        int direction = (angle > 0) ? 1 : -1;

        // Calculate the turning amount (in degrees)
        double turningAmount = Math.abs(angle);

        // TODO: Implement the code to turn the robot by the calculated turning amount and direction
        System.out.println("Turn the robot by " + turningAmount + " degrees in direction " + direction);
    }

    private static void moveRobot(double distance) {
        // Move the robot forward by the calculated distance

        // TODO: Implement the code to move the robot forward by the calculated distance
        System.out.println("Move the robot forward by " + distance + " units");
    }
}
