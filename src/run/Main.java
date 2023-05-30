package run;

import ObjectDetection.MrRobotDetection;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    //load the opencv library into the JVM at runtime
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static VideoCapture videoCapture;

    //An array to store relevant course coordinates. Index 0-3 will correspond to the raw corner coordinates.
    //Index 4-7 will be the adjusted corner coordinates. And 8-9 will be the goal coordinates.
    //for the corner coordinates, the firs elements will be from top left to bottom right.
    //The goals will be from left to right.
    //ie courseCoordinates[0] will be the to left corner. courseCoordinates[2] will be bottom left corner, and so on.
    public static Point[] courseCoordinates = new Point[10];

    public static void main(String[] args) throws InterruptedException {
        //print current version of opencv
        System.out.println(Core.VERSION);

        //variable for testing
        //VideoCapture videoCapture = null;

        videoCapture = new VideoCapture(0);
        setMaxResolution();

        executorservice();

        //FieldObjectDetection fieldObjectDetection = new FieldObjectDetection(videoCapture, courseCoordinates);

        MrRobotDetection mrRobot = new MrRobotDetection(courseCoordinates);
        mrRobot.updatePosition();

        //stop capturing
        //videoCapture.release();
    }

    private static void setMaxResolution() {
        // Set the property for maximum resolution to a high value
        int maxResolutionProperty = -1;
        // Find the property representing the maximum resolution
        double maxResolution = 0.0;
        ;
        for (int prop = 0; prop < 20; prop++) { // Iterate over the properties
            double value = videoCapture.get(prop);
            if (value > maxResolution) {
                maxResolution = value;
                maxResolutionProperty = prop;
            }
        }

        videoCapture.set(maxResolutionProperty, 9999);

        // Get the resolution of the video capture
        int width = (int) videoCapture.get(3);
        int height = (int) videoCapture.get(4);

        // Print the resolution
        System.out.println("Resolution: " + width + "x" + height);
    }

    private static void executorservice(){
        // Create an ExecutorService with a fixed thread pool
        ExecutorService executor = Executors.newFixedThreadPool(1);

        //Point[] areaOfInterest = new Point[4];

        // Create an instance of your task
        Callable<List<Point>> task = new findAreaOfInterestTask();
        // Submit the task to the executor
        Future<List<Point>> future = executor.submit(task);

        // Retrieve the result from the future object
        try {
            assignCoordinates(future.get());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // Shutdown the executor when done
            executor.shutdown();
        }
    }

    private static void assignCoordinates(List<Point> points) {

        for (int i = 0; i < points.size() ; i++) {
            courseCoordinates[i] = points.get(i);
        }
    }
}

