package ObjectDetection;

import Bitmasks.AreaOfInterestFrame;
import LineCreation.LineSegment;
import Singleton.VideoCaptureSingleton;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Langt brede = 178 cm.
 * Kort brede = 133 cm.
 */
public class RedRectangleDetection {

    private final int frameWidth = 1920;
    private final int frameHeight = 1080;
    private Mat frame;
    private Point[] courseCoordinates = new Point[4];
    private List<Point> corners = new ArrayList<>();
    private List<Point> goals = new ArrayList<>();
    private RedCrossDetection redCross;
    private double scaleFactor;
    private final int axelLength = 20;
    private final int axelTurnRadius = 22;
    private Mat aoiMask;

    /**
     * Method to be used in real time.
     * We will use the coordinates found to define an area of interest, in which we will search
     * for the remaining objects that is : table tennis balls, obstacle and Mr. Robot.
     * Having a subsection of the actual frame defined minimized the computational work errors / disturbances
     * of observations not of interest.
     * @return
     */
    public List<Point> detectField(){
        findAverageCorner(50);
        FindScaling();
        findFloorCorners();
        determineGoalCenters();
        AreaOfInterestFrame mask = new AreaOfInterestFrame(corners);

        for (Point p : courseCoordinates){
            System.out.println("Corner x : " + p.x + " and corner y : " + p.y);
        }

        redCross = new RedCrossDetection(mask, scaleFactor);

        drawCorners();

        return getFloorCorners();
    }

    private void findAverageCorner(int size) {
        Point[][] cornersAve = new Point[size][4];

        for (int i = 0; i < cornersAve.length; i++) {
            retrieveFrame();
            findCorners(cornersAve[i], findLines()); // find corners.
        }

        Point corner1 = new Point(0.0, 0.0);
        Point corner2 = new Point(0.0, 0.0);
        Point corner3 = new Point(0.0, 0.0);
        Point corner4 = new Point(0.0, 0.0);

        for (int i = 0; i < cornersAve.length; i++) {
            for (int j = 0; j < 4; j++) {
                corner1.x += cornersAve[i][j].x;
                corner1.y += cornersAve[i][j++].y;
                corner2.x += cornersAve[i][j].x;
                corner2.y += cornersAve[i][j++].y;
                corner3.x += cornersAve[i][j].x;
                corner3.y += cornersAve[i][j++].y;
                corner4.x += cornersAve[i][j].x;
                corner4.y += cornersAve[i][j++].y;
            }
        }

        courseCoordinates[0] = new Point(corner1.x /50.0, corner1.y / 50);
        courseCoordinates[1] = new Point(corner2.x /50.0, corner2.y / 50);
        courseCoordinates[2] = new Point(corner3.x /50.0, corner3.y / 50);
        courseCoordinates[3] = new Point(corner4.x /50.0, corner4.y / 50);
    }

    public RedCrossDetection getRedCross(){
        return redCross;
    }

    public double getScaleFactor(){ return this.scaleFactor; }

    public List<Point> getFloorCorners(){
        return corners;
    }

    public Mat getAoiMask(){return aoiMask;}

    public void determineGoalCenters() {
        // finds posts for lefthand side.
        goals.add(getAverage(corners.get(0),corners.get(3)));
        //finds posts for righthand side.
        goals.add(getAverage(corners.get(1),corners.get(2)));
    }

    public List<Point> getGoals(){ return goals; }

    private Point getAverage(Point upperPoint, Point lowerPoint) {
        double centerX = (upperPoint.x + lowerPoint.x) / 2;
        double centerY = (upperPoint.y + lowerPoint.y) / 2;

        return new Point(centerX,centerY);
    }

    /**
     * OBS!! This method needs more testing!!
     *
     * Fiinds an approximation of the coordiinates of the folding in the corner.
     * @return the coordinates of the approximated foldiing intersections for each corner.
     */
    private void findFloorCorners() {
        double adjustHeight = 11.0;
        double adjustWidth = 15.0;
        corners.add(0, new Point((adjustWidth + courseCoordinates[0].x),(adjustHeight + courseCoordinates[0].y)));
        corners.add(1, new Point((courseCoordinates[1].x - adjustWidth),(adjustHeight + courseCoordinates[1].y)));
        corners.add(2, new Point((courseCoordinates[3].x - adjustWidth),(courseCoordinates[3].y) - adjustHeight));
        corners.add(3, new Point((adjustWidth + courseCoordinates[2].x),(courseCoordinates[2].y) - adjustHeight));
    }

