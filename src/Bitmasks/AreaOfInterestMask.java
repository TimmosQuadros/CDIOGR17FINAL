package Bitmasks;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static run.Main.courseCoordinates;

public class AreaOfInterestMask {

    public static Mat aoiMask;

    public AreaOfInterestMask(Mat frame) {
        createAreaOfInterestMask(frame);
    }

    /**
     * The area of interest will be the course.
     * @param frame
     */
    private void createAreaOfInterestMask(Mat frame) {
        aoiMask = Mat.zeros(frame.size(), CvType.CV_8UC1);

        // Create a region of interest polygon using the four points
        List<Point> roiPoints = new ArrayList<>();
        roiPoints.add(courseCoordinates[4]);
        roiPoints.add(courseCoordinates[5]);
        roiPoints.add(courseCoordinates[7]);
        roiPoints.add(courseCoordinates[6]);

        MatOfPoint roiContour = new MatOfPoint();
        roiContour.fromList(roiPoints);

        // Fill the region of interest polygon with white color in the mask image
        Imgproc.fillPoly(aoiMask, List.of(roiContour), new Scalar(255));
    }

    public Mat getAoiMask() {
        return aoiMask;
    }
}
