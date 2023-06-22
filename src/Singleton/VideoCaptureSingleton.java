package Singleton;

import ObjectDetection.RedRectangleDetection;
import Observer.FindAreaOfInterestSubject;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

/*
 * Author Mohammed Irout and Timm Daniel Rasmussen.
 */

public class VideoCaptureSingleton {
    private static VideoCaptureSingleton instance;
    private final VideoCapture videoCapture;

    private Point point1 = null;
    private Point point2 = null;
    private Point point3 = null;
    private Point point4 = null;

    private VideoCaptureSingleton() {
        // Private constructor to prevent direct instantiation
        videoCapture = new VideoCapture(0);
        setMaxResolution(videoCapture);
    }

    public void setPoint(FindAreaOfInterestSubject findAreaOfInterestSubject){
        Point[] corners = findAreaOfInterestSubject.getCorners();
        point1 = new Point(corners[0].x,corners[0].y);
        point2 = new Point(corners[1].x,corners[1].y);
        point3 = new Point(corners[3].x,corners[3].y);
        point4 = new Point(corners[2].x,corners[2].y);
    }

    public static VideoCaptureSingleton getInstance() {
        if (instance == null) {
            synchronized (VideoCaptureSingleton.class) {
                if (instance == null) {
                    instance = new VideoCaptureSingleton();
                }
            }
        }
        return instance;
    }

    public VideoCapture getVideoCapture() {
        return videoCapture;
    }

    public Mat mask(Mat frame){
        // Create a mask to exclude the center region
        Mat mask = Mat.zeros(frame.size(), CvType.CV_8U);
        List<MatOfPoint> contours = new ArrayList<>();
        contours.add(new MatOfPoint(point1, point2, point3, point4));
        Imgproc.fillPoly(mask, contours, new Scalar(255));


        Mat result = new Mat();
        frame.copyTo(result, mask);
        return result;
    }

    private void setMaxResolution(VideoCapture videoCapture) {
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
}
//VideoCaptureSingleton videoCaptureSingleton = VideoCaptureSingleton.getInstance();
//VideoCapture videoCapture = videoCaptureSingleton.getVideoCapture();