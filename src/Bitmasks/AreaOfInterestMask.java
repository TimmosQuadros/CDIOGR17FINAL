package Bitmasks;

import Singleton.VideoCaptureSingleton;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

public class AreaOfInterestMask {

    private static AreaOfInterestMask instance;
    private Mat aoiMask;
    private Mat frame = new Mat();

    private AreaOfInterestMask(List<Point> corners) {
        createAreaOfInterestMask(corners);
    }

    public static AreaOfInterestMask getInstance(List<Point> corners) {
        if (instance == null) {
            synchronized (AreaOfInterestMask.class) {
                if (instance == null) {
                    instance = new AreaOfInterestMask(corners);
                }
            }
        }
        return instance;
    }

    public void retrieveFrame(){
        // Check if the VideoCapture object is opened successfully
        if (!VideoCaptureSingleton.getInstance().getVideoCapture().isOpened()) {
            System.out.println("Failed to open the webcam.");
            return ;
        }

        while (!VideoCaptureSingleton.getInstance().getVideoCapture().read(this.frame)) { //reads next frame of videocapture into the frame variable.
            System.out.println("Failed to capture a frame.");
        }
    }

    /**
     * The area of interest will be the course.
     * @param
     * @param corners
     */
    private void createAreaOfInterestMask(List<Point> corners) {
        retrieveFrame();

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
