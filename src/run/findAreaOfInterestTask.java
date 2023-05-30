package run;

import ObjectDetection.RedRectangleDetection;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class findAreaOfInterestTask implements Callable<List<Point>> {

    VideoCapture videoCapture = null;

    public findAreaOfInterestTask(VideoCapture capture){
        this.videoCapture = capture;
    }

    public List<Point> call() throws Exception{
        // Create an instance of the RedRectangleDetection class with the specified VideoCapture object
        RedRectangleDetection detectField = new RedRectangleDetection(this.videoCapture);

        // Call the testRedRectangleDetection method of the RedRectangleDetection class to detect and return points of interest
        return detectField.testRedRectangleDetection();

        // Alternative: Use the detectField method instead of testRedRectangleDetection
        //return detectField.detectField(this.videoCapture);
    }

}