    /**
     * method to test how well working the methods are using png images.
     */
    public void testRedRectangleDetection(){
        String imagePath = "resources/FieldImages/bluegreen.jpg";
        //String imagePath = "resources/FieldImages/MrRobotBlackGreenNBlueEnds.jpg";
        frame = Imgcodecs.imread(imagePath);

        //findFloorCorners();
        //drawCorners(frame);
        //for (Point x : courseCoordinates){
          //  System.out.println("X coordinate = " + x.x + " AND y coordinate = " + x.y);
        //}
    }

    /**
     * This method will draw green circles on each point received as input.
     */
    private void drawCorners() {
        // Draw circles for each coordinate
        for (Point coordinate : courseCoordinates) {
            Imgproc.circle(frame, coordinate, 5, new Scalar(0, 255, 0), -1);
        }
        //Imgproc.circle(frame, new Point(courseCoordinates[2].x + 10, courseCoordinates[2].y - 10), 5, new Scalar(0, 0, 255), -1);

        Imgproc.circle(frame, goals.get(0), 5, new Scalar(0, 255, 0), -1);
        Imgproc.circle(frame, goals.get(1), 5, new Scalar(0, 255, 0), -1);

        /*for (Point p : redCross.getCoordinates()){
            Imgproc.circle(frame, p, 5, new Scalar(0, 255, 0), -1);
            System.out.println("Crosspoint");
        }*/
        Imgproc.circle(frame, redCross.getCoordinates().get(1), 5, new Scalar(0, 255, 0), -1);
        Imgproc.circle(frame, redCross.getCoordinates().get(2), 5, new Scalar(10, 255, 255), -1);
        Imgproc.circle(frame, redCross.getCoordinates().get(3), 5, new Scalar(10, 255, 255), -1);
        Imgproc.circle(frame, redCross.getCoordinates().get(4), 5, new Scalar(0, 255, 0), -1);

        Imgproc.circle(frame, redCross.circle.getCenter(), (int) Math.round(redCross.circle.getRadius()), new Scalar(0, 255, 0), 2);

        // Display the frame
        HighGui.imshow("Frame", frame);
        HighGui.waitKey();

        frame.release();
    }

    /**
     * This method will retrieve a frame to analyze from the videocapture.
     * @return frame to analyze.
     */
    public void retrieveFrame(){
        // Check if the VideoCapture object is opened successfully
        if (!VideoCaptureSingleton.getInstance().getVideoCapture().isOpened()) {
            System.out.println("Failed to open the webcam.");
            return;
        }
        this.frame = new Mat();
        if (VideoCaptureSingleton.getInstance().getVideoCapture().read(this.frame)) { //reads next frame of videocapture into the frame variable.
             //Save the frame as a PNG file
            //imagePath = getRessourcePath();
            //Imgcodecs.imwrite(imagePath, this.frame);
            //System.out.println("Frame saved as " + imagePath);
        } else {
            System.out.println("Failed to capture a frame.");
        }
    }

    /**
     * This method is mostly for testing purposes, and should just help create a generic url for an image path.
     * @return path.
     */
    private String getRessourcePath(){
        // Get the resource path
        URL resourceUrl = RedRectangleDetection.class.getClassLoader().getResource("resources");

        String resourcePath = null;

        // Check if the resource URL is not null
        if (resourceUrl != null) {
            // Convert the resource URL to a file path
            resourcePath = new File(resourceUrl.getFile()).getAbsolutePath();

        }

        return (resourcePath != null) ? resourcePath : "file not found";
    }

    /**
     * We loop through our list of lines and perform the findIntersection method on each pair.
     * We end up with a point array of all the corners.
     * @param points
     * @param lines the list of linesegments.
     */
    private void findCorners(Point[] points, List<LineSegment> lines) {
        int j = 0;

        for (int i = 0; i < 4 ; i ++) {
            points[i] = findIntersection(lines.get(j),lines.get(++j));
            j++;
        }
    }

    private void FindScaling() {
        LineSegment[] findScale = new LineSegment[4];

        findScale[0] = new LineSegment(courseCoordinates[0], courseCoordinates[1]);
        findScale[1] = new LineSegment(courseCoordinates[2], courseCoordinates[3]);
        findScale[2] = new LineSegment(courseCoordinates[0], courseCoordinates[2]);
        findScale[3] = new LineSegment(courseCoordinates[1], courseCoordinates[3]);

        double[] scale = new double[4];
        scale[0] = findLengthForScale(0,findScale);
        scale[1] = findLengthForScale(1,findScale);
        scale[2] = findLengthForScale(2,findScale);
        scale[3] = findLengthForScale(3,findScale);

        double avg1 =(scale[0]+scale[1])/2;
        double avg2 =(scale[2]+scale[3])/2;

        double scale1 = avg1/(170.3-1.6);
        double scale2 = avg2/(125-1.6);

        scaleFactor = (scale1+scale2)/2;

        System.out.println("ScaleFactor : " + this.scaleFactor);

    }

