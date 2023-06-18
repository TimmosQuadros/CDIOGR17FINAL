package Singleton;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

public class VideoCaptureSingleton {
    private static VideoCaptureSingleton instance;
    private final VideoCapture videoCapture;

    private final Point point1 = new Point(419.0,118.0);
    private final Point point2 = new Point(1544.0,131.0);
    private final Point point3 = new Point(1551.0,961.0);
    private final Point point4 = new Point(390,960.0);

    private VideoCaptureSingleton() {
        // Private constructor to prevent direct instantiation
        videoCapture = new VideoCapture(0);
        setMaxResolution(videoCapture);
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