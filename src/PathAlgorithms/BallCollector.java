package PathAlgorithms;

import Navigation.NavigationSystem;
import Server.Server;
import org.opencv.core.Point;
import java.util.ArrayList;
import java.util.List;

/*
 * Author Mohammed Irout.
 */

public class BallCollector {
    private List<Point> ballPositions;
    private int capturedBalls;
    private int remainingBalls;
    private NavigationSystem navigationSystem;
    private Server server;

    public BallCollector() {
        ballPositions = new ArrayList<>();
        capturedBalls = 0;
        remainingBalls = 0;
        navigationSystem = new NavigationSystem();
        server = Server.getServer();
    }

    public void start() {
        // Start the camera or video capture
        // Initialize the server and establish communication

        // Main loop
        while (true) {
            // Step 2: Locate and track the balls
            locateBalls();

            // Step 3: Determine the order of ball collection
            determineCollectionOrder();

            // Step 4: Navigate the robot and collect the balls
            navigateAndCollectBalls();

            // Step 7: Handle communication with the server
            sendUpdatesToServer();

            // Check if all balls have been captured or any other condition to end the loop
            if (capturedBalls == ballPositions.size()) {
                break;
            }
        }

        // Clean up and release resources (e.g., stop the camera, close the server connection)
    }

    private void locateBalls() {
        // Perform ball detection and tracking using image processing techniques
        // Store the positions of the detected balls in the ballPositions list

        // Get the robot's position from the CircleDetector class
    //    Point robotPosition = CircleDetector.getRobotPosition();
        // Use the robot's position for further processing or tracking
    }

    private void determineCollectionOrder() {
        // Implement the logic to determine the order in which the balls should be collected
        // This could be based on proximity, predefined sequence, or any other criteria
    }

    private void navigateAndCollectBalls() {
        // Get the initial robot position
    //    Point robotPosition = CircleDetector.getRobotPosition();

        // Iterate through the ballPositions list and navigate to each ball's position
        for (Point ballPosition : ballPositions) {
            // Step 4a: Navigate the robot to the ball's position
    //        navigationSystem.goTo(robotPosition, ballPosition);

            // Step 4b: Collect the ball
            collectBall();

            // Update the counters for captured and remaining balls
            capturedBalls++;
            remainingBalls--;

            // Update the robot's position to the current ball's position
    //        robotPosition = ballPosition;
        }
    }


    private void collectBall() {
        // Implement the mechanism to collect the ball (e.g., gripper, suction device)
    }

    private void sendUpdatesToServer() {
        // Send relevant information about the remaining balls, captured balls, or any other updates to the server
        String message = "Remaining Balls: " + remainingBalls + ", Captured Balls: " + capturedBalls;
        server.writeMessage(message);
    }
}
