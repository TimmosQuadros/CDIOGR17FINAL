package Bitmasks;

import Singleton.VideoCaptureSingleton;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class AreaOfInterestFrame {

    private static AreaOfInterestFrame instance;
    private Mat aoiMask;
    private Mat frame = new Mat();

    public AreaOfInterestFrame(List<Point> corners) {
        createAreaOfInterestMask(corners);
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

        frame = maskedImage;

        Imgcodecs.imwrite("numberOne.jpg", maskedImage);
    }

    public Mat getAoiMask() {
        return frame;
    }
}
