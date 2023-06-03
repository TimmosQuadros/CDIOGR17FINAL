package ObjectDetection;

import Bitmasks.AreaOfInterestMask;
import LineCreation.LineSegment;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.List;

public class MrRobotDetection {

    private AreaOfInterestMask aoiMask = null;
    private Mat frame = new Mat();
    private LineSegment front;
    private LineSegment back;
    public static Point frontCenter;
    public static Point backCenter;


    public void updatePosition(RedRectangleDetection rectangleDetection) throws InterruptedException {
            // Read a new frame from the video capture
            findPoints(rectangleDetection);

            // Draw a dot at the object position on the frame
            Imgproc.circle(frame, frontCenter, 5, new Scalar(0, 0, 255), -1);
            Imgproc.circle(frame, backCenter, 5, new Scalar(0, 0, 255), -1);

            // Display the updated frame with the object position
            HighGui.imshow("Real-Time Object Tracking", frame);

            Thread.sleep(0x64);

            // Check for keyboard input and break the loop if the 'q' key is pressed
    }

    public void test(Point[] corners){
        //applys the area of interest to the frame to avoid noise - things outside the field.
        Mat aoiImage = narrowSearchArea(corners);

        //String imagePath = "resources/FieldImages/redblue.jpg";
        //Mat frame = Imgcodecs.imread(imagePath);

        //find green line
        back = findColouredLineSegment(aoiImage, true);
        //find blue line
        front = findColouredLineSegment(aoiImage, false);

        if (front != null){
            frontCenter = determineFrontCenter();
            System.out.println("front center" + frontCenter.x + " and " + frontCenter.y);
            Imgproc.circle(aoiImage, frontCenter, 5, new Scalar(0, 255, 0), -1);
        }
        if (back != null){
            backCenter = determineBackCenter();
            System.out.println("back center" + backCenter.x + " and " + backCenter.y);
            Imgproc.circle(aoiImage, backCenter, 5, new Scalar(0, 255, 0), -1);
            Imgproc.circle(aoiImage, back.getStartPoint(), 5, new Scalar(0, 255, 0), -1);
            Imgproc.circle(aoiImage, back.getEndPoint(), 5, new Scalar(0, 255, 0), -1);
        }

        // Display the frame
        HighGui.imshow("Frame", aoiImage);
        HighGui.waitKey();

        frame.release();

        /*if(!((front == null) || (back == null))) {
            frontCenter = determineFrontCenter();
            backCenter = determineBackCenter();
            System.out.println("front center" + frontCenter.x + " and " + frontCenter.y);
            System.out.println("back center" + backCenter.x + " and " + backCenter.y);
        }
         */

    }

    public void findPoints(RedRectangleDetection rectangleDetection){
        Mat aoiImage = rectangleDetection.getAoiMask();
        //find green line
        front = findColouredLineSegment(aoiImage, false);
        //find blue line
        back = findColouredLineSegment(aoiImage, true);
        frontCenter = determineFrontCenter();
        backCenter = determineBackCenter();

        System.out.println("front center" + frontCenter.x + " and " + frontCenter.y);
        System.out.println("back center" + backCenter.x + " and " + backCenter.y);
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


    private LineSegment findColouredLineSegment(Mat aoiImage, boolean red) {
        // Convert the frame to the HSV color space
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(aoiImage, hsvImage, Imgproc.COLOR_BGR2HSV);

        Scalar lower;
        Scalar upper;

        if (red) {
            // Define the lower and upper green color thresholds in HSV
            lower = new Scalar(0, 100, 100);
            upper = new Scalar(10, 255, 255);
        }
        else{
            lower = new Scalar(90, 50, 50,0); // Lower blue threshold in HSV
            upper = new Scalar(130, 255, 255); // Upper blue threshold in HSV
        }

        // Threshold the HSV image to get the green color region
        Mat colorMask = new Mat();
        Core.inRange(hsvImage, lower, upper, colorMask);

        // Apply morphological operations to enhance the mask
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Imgproc.morphologyEx(colorMask, colorMask, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(colorMask, colorMask, Imgproc.MORPH_CLOSE, kernel);

        // Detect lines using the Hough Line Transform
        Mat lines = new Mat();
        double minLineLength = 60; // Minimum line length
        double maxLineGap = 15; // Maximum gap between line segments
        Imgproc.HoughLinesP(colorMask, lines, 1, Math.PI / 180, 100, minLineLength, maxLineGap);

        return findBiggestLine(lines);
    }

    private static LineSegment findBiggestLine(Mat lines) {
        List<LineSegment> lineSegments = new ArrayList<>();

        // Convert each line from polar coordinates to start and end points
        for (int i = 0; i < lines.rows(); i++) {
            double[] lineData = lines.get(i, 0);
            double x1 = lineData[0];
            double y1 = lineData[1];
            double x2 = lineData[2];
            double y2 = lineData[3];
            Point startPoint = new Point(x1, y1);
            Point endPoint = new Point(x2, y2);
            lineSegments.add(new LineSegment(startPoint, endPoint));
        }

        // Find the longest line segment
        LineSegment biggestLine = null;
        double maxLineLength = 0;

        for (LineSegment lineSegment : lineSegments) {
            double lineLength = lineSegment.getLength();

            if (lineLength > maxLineLength) {
                maxLineLength = lineLength;
                biggestLine = lineSegment;
            }
        }

        return biggestLine;
    }

    private Point determineFrontCenter() {
        return new Point((front.getEndPoint().x + front.getStartPoint().x) / 2.0, (front.getEndPoint().y + front.getStartPoint().y) / 2.0);
    }

    private Point determineBackCenter() {
        return new Point((back.getEndPoint().x + back.getStartPoint().x) / 2.0, (back.getEndPoint().y + back.getStartPoint().y) / 2.0);
    }

    public Mat narrowSearchArea(Point[] corners) {
        if (VideoCaptureSingleton.getInstance().getVideoCapture() == null) {
            // Load the input image
            //frame = Imgcodecs.imread("C:\\Users\\emil1\\OneDrive\\Documents\\GitHub\\CDIOGR17FINAL\\resources\\FieldImages\\MrRobotBlackGreenNBlueEnds.jpg");
            frame = Imgcodecs.imread("C:\\Users\\emil1\\OneDrive\\Documents\\CDIOGR17FINAL\\resources\\FieldImages\\redblue.jpg");
        }

        // Apply the mask to the original image
        Mat maskedImage = new Mat();

        frame.copyTo(maskedImage, aoiMask.getAoiMask());

        // Save the masked image and the binary mask image
        Imgcodecs.imwrite("maskedImage.jpg", maskedImage);

        return maskedImage;
    }


}

