package Bitmasks;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

public class AreaOfInterestMask {

    private static AreaOfInterestMask instance;
    private Mat aoiMask;
    Mat frame = new Mat();

    private AreaOfInterestMask(VideoCapture videoCapture, List<Point> corners) {
        createAreaOfInterestMask(videoCapture, corners);
    }

    public static AreaOfInterestMask getInstance(VideoCapture videoCapture, List<Point> corners) {
        if (instance == null) {
            synchronized (AreaOfInterestMask.class) {
                if (instance == null) {
                    instance = new AreaOfInterestMask(videoCapture, corners);
                }
            }
        }
        return instance;
    }

    public void retrieveFrame(VideoCapture videoCapture){
        // Check if the VideoCapture object is opened successfully
        if (!videoCapture.isOpened()) {
            System.out.println("Failed to open the webcam.");
            return ;
        }

        while (!videoCapture.read(this.frame)) { //reads next frame of videocapture into the frame variable.
            System.out.println("Failed to capture a frame.");
        }
    }

    /**
     * The area of interest will be the course.
     * @param
     * @param corners
     */
    private void createAreaOfInterestMask(VideoCapture capture, List<Point> corners) {
        retrieveFrame(capture);

        aoiMask = Mat.zeros(frame.size(), CvType.CV_8UC1);

        MatOfPoint roiContour = new MatOfPoint();
        roiContour.fromList(corners);

        // Fill the region of interest polygon with white color in the mask image
        Imgproc.fillPoly(aoiMask, List.of(roiContour), new Scalar(255));

        Mat maskedImage = new Mat();

        frame.copyTo(maskedImage, aoiMask);

        Imgcodecs.imwrite("maskedImage.jpg", maskedImage);

    }

    public Mat getAoiMask() {
        return aoiMask;
    }
}
