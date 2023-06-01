package run;

import ObjectDetection.RedCrossDetection;
import ObjectDetection.MrRobotDetection;
import ObjectDetection.RedRectangleDetection;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.Core;
import org.opencv.core.Point;
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
    //public static VideoCapture videoCapture;

    //An array to store relevant course coordinates. Index 0-3 will correspond to the raw corner coordinates.
    //Index 4-7 will be the adjusted corner coordinates. And 8-9 will be the goal coordinates.
    //for the corner coordinates, the firs elements will be from top left to bottom right.
    //The goals will be from left to right.
    //ie courseCoordinates[0] will be the to left corner. courseCoordinates[2] will be bottom left corner, and so on.
    public static Point[] courseCoordinates = new Point[10];

    public static void main(String[] args) throws InterruptedException {
        //print current version of opencv
        System.out.println(Core.VERSION);

        testWithoutVideo();

        //runWithVideo();

    }

    private static void runWithVideo() {
        VideoCaptureSingleton videoCaptureSingleton = VideoCaptureSingleton.getInstance();
        setMaxResolution(videoCaptureSingleton);

        //detecs field
        RedRectangleDetection rectangleDetection = new RedRectangleDetection();
        rectangleDetection.detectField(videoCaptureSingleton);

        //detect cross
        RedCrossDetection fieldObjectDetection = new RedCrossDetection();

        MrRobotDetection mrRobot = new MrRobotDetection();
        mrRobot.findPoints(videoCaptureSingleton.getVideoCapture(), null); //replace null with corners

        videoCaptureSingleton.getVideoCapture().release();
    }

    private static void testWithoutVideo() {
        //detecs field
        RedRectangleDetection rectangleDetection = new RedRectangleDetection();
        rectangleDetection.testRedRectangleDetection();

        //detect cross
        RedCrossDetection redCrossDetection = new RedCrossDetection();
        redCrossDetection.detectCrossTest();

        //detect robot
        //MrRobotDetection mrRobot = new MrRobotDetection();
        //mrRobot.test();
    }

    private static void setMaxResolution(VideoCaptureSingleton videoCapture) {
        // Set the property for maximum resolution to a high value
        int maxResolutionProperty = -1;
        // Find the property representing the maximum resolution
        double maxResolution = 0.0;
        ;
        for (int prop = 0; prop < 20; prop++) { // Iterate over the properties
            double value = videoCapture.getVideoCapture().get(prop);
            if (value > maxResolution) {
                maxResolution = value;
                maxResolutionProperty = prop;
            }
        }

        videoCapture.getVideoCapture().set(maxResolutionProperty, 9999);

        // Get the resolution of the video capture
        int width = (int) videoCapture.getVideoCapture().get(3);
        int height = (int) videoCapture.getVideoCapture().get(4);

        // Print the resolution
        System.out.println("Resolution: " + width + "x" + height);
    }

    private static void executorservice(){
        // Create an ExecutorService with a fixed thread pool
        ExecutorService executor = Executors.newFixedThreadPool(1);

        // Create an instance of your task
        Callable<List<Point>> task = new findAreaOfInterestTask();
        // Submit the task to the executor
        Future<List<Point>> future = executor.submit(task);

        // Retrieve the result from the future object
        try {
            future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // Shutdown the executor when done
            executor.shutdown();
        }
    }

}

