package run;

import IncodeMessage.Incoder;
import IncodeMessage.MessageStrings;
import LineCreation.AlignRobot;
import LineCreation.LineSegment;
import Math1.LineCreation;
import Navigation.BallTracker;
import Navigation.PathAdjustment;
import Observer.HoughCircleDetectorSubject;
import Observer.RobotPositionSubject;
import Observer.FindAreaOfInterestSubject;
import Server.Server;
import Singleton.VideoCaptureSingleton;
import Vectors.VectorCalculations;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.*;

public class RobotAI {

    //Holds the corner in clock direction from top left to bottom left.
    private Point robotPos;
    private Server server;
    private PathAdjustment pather;
    private boolean robotFacingTop;
    private boolean robotFacingLeft;
    private FindAreaOfInterestSubject findAreaOfInterestSubject;
    RobotPositionSubject robotPositionSubject;
    Math1.LineCreation lineCreation;
    HoughCircleDetectorSubject houghCircleDetectorSubject;
    List<Point> ballPositions;


    public RobotAI(Server server){
        try {
            findAreaOfInterestSubject = new FindAreaOfInterestSubject();
        } catch (NullPointerException e){
            System.out.println("No cross or various other elements, get fucked Timmmmmmmmmmmmmmmmmmmmmmm");
        }
        pather = new PathAdjustment(findAreaOfInterestSubject.fieldDetection);
        this.server = server;
    }


    public void run() {
        VideoCaptureSingleton videoCaptureSingleton = VideoCaptureSingleton.getInstance();
        videoCaptureSingleton.setPoint(findAreaOfInterestSubject);

        lineCreation = new Math1.LineCreation();
        robotPositionSubject = new RobotPositionSubject();

        houghCircleDetectorSubject = new HoughCircleDetectorSubject();
        List<Ball> balls = getBalls();

        List<Point> blueCircle = robotPositionSubject.getPos(false);
        List<Point> redCircle = robotPositionSubject.getPos(true);
        if(blueCircle.size()>0 && redCircle.size()>0){
            robotPos = getMidPoint(blueCircle.get(0),redCircle.get(0));



            int ballsCollected = 0;

            // Repeat until all balls are picked
            while (balls.stream().anyMatch(ball -> !ball.picked)) {
                Ball nearestBall = null;

                if(ballsCollected==5 || ballsCollected == balls.size()){
                    goToObject(null);
                    //pukeBallsOrWhateverWeCallThisMethod();
                    balls = getBalls();
                }else{
                    nearestBall = findNearestBall(robotPos, balls);
                    goToObject(nearestBall.location);
                }

                if (nearestBall != null && ballsCollected < 5) {
                    nearestBall.picked = true;
                    ballsCollected++;
                    blueCircle = robotPositionSubject.getPos(false);
                    redCircle = robotPositionSubject.getPos(true);
                    if(blueCircle.size()>0 && redCircle.size()>0) {
                        robotPos = getMidPoint(blueCircle.get(0), redCircle.get(0));
                    }else{
                        robotPos = nearestBall.location;
                    }
                }else if(ballsCollected == 5){
                    ballsCollected = 0;
                }
                ballsCollected++;
            }
        }
    }

    private List<Ball> getBalls() {
        ballPositions = houghCircleDetectorSubject.getBalls();
        List<Ball> balls = new ArrayList<>();

        for (Point ball: ballPositions){                                //Looping through all balls
            if (pather.isEasy(ball)){                                   //Checking if the ball is considered easy
                balls.add(new Ball(ball));
            }
        }
        return balls;
    }

    private void goToObject(Point nearestBall) {
        if (nearestBall != null) {
            if (pather.pathIntersects(nearestBall, robotPos) ||
                    pather.isAroundCross(nearestBall) || pather.isNearSide(nearestBall)) {
                List<Point> path = pather.adjustPath(nearestBall, robotPos);
                for (Point waypoint : path) {
                    testRun(waypoint, lineCreation, robotPos, robotPositionSubject);
                    robotPos = robotPositionSubject.getPos(true).get(0);
                }
            } else
                testRun(nearestBall, lineCreation, robotPos, robotPositionSubject);
        } else {
            List<Point> path = pather.pathToGoal(robotPos);
            for (Point waypoint : path) {
                testRun(waypoint, lineCreation, robotPos, robotPositionSubject);
                robotPos = robotPositionSubject.getPos(true).get(0);
            }
        }
    }

    /**
     * @param firstCoordinate the coordinate for the front facing circle
     * @param secondCoordinate the coordinate for the back facing circle
     * @return the true or false if the robot is facing towards the direction in question
     */
    private boolean facingDirection(double firstCoordinate, double secondCoordinate){
        return firstCoordinate-secondCoordinate < 0;
    }

