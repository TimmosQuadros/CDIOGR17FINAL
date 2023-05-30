package run;

import ObjectDetection.RedRectangleDetection;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class findAreaOfInterestTask implements Callable<List<Point>> {

    VideoCapture videoCapture = null;

    public findAreaOfInterestTask(){
    }

    public List<Point> call() throws Exception{
        RedRectangleDetection detectField = new RedRectangleDetection();
        return detectField.testRedRectangleDetection();

        //return detectField.detectField();
    }

}
