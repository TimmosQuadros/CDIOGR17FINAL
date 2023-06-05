package ObjectDetection;

import LineCreation.LineSegment;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class RedCrossDetection {

    public static LineSegment[] crossLines = new LineSegment[2];
    private Mat frame = new Mat();
    private Point crossCenter;
    private final List<Point> coordinates = new ArrayList<>();

    /**
     * Constructs a FieldObjectDetection object to detect field objects, specifically the cross lines on the field.
     * @param aoiMask
     */
    public RedCrossDetection(Mat aoiMask) {
        Mat aoiImage = createAOIMask(aoiMask);

        fillObstableArray(aoiImage);
        crossCenter = RedRectangleDetection.findIntersection(crossLines[1], crossLines[0]);
        coordinates.add(0, crossCenter);

        determineCrosscoordinates();
    }

    private void determineCrosscoordinates() {
        Point[] points = new Point[4];
        points[0] = crossLines[0].getStartPoint();
        points[1] = crossLines[0].getEndPoint();
        points[2] = crossLines[1].getStartPoint();
        points[3] = crossLines[1].getEndPoint();

        addTopLeft(points);
        addTopRight(points);
        addBottomRight(points);
        addBottomLeft(points);
    }

    public List<Point> getCoordinates(){return coordinates;}

    private void addBottomLeft(Point[] points) {
        for (Point p : points){
            if ((p.x < coordinates.get(0).x) && (p.y > coordinates.get(0).y)) {
                coordinates.add(4, p);
            }
        }
    }

    private void addBottomRight(Point[] points) {
        for (Point p : points){
            if ((p.x > coordinates.get(0).x) && (p.y > coordinates.get(0).y)) {
                coordinates.add(3, p);
            }
        }
    }

    private void addTopRight(Point[] points) {
        for (Point p : points){
            if ((p.x > coordinates.get(0).x) && (p.y < coordinates.get(0).y)) {
                coordinates.add(2, p);
            }
        }
    }

    private void addTopLeft(Point[] points) {
        for (Point p : points){
            if ((p.x < coordinates.get(0).x) && (p.y < coordinates.get(0).y)) {
                coordinates.add(1, p);
            }
        }
    }

    public void detectCrossTest(){
        String imagePath = "resources/FieldImages/cutfieldImageWithCross.png";
        Mat frame = Imgcodecs.imread(imagePath);
        //Mat aoiImage = createAOIMask(frame);

        fillObstableArray(frame);

        Imgproc.circle(frame, crossLines[0].getStartPoint(), 5, new Scalar(0, 255, 0), -1);
        Imgproc.circle(frame, crossLines[0].getEndPoint(), 5, new Scalar(0, 255, 0), -1);

        Imgproc.circle(frame, crossLines[1].getStartPoint(), 5, new Scalar(0, 255, 0), -1);
        Imgproc.circle(frame, crossLines[1].getEndPoint(), 5, new Scalar(0, 255, 0), -1);

        crossCenter = RedRectangleDetection.findIntersection(crossLines[1], crossLines[0]);

        Imgproc.circle(frame, crossCenter, 5, new Scalar(0, 255, 0), -1);

        // Display the frame
        HighGui.imshow("Frame", frame);
        HighGui.waitKey();

        frame.release();
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

    private void fillObstableArray(Mat aoiMask) {
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(aoiMask, hsvImage, Imgproc.COLOR_BGR2HSV);
        Mat redCrossMask = new Mat();

        // Define the lower and upper thresholds for red color
        Scalar lowerRed = new Scalar(0, 100, 100);
        Scalar upperRed = new Scalar(10, 255, 255);

        Core.inRange(hsvImage, lowerRed, upperRed, redCrossMask);

        //vertical line
        crossLines[0] = findLinesegment(redCrossMask, true);

        //horizontal line
        crossLines[1] = findLinesegment(redCrossMask, false);

        for (LineSegment x : crossLines){
            System.out.println(x.getEndPoint() + " AND " + x.getStartPoint());
        }

    }

    /**
     * Creates a red cross mask based on the frame and the area of interest points.
     *
     * @return the red cross mask.
     */

    public Mat createAOIMask(Mat aoiMask) {
        // Apply the mask to the original image
        Mat maskedImage = new Mat();

        frame.copyTo(maskedImage, aoiMask);

        // Save the masked image and the binary mask image
        Imgcodecs.imwrite("maskedImage.jpg", maskedImage);

        return maskedImage;
    }

    private LineSegment findLinesegment(Mat binaryImage, boolean vertical) {
        Mat lines = new Mat();
        int rho = 1; // Distance resolution of the accumulator in pixels
        double theta = Math.PI / 180; // Angle resolution of the accumulator in radians
        int threshold = 100; // Minimum number of intersections to detect a line
        int minLineLength = 50; // Minimum length of a line in pixels
        int maxLineGap = 10; // Maximum gap between line segments allowed in pixels

        // The houghLinesP function helps us look for line shapes and patterns.
        Imgproc.HoughLinesP(binaryImage, lines, rho, theta, threshold, minLineLength, maxLineGap);

        double maxLineLength = 0;
        Point startPoint = new Point();
        Point endPoint = new Point();

        for (int i = 0; i < lines.rows(); i++) {
            double[] line = lines.get(i, 0);
            double x1 = line[0];
            double y1 = line[1];
            double x2 = line[2];
            double y2 = line[3];

            double length = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
            double angle = Math.atan2(y2 - y1, x2 - x1) * 180 / Math.PI;

            //checking if vertical
            //if true we look for vertical, otherwise we look for horizontal, as seen by the degree constraints.
            if(vertical) {
                if (Math.abs(angle) >= 45 && Math.abs(angle) <= 135 && length > maxLineLength) {
                    maxLineLength = length;
                    startPoint = new Point(x1, y1);
                    endPoint = new Point(x2, y2);
                }
            }else{
                if (Math.abs(angle) >= -55 && Math.abs(angle) <= 55 && length > maxLineLength) {
                    maxLineLength = length;
                    startPoint = new Point(x1, y1);
                    endPoint = new Point(x2, y2);
                }
            }
        }

        return new LineSegment(startPoint, endPoint);
    }

    public List<Point> getCross() {
        return this.coordinates;
    }
}