    private void testRun(Point wayPoint, LineCreation lineCreation, Point robotPosition, RobotPositionSubject robotPositionSubject){
        VectorCalculations vectorCalculations;
        if(wayPoint==null){
            return;
        }

        //Turn the robot to the desired angle
        turn(robotPositionSubject, wayPoint);

        double[] lineToWaypoint = lineCreation.getSlopeAndBegin(robotPosition, wayPoint);
        if(lineToWaypoint==null){
            return;
        }

        //Navigate to First wayPoint
        server.writeMessage(MessageStrings.WayPoints.toString()+":"+lineToWaypoint[0]+","+lineToWaypoint[1]+";"+wayPoint.x+","+wayPoint.y);
        server.receiveMessage();
        robotPosition = startFollowingLine(robotPositionSubject,robotPosition, wayPoint);

    }

    private void turn(RobotPositionSubject robotPositionSubject, Point targetWayPoint) {
        VectorCalculations vectorCalculations;
        for(int i = 0; i<2; i++){
            List<Point> redCirc = robotPositionSubject.getPos(true);
            List<Point> blueCirc = robotPositionSubject.getPos(false);
            if(redCirc.size()>0 && blueCirc.size()>0){
                vectorCalculations = new VectorCalculations(new LineSegment(blueCirc.get(0),redCirc.get(0)), targetWayPoint);
            }else {
                return;
            }
            //Turn to face firstWayPoint
            server.writeMessage(MessageStrings.Turn+":"+vectorCalculations.getAngle());
            //TODO maybe do something
            String res = server.receiveMessage();
        }
    }

    private Point startFollowingLine(RobotPositionSubject robotPositionSubject, Point robocupPos, Point targetWayPoint) {
        String res = server.receiveMessage();
        Point lastPosition = null;

        //Test Code
        VideoCapture videoCapture = VideoCaptureSingleton.getInstance().getVideoCapture();
        Mat frame = new Mat();
        boolean small = true;
        while(true){
            List<Point> smallCircles = robotPositionSubject.getPos(true);
            List<Point> bigCircles = robotPositionSubject.getPos(false);
            if(bigCircles.size()>0 && smallCircles.size()>0){
                Point p1 = bigCircles.get(0);
                Point p2 = smallCircles.get(0);
                robotFacingTop = facingDirection(p2.y, p1.y);
                robotFacingLeft = facingDirection(p2.x, p1.x);
                lastPosition = getMidPoint(p1,p2);

                server.writeMessage(p1.x+","+p1.y+";"+p2.x+","+p2.y+";"+robotFacingLeft);

            }
            res = server.receiveMessage();
            if(res.contains("finnish")){
                break;
            }
            videoCapture.read(frame);
            HighGui.imshow("abe",frame);
            if(robotFacingLeft){
                Imgproc.line(frame,robocupPos,targetWayPoint,new Scalar(255,0,0),6);
            }else{
                Imgproc.line(frame,robocupPos,targetWayPoint,new Scalar(255,0,0),6);
            }

            //test out puts
            Imgproc.circle(frame, findAreaOfInterestSubject.getCross().getCenter(), 5, new Scalar(255,0,0), 3);
            Imgproc.line(frame,findAreaOfInterestSubject.fieldDetection.getFloorCorners()[0],findAreaOfInterestSubject.fieldDetection.getFloorCorners()[1],new Scalar(255,0,0),6);
            Imgproc.line(frame,findAreaOfInterestSubject.fieldDetection.getFloorCorners()[0],findAreaOfInterestSubject.fieldDetection.getFloorCorners()[3],new Scalar(255,0,0),6);
            Imgproc.line(frame,findAreaOfInterestSubject.fieldDetection.getFloorCorners()[1],findAreaOfInterestSubject.fieldDetection.getFloorCorners()[2],new Scalar(255,0,0),6);
            Imgproc.line(frame,findAreaOfInterestSubject.fieldDetection.getFloorCorners()[3],findAreaOfInterestSubject.fieldDetection.getFloorCorners()[2],new Scalar(255,0,0),6);

            int key = HighGui.waitKey(10);
            if(key ==27){
                break;
            }
        }
        HighGui.destroyAllWindows();
        return lastPosition;
    }

    private Point getMidPoint(Point p1, Point p2) {
        return new Point((p1.x+p2.x)/2,(p1.y+p2.y)/2);
    }

    private Ball findNearestBall(Point vehiclePosition, List<Ball> balls) {
        Ball nearestBall = null;
        double minDistance = Double.MAX_VALUE;

        for (Ball ball : balls) {
            if (ball.picked) continue;

            double distance = euclideanDistance(vehiclePosition, ball.location);
            if (distance < minDistance) {
                minDistance = distance;
                nearestBall = ball;
            }
        }

        return nearestBall;
    }

    private double euclideanDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public class Ball {
        Point location;
        boolean picked = false;

        Ball(Point ball) {
            location = ball;
        }
    }
}