    private double findLengthForScale(int index, LineSegment[] findScale) {
        return Math.sqrt(Math.pow((findScale[index].getEndPoint().x - findScale[index].getStartPoint().x), 2.0) +
                Math.pow(findScale[index].getEndPoint().y - findScale[index].getStartPoint().y, 2.0));
    }

    private LineSegment findScaleShort(LineSegment lineSegment1, LineSegment lineSegment2, int cornerStart, int cornerEnd) {
        Point start;
        Point end;
        if(lineSegment1.getEndPoint().y < courseCoordinates[cornerStart].y)
            start = lineSegment1.getEndPoint();
        else
            start = lineSegment1.getStartPoint();

        if (lineSegment2.getEndPoint().y > courseCoordinates[cornerEnd].y)
            end = lineSegment2.getEndPoint();
        else
            end = lineSegment2.getStartPoint();

        return new LineSegment(start, end);
    }

    private LineSegment findScaleLong(LineSegment lineSegment, LineSegment lineSegment1, int cornerStart, int cornerEnd) {
        Point start;
        Point end;

        if(lineSegment.getEndPoint().x < courseCoordinates[cornerStart].x)
            start = lineSegment.getEndPoint();
        else
            start = lineSegment.getStartPoint();

        if(lineSegment1.getEndPoint().x > courseCoordinates[cornerEnd].x)
            end = lineSegment1.getEndPoint();
        else
            end = lineSegment1.getStartPoint();

        return new LineSegment(start, end);
    }

    /**
     * Using simple math equations, we find the intersection between two lines.
     *
     * Problems experienced (Fixed), when we would have a vertical line, the slope value would be infinite.
     * To counter this problem, we made a check to see if the x values of the start- and endpoint
     * of a vertical line were the same. If this was the case we would set the boolean value "infiniteSlope"
     * to true in the LineSegments object, and skip the individual calculation of the slope (a)
     * and intersection with y-axis (b) for this linesegment.
     * The point of the vertical line would thereby be calculated with a different function as seen in the if statement.
     *
     * @param horizontal lineSegment.
     * @param vertical lineSegment.
     * @return intersection point of the two lines - equal to the corner.
     */
    public Point findIntersection(LineSegment horizontal, LineSegment vertical) {
        horizontal.determineEquation();
        vertical.determineEquation();

        double horizontalA = horizontal.getA();  // slope of line 1
        double horizontalB = horizontal.getB();  // y-intercept of line 1

        double verticalA = vertical.getA(); // slope of line 2
        double verticalB = vertical.getB();  // y-intercept of line 2

        if (vertical.isInfiniteSlope()){
            // Handle the case of a vertical line
            double y = horizontalA * vertical.getEndPoint().x + horizontalB;  // Calculate the y-coordinate of intersection
            return new Point(vertical.getEndPoint().x,y);
        }
        // Calculate the intersection point
        double x = (verticalB - horizontalB) / (horizontalA - verticalA);
        double y = horizontalA * x + horizontalB;

        return new Point(x,y);
    }

