/*package Backup;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RedRectangleDetection {
    private final int frameWidth = 1920;
    private final int frameHeight = 1080;
    private Mat frame;
    // Other existing variables and methods...

    // Backup system methods

    public void startBackupSystem() {
        // Start the backup system
        Timer timer = new Timer();
        timer.schedule(new BackupTask(), 0, 5000); // Backup every 5 seconds
    }

    private void backupFrame(Mat frame) {
        // Backup the frame to the defined storage
        String backupPath = "/path/to/backup"; // Set the backup directory path
        String fileName = "frame_" + System.currentTimeMillis() + ".png"; // Generate a unique file name
        String filePath = backupPath + "/" + fileName; // Create the full file path

        // Save the frame as an image file
        Imgcodecs.imwrite(filePath, frame);
        System.out.println("Frame saved: " + filePath);
    }

    private class BackupTask extends TimerTask {
        @Override
        public void run() {
            retrieveFrame();
            backupFrame(frame);
        }
    }

    public static void main(String[] args) {
        // Initialize OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Create an instance of RedRectangleDetection
        RedRectangleDetection redRectangleDetection = new RedRectangleDetection();

        // Test the red rectangle detection
        redRectangleDetection.testRedRectangleDetection();

        // Start the backup system
        redRectangleDetection.startBackupSystem();
    }
}


 */