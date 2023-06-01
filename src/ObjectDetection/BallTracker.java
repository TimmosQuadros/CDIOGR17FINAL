/*package ObjectDetection;

import org.opencv.core.Core;
        import org.opencv.core.Mat;
        import org.opencv.core.MatOfPoint2f;
        import org.opencv.core.Point;
        import org.opencv.core.Scalar;
        import org.opencv.core.Size;
        import org.opencv.imgproc.Imgproc;
        import org.opencv.videoio.VideoCapture;

public class BallTracker {
    private static int totalBalls = 0;
    private static int capturedBalls = 0;
    private static int remainingBalls = 0;

    public static void main(String[] args) {
        // Initialize OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Initialize video capture from the camera
        VideoCapture videoCapture = new VideoCapture(0);

        // Check if video capture is successful
        if (!videoCapture.isOpened()) {
            System.out.println("Failed to open video capture device.");
            return;
        }

        // Create a window to display the video feed
        //OpenCVFrameViewer frameViewer = new OpenCVFrameViewer("Ball Tracker");

        // Main loop
        while (true) {
            // Read the current frame from the video capture
            Mat frame = new Mat();
            videoCapture.read(frame);

            // Perform ball detection and tracking here
            // ...

            // Update the number of remaining balls
            remainingBalls = totalBalls - capturedBalls;

            // Display the remaining balls count
            frameViewer.showImage(frame, "Remaining Balls: " + remainingBalls);

            // Break the loop if the 'Esc' key is pressed
            if (frameViewer.waitKey(1) == 27)
                break;
        }

        // Release the video capture and close the window
        videoCapture.release();
        frameViewer.dispose();
    }
}

 */
package ObjectDetection;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class BallTracker {
    private static int totalBalls = 0;
    private static int capturedBalls = 0;
    private static int remainingBalls = 0;

    public static void main(String[] args) {
        // Initialize OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Initialize video capture from the camera
        VideoCapture videoCapture = new VideoCapture(0);

        // Check if video capture is successful
        if (!videoCapture.isOpened()) {
            System.out.println("Failed to open video capture device.");
            return;
        }

        // Main loop
        while (true) {
            // Read the current frame from the video capture
            Mat frame = new Mat();
            videoCapture.read(frame);

            // Perform ball detection and tracking here
            // ...

            // Update the number of remaining balls
            remainingBalls = totalBalls - capturedBalls;

            // Print the remaining balls count
            System.out.println("Remaining Balls: " + remainingBalls);

            // Break the loop if desired conditions are met
            // ...

            // Break the loop if the 'Esc' key is pressed
            //if (SomeConditionToBreakTheLoop)
                break;
        }

        // Release the video capture
        videoCapture.release();
    }
}