    /**
     * In this method we create a red bitmask for the frame.
     * We then divide the bitmask into 4 regions of equal size,
     * that is we divide right down the middle vertically and horizontally to get 4 areas of interest.
     * These areas will each contain one corner of the field.
     * We then loop through each area of interest to find one vertical and one horizontal line segment.
     * The linesegment will be stored in the lineSegments arraylist, where the first 2 entries
     * will be the top left corner.
     * The next two entries will form the top right corner.
     * The next two entries will form the bottom left corner.
     * The last two entries will form the bottom right corner.
     * @return The list of line segments.
     */
    private List<LineSegment> findLines(){
        //redMask = applyCanny(redMask); //applying the canny edge detection algorithm for more precise detection.
        Mat redMask = findRedMask();

        // Define the number of divisions and the size of each division
        int areaWidth = frameWidth / 2;
        int areaHeight = frameHeight / 2;

        List<LineSegment> lineSegments = new ArrayList<>();

        // Divide the bitmask frame into smaller areas and search for line segments
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                // Define the top-left and bottom-right corners of the area
                int startX = j * areaWidth;
                int startY = i * areaHeight;
                int endX = startX + areaWidth;
                int endY = startY + areaHeight;

                // Extract the area of interest from the bitmask frame
                Mat areaOfInterest = new Mat(redMask, new Rect(new Point(startX, startY), new Point(endX, endY)));

                // Find the horizontal and vertical lines in the area of interest
                lineSegments.add(findLinesegment(areaOfInterest, false, (j > 0), (i > 0), areaWidth, areaHeight));
                lineSegments.add(findLinesegment(areaOfInterest, true, (j > 0), (i > 0), areaWidth, areaHeight));
            }
        }

        return lineSegments;
    }

    /**
     * This method finds the largest LineSegment in the binary image, and return the linesegment object.
     * To find the biggest line segments we use houghLinesP function.
     * @param binaryImage This image is derived from the original bitmask, but is diviided into four
     *                    smaller areas to easier find each corner.
     * @param vertical, if true the method will look for a vertical line segment.
     *                  If false we will be looking for a horizontal line segment
     * @param addToX Since we split our original frame up into smaller areas of interest,
     *               we need to add the pixels back into the final result of the coordinates.
     *               If X is true we will then be adding the width of the areaOfInterest
     *               to the X values of the starting and end point of the line segment.
     * @param addToY Same way idea as addToX but for the y values.
     * @param areaWidth The width of the area.
     * @param areaHeight The height of the area.
     * @return a linesegment.
     */
    private LineSegment findLinesegment(Mat binaryImage, boolean vertical, boolean addToX, boolean addToY, double areaWidth, double areaHeight) {
        Mat lines = new Mat();
        int rho = 1; // Distance resolution of the accumulator in pixels
        double theta = Math.PI / 180; // Angle resolution of the accumulator in radians
        int threshold = 100; // Minimum number of intersections to detect a line
        int minLineLength = 200; // Minimum length of a line in pixels
        int maxLineGap = 12; // Maximum gap between line segments allowed in pixels

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
                if (Math.abs(angle) >= 75 && Math.abs(angle) <= 105 && length > maxLineLength) {
                    maxLineLength = length;
                    startPoint = new Point(x1, y1);
                    endPoint = new Point(x2, y2);
                }
            }else{
                if (Math.abs(angle) >= -25 && Math.abs(angle) <= 25 && length > maxLineLength) {
                    maxLineLength = length;
                    startPoint = new Point(x1, y1);
                    endPoint = new Point(x2, y2);
                }
            }
        }
        if (addToX){ // here we add areawidth to x values, to make them fit with the original frame.
            startPoint.x += areaWidth;
            endPoint.x += areaWidth;
        }

        if (addToY){ // here we add areaHeight to y values, to make them fit with the original frame.
            startPoint.y += areaHeight;
            endPoint.y += areaHeight;
        }

        return new LineSegment(startPoint, endPoint);
    }

    /**
     * This method will give us the red mask, from detection colors within the red threshhold.
     * The binary mask is essentially a binary image, where all the red colors detected
     * are turned into white pixels, and those that are not red will be black.
     * We are also creating a mask to exclude the center region of the frame, which we are not interested in.
     * This mask will color all pixels black, within a radius of 100 pixels.
     * We will hereby avoid getting noise from the red cross in the middle,
     * that could possible interfere with the detection of the red corners.
     * @return the binary image (red mask) that we will use for fuurther processing.
     */
    private Mat findRedMask(){
        // Define the center region to exclude
        int centerX = frame.cols() / 2; // X-coordinate of the center
        int centerY = frame.rows() / 2; // Y-coordinate of the center
        int exclusionRadius = 300; // Radius of the center region to exclude

        // Create a mask to exclude the center region
        Mat mask = new Mat(frame.size(), CvType.CV_8UC1, Scalar.all(255));
        Imgproc.circle(mask, new Point(centerX, centerY), exclusionRadius, new Scalar(0), -1);

        //create hsv frame
        Mat hsvFrame = new Mat();
        //turn original frame into hsv frame for better color detection
        Imgproc.cvtColor(frame,hsvFrame,Imgproc.COLOR_BGR2HSV);

        // Define the lower and upper thresholds for red color
        Scalar lowerRed = new Scalar(0, 100, 100);
        Scalar upperRed = new Scalar(10, 255, 255);

        Mat redMask = new Mat();
        //all red areas will be represented as white dots while non red areas will be black.
        Core.inRange(hsvFrame, lowerRed, upperRed, redMask);

        return redMask;
    }

    /**
     * NOTE ! Well this method should supposedly make the detection more clear.
     * Through testing however we get more precise result using the red mask alone,
     *  without the canny algorithm, hence the method is left unused.. for now..!
     *
     * The canny algorithm should make shape detection in binary image clear and more precise.
     * Hence we chose to apply canny to our binary image, where we end up with an edge image.
     * An edge image will highlight different regions, ie different colors (black or white),
     * since we were to look for a coherent region of white dots (red mask),
     * the region will end up resemble a line much more when looking at the different regions,
     * which is what we are looking for.
     * @param redMask The red mask is the binary mask we have already created.
     */
    private Mat applyCanny(Mat redMask){
        double threshold1 = 50;  // Lower threshold for the intensity gradient
        double threshold2 = 150; // Upper threshold for the intensity gradient
        Mat edges = new Mat();
        Imgproc.Canny(redMask, edges, threshold1, threshold2);
        return edges;
    }

}
