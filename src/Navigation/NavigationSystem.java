package Navigation;

import Interface.Navigation;
import org.opencv.core.Point;

public class NavigationSystem implements Navigation {

    @Override
    public void goTo(Point src, Point dst, double direction) {
        // Calculate the target angle based on the source and destination points
        double targetAngle = Math.atan2(dst.y - src.y, dst.x - src.x) * 180 / Math.PI;

        // Adjust the target angle to be within the range of [0, 360)
        targetAngle = (targetAngle + 360) % 360;

        // Determine the angle difference between the current direction and the target angle
        double angleDifference = targetAngle - direction;

        // Normalize the angle difference to be within the range of [-180, 180)
        if (angleDifference < -180)
            angleDifference += 360;
        else if (angleDifference >= 180)
            angleDifference -= 360;

        // Turn to face the target direction
        turn(angleDifference);

        // Calculate the distance between the source and destination points
        double distance = Math.sqrt(Math.pow(dst.x - src.x, 2) + Math.pow(dst.y - src.y, 2));

        // Move forward by the calculated distance
        move(distance);

        // Update the direction to face the destination point
        direction = targetAngle;

        // Turn to face the destination point
        turn(direction - targetAngle);
    }

    private void turn(double angle) {
        // Code to turn the robot to the specified angle
        // Implementation depends on the specific hardware and control mechanism
    }

    private void move(double distance) {
        // Code to move the robot forward by the specified distance
        // Implementation depends on the specific hardware and control mechanism
    }
}
