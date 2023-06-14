/*package Backup;

import ObjectDetection.RedRectangleDetection;
import org.opencv.core.Point;

import java.util.List;

public class BackupSystem2 {
    private RedRectangleDetection redRectangleDetection;
    private boolean isBackupMode = false;

    public BackupSystem2() {
        redRectangleDetection = new RedRectangleDetection();
    }

    public void run() {
        // Detect the field and initialize the backup system
        redRectangleDetection.detectField();
        List<Point> fieldCorners = redRectangleDetection.getFloorCorners();
        List<Point> goalCenters = redRectangleDetection.getGoals();

        // Enter the main loop
        while (true) {
            if (!isBackupMode) {
                // Normal operation mode
                // Perform your regular operations here using the original program logic
                // ...

                // Check if something goes wrong
                boolean isSomethingWrong = checkForErrors();

                if (isSomethingWrong) {
                    // Something went wrong, switch to backup mode
                    isBackupMode = true;
                    System.out.println("Switching to backup mode");
                }
            } else {
                // Backup mode
                System.out.println("Backup mode activated");

                // Move the robot in a specific route to catch any extra balls
                moveRobotInBackupRoute();

                // Score any caught balls
                scoreCaughtBalls();

                // Return to normal operation mode
                isBackupMode = false;
                System.out.println("Switching back to normal operation mode");
            }
        }
    }

    private boolean checkForErrors() {
        // Check if there are any errors or disruptions in the original program
        // Return true if something is wrong, false otherwise
        // ...
        return false;
    }

    private void moveRobotInBackupRoute() {
        // Move the robot in a specific route to catch any extra balls
        // Implement the backup route logic here
        // ...
    }

    private void scoreCaughtBalls() {
        // Score any balls caught during the backup mode
        // Implement the logic to score the balls here
        // ...
    }

    public static void main(String[] args) {
        BackupSystem2 backupSystem = new BackupSystem2();
        backupSystem.run();
    }
}

 */
