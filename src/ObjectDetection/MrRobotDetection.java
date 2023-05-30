package ObjectDetection;

import LineCreation.LineSegment;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

import static run.Main.videoCapture;

public class MrRobotDetection {

    private Mat frame;
    private Point[] areaOfInterest = new Point[4];
    private LineSegment front;
    private LineSegment back;
    public static Point frontCenter;
    public static Point backCenter;

    /**
     * Constructs a MrRobotDetection object to detect the robot within the specified area of interest.
     *
     * @param area the area of interest on the field.
     */
    public MrRobotDetection(Point[] area){
        System.arraycopy(area, 4, areaOfInterest, 0, areaOfInterest.length);

        //System.out.println("front center" + frontCenter.x + " and " + frontCenter.y);
        //System.out.println("back center" + backCenter.x + " and " + backCenter.y);
    }
    public void updatePosition() throws InterruptedException {
        findPoints();

            // Read a new frame from the video capture

            // Draw a dot at the object position on the frame
            Imgproc.circle(frame, frontCenter, 5, new Scalar(0, 0, 255), -1);
            Imgproc.circle(frame, backCenter, 5, new Scalar(0, 0, 255), -1);

            // Display the updated frame with the object position
            HighGui.imshow("Real-Time Object Tracking", frame);

            Thread.sleep(0x64);

            // Check for keyboard input and break the loop if the 'q' key is pressed
    }

    public void findPoints(){
        retrieveFrame();
        Mat aoiImage = detectRobot(frame);
        //find green line
        front = findColouredLineSegment(aoiImage, true);
        //find blue line
        back = findColouredLineSegment(aoiImage, false);
        frontCenter = determineFrontCenter();
        backCenter = determineBackCenter();

        Point[] points = new Point[2];
        points[0] = frontCenter;
        points[1] = backCenter;
    }

    public void retrieveFrame(){
        // Check if the VideoCapture object is opened successfully
        if (!videoCapture.isOpened()) {
            System.out.println("Failed to open the webcam.");
            return ;
        }

        this.frame = new Mat();
        if (videoCapture.read(this.frame)) { //reads next frame of videocapture into the frame variable.
            //Save the frame as a PNG file
            //imagePath = getRessourcePath();
            //Imgcodecs.imwrite(imagePath, this.frame);
            //System.out.println("Frame saved as " + imagePath);
        } else {
            System.out.println("Failed to capture a frame.");
        }
    }


    private LineSegment findColouredLineSegment(Mat aoiImage, boolean green) {
        // Convert the frame to the HSV color space
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(aoiImage, hsvImage, Imgproc.COLOR_BGR2HSV);

        Scalar lower;
        Scalar upper;

        if (green) {
            // Define the lower and upper green color thresholds in HSV
            lower = new Scalar(35, 100, 100);
            upper = new Scalar(85, 255, 255);
        }
        else{
            lower = new Scalar(90, 50, 50); // Lower blue threshold in HSV
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
        double minLineLength = 100; // Minimum line length
        double maxLineGap = 10; // Maximum gap between line segments
        Imgproc.HoughLinesP(colorMask, lines, 1, Math.PI / 180, 100, minLineLength, 10);

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

    public Mat detectRobot(Mat frame) {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat image = frame;

        if (frame == null) {
            // Load the input image
            image = Imgcodecs.imread("C:\\Users\\emil1\\OneDrive\\Documents\\CDIOGR17FINAL\\resources\\FieldImages\\MrRobotBlackGreenNBlueEnds.jpg");
        }

        Mat mask = Mat.zeros(image.size(), CvType.CV_8UC1);

        // Create a region of interest polygon using the four points
        List<Point> roiPoints = new ArrayList<>();
        roiPoints.add(areaOfInterest[0]);
        roiPoints.add(areaOfInterest[1]);
        roiPoints.add(areaOfInterest[3]);
        roiPoints.add(areaOfInterest[2]);
        MatOfPoint roiContour = new MatOfPoint();
        roiContour.fromList(roiPoints);

        // Fill the region of interest polygon with white color in the mask image
        Imgproc.fillPoly(mask, List.of(roiContour), new Scalar(255));

        // Apply the mask to the original image
        Mat maskedImage = new Mat();
        image.copyTo(maskedImage, mask);

        // Save the masked image and the binary mask image
        Imgcodecs.imwrite("maskedImage.jpg", maskedImage);

        return maskedImage;
    }


}

