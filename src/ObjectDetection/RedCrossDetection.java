package ObjectDetection;

import Bitmasks.AreaOfInterestFrame;
import LineCreation.Circle;
import LineCreation.LineSegment;
import Singleton.VideoCaptureSingleton;
import Vectors.Vector;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class RedCrossDetection {

    public LineSegment[] crossLines = new LineSegment[2];
    private Mat frame = new Mat();
    private Mat aoiImage;
    private final List<Point> coordinates = new ArrayList<>();
    public Circle crossArea; //will be used to determine if a ball is near the cross.
    public Circle scalefactorAdjustedCrossArea; // will be used to determine if robert collides with the cross on its path.

    /**
     * Constructs a FieldObjectDetection object to detect field objects, specifically the cross lines on the field.
     * @param aoiMask
     */
    public RedCrossDetection(AreaOfInterestFrame aoiMask, double scaleFactor) {
        aoiImage = aoiMask.getAoiMask();

        fillObstableArray();
        findcenter();

        Vector radius = new Vector(crossLines[0].getStartPoint().x - coordinates.get(0).x,
                crossLines[0].getStartPoint().y - coordinates.get(0).y);

        //Creates area around the cross
        crossArea = new Circle(coordinates.get(0).x, coordinates.get(0).y, radius.getLength());
        //Creates adjusted area near the cross, where the robert is able to drive by it.
        scalefactorAdjustedCrossArea = new Circle(coordinates.get(0).x, coordinates.get(0).y, (radius.getLength() + (scaleFactor * 11)));
        System.out.println("Length " + radius.getLength());

        determineCrosscoordinates();

        for (LineSegment x : crossLines){
            System.out.println(x.getEndPoint() + " AND " + x.getStartPoint());
        }

        Point ball = null;
        Point robotCenter = null;

        adjustCoordinatesWithScaleFactor(scaleFactor);

    }

    public boolean pathIntersects(Point ball, Point robotCenter) {
        // Line equation y = mx + c
        double m = (ball.y - robotCenter.y) / (ball.x - robotCenter.x);
        double c = robotCenter.y - m * robotCenter.x;

        // Substitute line equation into circle equation
        double h = scalefactorAdjustedCrossArea.getCenter().x;
        double k = scalefactorAdjustedCrossArea.getCenter().y;

        // Coefficients for the quadratic equation
        double a = (m * m + 1);
        double b = (2 * m * c - 2 * m * k - 2 * h);
        double cc = (h * h + c * c - 2 * c * k + k * k - scalefactorAdjustedCrossArea.getRadius() * scalefactorAdjustedCrossArea.getRadius());

        // Discriminant
        double discriminant = b * b - 4 * a * cc;

        return discriminant > 0;
}

    private void adjustCoordinatesWithScaleFactor(double scaleFactor) {

    }

    private void findcenter() {
        coordinates.add(0, new Point((crossLines[0].getStartPoint().x + crossLines[0].getEndPoint().x) / 2,
                (crossLines[0].getStartPoint().y + crossLines[0].getEndPoint().y) / 2));
    }

    private void determineCrosscoordinates() {
        crossLines[1] = findSecondLine();

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

    public Point rotate90(Point p) {
        return new Point(-p.y, p.x);
    }

    // Translate a point by a given vector
    public Point translate(Point p, Point vector) {
        return new Point(p.x + vector.x, p.y + vector.y);
    }

    private LineSegment findSecondLine() {
        // Translate the start and end points so that the center point becomes the origin
        Point translatedStart = translate(crossLines[0].getStartPoint(), new Point(-coordinates.get(0).x, -coordinates.get(0).y));
        Point translatedEnd = translate(crossLines[0].getEndPoint(), new Point(-coordinates.get(0).x, -coordinates.get(0).y));

        // Rotate the translated points
        Point rotatedStart = rotate90(translatedStart);
        Point rotatedEnd = rotate90(translatedEnd);

        // Translate the points back
        Point finalStart = translate(rotatedStart, coordinates.get(0));
        Point finalEnd = translate(rotatedEnd, coordinates.get(0));

        return new LineSegment(finalStart, finalEnd);
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

        Imgproc.circle(frame, crossLines[0].getStartPoint(), 5, new Scalar(0, 255, 0), -1);
        Imgproc.circle(frame, crossLines[0].getEndPoint(), 5, new Scalar(0, 255, 0), -1);

        Imgproc.circle(frame, crossLines[1].getStartPoint(), 5, new Scalar(0, 255, 0), -1);
        Imgproc.circle(frame, crossLines[1].getEndPoint(), 5, new Scalar(0, 255, 0), -1);

        Imgproc.circle(frame, coordinates.get(0), 5, new Scalar(0, 255, 0), -1);

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

    private void fillObstableArray() {
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(aoiImage, hsvImage, Imgproc.COLOR_BGR2HSV);
        Mat redCrossMask = new Mat();

        // Define the lower and upper thresholds for red color
        Scalar lowerRed = new Scalar(0, 100, 100);
        Scalar upperRed = new Scalar(10, 255, 255);

        Core.inRange(hsvImage, lowerRed, upperRed, redCrossMask);

        // Use morphological operations to clean up the thresholded image
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.morphologyEx(redCrossMask, redCrossMask, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(redCrossMask, redCrossMask, Imgproc.MORPH_CLOSE, kernel);

        crossLines[0] = findLine(redCrossMask);
    }

    private LineSegment findLine(Mat redmask) {
        // Apply Hough Line Transform
        Mat lines = new Mat();
        Imgproc.HoughLinesP(redmask, lines, 1, Math.PI / 180, 100, 50, 12);

        // Find the longest line
        double maxLineLength = 0;
        double[] longestLine = null;

        for (int i = 0; i < lines.rows(); i++) {
            double[] line = lines.get(i, 0);
            double x1 = line[0];
            double y1 = line[1];
            double x2 = line[2];
            double y2 = line[3];

            // Calculate the length of the line using Euclidean distance formula
            double lineLength = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

            // Check if the current line is longer than the previous longest line
            if (lineLength > maxLineLength) {
                maxLineLength = lineLength;
                longestLine = line;
            }
        }

        // Process the longest line
        if (longestLine != null) {
            double x1 = longestLine[0];
            double y1 = longestLine[1];
            double x2 = longestLine[2];
            double y2 = longestLine[3];

            return new LineSegment(new Point(x1, y1), new Point(x2, y2));
        }
        return null;
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
